package gui;

public abstract class ClientFrame<PanelType> {

    protected String name;
    private PanelType activePanel;

    /**
     * Creates a new frame with the specified name
     *
     * @param name
     */
    public ClientFrame(String name) {
        this.name = name;
    }
    
    public ClientFrame(){
    	
    }

    /**
     * Instructs the frame to change its active panel and draw the new one
     *
     * @param panel
     */
    public void requestActivePanel(PanelType panel) {
        setActivePanel(panel);
    }

    /**
     * Returns the currently active panel
     *
     * @return
     */
    protected PanelType getActivePanel() {
        return this.activePanel;
    }

    private void setActivePanel(PanelType panel) {
        this.activePanel = panel;
    }

    /**
     * Instruct the panel to draw itself onto the screen
     */
    public abstract void draw();

}

