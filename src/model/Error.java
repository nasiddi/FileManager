package model;

import java.util.ArrayList;
import java.util.Observable;

import enums.Constants;
import enums.ErrorNotification;
import enums.ExceptionType;

public class Error extends Observable{
	private Episode episode;
	private String message;
	private String newBefore;
	private String newCurrent;
	private String current;
	private String newAfter;
	private ErrorNotification notification;
	private boolean seasonReloadNeeded;
	private boolean add;
	private ArrayList<ErrorNotification> actions;
	private String newWord;
	private Series show;
	private ExceptionType exceptionType;
	private String saveFile;

	
	public Error(Episode episode, String message, Series show){
		this.episode = episode;
		this.message = message;
		this.setShow(show);
		setCurrent(episode.getFileName());
		notification = ErrorNotification.ERRORFOUND;
		actions = new ArrayList<ErrorNotification>();
		setExceptionType(ExceptionType.SMALL);
	}
	
	public void executeActions(){
		seasonReloadNeeded = false;
		setSaveWord(false);
		for(ErrorNotification a : actions){
			switch(a){
			case ADD:
				setSaveWord(true);
				saveFile = Constants.DICTIONARYFILE;
				break;
			case DELETE_AFTER:
				episode.getAfter().getLocation().delete();
				seasonReloadNeeded = true;
				break;
			case DELETE_BEFORE:
				episode.getPrevious().getLocation().delete();
				seasonReloadNeeded = true;
				break;
			case DELETE_CURRENT:
				episode.getLocation().delete();
				seasonReloadNeeded = true;
				break;
			case RENAME_AFTER:
				episode.getAfter().renameFile(newAfter);
				seasonReloadNeeded = true;
				break;
			case RENAME_BEFORE:
				episode.getPrevious().renameFile(newBefore);
				seasonReloadNeeded = true;
				break;
			case RENAME_CURRENT:
				episode.renameFile(newCurrent);
				seasonReloadNeeded = true;
				break;
			case SMALLEXCEPTION:
				setSaveWord(true);
				saveFile = Constants.SMALLEXCEPTIONS;
				break;
			case NAMEEXCEPTION:
				setSaveWord(true);
				saveFile = Constants.NAMEEXCEPTIONS;
				break;
			case FORMATEXCEPTION:
				setSaveWord(true);
				saveFile = Constants.EXTENTIONS;
				break;
			default:
				break;
			
			}
		}
	}
	
	public String getSaveFile(){
		return saveFile;
	}

	public Episode getEpisode() {
		return episode;
	}

	public void setEpisode(Episode episode) {
		this.episode = episode;
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

	public String getBefore() {
		return episode.getPrevious().getFileName();
	}

	public void setBefore(String before) {
		episode.getPrevious().renameFile(before);
	}

	public String getAfter() {
		return episode.getAfter().getFileName();
	}

	public void setAfter(String after) {
		episode.getAfter().renameFile(after);
	}

	public String getMessage() {
		return message;
	}

	public ErrorNotification getNotification() {
		return notification;
	}

	public void setNotification(ErrorNotification notification) {
		this.notification = notification;
	}

	public boolean isSeasonReloadNeeded() {
		return seasonReloadNeeded;
	}

	public void setSeasonReloadNeeded(boolean seasonReloadNeeded) {
		this.seasonReloadNeeded = seasonReloadNeeded;
	}

	public ArrayList<ErrorNotification> getActions() {
		return actions;
	}

	public void setActions(ArrayList<ErrorNotification> actions) {
		this.actions = actions;
	}

	public String getNewAfter() {
		return newAfter;
	}

	public void setNewAfter(String newAfter) {
		this.newAfter = newAfter;
	}

	public String getNewCurrent() {
		return newCurrent;
	}

	public void setNewCurrent(String newCurrent) {
		this.newCurrent = newCurrent;
	}

	public String getNewBefore() {
		return newBefore;
	}

	public void setNewBefore(String newBefore) {
		this.newBefore = newBefore;
	}
	
	public void setNewNames(String newBefore, String newCurrent, String newAfter ){
		this.newAfter = newAfter;
		this.newBefore = newBefore;
		this.newCurrent = newCurrent;

	}

	public boolean isSaveWord() {
		return add;
	}

	public void setSaveWord(boolean add) {
		this.add = add;
	}

	public String getNewWord() {
		return newWord;
	}

	public void setNewWord(String newWord) {
		this.newWord = newWord;
	}

	public Series getShow() {
		return show;
	}

	public void setShow(Series show) {
		this.show = show;
	}

	public ExceptionType getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(ExceptionType exceptionType) {
		this.exceptionType = exceptionType;
	}
	
	public void addOrRemoveAction(ErrorNotification n) {
		if(n.equals(ErrorNotification.EXCEPTION)){
			switch(exceptionType){
			case FORMAT:
				n = ErrorNotification.FORMATEXCEPTION;
				break;
			case NAME:
				n = ErrorNotification.NAMEEXCEPTION;
				break;
			case SMALL:
				n = ErrorNotification.SMALLEXCEPTION;
				break;
			default:
				break;
			
			}
		}
		if (actions.contains(n))
			actions.remove(n);
		else
			actions.add(n);
	}
}