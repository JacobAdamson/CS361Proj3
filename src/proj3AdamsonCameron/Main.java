package proj3AdamsonCameron;

/*
 * File: proj2AdamsonBeckCameron.Main.java
 * Names: Jake Adamson, Charlie Beck, Nicholas Cameron
 * Class: CS361
 * Project: 2
 * Date: September 22, 2016
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * A JavaFX application composed of two buttons and an exit menu item.  The "Play"
 * button allows the user to input an integer starting note for a piano scale to
 * be played, and the "Stop" button halts any scale currently playing.
 *
 * @author Jake Adamson
 * @author Charlie Beck
 * @author Nicholas Cameron
 */
public class Main extends Application {

    /** A MidiPlayer instance to play musical notes. */
    private MidiPlayer player = new MidiPlayer(3, 60);

    /**
     * Initializes the application window.
     *
     * @param primaryStage      the Stage containing the application window
     * @throws IOException      exception thrown if Main.fxml fails to load
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
            Scene scene = new Scene(root, 200, 140);
            scene.getStylesheets().add("proj2AdamsonBeckCameron/Main.css");
            primaryStage.setTitle("Scale Player");
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(event -> close());
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("The FXML formatting file failed to load.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Closes the window and exits the program.
     */
    @FXML
    public void close() {
        Platform.exit();
        System.exit(0);
    }

    /**
     * Gives a prompt to the user to enter the starting note as an integer in the
     * range [0, 115] and then plays the scale starting at the given note.
     */
    @FXML
    public void playButtonHandler() {
        Optional<Integer> input = getNumericInput(
                "Give me a starting note (0-115):");
        if (input.isPresent()) {
            if (input.get() <= 115) {
                playNotes(player, input.get());
            }
        }
    }

    /**
     * Stops playing the scale, if one is currently playing.
     */
    @FXML
    public void stopButtonHandler() {
        player.clear();
    }

    /**
     * Plays all notes, up and down, in a twelve note scale for a given starting note.
     *
     * @param player            the Midi player to use for playing the scale
     * @param startNote         the starting note for the scale as an int in [0, 115]
     */
    private void playNotes(MidiPlayer player, int startNote){
        player.stop();
        player.clear();
        int[] scale = {0, 2, 4, 5, 7, 9, 11, 12, 12, 11, 9, 7, 5, 4, 2, 0};
        for (int i = 0; i < scale.length; i++) {
            player.addNote(startNote + scale[i], 80, i, 1, 0, 0);
        }
        player.play();
    }

    /**
     * Gets an optional non-negative integer from the user through a dialog window.
     *
     * @param prompt    a String to prompt the user in the dialog
     * @return          an Optional containing either the input Integer or nothing
     *                  if the user cancels the dialog
     */
    private Optional<Integer> getNumericInput(String prompt) {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setContentText(prompt);
        TextField inputTextField = inputDialog.getEditor();
        Button okButton = (Button) inputDialog.getDialogPane().
                lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        // Only allows non-negative integer input
        // http://stackoverflow.com/questions/7555564/what-is-the-
        //   recommended-way-to-make-a-numeric-textfield-in-javafx
        inputTextField.textProperty().addListener(
                (observable, oldValue, newValue) ->
            {
                // regex for non-negative number of numeric digits
                if (!newValue.matches("\\d*")) {
                    inputTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
                // regex for positive number of numeric digits
                okButton.setDisable(!observable.getValue().matches("\\d+"));
            });

        inputDialog.showAndWait();
        Integer inputInt;
        try {
            inputInt = Integer.parseInt(inputDialog.getResult());
        } catch (NumberFormatException e) {
            inputInt = null;
        }
        return Optional.ofNullable(inputInt);
    }

    /**
     * Launches the application in cases of failure to launch properly through
     * javafx.application.Application.launch
     *
     * @param args      command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
