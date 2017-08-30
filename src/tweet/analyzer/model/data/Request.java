package tweet.analyzer.model.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the REQUEST database table.
 * 
 */
@Entity
@Table(name="REQUEST")
@NamedQuery(name="Request.findAll", query="SELECT r FROM Request r")
public class Request implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;

	@Column(name="DESCRIPTION")
	private String description;

	@Column(name="KEYWORD_FILTER")
	private String keywordFilter;

	@Column(name="LANGUAGE_FILTER")
	private String languageFilter;

	@Column(name="LOCATION_FILTER")
	private String locationFilter;

	@Column(name="NAME")
	private String name;

	@Column(name="START_DATE")
	private Timestamp startDate;

	@Column(name="STOP_DATE")
	private Timestamp stopDate;

	@Column(name="TaskId")
	private BigDecimal taskId;
	
	@Column(name="EndSavingDate")
	private Timestamp endSavingDate;
	
	// bi-directional many-to-one association to Event
	@ManyToOne
	@JoinColumn(name = "IDEvent")
	private Event event;

	//bi-directional many-to-one association to SourcesRequest
	@OneToMany(mappedBy="request")
	private List<SourcesRequest> sourcesRequests;

	public Request() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKeywordFilter() {
		return this.keywordFilter;
	}

	public void setKeywordFilter(String keywordFilter) {
		this.keywordFilter = keywordFilter;
	}

	public String getLanguageFilter() {
		return this.languageFilter;
	}

	public void setLanguageFilter(String languageFilter) {
		this.languageFilter = languageFilter;
	}

	public String getLocationFilter() {
		return this.locationFilter;
	}

	public void setLocationFilter(String locationFilter) {
		this.locationFilter = locationFilter;
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

	public Timestamp getStopDate() {
		return this.stopDate;
	}

	public void setStopDate(Timestamp stopDate) {
		this.stopDate = stopDate;
	}

	public Event getEvent() {
		return this.event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
	
	public Timestamp getEndSavingDate() {
		return this.endSavingDate;
	}

	public void setEndSavingDate(Timestamp endSavingDate) {
		this.endSavingDate = endSavingDate;
	}
	
	public BigDecimal getTaskId() {
		return this.taskId;
	}

	public void setTaskId(BigDecimal taskId) {
		this.taskId = taskId;
	}

	public List<SourcesRequest> getSourcesRequests() {
		return this.sourcesRequests;
	}

	public void setSourcesRequests(List<SourcesRequest> sourcesRequests) {
		this.sourcesRequests = sourcesRequests;
	}

	public SourcesRequest addSourcesRequest(SourcesRequest sourcesRequest) {
		getSourcesRequests().add(sourcesRequest);
		sourcesRequest.setRequest(this);

		return sourcesRequest;
	}

	public SourcesRequest removeSourcesRequest(SourcesRequest sourcesRequest) {
		getSourcesRequests().remove(sourcesRequest);
		sourcesRequest.setRequest(null);

		return sourcesRequest;
	}

}