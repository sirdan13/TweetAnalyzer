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
 * The persistent class for the HASHTAG database table.
 * 
 */
@Entity
@Table(name="HASHTAG")
@NamedQuery(name="Hashtag.findAll", query="SELECT h FROM Hashtag h")
public class Hashtag implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;

	@Column(name="TEXT")
	private String text;

	@Column(name="TWEET_ID")
	private BigDecimal tweetId;

	public Hashtag() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public BigDecimal getTweetId() {
		return this.tweetId;
	}

	public void setTweetId(BigDecimal tweetId) {
		this.tweetId = tweetId;
	}

}