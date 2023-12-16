package com.example.m5.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.m5.R
import com.example.m5.activity.FavouriteActivity
import com.example.m5.activity.MainActivity
import com.example.m5.activity.PlayerActivity
import com.example.m5.activity.PlaylistActivity
import com.example.m5.activity.PlaylistDetails
import com.example.m5.adapter.PlaylistViewAdapter
import com.example.m5.data.StandardSongData
import com.example.m5.data.musicListPA
import com.example.m5.data.songPosition
import com.example.m5.ui.frag.RecommendMusic
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.imageview.ShapeableImageView

@SuppressLint("NotifyDataSetChanged")
fun showItemSelectDialog(context: Context, position: Int) {

    val dialog = BottomSheetDialog(context)
    dialog.setContentView(R.layout.item_select_dialog)
    dialog.show()

    //根据环境选择显示歌曲的item
    when (context) {
        is PlayerActivity -> {
            music = musicListPA[songPosition]
            dialog.findViewById<RelativeLayout>(R.id.deleteFromPlaylist)?.visibility = View.GONE
        }

        is FavouriteActivity -> {
            music = FavouriteActivity.favouriteSongs[position]
        }

        is PlaylistDetails -> {
            music =
                PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist[position]
        }

        is MainActivity -> {
            music = MainActivity.MusicListMAX[position]
            dialog.findViewById<RelativeLayout>(R.id.deleteFromPlaylist)?.visibility = View.GONE
        }

        //默认
        else -> {
            music = RecommendMusic.recommendMusic[position]
        }
    }
    showPlaylistSelectDialogMusic(dialog, context, music)

    //绑定点击事件
    //添加进某个歌单
    dialog.findViewById<RelativeLayout>(R.id.addPlaylist)?.setOnClickListener {
        showChoosePlaylistDialog(context, music)
        dialog.dismiss()
    }
    //下一首播放
    dialog.findViewById<RelativeLayout>(R.id.playNext)?.setOnClickListener {

        if (musicListPA.isNotEmpty()) {
            var inx = -1
            musicListPA.forEachIndexed { index, musicPA ->
                if (musicPA.id == music.id) {
                    inx = index
                }
            }
            if (inx != -1)
                musicListPA.removeAt(inx)
            musicListPA.add(songPosition, music)

        }
        musicListPA.add(songPosition, music)
        Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show()
        dialog.dismiss()
    }
    //永久删除
    dialog.findViewById<RelativeLayout>(R.id.deleteFromPlaylist)?.setOnClickListener {

        when (context) {
            is FavouriteActivity -> {
                try {
                    FavouriteActivity.favouriteSongs.removeAt(position)
                } catch (e: Exception) {
                    Toast.makeText(context, "好像出了什么问题...", Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(context, "移除成功", Toast.LENGTH_SHORT).show()
                FavouriteActivity.adapter.notifyDataSetChanged()
            }

            is PlaylistDetails -> {
                try {
                    PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.removeAt(
                        position
                    )
                } catch (e: Exception) {
                    Toast.makeText(context, "好像出了什么问题...", Toast.LENGTH_SHORT).show()

                }
                Toast.makeText(context, "移除成功", Toast.LENGTH_SHORT).show()
                PlaylistDetails.adapter.notifyDataSetChanged()
            }
        }
    }


    dialog.findViewById<RelativeLayout>(R.id.deleteForever)?.setOnClickListener {
        //在控制台打印
        if (deleteMusic(music.localInfo?.path.toString()))
            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context, "还没删除", Toast.LENGTH_SHORT).show()
        dialog.dismiss()
    }
}

//装载music_item
fun showPlaylistSelectDialogMusic(dialog: BottomSheetDialog, context: Context, music: StandardSongData) {
    dialog.findViewById<ShapeableImageView>(R.id.imageMV)?.let {
        Glide.with(context)
            .load(music.imageUrl)
            .apply(RequestOptions().placeholder(R.drawable.moni1).centerCrop())
            .into(it)
    }
    dialog.findViewById<TextView>(R.id.songNameISD)?.text =
        music.name
    dialog.findViewById<TextView>(R.id.songArtistISD)?.text =
        music.artists?.get(0)?.name
}

fun showChoosePlaylistDialog(context: Context, music: StandardSongData) {
    val dialog = BottomSheetDialog(context)
    dialog.setContentView(R.layout.choose_playlist_dialog)
    dialog.show()

    //显示歌单列表
    val binding = dialog.findViewById<RecyclerView>(R.id.playlistCPD)
    binding?.setHasFixedSize(true)
    binding?.setItemViewCacheSize(13)
    // 设置 RecyclerView 的布局管理器为线性布局管理器
    binding?.layoutManager = GridLayoutManager(context, 2)
    // 创建 MusicAdapter 实例，并传入 MainActivity 和音乐列表作为参数
    val adapter = PlaylistViewAdapter(
        context,
        playlistList = PlaylistActivity.musicPlaylist.ref,
        choosePlaylistActivity = true,
        dialog = dialog
    )
    // 将 musicAdapter 设置为 musicRV 的适配器
    binding?.adapter = adapter

    val decoration: RecyclerView.ItemDecoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.right = 0
            outRect.left = 0
            outRect.top = 10
            outRect.bottom = 10
        }
    }
    binding?.addItemDecoration(decoration)

    dialog.findViewById<RelativeLayout>(R.id.addFavourite)?.setOnClickListener {
        if (music in FavouriteActivity.favouriteSongs) {
            Toast.makeText(context, "已经收藏过啦", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        } else {
            FavouriteActivity.favouriteSongs.add(music)
            Toast.makeText(context, "收藏成功!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }
}