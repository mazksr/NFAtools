package org.MyTune;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;

public class Playlist {
    private ObservableList<Music> playlistContent = FXCollections.observableArrayList();

    private String playlistName;

    public Playlist(String playlistName) {
        this.playlistName = playlistName;
    }

    public ObservableList<Music> getPlaylistContent() {
        return playlistContent;
    }

    public boolean isAlreadyIn(Music music) {
        return playlistContent.contains(music);
    }

    public void removeFromPlaylist(Music msc) {
        playlistContent.remove(msc);
    }

    public void addMusicToPlaylist(Music music) {
        playlistContent.add(music);
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }


    /////from and to json//////
    public static Music fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Music.class);
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
