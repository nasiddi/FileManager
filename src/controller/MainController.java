package controller;

import java.util.Observable;

import javax.swing.SwingUtilities;

import enums.FrameState;
import enums.MainNotification;
import enums.SyncNotification;
import model.InfoModel;

public class MainController extends Controller {
	private MasterController mC;
	public MainController(MasterController masterController) {
		super(masterController);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof MainNotification) {
			mC = super.masterController;
			switch ((MainNotification) arg) {
			case SYNC:
				Thread syncThread = new Thread(new Runnable() {
					@Override
					public void run() {
						mC.sync();
					}
				});
				syncThread.start();
				break;
			case ERROR:
				Thread errorThread = new Thread(new Runnable() {
					@Override
					public void run() {
						mC.errorsearch();
					}
				});
				errorThread.start();				
				break;
			case RENAME:
				Thread renameThread = new Thread(new Runnable() {
					@Override
					public void run() {
						mC.batchRename();
					}
				});
				renameThread.start();
				break;
			case RELOAD:
				Thread reloadThread = new Thread(new Runnable() {
					@Override
					public void run() {
						mC.reload();
					}
				});
				reloadThread.start();				
				break;
			case STATISTIC:
				Thread statisticThread = new Thread(new Runnable() {
					@Override
					public void run() {
						mC.stats();
					}
				});
				statisticThread.start();
				break;
			case UPDATE_NAMES:
				Thread updateThread = new Thread(new Runnable() {
					@Override
					public void run() {
						mC.updateNames();
					}
				});
				updateThread.start();
			}

		}

	}
}