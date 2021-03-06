package fr.esgi.twitterc.view;

import fr.esgi.twitterc.client.TwitterClient;
import fr.esgi.twitterc.client.TwitterClientException;
import fr.esgi.twitterc.utils.Utils;
import fr.esgi.twitterc.view.controller.ViewController;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import twitter4j.auth.RequestToken;

import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.ResourceBundle;

public class PinView extends ViewController {

    // XML values
    public TextField pinLabel;

    // Internal values
    public static String ID = "PIN";
    public WebView webView;
    public Hyperlink link;
    private RequestToken token;

    /**
     * Initialization of the view controller, called when created from the XML view.
     *
     * @param url The URL used.
     * @param rb The resource bundle used.
     */
    public void initialize(URL url, ResourceBundle rb) {}

    @Override
    protected void onCreation() {}

    @Override
    protected void onShow(Map<String, Object> params) {
        try {
            // Get the auth token
            token = TwitterClient.get().requestToken();

            // Load the url in the web view
            WebEngine engine = webView.getEngine();
            engine.getLoadWorker().stateProperty().addListener((o, old, state) -> {
                if(engine.getDocument() != null) {

                    engine.executeScript("document.body.style.overflow = 'hidden';");

                    if(engine.getDocument().getDocumentURI().equals("https://api.twitter.com/oauth/authorize")) {
                        String pin = engine.getDocument().getElementsByTagName("code").item(0).getTextContent();

                        try {
                            TwitterClient.get().authenticate(token, pin);
                            getWindowController().showView("ProfilView.fxml", Collections.singletonMap("user", TwitterClient.get().getCurrentUser()));
                        } catch (TwitterClientException e) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Erreur !");
                            alert.setHeaderText("Code pin incorrect");
                            alert.setContentText("Le code pin récupéré sur le navigateur semble invalide");
                        }
                    }
                }
            });
            engine.load(token.getAuthorizationURL());

            // Hyperlink action
            link.setOnAction(event -> Utils.openWebPage(token.getAuthorizationURL()));
        } catch (TwitterClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onHide() {}

    @Override
    protected void onDeletion() {}

    @Override
    protected String getID() {
        return ID;
    }

    /**
     * Action when the pin is validated by the user.
     */
    public void pinAction() {
        String pin = pinLabel.getText().trim();

        if(!pin.isEmpty()) {
            try {
                TwitterClient.get().authenticate(token, pin);
                getWindowController().showView("ProfilView.fxml", Collections.singletonMap("user", TwitterClient.get().getCurrentUser()));
            } catch (TwitterClientException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Code pin incorrect");
                alert.setContentText("Twitter didn't give us access to our account with the pin you have provided. Are you sure to have copy and paste the right value?");
            }
        }
    }
}
