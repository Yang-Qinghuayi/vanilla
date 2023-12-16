package com.example.m5

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.example.m5.service.MusicService
import com.example.m5.util.PlayMusic

class MusicApplication: Application() {

    // 你好
    companion object{
        lateinit var context: Context
        const val CHANNEL_ID = "channel1"
        const val PLAY = "play"
        const val NEXT = "next"
        const val PREVIOUS = "previous"
        const val EXIT = "exit"
        var musicController = MutableLiveData<PlayMusic?>()
    }


    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        //这里是在创建应用程序时就创建了通知渠道
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(CHANNEL_ID, "Now Playing Song", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "This channel is important channel for showing song!!"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}