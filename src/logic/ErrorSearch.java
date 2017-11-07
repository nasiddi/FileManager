package logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import enums.Constants;
import enums.ErrorNotification;
import model.Episode;
import model.InfoModel;
import model.Season;
import model.Series;

public class ErrorSearch extends Thread { // NO_UCD (unused code)

	private static final String[] exceptions = { "AM", "PM", "Christmas Special", "Compilation Episode",
			"Compilation Show", "The Unseen Bits", "Preliminary Peril", "Meeting", "Truth" };
	private ArrayList<String> names;
	private ArrayList<String> dictionary;
	private ArrayList<String> clone;
	private int seriesNR;
	private ArrayList<Series> series;
	private String currentShow;
	private HashMap<String, Series> seriesAsMap;
	private InfoModel model;
	private static final String[] WRONGSYMBOLS = { ":", "  ", "/" };
	private static final String[] SMALLCAPS = { "A", "An", "And", "As", "At", "By", "For", "From", "If", "In", "Into",
			"Of", "On", "Or", "The", "To", "Too", "Und", "Unto", "V.", "Vs.", "With" };

	public ErrorSearch(InfoModel model) {
		clone = new ArrayList<String>();
		series = model.getSeriesAsSortedList();
		seriesAsMap = model.getSeries();
		this.model = model;
		names = new ArrayList<String>();
		dictionary = new ArrayList<String>();
		try {
			loadDictionary();
		} catch (Exception e) {
			displayError(new Episode(), "Error: Couldn't load Dictionary", null);
		}
		clone.addAll(dictionary);
	}

	private void loadDictionary() throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(Constants.DICTIONARYFILE));
		String line;
		while ((line = br.readLine()) != null) {
			dictionary.add(line);

		}
		br.close();
	}

	public void go() {
		model.setStatusText("Searching for Errors");
		loopThroughSeries(series);
		model.setSeries(seriesAsMap);
		cleanDictionary();
	}

	private void loopThroughSeries(ArrayList<Series> ser) {

		for (seriesNR = 0; seriesNR < ser.size(); seriesNR++) {
			names.clear();
			Series show = ser.get(seriesNR);
			currentShow = show.getSeriesName();
			if (currentShow.equals("New Series"))
				continue;
			model.setShowText(currentShow);
			loopThroughSeasons(show);
		}
	}

	private void loopThroughSeasons(Series currentShow) {
		ArrayList<Season> seasons = currentShow.getSeasons();
		for (Season season : seasons) {
			if (season.getEpisdoes().isEmpty()) {
				String s = "ERROR: " + currentShow.getSeriesName() + "/Season " + season.getSeasonNRasString()
						+ " is empty";
				displayError(new Episode(), s, null);
				continue;
			}

			loopThroughEpisodes(season, currentShow);

		}
	}

	private void loopThroughEpisodes(Season season, Series currentShow) {
		int namesStartIndex = names.size();
		int start = 0;
		boolean seasonOK = false;
		int loops = -1;
		while (!seasonOK) {
			loops++;
			while (names.size() > namesStartIndex) {
				names.remove(names.size() - 1);
			}
			if (loops > 0)
				season.loadEpisodes();

			ArrayList<Episode> episodes = season.getEpisodesAsSortedList();
			for (Episode e : episodes) {

				if (!e.fileExists()) {
					if (e.getEpisodeNR() == 0 || e.getPrevious().getIsMulti())
						continue;
					Episode lastFile = currentShow.getLastExistingFile();
					if (e.getEpisodeNR()+e.getSeasonNR()*100 > lastFile.getEpisodeNR()+lastFile.getSeasonNR()*100) {
						seasonOK = true;
						break;
					} else {
						displayError(e, "File for " + e.getCompiledFileNameWithoutExtention() + " missing", null);
						break;
					}
				}
				if (e.equals(episodes.get(0))) {
					start = e.getEpisodeNR();
					if (start > 1) {
						if (!e.getSeriesName().equals("Doctor Who Classic")) {

							if (checkForNumberError(1, start, e))
								break;
						}
					}
				}
				if ((e.getLocation().isDirectory())) {
					String s = "Folder in Seasonfolder: " + e.getSeriesName() + "/Season " + e.getSeasonNRasString();
					displayError(e, s, null);
					break;
				}
				if (checkNumberFormat(e))
					break;

				int index = e.getEpisodeNR();

				if (checkSeriesName(e))
					break;

				if (checkForWrongSymbols(e))
					break;

				if (checkForSpaceAtTheEnd(e))
					break;

				if (checkForPartNumbers(e))
					break;
				if (!e.getSeriesName().equals("Doctor Who Classic")) {
					if (checkForNumberError(start, index, e)) {
						break;
					}
					start = index;
				}
				try {
					if (checkIfNameIsCorrect(e))
						break;
				} catch (IOException ex) {
					ex.printStackTrace();
				}

				if (!checkIfException(e))
					if (checkForNameUsedTwice(e))
						break;
				if (e.getIsMulti())
					start++;
				start++;
				if (e.equals(season.getLastEpisode()))
					seasonOK = true;
			}

		}
		if (loops > 0)
			seriesAsMap.get(season.getSeriesName()).getSeasons().set(season.getSeasonNR() - 1, season);

	}

	private boolean checkSeriesName(Episode e) {
		if (!e.getFileName().contains(e.getSeriesName())) {
			String s = "Seriesname of " + e.getFileName() + " not correct";
			File f = new File(e.getLocation().getParentFile() + "/" + e.getSeriesName() + " " + e.getSeasonNRasString()
					+ "x" + e.getEpisodeNRasString() + (e.getEpisodeName().equals("") ? ""
							: " - " + e.getEpisodeName() + e.getFileFormat()));
			return displayError(e, s, f);
		}
		return false;
	}

	private boolean checkIfNameIsCorrect(Episode episode) throws IOException {
		String name = episode.getFileName().substring(0, episode.getFileName().lastIndexOf("."));
		if(name.contains("\u00A0")){
			System.out.println(name);
		}
			
		name = name.replaceAll("[0-9][0-9]x[0-9]+", "::");
		String cap = "";
		if (name.equals(""))
			return false;
		boolean errorfound = false;
		String[] words = name.split(" ");
		for (int i = 0; i < words.length; i++) {
			boolean smallC = false;
			boolean capOK = false;
			for (String small : SMALLCAPS) {
				if (words[i].equals(small)) {
					smallC = true;
					capOK = true;
					if (i != 0 && !(words[i - 1].equals("-")
							|| words[i - 1].substring(words[i - 1].length() - 1).equals(".")
							|| words[i - 1].substring(words[i - 1].length() - 1).equals("!")
							|| words[i - 1].equals("&"))) {
						capOK = false;
						cap = words[i];
						words[i] = words[i].toLowerCase();
						break;
					}
				}
			}
			String w = words[i];
			if (smallC)
				w = cap;

			String errorMessage = "New Word: " + w + " (" + episode.getFileName() + ")";
			String conc = "";
			if (smallC) {

				for (int ii = 0; ii < words.length; ii++) {
					conc += words[ii];
					if (ii != words.length - 1)
						conc += " ";
				}
			}

			if (!smallC && dictionary.contains(words[i])) {
				clone.remove(words[i]);
				continue;
			} else {
				String newName = conc.replaceFirst("::",
						episode.getSeasonNRasString() + "x" + episode.getEpisodeNRasString());
				if (episode.getIsMulti())
					newName = conc.replaceFirst("::",
							episode.getSeasonNRasString() + "x" + episode.getMultiNRasString());

				File f = new File(episode.getLocation().getParent() + "/" + newName + episode.getFileFormat());
				model.setIsNewWord(true);
				boolean erf = false;
				if (!capOK)
					erf = displayError(episode, errorMessage, (smallC) ? f : null);
				if (!errorfound)
					errorfound = erf;
			}

		}
		model.setIsNewWord(false);
		return errorfound;
	}

	public void addNewWord(String word) throws IOException {
		BufferedWriter output = new BufferedWriter(new FileWriter(Constants.DICTIONARYFILE, true));
		output.append(word + "\n");
		dictionary.add(word);
		output.close();
	}

	public boolean displayError(Episode episode, String errorMessage, File file) {
		String[] names = new String[3];
		names[0] = episode.getPrevious().getFileName();
		names[1] = (file == null) ? episode.getFileName() : file.getName();
		names[2] = episode.getAfter().getFileName();

		model.errorFound(errorMessage, names);

		synchronized (this) {
			while (!model.getAction().contains(ErrorNotification.CONTINUE)) {
				try {
					this.wait(50);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		boolean errorCorrected = false;
		loop: for (ErrorNotification action : model.getAction()) {
			switch (action) {
			case ADD:
				String[] words = errorMessage.split(" ");
				try {
					addNewWord(words[2]);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case CONTINUE:
				break loop;
			case DELETE_AFTER:
				if (episode.getAfter().getEpisodeName().equals("nullEpisode"))
					break;
				episode.getAfter().getLocation().delete();
				episode.getAfter().getAfter().setPrevious(episode);
				episode.setAfter(episode.getAfter().getAfter());
				errorCorrected = true;
				break;
			case DELETE_BEFORE:
				if (episode.getPrevious().getEpisodeName().equals("nullEpisode"))
					break;
				episode.getPrevious().getLocation().delete();
				episode.getPrevious().getPrevious().setAfter(episode);
				episode.setPrevious(episode.getPrevious().getPrevious());
				errorCorrected = true;
				break;
			case DELETE_CURRENT:
				if (episode.getEpisodeName().equals("nullEpisode"))
					break;
				episode.getLocation().delete();
				episode.getAfter().setPrevious(episode.getPrevious());
				episode.getPrevious().setAfter(episode.getAfter());
				errorCorrected = true;
				break;
			case ERRORFOUND:
				break;
			case PATH:
				break;
			case QUIT:
				break;
			case RENAME_AFTER:
				if (episode.getAfter().getEpisodeName().equals("nullEpisode"))
					break;
				episode.getAfter().getLocation().renameTo(new File(episode.getAfter().getLocation().getParentFile()
						+ "/" + model.getErrorNames()[Constants.AFTER]));
				episode.getAfter().reloadData();
				errorCorrected = true;
				break;
			case RENAME_BEFORE:
				if (episode.getPrevious().getEpisodeName().equals("nullEpisode"))
					break;
				episode.getPrevious().getLocation().renameTo(new File(
						episode.getLocation().getParentFile() + "/" + model.getErrorNames()[Constants.BEFORE]));
				episode.getPrevious().reloadData();
				errorCorrected = true;
				break;
			case RENAME_CURRENT:
				if (episode.getEpisodeName().equals("nullEpisode"))
					break;
				episode.getLocation().renameTo(new File(
						episode.getLocation().getParentFile() + "/" + model.getErrorNames()[Constants.CURRENT]));
				episode.reloadData();
				errorCorrected = true;
				break;
			default:
				break;

			}
		}
		model.clearError();

		return errorCorrected;
	}

	private boolean checkForNameUsedTwice(Episode episode) {
		if (names.contains(episode.getEpisodeName()) && episode.getEpisodeName().length() > 2) {
			String s = "Duplicated Filename: " + episode.getFileName();
			return displayError(episode, s, null);
		} else {
			names.add(episode.getEpisodeName());
			return false;
		}
	}

	private boolean checkIfException(Episode episode) {
		String epi = episode.getEpisodeName();
		for (String item : exceptions) {

			if (epi.contains(item)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkNumberFormat(Episode episode) {
		episode.reloadData();
		boolean error = false;
		String epi = episode.getFileName();
		String last = "\\s-\\s";
		if (episode.getEpisodeName().equals("")) {
			last = "\\.";
			epi = epi.substring(episode.getSeriesName().length(), epi.lastIndexOf(".") + 1);
		} else
			epi = epi.substring(episode.getSeriesName().length(),
					epi.indexOf(episode.getEpisodeName(), episode.getSeriesName().length()));
		if (episode.getIsMulti()) {
			if (!epi.matches("\\s" + episode.getSeasonNRasString() + "x" + episode.getEpisodeNRasString() + "\\s&\\s"
					+ episode.getSeasonNRasString() + "x" + episode.getMultiNRasString() + last))
				error = true;
		} else {
			if (!epi.matches("\\s" + episode.getSeasonNRasString() + "x" + episode.getEpisodeNRasString() + last))
				error = true;
		}

		if (error) {
			String s = "Number Format of " + episode.getFileName() + " not correct";
			return displayError(episode, s, null);
		}
		return false;
	}

	private boolean checkForNumberError(int start, int index, Episode episode) {
		if (start == 0)
			return false;
		if (!(index == start)) {
			String s = "Missing episode(s): " + episode.getSeasonNRasString() + "x" + ((start < 10) ? "0" : "") + start
					+ ((index - start == 1) ? ""
							: " - " + episode.getSeasonNRasString() + "x" + ((index - 1 < 10) ? "0" : "")
									+ (index - 1));
			return displayError(episode, s, null);
		}
		return false;

	}

	private boolean checkForPartNumbers(Episode episode) {
		String epi = episode.getFileName();
		if (epi.contains("Part 1") || epi.contains("Part 2") || epi.contains("Part 3") || epi.contains("Part 4")
				|| epi.contains("Part 5")) {
			String s = "Partnumber ERROR: " + episode.getFileName();
			String temp = epi.substring(epi.lastIndexOf("Part "));
			String i = temp.substring(5, 6);
			int c = Integer.parseInt(i);
			String p = "";
			for (int o = 0; o < c; o++)
				p += "I";
			String part = "Part " + p;
			if (epi.charAt(epi.lastIndexOf("Part") - 2) == ',')
				epi = epi.replace(epi.charAt(epi.lastIndexOf("Part") - 2), Character.MIN_VALUE);
			epi = epi.replace("Part " + i, part);
			File f = new File(episode.getLocation().getParentFile() + "/" + epi);
			return displayError(episode, s, f);
		}
		return false;
	}

	private boolean checkForSpaceAtTheEnd(Episode episode) {
		String epi = episode.getFileName();
		if (epi.charAt(epi.lastIndexOf(".") - 1) == ' ') {
			File f = new File(episode.getLocation().getParent() + "/" + epi.substring(0, epi.lastIndexOf(".") - 1)
					+ epi.substring(epi.lastIndexOf(".")));
			String s = "Space at the End " + epi;
			return displayError(episode, s, f);
		}
		return false;
	}

	private boolean checkForWrongSymbols(Episode episode) {
		String epi = episode.getFileName();
		for (String symbol : WRONGSYMBOLS) {
			if (epi.contains(symbol)) {
				String s = "Wrong symbol *" + symbol + "* " + epi;
				return displayError(episode, s, null);
			}
		}
		return false;
	}

	public void cleanDictionary() {
		ArrayList<String> multiCheck = new ArrayList<String>();
		ArrayList<String> multiList = new ArrayList<String>();
		for (String s : dictionary) {
			if (!multiCheck.contains(s)) {
				multiCheck.add(s);
			} else {
				multiList.add(s);
			}

		}

		for (String s : clone) {
			multiCheck.remove(s);
		}
		File f = new File(Constants.DICTIONARYFILE);
		f.delete();
		try {
			f.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (String s : SMALLCAPS)
			multiCheck.remove(s);

		multiCheck.sort(String::compareTo);
		for (String s : multiCheck)
			try {
				addNewWord(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

}
