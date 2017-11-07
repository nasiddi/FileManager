package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import enums.Constants;
import enums.ErrorNotification;
import enums.SyncNotification;
import gui.ShortsFrame;
import logic.InfoIO;

public class InfoModel extends Model {
	private HashMap<String, Series> series;
	private String statusText;
	private String showText;
	private String progress;
	private String[] errorNames;
	private boolean isNewWord;
	private String errorMessage;
	private ArrayList<ErrorNotification> actions;
	private String syncOverview;
	private ArrayList<ShowInfoFields> showFieldList;
	private int totalProgress;
	private int prog;
	private ShortsFrame shortsFrame;
	private boolean filesLoaded;

	public InfoModel(ShortsFrame shortsFrame) {
		setStatusText("");
		setShowText("");
		actions = new ArrayList<>();
		this.shortsFrame = shortsFrame;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
		setChanged();
		notifyObservers(ErrorNotification.UPDATE);
		notifyObservers(SyncNotification.UPDATE);
	}

	public void setShowText(String showText) {
		this.showText = showText;
		setChanged();
		notifyObservers(ErrorNotification.UPDATE);
		notifyObservers(SyncNotification.UPDATE);
	}

	public void adjustProgress() {
		this.progress = ++prog +"/"+ totalProgress;
		setChanged();
		notifyObservers(SyncNotification.UPDATE);

	}
	public void setTotalProgress(int totalProgress){
		prog = 0;
		this.totalProgress = totalProgress;
	}

	public void errorFound(String errorMessage, String[] names) {
		this.errorMessage = errorMessage;
		errorNames = names;
		setChanged();
		notifyObservers(ErrorNotification.ERRORFOUND);

	}

	public  void displaySyncInfos(ArrayList<String> overview){
		setSyncInfoOverView(overview);
		setChanged();
		notifyObservers(SyncNotification.DONE);
	}
	
	public void setBeforeName(String text) {
		errorNames[Constants.BEFORE] = text;
		addAction(ErrorNotification.RENAME_BEFORE);
	}

	public void setCurrentName(String text) {
		errorNames[Constants.CURRENT] = text;
		addAction(ErrorNotification.RENAME_CURRENT);
	}

	public void setAfterName(String text) {
		errorNames[Constants.AFTER] = text;
		addAction(ErrorNotification.RENAME_AFTER);
	}

	public void setIsNewWord(boolean isNewWord) {
		this.isNewWord = isNewWord;
	}

	private void addAction(ErrorNotification notification) {
		actions.add(notification);
	}

	public ArrayList<ErrorNotification> getAction() {
		return actions;
	}

	private void clearActions() {
		actions.clear();
	}

	public String getProgress() {
		return progress;
	}

	public String getStatusText() {
		return statusText;
	}

	public String getShowText() {
		return showText;
	}

	public void setSeries(HashMap<String, Series> series) {
		this.series = series;
	}

	public HashMap<String, Series> getSeries() {
		return series;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String[] getErrorNames() {
		return errorNames;
	}

	public boolean isNewWord() {
		return isNewWord;
	}
	
	public String getSyncOverView(){
		return syncOverview;
	}
	
	public void setSyncInfoOverView(ArrayList<String> strings){
		syncOverview = "";
		for(String s : strings)
			syncOverview += s + "\n";
		
	}

	public ArrayList<Series> getSeriesAsSortedList() {
		ArrayList<Series> ser = new ArrayList<Series>(series.values());
		Collections.sort(ser, new Comparator<Series>() {
			public int compare(Series i, Series j) {
				String s1 = i.getSeriesName();
				String s2 = j.getSeriesName();
				return s1.toLowerCase().compareTo(s2.toLowerCase());
			}
		});
		return ser;
	}

	
	@Override
	public void reset() {

	}


	public void setShowFieldList(ArrayList<ShowInfoFields> showFieldList) {
		this.showFieldList = showFieldList;
	}
	
	public ArrayList<ShowInfoFields> getShowFieldList(){
		return showFieldList;
	}
	
	public void reloadShortsFrame(){
		shortsFrame.initTable();
	}

	public void setFilesLoaded(boolean filesLoaded) {
		this.filesLoaded = filesLoaded;
		
	}
	
	public boolean areFilesLoaded(){
		return filesLoaded;
	}

}

