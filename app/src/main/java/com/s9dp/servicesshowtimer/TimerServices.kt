package com.s9dp.servicesshowtimer

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat

class TimerServices : Service() {

    private lateinit var countDownTimer: CountDownTimer
    private var seconds = 0
    private lateinit var notificationManager: NotificationManager

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        var getNotification: Notification?

        val getSecondFromMainActivity = intent?.getIntExtra("timerValue", 0)

        if (getSecondFromMainActivity != null) {
            if (getSecondFromMainActivity != 0) {
                seconds = getSecondFromMainActivity
            }
        }

        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                seconds++
                val hours = seconds / 3600
                val minutes = (seconds % 3600) / 60
                val seconds = seconds % 60
                getNotification = createNotification(hours, minutes, seconds)

                notificationManager.notify(1, getNotification)
                sendTimerValue(seconds)
            }

            override fun onFinish() {
                // not used in this example
            }

        }

        /* if (getNotification != null) {
             notificationManager.notify(1, getNotification)
         }*/

        countDownTimer.start()

        // Start foreground service
        val notification = createNotification(0, 0, 0)
        startForeground(1, notification)

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "timer_channel", "Timer Channel", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun createNotification(hours: Int, minutes: Int, seconds: Int): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val notification =
            NotificationCompat.Builder(this, "timer_channel").setContentTitle("Timer")
                .setContentText(String.format("%02d:%02d:%02d", hours, minutes, seconds))
                .setSmallIcon(R.drawable.ic_launcher_foreground).setContentIntent(pendingIntent)
                .build()
        return notification
    }

    private fun sendTimerValue(seconds: Int) {
        val intent = Intent(packageName)
        intent.putExtra("timer_value", seconds)
        sendBroadcast(intent)
    }


}