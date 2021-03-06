package fr.esgi.twitterc.view.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Window controller. A windows controller work with a stack of view, any new view created will be stacked on the old
 * ones. View can be freely re-used as long as they provided a non-null ID.
 *
 * Created by Vuzi on 22/09/2015.
 */
public class WindowController extends StackPane {

    private final static int MAX_SAVED = 15; // Max number of generic view saved

    private final AppController appController;
    private final Stack<ViewController> controllers;
    private final Map<String, ViewController> controllersByID;
    private Stage window;

    /**
     * Constructor for the windows controller.
     *
     * @param appController The application controller.
     */
    public WindowController(AppController appController, String firstView) {
        this(appController, firstView, null);
    }

    /**
     * Constructor for the windows controller.
     *
     * @param appController The application controller.
     */
    public WindowController(AppController appController, String firstView, Map<String, Object> firstViewParameters) {
        super();

        Logger.getLogger(this.getClass().getName()).info(MessageFormat.format("New view controller created with {0} as first view", firstView));

        this.appController = appController;
        this.controllers = new Stack<>();
        this.controllersByID = new HashMap<>();

        // Force the creation of the first view
        showView(firstView, firstViewParameters);
    }

    /**
     * Return the application controller.
     *
     * @return The application controller.
     */
    public AppController getAppController() {
        return appController;
    }

    /**
     * Show the previous view, and delete the actual.
     */
    public void showBack() {
        if(controllers.size() <= 1)
            return;

        ViewController previousView = controllers.pop();
        transitionFromTo(previousView, controllers.peek(), null);

        // Delete if not re-usable
        if(!previousView.isReusable())
            previousView.onDeletion();
    }

    /**
     * Show and reuse if possible the provided view. If no possible, the view will be created.
     *
     * @param name The view name.
     * @param ID The view ID to re-use if possible.
     */
    public void showOrReuseView(String name, String ID) {
        showOrReuseView(name, ID, null);
    }

    /**
     * Show and reuse if possible the provided view. If no possible, the view will be created.
     *
     * @param name The view name.
     * @param ID The view ID to re-use if possible.
     * @param parameters The view argument.
     */
    public <T> void showOrReuseView(String name, String ID, Map<String, Object> parameters) {
        ViewController controller = controllersByID.get(ID);

        // If null, force creation
        if(controller == null) {
            showView(name);
            return;
        }

        // Show
        Logger.getLogger(this.getClass().getName()).info(MessageFormat.format("View re-use of {0}", name));

        ViewController previousView = controllers.peek();
        controllers.push(controller);
        transitionFromTo(previousView, controller, parameters);
    }

    /**
     * Show and force the creation of the provided view.
     *
     * @param name The view name.
     */
    public void showView(String name) {
        showView(name, null);
    }

    /**
     * Show and force the creation of the provided view.
     *
     * @param name The view name.
     * @param parameters The view argument
     */
    public void showView(String name, Map<String, Object> parameters) {
        try {
            Logger.getLogger(this.getClass().getName()).info(MessageFormat.format("View creation of {0}", name));

            ViewController previousView = controllers.size() > 0 ? controllers.peek() :  null;
            controllers.push(createViewController(name));

            transitionFromTo(previousView, controllers.peek(), parameters != null ? parameters : new HashMap<>());
        } catch (IOException e) {
            // Do not add the view
            Logger.getLogger(this.getClass().getName()).info(MessageFormat.format("Error showing view {0} : {1}", name, e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * Performs a transition between the two given views. For now, no transitions are used, only adding/deleting
     * children nodes.
     *
     * @param from The old view controller. Can be null if the first panel is being displayed.
     * @param to The new view controller.
     * @param parameters The showed view parameter.
     */
    private void transitionFromTo(ViewController from, ViewController to, Map<String, Object> parameters) {
        // Remove the old view
        if(from != null) {
            getChildren().remove(0);
            from.onHide();
        }

        // Add the new one
        getChildren().add(0, to.getView());
        to.onShow(parameters);
    }

    /**
     * Create a view controller from a resource.
     *
     * @param resource The resource.
     * @return The created view controller.
     * @throws IOException Thrown on error.
     */
    private ViewController createViewController(String resource) throws IOException {
        Logger.getLogger(this.getClass().getName()).info("New view for " + resource);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/esgi/twitterc/view/" + resource));
        Parent view = loader.load();
        ViewController controller = loader.getController();

        // Initialize the controller
        controller.setWindowController(this);
        controller.setView(view);
        controller.onCreation();

        // Save the view for being re-used
        if(controller.getID() != null && controllersByID.size() < MAX_SAVED)
            controllersByID.put(controller.getID(), controller);

        return controller;
    }

    /**
     * Called when the windows is deleted. This method will call any view controller in the stack, or still keeped as a
     * re usable view.
     */
    public void onDeletion() {
        Set<ViewController> controllersToDelete = new HashSet<>(controllers);
        controllersToDelete.addAll(controllersByID.values());

        for(ViewController controller : controllersToDelete) {
            controller.onDeletion();
        }
    }

    /**
     * Set the associated windows with the controller.
     *
     * @param window The window.
     */
    public void setWindow(Stage window) {
        this.window = window;
    }

    /**
     * Close the window.
     */
    public void close() {
        this.window.close();
    }
}
