package controller;

import java.util.Observable;

import enums.FrameState;
import enums.LoadingNotification;


public class LoadingController extends Controller {

	private MasterController mC;

	public LoadingController(MasterController masterController) {
		super(masterController);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof LoadingNotification) {
			mC = super.masterController;
			switch ((LoadingNotification) arg) {
			case RETRY:
				Thread reloadThread = new Thread(new Runnable() {
					@Override
					public void run() {
						mC.reload();
					}
				});
				reloadThread.start();				
				break;
			case MAINMENU:
				super.masterController.getFrameStateManager().requestFrameState(FrameState.MAIN_MENU);
				break;
		}

	}
		

	}

}
