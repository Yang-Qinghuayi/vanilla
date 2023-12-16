package com.example.m5.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.m5.data.StandardSongData
import com.example.m5.logic.Repository
import com.example.m5.logic.model.Song

class SearchViewModel: ViewModel() {

    private val searchLiveData = MutableLiveData<String>()
    var searchSongs =  ArrayList<StandardSongData>()

    val musicLiveData = Transformations.switchMap(searchLiveData){ query->
        Repository.searchMusic(query)
    }

    fun searchMusic(query: String){
        searchLiveData.value = query
    }

}