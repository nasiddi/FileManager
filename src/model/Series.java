package logic;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import enums.DataType;
import enums.ShowStatus;

public class Series {
	private String seriesName;
	private String premiere;
	private String end;
	private int seasonCount;
	private ShowStatus showStatus;
	private File location;
	private ArrayList<Season> seasons;
	private int totalCount;
	private boolean hasEpisodesWithoutName;

	public Series(String seriesName, String seasonCount, String hasEpisodesWithoutName) {
		showStatus = ShowStatus.NONE;
		this.seriesName = seriesName;
		this.seasonCount = Integer.parseInt(seasonCount);

		
		this.hasEpisodesWithoutName = (hasEpisodesWithoutName.equals("%")) ? true : false;
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
		ArrayList<ArrayList<Episode>> sea = new ArrayList<ArrayList<Episode>>();
		for (Season s : seasons)
			sea.add(s.getEpisodesAsSortedList());

	}

	public Series(File location, String premiere, String end, int count, int seasons, ShowStatus showStatus) {
		showStatus = ShowStatus.NONE;
		this.seriesName = location.getName();
		this.premiere = premiere;
		this.end = end;
		this.showStatus = showStatus;
		this.location = location;
		loadSeasons();
		setSeasonCount();
		linkSeasons();
	}

	public void loadSeasons() {
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

				seasons.add(
						new Season(s, seriesName, Integer.parseInt(s.getName().substring(s.getName().length() - 2))));
			}
		}

	}

	public void linkSeasons() {
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
				e.printStackTrace();

			}

		}
	}

	private void setLabel(ShowStatus showStatus) {
		int label = 0;
		switch (showStatus) {
		case HIATUS:
			label = 3;
			break;
		case ENDED:
			label = 4;
			break;
		case INSEASON:
			label = 6;
			break;
		default:
			break;
		}
		String[] name = location.toString().split("/");
		String path = "Mac HD";
		for (String s : name) {
			path += s + ":";
		}
		
		try {
			String[] cmd = { "osascript", "-e",
					"tell application \"Finder\" to set label index of folder \"" + path + "\" to " + label };

			Runtime.getRuntime().exec(cmd);

		} catch (IOException e) {
			System.out.println(e);
		}
		
		
		this.showStatus = showStatus;
	}

	public void setInfo(String[] info) {
		premiere = info[1];
		end = info[2];
		setShowStatusFromString(info[3]);
	}
	
	public void changeStatusData(DataType type, String string){
		switch(type){
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
		
		InfoLoader.updateStatusSheet(new String[]{seriesName, premiere, end, showStatus.nameToString()});
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
		if (!old.equals(showStatus)){
			setLabel(showStatus);
			
			if (showStatus.equals(ShowStatus.ENDED)) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -1);

				end = (dateFormat.format(cal.getTime()));
			}
			
			
		}
	}
	
	public Season getCurrentSeason() {
		return seasons.get(seasons.size() - 1);
	}

	public String getSeriesName() {
		return seriesName;
	}

	public String getPremiere() {
		return premiere;
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

	public void setTotalCount() {
		int c = 0;
		for (Season s : seasons)
			c += s.getEpisodeCount();
		totalCount = c;
	}

	public Episode getLastEpisode() {
		Season s = seasons.get(seasons.size() - 1);
		return s.getLastEpisode();
	}

	public boolean hasEpisodesWithoutName() {
		return hasEpisodesWithoutName;
	}

	public void setHasEpisodesWithoutName(boolean hasEpisodesWithoutName) {
		this.hasEpisodesWithoutName = hasEpisodesWithoutName;
	}

}
