package com.simplemobiletools.dialer.activities

import android.content.Context
import android.content.Intent
import android.graphics.drawable.RippleDrawable
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.telecom.Call
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.simplemobiletools.dialer.R
import com.simplemobiletools.dialer.helpers.*
import com.simplemobiletools.dialer.models.CallContact
import kotlinx.android.synthetic.main.activity_call.*

class CallActivity : AppCompatActivity() {
    companion object {
        fun getStartIntent(context: Context): Intent {
            val openAppIntent = Intent(context, CallActivity::class.java)
            openAppIntent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            return openAppIntent
        }
    }

    private var isSpeakerOn = false
    private var isMicrophoneOff = false
    private var isCallEnded = false
    private var callContact: CallContact? = null
    private var proximityWakeLock: PowerManager.WakeLock? = null
    private var screenOnWakeLock: PowerManager.WakeLock? = null
    private var callDuration = 0
    private val callContactAvatarHelper by lazy { CallContactAvatarHelper(this) }
    private val callDurationHandler = Handler(Looper.getMainLooper())
    private var dragDownX = 0f
    private var stopAnimation = false
    private var viewsUnderDialpad = arrayListOf<Pair<View, Float>>()
    private var dialpadHeight = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        if (CallManager.getPhoneState() == NoCall) {
            finish()
            return
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (screenOnWakeLock?.isHeld == true) {
            screenOnWakeLock!!.release()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun initButtons() {
//            call_decline.setOnClickListener {
//                endCall()
//            }
//
//            call_accept.setOnClickListener {
//                acceptCall()
//            }
//        call_toggle_microphone.setOnClickListener {
//            toggleMicrophone()
//        }
//
//        call_toggle_speaker.setOnClickListener {
//            changeCallAudioRoute()
//        }
//
//        call_dialpad.setOnClickListener {
//            toggleDialpadVisibility()
//        }
//
//        dialpad_close.setOnClickListener {
//            hideDialpad()
//        }
//
//        call_toggle_hold.setOnClickListener {
//            toggleHold()
//        }
//
//        call_add.setOnClickListener {
//            Intent(applicationContext, DialpadActivity::class.java).apply {
//                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
//                startActivity(this)
//            }
//        }
//
//        call_swap.setOnClickListener {
//            CallManager.swap()
//        }
//
//        call_merge.setOnClickListener {
//            CallManager.merge()
//        }
//
//        call_manage.setOnClickListener {
//            startActivity(Intent(this, ConferenceActivity::class.java))
//        }
//
//        call_end.setOnClickListener {
//            endCall()
//        }
//
//        dialpad_0_holder.setOnClickListener { dialpadPressed('0') }
//        dialpad_1_holder.setOnClickListener { dialpadPressed('1') }
//        dialpad_2_holder.setOnClickListener { dialpadPressed('2') }
//        dialpad_3_holder.setOnClickListener { dialpadPressed('3') }
//        dialpad_4_holder.setOnClickListener { dialpadPressed('4') }
//        dialpad_5_holder.setOnClickListener { dialpadPressed('5') }
//        dialpad_6_holder.setOnClickListener { dialpadPressed('6') }
//        dialpad_7_holder.setOnClickListener { dialpadPressed('7') }
//        dialpad_8_holder.setOnClickListener { dialpadPressed('8') }
//        dialpad_9_holder.setOnClickListener { dialpadPressed('9') }
//
//        arrayOf(
//            dialpad_0_holder,
//            dialpad_1_holder,
//            dialpad_2_holder,
//            dialpad_3_holder,
//            dialpad_4_holder,
//            dialpad_5_holder,
//            dialpad_6_holder,
//            dialpad_7_holder,
//            dialpad_8_holder,
//            dialpad_9_holder,
//            dialpad_plus_holder,
//            dialpad_asterisk_holder,
//            dialpad_hashtag_holder
//        ).forEach {
//            it.background = ResourcesCompat.getDrawable(resources, R.drawable.pill_background, theme)
//            it.background?.alpha = LOWER_ALPHA_INT
//        }
//
//        dialpad_0_holder.setOnLongClickListener { dialpadPressed('+'); true }
//        dialpad_asterisk_holder.setOnClickListener { dialpadPressed('*') }
//        dialpad_hashtag_holder.setOnClickListener { dialpadPressed('#') }
//
//        dialpad_wrapper.setBackgroundColor(getProperBackgroundColor())
//        arrayOf(dialpad_close, call_sim_image).forEach {
//            it.applyColorFilter(getProperTextColor())
//        }
//
//        val bgColor = getProperBackgroundColor()
//        val inactiveColor = getInactiveButtonColor()
//        arrayOf(
//            call_toggle_microphone, call_toggle_speaker, call_dialpad,
//            call_toggle_hold, call_add, call_swap, call_merge, call_manage
//        ).forEach {
//            it.applyColorFilter(bgColor.getContrastColor())
//            it.background.applyColorFilter(inactiveColor)
//        }
//
//        arrayOf(
//            call_toggle_microphone, call_toggle_speaker, call_dialpad,
//            call_toggle_hold, call_add, call_swap, call_merge, call_manage
//        ).forEach { imageView ->
//            imageView.setOnLongClickListener {
//                if (!imageView.contentDescription.isNullOrEmpty()) {
//                    toast(imageView.contentDescription.toString())
//                }
//                true
//            }
//        }
//
//        call_sim_id.setTextColor(getProperTextColor().getContrastColor())
//        dialpad_input.disableKeyboard()
//
//        dialpad_wrapper.onGlobalLayout {
//            dialpadHeight = dialpad_wrapper.height.toFloat()
//        }
    }
}
