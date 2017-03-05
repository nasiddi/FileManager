package logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import org.apache.commons.io.FileUtils;

import controller.FrameStateManager;
import enums.DataType;
import enums.ShowStatus;
import enums.SyncNotification;
import enums.Type;
import model.InfoModel;

public class Syncer extends Thread {

	private File[] toCopy;
	private HashMap<String, String> shorts;
	private HashMap<String, Series> names;
	private HashMap<String, Series> series;
	private ArrayList<Episode> episodes;
	private InfoModel model;
	private boolean copying;
	private ArrayList<String> overview;
	private boolean failed;

	public Syncer(FrameStateManager frameStateManager) {
		episodes = new ArrayList<Episode>();
		overview = new ArrayList<String>();
		model = (InfoModel) frameStateManager.getCurrentScreen().getModel();
		this.series = model.getSeries();
		model.setStatusText("Syncing");
		shorts = InfoLoader.loadShorts();
		names = InfoLoader.loadNamesInHashMap();
		loadLocal();
	}

	public void loadLocal() {
		File local = new File("/Users/nadina/Series");
		toCopy = local.listFiles(new FileFilter() {
			public boolean accept(File file) {
				if (file.isHidden())
					file.deleteOnExit();
				return !file.isHidden();
			}
		});
	}

	public void sync() {
		for (File copy : toCopy) {
			sortFiles(copy);
		}
		copyEpisdoes();
		model.setSeries(series);
		model.displaySyncInfos(overview);
	}

	private void sortFiles(File copy) {
		Type type = Type.UNKNOWN;

		if (copy.isFile())
			type = Type.EPISODE;

		if (getTag(copy.getName()).equals("SD") || getTag(copy.getName()).equals("HD"))
			type = Type.MOVIE;

		if (getTag(copy.getName()).equals("RP"))
			type = Type.REPLACE;
		if (copy.getName().startsWith("Naruto") || copy.getName().startsWith("NS"))
			type = Type.ANIME;
		if (getTag(copy.getName()).equals("LS") || getTag(copy.getName()).equals("FN")
				|| (getTag(copy.getName()).equals("OG")))
			type = Type.STATUS;

		if (!copy.isDirectory() && (getExtention(copy).equals(".sub") || getExtention(copy).equals(".idx")
				|| getExtention(copy).equals(".srt")))
			type = Type.SUB;

		if (copy.isDirectory())
			type = Type.SHOW;

		switch (type) {
		case SHOW:
			copyShow(copy);
			break;
		case EPISODE:

		case ANIME:
			findName(copy, type);
			break;
		case SUB:
			if (!findName(copy, type)) {
				copySub(copy);
			}
			break;
		case MOVIE:
			copyMovie(copy);
			break;
		case UNKNOWN:
		default:
			String e = copy.getName() + " ERROR Type unknown";
			System.out.println(e);
			overview.add(e);
			break;
		}
	}

	private void copyEpisdoes() {
		for (Episode e : episodes) {
			if (e.getSeasonNR() == 1 && e.getEpisodeNR() == 1) {
				createNewShow(e);

			}
			Series s = series.get(e.getSeriesName());
			if (s == null) {
				String e1 = "Show for " + e.getFileName() + " not found";
				System.out.println(e1);
				overview.add(e1);
				continue;
			}
			Season sn = s.getSeasons().get(s.getSeasons().size() - 1);
			int sea = sn.getSeasonNR();

			if (sea > e.getSeasonNR()) {
				String e2 = "Season of " + e.getFileName() + " not correct";
				System.out.println(e2);
				overview.add(e2);
				continue;
			}

			if (sea + 1 == e.getSeasonNR()) {
				createNewSeason(e, s);
				sn = s.getSeasons().get(s.getSeasons().size() - 1);
			}
			e.setLocation(new File(sn.getLocation().getPath() + "/" + e.getFileName()));
			if (execute(Paths.get(e.getLocal().getAbsolutePath()), Paths.get(e.getLocation().getAbsolutePath()))) {
				series.get(e.getSeriesName()).getCurrentSeason().addEpisode(e);
			}
		}
	}

	private void createNewSeason(Episode e, Series s) {
		File folder = new File("/Volumes/Video/Series/" + e.getSeriesName() + "/" + "Season " + add0(e.getSeasonNR()));
		if (folder.mkdir()) {
			String e1 = "*** Season " + add0(e.getSeasonNR()) + " of " + e.getSeriesName() + "created ***";
			System.out.println(e1);
			overview.add(e1);
			s.getSeasons().add(new Season(folder, s.getSeriesName(), e.getSeasonNR()));
			s.changeStatusData(DataType.STATUS, "In Season");
		} else {
			String e2 = "ERROR Season " + add0(e.getSeasonNR()) + " of " + e.getSeriesName() + " Folder not created";
			System.out.println(e2);
			overview.add(e2);
		}
	}

	private String add0(int i) {
		String s = "" + i;
		if (i < 10)
			s = "0" + i;
		return s;
	}

	private void createNewShow(Episode ep) {
		File folder = new File("/Volumes/Video/Series/" + ep.getSeriesName() + "/Season 01");
		if (folder.mkdirs()) {
			String e1 = "***New Show " + ep.getSeriesName() + " created***";
			System.out.println(e1);
			overview.add(e1);
			Series s = new Series(folder.getParentFile());
			s.changeStatusData(DataType.ALL, ep.getSeriesName() + "###" + "In Season");
			series.put(ep.getSeriesName(), s);
		} else {
			String e2 = "ERROR New Showfolder for " + ep.getSeriesName() + " not created";
			System.out.println(e2);
			overview.add(e2);
		}
	}

	private String getTag(String copyName) {
		return copyName.substring(0, 2);
	}

	private String getExtention(File copy) {
		return copy.getName().substring(copy.getName().lastIndexOf("."));
	}

	private ShowStatus setAction(String tag) {
		ShowStatus action = ShowStatus.NONE;
		if (tag.equals(ShowStatus.HIATUS.getTag()))
			action = ShowStatus.HIATUS;

		if (tag.equals(ShowStatus.ENDED.getTag())) {
			action = ShowStatus.ENDED;
		}

		if (tag.equals(ShowStatus.INSEASON.getTag())) {
			action = ShowStatus.INSEASON;
		}
		return action;
	}

	private boolean findName(File copy, Type type) {
		String showName = shorts.get(copy.getName().substring(0, 2));
		int episodeNR;
		try {
			System.out.println(copy.getName().substring(2, (type.equals(Type.ANIME)) ? 5 : 4));
			System.out.println(type);
			episodeNR = Integer.parseInt(copy.getName().substring(2, (type.equals(Type.ANIME)) ? 5 : 4));
		} catch (Exception e) {

			return false;

		}

		ShowStatus action = ShowStatus.NONE;
		if (!names.containsKey(showName)) {
			String e1 = "Error: Show " + showName + " not found";
			System.out.println(e1);
			overview.add(e1);
			return false;
		}

		Series series = names.get(showName);
		if (!series.getCurrentSeason().getEpisdoes().containsKey(episodeNR)) {
			String e1 = "Error: no name found for " + copy.getName();
			System.out.println(e1);
			overview.add(e1);
			return false;
		}

		Episode e = series.getCurrentSeason().getEpisdoes().get(episodeNR);

		if (!e.getEpisodeName().equals("") || series.hasEpisodesWithoutName()) {

			if (rename(copy, showName, e.getSeasonNRasString(), series, e.getEpisodeNR(), type))
				return true;

		}
		String e1 = "Error: Episode not found " + copy.getName();
		System.out.println(e1);
		overview.add(e1);
		return false;
	}

	/**
	 * private void getDataFromFullFileName(File copy, int digit) { Series s;
	 * try { s = series.get(copy.getName().substring(0,
	 * copy.getName().indexOf("-", copy.getName().indexOf("x")) - (7 + digit)));
	 * } catch (StringIndexOutOfBoundsException ex) { s =
	 * series.get(copy.getName().substring(0,
	 * copy.getName().lastIndexOf(".")-(digit+))); } try { if
	 * (s.getSeasons().get(s.getSeasons().size() -
	 * 1).getEpisdoes().containsKey(copy.getName())) System.out.println("Copy: "
	 * + copy.getName() + " EXISTS"); } catch (NullPointerException e) {
	 * System.out.println("Error Filename of " + copy.getName() + " not correct"
	 * ); } }
	 */
	private boolean rename(File copy, String showName, String sea, Series series, int i, Type type) {
		boolean renameOK = false;
		Episode e = series.getCurrentSeason().getEpisdoes().get(i);
		if (copy.getName().contains("*") && i != 0) {
			e.setAfter(series.getCurrentSeason().getEpisdoes().get(i + 1));
			renameOK = setNameForDoubleEpisode(e, copy);
		} else if (e.getEpisodeName().length() != 0) {
			renameOK = setNameForSingleEpisode(e, copy, type);
		} else if (series.hasEpisodesWithoutName())
			renameOK = setNameForEpisodeWithoutName(e, copy, type);
		return renameOK;
	}

	private String setTag(String name, ShowStatus showStatus) {
		switch (showStatus) {
		case INSEASON:
			name = "OG " + name;
			return name;
		case HIATUS:
			name = "LS " + name;
			return name;
		case ENDED:
			name = "FN " + name;
			return name;
		case NONE:
		default:
			return name;

		}

	}

	private boolean setNameForEpisodeWithoutName(Episode episode, File copy, Type type) {
		File f = new File(copy.getParent() + "/" + episode.getSeriesName() + " " + episode.getSeasonNRasString() + "x"
				+ copy.getName().substring(2));
		if (copy.renameTo(f)) {
			copy = f;
			episodes.add(new Episode(copy, episode.getSeriesName(), false, "", episode.getSeasonNR(),
					episode.getEpisodeNR()));
			return true;
		}

		return false;

	}

	private boolean setNameForSingleEpisode(Episode episode, File copy, Type type) {
		File f = new File(copy.getParent() + "/" + episode.getCompiledFileNameWithoutExtention()
				+ copy.getName().substring(copy.getName().lastIndexOf(".")));
		if (copy.renameTo(f)) {
			copy = f;
			if (type.equals(Type.SUB))
				copySub(copy);
			else
				try {
					episodes.add(new Episode(copy, episode.getSeriesName(), false, episode.getEpisodeName(),
							episode.getSeasonNR(), episode.getEpisodeNR()));
				} catch (Exception e) {
					return false;
				}
			return true;
		}
		return false;
	}

	private boolean setNameForDoubleEpisode(Episode episode, File copy) {
		File f;
		String epName = "";
		String e1 = episode.getNumberAndName();
		String e2 = episode.getAfter().getNumberAndName();

		if (e1.contains("Part"))
			epName = e1.substring(e1.indexOf("-") + 1, e1.lastIndexOf("P") - 1);
		else
			epName = e1.substring(e1.indexOf("-") + 1) + " &" + e2.substring(e2.indexOf("-") + 1);

		f = new File(copy.getParent() + "/" + episode.getSeriesName() + " " + episode.getSeasonNRasString() + "x"
				+ e1.substring(0, e1.indexOf(" ")) + " & " + episode.getSeasonNRasString() + "x"
				+ e2.substring(0, e2.indexOf("-") + 1) + epName + getExtention(copy));

		if (copy.renameTo(f)) {
			copy = f;
			episodes.add(new Episode(copy, episode.getSeriesName(), true, epName, episode.getSeasonNR(),
					episode.getEpisodeNR()));
			return true;
		}
		return false;
	}

	private void copyShow(File copy) {
		String tag = getTag(copy.getName());
		ShowStatus showStatus = setAction(tag);
		if (showStatus.equals(ShowStatus.NONE)) {
			String e1 = "Error: No Tag found " + copy.getName();
			System.out.println(e1);
			overview.add(e1);
			return;
		}
		copy = removeTagFromFile(copy);
		if (series.containsKey(copy.getName())) {

			String e1 = "Show " + copy.getName() + " EXISTS";
			System.out.println(e1);
			overview.add(e1);
			return;
		}

		File copy_from_1 = copy;

		File copy_to_1 = new File("/Volumes/Video/Series/");
		model.setShowText("Copy new Show: " + copy.getName());
		copying = true;
		Thread progress = showProgress(copy_from_1, copy_to_1);
		String e1 = "Copy new Show: " + copy.getName();
		System.out.print(e1);
		failed = false;

		Thread worker = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					FileUtils.copyDirectoryToDirectory(copy_from_1, copy_to_1);
					System.out.println(" DONE");
				} catch (IOException e) {
					System.out.println(" EXISTS");
					failed = true;
				} finally {
					model.setShowText("");

				}
			}
		});
		worker.start();
		try {
			worker.join();
			copying = false;
			progress.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		e1 = (failed) ? e1 + " FAILED" : e1 + " DONE";
		overview.add(e1);
		Series s = new Series(new File(copy_to_1 + "/" + copy.getName()));
		s.changeStatusData(DataType.STATUS, showStatus.toString());
		series.put(copy.getName(), s);

	}

	private Thread showProgress(File copy_from_1, File copy_to_1) {

		Thread progress = new Thread(new Runnable() {

			@Override
			public void run() {
				long total = FileUtils.sizeOfDirectory(copy_from_1);
				while (copying) {

					File destination = new File(copy_to_1 + "/" + copy_from_1.getName());
					int percent = 0;
					if (destination.exists()) {
						double current = FileUtils.sizeOfDirectory(destination);
						percent = (int) (current * 100 / total);

					}
					model.setProgress(percent);

				}
			}
		});
		progress.start();
		return progress;
	}

	private File removeTagFromFile(File copy) {
		File f = new File(copy.getParent() + "/" + copy.getName().substring(3));
		copy.renameTo(f);
		return f;
	}

	private void copyMovie(File copy) {
		String quality = getTag(copy.getName());
		File f = removeTagFromFile(copy);
		copy.renameTo(f);
		copy = f;

		Path copy_from_1 = Paths.get(copy.getPath());

		Path copy_to_1 = Paths.get("/Volumes/Video/" + quality + "/" + copy.getName());
		execute(copy_from_1, copy_to_1);

	}

	private boolean execute(Path copy_from_1, Path copy_to_1) {
		model.setShowText(copy_from_1.getName(copy_from_1.getNameCount() - 1).toString());
		copying = true;
		failed = false;
		Thread progress = progress(copy_from_1, copy_to_1);
		String e1 = "Copy: " + copy_from_1.toFile().getName();
		System.out.print(e1);
		Thread worker = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Files.copy(copy_from_1, copy_to_1);
					System.out.println(" DONE");
					copying = false;
				} catch (IOException e) {
					System.out.println(" EXISTS");
					failed = false;
				} finally {
					model.setShowText("");

				}
			}
		});
		worker.start();
		try {
			worker.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (copying) {
			copying = false;
			try {
				progress.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		e1 = (failed) ? e1 + " FAILED" : e1 + " DONE";
		overview.add(e1);
		return !failed;
	}

	private Thread progress(Path copy_from_1, Path copy_to_1) {
		Thread progress = new Thread(new Runnable() {

			@Override
			public void run() {
				while (copying) {
					File original = copy_from_1.toFile();

					double total = original.length();
					File destination = copy_to_1.toFile();
					int percent = 0;
					if (destination.exists()) {
						double current = destination.length();
						percent = (int) (current * 100 / total);

					}
					model.setProgress(percent);

				}
			}
		});
		progress.start();
		return progress;
	}

	private void copySub(File copy) {
		Path copy_from_1 = Paths.get(copy.getPath());
		Path copy_to_1 = Paths.get("/Volumes/Temp/Subs/" + copy.getName());
		execute(copy_from_1, copy_to_1);

		if (copy.getName().startsWith("Game")) {
			copy_to_1 = Paths.get("/Users/nadina/Dropbox/Stuff/GoT06/" + copy.getName());
			execute(copy_from_1, copy_to_1);
		}
		copy.delete();

	}

	public HashMap<String, Series> getSeries() {
		return series;
	}

}

class Change {
	String[] eps;
	int i;

	Change(String[] eps, int i) {
		this.eps = eps;
		this.i = i;
	}
}