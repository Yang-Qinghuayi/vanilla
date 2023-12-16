package com.example.m5.frag

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.m5.MusicApplication
import com.example.m5.R
import com.example.m5.activity.MainActivity
import com.example.m5.activity.PlayerActivity
import com.example.m5.data.musicListPA
import com.example.m5.data.songPosition
import com.example.m5.databinding.FragmentNowPlayingBinding
import com.example.m5.util.PlayMusic
import com.example.m5.util.PlayMusic.Companion.isPlaying
import com.example.m5.util.PlayMusic.Companion.musicService
import com.example.m5.util.setSongPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class NowPlaying : Fragment() {

    private lateinit var viewModel: NowPlayingViewModel

    companion object {
        @Suppress("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        requireContext().theme.applyStyle(MainActivity.currentTheme[MainActivity.themeIndex], true)
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)

        viewModel = ViewModelProvider(requireActivity())[NowPlayingViewModel::class.java]

        PlayMusic.songData.observe(this.viewLifecycleOwner) {
            Glide.with(this)
                .load(it?.imageUrl)
                .apply(RequestOptions().placeholder(R.drawable.yqhy).centerCrop())
                .into(binding.songImgNP)
            binding.songNameNP.text = it?.name
            binding.artistNP.text = it?.artists?.get(0)?.name
        }

        isPlaying.observe(this.viewLifecycleOwner) {
            if (it) {
                binding.playPauseBtnNP.setImageResource(R.drawable.pause_icon)
            } else {
                binding.playPauseBtnNP.setImageResource(R.drawable.play_frag)
            }
        }

        /* MusicApplication.musicController.observe(this.viewLifecycleOwner) { nullableController ->
             val itx = this
             nullableController?.apply {
                 getPlayingSongData().observe(itx.viewLifecycleOwner) { songData ->
                     songData?.let {
                         binding.songNameNP.text = it.name
                         binding.artistNP.text = it.artists?.get(0)?.name
                         Glide.with(itx.requireContext())
                             .load(it?.imageUrl)
                             .apply(RequestOptions().placeholder(R.drawable.moni2).centerCrop())
                             .into(binding.songImgNP)
                     }
                 }

                 *//*isPlaying().observe(this@BaseActivity) {
                    if (it) {
                        mini.ivStartOrPause.contentDescription = getString(R.string.pause_music)
                    } else {
                        mini.ivStartOrPause.contentDescription = getString(R.string.play_music)
                    }
                    mini.ivStartOrPause.setImageResource(getPlayStateSourceId(it))
                }
                getPlayerCover().observe(this@BaseActivity) { bitmap ->
                    mini.ivCover.load(bitmap) {
                        size(ViewSizeResolver(mini.ivCover))
                        error(R.drawable.ic_song_cover)
                    }
                }*//*
            }
        }*/

        binding.playPauseBtnNP.setOnClickListener {
            if (musicService != null) {
                if (isPlaying.value!!) pauseMusic()
                else playMusic()
            }
        }
        binding.nextBtnNP.setOnClickListener {

            GlobalScope.launch(Dispatchers.Main) {
                if (musicService != null) {
                    setSongPosition(increment = true)
                    PlayMusic().createMediaPlayer(musicListPA[songPosition])
                    playMusic()
                }
            }
        }

        binding.preBtnNP.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                if (musicService != null) {
                    setSongPosition(increment = false)
                    PlayMusic().createMediaPlayer(musicListPA[songPosition])
                    playMusic()
                }
            }
        }

        binding.root.setOnClickListener {
            if (musicService != null) {
                val intent =
                    Intent(
                        requireContext(),
                        PlayerActivity::class.java
                    ).setAction("your.custom.action")
                ContextCompat.startActivity(requireContext(), intent, null)
            }
        }
        return view
    }

    private fun playMusic() {
        musicService!!.mediaPlayer!!.start()
        isPlaying.value = true
    }

    private fun pauseMusic() {
        musicService!!.mediaPlayer!!.pause()
        isPlaying.value = false
    }
}
