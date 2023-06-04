package org.MyTune;
import com.google.gson.Gson;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;

public class Music {
    private StringProperty artistName;
    private StringProperty title;
    private StringProperty songPath;

    public Music(String artistName, String title, String songPath) {
        this.artistName = new SimpleStringProperty(artistName);
        this.title = new SimpleStringProperty(title);
        this.songPath = new SimpleStringProperty(songPath);
    }

    public Music(String artistName, String songPath) {
        File file = new File(songPath);

        this.artistName = new SimpleStringProperty(artistName);
        this.title = new SimpleStringProperty(file.getName());
        this.songPath = new SimpleStringProperty(songPath);
    }

    public String getArtistName() {
        return artistName.get();
    }

    public StringProperty artistNameProperty() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName.set(artistName);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getSongPath() {
        return songPath.get();
    }

    public StringProperty songPathProperty() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath.set(songPath);
    }

    //////from and to json//////
    public static Music fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Music.class);
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}