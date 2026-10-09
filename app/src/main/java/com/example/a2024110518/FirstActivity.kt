package com.example.a2024110518

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 第一个二级 Activity：接收并显示 MainActivity 正向传递的参数（系统时间戳）。
 */
class FirstActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        val tvParam = findViewById<TextView>(R.id.tvParam)
        val btnBack = findViewById<Button>(R.id.btnBack)

        // 取 MainActivity 传入的时间戳
        val timestamp = intent.getLongExtra(EXTRA_TIMESTAMP, 0L)
        val formatted = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date(timestamp))
        tvParam.text = formatted

        btnBack.setOnClickListener { finish() }
    }

    companion object {
        const val EXTRA_TIMESTAMP = "key_timestamp"
    }
}
