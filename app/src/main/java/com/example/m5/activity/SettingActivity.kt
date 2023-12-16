package com.example.m5.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.m5.R
import com.example.m5.databinding.ActivitySettingBinding
import com.example.m5.util.setStatusBarTextColor
import com.example.m5.util.transparentStatusBar


class SettingActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolBlack)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transparentStatusBar(window)
        setStatusBarTextColor(window, false)

        binding.coolGreenTheme.setOnClickListener { saveTheme(0) }
        binding.coolRedTheme.setOnClickListener { saveTheme(1) }
        binding.coolCyanTheme.setOnClickListener { saveTheme(2) }
        binding.coolBlueTheme.setOnClickListener { saveTheme(3) }
        binding.coolBlackTheme.setOnClickListener { saveTheme(4) }

        binding.navAbout.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AboutActivity::class.java
                ).setAction("your.custom.action")
            )
        }
        binding.navFeedback.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    FeedbackActivity::class.java
                ).setAction("your.custom.action")
            )
        }
        binding.kiss.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).setAction("your.custom.action")
            )
        }
    }

    private fun saveTheme(index: Int) {
        if (MainActivity.themeIndex != index) {
            val editor = getSharedPreferences("THEMES", MODE_PRIVATE).edit()
            editor.putInt("themeIndex", index)
            editor.apply()
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).setAction("your.custom.action")
            )
        } else {
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).setAction("your.custom.action")
            )
        }
    }

    private fun setVersionDetails(): String {
        return "Version Name: 1.0"
    }

    override fun onStart() {
        super.onStart()
        binding.blurLayout.startBlur()
        binding.blurLayout.lockView()
    }

    override fun onStop() {
        super.onStop()
        binding.blurLayout.pauseBlur()
    }
}