package com.example.m5.logic.model

import com.example.m5.data.SOURCE_NETEASE
import com.example.m5.data.StandardSongData
import com.example.m5.util.getNeteasePicUrl


data class MusicSearchResponse(val result: Result, val code: String)


data class Result(val searchQcReminder: String, val songs: List<Song>, val songCount: Int)

data class Song(val al: Al?,
                val ar: List<Ar>,
                val copyright: Long,
                val fee: Int,
                val id: String,
                val name: String,
                val privilege: Privilege?,)
{
    fun switchToStandard(): StandardSongData {
        return StandardSongData(SOURCE_NETEASE, id, name,
            al?.getImageUrl(), getArtList(),getNeteaseInfo(), null, null)
    }
    private fun getNeteaseInfo(): StandardSongData.NeteaseInfo {
        return StandardSongData.NeteaseInfo(fee, privilege?.pl, 0, privilege?.maxbr)
    }

    private fun getArtList():ArrayList<StandardSongData.StandardArtistData> {
        val list = ArrayList<StandardSongData.StandardArtistData>()
        for (art in ar) {
            list.add(art.switchToStandard())
        }
        return list
    }
}
data class Ar(val id: Long, val name: String)
{
    fun switchToStandard():StandardSongData.StandardArtistData{
        return StandardSongData.StandardArtistData(id, name)
    }
}

data class Al(val id: Long, val name: String, val picUrl: String){
    fun getImageUrl():String {
        return picUrl
    }
}

data class Privilege(
    val maxbr: Int,
    val pl: Int
)

data class SongResponse(val data: List<Data>, val code: String)

data class Data(val id: Long, val url: String, val fee: Int, val time: Long)
