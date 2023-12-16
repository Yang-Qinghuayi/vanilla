package com.example.m5.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.m5.util.Playlist
import com.example.m5.activity.PlaylistActivity
import com.example.m5.activity.PlaylistDetails
import com.example.m5.R
import com.example.m5.databinding.PlaylistViewBinding
import com.example.m5.util.music
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistViewAdapter(
    private val context: Context, private var playlistList: ArrayList<Playlist>,
    private val choosePlaylistActivity: Boolean = false,
    private val dialog: BottomSheetDialog = BottomSheetDialog(context)
) :
    RecyclerView.Adapter<PlaylistViewAdapter.MyHolder>() {

    class MyHolder(binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.playlistImg
        val name = binding.playlistName
        val root = binding.root
        val songNum = binding.playlistSongNum
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyHolder {
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = playlistList[position].name
        holder.name.isSelected = true
        holder.songNum.text = playlistList[position].playlist.size.toString() + "首"

        when {
            choosePlaylistActivity -> {
                holder.root.setOnClickListener {
                    if (music in playlistList[position].playlist) {
                        Toast.makeText(context, "已经收藏过啦", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    } else {
                        playlistList[position].playlist.add(music)
                        Toast.makeText(context, "收藏成功!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }

            else -> {
                holder.root.setOnClickListener {
                    val intent =
                        Intent(context, PlaylistDetails::class.java).setAction("your.custom.action")
                    intent.putExtra("index", position)
                    ContextCompat.startActivity(context, intent, null)
                }
            }
        }


        //长按删除
        holder.root.setOnLongClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playlistList[position].name)
                .setMessage("你想要删除当前歌单吗?")
                .setPositiveButton("差不多") { dialog, _ ->
                    PlaylistActivity.musicPlaylist.ref.removeAt(position)
                    refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("手滑了") { dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            true
        }

        if (PlaylistActivity.musicPlaylist.ref[position].playlist.size > 0) {
            Glide.with(context)
                .load(PlaylistActivity.musicPlaylist.ref[position].playlist[0].imageUrl)
                .apply(RequestOptions().placeholder(R.drawable.yqhy).centerCrop())
                .into(holder.image)
        }
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshPlaylist() {
        playlistList = ArrayList()
        playlistList.addAll(PlaylistActivity.musicPlaylist.ref)
        notifyDataSetChanged()
    }


}