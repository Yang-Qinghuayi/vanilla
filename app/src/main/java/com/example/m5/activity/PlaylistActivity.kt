package com.example.m5.activity

import android.graphics.Rect
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.m5.R
import com.example.m5.adapter.PlaylistViewAdapter
import com.example.m5.databinding.ActivityPlaylistBinding
import com.example.m5.databinding.AddPlaylistDialogBinding
import com.example.m5.util.MusicPlaylist
import com.example.m5.util.Playlist
import com.example.m5.util.setStatusBarTextColor
import com.example.m5.util.transparentStatusBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Locale


class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adapter: PlaylistViewAdapter

    companion object {
        var musicPlaylist: MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        setTheme(R.style.coolRed)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transparentStatusBar(window)
        setStatusBarTextColor(window, light = true)

        // 设置 RecyclerView 的固定大小以及缓存的项数，以优化性能
        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        // 设置 RecyclerView 的布局管理器为线性布局管理器
        binding.playlistRV.layoutManager = GridLayoutManager(this@PlaylistActivity, 2)
        // 创建 MusicAdapter 实例，并传入 MainActivity 和音乐列表作为参数
        adapter = PlaylistViewAdapter(this, playlistList = musicPlaylist.ref)
        // 将 musicAdapter 设置为 musicRV 的适配器
        binding.playlistRV.adapter = adapter
        binding.addPlaylistBtn.setOnClickListener { customAlertDialog() }

        val decoration: ItemDecoration = object : ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.right = 0
                outRect.left = 38
                outRect.top = 10
                outRect.bottom = 10
            }
        }
        binding.playlistRV.addItemDecoration(decoration)
    }

    private fun customAlertDialog() {
        val customDialog = LayoutInflater.from(this@PlaylistActivity)
            .inflate(R.layout.add_playlist_dialog, binding.root, false)
        var binder = AddPlaylistDialogBinding.bind(customDialog)

        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle("创建")
            .setPositiveButton("完成") { dialog, _ ->
                val playlistName = binder.playlistName.text.toString()
                if (playlistName.isNotEmpty()) {
                    addPlaylist(playlistName)
                }
                dialog.dismiss()
            }
        val cusDialog = builder.create()
        cusDialog.show()

    }

    private fun addPlaylist(name: String) {
        var playlistExists = false
        for (i in musicPlaylist.ref) {
            if (name.equals(i.name)) {
                playlistExists = true
                break
            }
        }
        if (playlistExists) Toast.makeText(this, "已经创建过啦", Toast.LENGTH_SHORT).show()
        else {
            val tempPlaylist = Playlist()
            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn = sdf.format(calendar)
            musicPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlaylist()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}