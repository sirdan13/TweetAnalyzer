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
 * The persistent class for the Statistic_TrendingSubject database table.
 * 
 */
@Entity
@NamedQueries({ @NamedQuery(name = "Statistic_TrendingSubject.findAll", query = "SELECT s FROM Statistic_TrendingSubject s"),
		@NamedQuery(name = "Statistic_TrendingSubject.findById", query = "SELECT r FROM Statistic_TrendingSubject r WHERE r.id = :id") })
public class Statistic_TrendingSubject implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;

	@Column(name = "Interval1")
	private Integer interval1;

	@Column(name = "Interval2")
	private Integer interval2;

	@Column(name = "Interval3")
	private Integer interval3;

	@Column(name = "Interval4")
	private Integer interval4;

	@Column(name = "Interval5")
	private Integer interval5;

	@Column(name = "Medal", nullable = true)
	private Integer medal;

	@Column(name = "IDSubject")
	private Integer idsubject;
	
	@Column(name = "SubjectName")
	private String subjectname;

	@Column(name = "TweetIdList")
	private String tweetidlist;

	// bi-directional many-to-one association to AnalysisInterval
	@ManyToOne
	@JoinColumn(name = "IDAnalysisInterval")
	private AnalysisInterval analysisInterval;

	public Statistic_TrendingSubject() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getInterval1() {
		return this.interval1;
	}

	public void setInterval1(int interval1) {
		this.interval1 = interval1;
	}

	public int getInterval2() {
		return this.interval2;
	}

	public void setInterval2(int interval2) {
		this.interval2 = interval2;
	}

	public int getInterval3() {
		return this.interval3;
	}

	public void setInterval3(int interval3) {
		this.interval3 = interval3;
	}

	public int getInterval4() {
		return this.interval4;
	}

	public void setInterval4(int interval4) {
		this.interval4 = interval4;
	}

	public int getInterval5() {
		return this.interval5;
	}

	public void setInterval5(int interval5) {
		this.interval5 = interval5;
	}
	
	public Integer getMedal() {
		return medal;
	}

	public void setMedal(Integer medal) {
		this.medal = medal;
	}

	public Integer getIdSubject() {
		return idsubject;
	}

	public void setIdSubject(Integer subject) {
		this.idsubject = subject;
	}

	public String getSubjectName() {
		return this.subjectname;
	}

	public void setSubjectName(String subjectname) {
		this.subjectname = subjectname;
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

}