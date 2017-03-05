package screen;


import java.util.Observable;

import controller.Controller;
import model.Model;
import panel.Panel;

/**
 * Created by schule on 08/11/16.
 * A screen contains a panel, model and controller and connects them through observer registrations
 */
public abstract class Screen<PanelType extends Panel, ModelType extends Model, ControllerType extends Controller> extends Observable {

    private PanelType panel;
    private ModelType model;
    private ControllerType controller;

    /**
     * Creates a new screen with the respective panel, model and controller
     *
     * @param panel
     * @param model
     * @param controller
     */
    public Screen(PanelType panel, ModelType model, ControllerType controller) {
        this.panel = panel;
        this.model = model;
        this.controller = controller;
        this.controller.setModel(model);
    }

    /**
     * Adds all internal observers
     */
    public void addObservers() {
        model.addObserver(panel);
        panel.addObserver(controller);
    }

    /**
     * Removes all internal observers
     */
    public void removeObservers() {
        model.deleteObserver(panel);
        panel.deleteObserver(controller);
    }

    /**
     * Retuns the screen's panel
     *
     * @return
     */
    public PanelType getPanel() {
        return this.panel;
    }

    public ControllerType getController() {
        return this.controller;
    }

    public ModelType getModel() {
        return this.model;
    }
}
