package com.example.m5.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.m5.activity.MainActivity
import com.example.m5.data.SOURCE_LOCAL
import com.example.m5.data.StandardSongData
import java.io.File

/**
 * 本地音乐
 */
object LocalMusic {
     fun getAllAudioX(context: Context): ArrayList<StandardSongData> {
        val tempList = ArrayList<StandardSongData>()
        //这是一个筛选条件，表示只查询音乐文件
        val selection =
            MediaStore.Audio.Media.IS_MUSIC + "!=0 AND " + MediaStore.Audio.Media.DURATION + ">20000"

        // 创建一个包含需要查询的媒体库音乐信息的投影数组
        val projection = arrayOf(
            MediaStore.Audio.Media._ID, // 音乐文件ID
            MediaStore.Audio.Media.TITLE, // 音乐标题
            MediaStore.Audio.Media.ALBUM, // 音乐专辑
            MediaStore.Audio.Media.ARTIST, // 音乐艺术家
            MediaStore.Audio.Media.DURATION, // 音乐时长
            MediaStore.Audio.Media.DATE_ADDED, // 音乐添加日期
            MediaStore.Audio.Media.DATA, // 音乐文件路径
            MediaStore.Audio.Media.ALBUM_ID // 音乐文件路径
        )
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
            selection, null, MainActivity.sortingList[MainActivity.sortOrder], null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val id =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                    val title =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                    val album =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                    val artist =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                    val duration =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                    val path =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                    val albumIdC =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    //通过album_id找到专辑封面图片uri
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()

                    val artistList = ArrayList<StandardSongData.StandardArtistData>()
                    artistList.add(
                        StandardSongData.StandardArtistData(
                            null,
                            artist
                        )
                    )

                    val song = StandardSongData(
                        SOURCE_LOCAL,
                        id = id,
                        name = title,
                        imageUrl = artUriC,
                        artistList,
                        null,
                        StandardSongData.LocalInfo(duration = duration, path = path),
                        null
                    )
                    val file = File(path)
                    if (file.exists()) {
                        tempList.add(song)
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }
        }
        return tempList
    }



}