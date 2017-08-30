package tweet.analyzer.model.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the TWEET database table.
 * 
 */
@Entity
@Table(name="TWEET")
@NamedQuery(name="Tweet.findAll", query="SELECT t FROM Tweet t")
public class Tweet implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;

	@Column(name="CREATION_DATE")
	private Timestamp creationDate;

	@Column(name="FAVORITES_COUNT")
	private int favoritesCount;

	@Column(name="[LANGUAGE]")
	private String language;

	@Column(name="LATITUDE")
	private double latitude;

	@Column(name="LONGITUDE")
	private double longitude;

	@Column(name="REQUEST_ID")
	private BigDecimal requestId;

	@Column(name="SOURCE_ID")
	private BigDecimal sourceId;

	@Column(name="TEXT")
	private String text;

	@Column(name="TWEET_ID")
	private BigDecimal tweetId;

	@Column(name="USER_FOLLOWER_COUNT")
	private int userFollowerCount;

	@Column(name="USER_ID")
	private BigDecimal userId;

	@Column(name="USER_LISTED_COUNT")
	private int userListedCount;

	@Column(name="USER_LOCATION")
	private String userLocation;

//	//bi-directional many-to-one association to CaptureInterval_Tweet
//	@ManyToOne
//	@JoinColumn(name="ID", referencedColumnName="IDTweet",insertable = false, updatable = false)
//	private CaptureInterval_Tweet captureIntervalTweet;

	public Tweet() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Timestamp getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	public int getFavoritesCount() {
		return this.favoritesCount;
	}

	public void setFavoritesCount(int favoritesCount) {
		this.favoritesCount = favoritesCount;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public BigDecimal getRequestId() {
		return this.requestId;
	}

	public void setRequestId(BigDecimal requestId) {
		this.requestId = requestId;
	}

	public BigDecimal getSourceId() {
		return this.sourceId;
	}

	public void setSourceId(BigDecimal sourceId) {
		this.sourceId = sourceId;
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

	public int getUserFollowerCount() {
		return this.userFollowerCount;
	}

	public void setUserFollowerCount(int userFollowerCount) {
		this.userFollowerCount = userFollowerCount;
	}

	public BigDecimal getUserId() {
		return this.userId;
	}

	public void setUserId(BigDecimal userId) {
		this.userId = userId;
	}

	public int getUserListedCount() {
		return this.userListedCount;
	}

	public void setUserListedCount(int userListedCount) {
		this.userListedCount = userListedCount;
	}

	public String getUserLocation() {
		return this.userLocation;
	}

	public void setUserLocation(String userLocation) {
		this.userLocation = userLocation;
	}

//	public CaptureInterval_Tweet getCaptureIntervalTweet() {
//		return this.captureIntervalTweet;
//	}
//
//	public void setCaptureIntervalTweet(CaptureInterval_Tweet captureIntervalTweet) {
//		this.captureIntervalTweet = captureIntervalTweet;
//	}

}