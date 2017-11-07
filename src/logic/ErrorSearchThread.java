package logic;

import java.util.ArrayList;
import java.util.Observable;

import org.apache.commons.lang3.StringUtils;

import controller.ErrorController;
import enums.Constants;
import enums.ErrorNotification;
import enums.ExceptionType;
import model.Episode;
import model.Error;
import model.Season;
import model.Series;

public class ErrorSearchThread extends Observable implements Runnable {
	private ArrayList<String> names;
	private static ArrayList<String> DICTIONARY = InfoIO.loadInfoFile(Constants.DICTIONARYFILE);
	private static ArrayList<String> NAMEEXCEPTIONS = InfoIO.loadInfoFile(Constants.NAMEEXCEPTIONS);
	private static ArrayList<String> SMALLEXCEPTIONS = InfoIO.loadInfoFile(Constants.SMALLEXCEPTIONS);
	private static ArrayList<String> EXTENTIONS = InfoIO.loadInfoFile(Constants.EXTENTIONS);
	private static ArrayList<String> BIGEXCEPTIONS = new ArrayList<>();
	public static ArrayList<String> DIX = new ArrayList<>();
	private static final String[] WRONGSYMBOLS = { ":", "/", "{", "}", "\\", "<", ">", "*", "?", "$", "!", "\"", "@" };

	private Series show;
	private Error error;
	private int seasonNR;
	private ErrorNotification notification;
	private ArrayList<String> errorMessages;

	public ErrorSearchThread(Series series, ErrorController errorController) {
		this.show = series;
		this.addObserver(errorController);
		names = new ArrayList<>();
		errorMessages = new ArrayList<>();
		setNotification(ErrorNotification.ERRORFOUND);
	}

	public static void addWordtoList(String word, String file) {
		switch (file) {
		case Constants.DICTIONARYFILE:
			DICTIONARY.add(word);
			break;
		case Constants.SMALLEXCEPTIONS:
			SMALLEXCEPTIONS.add(word);
			Character.toUpperCase(word.charAt(0));
			BIGEXCEPTIONS.add(word);
			break;
		case Constants.EXTENTIONS:
			EXTENTIONS.add(word);
		}
	}

	public static void generateBIGEXCEPTIONS() {
		for (String w : SMALLEXCEPTIONS) {
			Character.toUpperCase(w.charAt(0));
			BIGEXCEPTIONS.add(w);
		}
	}

	@Override
	public void run() {
		if (notification.equals(ErrorNotification.RERUN)) {
			errorMessages.add(error.getMessage());
		}
		error = null;
		seasonNR = 0;
		names.clear();
		seasonLoop: for (Season season : show.getSeasons()) {
			if(show.getSeriesName().equals("Inhumans"))
				System.out.println(show.getCurrentSeason());

			if (show.getLastExistingFile() == null)
				break seasonLoop;

			if (checkForSeasonError(season))
				break;

			seasonNR = season.getSeasonNR();
			int index = 0;

			for (Episode e : season.getEpisodesAsSortedList()) {

				if (checkForMissingEpisode(e, index))
					break seasonLoop;

				if (e.getLocation() == null)
					continue;

				if (checkSeriesName(e))
					break seasonLoop;

				index = (e.getIsMulti()) ? e.getEpisodeNR() + 1 : e.getEpisodeNR();

				if (checkForMultipleFiles(e))
					break seasonLoop;

				if (checkForDoubleSpaceAndSpaceAtTheEnd(e))
					break seasonLoop;

				if (checkForEpisodeName(e))
					break seasonLoop;

				if (checkForSmallCap(e))
					break seasonLoop;

				if (checkForNewWord(e))
					break seasonLoop;

				if (checkExtention(e))
					break seasonLoop;

				if (checkSeasonNR(e))
					break seasonLoop;

				if (checkNumberFormat(e))
					break seasonLoop;

				if (checkDash(e))
					break seasonLoop;

				if (checkForWrongSymbols(e))
					break seasonLoop;

				if (checkForPartNumbers(e))
					break seasonLoop;

				if (e.equals(show.getLastExistingFile()))
					break seasonLoop;
			}

		}

		if (error == null) {
			if (!notification.equals(ErrorNotification.RERUN))
				notification = ErrorNotification.NOERRORFOUND;
			setChanged();
			notifyObservers(notification);

		}
	}

	private boolean checkNumberFormat(Episode e) {
		if (e.getEpisodeNR() == (e.getIsAnime() ? 999 : 99)) {
			if (e.getFileList().size() < 0)
				return generateError(e, (e.getFileList().size() + 1) + " Numberformat Errors in S"
						+ e.getSeasonNRasString() + " of " + e.getSeriesName());
			else
				return generateError(e,
						"Numberformat Error in S" + e.getSeasonNRasString() + " of " + e.getSeriesName());
		}

		String name = e.getLocation().getName();
		if (name.charAt(e.getSeriesName().length()) != ' ')
			return generateError(e, "Space missing after Seriesname for " + e.getFileName());
		String format = e.getLocation().getName().substring(e.getSeriesName().length());

		if (getCharactersBeforeAndAfterNumberFormat(e, format, e.getIsAnime()))
			return true;

		if (e.getIsMulti())
			return getCharactersBeforeAndAfterNumberFormat(e, format.substring(format.indexOf('&')), e.getIsAnime());
		return false;
	}

	private boolean checkForWrongSymbols(Episode e) {
		String epi = e.getFileName();
		for (String symbol : WRONGSYMBOLS) {
			if (epi.contains(symbol)) {
				return generateError(e, "Wrong Symbol (" + symbol + ") in " + e.getFileName());
			}
		}
		return false;
	}

	private boolean checkForPartNumbers(Episode e) {
		String epi = e.getFileName();
		if (epi.contains("Part 1") || epi.contains("Part 2") || epi.contains("Part 3") || epi.contains("Part 4")
				|| epi.contains("Part 5")) {

			String test = epi.replaceFirst("Part", "");
			if (test.contains("Part 1") || test.contains("Part 2") || test.contains("Part 3") || test.contains("Part 4")
					|| test.contains("Part 5")) {
				String suggestion = epi.substring(epi.indexOf("Part") - 1);
				return generateCapsError(e, "Part not needed for " + e.getFileName(), "", suggestion,
						ExceptionType.NONE);
			}

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
			return generateCapsError(e, "Partnumber Error in " + e.getFileName(), temp, epi, ExceptionType.NONE);
		}
		return false;
	}

	private boolean getCharactersBeforeAndAfterNumberFormat(Episode e, String format, boolean isAnime) {
		String[] numbers = new String[2];
		try {
			numbers[0] = format.substring(format.indexOf('x') - 3);
			numbers[1] = format.substring(format.indexOf('x') + ((isAnime) ? 4 : 3));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (!numbers[0].startsWith(" "))
			return generateError(e, "Space Missing before Season of " + e.getFileName());
		if ((!numbers[1].startsWith(" ") && !e.getEpisodeName().equals(""))
				|| (e.getEpisodeName().equals("") && !numbers[1].startsWith(".") && !e.getFileFormat().equals("")))
			return generateError(e, "Wrong Character after EpisodeNR of " + e.getFileName());
		return false;
	}

	private boolean checkDash(Episode e) {
		if (e.getEpisodeName().equals(""))
			return false;
		String name = e.getFileName();
		String dash = name.substring(name.indexOf(' ', name.indexOf('x', e.getSeriesName().length())));

		if (e.getIsMulti()) {
			if (!dash.startsWith(" & "))
				return generateError(e, "And Error for Multiepisode Format " + e.getFileName());
			dash = dash.substring(dash.indexOf(' ', dash.indexOf('x')));
		}
		if (!dash.startsWith(" - ") && !NAMEEXCEPTIONS.contains(e.getEpisodeName()))
			return generateError(e, "Dash Error for " + e.getFileName());
		return false;
	}

	private boolean checkSeasonNR(Episode e) {
		if (e.getSeasonNR() == e.getSeasonNRFromFile())
			return false;
		return generateError(e, "Season Number not correct for: " + e.getFileName());
	}

	private boolean checkExtention(Episode e) {
		String extention = e.getFileFormat();
		if (EXTENTIONS.contains(extention))
			return false;
		else if (extention.equals(""))
			return generateError(e, "Extention missing for: " + e.getFileName());
		return generateNameOrExtentionError(e, "Fileformat not correctc for: " + e.getFileName(), extention,
				ExceptionType.FORMAT);
	}

	private boolean checkForDoubleSpaceAndSpaceAtTheEnd(Episode e) {
		String fileName = e.getFileName();
		if (fileName == null)
			return false;
		if (!fileName.contains("."))
			return false;
		if (fileName.contains("  "))
			return generateError(e, "Double Space: " + e.getFileName());
		if (fileName.charAt(fileName.lastIndexOf('.') - 1) == ' ')
			return generateError(e, "Space at the End: " + e.getFileName());
		return false;
	}

	private boolean checkForEpisodeName(Episode e) {
		String name = e.getEpisodeName();
		if (show.getEpisodeNameNeeded() && name.equals("")) {
			return generateError(e, "Episode Name Missing for " + e.getSeriesNameAnd01x01());
		} else if (name.equals("")) {
			return false;
		} else if (names.contains(name)) {
			for (String ex : NAMEEXCEPTIONS) {
				if (ex.equals(name))
					return false;
			}
			return generateNameOrExtentionError(e, "Episode Name used twice: " + name, name, ExceptionType.NAME);
		}

		names.add(name);

		return false;
	}

	private boolean checkForSmallCap(Episode e) {
		String name = e.getEpisodeName();
		if (name.equals(""))
			return false;
		String[] words = name.split(" ");
		for (int i = 0; i < words.length; i++) {
			if (words[i].length() < 1)
				continue;

			if (Character.isLetter(words[i].charAt(0)) && Character.isLowerCase((words[i].charAt(0)))) {
				String cap = words[i].substring(0, 1).toUpperCase();
				String oldWord = words[i];
				words[i] = cap + words[i].substring(1);

				String newName = e.getSeriesNameAnd01x01() + " - " + StringUtils.join(words, " ") + e.getFileFormat();

				if (SMALLEXCEPTIONS.contains(oldWord)) {

					if (i == 0)
						return generateCapsError(e, "Lowercase Small Error: " + oldWord + " (" + name + ")", oldWord,
								newName, ExceptionType.SMALL);
					else if (words[i - 1].endsWith("[-&!.]"))
						return generateCapsError(e, "Lowercase Small Error: " + oldWord + " (" + name + ")", oldWord,
								newName, ExceptionType.SMALL);
					else
						continue;
				}

				if (DICTIONARY.contains(oldWord))
					continue;

				return generateCapsError(e, "Lowercase Error: " + oldWord + " (" + name + ")", oldWord, newName,
						ExceptionType.SMALL);

			} else if (BIGEXCEPTIONS.contains(words[i]) && i > 0) {

				if (words[i - 1].matches("(.*)[-&.!]"))
					continue;
				String cap = words[i].substring(0, 1).toLowerCase();
				String oldWord = words[i];
				words[i] = cap + words[i].substring(1);

				String newName = e.getSeriesNameAnd01x01() + " - " + StringUtils.join(words, " ") + e.getFileFormat();

				return generateCapsError(e, "Uppercase Error: " + words[i] + " (" + name + ")", oldWord, newName,
						ExceptionType.SMALL);
			}
		}
		return false;
	}

	private boolean checkForNewWord(Episode e) {
		if (e.getEpisodeName().equals(""))
			return false;
		String[] words = e.getEpisodeName().split(" ");
		for (String w : words) {
			if (!DICTIONARY.contains(w) && !SMALLEXCEPTIONS.contains(w)) {
				return generateNewWordError(e, "New Word: " + w + " (" + e.getFileName() + ")", w);
			}
			if (!DIX.contains(w))
				DIX.add(w);

		}
		return false;
	}

	private boolean checkSeriesName(Episode e) {
		if (e.getLocation() == null)
			return false;
		String comp = e.getSeriesName() + " " + e.getSeasonNRasString();
		try {
			e.getFileName().substring(0, comp.length());
		} catch (StringIndexOutOfBoundsException ex) {
			if (!e.getFileList().isEmpty())
				return generateError(e, "Multiple Filenames in Season " + e.getSeriesName() + "S"
						+ e.getSeasonNRasString() + " not correct");
			else
				return generateError(e, "Filename in Season " + e.getSeriesName() + "S" + e.getSeasonNRasString()
						+ " not correct for " + e.getFileName());
		}
		if (!e.getSeriesName().equals(e.getFileName().substring(0, e.getSeriesName().length()))) {
			return generateError(e, "Seriesname not correct for " + e.getFileName());
		}
		return false;
	}

	private boolean checkForMissingEpisode(Episode e, int index) {
		int eNR = e.getEpisodeNR();
		if (((eNR == 0) || (eNR == 1)) && index <= 1 || eNR == (e.getIsAnime() ? 999 : 99)) {
			return false;
		}

		if (index + 1 != eNR || e.getLocation() == null) {
			if (eNR - index > 2)
				return (generateError(e,
						"Missing Episodes: " + e.getSeriesName() + " " + e.getSeasonNRasString() + "x"
								+ add0(index + 1, e.getIsAnime()) + " - " + e.getSeasonNRasString() + "x"
								+ add0((eNR - 1), e.getIsAnime())));

			return (generateError(e, "Missing Episode: " + e.getSeriesName() + " " + e.getSeasonNRasString() + "x"
					+ add0(index + 1, e.getIsAnime())));
		}
		return false;
	}

	private boolean checkForMultipleFiles(Episode e) {
		if (e.getEpisodeNR() == (e.getIsAnime() ? 999 : 99))
			return false;
		if (e.getSeriesName().equals("Doctor Who Classic"))
			return false;
		if (e.getSeasonNRFromFile() != e.getSeasonNR())
			return false;
		if (!e.getFileList().isEmpty()) {
			return generateError(e, "Multiple Files found for " + e.getSeriesNameAnd01x01());
		}
		return false;
	}

	private boolean checkForSeasonError(Season season) {
		if (seasonNR + 1 != season.getSeasonNR()) {
			if (season.getSeasonNR() - seasonNR < 3) {
				if (generateError(new Episode(), "Season " + (seasonNR + 1) + " is missing"))
					return true;
			} else if (generateError(new Episode(),
					"Seasons " + (seasonNR + 1) + " through " + (season.getSeasonNR() - 1) + " are missing"))
				return true;
		}

		if (season.getEpisodeCount() < 1 && season.getLocation() != null) {
			if (generateError(new Episode(), "Season " + (seasonNR + 1) + " is empty"))
				return true;

		}
		seasonNR = season.getSeasonNR();
		return false;
	}

	public Error getError() {
		return error;
	}

	public ErrorNotification getNotification() {
		return notification;
	}

	public void setNotification(ErrorNotification notification) {
		this.notification = notification;
	}

	private boolean generateNameOrExtentionError(Episode e, String message, String name, ExceptionType exceptionType) {
		if (errorMessages.contains(message))
			return false;

		error = new Error(e, message, show);
		error.setExceptionType(exceptionType);
		error.setNewWord(name);
		setChanged();
		notifyObservers(notification);
		System.out.println(message);
		return true;
	}

	private boolean generateCapsError(Episode e, String message, String word, String suggestion,
			ExceptionType exceptionType) {
		if (errorMessages.contains(message))
			return false;

		error = new Error(e, message, show);
		error.setNewWord(word);
		error.setCurrent(suggestion);
		error.setExceptionType(exceptionType);
		setChanged();
		notifyObservers(notification);
		System.out.println(message);
		return true;
	}

	private boolean generateNewWordError(Episode e, String message, String word) {
		if (errorMessages.contains(message))
			return false;

		error = new Error(e, message, show);
		error.setNewWord(word);
		setChanged();
		notifyObservers(notification);
		System.out.println(message);
		return true;
	}

	private boolean generateError(Episode e, String message) {
		if (errorMessages.contains(message))
			return false;

		error = new Error(e, message, show);
		setChanged();
		notifyObservers(notification);
		System.out.println(message);
		return true;
	}

	private String add0(int i, boolean anime) {
		String s = Integer.toString(i);
		if (i < 10) {
			s = "0" + s;
		}
		if (i < 100 && anime) {
			s = "0" + s;
		}
		return s;
	}

	public Series getShow() {
		return show;
	}
}
