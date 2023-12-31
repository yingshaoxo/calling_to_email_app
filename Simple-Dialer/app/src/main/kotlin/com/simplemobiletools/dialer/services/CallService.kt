package com.simplemobiletools.dialer.services

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.PowerManager
import android.telecom.Call
import android.telecom.Call.Details.DIRECTION_INCOMING
import android.telecom.CallAudioState
import android.telecom.InCallService
import androidx.annotation.RequiresApi
import com.simplemobiletools.dialer.Global_Variable
import com.simplemobiletools.dialer.activities.CallActivity
import com.simplemobiletools.dialer.helpers.CallManager
import com.simplemobiletools.dialer.helpers.NoCall


class CallService : InCallService() {
    private val callListener = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            super.onStateChanged(call, state)
        }
    }

    fun is_screen_on(): Boolean {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) powerManager.isInteractive else powerManager.isScreenOn
    }

    fun action_after_call_acception() {
        Global_Variable.my_tts_service = tts_service(this.applicationContext)
        Global_Variable.my_tts_service?.play()
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)

        Global_Variable.current_call = call

        CallManager.onCallAdded(call)
        CallManager.inCallService = this
        call.registerCallback(callListener)

        var incoming_call = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (call.details.callDirection == DIRECTION_INCOMING) {
                incoming_call = true
            } else {
                incoming_call = false
            }
        }

        if (incoming_call == true) {
            Handler().postDelayed({
                var i = 0;
                while (!is_screen_on()) {
                    Thread.sleep(10)
                    i += 1
                    if (i > 20) {
                        break
                    }
                }
                this.startActivity(CallActivity.getStartIntent(this.applicationContext))
                CallManager.accept()
                Thread.sleep(1000)
                action_after_call_acception()
            }, 0)
        }

        val isScreenLocked = (getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).isDeviceLocked
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)

        Global_Variable.current_call = null

        call.unregisterCallback(callListener)
        val wasPrimaryCall = call == CallManager.getPrimaryCall()
        CallManager.onCallRemoved(call)
        if (CallManager.getPhoneState() == NoCall) {
            CallManager.inCallService = null
//            callNotificationManager.cancelNotification()
        } else {
//            callNotificationManager.setupNotification()
            if (wasPrimaryCall) {
                startActivity(CallActivity.getStartIntent(this))
            }
        }

        Global_Variable.my_tts_service?.stop()
    }

    override fun onCallAudioStateChanged(audioState: CallAudioState?) {
        super.onCallAudioStateChanged(audioState)
        if (audioState != null) {
            CallManager.onAudioStateChanged(audioState)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        Global_Variable.my_tts_service?.stop()
    }
}
