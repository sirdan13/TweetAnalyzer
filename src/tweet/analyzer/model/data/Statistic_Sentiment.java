package tweet.analyzer.model.data;

import java.io.Serializable;
import java.math.BigDecimal;

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
 * The persistent class for the Statistic_Sentiment database table.
 * 
 */
@Entity
@NamedQueries({ @NamedQuery(name = "Statistic_Sentiment.findAll", query = "SELECT s FROM Statistic_Sentiment s"),
		@NamedQuery(name = "Statistic_Sentiment.findById", query = "SELECT r FROM Statistic_Sentiment r WHERE r.id = :id") })
public class Statistic_Sentiment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	
	// bi-directional many-to-one association to Subject
	@ManyToOne
	@JoinColumn(name = "IDSubject")
	private Subject subject;
	
	@Column(name="SubjectName")
	private String subjectname;
	
	@Column(name="SubjectWords")
	private String subjectwords;
	
	@Column(name = "LikeValue", nullable = true, precision=8, scale=3)
	private BigDecimal likevalue;
	
	@Column(name="LikeTweetIdList")
	private String liketweetidlist;
	
	@Column(name = "HilariousValue", nullable = true, precision=8, scale=3)
	private BigDecimal hilariousvalue;
	
	@Column(name="HilariousTweetIdList")
	private String hilarioustweetidlist;
	
	@Column(name = "SadValue", nullable = true, precision=8, scale=3)
	private BigDecimal sadvalue;
	
	@Column(name="SadTweetIdList")
	private String sadtweetidlist;
	
	@Column(name = "AngryValue", nullable = true, precision=8, scale=3)
	private BigDecimal angryvalue;
	
	@Column(name="AngryTweetIdList")
	private String angrytweetidlist;

	// bi-directional many-to-one association to AnalysisInterval
	@ManyToOne
	@JoinColumn(name = "IDAnalysisInterval")
	private AnalysisInterval analysisInterval;

	public Statistic_Sentiment() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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
	
	public BigDecimal getLikevalue() {
		return likevalue;
	}

	public void setLikevalue(BigDecimal likevalue) {
		this.likevalue = likevalue;
	}

	public BigDecimal getHilariousvalue() {
		return hilariousvalue;
	}

	public void setHilariousvalue(BigDecimal hilariousvalue) {
		this.hilariousvalue = hilariousvalue;
	}

	public BigDecimal getSadvalue() {
		return sadvalue;
	}

	public void setSadvalue(BigDecimal sadvalue) {
		this.sadvalue = sadvalue;
	}

	public BigDecimal getAngryvalue() {
		return angryvalue;
	}

	public void setAngryvalue(BigDecimal angryvalue) {
		this.angryvalue = angryvalue;
	}

	public String getLiketweetidlist() {
		return liketweetidlist;
	}

	public void setLiketweetidlist(String liketweetidlist) {
		this.liketweetidlist = liketweetidlist;
	}

	public String getHilarioustweetidlist() {
		return hilarioustweetidlist;
	}

	public void setHilarioustweetidlist(String hilarioustweetidlist) {
		this.hilarioustweetidlist = hilarioustweetidlist;
	}

	public String getSadtweetidlist() {
		return sadtweetidlist;
	}

	public void setSadtweetidlist(String sadtweetidlist) {
		this.sadtweetidlist = sadtweetidlist;
	}

	public String getAngrytweetidlist() {
		return angrytweetidlist;
	}

	public void setAngrytweetidlist(String angrytweetidlist) {
		this.angrytweetidlist = angrytweetidlist;
	}

	public AnalysisInterval getAnalysisInterval() {
		return this.analysisInterval;
	}

	public void setAnalysisInterval(AnalysisInterval analysisInterval) {
		this.analysisInterval = analysisInterval;
	}
}