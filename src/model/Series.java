package model;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;

import enums.DataType;
import enums.ShowStatus;
import logic.InfoIO;

public class Series {
	private String seriesName;
	private String premiere = "";
	private String end = "";
	private String shortName = "";
	private int seasonCount;
	private ShowStatus showStatus = ShowStatus.NONE;
	private File location;
	private ArrayList<Season> seasons;
	private int totalCount;
	private boolean episodeNameNeeded;
	private double folderSize;
	private Episode firstNameFileEpisode;

	public Series(String seriesName, String shortName, String seasonCount, String episodeNameNeeded) {
		showStatus = ShowStatus.NONE;
		this.seriesName = seriesName;
		this.seasonCount = Integer.parseInt(seasonCount);
		this.shortName = shortName;

		this.episodeNameNeeded = (episodeNameNeeded.equals("Y")) ? true : false;
		seasons = new ArrayList<Season>();

		seasons.add(new Season(seriesName, this.seasonCount));
	}

	public Series(File location) {
		showStatus = ShowStatus.NONE;
		seasons = new ArrayList<Season>();
		this.seriesName = location.getName();
		this.location = location;
		loadSeasons();
		setSeasonCount();
		setTotalCount();
		linkSeasons();
		setFolderSize();
	}

	private void loadSeasons() {
		File[] sea = location.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return !file.isHidden() && (file.getName().startsWith("Season") || file.isFile());
			}
		});
		if (sea[0].isFile())
			seasons.add(new Season(location, seriesName, 1));
		else {
			Arrays.sort(sea);
			for (File s : sea) {
				try {
					seasons.add(new Season(s, seriesName,
							Integer.parseInt(s.getName().substring(s.getName().length() - 2))));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void linkSeasons() {
		for (int i = 0; i < seasons.size() - 1; i++) {
			Episode previous = new Episode();
			Episode after = new Episode();
			Season s1 = seasons.get(i);
			Season s2 = seasons.get(i + 1);
			try {
				previous = s1.getEpisodesAsSortedList().get(s1.getEpisodesAsSortedList().size() - 1);
				after = s2.getEpisodesAsSortedList().get(0);
				s1.getEpisodesAsSortedList().get(s1.getEpisodesAsSortedList().size() - 1).setAfter(after);
				s2.getEpisodesAsSortedList().get(0).setPrevious(previous);
			} catch (IndexOutOfBoundsException e) {

			}

		}
	}

	public void setInfo(String[] info) {
		try {
			premiere = info[1];
			end = info[2];
			setShowStatusFromString(info[3]);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

	}

	public void changeStatusData(DataType type, String string) {
		switch (type) {
		case END:
			end = string;
			break;
		case NAME:
			seriesName = string;
			break;
		case PREMIERE:
			premiere = string;
			break;
		case STATUS:
			setShowStatusFromString(string);
			break;
		case ALL:
			String[] splits = string.split("#");
			seriesName = splits[0];
			premiere = splits[1];
			end = splits[2];
			setShowStatusFromString(splits[3]);
		default:
			break;
		}

		InfoIO.updateStatusSheet(new String[] { seriesName, premiere, end, showStatus.toString() });
	}

	private void setShowStatusFromString(String status) {
		ShowStatus old = showStatus;
		switch (status) {
		case "Ended":
			showStatus = ShowStatus.ENDED;
			break;
		case "Hiatus":
			showStatus = ShowStatus.HIATUS;
			break;
		case "In Season":
			showStatus = ShowStatus.INSEASON;
			break;
		default:
			showStatus = ShowStatus.NONE;
		}
		if (!old.equals(showStatus)) {
			if (showStatus.equals(ShowStatus.ENDED) && end.equals("")) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -1);

				end = (dateFormat.format(cal.getTime()));
			}

		}
	}

	public void addNamesForNewEpisodes(Series s) {
		shortName = s.getShort();
		episodeNameNeeded = s.getEpisodeNameNeeded();

		for (Season newSeason : s.getSeasons()) {
			Season season = getSeason(newSeason.getSeasonNR());
			if (season.getLocation() == null) {
				seasons.add(newSeason);
			}
			for (Episode e : newSeason.getEpisodesAsSortedList()) {
				if (season.getEpisdoes().containsKey(e.getEpisodeNR()) && season.getEpisdoes().get(e.getEpisodeNR()).fileExists())
					continue;
				if(season.getEpisdoes().containsKey(e.getEpisodeNR()-1) && season.getEpisdoes().get(e.getEpisodeNR()-1).getIsMulti())
					continue;
				season.getEpisdoes().put(e.getEpisodeNR(), e);
			}
			season.linkEpisodes();
		}
		linkSeasons();
	}

	public Season getSeason(int nr) {
		for (Season s : seasons) {
			if (s.getSeasonNR() == nr)
				return s;
		}
		return new Season(seriesName, nr);
	}

	public Season getCurrentSeason() {
		return seasons.get(seasons.size() - 1);
	}

	public String getSeriesName() {
		return seriesName;
	}

	public void setPreiere(String premiere) {
		this.premiere = premiere;
	}

	public String getPremiere() {
		return premiere;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getEnd() {
		return end;
	}

	public ShowStatus getShowStatus() {
		return showStatus;
	}

	public File getLocation() {
		return location;
	}

	public ArrayList<Season> getSeasons() {
		return seasons;
	}

	public void setLocation(File location) {
		this.location = location;
	}

	public void setSeasons(ArrayList<Season> seasons) {
		this.seasons = seasons;
	}

	public int getSeasonCount() {
		return seasonCount;
	}

	public void setSeasonCount() {
		seasonCount = seasons.size();
	}

	public int getTotalCount() {
		return totalCount;
	}

	private void setTotalCount() {
		int c = 0;
		for (Season s : seasons)
			c += s.getEpisodeCount();
		totalCount = c;
	}

	public Episode getLastEpisode() {
		Season s = seasons.get(seasons.size() - 1);
		return s.getLastEpisode();
	}

	public boolean getEpisodeNameNeeded() {
		return episodeNameNeeded;
	}

	public void setHasEpisodesWithoutName(boolean hasEpisodesWithoutName) {
		this.episodeNameNeeded = hasEpisodesWithoutName;
	}

	public String getShort() {
		return shortName;
	}

	public void setShort(String shortName) {
		this.shortName = shortName;
	}

	private void setFolderSize() {
		double size = FileUtils.sizeOfDirectory(location);
		folderSize = (int) ((size / (1024 * 1024 * 1024)) * 100) / 100.0;
	}

	public double getSizeOfFolder() {
		return folderSize;
	}

	public Episode getLastExistingFile() {
		for (int s = seasons.size() - 1; s >= 0; s--) {
			ArrayList<Episode> episodes = seasons.get(s).getEpisodesAsSortedList();
			for (int i = episodes.size() - 1; i >= 0; i--) {
				if (episodes.get(i).fileExists())
					return episodes.get(i);
			}
		}
		return null;
	}

	public void setFirstNameFileEpisode(int episodeNR, int seasonNR, String episodeName) {
		firstNameFileEpisode = new Episode(seriesName, seasonNR, episodeNR, episodeName);
	}

	public Episode getFirstNameFileEpisode() {
		return firstNameFileEpisode;
	}
}
