package com.example.m5.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.m5.R
import com.example.m5.databinding.ActivityAboutBinding
import com.example.m5.util.setStatusBarTextColor
import com.example.m5.util.transparentStatusBar

class AboutActivity : AppCompatActivity() {
    lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolBlue)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transparentStatusBar(window)
        setStatusBarTextColor(window, false)

        supportActionBar?.title = "关于"
        binding.aboutText.text = aboutText()
    }

    private fun aboutText(): String {
        return "这是一个简单而高贵的音乐播放器"
    }

}