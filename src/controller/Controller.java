package controller;

import java.io.File;
import java.util.Observer;

import enums.Constants;
import enums.FrameState;
import enums.LoadingNotification;
import model.InfoModel;
import model.Model;

public abstract class Controller<ModelType extends Model> implements Observer {

	protected static MasterController masterController;
	protected ModelType model; // NO_UCD (unused code)

	Controller(MasterController masterController) {
		this.masterController = masterController;
	}

	public void setModel(ModelType model) {
		this.model = model;
	}

	boolean reload() {
		File f = new File(Constants.SERIESDIR);
		InfoModel infoModel = masterController.getInfoModel();

		if (f.exists() && infoModel.areFilesLoaded())
			return true;

		if (f.exists() && !infoModel.areFilesLoaded()) {
			masterController.getFrameStateManager().requestFrameState(FrameState.LOADING);
			masterController.getFrameStateManager().getCurrentScreen().getController().update(null,
					LoadingNotification.INIT);
			return true;
		}
		
		if(!f.exists()){
			
		}
		
		if (!isLoaded(infoModel)) {
			masterController.getFrameStateManager().requestFrameState(FrameState.LOADING);
			masterController.getFrameStateManager().getCurrentScreen().getController().update(null,
					LoadingNotification.INIT);
			if (f.exists())
				return true;
			else
				return false;
		}

		return false;

	}

	private boolean isLoaded(InfoModel infoModel) {
		if (infoModel.getSeries() == null)
			return false;
		return true;
	}

}
