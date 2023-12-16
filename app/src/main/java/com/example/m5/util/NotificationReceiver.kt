package com.example.m5.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.m5.MusicApplication
import com.example.m5.R
import com.example.m5.activity.PlayerActivity
import com.example.m5.activity.PlayerActivity.Companion.fIndex
import com.example.m5.data.musicListPA
import com.example.m5.data.songPosition
import com.example.m5.frag.NowPlaying
import com.example.m5.util.PlayMusic.Companion.isPlaying
import com.example.m5.util.PlayMusic.Companion.musicService

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(content: Context?, intent: Intent?) {
        when (intent?.action) {
            MusicApplication.PREVIOUS -> preNextSong(increment = false, context = content!!)
            MusicApplication.PLAY -> if (isPlaying.value!!) pauseMusic() else playMusic()

            MusicApplication.NEXT -> preNextSong(increment = true, context = content!!)

            MusicApplication.EXIT -> {
                exitApplication()
            }
        }
    }

    //根据通知栏里面的点击情况来控制音乐的播放和暂停,改变通知栏的图标以及播放界面的播放图标
    private fun playMusic() {
          isPlaying.value = true
          musicService!!.mediaPlayer!!.start()
    }
    private fun pauseMusic() {
          isPlaying.value = false
          musicService!!.mediaPlayer!!.pause()
    }
    private fun preNextSong(increment: Boolean, context: Context) {
        setSongPosition(increment = increment)
        playMusic()
          fIndex =
            favouriteChecker(  musicListPA[  songPosition].id.toString())
    }
}