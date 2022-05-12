package sample;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;


public class Controller implements Initializable {

    @FXML
    private BorderPane borderParent;


    @FXML
    private Button buttonPPR;

    @FXML
    private Button buttonStop;

    @FXML
    private Button slow;

    @FXML
    private Button fast;

    @FXML
    private Button skipForward10;

    @FXML
    private Button skipBack10;

    @FXML
    private Button volumeMute;

    @FXML
    private Label fullScreen;

    @FXML
    private Label currentTime;

    @FXML
    private Label totalTime;

    @FXML
    private VBox vbVolume;

    @FXML
    private Slider sliderTime;

    @FXML
    private Slider volumeSlider;

    @FXML
    private HBox hbControls;

    @FXML
    private MediaView mediaView;

    @FXML
    private Button btnBrowse;

    private File file;
    private MediaPlayer mediaPlayer;
    private Media media;

    private boolean endOfVideo = false;
    private boolean isPlaying = false;
    private boolean isMuted = false;

    private ImageView ivPlay;
    private ImageView ivPause;
    private ImageView ivReplay;
    private ImageView ivExitFullScreen;
    private ImageView ivFullScreen;
    private ImageView ivStop;
    private ImageView ivVolume;
    private ImageView ivVolumeMute;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        final int IV_SIZE = 25;

        file = new File("src/sample/video1.mp4");
        media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        DoubleProperty widthProp = mediaView.fitWidthProperty();
        DoubleProperty heightProp = mediaView.fitHeightProperty();

        widthProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        heightProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));


        Image imagePlay = new Image(new File("src/sample/play.png").toURI().toString());
        ivPlay = new ImageView(imagePlay);
        ivPlay.setFitHeight(IV_SIZE);
        ivPlay.setFitWidth(IV_SIZE);

        Image imagePause = new Image(new File("src/sample/pause.png").toURI().toString());
        ivPause = new ImageView(imagePause);
        ivPause.setFitHeight(IV_SIZE);
        ivPause.setFitWidth(IV_SIZE);

        Image imageReplay = new Image(new File("src/sample/reload.png").toURI().toString());
        ivReplay = new ImageView(imageReplay);
        ivReplay.setFitHeight(IV_SIZE);
        ivReplay.setFitWidth(IV_SIZE);

        Image imageStop = new Image(new File("src/sample/stop.png").toURI().toString());
        ivStop = new ImageView(imageStop);
        ivStop.setFitHeight(IV_SIZE);
        ivStop.setFitWidth(IV_SIZE);

        Image imageVolume = new Image(new File("src/sample/volume.png").toURI().toString());
        ivVolume = new ImageView(imageVolume);
        ivVolume.setFitHeight(20);
        ivVolume.setFitWidth(20);

        Image imageMute = new Image(new File("src/sample/mute.png").toURI().toString());
        ivVolumeMute = new ImageView(imageMute);
        ivVolumeMute.setFitHeight(20);
        ivVolumeMute.setFitWidth(20);

        Image imageFullScreen = new Image(new File("src/sample/full-screen.png").toURI().toString());
        ivFullScreen = new ImageView(imageFullScreen);
        ivFullScreen.setFitHeight(IV_SIZE);
        ivFullScreen.setFitWidth(IV_SIZE);

        Image imageExitFullScreen = new Image(new File("src/sample/exit-full-screen.png").toURI().toString());
        ivExitFullScreen = new ImageView(imageExitFullScreen);
        ivExitFullScreen.setFitHeight(IV_SIZE);
        ivExitFullScreen.setFitWidth(IV_SIZE);

        buttonPPR.setGraphic(ivPlay);
        buttonStop.setGraphic(ivStop);
        volumeMute.setGraphic(ivVolume);
        fullScreen.setGraphic(ivFullScreen);

        volumeSlider.setValue(0.4);
        buttonPPR.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                bindCurrentTimeLabel();
                mediaPlayer.setRate(1);
                Button buttonPlay = (Button) actionEvent.getSource();
                if (endOfVideo) {
                    sliderTime.setValue(0);
                    endOfVideo = false;
                    isPlaying = false;
                }
                if (isPlaying) {
                    buttonPlay.setGraphic(ivPlay);
                    mediaPlayer.pause();
                    isPlaying = false;
                } else {
                    buttonPlay.setGraphic(ivPause);
                    mediaPlayer.play();
                    isPlaying = true;

                }
            }
        });

        mediaPlayer.volumeProperty().bindBidirectional(volumeSlider.valueProperty());

        bindCurrentTimeLabel();
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (mediaPlayer.getVolume() != 0.0) {
                    volumeMute.setGraphic(ivVolume);
                    isMuted = false;
                } else {
                    mediaPlayer.setVolume(volumeSlider.getValue());
                    volumeMute.setGraphic(ivVolumeMute);
                    isMuted = true;
                }
            }
        });
        volumeMute.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (isMuted) {
                    volumeMute.setGraphic(ivVolume);
                    volumeSlider.setValue(0.8);
                    mediaPlayer.setVolume(volumeSlider.getValue());
                    isMuted = false;

                } else {
                    volumeMute.setGraphic(ivVolumeMute);
                    volumeSlider.setValue(0);
                    mediaPlayer.setVolume(volumeSlider.getValue());
                    isMuted = true;
                }
            }
        });

        borderParent.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observableValue, Scene oldScene, Scene newScene) {
                if (oldScene == null && newScene != null) {
                    mediaView.fitHeightProperty().bind(newScene.heightProperty().subtract(hbControls.heightProperty().add(20)));
                }

            }
        });
        fullScreen.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Label label = (Label) mouseEvent.getSource();
                Stage stage = (Stage) label.getScene().getWindow();
                if (stage.isFullScreen()) {
                    stage.setFullScreen(false);
                    fullScreen.setGraphic(ivFullScreen);
                } else {
                    stage.setFullScreen(true);
                    fullScreen.setGraphic(ivExitFullScreen);
                }
                stage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent keyEvent) {

                        if (keyEvent.getCode() == KeyCode.ESCAPE) {
                            fullScreen.setGraphic(ivFullScreen);
                        }
                    }
                });
            }
        });
        mediaPlayer.totalDurationProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldDuration, Duration newDuration) {
                bindCurrentTimeLabel();
                sliderTime.setMax(newDuration.toSeconds());
                totalTime.setText(getTime(newDuration));

            }
        });
        sliderTime.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasChanging, Boolean isChanging) {
                if (!isChanging) {
                    mediaPlayer.seek(Duration.seconds(sliderTime.getValue()));
                }
            }
        });
        sliderTime.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                bindCurrentTimeLabel();
                double currentTime1 = mediaPlayer.getCurrentTime().toSeconds();
                if (Math.abs(currentTime1 - newValue.doubleValue()) > 0.5) {
                    mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                }
                labelMatchEndVideo(currentTime.getText(), totalTime.getText());
            }
        });

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldTime, Duration newTime) {
                sliderTime.setValue(newTime.toSeconds());
                labelMatchEndVideo(currentTime.getText(), totalTime.getText());
            }
        });

        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                buttonPPR.setGraphic(ivReplay);
                endOfVideo = true;
                if (currentTime.textProperty().equals(totalTime.textProperty())) {
                    currentTime.textProperty().unbind();
                    currentTime.setText(getTime(mediaPlayer.getTotalDuration()));

                }
            }
        });
    }

    public void bindCurrentTimeLabel() {
        currentTime.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {

            public String call() throws Exception {
                return getTime(mediaPlayer.getCurrentTime());

            }
        }, mediaPlayer.currentTimeProperty()));
    }

    public String getTime(Duration time) {
        int hours = (int) time.toHours();
        int minutes = (int) time.toMinutes();
        int seconds = (int) time.toSeconds();

        if (seconds > 59) seconds = seconds % 60;
        if (minutes > 59) minutes = minutes % 60;
        if (hours > 59) hours = hours % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }

    }


    public void labelMatchEndVideo(String labelTime, String totalTime) {
        for (int i = 0; i < totalTime.length(); i++) {
            if (labelTime.charAt(i) != totalTime.charAt(i)) {
                endOfVideo = false;
                if (isPlaying) buttonPPR.setGraphic(ivPause);
                else buttonPPR.setGraphic(ivPlay);
                break;
            } else {
                endOfVideo = true;
                buttonPPR.setGraphic(ivReplay);
            }
        }
    }




    public void chooseFile(ActionEvent event) {
        String path;
        FileChooser fileChooser = new FileChooser();
        file = fileChooser.showOpenDialog(null);
        path = file.toURI().toString();

        if (path != null) {
            mediaPlayer.stop();
            //volumeSlider.setValue(0.4);
            media = new Media(path);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            mediaPlayer.volumeProperty().bindBidirectional(volumeSlider.valueProperty());
            bindCurrentTimeLabel();
            volumeSlider.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    if (mediaPlayer.getVolume() != 0.0) {
                        volumeMute.setGraphic(ivVolume);
                        isMuted = false;
                    } else {
                        mediaPlayer.setVolume(volumeSlider.getValue());
                        volumeMute.setGraphic(ivVolumeMute);
                        isMuted = true;
                    }
                }
            });
            volumeMute.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (isMuted) {
                        volumeMute.setGraphic(ivVolume);
                        volumeSlider.setValue(0.8);
                        mediaPlayer.setVolume(volumeSlider.getValue());
                        isMuted = false;

                    } else {
                        volumeMute.setGraphic(ivVolumeMute);
                        volumeSlider.setValue(0);
                        mediaPlayer.setVolume(volumeSlider.getValue());
                        isMuted = true;
                    }
                }
            });


            fullScreen.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    Label label = (Label) mouseEvent.getSource();
                    Stage stage = (Stage) label.getScene().getWindow();
                    if (stage.isFullScreen()) {
                        stage.setFullScreen(false);
                        fullScreen.setGraphic(ivFullScreen);
                    } else {
                        stage.setFullScreen(true);
                        fullScreen.setGraphic(ivExitFullScreen);
                    }
                    stage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent keyEvent) {

                            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                                fullScreen.setGraphic(ivFullScreen);
                            }
                        }
                    });
                }
            });
            mediaPlayer.totalDurationProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observableValue, Duration oldDuration, Duration newDuration) {
                    bindCurrentTimeLabel();
                    sliderTime.setMax(newDuration.toSeconds());
                    totalTime.setText(getTime(newDuration));

                }
            });
            sliderTime.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasChanging, Boolean isChanging) {
                    if (!isChanging) {
                        mediaPlayer.seek(Duration.seconds(sliderTime.getValue()));
                    }
                }
            });
            sliderTime.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                    bindCurrentTimeLabel();
                    double currentTime1 = mediaPlayer.getCurrentTime().toSeconds();
                    if (Math.abs(currentTime1 - newValue.doubleValue()) > 0.5) {
                        mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                    }
                    labelMatchEndVideo(currentTime.getText(), totalTime.getText());
                }
            });

            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observableValue, Duration oldTime, Duration newTime) {
                    sliderTime.setValue(newTime.toSeconds());
                    labelMatchEndVideo(currentTime.getText(), totalTime.getText());
                }
            });

            mediaPlayer.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    buttonPPR.setGraphic(ivReplay);
                    endOfVideo = true;
                    if (currentTime.textProperty().equals(totalTime.textProperty())) {
                        currentTime.textProperty().unbind();
                        currentTime.setText(getTime(mediaPlayer.getTotalDuration()));

                    }
                }
            });
        }




    }

    public  void slowRate(ActionEvent event){
        mediaPlayer.setRate(0.5);
    }

    public  void fastRate(ActionEvent event){
        mediaPlayer.setRate(2);
    }
    public void mediaStop(){
        mediaPlayer.stop();
    }
    public void skip10(ActionEvent event){
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(10)));
    }
    public void back10(ActionEvent event){
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(-10)));
    }


}



