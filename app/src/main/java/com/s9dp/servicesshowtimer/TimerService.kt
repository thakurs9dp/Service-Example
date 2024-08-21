package com.s9dp.servicesshowtimer

import android.R
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import androidx.core.app.NotificationCompat


class TimerService : Service() {
    private var startTime: Long = 0
    private var isRunning = false
    private var notificationManager: NotificationManager? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startTime = intent.getLongExtra("START_TIME", 0)
        isRunning = true
        startForeground(1, createNotification())
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    private fun createNotification(): Notification {
        val builder = NotificationCompat.Builder(this)
        builder.setSmallIcon(R.drawable.ic_notification_overlay)
        builder.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_secure))
        builder.setContentTitle("Timer Running")
        builder.setContentText("00:00:00")
        builder.setOngoing(true)

        Thread {
            while (isRunning) {
                val time = System.currentTimeMillis() - startTime
                val intent = Intent("TIMER_UPDATE")
                intent.putExtra("TIME", time)
                sendBroadcast(intent)
                builder.setContentText(formatTime(time))
                notificationManager!!.notify(1, builder.build())
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
        return builder.build()
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(time: Long): String {
        val hours = (time / 3600000).toInt()
        val minutes = ((time % 3600000) / 60000).toInt()
        val seconds = ((time % 60000) / 1000).toInt()
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}