package controller;

import java.util.Observable;

import enums.FrameState;
import enums.SyncNotification;
import enums.UpdateNameNotification;
import logic.Syncer;
import model.InfoModel;
import swingPanel.SwingSyncPanel;

public class SyncController extends Controller {

	private Syncer sync;

	SyncController(MasterController masterController) {
		super(masterController);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof SyncNotification) {
			SwingSyncPanel panel = (SwingSyncPanel) o;
			MasterController mC = super.masterController;
			switch ((SyncNotification) arg) {
			case INIT:
				InfoModel model = mC.getInfoModel();
				panel.update(model, SyncNotification.UPDATE);
				sync = new Syncer(model);
				sync.sync();
				break;
			
			case MAINMENU:
				sync = null;
				panel.reset();
				mC.getFrameStateManager().requestFrameState(FrameState.MAIN_MENU);
			default:
				break;
				
			
			}

		}

	}

}
