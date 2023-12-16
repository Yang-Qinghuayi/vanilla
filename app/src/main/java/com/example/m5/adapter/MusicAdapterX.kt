package com.example.m5.adapter

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.os.IBinder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.m5.R
import com.example.m5.data.StandardSongData
import com.example.m5.data.musicListPA
import com.example.m5.data.songPosition
import com.example.m5.databinding.MusicViewBinding
import com.example.m5.frag.NowPlaying
import com.example.m5.service.MusicService
import com.example.m5.util.PlayMusic
import com.example.m5.util.PlayMusic.Companion.musicService
import com.example.m5.util.showItemSelectDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MusicAdapterX(
    private val context: Context, private var musicList: ArrayList<StandardSongData>,
    private val selectionActivity: Boolean = false
) : RecyclerView.Adapter<MusicAdapterX.MyHolder>(), ServiceConnection {

    class MyHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNameMV
        val artist = binding.artistMV
        val image = binding.imageMV
        val root = binding.root
        val moreAction = binding.moreAction
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    /*    private fun addSong(song: Music): Boolean {
            val playlist = if (from == "favouriteActivity")
                FavouriteActivity.favouriteSongs
            else
                PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist

            playlist.forEachIndexed { index, music ->
                if (music.id == song.id) {
                    playlist.removeAt(
                        index
                    )
                    return false
                }
            }
            playlist.add(song)
            return true
        }*/

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.title.text = musicList[position].name
        holder.artist.text = musicList[position].artists?.get(0)?.name
        //加载图片
        Glide.with(context)
            .load(musicList[position].imageUrl)
            .apply(RequestOptions().placeholder(R.drawable.moni2).centerCrop())
            .into(holder.image)

        if (!selectionActivity)
            holder.moreAction.setOnClickListener {
                showItemSelectDialog(context, position)
            }
        holder.root.setOnClickListener {
            musicListPA = musicList
            songPosition = position

            if (musicService == null)
                startService(musicList, shuffle = false, position)
            else {
                GlobalScope.launch(Dispatchers.Main) {
                    PlayMusic().createMediaPlayer(musicList[position])
                }
            }
            PlayMusic.isPlaying.value = true
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicList(searchList: ArrayList<StandardSongData>) {
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }

    private fun startService(
        playlist: ArrayList<StandardSongData>,
        shuffle: Boolean,
        position: Int
    ) {
        val intent = Intent(context, MusicService::class.java)
        context.bindService(intent, this, AppCompatActivity.BIND_AUTO_CREATE)
        context.startService(intent)
        musicListPA = ArrayList()
        musicListPA.addAll(playlist)
        songPosition = position
        if (shuffle) musicListPA.shuffle()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        GlobalScope.launch(Dispatchers.Main) {
            if (musicService == null) {
                val binder = service as MusicService.MyBinder
                musicService = binder.currentService()
                musicService!!.audioManager =
                    context.getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
                musicService!!.audioManager.requestAudioFocus(
                    musicService,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
                )
            }
            PlayMusic().createMediaPlayer(musicListPA[songPosition])
        }
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }
}
