package com.example.m5.logic.model


data class MainNcResponse(
    val hotMusics: List<HotMusic>,
    val playLists: List<PlayList>,
    val newMusicesAls: List<MusicAl>
)

data class HighQualityResponse(val playlists: List<PlayList>, val code: String)

data class PlayList(
    val name: String,
    val id: Long,
    val coverImgUrl: String,
    val description: String
)

data class HotMusicListResponse(val code: String, val message: String, val data: List<HotMusic>)

data class HotMusic(val searchWord: String, val content: String, val iconUrl: String)

data class NewMusicesAls(val data: List<MusicAl>, val code: String)
data class MusicAl(val artists: List<Artist>)

data class Artist(val name: String, val picUrl: String)
