package com.example.a2024110518

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvResult = findViewById<TextView>(R.id.tvResult)

        // 1. 正向传参：启动 FirstActivity 并传入系统时间戳
        findViewById<Button>(R.id.btnFirst).setOnClickListener {
            val intent = Intent(this, FirstActivity::class.java).apply {
                putExtra(FirstActivity.EXTRA_TIMESTAMP, System.currentTimeMillis())
            }
            startActivity(intent)
        }

        // 2. 等待返回结果：启动 SecondActivity
        findViewById<Button>(R.id.btnSecond).setOnClickListener {
            startActivityForResult(Intent(this, SecondActivity::class.java), REQUEST_SECOND)
        }

        // 3. 拍照：启动 ThirdActivity（相机在 ThirdActivity 内部调起）
        findViewById<Button>(R.id.btnThird).setOnClickListener {
            startActivity(Intent(this, ThirdActivity::class.java))
        }

        // 4. 秒表：启动 FourthActivity
        findViewById<Button>(R.id.btnFourth).setOnClickListener {
            startActivity(Intent(this, FourthActivity::class.java))
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SECOND && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra(SecondActivity.EXTRA_RESULT).orEmpty()
            findViewById<TextView>(R.id.tvResult).text = getString(R.string.result_prefix, result)
        }
    }

    companion object {
        private const val REQUEST_SECOND = 100
    }
}
