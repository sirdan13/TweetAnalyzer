package tweet.analyzer.model.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 * The persistent class for the AnalysisInterval database table.
 * 
 */
@Entity
@NamedQueries({ @NamedQuery(name = "AnalysisInterval.findAll", query = "SELECT r FROM AnalysisInterval r"),
		@NamedQuery(name = "AnalysisInterval.findById", query = "SELECT r FROM AnalysisInterval r WHERE r.id = :id") })
public class AnalysisInterval implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;

	@Column(name = "EndDate")
	private Timestamp endDate;

	@Column(name = "StartDate")
	private Timestamp startDate;

	@Column(name = "Completed", nullable = true)
	private Boolean completed;
	
	@Column(name = "Name", nullable = true)
	private String name;

	// bi-directional many-to-one association to Event
	@ManyToOne
	@JoinColumn(name = "IDEvent")
	private Event event;

	// bi-directional many-to-one association to Statistic_Sentiment
	@OneToMany(mappedBy = "analysisInterval")
	private List<Statistic_Sentiment> statisticSentiments;

	// bi-directional many-to-one association to Statistic_TrendingSubject
	@OneToMany(mappedBy = "analysisInterval")
	private List<Statistic_TrendingSubject> statisticTrendingSubject;

	public AnalysisInterval() {
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name=name;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Timestamp getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public Timestamp getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Event getEvent() {
		return this.event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}	

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public List<Statistic_Sentiment> getStatisticSentiments() {
		return this.statisticSentiments;
	}

	public void setStatisticSentiments(List<Statistic_Sentiment> statisticSentiments) {
		this.statisticSentiments = statisticSentiments;
	}

	public Statistic_Sentiment addStatisticSentiment(Statistic_Sentiment statisticSentiment) {
		getStatisticSentiments().add(statisticSentiment);
		statisticSentiment.setAnalysisInterval(this);

		return statisticSentiment;
	}

	public Statistic_Sentiment removeStatisticSentiment(Statistic_Sentiment statisticSentiment) {
		getStatisticSentiments().remove(statisticSentiment);
		statisticSentiment.setAnalysisInterval(null);

		return statisticSentiment;
	}

	public List<Statistic_TrendingSubject> getStatisticTrendingWords() {
		return this.statisticTrendingSubject;
	}

	public void setStatisticTrendingWords(List<Statistic_TrendingSubject> statisticTrendingSubject) {
		this.statisticTrendingSubject = statisticTrendingSubject;
	}

	public Statistic_TrendingSubject addStatisticTrendingWord(Statistic_TrendingSubject statisticTrendingSubject) {
		getStatisticTrendingWords().add(statisticTrendingSubject);
		statisticTrendingSubject.setAnalysisInterval(this);

		return statisticTrendingSubject;
	}

	public Statistic_TrendingSubject removeStatisticTrendingWord(Statistic_TrendingSubject statisticTrendingSubject) {
		getStatisticTrendingWords().remove(statisticTrendingSubject);
		statisticTrendingSubject.setAnalysisInterval(null);

		return statisticTrendingSubject;
	}

}