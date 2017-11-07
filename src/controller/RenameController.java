package controller;

import java.io.IOException;
import java.util.Observable;

import enums.FrameState;
import enums.RenameNotification;
import enums.SyncNotification;
import logic.BatchRename;
import model.RenameModel;
import swingPanel.SwingRenameInfoPanel;
import swingPanel.SwingRenamePanel;

@SuppressWarnings("rawtypes")
public class RenameController extends Controller {

	private BatchRename batchRename;

	RenameController(MasterController masterController) {
		super(masterController);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof RenameNotification) {

			RenameModel model = (RenameModel) super.masterController.getFrameStateManager().getCurrentScreen()
					.getModel();
			switch ((RenameNotification) arg) {
			case CONFIRM_ENTRY:
				SwingRenamePanel panel = (SwingRenamePanel) o;

				model.setRenameInfo(panel.getRenameInfo());
				panel.reset();

				try {
					batchRename = new BatchRename(masterController.getFrameStateManager(), super.masterController.getInfoModel());
					batchRename.run();
				} catch (IOException e) {
					e.printStackTrace();
				}
				super.masterController.getFrameStateManager().requestFrameState(FrameState.RENAMEINFO);
				SwingRenameInfoPanel infoPanel = (SwingRenameInfoPanel) super.masterController.getFrameStateManager()
						.getCurrentScreen().getPanel();
				infoPanel.update(model, RenameNotification.DATA_COMPLIED);
				break;
			case CONFIRM_RENAME:
				try {
					model.setRenameConfirmed(true);
					batchRename.execute();
				} catch (IOException e) {
					e.printStackTrace();
				}
				SwingRenameInfoPanel iP = (SwingRenameInfoPanel) o;
				iP.update(model, RenameNotification.FINISHED);
				break;
			case QUIT:
				super.masterController.getFrameStateManager().requestFrameState(FrameState.MAIN_MENU);
				break;
			case SYNC:
				MasterController mC = super.masterController;
				Thread syncThread = new Thread(new Runnable() {
					@Override
					public void run() {
						reload();
						mC.getFrameStateManager().requestFrameState(FrameState.SYNC);
						mC.update(mC.getFrameStateManager().getCurrentScreen().getPanel(), SyncNotification.INIT);					}
				});
				syncThread.start();
				break;
			case ERROR:
				super.masterController.getFrameStateManager().getCurrentScreen().getPanel().update(model, RenameNotification.ERROR);
				break;
			default:
				break;
			}
		}
	}

}
