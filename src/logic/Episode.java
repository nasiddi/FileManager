package logic;

import java.io.File;
import java.util.ArrayList;

import javax.annotation.Generated;

public class Episode {

	private int seasonNR;
	private File location;
	private File local;
	private String seriesName;
	private int episodeNR;
	private String episodeName = "";
	private String fileFormat;
	private boolean isMulti = false;
	private boolean isAnime = false;
	private String fileName;
	private Episode previous;
	private Episode after;
	private ArrayList<File> fileList;

	public Episode(File local, String seriesName, boolean isMulti, String episodeName, int seasonNR, int episodeNR) {
		fileName = local.getName();
		this.setLocal(local);
		this.seriesName = seriesName;
		this.isMulti = isMulti;
		this.seasonNR = seasonNR;
		this.episodeName = episodeName;
		this.episodeNR = episodeNR;
		this.fileFormat = fileName.substring(fileName.lastIndexOf("."));
	}
	
	public Episode(String seriesName, int seasonNR, int episodeNR, String episodeName){
		this.seriesName = seriesName;
		this.seasonNR = seasonNR;
		this.episodeNR = episodeNR;
		this.episodeName = episodeName;
		fileList = new ArrayList<File>();
	}

	public Episode(File location, String seriesName, int seasonNR) {
		this.setPrevious(previous);
		fileName = location.getName();
		this.location = location;
		this.seriesName = seriesName;
		this.seasonNR = seasonNR;
		isAnime = false;

		setEpisodeNRFromFile();
		this.fileFormat = fileName.substring(fileName.lastIndexOf("."));
		setEpisodeNameFromFile();
		checkForTwoParter(fileName);
		setPrevious(new Episode());
		setAfter(new Episode());
	}
	
	public Episode(){
		seasonNR = 1;
		seriesName = "nullEpisode";
		episodeNR = 0;
		episodeName = "nullEpisode";
		fileFormat = "nullEpisode";
		isMulti = false;
		isAnime = false;
		fileName = "nullEpisode";
		previous = new Episode(0);
		after = new Episode(0);
		
	}
	
	public Episode(int i){
		seasonNR = 1;
		seriesName = "nullEpisode";
		episodeNR = 0;
		episodeName = "nullEpisode";
		fileFormat = "nullEpisode";
		isMulti = false;
		isAnime = false;
		fileName = "nullEpisode";
		
		
	}
	
	public String getNumberAndName(){
		return getEpisodeNRasString() + " - " + episodeName;
	}

	public void reloadData() {
		setEpisodeNameFromFile();
		setEpisodeNRFromFile();
		fileName = location.getName();
	}

	private void checkForTwoParter(String fileName) {
		if (seriesName.length() + 15 > fileName.lastIndexOf("."))
			return;
		int adj = 0;
		if (isAnime)
			adj++;
		String doub = (String) fileName.subSequence((seriesName.length() + 12 + adj), (seriesName.length() + 15 + adj));
		if (!checkForInt(doub.substring(2)))
			doub = doub.substring(0, 2);
		try {
			int multi = Integer.parseInt(doub);

			if (multi == episodeNR + 1) {
				setMulti(true);
			}
		} catch (NumberFormatException ex) {
			// s is not an integer
		}
	}

	public File getLocation() {
		return location;
	}

	public String getSeriesName() {
		return seriesName;
	}

	public int getEpisodeNR() {
		return episodeNR;
	}

	public String getEpisodeName() {
		return episodeName;
	}

	public void setLocation(File location) {
		this.location = location;
	}

	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}
	
	public String getCompiledFileNameWithoutExtention(){
		return getSeriesNameAnd01x01() + " - " + episodeName;
	}
	
	public String getSeriesNameAnd01x01(){
		return seriesName + " " + getSeasonNRasString() + "x" + getEpisodeNRasString() + ((isMulti) ? " & " +getSeasonNRasString()+"x"+getMultiNRasString():"");
	}

	public void setEpisodeNRFromFile() {
		String s = (fileName.substring(seriesName.length() + 4, seriesName.length() + 7));
		if (!checkForInt(s.substring(2)))
			s = s.substring(0, 2);
		else
			isAnime = true;
		try{
		episodeNR = Integer.parseInt(s);
		}catch(NumberFormatException e){
			episodeNR = 99;
		}
	}

	public void setEpisdoeName(String episdoeName) {
		this.episodeName = episdoeName;
	}

	public String getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	private boolean checkForInt(String num) {
		try {
			Integer.parseInt(num);
			return true;

		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public boolean getIsMulti() {
		return isMulti;
	}

	public void setEpisodeNameFromFile() {
		if (seriesName.length() + 7 < fileName.lastIndexOf("."))
			this.episodeName = fileName.substring(fileName.indexOf("-", seriesName.length())+2,
					fileName.lastIndexOf("."));
	}

	public void setMulti(boolean isMulti) {
		this.isMulti = isMulti;
	}

	public File getLocal() {
		return local;
	}

	public void setLocal(File local) {
		this.local = local;
	}

	public void addFile(File file){
		fileList.add(file);
	}
	
	public ArrayList<File> getFileList(){
		return fileList;
	}
	public int getSeasonNR() {
		return seasonNR;
	}

	public void setSeasonNR(int seasonNR) {
		this.seasonNR = seasonNR;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Episode getPrevious() {
		return previous;
	}

	public void setPrevious(Episode previous) {
		this.previous = previous;
	}

	public Episode getAfter() {
		return after;
	}

	public void setAfter(Episode after) {
		this.after = after;
	}

	public String getEpisodeNRasString() {
		if (isAnime) {
			if (episodeNR < 10)
				return "00" + episodeNR;
			if (episodeNR < 100)
				return "0" + episodeNR;
		}
		if (episodeNR < 10) {
			return "0" + episodeNR;
		}
		
		return "" + episodeNR;
	}
	
	public String getMultiNRasString() {
		int multiNR = episodeNR+1;
		if (isAnime) {
			if (multiNR < 10)
				return "00" + multiNR;
			if (multiNR < 100)
				return "0" + multiNR;
		}
		if (multiNR < 10) 
			return "0" + multiNR;
		
		return "" + multiNR;
	}

	public String getSeasonNRasString() {
		if (seasonNR < 10) {
			return "0" + seasonNR;
		}
		return "" + seasonNR;
	}

	public boolean getIsAnime() {
		return isAnime;
	}

	public void setAnime(boolean isAnime) {
		this.isAnime = isAnime;
	}
}
