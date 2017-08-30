package tweet.analyzer.model.data;

public class Statistic_TopMostWordDB {
	private int id;
	private int idAnalysisInterval;
	private String word;
	private int weight;
	private String tweetIDList;
	private int idSubject;
	private String subjectName;
	private String subjectWords;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdAnalysisInterval() {
		return idAnalysisInterval;
	}
	public void setIdAnalysisInterval(int idAnalysisInterval) {
		this.idAnalysisInterval = idAnalysisInterval;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getIdSubject() {
		return idSubject;
	}
	public void setIdSubject(int idSubject) {
		this.idSubject = idSubject;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getSubjectWords() {
		return subjectWords;
	}
	public void setSubjectWords(String subjectWords) {
		this.subjectWords = subjectWords;
	}
	public String getTweetIDList() {
		return tweetIDList;
	}
	public void setTweetIDList(String tweetIDList) {
		this.tweetIDList = tweetIDList;
	}
	
	public static Statistic_TopMostWordDB convert(Statistic_TopMostWord stmw){
		Statistic_TopMostWordDB out = new Statistic_TopMostWordDB();
		out.setIdAnalysisInterval(stmw.getAnalysisInterval().getId());
		out.setIdSubject(stmw.getSubject().getId());
		out.setWord(stmw.getWord());
		out.setWeight(stmw.getWeight());
		out.setTweetIDList(stmw.getTweetidlist());
		out.setSubjectName(stmw.getSubjectname());
		out.setSubjectWords(stmw.getSubjectwords());
		return out;
		
	}

}
