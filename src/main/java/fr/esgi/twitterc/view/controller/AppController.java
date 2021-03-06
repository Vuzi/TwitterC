package fr.esgi.twitterc.view.controller;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * TwitterCController controller of the application.
 *
 * Created by Vuzi on 22/09/2015.
 */
public abstract class AppController extends Application {

    private List<WindowController> windows;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Logger.getLogger(this.getClass().getName()).info("Application controller started");
        onCreation();
    }

    @Override
    public void stop() {
        Logger.getLogger(this.getClass().getName()).info("Application controller stopped");
        onEnd();
    }


    /**
     * Called on the creation of the application controller.
     */
    protected void onCreation() {}

    protected void onWindowCreation() {}

    protected void onWindowDeletion() {}

    protected abstract void onEnd();

    /**
     * TwitterClient constructor.
     */
    public AppController() {
        this.windows = new ArrayList<>();
    }

    /**
     * Return the image of the application.
     *
     * @return The image of the application.
     */
    protected abstract Image getAppIcon();

    /**
     * Create a window, and return its controller. The view will be initialized with the provided panel name, and shown.
     *
     * @param title The title of the window.
     * @param mainPanel The main panel name.
     */
    public WindowController createWindow(String title, String mainPanel) {
        return createWindow(new Stage(), title, mainPanel, null);
    }
    /**
     * Create a window, and return its controller. The view will be initialized with the provided panel name, and shown.
     *
     * @param title The title of the window.
     * @param mainPanel The main panel name.
     * @param mainPanelParameter The main panel parameters.
     */
    public WindowController createWindow(String title, String mainPanel, Map<String, Object> mainPanelParameter) {
        return createWindow(new Stage(), title, mainPanel, mainPanelParameter);
    }

    /**
     * Create a window, and return its controller. The view will be initialized with the provided panel name, and shown.
     *
     * @param window The window to use.
     * @param title The title of the window.
     * @param mainPanel The main panel name.
     * @param mainPanelParameter The main panel parameters.
     */
    public WindowController createWindow(Stage window, String title, String mainPanel, Map<String, Object> mainPanelParameter) {
        // The controller, AKA the window content
        final WindowController controller = new WindowController(this, mainPanel, mainPanelParameter);

        // Add and show
        Scene scene = new Scene(controller);
        window.setScene(scene);
        window.setTitle(title);
        window.getIcons().add(getAppIcon());
        window.setOnCloseRequest(event -> {
            // Delete the controller
            controller.onDeletion();
            windows.remove(controller);
            onWindowDeletion();
        });
        onWindowCreation();
        window.show();

        // Add to the list
        windows.add(controller);
        controller.setWindow(window);

        return controller;
    }

    public List<WindowController> getWindows() {
        return windows;
    }
}
