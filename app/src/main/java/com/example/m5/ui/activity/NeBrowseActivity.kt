package com.example.m5.ui.activity

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.m5.R
import com.example.m5.databinding.ActivityNcBinding
import com.example.m5.ui.adapter.HotMusicAdapter
import com.example.m5.ui.viewmodel.MusicActivityViewModel
import com.example.m5.ui.adapter.PlayListsAdapter
import com.example.m5.ui.adapter.ViewPagerAdapter
import com.example.m5.util.setStatusBarTextColor
import com.example.m5.util.transparentStatusBar


class NeBrowseActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this)[MusicActivityViewModel::class.java] }

    private lateinit var binding: ActivityNcBinding

    private lateinit var adapterPlayList: PlayListsAdapter
    private lateinit var adapterNewMusicAls: ViewPagerAdapter


    companion object {
        var instance: NeBrowseActivity? =null
    }


    @SuppressLint("ResourceType", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolBlack)
        instance = this

        binding = ActivityNcBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transparentStatusBar(window)
        setStatusBarTextColor(window, light = false)

        val layoutManagerPlayLists = GridLayoutManager(this,2,GridLayoutManager.HORIZONTAL,false)
        binding.recyclerViewPlaylist.layoutManager = layoutManagerPlayLists
        adapterPlayList = PlayListsAdapter(viewModel.playLists,this)
        binding.recyclerViewPlaylist.adapter = adapterPlayList

        val decoration: RecyclerView.ItemDecoration = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.right = 0
                outRect.left = 38
                outRect.top = 10
                outRect.bottom = 10
            }
        }
        binding.recyclerViewPlaylist.addItemDecoration(decoration)

        viewModel.refresh()

        viewModel.mainLiveData.observe(this) { result->
            val mainNcResponse = result.getOrNull()
            if(mainNcResponse != null){
                viewModel.playLists.addAll(mainNcResponse.playLists)
                viewModel.hotMusicLists.addAll(mainNcResponse.hotMusics)
                viewModel.newMusicAls.addAll(mainNcResponse.newMusicesAls)

                adapterPlayList.notifyDataSetChanged()
                adapterPlayList.notifyDataSetChanged()
                adapterNewMusicAls.notifyDataSetChanged()
            }
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}