package com.example.m5.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.example.m5.MusicApplication
import com.example.m5.R
import com.example.m5.activity.MainActivity
import com.example.m5.activity.PlayerActivity
import com.example.m5.data.StandardSongData
import com.example.m5.data.musicListPA
import com.example.m5.data.songPosition
import com.example.m5.frag.NowPlaying
import com.example.m5.temp.ServiceSongUrl
import com.example.m5.temp.ServiceSongUrl.getUrlProxy
import com.example.m5.util.NotificationReceiver
import com.example.m5.util.PlayMusic.Companion.isPlaying
import com.example.m5.util.formatDuration
import com.example.m5.util.getImgArt
import com.example.m5.util.runOnMainThread

class MusicService : Service(), AudioManager.OnAudioFocusChangeListener {

    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null

    //MediaSessionCompat是一个用于与媒体控制器进行通信的类
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager

    override fun onBind(intent: Intent?): IBinder {
        //创建一个MediaSessionCompat对象,baseContext是上下文,My Music是标签
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    // 作用是返回当前服务
    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun showNotification(playPauseBtn: Int, playbackSpeed: Float) {
        val intent = Intent(baseContext, MainActivity::class.java)
        intent.putExtra("index", songPosition)
        intent.putExtra("class", "NowPlaying")
        val contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        //这里的flag实现了什么功能? 不同的安卓版本有不同的实现方式
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        //PendingIntent是一种特殊的Intent,它允许其他应用程序执行您的应用程序的操作,通过广播实现从通知栏影响本地程序
        val prevIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(MusicApplication.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, flag)

        val playIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(MusicApplication.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, flag)

        val nextIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(MusicApplication.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, flag)

        val exitIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(MusicApplication.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, flag)

        val imgArt =
            getImgArt(musicListPA[songPosition].localInfo?.path!!)
        val image = if (imgArt != null) {
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.moni1)
        }

        val notification =
            androidx.core.app.NotificationCompat.Builder(baseContext, MusicApplication.CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(musicListPA[songPosition].name)
                .setContentText(
                    musicListPA[songPosition].artists?.get(
                        0
                    )?.name
                )
                .setSmallIcon(R.drawable.music_icon)
                .setLargeIcon(image)
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.sessionToken)
                )
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_skip_previous, "Previous", prevPendingIntent)
                .addAction(playPauseBtn, "Play", playPendingIntent)
                .addAction(R.drawable.ic_skip_next, "Next", nextPendingIntent)
                .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                    .putLong(
                        MediaMetadataCompat.METADATA_KEY_DURATION,
                        mediaPlayer!!.duration.toLong()
                    )
                    .build()
            )
            mediaSession.setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(
                        PlaybackStateCompat.STATE_PLAYING,
                        mediaPlayer!!.currentPosition.toLong(),
                        playbackSpeed
                    )
                    .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                    .build()
            )
        }
        startForeground(13, notification)
    }

    fun createMediaPlayer(song: StandardSongData) {
        try {
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer!!.reset()

            mediaPlayer.apply {
                getUrlProxy(song) {
                    runOnMainThread {
                        if (it == null || it is String && it.isEmpty()) {
                            /*if (playNext) {
                                toast("当前歌曲不可用, 播放下一首")
                                playNext()
                            }*/
                            return@runOnMainThread
                        }
                        when (it) {
                            is String -> {
                                try {
                                    this?.setDataSource(it)
                                } catch (e: Exception) {
                                    return@runOnMainThread
                                }
                            }
                            is Uri -> {
                                try {
                                    this?.setDataSource(applicationContext, it)
                                } catch (e: Exception) {
                                    return@runOnMainThread
                                }
                            }
                            else -> {
                                return@runOnMainThread
                            }
                        }
               /*         this?.setOnPreparedListener(this@MusicController) // 歌曲准备完成的监听
                        this?.setOnCompletionListener(this@MusicController) // 歌曲完成后的回调
                        this?.setOnErrorListener(this@MusicController)
                        this?.prepareAsync()*/
                    }
                }
            }

            mediaPlayer!!.prepare()
            mediaPlayer!!.start()

            //把播放界面装载
            PlayerActivity.binding.playPauseBtnPA.setImageResource(R.drawable.ic_pause)
//            showNotification(R.drawable.ic_pause, 1F)
            PlayerActivity.binding.tvSeekBarStart.text =
                formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.tvSeekBarEnd.text =
                formatDuration(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekBarPA.progress = 0
            PlayerActivity.binding.seekBarPA.max = mediaPlayer!!.duration
            PlayerActivity.nowPlayingId =
                musicListPA[songPosition].id.toString()
        } catch (e: Exception) {
            return
        }
    }

    fun seekBarSetup() {
        runnable = Runnable {
            PlayerActivity.binding.tvSeekBarStart.text =
                formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekBarPA.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 1000)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0) {
            isPlaying.value = false
            mediaPlayer!!.pause()
        } else {
            isPlaying.value = true
            mediaPlayer!!.start()
        }

    }

    //for making persistent
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}

suspend fun main() {
    val song = StandardSongData()
    song.id = "405998841"
    ServiceSongUrl.getUrl(song) {
        println(it.toString())
    }
    kotlinx.coroutines.delay(1000)
    println("Kissing the fire")

}
