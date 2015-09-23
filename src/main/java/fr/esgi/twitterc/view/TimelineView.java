package fr.esgi.twitterc.view;

import fr.esgi.twitterc.apiEngine.App;
import fr.esgi.twitterc.view.controller.ViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Emerich on 22/09/2015.
 */
public class TimelineView extends ViewController {

    public ListView listview;
    public TextField tweetLabel;
    public ImageView userProfil;
    public static String ID = "TIMELINE";
    public Label tagName;

    /**
     * @param url
     * @param rb
     */
    public void initialize(URL url, ResourceBundle rb) {
        List<String> items = Arrays.asList("One", "Two", "Three");
        ObservableList<String> itemsObservable = FXCollections.observableList(items);
        listview.setItems(itemsObservable);
    }

    @Override
    protected void onCreation() {

    }

    @Override
    protected void onShow() {
        // Set user profil image
        String imageURL = App.getUserProfilImage();
        if(imageURL != null) {
            imageURL =  imageURL.replace("_normal","");
            Image image = new Image(imageURL);
            userProfil.setImage(image);
        }


        // Set user Tag Name
        tagName.setText("@" + App.getUser().getScreenName());
    }

    @Override
    protected void onHide() {

    }

    @Override
    protected void onDeletion() {

    }

    @Override
    protected String getID() {
        return ID;
    }

    public void change(ActionEvent actionEvent) {

        //myController.setScreen(Main.PIN_SCREEN);
    }

    public void mouseClicked(Event event) {
        listview.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().name().equals("PRIMARY")) {
                    if (mouseEvent.getClickCount() == 2) {
                        System.out.printf(listview.getSelectionModel().getSelectedItem().toString());
                    }
                }
            }
        });
    }

    public void sendTweet(ActionEvent actionEvent) {
        String content = tweetLabel.getText();
        if(App.updateStatus(content))
            tweetLabel.setText("");
    }

    public void userProfilClicked(Event event) {
        getAppController().createWindow("Profil", "ProfilView.fxml");
    }
}