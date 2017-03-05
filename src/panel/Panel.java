package panel;

import java.util.Observable;
import java.util.Observer;


public abstract class Panel extends Observable implements Observer {

    /**
     * Resets the panel to its original state
     */
    public abstract void reset();

}