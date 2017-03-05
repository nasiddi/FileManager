package enums;

import java.util.ArrayList;

public class Constants {
    public final static int FRAMEWIDTH = 800;
    public final static int FRAMEHEIGHT = 400;
    
    public final static int BEFORE = 0;
    public final static int CURRENT = 1;
    public final static int AFTER = 2;
    public static ArrayList<String> EXTENSIONS;

    public Constants(){
    	EXTENSIONS = new ArrayList<String>();
    	String[] ext = new String[]{".mp4",".mkv","avi","m4v","flv","mpg","divx","webm",".vob"}; 
    	for(String e : ext){
    		EXTENSIONS.add(e);
    	}
    }
}
