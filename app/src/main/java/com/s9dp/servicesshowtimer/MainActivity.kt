package com.s9dp.servicesshowtimer

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

class MainActivity : AppCompatActivity(), TimerBroadcastReceiver.TimerListener {

    private lateinit var startButton: Button
    private lateinit var timerTextView: TextView
    private var seconds = 0
    private var isRunning = false
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var timerBroadcastReceiver: TimerBroadcastReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
        initViews()
    }

    private fun initViews() {
        startButton = findViewById(R.id.btnStartTimer)
        timerTextView = findViewById(R.id.txtShowTimer)
        startButton.setOnClickListener {
            startTimer()
        }
        // Initialize the BroadcastReceiver with the listener (Activity in this case)
        timerBroadcastReceiver = TimerBroadcastReceiver(this)
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            @SuppressLint("DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                seconds++
                val hours = seconds / 3600
                val minutes = (seconds % 3600) / 60
                val seconds = seconds % 60
                timerTextView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            }

            override fun onFinish() {
                // not used in this example
            }
        }
        countDownTimer.start()
    }

    private fun startForegroundService() {
        val intent = Intent(this, TimerServices::class.java)
        intent.putExtra("timerValue", seconds)  // Add data to the intent
        startService(intent)


    }

    override fun onPause() {
        super.onPause()
        countDownTimer.cancel()
        startForegroundService()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
        unregisterReceiver(timerBroadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        if (isRunning) {
            startTimer()
        }
        val filter = IntentFilter(packageName)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(timerBroadcastReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(timerBroadcastReceiver, filter)
        }

        if (seconds != 0) {
            startTimer()
        }

    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(
                    this, permission.POST_NOTIFICATIONS
                ) != PermissionChecker.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean? -> }

    override fun onTimerUpdate(timerValue: Int) {
        if (timerValue != 0) {
            seconds = timerValue
        }
    }


}