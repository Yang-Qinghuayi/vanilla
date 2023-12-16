package com.example.m5.logic.model

import com.example.m5.data.SOURCE_NETEASE
import com.example.m5.data.StandardSongData

data class RecommendSongsResponse(val code: String, val data: RecommendData)

data class RecommendData(val dailySongs: List<DailySong>)

data class DailySong(val name:String, val id: String, val ar: List<ArRecommend>, val al: AlRecommend, val reason: String)
{
    fun switchToStandard(): StandardSongData {
        return StandardSongData(
            SOURCE_NETEASE, id, name,
            al.getImageUrl(), getArtList(),null, null, null)
    }
   /* private fun getNeteaseInfo(): StandardSongData.NeteaseInfo {
        return StandardSongData.NeteaseInfo(null, privilege?.pl, 0, privilege?.maxbr)
    }*/

    private fun getArtList():ArrayList<StandardSongData.StandardArtistData> {
        val list = ArrayList<StandardSongData.StandardArtistData>()
        for (art in ar) {
            list.add(art.switchToStandard())
        }
        return list
    }

}

data class ArRecommend(val id: Long, val name: String)
{
    fun switchToStandard():StandardSongData.StandardArtistData{
        return StandardSongData.StandardArtistData(id, name)
    }
}

data class AlRecommend(val id: Long, val name: String, val picUrl: String)
{
    fun getImageUrl():String {
        return picUrl
    }
}