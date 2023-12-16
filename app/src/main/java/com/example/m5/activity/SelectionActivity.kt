package com.example.m5.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.m5.R
import com.example.m5.adapter.MusicAdapterX
import com.example.m5.databinding.ActivitySelectionBinding
import com.example.m5.util.setStatusBarTextColor
import com.example.m5.util.transparentStatusBar

class SelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectionBinding
    private lateinit var adapter: MusicAdapterX

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        setContentView(binding.root)
        transparentStatusBar(window)
        setStatusBarTextColor(window, false)

        //假如是从喜欢的界面传过来,就得到上一个界面的来源并且传给Music Adapter
        val from = intent.getStringExtra("from").toString()

        binding.selectionRV.setItemViewCacheSize(10)
        binding.selectionRV.setHasFixedSize(true)
        binding.selectionRV.layoutManager = LinearLayoutManager(this)
        adapter = MusicAdapterX(
            this,
            MainActivity.MusicListMAX,
            selectionActivity = true
        )
        binding.selectionRV.adapter = adapter

        R.drawable.moni1
        //搜索框监听
        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.musicListSearch = ArrayList()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    for(song in MainActivity.MusicListMAX){
                        if(song.name?.lowercase()?.contains(userInput) == true){
                            MainActivity.musicListSearch.add(song)
                        }
                    }
                    MainActivity.search = true
                    //实时更新页面
                    adapter.updateMusicList(searchList = MainActivity.musicListSearch)
                }
                return true
            }
        })
    }
}