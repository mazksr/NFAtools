package org.MyTune;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;

public class MusicDatabaseDummy {
    private ObservableList<Music> musicList = FXCollections.observableArrayList();

    public Music getMusicObject(int index) {
        return musicList.get(index);
    }
    public void addToMusicList(Music music) {
        musicList.add(music);
    }

    public ObservableList<Music> getMusicList() {
        return musicList;
    }

    public void removeMusic(Music music) {
        musicList.remove(music);
    }

    public int getIndex(Music music) {
        return musicList.indexOf(music);
    }

    public int getSize() {
        return musicList.size();
    }


    ////////////////Playlist/////////////////////////
    private ObservableList<Playlist> playlist = FXCollections.observableArrayList();

    public int getIndex(Playlist p) {
        return playlist.indexOf(p);
    }

    public void addPlaylist(Playlist x) {
        playlist.add(x);
    }

    public void removePlaylist(Playlist pl) {
        playlist.remove(pl);
    }

    public ObservableList<Playlist> getPlaylist() {
        return playlist;
    }
}

