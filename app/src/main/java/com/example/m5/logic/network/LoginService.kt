package com.example.m5.logic.network

import com.example.m5.logic.model.LoginCodeResponse
import com.example.m5.logic.model.LoginCodeStatusResponse
import com.example.m5.logic.model.LoginKetResponse
import com.example.m5.logic.model.LoginStatusResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LoginService {

    //获取key
    @GET("/login/qr/key")
    fun getKey(@Query("timestamp")timestamp: String): Call<LoginKetResponse>

    //获取二维码
    @GET("/login/qr/create?qrimg=1")
    fun getCode(@Query("key")key: String, @Query("timestamp")timestamp: String): Call<LoginCodeResponse>

    //查询二维码扫码情况
    @GET("/login/qr/check")
    fun getLoginCodeStatus(@Query("key")key: String, @Query("timestamp")timestamp: String): Call<LoginCodeStatusResponse>

    //查询登录状态
    @GET("/login/status")
    fun getLoginStatus(@Query("timestamp")timestamp: String, @Query("cookie")cookie: String): Call<LoginStatusResponse>


}