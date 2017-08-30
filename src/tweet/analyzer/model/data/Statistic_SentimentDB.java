package tweet.analyzer.model.data;

import java.math.BigDecimal;

public class Statistic_SentimentDB {
	

	private int id;
	private int idAnalysisInterval;
	private int idSubject;
	private String subjectName;
	private String subjectWords;
	private BigDecimal likeValue;
	private String likeTweetIdList;
	private BigDecimal hilariousValue;
	private String hilariousTweetIdList;
	private BigDecimal sadValue;
	private String sadTweetIdList;
	private BigDecimal angryValue;
	private String angryTweetIdList;
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
	public BigDecimal getLikeValue() {
		return likeValue;
	}
	public void setLikeValue(BigDecimal likeValue) {
		this.likeValue = likeValue;
	}
	public String getLikeTweetIdList() {
		return likeTweetIdList;
	}
	public void setLikeTweetIdList(String likeTweetIdList) {
		this.likeTweetIdList = likeTweetIdList;
	}
	public BigDecimal getHilariousValue() {
		return hilariousValue;
	}
	public void setHilariousValue(BigDecimal hilariousValue) {
		this.hilariousValue = hilariousValue;
	}
	public String getHilariousTweetIdList() {
		return hilariousTweetIdList;
	}
	public void setHilariousTweetIdList(String hilariousweetIdList) {
		this.hilariousTweetIdList = hilariousweetIdList;
	}
	public BigDecimal getSadValue() {
		return sadValue;
	}
	public void setSadValue(BigDecimal sadValue) {
		this.sadValue = sadValue;
	}
	public String getSadTweetIdList() {
		return sadTweetIdList;
	}
	public void setSadTweetIdList(String sadTweetIdList) {
		this.sadTweetIdList = sadTweetIdList;
	}
	public BigDecimal getAngryValue() {
		return angryValue;
	}
	public void setAngryValue(BigDecimal angryValue) {
		this.angryValue = angryValue;
	}
	public String getAngryTweetIdList() {
		return angryTweetIdList;
	}
	public void setAngryTweetIdList(String angryTweetIdList) {
		this.angryTweetIdList = angryTweetIdList;
	}
	
	public static Statistic_SentimentDB convert(Statistic_Sentiment stmw){
		Statistic_SentimentDB out = new Statistic_SentimentDB();
		out.setIdAnalysisInterval(stmw.getAnalysisInterval().getId());
		out.setIdSubject(stmw.getSubject().getId());
		out.setSubjectName(stmw.getSubjectname());
		out.setSubjectWords(stmw.getSubjectwords());
		out.setLikeValue(stmw.getLikevalue());
		out.setLikeTweetIdList(stmw.getLiketweetidlist());
		out.setAngryValue(stmw.getAngryvalue());
		out.setAngryTweetIdList(stmw.getAngrytweetidlist());
		out.setHilariousValue(stmw.getHilariousvalue());
		out.setHilariousTweetIdList(stmw.getHilarioustweetidlist());
		out.setSadValue(stmw.getSadvalue());
		out.setSadTweetIdList(stmw.getSadtweetidlist());
		return out;
		
	}
	
	}