package com.simplemobiletools.dialer.services

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import com.simplemobiletools.dialer.R

class tts_service(context: Context) {
    private var context: Context? = null
    public var mediaPlayer: MediaPlayer? = null
    public var mediaPlayer2: MediaPlayer? = null

    init {
        this.context = context
    }

    fun speak_it(text: String) {
    }

    fun play_audio() {
        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.yingshaoxo_email);
            mediaPlayer?.setVolume(1.0f, 1.0f);
            //mediaPlayer.prepare();
            mediaPlayer?.start();
        } catch (e: Error) {
            print(e)
        }

        Handler().postDelayed({
            mediaPlayer2 = MediaPlayer.create(context, R.raw.yingshaoxo_email);
            mediaPlayer2?.setVolume(1.0f, 1.0f);
            //mediaPlayer.prepare();
            mediaPlayer2?.start();
        }, 200)
    }

    fun stop_playing() {
        try {
            mediaPlayer?.stop();
            mediaPlayer2?.stop();
        } catch (e: Error) {
            print(e)
        }
    }
}
