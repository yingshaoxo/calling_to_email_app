package com.simplemobiletools.dialer.activities

import android.content.Context
import android.content.Intent
import android.os.*
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.simplemobiletools.dialer.Global_Variable
import com.simplemobiletools.dialer.R
import com.simplemobiletools.dialer.helpers.*
import kotlinx.android.synthetic.main.activity_call.*

class CallActivity : AppCompatActivity() {
    companion object {
        fun getStartIntent(context: Context): Intent {
            val openAppIntent = Intent(context, CallActivity::class.java)
            openAppIntent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            return openAppIntent
        }
    }

    private var screenOnWakeLock: PowerManager.WakeLock? = null
    private var end_call_button: Button? = null

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        end_call_button = findViewById<Button>(R.id.end_call_button)
        end_call_button?.setOnClickListener {
            try {
                Global_Variable.current_call?.disconnect()
                Global_Variable.my_tts_service?.stop_network_audio();
            } catch (e: Throwable) {
                print(e)
            }

            finish()
        }

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
}
