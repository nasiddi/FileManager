package model;

import java.util.ArrayList;

public class ErrorModel extends Model {

	private ArrayList<String> names;
	private ArrayList<String> dictionary;
	private ArrayList<String> clone;
	private String seriesName = "";
	private int seasonNR = 0;
	private Season season;
	

	

	


	
	@Override
	public void reset() {
		clone = new ArrayList<String>();
		names = new ArrayList<String>();
		dictionary = new ArrayList<String>();
	}
}
