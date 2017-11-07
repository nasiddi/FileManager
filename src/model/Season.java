package model;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Season {

	private int seasonNR;
	private HashMap<Integer, Episode> episodes;
	private int episodeCount;
	private File location;
	private String seriesName;

	Season(File location, String seriesName, int seasonNR) {
		episodes = new HashMap<Integer, Episode>();
		this.seasonNR = seasonNR;
		this.location = location;
		this.seriesName = seriesName;
		if (location.exists())
			loadEpisodes();
		setEpisodeCount();
	}

	public Season(String seriesName, int seasonNR) {
		episodes = new HashMap<Integer, Episode>();
		this.seriesName = seriesName;
		this.seasonNR = seasonNR;
	}

	public void loadEpisodes() {
		episodes.clear();
		File[] eps = location.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return !file.isHidden() && !file.getName().equals("Specials");
			}
		});
		try {
			Arrays.sort(eps);
		} catch (NullPointerException e) {

		}

		for (int i = 0; i < eps.length; i++) {
			Episode e = new Episode(eps[i], seriesName, seasonNR);
			if (episodes.containsKey(e.getEpisodeNR())) {
				Episode ep = episodes.get(e.getEpisodeNR());
				ep.addFile(eps[i]);
			} else
				episodes.put(e.getEpisodeNR(), e);

		}
		linkEpisodes();

	}

	void linkEpisodes() {
		ArrayList<Episode> sortedList = getEpisodesAsSortedList();
		for (Episode ep : sortedList) {
			linkEpisode(ep);
		}
	}

	private void linkEpisode(Episode e) {
		Episode previous = episodes.get(e.getEpisodeNR() - 1);
		if (previous == null)
			previous = new Episode();
		previous.setAfter(e);
		e.setPrevious(previous);

		Episode after = episodes.get((e.getIsMulti()) ? e.getEpisodeNR() + 2 : e.getEpisodeNR() + 1);
		if (after == null)
			after = new Episode();
		after.setPrevious(e);
		e.setAfter(after);

	}

	public void addEpisode(Episode newEpisode) {
		Episode previous = episodes.get(newEpisode.getEpisodeNR() - 1);
		if (previous == null)
			previous = new Episode();
		previous.setAfter(newEpisode);
		newEpisode.setPrevious(previous);

		Episode after = episodes.get(newEpisode.getEpisodeNR() + 1);
		if (after == null)
			after = new Episode();
		after.setPrevious(newEpisode);
		newEpisode.setAfter(after);

		episodes.put(newEpisode.getEpisodeNR(), newEpisode);
	}

	public Episode getLastEpisode() {
		Episode e = new Episode();
		try {
			e = getEpisodesAsSortedList().get(getEpisodesAsSortedList().size() - 1);
		} catch (Exception ex) {
		}
		return e;
	}

	public int getEpisodeCount() {
		setEpisodeCount();
		return episodeCount;
	}

	private void setEpisodeCount() {
		Collection<Episode> ep = episodes.values();
		int c = 0;
		for (Episode e : ep) {
			if (e.fileExists()) {
				c++;
				if (e.getIsMulti())
					c++;
			}
		}
		this.episodeCount = c;
	}

	public int getSeasonNR() {
		return seasonNR;
	}

	public HashMap<Integer, Episode> getEpisdoes() {
		return episodes;
	}

	public File getLocation() {
		return location;
	}

	public String getSeriesName() {
		return seriesName;
	}

	public void setSeasonNR(int seasonNR) {
		this.seasonNR = seasonNR;
	}

	public void setEpisdoes(HashMap<Integer, Episode> episdoes) {
		this.episodes = episdoes;
	}

	public void setLocation(File location) {
		this.location = location;
	}

	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}

	public ArrayList<Episode> getEpisodesAsSortedList() {
		Collection<Episode> c = episodes.values();
		ArrayList<Episode> l = new ArrayList<Episode>(c);
		Collections.sort(l, new Comparator<Episode>() {
			public int compare(Episode i, Episode j) {
				int s1 = i.getEpisodeNR();
				int s2 = j.getEpisodeNR();
				return s1 - s2;
			}
		});
		return l;
	}

	public void addEpisode(int episodeNR, String episodeName) {
		episodes.put(episodeNR, new Episode(seriesName, seasonNR, episodeNR, episodeName));
	}

	public String getSeasonNRasString() {
		if (seasonNR < 10) {
			return "0" + seasonNR;
		}
		return "" + seasonNR;
	}
}
