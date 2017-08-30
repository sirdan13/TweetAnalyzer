package tweet.analyzer.core;

/**
 * Word Class
 * @author TvPad
 *
 */
public class Word {
	
	protected String type;
	public boolean hashtag;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Word(String type) {
		super();
		this.type = type;
	}
	
	public void isHashtag() {
		String text= this.type;
		if(text.contains("#")) this.hashtag= true;
		else this.hashtag=false;
		
	}
	
	

}
