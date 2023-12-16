package com.example.m5.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.m5.R
import com.example.m5.data.SOURCE_LOCAL
import com.example.m5.data.StandardSongData
import com.example.m5.data.musicListPA
import com.example.m5.data.repeatPlay
import com.example.m5.data.songPosition
import com.example.m5.databinding.ActivityPlayerBinding
import com.example.m5.frag.NowPlaying
import com.example.m5.util.PlayMusic
import com.example.m5.util.PlayMusic.Companion.isPlaying
import com.example.m5.util.PlayMusic.Companion.musicService
import com.example.m5.util.exitApplication
import com.example.m5.util.favouriteChecker
import com.example.m5.util.formatDuration
import com.example.m5.util.getImgArt
import com.example.m5.util.setSongPosition
import com.example.m5.util.setStatusBarTextColor
import com.example.m5.util.showItemSelectDialog
import com.example.m5.util.transparentStatusBar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PlayerActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var min15: Boolean = false
        var min30: Boolean = false
        var min60: Boolean = false
        var nowPlayingId: String = ""
        var isFavourite: Boolean = false
        var fIndex: Int = -1
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolBlue)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transparentStatusBar(window)
        setStatusBarTextColor(window, false)

        //界面填充
        PlayMusic.songData.observe(this) {
            Glide.with(this)
                .load(it?.imageUrl)
                .apply(RequestOptions().placeholder(R.drawable.yqhy).centerCrop())
                .into(binding.songImgPA)
            binding.songNamePA.text = it?.name
            binding.artistPA.text = it?.artists?.get(0)?.name
            setLayout()
        }

        isPlaying.observe(this) {
            if (it) {
                binding.playPauseBtnPA.setImageResource(R.drawable.ic_pause)
            } else {
                binding.playPauseBtnPA.setImageResource(R.drawable.play_icon)
            }
        }

        binding.navPA.setOnClickListener {
            showItemSelectDialog(this@PlayerActivity, position = songPosition)
        }

        //绑定播放按钮
        binding.playPauseBtnPA.setOnClickListener {
            if (isPlaying.value!!) {
                pauseMusic()
            } else {
                playMusic()
            }
        }
        //上下一首
        binding.previousBtnPA.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                preNextSong(false)
            }
        }
        binding.nextBtnPA.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                preNextSong(true)
            }
        }
        //进度条
        binding.seekBarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
            }

            //当拖动条开始拖动的时候调用,Unit是kotlin中的空类型,类似于java中的void
            override fun onStartTrackingTouch(p0: SeekBar?) = Unit
            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        })
        binding.repeatBtnPA.setOnClickListener {
            if (!repeatPlay) {
                repeatPlay = true
                binding.repeatBtnPA.setImageResource(R.drawable.repeat_one_icon)
            } else {
                repeatPlay = false
                binding.repeatBtnPA.setImageResource(R.drawable.repeat_icon)
            }
        }
        //倒计时
        binding.timerBtnPA.setOnClickListener {
            val timer = min15 || min30 || min60
            //如果没有设置定时器,就弹出设置定时器的界面
            if (!timer)
                showBottomSheetDialog()
            //如果设置了定时,就询问是否要取消
            else {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("取消定时")
                    .setMessage("你希望取消定时吗?")
                    .setPositiveButton("当然") { _, _ ->
                        min15 = false
                        min30 = false
                        min60 = false
                        binding.timerBtnPA.setColorFilter(
                            ContextCompat.getColor(
                                this,
                                R.color.blue
                            )
                        )
                    }
                    .setNegativeButton("手滑了") { dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
            }
        }
        //分享音乐文件
        binding.shareBtnPA.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                Uri.parse(musicListPA[songPosition].localInfo?.path)
            )
            startActivity(Intent.createChooser(shareIntent, "分享音乐文件给你的朋友"))
        }

        //收藏喜欢音乐

        binding.favouriteBtnPA.setOnClickListener {
            fIndex = favouriteChecker(musicListPA[songPosition].id!!)
            if (isFavourite) {
                isFavourite = false
                binding.favouriteBtnPA.setImageResource(R.drawable.favourite_empty_icon)
                FavouriteActivity.favouriteSongs.removeAt(fIndex)
            } else {
                isFavourite = true
                binding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon)
                FavouriteActivity.favouriteSongs.add(musicListPA[songPosition])
            }
            FavouriteActivity.favouritesChanged = true
        }
    }

    //填充界面的图片以及歌曲名称
    private fun setLayout() {

        //找到当前音乐在favourite中的位置
        fIndex = musicListPA[songPosition].id?.let { favouriteChecker(it) }!!
        if (repeatPlay) binding.repeatBtnPA.setImageResource(R.drawable.repeat_one_icon)
        else binding.repeatBtnPA.setImageResource(R.drawable.repeat_icon)
        if (min15 || min30 || min60)
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.bordeaux_red))
        if (isFavourite) binding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon)
        else binding.favouriteBtnPA.setImageResource(R.drawable.favourite_empty_icon)
        binding.tvSeekBarStart.text =
            formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
        binding.tvSeekBarEnd.text =
            formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
        binding.seekBarPA.progress = musicService!!.mediaPlayer!!.currentPosition
        binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
        musicService!!.seekBarSetup()
    }

    private fun playMusic() {
        isPlaying.value = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        isPlaying.value = false
        musicService!!.mediaPlayer!!.pause()
    }

    private suspend fun preNextSong(increment: Boolean) {
        if (increment) {
            setSongPosition(increment = true)

            PlayMusic().createMediaPlayer(musicListPA[songPosition])
        } else {
            setSongPosition(increment = false)
            PlayMusic().createMediaPlayer(musicListPA[songPosition])
        }
    }


    override fun onCompletion(p0: MediaPlayer?) {
        GlobalScope.launch(Dispatchers.IO) {
            setSongPosition(increment = true)
            PlayMusic().createMediaPlayer(musicListPA[songPosition])
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 || resultCode == RESULT_OK) {
            return
        }
    }

    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(this@PlayerActivity)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener {
            Toast.makeText(baseContext, "15分钟之后播放结束", Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.bordeaux_red))
            min15 = true
            Thread {
                Thread.sleep(15 * 60000)
                if (min15) exitApplication()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener {
            Toast.makeText(baseContext, "30分钟之后播放结束", Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.bordeaux_red))
            min30 = true
            Thread {
                Thread.sleep(30 * 60000)
                if (min30) exitApplication()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener {
            Toast.makeText(baseContext, "60分钟之后播放结束", Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.bordeaux_red))
            min60 = true
            Thread {
                Thread.sleep(60 * 60000)
                if (min60) exitApplication()
            }.start()
            dialog.dismiss()
        }
    }
}