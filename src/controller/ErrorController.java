package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.concurrent.LinkedBlockingQueue;

import enums.Constants;
import enums.ErrorNotification;
import enums.FrameState;
import logic.ErrorSearchThread;
import logic.InfoIO;
import model.Error;
import model.InfoModel;
import model.Series;
import swingPanel.SwingErrorPanel;

public class ErrorController extends Controller {

	private SwingErrorPanel panel;
	private InfoModel infoModel;
	private ArrayList<Series> series;
	private ArrayList<ErrorSearchThread> threads;
	private int index;
	private ArrayList<ErrorSearchThread> activeThreads;
	private LinkedBlockingQueue<ErrorSearchThread> errorThreads;
	private boolean done;

	ErrorController(MasterController masterController) {
		super(masterController);
		infoModel = masterController.getInfoModel();

	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof ErrorNotification) {
			switch ((ErrorNotification) arg) {
			case ADD:
				break;
			case CONTINUE:
				ErrorSearchThread est = panel.getErrorSearchThread();
				Error error = est.getError();
				error.executeActions();
				if (error.isSeasonReloadNeeded()) {
					error.getShow().getSeason(error.getEpisode().getSeasonNR()).loadEpisodes();
				}
				if (error.isSaveWord()) {
					try {
						InfoIO.addNewWordToFile(error.getNewWord(), error.getSaveFile());
						ErrorSearchThread.addWordtoList(error.getNewWord(), error.getSaveFile());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				est.setNotification(ErrorNotification.RERUN);
				est.run();
				break;
			case DELETE_AFTER:
				break;
			case DELETE_BEFORE:
				break;
			case DELETE_CURRENT:
				break;
			case ERRORFOUND:
				errorThreads.add((ErrorSearchThread) o);
				if (errorThreads.size() == 1) {
					panel.update(errorThreads.peek(), arg);
				}
				break;
			case INIT:
				panel = (SwingErrorPanel) o;
				series = infoModel.getSeriesAsSortedList();
				prepareErrorSearch();
				try {
					startSearch();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case NEW_WORD:
				break;
			case NOERRORFOUND:

				break;
			case PATH:
				break;
			case QUIT:
				reset();
				super.masterController.getFrameStateManager().requestFrameState(FrameState.MAIN_MENU);
				break;
			case READY:
				break;
			case RENAME_AFTER:
				break;
			case RENAME_BEFORE:
				break;
			case RENAME_CURRENT:
				break;
			case RERUN:
				est = (ErrorSearchThread) o;
				if (est.getError() == null) {
					est.setNotification(ErrorNotification.NOERRORFOUND);
					errorThreads.poll();
					if (!errorThreads.isEmpty()) {
						panel.update(errorThreads.peek(), ErrorNotification.ERRORFOUND);
					} else if (done)
						update(null, ErrorNotification.QUIT);
				} else {
					panel.update(est, ErrorNotification.ERRORFOUND);
				}
				break;
			case UPDATE:
				break;
			default:
				break;

			}

		}
	}

	private void startSearch() throws InterruptedException {
		while (index < threads.size()) {
			while (activeThreads.size() <= 9) {
				if (index >= threads.size())
					break;
				threads.get(index).run();
				activeThreads.add(threads.get(index));
				index++;
			}

			Iterator<ErrorSearchThread> i = activeThreads.iterator();
			while (i.hasNext()) {
				ErrorSearchThread est = i.next();
				if (est.getNotification().equals(ErrorNotification.NOERRORFOUND)) {
					i.remove();
				}
			}

		}
		done = true;
		if (errorThreads.isEmpty()) {
			update(null, ErrorNotification.QUIT);
		}
	}

	private void reset() {
		cleanDictionary();
		series = null;
		threads = null;
		activeThreads = null;
		errorThreads = null;
		index = 0;
		done = false;
		
		
	}

	private void cleanDictionary(){
		Collections.sort(ErrorSearchThread.DIX);
		InfoIO.saveDictionary(ErrorSearchThread.DIX);
		}
		
		
		
		
	
	
	private void prepareErrorSearch() {
		ErrorSearchThread.generateBIGEXCEPTIONS();
		threads = new ArrayList<ErrorSearchThread>();
		activeThreads = new ArrayList<ErrorSearchThread>();
		errorThreads = new LinkedBlockingQueue<ErrorSearchThread>();
		for (Series s : series) {
			threads.add(new ErrorSearchThread(s, this));
		}
	}

}