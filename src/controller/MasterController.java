package controller;

import java.util.Observable;
import java.util.Observer;

import enums.FrameState;
import gui.ShortsFrame;
import model.InfoModel;
import model.LoadingModel;
import model.RenameModel;
import screen.ErrorScreen;
import screen.LoadingScreen;
import screen.MainScreen;
import screen.RenameInfoScreen;
import screen.RenameScreen;
import screen.SyncScreen;
import screen.UpdateScreen;
import swingPanel.SwingErrorPanel;
import swingPanel.SwingInfoPanel;
import swingPanel.SwingLoadingPanel;
import swingPanel.SwingMainPanel;
import swingPanel.SwingRenameInfoPanel;
import swingPanel.SwingRenamePanel;
import swingPanel.SwingSyncPanel;
import swingPanel.SwingUpdatePanel;

public class MasterController implements Observer {

	private FrameStateManager frameStateManager;
	private InfoModel infoModel;
	

	public MasterController(FrameStateManager frameStateManager) {
		this.frameStateManager = frameStateManager;
		initSplaschScreen();

	}

	public void init() {
		initUI();
	}

	public FrameStateManager getFrameStateManager() {
		return frameStateManager;
	}
	
	public InfoModel getInfoModel(){
		return infoModel;
	}


	private void initSplaschScreen() {
		infoModel = new InfoModel(new ShortsFrame());
		MainController mainController = new MainController(this);
		MainScreen mainScreen = new MainScreen(new SwingMainPanel(), infoModel, mainController);
		frameStateManager.addFrameState(FrameState.MAIN_MENU, mainScreen);
		frameStateManager.requestFrameState(FrameState.MAIN_MENU);
	}

	private void initUI() {
		RenameModel renameModel = new RenameModel();

		LoadingModel loadingModel = new LoadingModel();
		LoadingController loadingController = new LoadingController(this);
		LoadingScreen loadingScreen = new LoadingScreen(new SwingLoadingPanel(), loadingModel, loadingController);
		frameStateManager.addFrameState(FrameState.LOADING, loadingScreen);

		SyncController syncController = new SyncController(this);
		SyncScreen syncScreen = new SyncScreen(new SwingSyncPanel(), infoModel, syncController);
		frameStateManager.addFrameState(FrameState.SYNC, syncScreen);

		ErrorController errorController = new ErrorController(this);
		ErrorScreen errorScreen = new ErrorScreen(new SwingErrorPanel(), infoModel, errorController);
		frameStateManager.addFrameState(FrameState.ERROR, errorScreen);

		InfoController infoController = new InfoController(this);
		InfoScreen infoScreen = new InfoScreen(new SwingInfoPanel(), infoModel, infoController);
		frameStateManager.addFrameState(FrameState.INFO, infoScreen);

		RenameController renameController = new RenameController(this);
		RenameScreen renameScreen = new RenameScreen(new SwingRenamePanel(), renameModel, renameController);
		frameStateManager.addFrameState(FrameState.RENAME, renameScreen);

		RenameInfoScreen renameInfoScreen = new RenameInfoScreen(new SwingRenameInfoPanel(), renameModel, renameController);
		frameStateManager.addFrameState(FrameState.RENAMEINFO, renameInfoScreen);

		UpdateController updateController = new UpdateController(this);
		UpdateScreen updateScreen = new UpdateScreen(new SwingUpdatePanel(), infoModel, updateController);
		frameStateManager.addFrameState(FrameState.UPDATE, updateScreen);
	
        

	}
	

	@Override
	public synchronized void update(Observable observable, Object o) {
		frameStateManager.getCurrentScreen().getController().update(observable, o);
	}
}
