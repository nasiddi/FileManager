package logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.OverlayLayout;

public  class InfoLoader {
	

	public InfoLoader(){
		
	}
	
	public static HashMap<String, String[]> loadStatus() {
		HashMap<String, String[]> lines = new HashMap<String, String[]>();
		try {
			BufferedReader br = new BufferedReader(
					new FileReader("/Users/nadina/Documents/workspace/Files/status.txt"));

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
	
	public static HashMap<String, String> loadShorts() {
		HashMap<String, String> shorts = new HashMap<String, String>();
		try {
			BufferedReader br = new BufferedReader(
					new FileReader("/Users/nadina/Dropbox/Stuff/Files/shorts.txt"));

			String line;
			while ((line = br.readLine()) != null) {

				String[] data = line.split("#+");
				shorts.put(data[0], data[1]);

			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shorts;
	}
	
	public static HashMap<String, Series> loadNamesInHashMap(){
		HashMap<String, Series> s = new HashMap<String, Series>();
		for(String[] show :loadNames().values()){
			int i=0;
			for(String st : show){
			}
			Series series = new Series(show[i++], show[i++], show[i]);
			for(i++;i<show.length;i++){
				series.getCurrentSeason().addEpisode(Integer.parseInt(show[i].substring(0, show[i].indexOf(" "))), show[i].substring(show[i].indexOf("- ")+2));
			}
			
			s.put(series.getSeriesName(), series);
		}
		
		return s;
	}
	
	private static HashMap<String, String[]> loadNames() {
		HashMap<String, String[]> shows = new HashMap<String, String[]>();

		try {
			BufferedReader br = new BufferedReader(new FileReader("/Users/nadina/Dropbox/Stuff/Files/names.txt"));

			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split("#+");
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
	 * @param show 
	 * @param newValues (showNamw, premieredate, finaldate, status)
	 */
	public static void updateStatusSheet(String[] newValues) {
		String show = newValues[0];
		HashMap<String, String[]> statusSheet = InfoLoader.loadStatus();
		statusSheet.put(show, newValues);

		ArrayList<String[]> lines = new ArrayList<String[]>(statusSheet.values());
		Collections.sort(lines, new Comparator<String[]>() {
			@Override
			public int compare(String[] o1, String o2[]) {
				return o1[0].toLowerCase().compareTo(o2[0].toLowerCase());
			}
		});

		File file = new File("/Users/nadina/Documents/workspace/Files/status.txt");

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
	
}
