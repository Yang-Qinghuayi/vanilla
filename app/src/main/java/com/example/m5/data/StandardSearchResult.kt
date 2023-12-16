package com.example.m5.data

data class StandardSearchResult(
    val songs:List<StandardSongData>,
    val playlist:List<StandardPlaylist>,
    val albums:List<StandardAlbum>,
    val singers:List<StandardSinger>
)
