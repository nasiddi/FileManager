package controller;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;

import enums.FrameState;
import enums.LoadingNotification;
import enums.SyncNotification;
import enums.UpdateNameNotification;
import gui.StatFrame;
import logic.ErrorSearch;
import logic.FileLoader;
import logic.InfoLoader;
import logic.MountSkyship;
import logic.Syncer;
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
	private RenameModel renameModel;
	private LoadingModel loadingModel;
	private MainController mainController;
	private MainScreen mainScreen;
	private SyncController syncController;
	private SyncScreen syncScreen;
	private ErrorController errorController;
	private ErrorScreen errorScreen;
	private InfoController infoController;
	private InfoScreen infoScreen;
	private RenameController renameController;
	private RenameScreen renameScreen;
	private LoadingController loadingController;
	private LoadingScreen loadingScreen;
	private RenameInfoScreen renameInfoScreen;
	private UpdateController updateController;
	private UpdateScreen updateScreen;

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

	public void errorsearch() {
		mountAndLoad();
		frameStateManager.requestFrameState(FrameState.ERROR);
		ErrorSearch errorSearch = new ErrorSearch(frameStateManager);
		errorSearch.go();
		frameStateManager.requestFrameState(FrameState.MAIN_MENU);
	}
	
	public void updateNames(){
		mountAndLoad();
		frameStateManager.requestFrameState(FrameState.UPDATE);
		frameStateManager.getCurrentScreen().getPanel().update(infoModel, UpdateNameNotification.INIT);
	}

	public void sync() {
		mountAndLoad();
		frameStateManager.requestFrameState(FrameState.SYNC);
		frameStateManager.getCurrentScreen().getPanel().update(infoModel, SyncNotification.UPDATE);
		Syncer sync = new Syncer(frameStateManager);
		sync.sync();
	}

	private void mountAndLoad() {
		if(infoModel.getSeries() == null)
			reload();
	}

	private void mountSkyship() {
		LoadingModel model = (LoadingModel) frameStateManager.getCurrentScreen().getModel();
		model.setStatusText("Mounting Skyship");
		model.setShowText("");
		new MountSkyship();
		model.clearStatusLable();

	}

	public void stats() {
		mountAndLoad();
		new StatFrame(infoModel);
	}

	public void reload() {
		frameStateManager.requestFrameState(FrameState.LOADING);
		frameStateManager.getCurrentScreen().getPanel().update(loadingModel, LoadingNotification.UPDATE);
		mountSkyship();
		FileLoader fileLoader = new FileLoader(frameStateManager);
		
		infoModel.setSeries(fileLoader.getSeries());
		
		if(infoModel.getSeries() != null)
			frameStateManager.requestFrameState(FrameState.MAIN_MENU);
	}

	public void batchRename() {
		frameStateManager.requestFrameState(FrameState.RENAME);
	}

	private void initSplaschScreen() {
		infoModel = new InfoModel();
		mainController = new MainController(this);
		mainScreen = new MainScreen(new SwingMainPanel(), infoModel, mainController);
		frameStateManager.addFrameState(FrameState.MAIN_MENU, mainScreen);

		frameStateManager.requestFrameState(FrameState.MAIN_MENU);
	}

	private void initUI() {
		renameModel = new RenameModel();

		loadingModel = new LoadingModel();
		loadingController = new LoadingController(this);
		loadingScreen = new LoadingScreen(new SwingLoadingPanel(), loadingModel, loadingController);
		frameStateManager.addFrameState(FrameState.LOADING, loadingScreen);

		syncController = new SyncController(this);
		syncScreen = new SyncScreen(new SwingSyncPanel(), infoModel, syncController);
		frameStateManager.addFrameState(FrameState.SYNC, syncScreen);

		errorController = new ErrorController(this);
		errorScreen = new ErrorScreen(new SwingErrorPanel(), infoModel, errorController);
		frameStateManager.addFrameState(FrameState.ERROR, errorScreen);

		infoController = new InfoController(this);
		infoScreen = new InfoScreen(new SwingInfoPanel(), infoModel, infoController);
		frameStateManager.addFrameState(FrameState.INFO, infoScreen);

		renameController = new RenameController(this);
		renameScreen = new RenameScreen(new SwingRenamePanel(), renameModel, renameController);
		frameStateManager.addFrameState(FrameState.RENAME, renameScreen);

		renameInfoScreen = new RenameInfoScreen(new SwingRenameInfoPanel(), renameModel, renameController);
		frameStateManager.addFrameState(FrameState.RENAMEINFO, renameInfoScreen);

		updateController = new UpdateController(this);
		updateScreen = new UpdateScreen(new SwingUpdatePanel(), infoModel, updateController);
		frameStateManager.addFrameState(FrameState.UPDATE, updateScreen);
	}

	@Override
	public synchronized void update(Observable observable, Object o) {
		frameStateManager.getCurrentScreen().getController().update(observable, o);
	}
}
