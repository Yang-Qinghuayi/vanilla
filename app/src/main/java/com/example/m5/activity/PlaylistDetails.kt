package com.example.m5.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.m5.R
import com.example.m5.adapter.MusicAdapterX
import com.example.m5.databinding.ActivityPlaylistDetailsBinding
import com.example.m5.util.checkPlaylist
import com.example.m5.util.setStatusBarTextColor
import com.example.m5.util.transparentStatusBar
import com.google.gson.GsonBuilder

class PlaylistDetails : AppCompatActivity() {
    lateinit var binding: ActivityPlaylistDetailsBinding

    companion object {
        var currentPlaylistPos: Int = -1
        @SuppressLint("StaticFieldLeak")
        lateinit var adapter: MusicAdapterX
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transparentStatusBar(window)
        setStatusBarTextColor(window, light = true)

        currentPlaylistPos = intent.extras?.get("index") as Int
        try {
            PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist =
                checkPlaylist(PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)
        adapter =
            MusicAdapterX(this, PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist)
        binding.playlistDetailsRV.adapter = adapter

        binding.shuffleBtnPD.setOnClickListener {
            if (PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist.size >= 1) {
                val intent =
                    Intent(this, PlayerActivity::class.java).setAction("your.custom.action")
                intent.putExtra("index", 0)
                intent.putExtra("class", "PlaylistDetailsShuffle")
                startActivity(intent)
            } else {
                Toast.makeText(this, "请先添加一点音乐吧", Toast.LENGTH_SHORT).show()
            }
        }
        binding.sequenceBtnPD.setOnClickListener {
            if (PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist.size >= 1) {
                val intent =
                    Intent(this, PlayerActivity::class.java).setAction("your.custom.action")
                intent.putExtra("index", 0)
                intent.putExtra("class", "PlaylistDetailsSequence")
                startActivity(intent)
            } else {
                Toast.makeText(this, "请先添加一点音乐吧", Toast.LENGTH_SHORT).show()
            }
        }

        binding.shuffleBtnPD.setOnLongClickListener {
            PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist.shuffle()
            adapter.notifyDataSetChanged()
            true
        }

        //为歌单添加歌曲
        binding.playlistNamePD.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SelectionActivity::class.java
                ).setAction("your.custom.action")
            )
        }
        /*        binding.removeAllPD.setOnClickListener {
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("删除全部音乐")
                        .setMessage("你想要删除所有音乐吗?")
                        .setPositiveButton("我意已决") { dialog, _ ->
                            PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist.clear()

                            dialog.dismiss()
                        }
                        .setNegativeButton("悬崖勒马") { dialog, _ ->
                            dialog.dismiss()
                        }
                    val customDialog = builder.create()
                    customDialog.show()
                }*/
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.playlistNamePD.text = PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].name

        if (adapter.itemCount > 0) {
            Glide.with(this)
                .load(PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist[0].imageUrl)
                .apply(RequestOptions().placeholder(R.drawable.moni1).centerCrop())
                .into(binding.playlistImgPD)
            binding.shuffleBtnPD.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()

        //存储我喜欢的歌
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()
    }

}