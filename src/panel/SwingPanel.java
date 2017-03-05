package panel;

import java.awt.Container;


public abstract class SwingPanel extends Panel {

    /**
     * Adds the panel to the component
     *
     * @param component
     */
    public abstract void addToContainer(Container component);

    /**
     * Removes the panel from the component
     *
     * @param component
     */
    public abstract void removeFromContainer(Container component);

}