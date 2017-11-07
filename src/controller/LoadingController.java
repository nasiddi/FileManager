package controller;

import java.util.Observable;

import enums.FrameState;
import enums.LoadingNotification;
import logic.FileLoader;
import logic.MountSkyship;
import model.LoadingModel;

public class LoadingController extends Controller {

	LoadingController(MasterController masterController) {
		super(masterController);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(Observable o, Object arg) {
		LoadingModel model = (LoadingModel) super.masterController.getFrameStateManager().getCurrentScreen().getModel();

		if (arg instanceof LoadingNotification) {
			switch ((LoadingNotification) arg) {
			case INIT:

			//	mountSkyship(model);
				loadData(model);

				break;
			case MAINMENU:
				super.masterController.getFrameStateManager().requestFrameState(FrameState.MAIN_MENU);
				break;
			case ERROR:
				break;
			case UPDATE:
				break;
			default:
				break;
			}

		}

	}

	public void mountSkyship(LoadingModel model) { // NO_UCD (unused code)
		model.setStatusText("Mounting Skyship");
		new MountSkyship();
		model.clearStatusLable();
	}

	private void loadData(LoadingModel model) {
		FileLoader loader = new FileLoader(model);
		
		super.masterController.getInfoModel().setSeries(loader.getSeries());
		super.masterController.getInfoModel().setFilesLoaded(loader.isFilesLoaded());

		update(null, LoadingNotification.MAINMENU);
	}

}
