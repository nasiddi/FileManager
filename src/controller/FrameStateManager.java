package controller;



import java.util.HashMap;
import java.util.logging.Logger;

import enums.FrameState;
import gui.ClientFrame;
import panel.Panel;
import screen.Screen;


public class FrameStateManager<PanelType extends Panel, ScreenType extends Screen<PanelType, ?, ?>, FrameType extends ClientFrame<PanelType>> {

    private HashMap<FrameState, ScreenType> screenList;
    private FrameState frameState;

    private FrameType frame;

    private Logger logger = Logger.getLogger("crazy_sepp");

    /**
     * Create a new FrameStateManager
     *
     * @param frame
     */
    public FrameStateManager(FrameType frame) {
        this.frame = frame;
        screenList = new HashMap<>();
    }

    /**
     * Associate a screen with a given frameState
     *
     * @param frameState
     * @param screen
     */
    public void addFrameState(FrameState frameState, ScreenType screen) {
        screenList.put(frameState, screen);
    }

    /**
     * Request that the manager to activate the frame associated with the given FrameState
     *
     * @param frameState
     */
    public void requestFrameState(FrameState frameState) {
        if (getCurrentScreen() != null) {
            if (screenList.size() > 0) {
                getCurrentScreen().removeObservers();
            } else {
                throw new NullPointerException();
            }
        }
        setFrameState(frameState);
        getCurrentScreen().getPanel().reset();
        frame.requestActivePanel(getCurrentScreen().getPanel());
        frame.draw();
        getCurrentScreen().addObservers();
        logger.info("Frame State: " + frameState.name() + " -> " + getCurrentScreen().getClass().getName());
    }

    /**
     * Returns the screen associated with the current FrameState
     *
     * @return
     */
    public ScreenType getCurrentScreen() {
        return screenList.get(getFrameState());
    }

    /**
     * Returns the current state of the frame
     *
     * @return
     */
    public FrameState getFrameState() {
        return frameState;
    }

    public FrameType getFrame(){
    	return frame;
    }
    private void setFrameState(FrameState frameState) {
        this.frameState = frameState;
    }
    
    

}
