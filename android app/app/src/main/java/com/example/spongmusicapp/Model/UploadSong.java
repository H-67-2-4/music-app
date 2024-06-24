package com.example.spongmusicapp.Model;

public class UploadSong {

    public String songsCategory,songTitle, artist, album_art, songDuration, songlink, mkey;

    public UploadSong(String songsCategory, String songTitle,String artist,String album_art,String songDuration,String songlink){


        if (songTitle.trim().equals("")){
            songTitle = "No title";
        }



        this.songsCategory =songsCategory;
        this.songTitle = songTitle;
        this.artist = artist;
        this.album_art = album_art;
        this.songDuration =songDuration;
        this.songlink =songlink;


    }

    public UploadSong() {
    }

    public void setSongsCategory(String songsCategory) {
        this.songsCategory = songsCategory;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum_art() {
        return album_art;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getSonglink() {
        return songlink;
    }

    public void setSonglink(String songlink) {
        this.songlink = songlink;
    }

    public String getmkey() {
        return mkey;
    }

    public void setmkey(String mkey) {
        this.mkey = mkey;
    }
}
