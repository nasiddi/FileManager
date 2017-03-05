package controller;

import java.util.Observable;

import enums.FrameState;
import enums.SyncNotification;
import enums.UpdateNameNotification;
import swingPanel.SwingUpdatePanel;

public class UpdateController extends Controller {

	public UpdateController(MasterController masterController) {
		super(masterController);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof UpdateNameNotification) {
			switch ((UpdateNameNotification) arg) {
			case MAINMENU:
				SwingUpdatePanel panel = (SwingUpdatePanel) o;
				panel.update(super.masterController.getFrameStateManager().getCurrentScreen().getModel(), arg);
				super.masterController.getFrameStateManager().requestFrameState(FrameState.MAIN_MENU);
			}

		}
	}
}
