package com.simplemobiletools.dialer

import android.telecom.Call
import com.simplemobiletools.dialer.services.tts_service

object Global_Variable {
    var current_call: Call? = null

    var my_tts_service: tts_service? = null

}
