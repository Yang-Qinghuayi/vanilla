package com.example.m5.ui.frag

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.m5.adapter.MusicAdapterX
import com.example.m5.data.StandardSongData
import com.example.m5.databinding.ActivityRecommendBinding
import com.example.m5.databinding.ActivitySearchBinding
import com.example.m5.ui.viewmodel.RecommendViewModel
import com.example.m5.logic.model.DailySong
import com.example.m5.logic.model.Song
import com.example.m5.ui.AppConfig
import com.example.m5.ui.viewmodel.SearchViewModel

class SearchMusic : Fragment() {

    lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel

    companion object {
        var instance: SearchMusic? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        instance = this
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        //绑定adapter
        val layoutManagerRecommend = LinearLayoutManager(this.context)
        binding.searchRecyclerView.layoutManager = layoutManagerRecommend
        val musicAdapter = MusicAdapterX(this.requireContext(),viewModel.searchSongs )
        binding.searchRecyclerView.adapter = musicAdapter

        //点击搜索事件
        binding.toolbarSearch.searchMusic.setOnClickListener {
            val context = binding.toolbarSearch.searchMusicEdit.text.toString()
            if(context.isNotEmpty()){
                viewModel.searchMusic(context)
            }
        }

        //监听
        viewModel.musicLiveData.observe(this.viewLifecycleOwner){ result->
            val songs = result.getOrNull()
            songs?.reversed()
            if (songs != null){
                viewModel.searchSongs.clear()
                viewModel.searchSongs.addAll(searchSongToSong(songs))
                musicAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun searchSongToSong(songs: List<Song>): ArrayList<StandardSongData> {
        val list = ArrayList<StandardSongData>()
        for (song in songs) {
            list.add(song.switchToStandard())
        }
        return list
    }

}