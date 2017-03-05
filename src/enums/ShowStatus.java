package enums;

public enum ShowStatus {
	INSEASON("OG", "In Season"), HIATUS("LS", "Hiatus"), ENDED("FN", "Ended"), NONE("","");
	private final String tag;
	private final String string;
	
	ShowStatus(String s, String string) {
		this.tag = s;
		this.string = string;
	}

	public String getTag() {
		return tag;
	}
	
	public String nameToString(){
		return string;
	}
}
