package logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

import enums.Constants;
import model.Series;

public class InfoIO {

	/**
	 * 
	 * @return key: SeriesName value; 0 SeriesName, 1 Premiere, 2 Final, 3
	 *         Status
	 * 
	 */

	static HashMap<String, String[]> loadStatus() {
		HashMap<String, String[]> lines = new HashMap<String, String[]>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(Constants.STATUSFILE));

			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split(";");
				lines.put(data[0], data);

			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}

	public static HashMap<String, Series> loadNamesInHashMap() {
		HashMap<String, Series> s = new HashMap<String, Series>();
		for (String[] show : loadNames().values()) {
			int i = 0;
			Series series = new Series(show[i++], show[i++], show[i++], show[i++]);
			int snr = Integer.parseInt(show[2]);
			series.setFirstNameFileEpisode(Integer.parseInt(show[i].substring(0, show[i].indexOf(" "))), snr,
					show[i].substring(show[i].indexOf("- ") + 2));
			for (i++; i < show.length; i++) {
				if(Integer.parseInt(show[i].substring(0, show[i].indexOf(" ")))<Integer.parseInt(show[i-1].substring(0, show[i-1].indexOf(" ")))){
					series.getSeasons().add(series.getSeason(snr+1));
				}
					
				series.getCurrentSeason().addEpisode(Integer.parseInt(show[i].substring(0, show[i].indexOf(" "))),
						show[i].substring(show[i].indexOf("- ") + 2));
			}
			s.put(series.getSeriesName(), series);
		}

		return s;
	}

	private static HashMap<String, String[]> loadNames() {
		HashMap<String, String[]> shows = new HashMap<String, String[]>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(Constants.NAMEFILE));

			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split("#");
				shows.put(data[0], data);

			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shows;
	}

	/**
	 * 
	 * @param newNames:
	 *            SeriesName#short#Season#noName#Episodes
	 */
	public static void saveNameChanges(ArrayList<String> newNames) {
		File file = new File(Constants.NAMEFILE);

		File temp;
		try {
			temp = File.createTempFile("file", ".txt", file.getParentFile());

			String charset = "UTF-8";

			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(temp), charset));
			for (String s : newNames) {
				writer.println(s);

			}

			writer.close();

			file.delete();

			temp.renameTo(file);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param newNames:
	 *            SeriesName;premiere;final;status
	 */
	public static void saveAllStatus(ArrayList<String> status) {
		File file = new File(Constants.STATUSFILE);

		File temp;
		try {
			temp = File.createTempFile("file", ".txt", file.getParentFile());

			String charset = "UTF-8";

			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(temp), charset));
			for (String s : status) {
				writer.println(s);
			}

			writer.close();

			file.delete();

			temp.renameTo(file);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param newValues
	 *            (showNamw, premieredate, finaldate, status)
	 */
	public static void updateStatusSheet(String[] newValues) {
		String show = newValues[0];
		HashMap<String, String[]> statusSheet = InfoIO.loadStatus();
		statusSheet.put(show, newValues);

		ArrayList<String[]> lines = new ArrayList<String[]>(statusSheet.values());
		Collections.sort(lines, new Comparator<String[]>() {
			@Override
			public int compare(String[] o1, String o2[]) {
				return o1[0].toLowerCase().compareTo(o2[0].toLowerCase());
			}
		});

		File file = new File(Constants.STATUSFILE);

		File temp;
		try {
			temp = File.createTempFile("file", ".txt", file.getParentFile());

			String charset = "UTF-8";

			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(temp), charset));

			for (String[] s : lines) {
				for (int i = 0; i < s.length - 1; i++) {
					writer.print(s[i] + ";");
				}
				writer.println(s[s.length - 1]);
			}

			writer.close();

			file.delete();

			temp.renameTo(file);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> loadInfoFile(String file) {
		ArrayList<String> input = new ArrayList<String>();
		StringWriter writer = new StringWriter();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(file));
			IOUtils.copy(fis, writer, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String theString = writer.toString();
		String[] split = theString.split("\r\n");
		for (String s : split)
			input.add(s);
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return input;

	}

	public static void saveDictionary(ArrayList<String> dictionary) {
		File file = new File(Constants.DICTIONARYFILE);

		File temp;
		try {
			temp = File.createTempFile("file", ".txt", file.getParentFile());

			String charset = "UTF-8";

			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(temp), charset));
			for (String s : dictionary) {
				writer.println(s);
			}

			writer.close();

			file.delete();

			temp.renameTo(file);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addNewWordToFile(String word, String file) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");
		BufferedWriter fbw = new BufferedWriter(writer);
		fbw.write(word);
		fbw.newLine();
		fbw.close();
		writer.close();
	}

}
