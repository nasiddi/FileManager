package logic;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashMap;

import controller.FrameStateManager;
import controller.MasterController;
import model.LoadingModel;

public class FileLoader {

	private HashMap<String, Series> series;
	private FrameStateManager frameStateManager;
	
	public FileLoader(FrameStateManager frameStateManager) {
		series = new HashMap<String, Series>();
		this.frameStateManager=frameStateManager;
		run("/Volumes/Video/Series");
		run("/Volumes/Video/Anime");
	}

	public void run(String path) {
		HashMap<String, String[]> status = InfoLoader.loadStatus();
		LoadingModel model = (LoadingModel) frameStateManager.getCurrentScreen().getModel();

		File[] ser = loadSeries(path);
		
		if(ser == null){
			model.displayError();
			return;
		}
			
		model.setStatusText("Loading Shows");
		int i =0;
		for(File s : ser){
			i++;
			//if(i>20)
				//break;
			model.setShowText(s.getName());
			Series show = new Series(s);
			if(status.containsKey(show.getSeriesName())){
				show.setInfo(status.get(show.getSeriesName()));
			} else{
			}
			series.put(s.getName(), show);
			
		}
		
		model.clearStatusLable();
		model.setStatusText("Loading Done");
	}

	private File[] loadSeries(String path) {
		File file = new File(path);
		File[] ser = file.listFiles(new FileFilter() {
			public boolean accept(File file) {
				if (file.isHidden())
					file.deleteOnExit();
				return !file.isHidden();
			}
		});
		
		//TODO fix exception
		try {
			Arrays.sort(ser);
		} catch (NullPointerException ex) {
			System.out.println("ERROR Skyship not connected");
			ser = null;
			
		}

		return ser;
	}
	
	public HashMap<String, Series> getSeries(){		
		return series;
	}
}
