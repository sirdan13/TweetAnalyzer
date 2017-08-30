package tweet.analyzer.model.data;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the MEDIA database table.
 * 
 */
@Entity
@Table(name="MEDIA")
@NamedQuery(name="Media.findAll", query="SELECT m FROM Media m")
public class Media implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;

	@Column(name="EXPANDED_URL")
	private String expandedUrl;

	@Column(name="TWEET_ID")
	private BigDecimal tweetId;

	@Column(name="[TYPE]")
	private String type;

	public Media() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getExpandedUrl() {
		return this.expandedUrl;
	}

	public void setExpandedUrl(String expandedUrl) {
		this.expandedUrl = expandedUrl;
	}

	public BigDecimal getTweetId() {
		return this.tweetId;
	}

	public void setTweetId(BigDecimal tweetId) {
		this.tweetId = tweetId;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}