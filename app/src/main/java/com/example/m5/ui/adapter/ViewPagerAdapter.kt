package com.example.m5.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.m5.R
import com.example.m5.logic.model.MusicAl
import com.example.m5.ui.activity.NeBrowseActivity

class ViewPagerAdapter(val musicAls: List<MusicAl>): PagerAdapter() {

//    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
//        val loopPicture: ImageView = view.findViewById(R.id.loopPicture)
//        val loopName: TextView = view.findViewById(R.id.loopName)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_new, parent, false)
//        val holder = ViewHolder(view)
//
//        return holder
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val musicAl = musicAls[position]
//        holder.loopName.text = musicAl.artists[0]?.name
//
//        Glide.with(NcActivity.instance!!)
//            .load(musicAl.artists[0]?.picUrl)
//            .into(holder.loopPicture)
//    }
//
//    override fun getItemCount() = musicAls.size

    override fun getCount(): Int {
        return musicAls.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.item_new, container, false)
        val imageView: ImageView = view.findViewById(R.id.loopPicture)
        val textView: TextView = view.findViewById(R.id.loopName)

        val musicAl = musicAls[position]
        textView.text = musicAl.artists[0].name
        Glide.with(NeBrowseActivity.instance!!)
            .load(musicAl.artists[0].picUrl)
            .into(imageView)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        container.removeView(`object` as  View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
}