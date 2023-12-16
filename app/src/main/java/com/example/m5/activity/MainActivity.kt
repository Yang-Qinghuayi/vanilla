package com.example.m5.activity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.m5.R
import com.example.m5.adapter.MusicAdapterX
import com.example.m5.data.StandardSongData
import com.example.m5.data.musicListPA
import com.example.m5.data.songPosition
import com.example.m5.databinding.ActivityMainBinding
import com.example.m5.frag.NowPlaying
import com.example.m5.service.MusicService
import com.example.m5.ui.activity.NetEaseMainActivity
import com.example.m5.util.LocalMusic.getAllAudioX
import com.example.m5.util.Music
import com.example.m5.util.MusicPlaylist
import com.example.m5.util.PlayMusic
import com.example.m5.util.PlayMusic.Companion.isPlaying
import com.example.m5.util.PlayMusic.Companion.musicService
import com.example.m5.util.exitApplication
import com.example.m5.util.setStatusBarTextColor
import com.example.m5.util.transparentStatusBar
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class MainActivity : AppCompatActivity(), ServiceConnection {

    private lateinit var binding: ActivityMainBinding
    private lateinit var musicAdapterX: MusicAdapterX


    companion object {
        lateinit var MusicListMAX: ArrayList<StandardSongData>
        lateinit var musicListSearch: ArrayList<StandardSongData>
        var search: Boolean = false
        var themeIndex: Int = 2
        val currentTheme = arrayOf(
            R.style.coolGreen,
            R.style.coolRed,
            R.style.coolCyan,
            R.style.coolBlue,
            R.style.coolBlack
        )
        var sortOrder: Int = 0
        val sortingList = arrayOf(
            MediaStore.Audio.Media.DATE_ADDED + " DESC", MediaStore.Audio.Media.TITLE + " DESC",
            MediaStore.Audio.Media.DURATION + " DESC"
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeEditor = getSharedPreferences("THEMES", MODE_PRIVATE)
        themeIndex = themeEditor.getInt("themeIndex", 0)
        setTheme(currentTheme[themeIndex])
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //透明状态栏以及文字颜色设定
        transparentStatusBar(window)
        setStatusBarTextColor(window, false)



        /*NowPlaying.binding.songNameNP.text = musicListPA[songPosition].name
        NowPlaying.binding.artistNP.text = musicListPA[songPosition].artists?.get(0)?.name
*/

        if (requestRuntimePermission()) {
            initializeLayout()

            //通过缓存得到音乐列表以及喜欢的音乐
            FavouriteActivity.favouriteSongs = ArrayList()
            //取得我喜欢的列表
            val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
            val jsonString = editor.getString("FavouriteSongs", null)
            val typeToken = object : TypeToken<ArrayList<StandardSongData>>() {}.type
            if (jsonString != null) {
                val data: ArrayList<StandardSongData> =
                    GsonBuilder().create().fromJson(jsonString, typeToken)
                FavouriteActivity.favouriteSongs.addAll(data)
            }

            PlaylistActivity.musicPlaylist = MusicPlaylist()
            //取得播放列表
            val jsonStringPlaylist = editor.getString("MusicPlaylist", null)
            if (jsonStringPlaylist != null) {
                val dataPlaylist: MusicPlaylist =
                    GsonBuilder().create().fromJson(jsonStringPlaylist, MusicPlaylist::class.java)
                PlaylistActivity.musicPlaylist = dataPlaylist
            }
        }


        //点击跳转网易云音乐源
        binding.NetEaseBtn.setOnClickListener {
            val intent: Intent =
                Intent(this, NetEaseMainActivity::class.java).setAction("your.custom.action")
            startActivity(intent)
        }


        //从设置按钮跳到设置界面
        binding.setBtn.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    SettingActivity::class.java
                ).setAction("your.custom.action")
            )
        }

        //从随机播放按钮跳到播放界面,并用intent带去一些远方的信息
        binding.shuffleBtn.setOnClickListener {
            musicListPA = MusicListMAX
            songPosition = 0

            if (musicService == null)
                startServicex(MusicListMAX, shuffle = false, songPosition)
            else {
                GlobalScope.launch(Dispatchers.Main) {
                    PlayMusic().createMediaPlayer(MusicListMAX[songPosition])
                }
            }
            isPlaying.value = true
        }


        //长按进行歌单的打乱(我还没有做持久化)
        //todo 进行打乱的歌单的持久化
        binding.shuffleBtn.setOnLongClickListener {
            MusicListMAX.shuffle()
            musicAdapterX.notifyDataSetChanged()
            true
        }
        //从喜欢按钮跳到喜欢的音乐列表
        binding.favouriteBtn.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    FavouriteActivity::class.java
                ).setAction("your.custom.action")
            )
        }

        //从播放列表按钮跳到播放列表
        binding.playlistBtn.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    PlaylistActivity::class.java
                ).setAction("your.custom.action")
            )
        }

    }

    //打开应用时获取权限的函数
    private fun requestRuntimePermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13
                )
                return false
            }
        }
        //android 13 permission request
        else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_AUDIO
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO),
                    13
                )
                return false
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                initializeLayout()
            } else
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13
                )
        }
    }

    //界面初始化函数
    @RequiresApi(Build.VERSION_CODES.R)
    private fun initializeLayout() {
        search = false
        //排序的偏好
        val sortEditor = getSharedPreferences("SORTING", MODE_PRIVATE)
        sortOrder = sortEditor.getInt("sortOrder", 0)

        MusicListMAX = getAllAudioX(this)
        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(13)
        binding.musicRV.layoutManager = LinearLayoutManager(this@MainActivity)
        musicAdapterX = MusicAdapterX(this@MainActivity, MusicListMAX)

        binding.musicRV.adapter = musicAdapterX
    }


    override fun onDestroy() {
        super.onDestroy()
        if (!isPlaying.value!! && musicService != null) {
            exitApplication()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        //存储我喜欢的歌
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouriteActivity.favouriteSongs)
        editor.putString("FavouriteSongs", jsonString)
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()
        val sortEditor = getSharedPreferences("SORTING", MODE_PRIVATE)
        val sortValue = sortEditor.getInt("sortOrder", 0)
        if (sortValue != sortOrder) {
            sortOrder = sortValue
            MusicListMAX = getAllAudioX(this)
            musicAdapterX.updateMusicList(MusicListMAX)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu, menu)
        val searchView = menu?.findItem(R.id.searchView)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch = ArrayList()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    for (song in MusicListMAX) {
                        if (song.name?.lowercase()?.contains(userInput) == true) {
                            musicListSearch.add(song)
                        }
                    }
                    search = true
                    //实时更新页面
//                    musicAdapterX.updateMusicList(searchList = musicListSearch)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun startServicex(
        playlist: ArrayList<StandardSongData>,
        shuffle: Boolean,
        position: Int
    ) {
        val intent = Intent(this, MusicService::class.java)
        this.bindService(intent, this, BIND_AUTO_CREATE)
        this.startService(intent)
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
                    this@MainActivity.getSystemService(AUDIO_SERVICE) as AudioManager
                musicService!!.audioManager.requestAudioFocus(
                    musicService,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
                )
            }
            PlayMusic().createMediaPlayer(musicListPA[songPosition])
//            musicService!!.seekBarSetup()
        }
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

}