package com.example.m5.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.m5.adapter.MusicAdapterX
import com.example.m5.data.StandardSongData
import com.example.m5.databinding.ActivityRecommendBinding
import com.example.m5.ui.viewmodel.RecommendViewModel
import com.example.m5.logic.model.DailySong
import com.example.m5.ui.AppConfig

class RecommendMusic : Fragment() {

    lateinit var binding: ActivityRecommendBinding
    private lateinit var viewModel: RecommendViewModel

    companion object {
        var instance: RecommendMusic? = null

        var recommendMusic:List<StandardSongData> = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityRecommendBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        instance = this
        viewModel = ViewModelProvider(this)[RecommendViewModel::class.java]

        //绑定adapter
        val layoutManagerRecommend = LinearLayoutManager(this.context)
        binding.recommendRecyclerView.layoutManager = layoutManagerRecommend
        val musicAdapter = MusicAdapterX(this.requireContext(),viewModel.dailySongs)
        binding.recommendRecyclerView.adapter = musicAdapter


        //数据绑定
        viewModel.recommendFocus.observe(this.viewLifecycleOwner) { result->
            val recommend = result.getOrNull()
            if (recommend != null){
                viewModel.dailySongs.clear()
                viewModel.dailySongs.addAll(dailySongToSong(recommend.data.dailySongs))
                recommendMusic = viewModel.dailySongs
                musicAdapter.notifyDataSetChanged()
            }
        }

        //获取每日推荐
        viewModel.getRecommend(AppConfig.cookie)
    }

    private fun dailySongToSong(songs: List<DailySong>): ArrayList<StandardSongData> {
        val list = ArrayList<StandardSongData>()
        for (song in songs) {
            list.add(song.switchToStandard())
        }
        return list
    }

}