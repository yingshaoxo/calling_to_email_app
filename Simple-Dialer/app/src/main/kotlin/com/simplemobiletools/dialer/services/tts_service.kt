package com.simplemobiletools.dialer.services

import android.content.Context
import android.media.MediaPlayer
import com.simplemobiletools.dialer.R

class tts_service(context: Context) {
    private var context: Context? = null
    public var mediaPlayer: MediaPlayer? = null

    init {
        this.context = context
    }

    fun speak_it(text: String) {
    }

    fun play_audio() {
        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.yingshaoxo_gmail);
            mediaPlayer?.setVolume(0.8f, 0.8f);
            //mediaPlayer.prepare();
            mediaPlayer?.start();
        } catch (e: Error) {
            print(e)
        }
    }

    fun stop_playing() {
        try {
            mediaPlayer?.stop();
        } catch (e: Error) {
            print(e)
        }
    }
}
