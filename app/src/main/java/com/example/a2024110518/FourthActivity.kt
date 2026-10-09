package com.example.a2024110518

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

/**
 * 第四个二级 Activity：秒表。
 *
 * 横竖屏旋转会导致 Activity 重建，这里采用 onSaveInstanceState 保存计时状态，
 * 并以 SystemClock.elapsedRealtime()（系统单调时钟，跨重建依然有效）为基准，
 * 从而保证旋转后计时连续、不归零、不跳变。
 */
class FourthActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var btnStartPause: Button

    private val handler = Handler(Looper.getMainLooper())

    /** 之前已完成计时的累计毫秒数 */
    private var accumulatedMs = 0L

    /** 当前计时段的起点（elapsedRealtime） */
    private var segmentStartElapsed = 0L

    private var isRunning = false

    private val tick = object : Runnable {
        override fun run() {
            updateDisplay()
            if (isRunning) {
                handler.postDelayed(this, TICK_INTERVAL_MS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fourth)

        tvTimer = findViewById(R.id.tvTimer)
        btnStartPause = findViewById(R.id.btnStartPause)
        val btnReset = findViewById<Button>(R.id.btnReset)

        // 恢复旋转前保存的计时状态
        if (savedInstanceState != null) {
            accumulatedMs = savedInstanceState.getLong(STATE_ACCUMULATED)
            segmentStartElapsed = savedInstanceState.getLong(STATE_SEGMENT_START)
            isRunning = savedInstanceState.getBoolean(STATE_RUNNING)
        }

        updateStartPauseText()
        updateDisplay()

        btnStartPause.setOnClickListener { toggleStartPause() }
        btnReset.setOnClickListener { reset() }
    }

    override fun onStart() {
        super.onStart()
        if (isRunning) startTicking()
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(tick)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(STATE_ACCUMULATED, accumulatedMs)
        outState.putLong(STATE_SEGMENT_START, segmentStartElapsed)
        outState.putBoolean(STATE_RUNNING, isRunning)
    }

    private fun toggleStartPause() {
        if (isRunning) {
            // 暂停：把当前段的时间并入累计值
            accumulatedMs = currentElapsedMs()
            isRunning = false
            handler.removeCallbacks(tick)
        } else {
            // 开始/继续：记录新的段起点
            segmentStartElapsed = SystemClock.elapsedRealtime()
            isRunning = true
            startTicking()
        }
        updateStartPauseText()
        updateDisplay()
    }

    private fun reset() {
        handler.removeCallbacks(tick)
        accumulatedMs = 0L
        segmentStartElapsed = 0L
        isRunning = false
        updateStartPauseText()
        updateDisplay()
    }

    private fun startTicking() {
        handler.removeCallbacks(tick)
        handler.post(tick)
    }

    private fun currentElapsedMs(): Long =
        accumulatedMs + if (isRunning) SystemClock.elapsedRealtime() - segmentStartElapsed else 0L

    private fun updateDisplay() {
        tvTimer.text = formatTime(currentElapsedMs())
    }

    private fun updateStartPauseText() {
        btnStartPause.text = if (isRunning) "暂停" else "开始"
    }

    private fun formatTime(ms: Long): String {
        val minutes = ms / 60000
        val seconds = (ms % 60000) / 1000
        val hundredths = (ms % 1000) / 10
        return String.format(Locale.getDefault(), "%02d:%02d.%02d", minutes, seconds, hundredths)
    }

    companion object {
        private const val TICK_INTERVAL_MS = 16L
        private const val STATE_ACCUMULATED = "state_accumulated"
        private const val STATE_SEGMENT_START = "state_segment_start"
        private const val STATE_RUNNING = "state_running"
    }
}
