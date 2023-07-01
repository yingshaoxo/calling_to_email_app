package com.simplemobiletools.dialer.helpers

import android.annotation.SuppressLint
import android.os.Build
import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import android.telecom.VideoProfile
import androidx.annotation.RequiresApi
import java.util.concurrent.CopyOnWriteArraySet

class CallManager {
    companion object {
        @SuppressLint("StaticFieldLeak")
        var inCallService: InCallService? = null
        private var call: Call? = null
        private val calls = mutableListOf<Call>()
        private val listeners = CopyOnWriteArraySet<CallManagerListener>()

        fun onCallAdded(call: Call) {
            this.call = call
            calls.add(call)
            for (listener in listeners) {
                listener.onPrimaryCallChanged(call)
            }
            call.registerCallback(object : Call.Callback() {
                @RequiresApi(Build.VERSION_CODES.S)
                override fun onStateChanged(call: Call, state: Int) {
                    updateState()
                }

                @RequiresApi(Build.VERSION_CODES.S)
                override fun onDetailsChanged(call: Call, details: Call.Details) {
                    updateState()
                }

                @RequiresApi(Build.VERSION_CODES.S)
                override fun onConferenceableCallsChanged(call: Call, conferenceableCalls: MutableList<Call>) {
                    updateState()
                }
            })
        }

        @RequiresApi(Build.VERSION_CODES.S)
        fun onCallRemoved(call: Call) {
            calls.remove(call)
            updateState()
        }

        fun onAudioStateChanged(audioState: CallAudioState) {
//            val route = AudioRoute.fromRoute(audioState.route) ?: return
//            for (listener in listeners) {
//                listener.onAudioStateChanged(route)
//            }
        }

        @RequiresApi(Build.VERSION_CODES.S)
        fun getPhoneState(): PhoneState {
            return when (calls.size) {
                0 -> NoCall
                1 -> SingleCall(calls.first())
                2 -> {
                    val active = calls.find { it.details.state == Call.STATE_ACTIVE }
                    val newCall = calls.find { it.details.state == Call.STATE_CONNECTING || it.details.state == Call.STATE_DIALING }
                    val onHold = calls.find { it.details.state == Call.STATE_HOLDING }
                    if (active != null && newCall != null) {
                        TwoCalls(newCall, active)
                    } else if (newCall != null && onHold != null) {
                        TwoCalls(newCall, onHold)
                    } else if (active != null && onHold != null) {
                        TwoCalls(active, onHold)
                    } else {
                        TwoCalls(calls[0], calls[1])
                    }
                }
                else -> {
                    NoCall
                }
            }
        }

        private fun getCallAudioState() = inCallService?.callAudioState

        fun setAudioRoute(newRoute: Int) {
            inCallService?.setAudioRoute(newRoute)
        }

        @RequiresApi(Build.VERSION_CODES.S)
        private fun updateState() {
            val primaryCall = when (val phoneState = getPhoneState()) {
                is NoCall -> null
                is SingleCall -> phoneState.call
                is TwoCalls -> phoneState.active
            }
            var notify = true
            if (primaryCall == null) {
                call = null
            } else if (primaryCall != call) {
                call = primaryCall
                for (listener in listeners) {
                    listener.onPrimaryCallChanged(primaryCall)
                }
                notify = false
            }
            if (notify) {
                for (listener in listeners) {
                    listener.onStateChanged()
                }
            }

            // remove all disconnected calls manually in case they are still here
            calls.removeAll { it.details.state == Call.STATE_DISCONNECTED }
        }

        fun getPrimaryCall(): Call? {
            return call
        }

        fun accept() {
            call?.answer(VideoProfile.STATE_AUDIO_ONLY)
        }

        @RequiresApi(Build.VERSION_CODES.S)
        fun reject() {
            if (call != null) {
                val state = getState()
                if (state == Call.STATE_RINGING) {
                    call!!.reject(false, null)
                } else if (state != Call.STATE_DISCONNECTED && state != Call.STATE_DISCONNECTING) {
                    call!!.disconnect()
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.S)
        fun toggleHold(): Boolean {
            val isOnHold = getState() == Call.STATE_HOLDING
            if (isOnHold) {
                call?.unhold()
            } else {
                call?.hold()
            }
            return !isOnHold
        }

        @RequiresApi(Build.VERSION_CODES.S)
        fun swap() {
            if (calls.size > 1) {
                calls.find { it.details.state == Call.STATE_HOLDING }?.unhold()
            }
        }
        fun addListener(listener: CallManagerListener) {
            listeners.add(listener)
        }

        fun removeListener(listener: CallManagerListener) {
            listeners.remove(listener)
        }

        @RequiresApi(Build.VERSION_CODES.S)
        fun getState() = getPrimaryCall()?.details?.state
    }
}

interface CallManagerListener {
    fun onStateChanged()
    fun onAudioStateChanged(audioState: Any)
    fun onPrimaryCallChanged(call: Call)
}

sealed class PhoneState
object NoCall : PhoneState()
class SingleCall(val call: Call) : PhoneState()
class TwoCalls(val active: Call, val onHold: Call) : PhoneState()
