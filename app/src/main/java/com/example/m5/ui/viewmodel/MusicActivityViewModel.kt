package com.example.m5.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.m5.logic.Repository
import com.example.m5.logic.model.HotMusic
import com.example.m5.logic.model.MusicAl
import com.example.m5.logic.model.PlayList

class MusicActivityViewModel: ViewModel() {

    private val refreshLiveData = MutableLiveData<Any?>()



    var playLists = ArrayList<PlayList>()
    var hotMusicLists = ArrayList<HotMusic>()
    var newMusicAls = ArrayList<MusicAl>()


    val mainLiveData = Transformations.switchMap(refreshLiveData){
        Repository.refreshNc()
    }

    fun refresh(){
        refreshLiveData.value = refreshLiveData.value
    }












}