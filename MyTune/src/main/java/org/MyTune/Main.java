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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
        ImageView imageView1 = new ImageView();
        String imagePath = "/images/logo.png";
        Image image1 = new Image(imagePath);
        imageView1.setImage(image1);
        imageView1.setFitWidth(500);
        imageView1.setFitHeight(500);
        imageView1.setOpacity(0.5);

        VBox playlistBox = new VBox();
        ChoiceBox<String> playlistChoiceBox = new ChoiceBox<>();
        playlistChoiceBox.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        playlistChoiceBox.setOnMouseEntered(e -> {
            playlistChoiceBox.setStyle("-fx-background-color: #9BABB8; -fx-font-weight: bold; -fx-cursor: hand;");});
        playlistChoiceBox.setOnMouseExited(e -> {
            playlistChoiceBox.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        });

        //playlistBox.setPrefSize(900, 900);
        playlistChoiceBox.setMinWidth(200);

        playlistBox.setAlignment(Pos.CENTER);
        playlistBox.setSpacing(75);

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

        StackPane stackPane = new StackPane();

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(stackPane);
        borderPane.setStyle("-fx-background-color: #C4DFDF; ");
        Scene scene = new Scene(borderPane, 900, 900);


        Button back = new Button();
        ImageView imageView = new ImageView();
        Image image = new Image("/images/back.png");
        imageView.setImage(image);
        imageView.setFitWidth(10);
        imageView.setFitHeight(10);
        back.setGraphic(imageView);
        Circle shape = new Circle(180);
        back.setShape(shape);
        back.setTextFill(Color.WHITE);
        back.setStyle("-fx-background-color: #667080; -fx-font-weight: bold;  ");
        back.setOnMouseEntered(e -> {
            back.setStyle("-fx-background-color: #9BABB8; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        back.setOnMouseExited(e -> {
            back.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        back.setOnAction(event -> mainMenu());
        borderPane.setTop(back);
        //borderPane.setBottom(back);

        Button addPlaylist = new Button();
        Circle shape1 = new Circle(180);
        addPlaylist.setShape(shape1);
        ImageView addIcon = new ImageView();
        Image icon1 = new Image("/images/add.png");
        addIcon.setImage(icon1);
        addIcon.setFitWidth(20);
        addIcon.setFitHeight(20);
        addPlaylist.setGraphic(addIcon);
        addPlaylist.setTextFill(Color.WHITE);
        addPlaylist.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; ");
        addPlaylist.setOnMouseEntered(e -> {
            addPlaylist.setStyle("-fx-background-color: #9BABB8; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        addPlaylist.setOnMouseExited(e -> {
            addPlaylist.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        addPlaylist.setOnAction(e -> showAddMusicDialog());

        Button deletePlaylist = new Button();
        Circle shape2 = new Circle(180);
        deletePlaylist.setShape(shape2);
        ImageView deleteIcon = new ImageView();
        Image icon2 = new Image("/images/delete.png");
        deleteIcon.setImage(icon2);
        deleteIcon.setFitWidth(20);
        deleteIcon.setFitHeight(20);
        deletePlaylist.setGraphic(deleteIcon);
        deletePlaylist.setTextFill(Color.WHITE);
        deletePlaylist.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; ");
        deletePlaylist.setOnMouseEntered(e -> {
            deletePlaylist.setStyle("-fx-background-color: #9BABB8; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        deletePlaylist.setOnMouseExited(e -> {
            deletePlaylist.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        deletePlaylist.setOnAction(e -> {
            // Create a ChoiceBox to select the playlist to delete
            ChoiceBox<String> deleteChoiceBox = new ChoiceBox<>();
            deleteChoiceBox.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");

            // Add playlist names to the ChoiceBox
            for (Playlist playlist : musicDB.getPlaylist()) {
                deleteChoiceBox.getItems().add(playlist.getPlaylistName());
            }

            // Create a confirmation dialog
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Delete Playlist");
            confirmationDialog.setHeaderText("Select the playlist to delete:");
            confirmationDialog.setGraphic(deleteChoiceBox);

            // Handle the user's choice
            Optional<ButtonType> result = confirmationDialog.showAndWait();
            result.ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    String selectedPlaylistName = deleteChoiceBox.getSelectionModel().getSelectedItem();
                    if (selectedPlaylistName != null) {
                        // Find the selected playlist
                        Playlist selectedPlaylist = null;
                        for (Playlist playlist : musicDB.getPlaylist()) {
                            if (playlist.getPlaylistName().equals(selectedPlaylistName)) {
                                selectedPlaylist = playlist;
                                break;
                            }
                        }
                        // Delete the playlist and remove it from the ChoiceBox
                        if (selectedPlaylist != null) {
                            musicDB.removePlaylist(selectedPlaylist);
                            playlistChoiceBox.getItems().remove(selectedPlaylistName);
                        }
                    }
                }
            });
        });

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
        Label text = new Label("Select Playlist: ");
        text.setStyle("-fx-font-weight: bold; -fx-text-fill: #667080; -fx-font-size: 20px; -fx-stroke: white; -fx-stroke-weight: 100px;");

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(playlistChoiceBox, addPlaylist);
        playlistBox.getChildren().addAll(text, hBox, deletePlaylist);
        stackPane.getChildren().addAll(imageView1, playlistBox);

        stackPane.setPadding(new Insets(10));
        VBox.setVgrow(stackPane, Priority.ALWAYS);

        mainStage.setScene(scene);
        mainStage.show();
    }

    public void playlistMenu() {
        BorderPane borderPane = new BorderPane();
        HBox mediaButtons = new HBox();
        VBox rightSide = new VBox();
        Scene scene = new Scene(borderPane, 900, 900);
        mediaButtons.setStyle("-fx-background-color: #C4DFDF; ");
        rightSide.setStyle("-fx-background-color: #C4DFDF; ");
        borderPane.setStyle("-fx-background-color: #C4DFDF; ");

        try {
            progressSlider.setMax(musicPlayer.getTotalPlayingTime());
        } catch (NullPointerException e) {
            progressSlider.setMax(100);
        }
        progressSlider.setMin(0);
        progressSlider.setValue(0);
        progressSlider.setPrefWidth(300);

        Button back = new Button();
        ImageView imageView = new ImageView();
        Image image = new Image("/images/back.png");
        imageView.setImage(image);
        imageView.setFitWidth(10);
        imageView.setFitHeight(10);
        back.setGraphic(imageView);
        Circle shape = new Circle(180);
        back.setShape(shape);
        back.setTextFill(Color.WHITE);
        back.setStyle("-fx-background-color: #667080; -fx-font-weight: bold;  ");
        back.setOnMouseEntered(e -> {
            back.setStyle("-fx-background-color: #9BABB8; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        back.setOnMouseExited(e -> {
            back.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        back.setOnAction(event -> playListChoiceBox());

        Button playPause = new Button();
        ImageView playpos = new ImageView();
        Image play = new Image("/images/play.png");
        Image pause = new Image("/images/pause.png");
        if (musicPlayer.isPlaying()) {
            playpos.setImage(pause);
        } else {
            playpos.setImage(play);
        }
        playpos.setFitWidth(20);
        playpos.setFitHeight(20);
        playPause.setGraphic(playpos);
        Circle shape1 = new Circle(180);
        playPause.setShape(shape1);
        playPause.setTextFill(Color.WHITE);
        playPause.setStyle("-fx-background-color: #667080; -fx-font-weight: bold;  -fx-cursor: hand;");
        playPause.setOnAction(e -> {
            if (musicPlayer.isPlaying()) {
                playpos.setImage(play);
            } else {
                playpos.setImage(pause);
            }
            musicPlayer.playPause();
        });

        Button add = new Button("Add");
        ImageView addIcon = new ImageView();
        Image icon1 = new Image("/images/add.png");
        addIcon.setImage(icon1);
        addIcon.setFitWidth(10);
        addIcon.setFitHeight(10);
        add.setGraphic(addIcon);
        add.setTextFill(Color.WHITE);
        add.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; ");
        add.setOnMouseEntered(e -> {
            add.setStyle("-fx-background-color: #9BABB8; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        add.setOnMouseExited(e -> {
            add.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        add.setOnAction(e -> {
            addToPlaylist();
        });
        Button delete = new Button("Delete");
        ImageView deleteIcon = new ImageView();
        Image icon2 = new Image("/images/delete.png");
        deleteIcon.setImage(icon2);
        deleteIcon.setFitWidth(20);
        deleteIcon.setFitHeight(20);
        delete.setGraphic(deleteIcon);
        delete.setTextFill(Color.WHITE);
        delete.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; ");
        delete.setOnMouseEntered(e -> {
            delete.setStyle("-fx-background-color: #9BABB8; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        delete.setOnMouseExited(e -> {
            delete.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        });


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

        Circle shapeNext = new Circle(180);
        next.setShape(shapeNext);
        ImageView nextIcon = new ImageView();
        Image logo3 = new Image("/images/next.png");
        nextIcon.setImage(logo3);
        nextIcon.setFitWidth(10);
        nextIcon.setFitHeight(10);
        next.setGraphic(nextIcon);
        next.setTextFill(Color.WHITE);
        next.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; ");
        next.setOnMouseEntered(e -> {
            next.setStyle("-fx-background-color: #9BABB8; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        next.setOnMouseExited(e -> {
            next.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        });

        Circle shapePrev = new Circle(180);
        prev.setShape(shapePrev);
        ImageView prevIcon = new ImageView();
        Image logo4 = new Image("/images/previous.png");
        prevIcon.setImage(logo4);
        prevIcon.setFitWidth(10);
        prevIcon.setFitHeight(10);
        prev.setGraphic(prevIcon);
        prev.setTextFill(Color.WHITE);
        prev.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; ");
        prev.setOnMouseEntered(e -> {
            prev.setStyle("-fx-background-color: #9BABB8; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        prev.setOnMouseExited(e -> {
            prev.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        });

        mainStage.setScene(scene);
        //mainStage.setResizable(false);
        mainStage.show();
        //mainStage.setFullScreen(true);

        //saat musik yang ditambahkan diklik 2x
        playlistSongs.setOnMouseClicked(event -> {
            if (selectedMusic != null && playingFrom.getText().equals("Playing from: Music Library")) {
                selectedMusic = null;
            }

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
        Scene scene = new Scene(borderPane, 900, 900);

        Button back = new Button();
        ImageView imageView = new ImageView();
        Image image = new Image("/images/back.png");
        imageView.setImage(image);
        imageView.setFitWidth(10);
        imageView.setFitHeight(10);
        back.setGraphic(imageView);
        Circle shape = new Circle(180);
        back.setShape(shape);
        back.setTextFill(Color.WHITE);
        back.setStyle("-fx-background-color: #667080; -fx-font-weight: bold;  ");
        back.setOnMouseEntered(e -> {
            back.setStyle("-fx-background-color: #9BABB8; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        back.setOnMouseExited(e -> {
            back.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        back.setOnAction(event -> playlistMenu());

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
        borderPane.setStyle("-fx-background-color: #C4DFDF; ");
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
        Scene scene = new Scene(root, 900, 900);

        //Texts and buttons
        vBox.setStyle("-fx-background-color: #C4DFDF; ");
        vBox.setPrefSize(2000, 1000);

        ImageView imageView = new ImageView();
        String imagePath = "/images/logo.png";
        Image image = new Image(imagePath);
        imageView.setImage(image);
        imageView.setFitWidth(500);
        imageView.setFitHeight(500);

        Button viewLibrary = new Button("View Library");
        viewLibrary.setTextFill(Color.WHITE);
        viewLibrary.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        viewLibrary.setOnMouseEntered(e -> {
            viewLibrary.setStyle("-fx-background-color: #9BABB8; -fx-font-weight: bold; -fx-cursor: hand;");});
        viewLibrary.setOnMouseExited(e -> {
            viewLibrary.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        viewLibrary.setOnAction(event -> musicLibrary());

        Button viewPlaylist = new Button("View Playlist");
        viewPlaylist.setTextFill(Color.WHITE);
        viewPlaylist.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        viewPlaylist.setOnMouseEntered(e -> {
            viewPlaylist.setStyle("-fx-background-color: #9BABB8; -fx-font-weight: bold; -fx-cursor: hand;");});
        viewPlaylist.setOnMouseExited(e -> {
            viewPlaylist.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        viewPlaylist.setOnAction(e -> playListChoiceBox());

        Button exit = new Button("Exit");
        exit.setTextFill(Color.WHITE);
        exit.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        exit.setOnMouseEntered(e -> {
            exit.setStyle("-fx-background-color: #9BABB8; -fx-font-weight: bold; -fx-cursor: hand;");});
        exit.setOnMouseExited(e -> {
            exit.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        exit.setOnAction(e -> System.exit(0));
        exit.prefWidthProperty().bind(viewPlaylist.widthProperty());

        HBox hBox = new HBox(viewLibrary, viewPlaylist);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);

        //Tambah ke box
        vBox.getChildren().addAll(imageView, hBox, exit);

        //set mainStage icon and title
        Image icon = new Image("file:src/main/resources/logo.png");
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
        //mainStage.setFullScreen(true);
        mainStage.show();

    }

    public void musicLibrary() {
        BorderPane borderPane = new BorderPane();
        HBox mediaButtons = new HBox();
        VBox rightSide = new VBox();
        Scene scene = new Scene(borderPane, 900, 900);

        try {
            progressSlider.setMax(musicPlayer.getTotalPlayingTime());
        } catch (NullPointerException e) {
            progressSlider.setMax(100);
        }
        progressSlider.setMin(0);
        progressSlider.setValue(0);
        progressSlider.setPrefWidth(300);

        rightSide.setStyle("-fx-background-color: #C4DFDF; ");
        borderPane.setStyle("-fx-background-color: #C4DFDF; ");

        Button back = new Button();
        ImageView imageView = new ImageView();
        Image image = new Image("/images/back.png");
        imageView.setImage(image);
        imageView.setFitWidth(10);
        imageView.setFitHeight(10);
        back.setGraphic(imageView);
        Circle shape = new Circle(180);
        back.setShape(shape);
        back.setTextFill(Color.WHITE);
        back.setStyle("-fx-background-color: #667080; -fx-font-weight: bold;  ");
        back.setOnMouseEntered(e -> {
            back.setStyle("-fx-background-color: #9BABB8; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        back.setOnMouseExited(e -> {
            back.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        back.setOnAction(event -> mainMenu());

        Button playPause = new Button();
        ImageView playpos = new ImageView();
        Image play = new Image("/images/play.png");
        Image pause = new Image("/images/pause.png");
        if (musicPlayer.isPlaying()) {
            playpos.setImage(pause);
        } else {
            playpos.setImage(play);
        }
        playpos.setFitWidth(20);
        playpos.setFitHeight(20);
        playPause.setGraphic(playpos);
        Circle shape1 = new Circle(180);
        playPause.setShape(shape1);
        playPause.setTextFill(Color.WHITE);
        playPause.setStyle("-fx-background-color: #667080; -fx-font-weight: bold;  -fx-cursor: hand;");
        playPause.setOnAction(e -> {
            if (musicPlayer.isPlaying()) {
                playpos.setImage(play);
            } else {
                playpos.setImage(pause);
            }
            musicPlayer.playPause();
        });

        Circle shapeNext = new Circle(180);
        next.setShape(shapeNext);
        ImageView nextIcon = new ImageView();
        Image logo3 = new Image("/images/next.png");
        nextIcon.setImage(logo3);
        nextIcon.setFitWidth(10);
        nextIcon.setFitHeight(10);
        next.setGraphic(nextIcon);
        next.setTextFill(Color.WHITE);
        next.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; ");
        next.setOnMouseEntered(e -> {
            next.setStyle("-fx-background-color: #9BABB8; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        next.setOnMouseExited(e -> {
            next.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        });

        Circle shapePrev = new Circle(180);
        prev.setShape(shapePrev);
        ImageView prevIcon = new ImageView();
        Image logo4 = new Image("/images/previous.png");
        prevIcon.setImage(logo4);
        prevIcon.setFitWidth(10);
        prevIcon.setFitHeight(10);
        prev.setGraphic(prevIcon);
        prev.setTextFill(Color.WHITE);
        prev.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; ");
        prev.setOnMouseEntered(e -> {
            prev.setStyle("-fx-background-color: #9BABB8; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        prev.setOnMouseExited(e -> {
            prev.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        });

        Button add = new Button("Add");
        ImageView addIcon = new ImageView();
        Image icon1 = new Image("/images/add.png");
        addIcon.setImage(icon1);
        addIcon.setFitWidth(10);
        addIcon.setFitHeight(10);
        add.setGraphic(addIcon);
        add.setTextFill(Color.WHITE);
        add.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; ");
        add.setOnMouseEntered(e -> {
            add.setStyle("-fx-background-color: #9BABB8; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        add.setOnMouseExited(e -> {
            add.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        add.setOnAction(e -> showAddMusicDialog());

        Button delete = new Button("Delete");
        ImageView deleteIcon = new ImageView();
        Image icon2 = new Image("/images/delete.png");
        deleteIcon.setImage(icon2);
        deleteIcon.setFitWidth(20);
        deleteIcon.setFitHeight(20);
        delete.setGraphic(deleteIcon);
        delete.setTextFill(Color.WHITE);
        delete.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; ");
        delete.setOnMouseEntered(e -> {
            delete.setStyle("-fx-background-color: #9BABB8; -fx-font-weight: bold; -fx-cursor: hand;");
        });
        delete.setOnMouseExited(e -> {
            delete.setStyle("-fx-background-color: #667080; -fx-font-weight: bold; -fx-cursor: hand;");
        });

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
        //mainStage.setFullScreen(true);
        mainStage.show();


        //saat musik yang ditambahkan diklik 2x
        librarySongs.setOnMouseClicked(event -> {
            if (selectedMusic != null && playingFrom.getText().equals("Playing from: " + selectedPlaylist.getPlaylistName())) {
                selectedMusic = null;
            }

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
        VBox vBox = new VBox();
        vBox.setStyle("-fx-background-color: #C4DFDF; ");
        vBox.setPrefSize(2000, 1000);

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
                if (titleField.getText().equals("")) {
                    return new Music(artist, path);
                }
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