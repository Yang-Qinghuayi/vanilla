package com.example.m5.logic.dao

import android.content.Context
import com.example.m5.MusicApplication

object AppConfigDao {

    private fun sharePreferences() = MusicApplication.context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE)

    fun saveCookie(cookie: String){
        val edit = sharePreferences().edit()
        edit.putString("cookie", cookie)
        edit.apply()
    }

    fun getSavedCookie(): String?{
        val cookie = sharePreferences().getString("cookie", "")
        return cookie
    }

    fun isCookieSaved(): Boolean{
        val pd = sharePreferences().contains("cookie")
        return pd
    }


    fun saveUid(uid: Long){
        val edit = sharePreferences().edit()
        edit.putLong("uid", uid)
        edit.apply()
    }

    fun getSavedUid(): Long?{
        val uid = sharePreferences().getLong("uid", 0L)
        return uid
    }

    fun isUidSaved(): Boolean{
        val pd = sharePreferences().contains("uid")
        return pd
    }



}