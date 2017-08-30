package tweet.analyzer.model.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the SOURCES_REQUEST database table.
 * 
 */
@Entity
@Table(name="SOURCES_REQUEST")
@NamedQuery(name="SourcesRequest.findAll", query="SELECT s FROM SourcesRequest s")
public class SourcesRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;

	//bi-directional many-to-one association to Source
	@ManyToOne
	@JoinColumn(name="SOURCE_ID")
	private Source source;

	//bi-directional many-to-one association to Request
	@ManyToOne
	@JoinColumn(name="REQUEST_ID")
	private Request request;

	public SourcesRequest() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Source getSource() {
		return this.source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Request getRequest() {
		return this.request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

}