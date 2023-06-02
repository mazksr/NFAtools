package org.MyTune;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;


public class Main extends Application {
    private MusicPlayer musicPlayer = new MusicPlayer();
    private Stage mainStage;
    private TableView<Music> librarySongs;
    private TableView<Music> playlistSongs;
    private TableView<Music> libraryToPlaylist;
    private Music selectedMusic;
    MusicDatabaseDummy musicDB = new MusicDatabaseDummy();
    Label nowPlaying = new Label();
    Label playingFrom = new Label("Playing from: ");
    Slider progressSlider = new Slider();
    Playlist selectedPlaylist;
    Button next = new Button("Next");
    Button prev = new Button("Previous");

    public static void main(String[] args) {
        launch(args);
    }

    public void playListChoiceBox() {
        VBox playlistBox = new VBox();
        ChoiceBox<String> playlistChoiceBox = new ChoiceBox<>();
        playlistBox.setAlignment(Pos.CENTER);
        playlistBox.setSpacing(10);

        for (int i = 0; i < musicDB.getPlaylist().size(); i++) {
            playlistChoiceBox.getItems().add(musicDB.getPlaylist().get(i).getPlaylistName());
        }
        playlistChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int selectedIndex = playlistChoiceBox.getItems().indexOf(newValue);
                selectedPlaylist = musicDB.getPlaylist().get(selectedIndex);
                playlistMenu();
            }
        });

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(playlistBox);

        Scene scene = new Scene(borderPane, 1000, 1000);

        Button back = new Button("<- Back");
        back.setOnAction(event -> mainMenu());
        borderPane.setBottom(back);

        Button addPlaylist = new Button("Add...");
        addPlaylist.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Playlist");
            dialog.setHeaderText("Enter the playlist name:");
            dialog.setContentText("Playlist Name:");
            dialog.setGraphic(null);

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(playlistName -> {
                Playlist obj = new Playlist(playlistName);
                musicDB.addPlaylist(obj);
                playlistChoiceBox.getItems().add(playlistName);
            });
        });
        playlistBox.getChildren().addAll(new Label("Select Playlist:"), playlistChoiceBox, addPlaylist);

        //agar vbox di tengah
        playlistBox.setPadding(new Insets(10));
        VBox.setVgrow(playlistBox, Priority.ALWAYS);

        mainStage.setScene(scene);
        mainStage.show();
    }

    public void playlistMenu() {
        BorderPane borderPane = new BorderPane();
        HBox mediaButtons = new HBox();
        VBox rightSide = new VBox();
        Scene scene = new Scene(borderPane, 1000, 1000);

        try {
            progressSlider.setMax(musicPlayer.getTotalPlayingTime());
        } catch (NullPointerException e) {
            progressSlider.setMax(100);
        }
        progressSlider.setMin(0);
        progressSlider.setValue(0);
        progressSlider.setPrefWidth(300);

        Button back = new Button("<- Back");
        back.setOnAction(event -> playListChoiceBox());

        Button playPause = new Button("Play / Pause");
        playPause.setOnAction(e -> {
            musicPlayer.playPause();
        });

        Button add = new Button("Add");
        add.setOnAction(e -> {
            addToPlaylist();
        });
        Button delete = new Button("Delete");

        //tambah music ke TableView
        playlistSongs.getColumns().clear();//clear agar tidak double
        TableColumn<Music, String> column1 = new TableColumn<>("Title");
        TableColumn<Music, String> column2 = new TableColumn<>("Artist");
        TableColumn<Music, String> column3 = new TableColumn<>("Path");
        column1.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        column2.setCellValueFactory(cellData -> cellData.getValue().artistNameProperty());
        column3.setCellValueFactory(cellData -> cellData.getValue().songPathProperty());

        playlistSongs.getColumns().addAll(column1, column2, column3);
        playlistSongs.setItems(selectedPlaylist.getPlaylistContent());

        mediaButtons.getChildren().addAll(nowPlaying, prev, playPause, next, progressSlider, playingFrom);

        rightSide.getChildren().addAll(add, delete);

        add.prefWidthProperty().bind(delete.widthProperty());
        mediaButtons.setSpacing(8);
        rightSide.setSpacing(8);
        mediaButtons.setPadding(new Insets(15, 0, 15, 0));
        rightSide.setPadding(new Insets(0, 8, 0, 8));

        mediaButtons.setAlignment(Pos.CENTER);
        rightSide.setAlignment(Pos.TOP_CENTER);

        borderPane.setTop(back);
        borderPane.setCenter(playlistSongs);
        borderPane.setBottom(mediaButtons);
        borderPane.setRight(rightSide);

        mainStage.setScene(scene);
        //mainStage.setResizable(false);
        mainStage.show();

        //saat musik yang ditambahkan diklik 2x
        playlistSongs.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                if (selectedMusic == playlistSongs.getSelectionModel().getSelectedItem()) {
                    return;
                } else {
                    selectedMusic = playlistSongs.getSelectionModel().getSelectedItem();
                }

                nowPlaying.setText("Now Playing: " + selectedMusic.getTitle() + " - " + selectedMusic.getArtistName());
                playingFrom.setText("Playing from: " + selectedPlaylist.getPlaylistName());

                nextPrevAction();

                //agar musik tidak terplay secara bersamaan
                if (musicPlayer.isPlaying()) {
                    musicPlayer.terminate();
                    musicPlayer.setPath(selectedMusic.getSongPath());
                    musicPlayer.play();
                } else {
                    musicPlayer.setPath(selectedMusic.getSongPath());
                }
                syncSlider();
            }
        });

        //Ubah value slider saat klik kiri mouse terdeteksi
        try {
            progressSlider.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    double mouseX = event.getX();
                    double width = progressSlider.getWidth();
                    double value = (mouseX / width) * progressSlider.getMax();
                    progressSlider.setValue(value);
                    musicPlayer.seekPlaying(Duration.seconds(value));
                }
            });
        } catch (Exception e) {
            e = null;
        }

        //Ubah value slider saat di-drag menggunakan listener
        try {
            progressSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (progressSlider.isValueChanging()) {
                    // Handle seek when the slider value is changed
                    musicPlayer.seekPlaying(Duration.seconds(newValue.doubleValue()));
                }
            });
        } catch (Exception e) {
            e = null;
        }
    }

    public void addToPlaylist() {
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 1000, 1000);
        Button back = new Button("<- Back");
        back.setOnAction(e -> {
            playlistMenu();
        });

        // Tambah music ke TableView
        libraryToPlaylist.getColumns().clear(); // Clear agar tidak double
        TableColumn<Music, String> column1 = new TableColumn<>("Title");
        TableColumn<Music, String> column2 = new TableColumn<>("Artist");
        TableColumn<Music, String> column3 = new TableColumn<>("Path");
        column1.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        column2.setCellValueFactory(cellData -> cellData.getValue().artistNameProperty());
        column3.setCellValueFactory(cellData -> cellData.getValue().songPathProperty());

        libraryToPlaylist.getColumns().addAll(column1, column2, column3);
        libraryToPlaylist.setItems(musicDB.getMusicList());

        borderPane.setTop(back);
        borderPane.setCenter(libraryToPlaylist);

        mainStage.setScene(scene);
        mainStage.show();


        // Saat musik yang ditambahkan diklik 2x
        libraryToPlaylist.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Music x = libraryToPlaylist.getSelectionModel().getSelectedItem();
                if (!selectedPlaylist.isAlreadyIn(x)) {
                    selectedPlaylist.addMusicToPlaylist(x);
                    playlistMenu();
                } else {
                    playlistMenu();
                }
            }
        });
    }


    private void mainMenu() {
        //Layout container
        Group root = new Group();
        VBox vBox = new VBox();
        Scene scene = new Scene(root, 1000, 1000);

        //Texts and buttons
        Text welcome = new Text("Welcome to MyTune");
        welcome.setFont(Font.font("Arial", 50));
        Button viewLibrary = new Button("View Library");
        viewLibrary.setOnAction(event -> musicLibrary());
        Button viewPlaylist = new Button("View Playlist");
        viewPlaylist.setOnAction(e -> playListChoiceBox());
        Button exit = new Button("Exit");
        exit.setOnAction(e -> System.exit(0));
        exit.prefWidthProperty().bind(viewPlaylist.widthProperty());

        //Tambah ke box
        vBox.getChildren().addAll(welcome, viewLibrary, viewPlaylist, exit);

        //set mainStage icon and title
        Image icon = new Image("file:src/main/resources/music.png");
        mainStage.getIcons().add(icon);
        mainStage.setTitle("MyTune");

        vBox.setSpacing(20);
        vBox.setAlignment(Pos.CENTER);

        root.getChildren().add(vBox);

        // Agar vBox di tengah
        root.layoutXProperty().bind(scene.widthProperty().subtract(vBox.widthProperty()).divide(2));
        root.layoutYProperty().bind(scene.heightProperty().subtract(vBox.heightProperty()).divide(2));

        mainStage.setScene(scene);
        //mainStage.setResizable(false);
        mainStage.show();

    }

    public void musicLibrary() {
        BorderPane borderPane = new BorderPane();
        HBox mediaButtons = new HBox();
        VBox rightSide = new VBox();
        Scene scene = new Scene(borderPane, 1000, 1000);

        try {
            progressSlider.setMax(musicPlayer.getTotalPlayingTime());
        } catch (NullPointerException e) {
            progressSlider.setMax(100);
        }
        progressSlider.setMin(0);
        progressSlider.setValue(0);
        progressSlider.setPrefWidth(300);

        Button back = new Button("<- Back");
        back.setOnAction(event -> mainMenu());

        Button playPause = new Button("Play / Pause");
        playPause.setOnAction(e -> {
            musicPlayer.playPause();
        });

        Button add = new Button("Add");
        add.setOnAction(e -> showAddMusicDialog());
        Button delete = new Button("Delete");

        //tambah music ke TableView
        librarySongs.getColumns().clear();//clear agar tidak double
        TableColumn<Music, String> column1 = new TableColumn<>("Title");
        TableColumn<Music, String> column2 = new TableColumn<>("Artist");
        TableColumn<Music, String> column3 = new TableColumn<>("Path");
        column1.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        column2.setCellValueFactory(cellData -> cellData.getValue().artistNameProperty());
        column3.setCellValueFactory(cellData -> cellData.getValue().songPathProperty());

        librarySongs.getColumns().addAll(column1, column2, column3);
        librarySongs.setItems(musicDB.getMusicList());

        mediaButtons.getChildren().addAll(nowPlaying, prev, playPause, next, progressSlider, playingFrom);

        rightSide.getChildren().addAll(add, delete);

        add.prefWidthProperty().bind(delete.widthProperty());
        mediaButtons.setSpacing(8);
        rightSide.setSpacing(8);
        mediaButtons.setPadding(new Insets(15, 0, 15, 0));
        rightSide.setPadding(new Insets(0, 8, 0, 8));

        mediaButtons.setAlignment(Pos.CENTER);
        rightSide.setAlignment(Pos.TOP_CENTER);

        borderPane.setTop(back);
        borderPane.setCenter(librarySongs);
        borderPane.setBottom(mediaButtons);
        borderPane.setRight(rightSide);

        mainStage.setScene(scene);
        //mainStage.setResizable(false);
        mainStage.show();


        //saat musik yang ditambahkan diklik 2x
        librarySongs.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                if (selectedMusic == librarySongs.getSelectionModel().getSelectedItem()) {
                    return;
                } else {
                    selectedMusic = librarySongs.getSelectionModel().getSelectedItem();
                }

                nowPlaying.setText("Now Playing: " + selectedMusic.getTitle() + " - " + selectedMusic.getArtistName());
                playingFrom.setText("Playing from: Music Library");

                delete.setOnAction(e -> {
                    musicDB.removeMusic(selectedMusic);
                    musicPlayer.terminate();
                    musicPlayer.setPath("");
                    librarySongs.setItems(musicDB.getMusicList()); //refresh tableview
                });

                nextPrevAction();

                //agar musik tidak terplay secara bersamaan
                if (musicPlayer.isPlaying()) {
                    musicPlayer.terminate();
                    musicPlayer.setPath(selectedMusic.getSongPath());
                    musicPlayer.play();
                } else {
                    musicPlayer.setPath(selectedMusic.getSongPath());
                }
                syncSlider();
            }
        });

        //Ubah value slider saat klik kiri mouse terdeteksi
        try {
            progressSlider.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    double mouseX = event.getX();
                    double width = progressSlider.getWidth();
                    double value = (mouseX / width) * progressSlider.getMax();
                    progressSlider.setValue(value);
                    musicPlayer.seekPlaying(Duration.seconds(value));
                }
            });
        } catch (Exception e) {
            e = null;
        }

        //Ubah value slider saat di-drag menggunakan listener
        try {
            progressSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (progressSlider.isValueChanging()) {
                    // Handle seek when the slider value is changed
                    musicPlayer.seekPlaying(Duration.seconds(newValue.doubleValue()));
                }
            });
        } catch (Exception e) {
            e = null;
        }
    }

    private void syncSlider() {
        musicPlayer.mediaPlayer.setOnReady(() -> {
            try {
                progressSlider.setMax(musicPlayer.getTotalPlayingTime());
            } catch (NullPointerException e) {
                progressSlider.setMax(100);
            }
        });

        //slider mengikuti progress musik
        musicPlayer.getCurrentPlayingTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                progressSlider.setValue(newValue.toSeconds());
            }
        });
    }

    public void nextPrevAction() {
        if (playingFrom.getText().equals("Playing from: Music Library")) {
            next.setOnAction(e -> {
                if (musicDB.getIndex(selectedMusic) + 1 >= musicDB.getSize()) {
                    return;
                }
                musicPlayer.terminate();
                musicPlayer.setPath(musicDB.getMusicList().get(musicDB.getIndex(selectedMusic) + 1).getSongPath());
                selectedMusic = musicDB.getMusicList().get(musicDB.getIndex(selectedMusic) + 1);
                nowPlaying.setText("Now Playing: " + selectedMusic.getTitle() + " - " + selectedMusic.getArtistName()); //refresh
                syncSlider();
                musicPlayer.play();

                // Move the selection mark in the TableView
                librarySongs.getSelectionModel().select(selectedMusic);
                librarySongs.scrollTo(selectedMusic);
            });

            prev.setOnAction(e -> {
                if (musicDB.getIndex(selectedMusic) - 1 < 0) {
                    return;
                }
                musicPlayer.terminate();
                musicPlayer.setPath(musicDB.getMusicList().get(musicDB.getIndex(selectedMusic) + -1).getSongPath());
                selectedMusic = musicDB.getMusicList().get(musicDB.getIndex(selectedMusic) - 1);
                nowPlaying.setText("Now Playing: " + selectedMusic.getTitle() + " - " + selectedMusic.getArtistName()); //refresh
                syncSlider();
                musicPlayer.play();

                // Move the selection mark in the TableView
                librarySongs.getSelectionModel().select(selectedMusic);
                librarySongs.scrollTo(selectedMusic);
            });
        } else if (playingFrom.getText().equals("Playing from: " + selectedPlaylist.getPlaylistName())) {
            next.setOnAction(e -> {
                if (selectedPlaylist.getPlaylistContent().indexOf(selectedMusic) + 1 >= selectedPlaylist.getPlaylistContent().size()) {
                    return;
                }
                musicPlayer.terminate();
                musicPlayer.setPath(selectedPlaylist.getPlaylistContent().get(selectedPlaylist.getPlaylistContent().indexOf(selectedMusic) + 1).getSongPath());
                selectedMusic = selectedPlaylist.getPlaylistContent().get(selectedPlaylist.getPlaylistContent().indexOf(selectedMusic) + 1);
                nowPlaying.setText("Now Playing: " + selectedMusic.getTitle() + " - " + selectedMusic.getArtistName()); //refresh
                syncSlider();
                musicPlayer.play();

                // Move the selection mark in the TableView
                playlistSongs.getSelectionModel().select(selectedMusic);
                playlistSongs.scrollTo(selectedMusic);
            });

            prev.setOnAction(e -> {
                if (selectedPlaylist.getPlaylistContent().indexOf(selectedMusic) - 1 < 0) {
                    return;
                }
                musicPlayer.terminate();
                musicPlayer.setPath(selectedPlaylist.getPlaylistContent().get(selectedPlaylist.getPlaylistContent().indexOf(selectedMusic) - 1).getSongPath());
                selectedMusic = selectedPlaylist.getPlaylistContent().get(selectedPlaylist.getPlaylistContent().indexOf(selectedMusic) - 1);
                nowPlaying.setText("Now Playing: " + selectedMusic.getTitle() + " - " + selectedMusic.getArtistName()); //refresh
                syncSlider();
                musicPlayer.play();

                // Move the selection mark in the TableView
                playlistSongs.getSelectionModel().select(selectedMusic);
                playlistSongs.scrollTo(selectedMusic);
            });
        }
    }

    private void showAddMusicDialog() {
        // Pop up window saat klik add
        Dialog<Music> dialog = new Dialog<>();
        dialog.setTitle("Add Music");
        dialog.setHeaderText("Enter the details for the new music");

        // Textfield untuk input atribut class music
        TextField titleField = new TextField();
        TextField artistField = new TextField();

        // File chooser untuk memilih path
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Music File");

        // Textfield untuk menampilkan path
        TextField pathField = new TextField();
        pathField.setEditable(false);

        // Tombol browse untuk memilih file
        Button browseButton = new Button("Browse");
        browseButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(dialog.getOwner());
            if (selectedFile != null) {
                String path = selectedFile.getAbsolutePath().replace("\\", "/");
                pathField.setText(path);
            }
        });

        // Label Masing-masing Textfield
        VBox content = new VBox(
                new Label("Title:"), titleField,
                new Label("Artist:"), artistField,
                new Label("Song Path:"), new HBox(pathField, browseButton)
        );
        dialog.getDialogPane().setContent(content);

        // Tombol Ok dan Cancel
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Membuat objek saat klik ok
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String title = titleField.getText();
                String artist = artistField.getText();
                String path = pathField.getText();
                return new Music(artist, title, path);
            }
            return null;
        });

        // Tambahkan
        dialog.showAndWait().ifPresent(newMusic -> {
            musicDB.addToMusicList(newMusic);
            librarySongs.setItems(musicDB.getMusicList()); //refresh tableview
        });

    }

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;
        librarySongs = new TableView<>();
        playlistSongs = new TableView<>();
        libraryToPlaylist = new TableView<>();

        mainStage.setOnCloseRequest(this::handleWindowClose);

        mainMenu();
    }

    private void handleWindowClose(WindowEvent event) {
        // Close the application
        Platform.exit();
    }
}