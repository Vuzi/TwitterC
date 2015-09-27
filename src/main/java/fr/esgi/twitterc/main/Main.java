package fr.esgi.twitterc.main;

import fr.esgi.twitterc.client.TwitterClient;
import fr.esgi.twitterc.client.TwitterClientException;
import fr.esgi.twitterc.view.controller.AppController;
import twitter4j.TwitterException;

import java.util.logging.Logger;

/**
 * TwitterClient main controller and entry point.
 *
 * Created by Vuzi on 23/09/2015.
 */
public class Main extends AppController {

    /**
     * Entry point.
     *
     * @param args Arguments.
     */
    public static void main(String[] args) {
        Logger.getLogger(Main.class.getName()).info("TwitterClient started");

        // Load and try to load the access token
        try {
            TwitterClient.initialize().authenticate();
        } catch (TwitterClientException e) {}

        // Show the views
        Main.launch(Main.class, args);
    }

    @Override
    protected String getAppName() {
        return "TwitterC";
    }

    @Override
    protected String getFirstView() {
        if(TwitterClient.get().getCurrentUser() == null)
            return "PinView.fxml"; // Not authenticated
        return "TimelineView.fxml"; // Authenticated
    }
}
