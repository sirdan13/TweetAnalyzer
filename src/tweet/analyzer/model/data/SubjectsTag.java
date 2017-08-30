package tweet.analyzer.model.data;

public class SubjectsTag {
	
	private int id;
	private String name;
	private String tag;
	private String stopwords;

	public SubjectsTag(int id, String name, String tag) {
		this.setId(id);
		this.setName(name);
		this.setTag(tag);
	}
	
	public SubjectsTag(int id, String name, String tag, String stopwords) {
		this.setId(id);
		this.setName(name);
		this.setTag(tag);
		this.setStopwords(stopwords);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getStopwords() {
		if(this.stopwords!=null)
			return stopwords;
		else
			return null;
	}

	public void setStopwords(String stopwords) {
		this.stopwords = stopwords;
	}

}
