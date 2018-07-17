package enums;

import java.awt.Font;
import java.util.ArrayList;

public class Constants {
    public static final int FRAMEWIDTH = 1600;
    public static final int FRAMEHEIGHT = 800;
    
    public static final int BEFORE = 0;
    public static final int CURRENT = 1;
    public static final int AFTER = 2;
	public static final String STATUSFILE = "assets/status.txt";
	public static final String NAMEFILE = "assets/names.txt";
	public static final String DICTIONARYFILE = "assets/dictionary.txt";
	public static final String TESTTREE = "assets/tree";
	public static String SERIESDIR;
	public static String ANIMEDIR;
	public static String LOCALDIR = "L:\\Sync";
	public static final String PREPDIR = "L:\\complete";
	public static final String SUBDIR = "T:\\Subs\\";
	public static final String VIDEODRIVE = "V:\\";
	public static final String NULLEPISODE = "nullEpisode";
	public static final Font BOXFONT = new Font("Open Sans", Font.PLAIN, 30);
	public static final Font BIGFONT = new Font("Open Sans", Font.PLAIN, 48);
	public static final Font SMALLFONT = new Font("Open Sans", Font.PLAIN, 36);
	public static final String SMALLEXCEPTIONS = "assets/smallExceptions.txt";
	public static final String NAMEEXCEPTIONS = "assets/nameExceptions.txt";
	public static final String EXTENTIONS = "assets/extentions.txt";
	public static String BACKGROUNDIMAGE;
	public static final String BACKGROUNDSTD = "assets/background.jpg";
	public static final String BACKGROUNDCON = "assets/background2.jpg";
	public static final String BACKGROUNDTEST = "assets/testBackground.jpg";
	public static ArrayList<String> SUB_EXTENSIONS;
	
	





    public Constants(){
    	
    	if(false){
    		SERIESDIR = "assets/tree/Series";
    		ANIMEDIR = "assets/tree/Anime";
    		LOCALDIR = "assets/tree/sync";
    		BACKGROUNDIMAGE = BACKGROUNDTEST;
    	}else{
    		SERIESDIR = "V:\\Series";
    		ANIMEDIR = "V:\\Anime";
    		BACKGROUNDIMAGE = BACKGROUNDSTD;
    	}
    	
    		
    	
    	
    	
    	SUB_EXTENSIONS = new ArrayList<String>();
    	String[] subExt = new String[]{".idx",".sub",".srt"}; 
    	for(String e : subExt){
    		SUB_EXTENSIONS.add(e);
    	}
    }
}
