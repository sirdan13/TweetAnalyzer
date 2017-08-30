package tweet.analyzer.utilities;

import java.io.Serializable;

/**
 * Word Statisitcs Class
 * @author Luca
 *
 */
public class WordStats implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5295234057103029231L;
	/**
	 * number of word occurrences
	 */
	private int occurrences;
	/**
	 * number of documents in which a word appears
	 */
	private int numOfDocs;
	/**
	 * Inverse Document Frequency
	 */
	private double IDF = 0.0;
	
	public int getOccurrences() {
		return occurrences;
	}
	public void setOccurrences(int occurrences) {
		this.occurrences = occurrences;
	}
	public int getNumOfDocs() {
		return numOfDocs;
	}
	public void setNumOfDocs(int numOfDocs) {
		this.numOfDocs = numOfDocs;
	}
	public double getIDF() {
		return IDF;
	}
	public void setIDF(double iDF) {
		IDF = iDF;
	}
	
	public WordStats(int occurrences, int numOfDocs) {
		super();
		this.occurrences = occurrences;
		this.numOfDocs = numOfDocs;
	}
	

	

}
