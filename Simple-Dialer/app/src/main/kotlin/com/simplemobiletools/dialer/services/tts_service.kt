package com.simplemobiletools.dialer.services

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import com.simplemobiletools.dialer.R

class tts_service(context: Context) {
    private var context: Context? = null
    public var mediaPlayer: MediaPlayer? = null
    public var mediaPlayer2: MediaPlayer? = null

    init {
        this.context = context
    }

    fun play_network_audio(): Boolean {
        var result = GoFind.GoFind.post_to_the_network("192.168.49.1/24", 1919, 1919, "/play_post", "{}", 3000)
        if (result.contains("ok")) {
            return true
        } else {
            return false
        }
    }

    fun play_audio() {
        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.send_email);
            mediaPlayer?.setVolume(1.0f, 1.0f);
            //mediaPlayer.prepare();
            mediaPlayer?.start();
        } catch (e: Error) {
            print(e)
        }

        Handler().postDelayed({
            mediaPlayer2 = MediaPlayer.create(context, R.raw.send_email);
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
