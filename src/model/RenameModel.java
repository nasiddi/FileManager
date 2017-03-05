package model;

import enums.RenameNotification;

public class RenameModel extends Model {
	private String seriesName = "";
	private String separationSymbols = "";
	private String statusTag = "";
	private String overview;
	private boolean renameConfirmed;
	private boolean renameSuccessful;
	private String statusText;

	public RenameModel() {
		statusText = "Batch Rename";
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setRenameInfo(String[] info) {
		seriesName = info[0];
		separationSymbols = info[1];
		statusTag = info[2];
	}

	public void resetEntryFields() {
		seriesName = "Series Name";
		separationSymbols = "Separation Symbols";
	}

	public String getSeriesName() {
		return seriesName;
	}

	public String getSeparationSymbols() {
		return separationSymbols;
	}

	public String getStatusTag() {
		return statusTag;
	}

	public void setRenameOverview(String overview) {
		this.overview = overview;
		setChanged();
		notifyObservers(RenameNotification.DATA_COMPLIED);
	}

	public String getRenameOverview() {
		return overview;
	}

	public void setRenameConfirmed(boolean renameCofirmed) {
		this.renameConfirmed = renameCofirmed;
	}

	public void setRenameSuccessful(boolean renameSuccessful) {
		this.renameSuccessful = renameSuccessful;
	}

	public boolean getRenameSuccessful() {
		return renameSuccessful;
	}

	public boolean getRenameConfirmed() {
		return renameConfirmed;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
