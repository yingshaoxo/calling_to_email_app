package com.simplemobiletools.dialer.activities

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.simplemobiletools.dialer.Global_Variable
import com.simplemobiletools.dialer.R
import com.simplemobiletools.dialer.services.tts_service


class MainActivity : AppCompatActivity() {
    var my_tts_service: tts_service? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<FloatingActionButton>(R.id.play_button).setOnClickListener {
            Global_Variable.my_tts_service = tts_service(this.applicationContext)
            Global_Variable.my_tts_service?.play()
        }

        findViewById<FloatingActionButton>(R.id.stop_button).setOnClickListener {
            Global_Variable.my_tts_service?.stop()
        }

        if (!is_this_a_default_dialer()) {
            request_to_be_default_dialer()
        } else {
//            checkContactPermissions()
//
//            if (!config.wasOverlaySnackbarConfirmed && !Settings.canDrawOverlays(this)) {
//                val snackbar = Snackbar.make(main_holder, R.string.allow_displaying_over_other_apps, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok) {
//                    config.wasOverlaySnackbarConfirmed = true
//                    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
//                }
//            }
//
//            handleNotificationPermission { granted ->
//                if (!granted) {
//                    PermissionRequiredDialog(this, R.string.allow_notifications_incoming_calls)
//                }
//            }
//        } else {
//            launchSetDefaultDialerIntent()
//        }
        }
    }

    fun is_this_a_default_dialer(): Boolean {
        val telecom_manager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager;
        if (telecom_manager.defaultDialerPackage == packageName) {
            return true
        } else {
            return false
        }
    }

    private fun request_to_be_default_dialer() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
            intent.putExtra(
                TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                this.packageName
            )
            startActivity(intent)
        } else {
            val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
            val setDefaultDialerLauncher = registerForActivityResult<Intent, ActivityResult>(
                ActivityResultContracts.StartActivityForResult()
            ) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    // Check if your app is now the default dialer
                    if (roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                        // Your app is now the default dialer
                        Toast.makeText(this, "This app is now set as default dialer", Toast.LENGTH_SHORT).show()
                        //navigate to dashboard like screen here
                    } else {
                        // Your app is not the default dialer
                        Toast.makeText(this, "Please set this app as default calling app to proceed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Failed to set your app as the default dialer
                    Log.d("Dialer", "something went wrong while setting as default, most likely user cancelled the popup")
                    Toast.makeText(this, "Please set this app as default calling app to proceed", Toast.LENGTH_SHORT).show()
                }
            }
            val intent: Intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
            setDefaultDialerLauncher.launch(intent)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed() {
        try {
            Global_Variable.my_tts_service?.stop_network_audio()
        } catch (e: Throwable) {
        }

        try {
            Global_Variable.my_tts_service?.stop_playing()
        } catch (e: Throwable) {
        }

        try {
            Global_Variable.current_call?.disconnect()
        } catch (e: Throwable) {
        }

        finish()
    }
}
