package org.MyTune;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public abstract class MediaPlayerBlueprint {
    private boolean isPlaying = false;
    private String path;
    private Media hit;
    MediaPlayer mediaPlayer;

    public void setPath(String path) {
        try {
            this.path = path;
            hit = new Media(new File(path).toURI().toString());
            mediaPlayer = new MediaPlayer(hit);
        } catch (Exception e) {
            e = null;
        }
    }

    public String getPath() {
        if (path == null) {
            return "none";
        }
        return path;
    }

    public MediaPlayerBlueprint() {
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public abstract void playPause();
    public abstract void play();
    public abstract void terminate();
    public abstract void seekPlaying(Duration duration);
    public abstract double getTotalPlayingTime();
    public abstract Duration getCurrentPlayingTime();
}

