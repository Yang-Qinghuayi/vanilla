package com.example.m5.logic.model


//key返回
data class LoginKetResponse(val code: String, val data: KeyData)

data class KeyData(val code: String, val unikey: String)

//二维码返回
data class LoginCodeResponse(val code: String, val data: CodeData)

data class CodeData(val qrurl: String, val qrimg: String)

//查询二维码状态
data class LoginCodeStatusResponse(val code: String, val message: String, val cookie: String)

//查询登录状态
data class LoginStatusResponse(val data: StatusData)

data class StatusData(val code: String, val profile: Profile)

data class Profile(val userId: Long, val nickname: String, val avatarUrl: String)

//登陆状态下获取用户详情


