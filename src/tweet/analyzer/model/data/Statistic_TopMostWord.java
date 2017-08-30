package tweet.analyzer.model.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * The persistent class for the Statistic_TopMostWord database table.
 * 
 */
@Entity
@NamedQueries({ @NamedQuery(name = "Statistic_TopMostWord.findAll", query = "SELECT s FROM Statistic_TopMostWord s"),
		@NamedQuery(name = "Statistic_TopMostWord.findById", query = "SELECT r FROM Statistic_TopMostWord r WHERE r.id = :id") })
public class Statistic_TopMostWord implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;

	@Column(name = "Weight")
	private Integer weight;

	@Column(name = "Word")
	private String word;
	
	@Column(name="TweetIdList")
	private String tweetidlist;

	// bi-directional many-to-one association to AnalysisInterval
	@ManyToOne
	@JoinColumn(name = "IDAnalysisInterval")
	private AnalysisInterval analysisInterval;
	
	// bi-directional many-to-one association to Subject
	@ManyToOne
	@JoinColumn(name = "IDSubject")
	private Subject subject;
	
	@Column(name="SubjectName")
	private String subjectname;
	
	@Column(name="SubjectWords")
	private String subjectwords;

//	// bi-directional many-to-one association to Statistic_TopMostWord
//	@OneToMany(mappedBy = "statistic_topmostword", cascade = CascadeType.PERSIST, orphanRemoval = true)
//	private List<Statistic_TopMostWord_Tweet> statistic_topmostword_tweet;

	public Statistic_TopMostWord() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getWord() {
		return this.word;
	}

	public void setWord(String word) {
		this.word = word;
	}
	
	public String getTweetidlist() {
		return tweetidlist;
	}

	public void setTweetidlist(String tweetidlist) {
		this.tweetidlist = tweetidlist;
	}

	public AnalysisInterval getAnalysisInterval() {
		return this.analysisInterval;
	}

	public void setAnalysisInterval(AnalysisInterval analysisInterval) {
		this.analysisInterval = analysisInterval;
	}
	
	public Subject getSubject() {
		return this.subject;
	}

	public void setSubject(Subject sub) {
		this.subject = sub;
	}

	public String getSubjectname() {
		return subjectname;
	}

	public void setSubjectname(String subjectname) {
		this.subjectname = subjectname;
	}

	public String getSubjectwords() {
		return subjectwords;
	}

	public void setSubjectwords(String subjectwords) {
		this.subjectwords = subjectwords;
	}
	
//	public List<Statistic_TopMostWord_Tweet> getStatistic_topmostword_tweet() {
//		return statistic_topmostword_tweet;
//	}
//
//	public void setStatistic_topmostword_tweet(List<Statistic_TopMostWord_Tweet> statistic_topmostword_tweet) {
//		this.statistic_topmostword_tweet = statistic_topmostword_tweet;
//	}
//
//	public Statistic_TopMostWord_Tweet addStatistic_TopMostWord_Tweet(Statistic_TopMostWord_Tweet statistic_topmostword_tweet) {
//		getStatistic_topmostword_tweet().add(statistic_topmostword_tweet);
//		statistic_topmostword_tweet.setStatistic_topmostword(this);
//
//		return statistic_topmostword_tweet;
//	}
//
//	public Statistic_TopMostWord_Tweet removeSubjectWord(Statistic_TopMostWord_Tweet statistic_topmostword_tweet) {
//		getStatistic_topmostword_tweet().remove(statistic_topmostword_tweet);
//		statistic_topmostword_tweet.setStatistic_topmostword(null);
//
//		return statistic_topmostword_tweet;
//	}

}
