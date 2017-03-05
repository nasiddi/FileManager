package controller;

import java.util.Observable;

import enums.FrameState;
import enums.MainNotification;
import enums.SyncNotification;
import logic.Syncer;
import model.InfoModel;

public class SyncController extends Controller {

	private Syncer sync;

	public SyncController(MasterController masterController) {
		super(masterController);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof SyncNotification) {
			switch ((SyncNotification) arg) {
			case INIT:
				sync = new Syncer(super.masterController.getFrameStateManager());
				sync.sync();
				break;
			
			case MAINMENU:
				sync = null;
				super.masterController.getFrameStateManager().requestFrameState(FrameState.MAIN_MENU);
				
			
			}

		}

	}

}
