package tweet.analyzer.core;

import java.util.List;

/**
 * "Sentiment" class. Possible Sentiment Values are: POSITIVE, INSULTING,
 * WITH_LINKS
 * 
 * @author TvPad
 *
 */
public class Sentiment {

	/**
	 * Sentiment classes. Available Values are: POSITIVE, INSULTING, WITH_LINKS
	 *
	 */
	public enum SentimentName {
		POSITIVE, INSULTING, WITH_LINKS, LOVELIKEWOW, HILARIOUS, SAD, ANGRY;
	}

	private SentimentName name;

	private Double value;

	public List<String> listaIdTweet;

	public Sentiment(SentimentName name, Double value, List<String> listaIdTweet) {
		this.name = name;
		this.value = value;
		this.listaIdTweet = listaIdTweet;
	}
	
	
	public Sentiment(SentimentName name, Double value) {
		this.name = name;
		this.value = value;
	
	}
	

	public Sentiment(SentimentName name) {
		this.name = name;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public SentimentName getName() {
		return name;
	}

	public List<String> getListaIdTweet() {
		return listaIdTweet;
	}

	public void setListaIdTweet(List<String> listaIdTweet) {
		this.listaIdTweet = listaIdTweet;
	}

}
