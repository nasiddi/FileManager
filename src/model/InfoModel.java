package model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import enums.Constants;
import enums.DataType;
import enums.ErrorNotification;
import enums.SyncNotification;
import logic.InfoLoader;
import logic.Series;

public class InfoModel extends Model {
	HashMap<String, Series> series;
	private String statusText;
	private String showText;
	private String percent;
	private String[] errorNames;
	private boolean isNewWord;
	private String errorMessage;
	private ArrayList<ErrorNotification> actions;
	private String syncOverview;

	public InfoModel() {
		setStatusText("");
		setShowText("");
		actions = new ArrayList<ErrorNotification>();
	}

	public void clearStatusLable() {
		statusText = "";
		showText = "";
		setChanged();
		notifyObservers(ErrorNotification.UPDATE);
		notifyObservers(SyncNotification.UPDATE);
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

	public void setProgress(int percent) {
		this.percent = percent + "%";
		setChanged();
		notifyObservers(SyncNotification.UPDATE);

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

	public void addAction(ErrorNotification notification) {
		actions.add(notification);
	}

	public ArrayList<ErrorNotification> getAction() {
		return actions;
	}

	public void clearActions() {
		actions.clear();
	}

	public String getProgress() {
		return percent;
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

	

	

	public HashMap<String, Series> getNewEpisodeNames() {
		return InfoLoader.loadNamesInHashMap();
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	public void clearError() {
		clearActions();
		for (int i = 0; i < 3; i++) {
			errorNames[i] = "";
		}
	}

	public void saveNameChanges(ArrayList<String> newNames) {
		File file = new File("/Users/nadina/Dropbox/Stuff/Files/names.txt");

		File temp;
		try {
			temp = File.createTempFile("file", ".txt", file.getParentFile());

			String charset = "UTF-8";

			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(temp), charset));
			for (String s : newNames) {
				writer.println(s);
				System.out.println(s);

			}

			writer.close();

			file.delete();

			temp.renameTo(file);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

