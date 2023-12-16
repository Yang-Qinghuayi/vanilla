package com.example.m5.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.m5.R
import com.example.m5.databinding.ActivityFeedbackBinding
import com.example.m5.util.setStatusBarTextColor
import com.example.m5.util.transparentStatusBar

class FeedbackActivity : AppCompatActivity() {

    lateinit var binding: ActivityFeedbackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolGreen)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transparentStatusBar(window)
        setStatusBarTextColor(window, false)
        supportActionBar?.title = "反馈"
    }
}