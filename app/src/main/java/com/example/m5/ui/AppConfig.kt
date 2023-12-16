package com.example.m5.ui

object AppConfig {
    lateinit var cookie: String
    var uid: Long? = null
    var isLogined: Boolean = false//还是需要有一个登录状态，因为如果cookie过期了呢
    var nickname: String? = null
    var avatarUrl: String? = null
    var isChanged: Boolean = false
}