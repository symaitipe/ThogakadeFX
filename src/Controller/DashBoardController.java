package Controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DashBoardController implements Initializable {

    public AnchorPane dashBoardPane;
    @FXML
    private Label lblDate;

    @FXML
    private Label lblTime;

    @FXML
    void customerButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) dashBoardPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../View/CustomerForm.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
    }

    @FXML
    void itemButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) dashBoardPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../View/ItemForm.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
    }

    @FXML
    void orderButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) dashBoardPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../View/OrderForm.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
    }

    @FXML
    void placeOrderButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) dashBoardPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../View/PlaceOrderForm.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        manageDateTime();
    }

    private void manageDateTime() {
        //set Date For Date Label
        Timeline date = new Timeline(new KeyFrame(Duration.ZERO,
                actionEvent -> lblDate.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))),
                new KeyFrame(Duration.seconds(1)));

        date.setCycleCount(Animation.INDEFINITE);
        date.play();

        //set Time For Time Label
        KeyFrame ky0ne = new KeyFrame(Duration.ZERO,
                actionEvent ->lblTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss"))) );
        KeyFrame kyTwo = new KeyFrame(Duration.seconds(1));

        Timeline time = new Timeline(ky0ne,kyTwo);
        time.setCycleCount(Animation.INDEFINITE);
        time.play();

    }


}
