package logic;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import enums.Constants;
import enums.ErrorNotification;
import enums.FrameState;
import model.LoadingModel;
import model.Series;

public class FileLoader {

	private HashMap<String, Series> series;
	private LoadingModel model;
	private HashMap<String, String[]> status;
	private HashMap<String, Series> names;
	private int nrOfThreads = 11;
	private boolean filesLoaded;

	public FileLoader(LoadingModel model) {
		this.model = model;
		filesLoaded = false;
		start();
		//findThreadNR();

	}

	private void findThreadNR() {
		nrOfThreads = 20;
		for (int i = 1; i < 1; i++)
			start();

		for (nrOfThreads = 7; nrOfThreads <= 100; nrOfThreads++) {
			long time = 0;
			for (int i = 0; i < 10; i++) {
				long start = System.currentTimeMillis();
				start();
				time = System.currentTimeMillis() - start;
				System.out.println(nrOfThreads + " " +time);
			}
			

		}
	}

	private void start() {
		series = new HashMap<String, Series>();
		status = InfoIO.loadStatus();
		names = InfoIO.loadNamesInHashMap();
		run(Constants.SERIESDIR);
		run(Constants.ANIMEDIR);
	}

	private void run(String path) {

		File[] ser = loadFilesInFolder(path);
		int c = 0;
		if (ser == null) {
			model.displayError();
			addMissingNames();
			return;
		}
		model.setStatusText("Loading Shows");
		ArrayList<Thread> threads = new ArrayList<>();

		while (c < ser.length || !threads.isEmpty()) {
			while (c < ser.length && threads.size() < nrOfThreads) {
				File s = ser[c];
				c++;
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						Series show = new Series(s);

						String seriesName = show.getSeriesName();
						if (names.containsKey(seriesName)) {
							show.addNamesForNewEpisodes(names.get(seriesName));
							names.remove(seriesName);

						}
						if (status.containsKey(seriesName)) 
							show.setInfo(status.get(seriesName));

						series.put(show.getSeriesName(), show);
					}
				});
				thread.start();
				threads.add(thread);
			}

			Iterator<Thread> i = threads.iterator();
			while (i.hasNext()) {
				Thread thread = i.next();
				if (!thread.isAlive()) {
					i.remove();
				}
			}
		}
		
		filesLoaded = true;
		
		if (names.isEmpty())
			return;

		addMissingNames();

	}

	private void addMissingNames() {
		for (Series s : names.values()) {
			if (status.containsKey(s.getSeriesName())) {
				s.setInfo(status.get(s.getSeriesName()));
			}
			series.put(s.getSeriesName(), s);
			
		}
	}

	static File[] loadFilesInFolder(String path) {
		File file = new File(path);
		File[] ser = file.listFiles(new FileFilter() {
			public boolean accept(File file) {
				if (file.isHidden())
					file.deleteOnExit();
				return !file.isHidden();
			}
		});

		// TODO fix exception
		try {
			Arrays.sort(ser);
		} catch (NullPointerException ex) {
			System.out.println("ERROR Skyship not connected");
			ser = null;

		}

		return ser;
	}

	public HashMap<String, Series> getSeries() {
		return series;
	}

	public boolean isFilesLoaded() {
		return filesLoaded;
	}

	public void setFilesLoaded(boolean filesLoaded) {
		this.filesLoaded = filesLoaded;
	}
}
