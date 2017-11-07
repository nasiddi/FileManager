package logic;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import controller.FrameStateManager;
import enums.Constants;
import model.Episode;
import model.InfoModel;
import model.RenameModel;
import model.Season;
import model.Series;

public class BatchRename {

	private String[] episodes;
	private File[] show;
	private int numberOfSeasons;
	private RenameModel renameModel;
	private String seriesName;
	private String separationSymbols;
	private String statusTag;
	private HashMap<Integer, Season> episodeList;
	private HashMap<Integer, String> overview;
	private ArrayList<File> seasonFolders;
	private HashMap<String, Series> series;
	private boolean seriesOnServer;

	@SuppressWarnings("rawtypes")
	public BatchRename(FrameStateManager frameStateManager, InfoModel infoModel) throws IOException {
		renameModel = (RenameModel) frameStateManager.getCurrentScreen().getModel();
		series = infoModel.getSeries();
	}

	public void run() throws IOException {
		seriesName = renameModel.getSeriesName();
		separationSymbols = renameModel.getSeparationSymbols();
		statusTag = renameModel.getStatusTag();
		episodeList = new HashMap<>();
		overview = new HashMap<>();
		seriesOnServer = false;
		
		Thread renameThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					addFolderNameToFileName();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		renameThread.start();
		try {
			renameThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!loadFiles())
			return;

		if (seriesOnServer)
			prepareNamesFromServer();
		else
			prepareNamesFromFile();

		numberOfSeasons = Collections.max(episodeList.keySet());
		findMatches();
		renameFiles();
		renameModel.setRenameOverview(sortData(overview));

	}

	private void prepareNamesFromServer() {
		ArrayList<Season> seasons = series.get(seriesName).getSeasons();
		for (Season s : seasons) {
			int seasonNR = s.getSeasonNR();
			for (Episode e : s.getEpisodesAsSortedList()) {
				if (!episodeList.containsKey(seasonNR))
					episodeList.put(seasonNR, new Season(seriesName, seasonNR));
				String eName = e.getEpisodeName();
				String[] names = eName.split("( & )");
				if (e.getIsMulti()) {
					if (eName.contains("&")) 
						episodeList.get(seasonNR).addEpisode(e.getEpisodeNR() + 1, names[1]);
					else
						episodeList.get(seasonNR).addEpisode(e.getEpisodeNR()+1, names[0]);
				}
				episodeList.get(seasonNR).addEpisode(e.getEpisodeNR(), names[0]);
			}
		}
	}

	private boolean loadFiles() throws IOException {

		String sh = (Constants.PREPDIR + "/" + seriesName + ".txt");
		String s = "";

		File file = new File(Constants.PREPDIR + "/" + seriesName);

		if (!file.exists()) {
			renameModel.setStatusText("Folder not found");
			return false;
		}

		show = file.listFiles();

		Arrays.sort(show);

		try {
			s = new String(readAllBytes(get(sh)));

		} catch (NoSuchFileException ex) {
			if (series.containsKey(seriesName)) {
				seriesOnServer = true;
				return seriesOnServer;
			}

			renameModel.setStatusText("Name not correct");
			return false;

		}
		episodes = s.split("[\\r\\n]+");
		return true;
	}

	private void prepareNamesFromFile() {
		for (String e : episodes) {
			e = e.replaceAll("\u00C2", "");
			e = e.trim();
			if (e.indexOf(".") == 1) {
				e = "0" + e;
			}
			try {

				Integer.parseInt(e.substring(3, 5));

			} catch (NumberFormatException ex) {

				e = e.replaceFirst("[.]", ".0");

			}
			int seasonNR = -1;
			int episodeNR = -1;
			try {
				seasonNR = Integer.parseInt(e.substring(0, 2));
				episodeNR = Integer.parseInt(e.substring(3, 5));

			} catch (NumberFormatException ex) {
				return;
			}
			if (!episodeList.containsKey(seasonNR))
				episodeList.put(seasonNR, new Season(seriesName, seasonNR));
			episodeList.get(seasonNR).addEpisode(episodeNR, e.substring(6));
		}
	}

	private void findMatches() {
		int unmatched = 0;
		Pattern pattern = null;
		pattern = Pattern.compile("[0-9][0-9][" + separationSymbols + "][0-9][0-9]");
		for (File ep : show) {
			String num = null;
			if (ep.isDirectory() || ep.isHidden())
				continue;

			Matcher m = pattern.matcher(ep.getName());

			if (m.find()) {
				num = m.group();

			} else {
				overview.put(unmatched, "Patter not found for: " + ep.getName() + "\n*****\n");
				continue;
			}

			int sn = Integer.parseInt(num.substring(0, 2));
			int es = Integer.parseInt(num.substring(3, 5));

			if (!episodeList.containsKey(sn)) {
				overview.put(unmatched, "No Season found for: " + ep.getName() + "\n*****\n");
				unmatched++;
				continue;
			}
			Episode e = null;
			try {
				e = episodeList.get(sn).getEpisdoes().get(es);
				e.addFile(ep);
			} catch (NullPointerException ex) {
				overview.put(unmatched, "No Match found for: " + ep.getName() + "\n*****\n");
				unmatched++;
				continue;
			}
			if (ep.getName().contains("#")) {
				unmatched = renameForTwoParter(unmatched, ep, e);
				e = episodeList.get(sn).getEpisdoes().get(es);
			}

		}

	}

	private void addFolderNameToFileName() {
		try {
			loadFiles();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (File f : show) {
			if (f.isDirectory()) {
				File[] files = f.listFiles();
				for (File file : files) {
					if (!(file.getName().contains("ample") || file.getName().contains("AMPLE")))
						file.renameTo(new File(f.getParent() + "/" + f.getName() + file.getName()));
				}
				f.delete();
			}
		}
		show = null;
	}

	private void renameFiles() {
		Collection<Season> seasons = episodeList.values();
		ArrayList<Episode> flatenedList = new ArrayList<Episode>();
		for (Season s : seasons) {
			flatenedList.addAll(s.getEpisdoes().values());
		}
		renameModel.setRenameSuccessful(true);
		for (Episode e : flatenedList) {
			for (File f : e.getFileList()) {

				int sn = e.getSeasonNR();
				String ext = f.getName().substring(f.getName().lastIndexOf("."));
				ArrayList<String> videoExt = InfoIO.loadInfoFile(Constants.EXTENTIONS);
				if (!videoExt.contains(ext) && !Constants.SUB_EXTENSIONS.contains(ext)) {
					continue;
				}

				File name;
				if (Constants.SUB_EXTENSIONS.contains(ext)) {
					name = new File(Constants.LOCALDIR + "/" + e.getCompiledFileNameWithoutExtention() + ext);
					sn *= 10;
				} else {
					name = new File(Constants.PREPDIR + "/" + seriesName + "/Season " + e.getSeasonNRasString() + "/"
							+ e.getCompiledFileNameWithoutExtention() + ext);
				}

				if (renameModel.getRenameConfirmed()) {
					if (!f.renameTo(name)) {
						renameModel.setRenameSuccessful(false);
					}
				} else {
					overview.put(sn * 100 + e.getEpisodeNR(),
							"Old Name: " + f.getName() + "\nNew Name: " + name + "\n*****\n");

				}
			}
		}
	}

	private void cleanUpAndMoveFiles() {
		File folder = new File(Constants.LOCALDIR + "/" + statusTag + " " + seriesName);
		if (!folder.mkdir()) {
			renameModel.setStatusText("Clean Up Failed");
			return;
		}
		try {
			for (File sf : seasonFolders)
				sf.renameTo(new File(folder + "/" + sf.getName()));
		} catch (Exception e) {
			renameModel.setRenameSuccessful(false);
			e.printStackTrace();
		}
		new File(Constants.PREPDIR + "/" + seriesName + ".txt").delete();

		Path rootPath = Paths.get(Constants.PREPDIR + "/" + seriesName);
		try {
			Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS).sorted(Comparator.reverseOrder()).map(Path::toFile)
					.forEach(File::delete);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int renameForTwoParter(int unmatched, File ep, Episode e) {
		e.setMulti(true);
		boolean sameName = false;
		String e1 = e.getEpisodeName();

		Episode ep2 = episodeList.get(e.getSeasonNR()).getEpisdoes().get(e.getEpisodeNR() + 1);
		if (ep2 == null) {
			overview.put(unmatched, "No second Episdoe found for TwoParter: " + ep.getName() + "\n*****\n");
			unmatched++;
			sameName = true;
			return unmatched;
		}

		String e2 = ep2.getEpisodeName();

		if (e1.contains("Part") && e2.contains("Part") && !sameName) {
			if (e1.substring(0, e1.indexOf("Part")).equals(e2.substring(0, e2.indexOf("Part")))) {
				e.setEpisdoeName(e1.substring(0, e1.lastIndexOf(":")));
				sameName = true;
			}
		}
		if (!sameName)
			e.setEpisdoeName((e1.contains("Part") ? e1.substring(0, e1.lastIndexOf(":")) : e1) + " & "
					+ (e2.contains("Part") ? e2.substring(0, e2.lastIndexOf(":")) : e2));
		episodeList.get(e.getSeasonNR()).getEpisdoes().remove(e.getEpisodeNR() + 1);
		return unmatched;
	}

	private String sortData(HashMap<Integer, String> overview) {
		ArrayList<Integer> sortedKeys = new ArrayList<Integer>(overview.keySet());
		Collections.sort(sortedKeys);
		String data = "";
		boolean subtitleReached = false;
		for (int s : sortedKeys) {
			if (!subtitleReached && s > 999) {
				data += "\n ****** SUBTITLES *****\n\n";
				subtitleReached = true;
			}
			data += overview.get(s);
		}
		return data;
	}

	private void generateFolders() {
		seasonFolders = new ArrayList<>();
		for (int a = 1; a <= numberOfSeasons; a++) {
			String nr = smallNR(a);
			File folder = new File(Constants.PREPDIR + "/" + seriesName + "/" + "Season " + nr);
			folder.mkdir();
			seasonFolders.add(folder);
		}
	}

	public void execute() throws IOException {
		try {
			generateFolders();
		} catch (Exception e) {
			e.printStackTrace();
		}
		renameFiles();
		if (renameModel.getRenameSuccessful())
			cleanUpAndMoveFiles();
	}

	private String smallNR(int n) {
		String nr = "" + n;
		if (n < 10) {
			nr = "0" + n;
		}
		return nr;
	}
}
