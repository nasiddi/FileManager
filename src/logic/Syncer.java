package logic;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import enums.Constants;
import enums.DataType;
import enums.ShowStatus;
import enums.Type;
import model.Episode;
import model.InfoModel;
import model.Season;
import model.Series;

public class Syncer extends Thread {

	private File[] toCopy;
	private HashMap<String, Series> series;
	private ArrayList<Episode> episodes;
	private InfoModel model;
	private ArrayList<String> overview;
	private boolean failed;
	private ArrayList<Copy> readytoCopy;

	public Syncer(InfoModel model) {
		episodes = new ArrayList<>();
		overview = new ArrayList<>();
		readytoCopy = new ArrayList<>();
		this.series = model.getSeries();
		this.model = model;
		model.setStatusText("Syncing");
		loadLocal();
	}

	private void loadLocal() {
		File local = new File(Constants.LOCALDIR);
		toCopy = local.listFiles(new FileFilter() {
			public boolean accept(File file) {
				if (file.isHidden())
					file.deleteOnExit();
				return !file.isHidden();
			}
		});

		Arrays.sort(toCopy);
	}

	public void sync() {
		for (File copy : toCopy) {
			sortFiles(copy);
		}
		prepEpisdoes();
		execute();
		model.setSeries(series);
		model.displaySyncInfos(overview);
	}

	private void sortFiles(File copy) {
		Type type = Type.UNKNOWN;

		if (copy.isFile())
			type = Type.EPISODE;

		if (getTag(copy.getName()).equals("SD") || getTag(copy.getName()).equals("HD"))
			type = Type.MOVIE;
		if (!copy.isDirectory() && (Constants.SUB_EXTENSIONS.contains(getExtention(copy))))
			type = Type.SUB;
		if (copy.isDirectory())
			type = Type.SHOW;

		switch (type) {
		case SHOW:
			copyShow(copy);
			break;
		case EPISODE:
			findName(copy, type);
			break;
		case SUB:
			if (!findName(copy, type)) {
				copySub(null, copy);
			}
			break;
		case MOVIE:
			copyMovie(copy);
			break;
		case UNKNOWN:
		default:
			String e = copy.getName() + " ERROR Type unknown";
			overview.add(e);
			break;
		}
	}

	private void prepEpisdoes() {
		for (Episode e : episodes) {
			if (Constants.SUB_EXTENSIONS.contains(e.getFileFormat()))
				continue;

			if (e.getSeasonNR() == 1 && e.getEpisodeNR() == 1) {
				createNewShow(e);

			}
			Series s = series.get(e.getSeriesName());

			if (s.getSeason(e.getSeasonNR()).getLocation() == null) {
				createNewSeason(e, s);
			}

			Season sn = s.getSeasons().get(s.getSeasons().size() - 1);

			try {
				e.setLocation(new File(sn.getLocation().getPath() + "\\" + e.getCompiledFileNameWithoutExtention()
						+ e.getFileFormat()));
			} catch (NullPointerException ex) {
				String e2 = "ERROR Season " + add0(e.getSeasonNR()) + " of " + e.getSeriesName()
						+ " Folder not created";
				overview.add(e2);
				ex.printStackTrace();
				continue;
			}
			readytoCopy.add(new Copy(Paths.get(e.getLocal().getAbsolutePath()),
					Paths.get(e.getLocation().getAbsolutePath()), Type.EPISODE, e));
		}
	}

	private void execute() {
		model.setTotalProgress(readytoCopy.size());
		for (Copy c : readytoCopy) {
			boolean success = execute(c.from, c.to);
			switch (c.type) {
			case ANIME:
			case EPISODE:
				if (!success)
					break;
				Series show = series.get(c.episode.getSeriesName());
				show.getCurrentSeason().addEpisode(c.episode);

				c.episode.getLocal()
						.renameTo(new File(c.episode.getLocal().getParentFile() + "\\_" + c.episode.getFileName()));
				changeStatus(c.from.toFile(), show);
				break;
			case SHOW:
				Series s = new Series(c.to.toFile().getParentFile().getParentFile());
				s.changeStatusData(DataType.STATUS, c.tag);
				series.put(c.to.toFile().getParentFile().getParent(), s);
				break;
			case MOVIE:
			case SUB:
				if (success)
					c.from.toFile().delete();
				break;
			case UNKNOWN:
			default:
				break;

			}
		}

	}

	private void createNewSeason(Episode e, Series s) {
		File folder = new File(s.getLocation() + "\\" + "Season " + add0(e.getSeasonNR()));
		if (folder.mkdir()) {
			Season season = s.getSeason(e.getSeasonNR());
			season.setLocation(folder);
			s.setSeasonCount();
			String e1 = "*** Season " + add0(e.getSeasonNR()) + " of " + e.getSeriesName() + " created ***";
			overview.add(e1);
		}
	}

	private String add0(int i) {
		String s = Integer.toString(i);
		if (i < 10)
			s = "0" + i;
		return s;
	}

	private void createNewShow(Episode ep) {
		File folder = new File(Constants.SERIESDIR + "\\" + ep.getSeriesName() + "\\Season 01");
		if (folder.mkdirs()) {
			String e1 = "***New Show " + ep.getSeriesName() + " created***";
			overview.add(e1);
			Series s = new Series(folder.getParentFile());
			s.changeStatusData(DataType.ALL, ep.getSeriesName() + "###" + "In Season");
			series.put(ep.getSeriesName(), s);
		} else {
			String e2 = "ERROR New Showfolder for " + ep.getSeriesName() + " not created";
			overview.add(e2);
		}
	}

	private String getTag(String copyName) {
		try {
			return copyName.substring(0, 2);
		} catch (Exception e) {
			return "";
		}
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
		Series show = null;

		for (Series s : model.getSeriesAsSortedList()) {
			if (s.getShort().equals(copy.getName().substring(0, 2))) {
				show = s;
				break;
			}
		}

		if (show == null) {
			if (copy.getName().length() < 6) {
				String e1 = "Error: no Show found for " + copy.getName();
				overview.add(e1);
			}
			return false;
		}

		int episodeNR;

		try {
			if (copy.getName().substring(4, 5).matches("[0-9]"))
				type = Type.ANIME;
			episodeNR = Integer.parseInt(copy.getName().substring(2, (type.equals(Type.ANIME)) ? 5 : 4));

		} catch (Exception e) {
			return false;

		}

		if (!show.getCurrentSeason().getEpisdoes().containsKey(episodeNR)) {
			String e1 = "Error: no name found for " + copy.getName();
			overview.add(e1);
			return false;
		}

		Episode e = show.getCurrentSeason().getEpisdoes().get(episodeNR);
		
		if (copy.getName().contains("=") & e.fileExists()) {
			e.getLocation().delete();
		}
		
		if (type.equals(Type.ANIME))
			e.setAnime(true);
		if (!e.getEpisodeName().equals("") || !show.getEpisodeNameNeeded()) {

			File f = rename(copy, e);
			if (f != null) {
				if (type.equals(Type.SUB))
					copySub(copy, f);

				return true;
			}
		}

		String e1 = "Error: Episode not found " + copy.getName();
		overview.add(e1);
		return false;

	}

	private void changeStatus(File copy, Series series) {
		if (series.getLocation() != null && series.getLocation().exists()) {
			if (copy.getName().contains("+")) {
				series.changeStatusData(DataType.STATUS, ShowStatus.HIATUS.toString());
				String e1 = "*** Showstatus of " + series.getSeriesName() + " changed to Hiatus ***";
				overview.add(e1);
			} else if (copy.getName().contains("-")) {
				series.changeStatusData(DataType.STATUS, ShowStatus.ENDED.toString());
				String e1 = "*** Showstatus of " + series.getSeriesName() + " changed to Ended ***";
				overview.add(e1);
			} else if (!series.getShowStatus().equals(ShowStatus.INSEASON))
				series.changeStatusData(DataType.STATUS, ShowStatus.INSEASON.toString());
		}
	}

	private File rename(File copy, Episode e) {
		File f = null;
		if (copy.getName().contains("&")) {
			f = setNameForDoubleEpisode(e, copy);
		} else if (e.getEpisodeName().length() != 0) {
			f = setNameForSingleEpisode(e, copy);
		} else if (!series.get(e.getSeriesName()).getEpisodeNameNeeded())
			f = setNameForEpisodeWithoutName(e, copy);
		return f;
	}

	private File setNameForEpisodeWithoutName(Episode episode, File copy) {
		File f = new File(copy.getParent() + "/" + episode.getSeriesName() + " " + episode.getSeasonNRasString() + "x"
				+ episode.getEpisodeNRasString() + copy.getName().substring(copy.getName().lastIndexOf(".")));
		episodes.add(
				new Episode(copy, episode.getSeriesName(), false, "", episode.getSeasonNR(), episode.getEpisodeNR(), episode.getIsAnime()));
		return f;
	}

	private File setNameForSingleEpisode(Episode episode, File copy) {
		File f = new File(copy.getParent() + "/" + episode.getCompiledFileNameWithoutExtention()
				+ copy.getName().substring(copy.getName().lastIndexOf('.')));
		try {
			episodes.add(new Episode(copy, episode.getSeriesName(), false, episode.getEpisodeName(),
					episode.getSeasonNR(), episode.getEpisodeNR(), episode.getIsAnime()));
		} catch (Exception e) {
			return null;
		}
		return f;
	}

	private File setNameForDoubleEpisode(Episode episode, File copy) {
		File f;
		String epName = "";
		String e1 = episode.getNumberAndName();
		String e2 = episode.getAfter().getNumberAndName();

		if (e2.contains(Constants.NULLEPISODE) || episode.getAfter().getEpisodeName().equals(""))
			return null;

		if (e1.contains("Part"))
			epName = e1.substring(e1.indexOf('-') + 1, e1.lastIndexOf('P') - 1);
		else
			epName = e1.substring(e1.indexOf('-') + 1) + " &" + e2.substring(e2.indexOf('-') + 1);

		f = new File(copy.getParent() + "/" + episode.getSeriesName() + " " + episode.getSeasonNRasString() + "x"
				+ e1.substring(0, e1.indexOf(' ')) + " & " + episode.getSeasonNRasString() + "x"
				+ e2.substring(0, e2.indexOf('-') + 1) + epName + getExtention(copy));

		episodes.add(new Episode(copy, episode.getSeriesName(), true, epName, episode.getSeasonNR(),
				episode.getEpisodeNR(), episode.getIsAnime()));
		return f;

	}

	private void copyShow(File copy) {
		String tag = getTag(copy.getName());
		ShowStatus showStatus = setAction(tag);
		if (showStatus.equals(ShowStatus.NONE)) {
			String e1 = "Error: No Tag found for " + copy.getName();
			overview.add(e1);
			return;
		}
		copy = removeTagFromFile(copy);
		File[] localSeasons = FileLoader.loadFilesInFolder(copy.toString());

		File seriesRoot = new File(Constants.SERIESDIR + "/" + copy.getName());
		failed = false;
		seriesRoot.mkdir();
		for (File localSeason : localSeasons) {
			if (!localSeason.isDirectory())
				continue;

			File driveFolder = new File(seriesRoot + "/" + localSeason.getName());
			driveFolder.mkdir();

			File[] localEps = FileLoader.loadFilesInFolder(localSeason.toString());
			for (File localEp : localEps) {
				File driveEp = new File(driveFolder + "/" + localEp.getName());
				readytoCopy.add(new Copy(Paths.get(localEp.toURI()), Paths.get(driveEp.toURI())));
			}
		}
		readytoCopy.get(readytoCopy.size() - 1).type = Type.SHOW;
		readytoCopy.get(readytoCopy.size() - 1).tag = showStatus.toString();
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

		Path copyFrom = Paths.get(copy.getPath());

		Path copyTo = Paths.get(Constants.VIDEODRIVE + quality + "\\" + copy.getName());
		readytoCopy.add(new Copy(copyFrom, copyTo));

	}

	private boolean execute(Path copyFrom, Path copyTo) {
		model.setShowText(copyTo.getName(copyTo.getNameCount() - 1).toString());
		model.adjustProgress();
		failed = false;
		String e1 = "Copy: " + copyTo.toFile().getName();
		Thread worker = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Files.copy(copyFrom, copyTo);
				} catch (IOException e) {
					failed = true;
				} finally {
					model.setShowText("");

				}
			}
		});
		worker.start();
		try {
			worker.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		e1 = (failed) ? e1 + " FAILED" : e1 + " DONE";
		overview.add(e1);
		return !failed;
	}

	private void copySub(File copy, File f) {
		Path copyFrom = null;
		if (copy != null)
			copyFrom = Paths.get(copy.getPath());
		else
			copyFrom = Paths.get(f.getPath());
		Path copyTo = Paths.get(Constants.SUBDIR + f.getName());
		readytoCopy.add(new Copy(copyFrom, copyTo, Type.SUB));
	}

	public Map<String, Series> getSeries() {
		return series;
	}

}

class Copy {
	Path to;
	Path from;
	Type type = Type.UNKNOWN;
	Episode episode;
	String tag;

	Copy(Path from, Path to) {
		this.from = from;
		this.to = to;
	}

	Copy(Path from, Path to, Type type, Episode episode) {
		this.from = from;
		this.to = to;
		this.type = type;
		this.episode = episode;
	}

	public Copy(Path copyFrom, Path copyTo, Type type) {
		this.from = copyFrom;
		this.to = copyTo;
		this.type = type;
	}
}