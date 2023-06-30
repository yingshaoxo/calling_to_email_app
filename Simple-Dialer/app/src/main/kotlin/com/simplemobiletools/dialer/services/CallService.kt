package com.simplemobiletools.dialer.services

import android.app.KeyguardManager
import android.content.Context
import android.media.MediaRouter.RouteGroup
import android.os.Build
import android.os.Handler
import android.os.PowerManager
import android.telecom.Call
import android.telecom.Call.Details.DIRECTION_INCOMING
import android.telecom.CallAudioState
import android.telecom.InCallService
import com.simplemobiletools.dialer.activities.CallActivity
import com.simplemobiletools.dialer.extensions.config
import com.simplemobiletools.dialer.extensions.isOutgoing
import com.simplemobiletools.dialer.extensions.powerManager
import com.simplemobiletools.dialer.helpers.CallManager
import com.simplemobiletools.dialer.helpers.CallNotificationManager
import com.simplemobiletools.dialer.helpers.NoCall


class CallService : InCallService() {
    private val callNotificationManager by lazy { CallNotificationManager(this) }

    private val callListener = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            super.onStateChanged(call, state)
            if (state == Call.STATE_DISCONNECTED || state == Call.STATE_DISCONNECTING) {
                callNotificationManager.cancelNotification()
            } else {
                callNotificationManager.setupNotification()
            }
        }
    }

    fun is_screen_on(): Boolean {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) powerManager.isInteractive else powerManager.isScreenOn
    }

    var my_tts_service: tts_service? = null
    fun action_after_call_acception() {
        my_tts_service = tts_service(this.applicationContext)
        my_tts_service?.play_audio()
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
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
        if (!powerManager.isInteractive || call.isOutgoing() || isScreenLocked || config.alwaysShowFullscreen) {
            try {
                callNotificationManager.setupNotification(true)
                startActivity(CallActivity.getStartIntent(this))
            } catch (e: Exception) {
                // seems like startActivity can throw AndroidRuntimeException and ActivityNotFoundException, not yet sure when and why, lets show a notification
                callNotificationManager.setupNotification()
            }
        } else {
            callNotificationManager.setupNotification()
        }
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        call.unregisterCallback(callListener)
        val wasPrimaryCall = call == CallManager.getPrimaryCall()
        CallManager.onCallRemoved(call)
        if (CallManager.getPhoneState() == NoCall) {
            CallManager.inCallService = null
            callNotificationManager.cancelNotification()
        } else {
            callNotificationManager.setupNotification()
            if (wasPrimaryCall) {
                startActivity(CallActivity.getStartIntent(this))
            }
        }

        my_tts_service?.stop_playing();
    }

    override fun onCallAudioStateChanged(audioState: CallAudioState?) {
        super.onCallAudioStateChanged(audioState)
        if (audioState != null) {
            CallManager.onAudioStateChanged(audioState)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        callNotificationManager.cancelNotification()

        my_tts_service?.stop_playing();
    }
}
