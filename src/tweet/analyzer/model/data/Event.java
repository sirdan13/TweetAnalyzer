package tweet.analyzer.model.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 * The persistent class for the Event database table.
 * 
 */
@Entity
@NamedQueries({ @NamedQuery(name = "Event.findAll", query = "SELECT r FROM Event r"), @NamedQuery(name = "Event.findById", query = "SELECT r FROM Event r WHERE r.id = :id") })
public class Event implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;

	@Column(name = "Duration")
	private int duration;

	@Column(name = "EndDate")
	private Timestamp endDate;

	@Column(name = "Name")
	private String name;

	@Column(name = "StartDate")
	private Timestamp startDate;
	
	@Column(name="SubjectTags")
	private String tags;

	// bi-directional many-to-one association to AnalysisInterval
	@OneToMany(mappedBy = "event")
	private List<AnalysisInterval> analysisIntervals;
	
	// bi-directional many-to-one association to AnalysisInterval
	@OneToMany(mappedBy = "event")
	private List<Subject> subjects;

//	// bi-directional many-to-one association to CaptureInterval
//	@OneToMany(mappedBy = "event")
//	private List<CaptureInterval> captureIntervals;

	public Event() {
	}
	
	public String getSubjectTags(){
		return this.tags;
	}
	
	public void setSubjectTags(String tags){
		this.tags=tags;
	}
	
	public String[] getSplitTags(){
		return this.tags.split(",");
	}

	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDuration() {
		return this.duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public Timestamp getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public List<AnalysisInterval> getAnalysisIntervals() {
		return this.analysisIntervals;
	}

	public void setAnalysisIntervals(List<AnalysisInterval> analysisIntervals) {
		this.analysisIntervals = analysisIntervals;
	}

	public AnalysisInterval addAnalysisInterval(AnalysisInterval analysisInterval) {
		getAnalysisIntervals().add(analysisInterval);
		analysisInterval.setEvent(this);

		return analysisInterval;
	}

	public AnalysisInterval removeAnalysisInterval(AnalysisInterval analysisInterval) {
		getAnalysisIntervals().remove(analysisInterval);
		analysisInterval.setEvent(null);

		return analysisInterval;
	}	
	
	public List<Subject> getSubjects() {
		return this.subjects;
	}

	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}

	public Subject addSubject(Subject subject) {
		getSubjects().add(subject);
		subject.setEvent(this);

		return subject;
	}

	public Subject removeSubject(Subject subject) {
		getSubjects().remove(subject);
		subject.setEvent(null);

		return subject;
	}	

//	public List<CaptureInterval> getCaptureIntervals() {
//		return this.captureIntervals;
//	}
//
//	public void setCaptureIntervals(List<CaptureInterval> captureIntervals) {
//		this.captureIntervals = captureIntervals;
//	}
//
//	public CaptureInterval addCaptureInterval(CaptureInterval captureInterval) {
//		getCaptureIntervals().add(captureInterval);
//		captureInterval.setEvent(this);
//
//		return captureInterval;
//	}
//
//	public CaptureInterval removeCaptureInterval(CaptureInterval captureInterval) {
//		getCaptureIntervals().remove(captureInterval);
//		captureInterval.setEvent(null);
//
//		return captureInterval;
//	}
}