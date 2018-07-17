package model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import enums.Constants;

public class Episode {

	private int seasonNR;
	private File location;
	private File local;
	private String seriesName;
	private int episodeNR;
	private String episodeName = "";
	private String fileFormat = "";
	private boolean isMulti = false;
	private boolean isAnime = false;
	private String fileName;
	private Episode previous;
	private Episode after;
	private ArrayList<File> fileList = new ArrayList<File>();
	private double fileSize;

	public Episode(File local, String seriesName, boolean isMulti, String episodeName, int seasonNR, int episodeNR, boolean isAnime) {
		this.setLocal(local);
		this.seriesName = seriesName;
		this.isMulti = isMulti;
		this.seasonNR = seasonNR;
		this.episodeName = episodeName;
		this.episodeNR = episodeNR;
		this.isAnime = isAnime;
		fileName = getCompiledFileNameWithoutExtention() + local.getName().substring(local.getName().lastIndexOf("."));
		
		this.fileFormat = fileName.substring(fileName.lastIndexOf("."));
	}

	Episode(String seriesName, int seasonNR, int episodeNR, String episodeName) {
		this.seriesName = seriesName;
		this.seasonNR = seasonNR;
		this.episodeNR = episodeNR;
		this.episodeName = episodeName;
	}

	Episode(File location, String seriesName, int seasonNR) {
		this.location = location;
		fileName = location.getName();
		this.seriesName = seriesName;
		this.seasonNR = seasonNR;
		isAnime = (location.getAbsolutePath().contains("Anime")) ? true : false;
		setFileSize();
		setEpisodeNRFromFile();
		try {
			this.fileFormat = fileName.substring(fileName.lastIndexOf("."));
		} catch (StringIndexOutOfBoundsException e) {
		}
		setEpisodeNameFromFile();
		checkForTwoParter(fileName);
		setPrevious(new Episode());
		setAfter(new Episode());

		createTreeFile();
	}

	private void setFileSize() {
		double size = FileUtils.sizeOf(location);
		fileSize = (int) ((size / (1024 * 1024)) * 100) / 100.0;
		
	}

	public double getFileSize() {
		return fileSize;
	}

	public void setFileSize(double fileSize) {
		this.fileSize = fileSize;
	}

	private void createTreeFile() {
		String folder = (location.getAbsolutePath().contains("Anime")) ? "/Anime/" : "/Series/";

		File f = new File(Constants.TESTTREE + folder + seriesName + "/Season " + getSeasonNRasString() + "/"
				+ location.getName());
		if (f.exists())
			return;
		f.getParentFile().mkdirs();
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * NullEpisode
	 */
	public Episode() {
		seasonNR = 1;
		seriesName = Constants.NULLEPISODE;
		episodeNR = 0;
		episodeName = Constants.NULLEPISODE;
		isMulti = false;
		isAnime = false;
		fileName = Constants.NULLEPISODE;
		previous = new Episode(0);
		after = new Episode(0);

	}

	private Episode(int i) {
		seasonNR = 1;
		seriesName = Constants.NULLEPISODE;
		episodeNR = 0;
		episodeName = Constants.NULLEPISODE;
		isMulti = false;
		isAnime = false;
		fileName = Constants.NULLEPISODE;

	}

	public String getNumberAndName() {
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

	public String getCompiledFileNameWithoutExtention() {
		if(episodeName.equals(""))
			return getSeriesNameAnd01x01();
		else
			return getSeriesNameAnd01x01() + " - " + episodeName;
	}

	public String getSeriesNameAnd01x01() {
		return seriesName + " " + getSeasonNRasString() + "x" + getEpisodeNRasString()
				+ ((isMulti) ? " & " + getSeasonNRasString() + "x" + getMultiNRasString() : "");
	}

	private void setEpisodeNRFromFile() {
		String s = "";
		try {
			int x = fileName.indexOf('x', seriesName.length());
			s = (fileName.substring(x+1, (x + ((isAnime) ? 4 : 3))));
			episodeNR = Integer.parseInt(s);
		} catch (Exception e) {
			episodeNR = (isAnime)? 999:99;
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

	private void setEpisodeNameFromFile() {
		if (seriesName.length() + 7 < fileName.lastIndexOf("."))
			this.episodeName = fileName.substring(fileName.indexOf("-", seriesName.length()) + 2,
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

	public void addFile(File file) {
		fileList.add(file);
	}

	public ArrayList<File> getFileList() {
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
		int multiNR = episodeNR + 1;
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

	public int getSeasonNRFromFile() {
		int s = (isAnime)?999:99;
		int x = -1;
		try {
			x = fileName.indexOf('x', seriesName.length());
			String st = (fileName.substring(x-2, x));
			s = Integer.parseInt(st);
		} catch (Exception e) {
		}
		return s;
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

	public boolean fileExists() {
		return location != null;
	}

	void renameFile(String name) {
		location.renameTo(new File(location.getParentFile() + "/" + name));
		reloadData();

	}
}
