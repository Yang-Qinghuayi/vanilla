package com.example.m5.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.m5.activity.FavouriteActivity
import com.example.m5.activity.MainActivity
import com.example.m5.databinding.ActivityNetEaseMainBinding
import com.example.m5.ui.frag.NeteaseBrowse
import com.example.m5.ui.frag.NeteaseMine
import com.example.m5.ui.frag.RecommendMusic
import com.example.m5.ui.frag.SearchMusic
import com.example.m5.ui.viewmodel.NetEaseMainViewModel
import com.example.m5.util.setStatusBarTextColor
import com.example.m5.util.transparentStatusBar

class NetEaseMainActivity : AppCompatActivity() {

    lateinit var binding: ActivityNetEaseMainBinding
    private lateinit var viewModel: NetEaseMainViewModel

    companion object {
        var install: NetEaseMainActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val themeEditor = getSharedPreferences("THEMES", MODE_PRIVATE)
        MainActivity.themeIndex = themeEditor.getInt("themeIndex", 0)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])

        binding = ActivityNetEaseMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[NetEaseMainViewModel::class.java]
        //透明状态栏以及文字颜色设定
        transparentStatusBar(window)
        setStatusBarTextColor(window, false)

        install = this

        //这是activity自带的,不用提取到fragment
        binding.viewPager2.offscreenPageLimit = 4
        binding.viewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 4
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> NeteaseMine()
                    1 -> RecommendMusic()
                    2 -> NeteaseBrowse()
                    3 -> SearchMusic()
                    else -> SearchMusic()
                }
            }
        }
        //根据所选的页数更换展示的fragment
        viewModel.netEasePage.observe(this) {
            binding.viewPager2.setCurrentItem(it, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}