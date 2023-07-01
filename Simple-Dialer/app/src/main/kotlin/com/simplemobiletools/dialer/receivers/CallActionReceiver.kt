package com.simplemobiletools.dialer.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.simplemobiletools.dialer.activities.CallActivity
import com.simplemobiletools.dialer.helpers.CallManager

class CallActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.startActivity(CallActivity.getStartIntent(context))
        CallManager.accept()
    }
}
