package com.s9dp.servicesshowtimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimerBroadcastReceiver(private val listener: TimerListener) : BroadcastReceiver() {

    interface TimerListener {
        fun onTimerUpdate(timerValue: Int)
    }

    override fun onReceive(context: Context, intent: Intent) {
        val timerValue = intent.getIntExtra("timer_value", 0)
        listener.onTimerUpdate(timerValue)
    }
}
