
import java.util.ArrayList;

import controller.FrameStateManager;
import controller.MasterController;
import enums.Constants;
import gui.AssetLoader;
import gui.ClientFrame;
import gui.SwingFrame;
import logic.InfoIO;
import panel.SwingPanel;
import screen.SwingScreen;

class SwingClient extends ClientFrame<SwingPanel> {


	public SwingClient() {
    	new Constants();
    	new AssetLoader();
        SwingFrame swingFrame = new SwingFrame("FileManager");
        SwingFrame frame = swingFrame;

        MasterController masterController = new MasterController(new FrameStateManager<SwingPanel, SwingScreen<SwingPanel, ?, ?>, SwingFrame>(frame));
        frame.draw();
        masterController.init();
    }

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
	}
}