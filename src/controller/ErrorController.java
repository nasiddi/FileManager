package controller;

import java.util.Observable;

import enums.ErrorNotification;
import enums.FrameState;
import enums.MainNotification;
import model.InfoModel;
import swingPanel.SwingErrorPanel;

public class ErrorController extends Controller {

	public ErrorController(MasterController masterController) {
		super(masterController);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof ErrorNotification) {
			InfoModel model = (InfoModel) super.masterController.getFrameStateManager().getCurrentScreen().getModel();
			SwingErrorPanel panel = (SwingErrorPanel) o;
			switch ((ErrorNotification) arg) {
			case ADD:
				panel.update(model, arg);
				break;
			case CONTINUE:
				panel.update(model, arg);
				break;
			case ERRORFOUND:
				panel.update(model, arg);
				break;
			case PATH:
				panel.update(model, arg);
				break;
			case QUIT:
				super.masterController.getFrameStateManager().requestFrameState(FrameState.MAIN_MENU);
				break;
			case RENAME_AFTER:
				panel.update(model, arg);
				break;
			case RENAME_BEFORE:
				panel.update(model, arg);
				break;
			case RENAME_CURRENT:
				panel.update(model, arg);
				break;
			case DELETE_AFTER:
				panel.update(model, arg);
				break;
			case DELETE_BEFORE:
				panel.update(model, arg);
				break;
			case DELETE_CURRENT:
				panel.update(model, arg);
				break;
			default:
				break;
			
			}
		}

	}

}
