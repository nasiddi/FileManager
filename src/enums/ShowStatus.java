package enums;

public enum ShowStatus {
	NONE("",""), INSEASON("OG", "In Season"), HIATUS("LS", "Hiatus"), ENDED("FN", "Ended");
	private final String tag;
	private final String string;
	
	ShowStatus(String s, String string) {
		this.tag = s;
		this.string = string;
	}

	public String getTag() {
		return tag;
	}
	
	@Override
	public String toString(){
		return string;
	}
	
}
