package tests;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import enums.Constants;
import enums.ShowStatus;
import logic.Episode;
import logic.MountSkyship;
import logic.Series;
import logic.Syncer;
import model.InfoModel;

public class SyncerTest {

	private File local;
	private InfoModel model;
	private File tf;
	private File tn;
	private File ts;
	public SyncerTest() throws IOException {
		new MountSkyship();
		local = new File(Constants.LOCALDIR);
		model = new InfoModel();
		createTestFiles();
		prepareModel();
		
		testEpisodesWithName();
	}
	private void prepareModel() {
		Series tf = new Series("TestF", "TF", "01", "Y");
		Series s = new Series("TestF", "TF", "01", "Y");
		
		
		s.getCurrentSeason().addEpisode(new Episode("TestF", 1, 1, "first"));
		s.getCurrentSeason().addEpisode(new Episode("TestF", 1, 2, "second"));
		s.getCurrentSeason().addEpisode(new Episode("TestF", 1, 3, "third"));
		s.getCurrentSeason().addEpisode(new Episode("TestF", 1, 4, "fourth"));
		s.getCurrentSeason().addEpisode(new Episode("TestF", 1, 5, "fifth"));
		s.getCurrentSeason().addEpisode(new Episode("TestF", 1, 7, ""));
		s.getCurrentSeason().addEpisode(new Episode("TestF", 1, 8, "hiatus"));
		s.getCurrentSeason().addEpisode(new Episode("TestF", 2, 1, "newSeason"));
		s.getCurrentSeason().addEpisode(new Episode("TestF", 2, 10, "ended"));

		tf.addNamesForNewEpisodes(s);
		Series tn = new Series("TestN", "TN", "03", "N");
		Series ts = new Series("TestS", "TS", "02", "Y");
		
		HashMap<String, Series> series = new HashMap<String, Series>();
		series.put(tf.getSeriesName(), tf);
		series.put(tn.getSeriesName(), tn);
		series.put(ts.getSeriesName(), ts);
		
		model.setSeries(series);

	}
	private void createTestFiles() {
		try {
			//correctEpisodeOfNewSeries
			File correct1 = new File(Constants.LOCALDIR+"/TF01.jUnitTest");
			correct1.createNewFile();
			//correctEpisodeWithName
			File correct2 = new File(Constants.LOCALDIR+"/TF02.jUnitTest");
			correct2.createNewFile();//			
			//correctDoubleEpisode
			File correct3 = new File(Constants.LOCALDIR+"/TF03*.jUnitTest");
			correct3.createNewFile();
			//doubleEpisodeWithSecondNameMissing
			File correct4 = new File(Constants.LOCALDIR+"/TF05*.jUnitTest");
			correct4.createNewFile();
			//EpisodeNameMissing	
			File correct5 = new File(Constants.LOCALDIR+"/TF07.jUnitTest");
			correct5.createNewFile();
			//correctHiatusEpisode
			File correct6 = new File(Constants.LOCALDIR+"/TF08+.jUnitTest");
			correct6.createNewFile();
			//correctEpisodeNewSeason
			File correct7 = new File(Constants.LOCALDIR+"/TF01.jUnitTest");
			correct7.createNewFile();
			//correctEndedEpisode
			File correct8 = new File(Constants.LOCALDIR+"/TF02.jUnitTest");
			correct8.createNewFile();
//			//correctEpisodeOfNewSeason
//			correct5 = File.createTempFile("TS01", "mp4", local);
//			
//			//correctDoubleEpisode
//			//TODO noName?
//			correct6 = File.createTempFile("TS02*", "mp4", local);
//			
//			//correctHiatusEpisode
//			correct7 = File.createTempFile("TS03+", "mp4", local);
//			//correctEndedEpisode
//			correct4 = File.createTempFile("TF03-", "mp4", local);

			
			
			
			
			
			

			
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	public void testEpisodesWithName() throws IOException {
		Thread syncThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Syncer syncer = new Syncer(model);
				syncer.sync();
			}
		});
		syncThread.start();
		try {
			syncThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	@Test
	public void createNewSeries(){
		File f = new File(Constants.SERIESDIR +"/TestF/Season 01/TestF 01x01 - first.jUnitTest");
		assertTrue("Series was created and File synced", f.exists());
	}
	
	@Test
	public void syncEpisodeWithName(){
 File f = new File(Constants.SERIESDIR +"/TestF/Season 01/TestF 01x02 - second.jUnitTest");
		assertTrue("File with Name synced", f.exists());
		
	}
	
	@Test
	public void doubleEpisodeWithDifferentNames(){
	File f = new File(Constants.SERIESDIR +"/TestF/Season 01/TestF 01x03 & 01x04 - third & fourth.jUnitTest");
		assertTrue("Double Episode with different Names synced", f.exists());
		
	}
	
	@Test
	public void doubleEpisodeWithSecondNameMissing(){
	File f = new File(Constants.LOCALDIR +"/TF05*.jUnitTest");
		assertTrue("Double Episode with second Name missing not synced ", f.exists());
		
	}
	
	@Test
	public void episodeWithNameMissing(){
	File f = new File(Constants.LOCALDIR +"/TF07.jUnitTest");
		assertTrue("Episode with Name missing not synced ", f.exists());
		
	}
	
	@Test
	public void hiatusEpisode(){
	File f = new File(Constants.SERIESDIR +"/TestF/Season 01/TestF 01x08 - hiatus");
		assertTrue("Episode with Hiatus tag", f.exists());
		assertEquals(ShowStatus.HIATUS, model.getSeries().get("TestF").getShowStatus());
	}
	
	@Test
	public void newSeason(){
	File f = new File(Constants.SERIESDIR +"/TestF/Season 02/TestF 02x01 - newSeason");
		assertTrue("first Episode of new Season", f.exists());
		assertEquals(ShowStatus.INSEASON, model.getSeries().get("TestF").getShowStatus());
	}
	
	@Test
	public void endedEpisode(){
	File f = new File(Constants.SERIESDIR +"/TestF/Season 01/TestF 02x10 - ended");
		assertTrue("Episode with Name missing not synced ", f.exists());
		assertEquals(ShowStatus.ENDED, model.getSeries().get("TestF").getShowStatus());
	}
	
	@After
	public void cleanUP() throws IOException{
		System.out.println(model.getSyncOverView());
		File d = new File(Constants.SERIESDIR +"/TestF");
		FileUtils.deleteDirectory(d);	
		File[] files = local.listFiles();
		for(File f : files){
			if(f.getName().contains(".jUnitTest"))
				f.deleteOnExit();
		}
	}

}
