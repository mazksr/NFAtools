package org.MyTune;

import java.io.File;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
public class MusicPlayer extends MediaPlayerBlueprint {
    public MusicPlayer() {
        super();
    }

    public void playPause() {
        try {
            if (!isPlaying()) {
                mediaPlayer.play();
                setPlaying(true);
            } else {
                mediaPlayer.pause();
                setPlaying(false);
            }
        } catch (Exception e) {
            e = null;
        }
    }

    public Duration getCurrentPlayingTime() {
        try {
            return mediaPlayer.getCurrentTime();
        } catch (Exception e) {
            return Duration.seconds(0);
        }
    }

    public void seekPlaying(Duration duration) {
        try {
            mediaPlayer.seek(duration);
        } catch (Exception e) {
            e = null;
        }
    }

    public double getTotalPlayingTime() {
        try {
            return mediaPlayer.getTotalDuration().toSeconds();
        } catch (Exception e) {
            return 0;
        }

    }

    public ReadOnlyObjectProperty<Duration> getCurrentPlayingTimeProperty() {
        try {
            return mediaPlayer.currentTimeProperty();
        } catch (Exception e) {
            return new ReadOnlyObjectWrapper<>(Duration.ZERO).getReadOnlyProperty();
        }
    }

    public void terminate() {
        mediaPlayer.stop();
    }

    public void play() {
        mediaPlayer.play();
    }

    public void setPath(String filePath) {
        super.setPath(filePath);
    }
}
