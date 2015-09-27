package fr.esgi.twitterc.view;

import fr.esgi.twitterc.client.TwitterClient;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 * Controller of a tweet contained in tweet list view.
 *
 * Created by Vuzi on 27/09/2015.
 */
public class TweetListView {

    // XML values
    public Pane profileImage;
    public Label userName;
    public Label userTag;
    public Label date;
    public Label content;
    public Pane tweetPanel;

    // Running values
    private Status status;
    private Image userImage;

    /**
     * Return the view of the controller.
     *
     * @return The view.
     */
    public Node getView() {
        return tweetPanel;
    }

    /**
     * Update the view with the provided status.
     *
     * @param status The status.
     */
    public void update(Status status) {
        this.status = status;

        // Set user real name
        userName.setText(status.getUser().getName());

        // Set user TAG
        userTag.setText("@" + status.getUser().getScreenName());

        // Set date
        date.setText(status.getCreatedAt().toString());

        // Set content
        if(status.isRetweet()) {
            content.setText(status.getRetweetedStatus().getText());
        } else {
            content.setText(status.getText());
        }

        // Set user image
        userImage = null;

        if(status.getUser().getProfileImageURL() != null) {

            Task<Image> imageLoading = new Task<Image>() {
                @Override
                protected Image call() throws Exception {
                    return new Image(status.getUser().getProfileImageURL());
                }
            };

            imageLoading.setOnSucceeded(event -> {
                userImage = imageLoading.getValue();

                BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true);
                BackgroundImage backgroundImage = new BackgroundImage(userImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
                profileImage.setBackground(new Background(backgroundImage));
            });

            imageLoading.run();
        }
    }

    /**
     * Action when the "respond" button is clicked.
     *
     * @param actionEvent The action event.
     */
    public void respondAction(ActionEvent actionEvent) {
    }

    /**
     * Action when the "retweet" button is clicked.
     *
     * @param actionEvent The action event.
     */
    public void retweetAction(ActionEvent actionEvent) {

        if(status == null)
            return;

        // Show confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation retweet");
        alert.setHeaderText("Confirmation de retweet");

        if(userImage != null)
            alert.setGraphic(new ImageView(userImage));

        alert.setContentText(MessageFormat.format("Êtes-vous certain de vouloir retweeter le message de {0} :\n\"{1}\" ?",
                status.getUser().getName(), status.isRetweet() ? status.getRetweetedStatus().getText() : status.getText()));

        if(alert.showAndWait().get() == ButtonType.OK) {
            try {
                TwitterClient.client().retweetStatus(status.getId());

                // Show success
                alert.setTitle("Retweet effectué");
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Retweet effectué avec succès !");
                alert.showAndWait();

            } catch (TwitterException e) {
                Logger.getLogger(this.getClass().getName()).info("Error while retweeting status : " + e.getMessage());
                e.printStackTrace();

                // Show that an error occurred
                alert.setTitle("Erreur durant le retweet");
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Le retweet n'a pas pu être effectué...");
                alert.showAndWait();
            }
        }
    }

    /**
     * Action when the "favorite" button is clicked.
     *
     * @param actionEvent The action event.
     */
    public void addFavoriteAction(ActionEvent actionEvent) {
    }

    /**
     * Action when the "see" button is clicked.
     *
     * @param actionEvent The action event.
     */
    public void seeDetailAction(ActionEvent actionEvent) {

    }
}
