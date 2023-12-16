package com.example.m5.logic.network

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object MusicNetwork {

    private val searchService = ServiceCreator.create<SearchService>()
    private val mainService = ServiceCreator.create<MainService>()
    private val loginService = ServiceCreator.create<LoginService>()
    private val RecommendService = ServiceCreator.create<RecommendService>()


    suspend fun searchMusic(keywords: String) = searchService.searchMusic(keywords).await()
    suspend fun getUrl(id: String, level: String) = searchService.getUrl(id, level)

    suspend fun getHighQuality() = mainService.mainHighQuality().await()

    suspend fun getHotMusic() = mainService.mainHotMusic().await()

    suspend fun getNewMusicAls() = mainService.mainNewSongAl().await()

    suspend fun getKey(timestamp: String) = loginService.getKey(timestamp).await()

    suspend fun getCode(key: String, timestamp: String) =
        loginService.getCode(key, timestamp).await()

    suspend fun getCodeStatue(key: String, timestamp: String) =
        loginService.getLoginCodeStatus(key, timestamp).await()

    suspend fun getStatus(timestamp: String, cookie: String) =
        loginService.getLoginStatus(timestamp, cookie).await()

    suspend fun getRecommend(cookie: String) = RecommendService.getRecommend(cookie).await()
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) {
                        continuation.resume(body)
                    } else {
                        continuation.resumeWithException(
                            RuntimeException("response body is null")
                        )
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    Log.d("hucheng", "network error: $t")
                    continuation.resumeWithException(t)
                }
            })
        }
    }

}