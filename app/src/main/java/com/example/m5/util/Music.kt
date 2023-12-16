package com.example.m5.util

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.example.m5.activity.FavouriteActivity
import com.example.m5.activity.PlayerActivity
import com.example.m5.activity.PlayerActivity.Companion.isFavourite
import com.example.m5.data.StandardSongData
import com.example.m5.data.musicListPA
import com.example.m5.data.repeatPlay
import com.example.m5.data.songPosition
import com.example.m5.logic.model.Al
import com.example.m5.logic.model.Ar
import com.example.m5.util.PlayMusic.Companion.musicService
import java.io.File
import kotlin.system.exitProcess


data class Music(
    val id: String, val title: String, val album: String,
    val artist: String, val duration: Long = 0, val path: String, val artUri: String
)

lateinit var music: StandardSongData

class Playlist {
    lateinit var name: String
    lateinit var playlist: ArrayList<StandardSongData>
    lateinit var createdOn: String
}

class MusicPlaylist {
    var ref: ArrayList<Playlist> = ArrayList()
}

fun formatDuration(duration: Long): String {
    val minute = duration / 1000 / 60
    val second = duration / 1000 % 60
    return String.format("%02d:%02d", minute, second)
}

//从指定的音频文件路径中获取嵌入的图片数据,即专辑封面
fun getImgArt(path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}

fun setSongPosition(increment: Boolean) {
    //如果不是单曲循环,那么就正常的播放下一首或者上一首
    if (! repeatPlay) {
        if (increment) {
            if (  songPosition ==   musicListPA.size - 1) {
                  songPosition = 0
            } else   songPosition++

        } else {
            if (  songPosition == 0) {
                  songPosition =   musicListPA.size - 1
            } else   songPosition--
        }
    }
    //如果是单曲循环,那么就不改变songPosition的值
}

fun exitApplication() {
    if (  musicService != null) {
          musicService!!.audioManager.abandonAudioFocus(  musicService)
          musicService!!.stopForeground(true)
          musicService!!.mediaPlayer!!.release()
          musicService = null
    }
    exitProcess(1)
}

fun favouriteChecker(id: String): Int {
      isFavourite = false
    FavouriteActivity.favouriteSongs.forEachIndexed { index, music ->
        if (id == music.id) {
              isFavourite = true
            return index
        }
    }
    return -1
}

fun checkPlaylist(playlist: ArrayList<StandardSongData>): ArrayList<StandardSongData> {
    playlist.forEachIndexed { index, music ->
        val file = music.localInfo?.path?.let { File(it) }
        if (!file?.exists()!!)
            playlist.removeAt(index)
    }
    return playlist
}

fun underBar(
    activity: Activity,
    drawable: Drawable,
    contentView: View
) {
    val old = activity.window.decorView.background
    activity.window.setBackgroundDrawable(drawable)
    contentView.background = old
}

fun transparentStatusBar(window: Window) {
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    var systemUiVisibility = window.decorView.systemUiVisibility
    systemUiVisibility =
        systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    window.decorView.systemUiVisibility = systemUiVisibility
    window.statusBarColor = Color.TRANSPARENT

    //设置状态栏文字颜色
//    setStatusBarTextColor(window, false)
}
fun setStatusBarTextColor(window: Window, light: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility = if (light) { //白色文字
            systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        } else { //黑色文字
            systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.decorView.systemUiVisibility = systemUiVisibility
    }
}

fun updateFavourites(newList: ArrayList<StandardSongData>) {
    val musicList = ArrayList<StandardSongData>()
    musicList.addAll(newList)
}



