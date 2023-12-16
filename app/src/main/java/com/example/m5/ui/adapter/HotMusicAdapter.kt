package com.example.m5.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.m5.R
import com.example.m5.logic.model.HotMusic
import com.example.m5.ui.activity.NeBrowseActivity

class HotMusicAdapter(private val musics: List<HotMusic>): RecyclerView.Adapter<HotMusicAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val hotMusicImage1: ImageView = view.findViewById(R.id.hotMusicImage1)
        val hotMusicTitle1: TextView = view.findViewById(R.id.hotMusicTitle1)
        val hotMusicContent1: TextView = view.findViewById(R.id.hotMusicContent1)
        val hotMusicImage2: ImageView = view.findViewById(R.id.hotMusicImage2)
        val hotMusicTitle2: TextView = view.findViewById(R.id.hotMusicTitle2)
        val hotMusicContent2: TextView = view.findViewById(R.id.hotMusicContent2)
        val hotMusicImage3: ImageView = view.findViewById(R.id.hotMusicImage3)
        val hotMusicTitle3: TextView = view.findViewById(R.id.hotMusicTitle3)
        val hotMusicContent3: TextView = view.findViewById(R.id.hotMusicContent3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hotmusic, parent, false)
        val holder = ViewHolder(view)

        //点击事件
        holder.itemView.setOnClickListener{
            Log.d("hucheng", "hot 点击")
        }

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //加载数据
        if(position < musics.size - 1){
            holder.hotMusicContent1.text = musics[position*3].content
            holder.hotMusicContent2.text = musics[position*3+1].content
            holder.hotMusicContent3.text = musics[position*3+2].content

            holder.hotMusicTitle1.text = musics[position*3].searchWord
            holder.hotMusicTitle2.text = musics[position*3+1].searchWord
            holder.hotMusicTitle3.text = musics[position*3+2].searchWord

            Glide.with(NeBrowseActivity.instance!!)
                .load(musics[position*3].iconUrl)
                .into(holder.hotMusicImage1)

            Glide.with(NeBrowseActivity.instance!!)
                .load(musics[position*3+1].iconUrl)
                .into(holder.hotMusicImage2)

            Glide.with(NeBrowseActivity.instance!!)
                .load(musics[position*3+2].iconUrl)
                .into(holder.hotMusicImage3)
        }else{
            holder.hotMusicContent1.text = musics[position*3].content
            holder.hotMusicTitle1.text = musics[position*3].searchWord
            Glide.with(NeBrowseActivity.instance!!)
                .load(musics[position*3].iconUrl)
                .into(holder.hotMusicImage1)
        }


    }

    override fun getItemCount() = musics.size/3
}