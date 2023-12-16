package com.example.m5.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.m5.R
import com.example.m5.adapter.MusicAdapterX
import com.example.m5.data.StandardSongData
import com.example.m5.databinding.ActivityFavouriteBinding
import com.example.m5.util.setStatusBarTextColor
import com.example.m5.util.transparentStatusBar
import com.example.m5.util.updateFavourites

class FavouriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouriteBinding

    companion object {
        var favouriteSongs: ArrayList<StandardSongData> = ArrayList()
        var favouritesChanged: Boolean = false
        @SuppressLint("StaticFieldLeak")
        lateinit var adapter: MusicAdapterX
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolRed)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transparentStatusBar(window)
        setStatusBarTextColor(window, light = true)


        //检查并排除已经不存在的音乐
//        favouriteSongs = checkPlaylist(favouriteSongs)



        binding.favouriteRV.setHasFixedSize(true)
        binding.favouriteRV.setItemViewCacheSize(13)
        // 设置 RecyclerView 的布局管理器为线性布局管理器
        binding.favouriteRV.layoutManager = LinearLayoutManager(this)
        // 创建 MusicAdapter 实例，并传入 MainActivity 和音乐列表作为参数
//        adapter = FavouriteAdapter(this, favouriteSongs)
        adapter =
            MusicAdapterX(this@FavouriteActivity, favouriteSongs)        // 将 musicAdapter 设置为 musicRV 的适配器
        binding.favouriteRV.adapter = adapter


        binding.shuffleBtnFA.setOnClickListener {
            if (favouriteSongs.size >= 1) {
                val intent = Intent(
                    this,
                    PlayerActivity::class.java
                ).setAction("your.custom.action")
                intent.putExtra("index", 0)
                intent.putExtra("class", "FavouriteShuffle")
                //点击随机播放跳到播放界面
                startActivity(intent)
            } else {
                Toast.makeText(this, "请先添加一点音乐吧", Toast.LENGTH_SHORT).show()
            }
        }
        //长按随机打乱歌单顺序
        binding.shuffleBtnFA.setOnLongClickListener {
            favouriteSongs.shuffle()
            adapter.notifyDataSetChanged()
            true
        }
        //顺序播放
        binding.sequenceBtnFA.setOnClickListener {
            if (favouriteSongs.size >= 1) {
                val intent = Intent(
                    this,
                    PlayerActivity::class.java
                ).setAction("your.custom.action")
                intent.putExtra("index", 0)
                intent.putExtra("class", "FavouriteSequence")
                //点击随机播放跳到播放界面
                startActivity(intent)
            } else {
                Toast.makeText(this, "请先添加一点音乐吧", Toast.LENGTH_SHORT).show()
            }
        }
        binding.addBtnFA.setOnClickListener {
            val intent = Intent(this, SelectionActivity::class.java).setAction("your.custom.action")
            intent.putExtra("from", "favouriteActivity")
            ContextCompat.startActivity(this, intent, null)
            favouritesChanged = true
        }

        favouritesChanged = false
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        if (favouritesChanged) {
            updateFavourites(favouriteSongs)
            adapter.notifyDataSetChanged()
            favouritesChanged = false
        }
    }

}
