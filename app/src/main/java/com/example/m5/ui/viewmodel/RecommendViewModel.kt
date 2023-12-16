package com.example.m5.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.m5.data.StandardSongData
import com.example.m5.logic.Repository

class RecommendViewModel: ViewModel() {

    private var recommendLiveData = MutableLiveData<String>()

    var dailySongs =  ArrayList<StandardSongData>()
    var position: Int = 0


    val recommendFocus = Transformations.switchMap(recommendLiveData){ cookie->
        Repository.getRecommend(cookie)
    }

    fun getRecommend(cookie: String?){
        recommendLiveData.value = cookie
    }


}