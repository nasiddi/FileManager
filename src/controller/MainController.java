package controller;

import java.util.Observable;

import enums.ErrorNotification;
import enums.FrameState;
import enums.LoadingNotification;
import enums.MainNotification;
import enums.SyncNotification;
import enums.UpdateNameNotification;
import gui.StatFrame;

public class MainController extends Controller {

	MainController(MasterController masterController) {
		super(masterController);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof MainNotification) {
			MasterController mC = super.masterController;
			FrameStateManager frameStateManager = mC.getFrameStateManager();
			if (arg instanceof MainNotification) {
				switch ((MainNotification) arg) {
				case SYNC:
					Thread syncThread = new Thread(new Runnable() {
						@Override
						public void run() {
							if(!reload())
								return;
							frameStateManager.requestFrameState(FrameState.SYNC);
							mC.update(frameStateManager.getCurrentScreen().getPanel(), SyncNotification.INIT);
						}
					});
					syncThread.start();
					break;
				case ERROR:
					Thread errorThread = new Thread(new Runnable() {
						@Override
						public void run() {
							if(!reload())
								return;
							frameStateManager.requestFrameState(FrameState.ERROR);
							mC.update(frameStateManager.getCurrentScreen().getPanel(), ErrorNotification.INIT);
						}
					});
					errorThread.start();
					break;
				case RENAME:
					Thread renameThread = new Thread(new Runnable() {
						@Override
						public void run() {
							if(!reload())
								return;
							frameStateManager.requestFrameState(FrameState.RENAME);
						}
					});
					renameThread.start();
					break;

				case RELOAD:
					Thread reloadThread = new Thread(new Runnable() {
						@Override
						public void run() {
							frameStateManager.requestFrameState(FrameState.LOADING);
							frameStateManager.getCurrentScreen().getController().update(null, LoadingNotification.INIT);
						}
					});
					reloadThread.start();
					break;
				case STATISTIC:
					Thread statisticThread = new Thread(new Runnable() {
						@Override
						public void run() {
							if(!reload())
								return;
							new StatFrame(mC.getInfoModel());
						}
					});
					statisticThread.start();
					break;
				case UPDATE_NAMES:
					Thread updateThread = new Thread(new Runnable() {
						@Override
						public void run() {
							reload();
							frameStateManager.requestFrameState(FrameState.UPDATE);
							mC.update(frameStateManager.getCurrentScreen().getPanel(), UpdateNameNotification.UPDATE);
						}
					});
					updateThread.start();
				}
			}
		}

	}
}