package com.example.m5.logic.network

import com.example.m5.logic.model.MusicSearchResponse
import com.example.m5.logic.model.SongResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface SearchService {

    @GET("/cloudsearch?limit=16")
    fun searchMusic(@Query("keywords") keywords: String): Call<MusicSearchResponse>

    @GET("/song/url/v2")
    suspend fun getUrl(@Query("id")id: String, @Query("level")level: String): SongResponse

}