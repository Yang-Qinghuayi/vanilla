package com.example.m5.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.m5.databinding.ItemPlaylistBinding
import com.example.m5.logic.model.PlayList
import com.example.m5.ui.activity.NeBrowseActivity
import com.example.m5.ui.frag.NeteaseBrowse

class PlayListsAdapter(
    private val playLists: ArrayList<PlayList>,
    private val context: Context
) : RecyclerView.Adapter<PlayListsAdapter.MyHolder>() {

    class MyHolder(binding: ItemPlaylistBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.playlistImg
        val name = binding.playlistName
        val root = binding.root
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyHolder {
        return MyHolder(
            ItemPlaylistBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        //加载
        val playList = playLists[position]
        holder.name.text = playList.name
        Glide.with(NeteaseBrowse.instance!!)
            .load(playList.coverImgUrl)
            .into(holder.image)
    }

    override fun getItemCount() = playLists.size
}