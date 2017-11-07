package model;

import enums.LoadingNotification;

public class LoadingModel extends Model {

	private String statusText;
	private String showText;

	public LoadingModel() {
		setStatusText("Status");
	}
	
	public void clearStatusLable(){
		statusText = "";
		showText = "";
		setChanged();
		notifyObservers(LoadingNotification.UPDATE);
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
		setChanged();
		notifyObservers(LoadingNotification.UPDATE);
	}

	public String getStatusText() {
		return statusText;
	}

	public String getShowText() {
		return showText;
	}
	
	public void displayError(){
		statusText = "Skyship couldn't be mounted";
		showText = "";
		setChanged();
		notifyObservers(LoadingNotification.ERROR);

	}
	

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
