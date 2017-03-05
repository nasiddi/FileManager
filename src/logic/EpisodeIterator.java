package logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class EpisodeIterator {

	public static ArrayList<Episode> getAllEpisodesAsList(HashMap<String, Series> series) {
		ArrayList<Episode> episodes = new ArrayList<Episode>();
		Collection<Series> c = series.values();
		for (Series s : c) {
			ArrayList<Season> sea = s.getSeasons();
			for (Season se : sea) {
				episodes.addAll(se.getEpisodesAsSortedList());
			}
		}
		return episodes;
	}
}
