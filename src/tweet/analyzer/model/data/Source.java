package tweet.analyzer.model.data;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the SOURCE database table.
 * 
 */
@Entity
@Table(name="SOURCE")
@NamedQuery(name="Source.findAll", query="SELECT s FROM Source s")
public class Source implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;

	@Column(name="INFO_LEVEL")
	private String infoLevel;

	@Column(name="SOCIAL_NETWORK")
	private String socialNetwork;

	@Column(name="SRC_SN_ID")
	private String srcSnId;

	@Column(name="SRC_SN_NAME")
	private String srcSnName;

	@Column(name="[TYPE]")
	private String type;

	//bi-directional many-to-one association to SourcesRequest
	@OneToMany(mappedBy="source")
	private List<SourcesRequest> sourcesRequests;

	public Source() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getInfoLevel() {
		return this.infoLevel;
	}

	public void setInfoLevel(String infoLevel) {
		this.infoLevel = infoLevel;
	}

	public String getSocialNetwork() {
		return this.socialNetwork;
	}

	public void setSocialNetwork(String socialNetwork) {
		this.socialNetwork = socialNetwork;
	}

	public String getSrcSnId() {
		return this.srcSnId;
	}

	public void setSrcSnId(String srcSnId) {
		this.srcSnId = srcSnId;
	}

	public String getSrcSnName() {
		return this.srcSnName;
	}

	public void setSrcSnName(String srcSnName) {
		this.srcSnName = srcSnName;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<SourcesRequest> getSourcesRequests() {
		return this.sourcesRequests;
	}

	public void setSourcesRequests(List<SourcesRequest> sourcesRequests) {
		this.sourcesRequests = sourcesRequests;
	}

	public SourcesRequest addSourcesRequest(SourcesRequest sourcesRequest) {
		getSourcesRequests().add(sourcesRequest);
		sourcesRequest.setSource(this);

		return sourcesRequest;
	}

	public SourcesRequest removeSourcesRequest(SourcesRequest sourcesRequest) {
		getSourcesRequests().remove(sourcesRequest);
		sourcesRequest.setSource(null);

		return sourcesRequest;
	}

}