package tweet.analyzer.utilities;

import tweet.analyzer.core.Word;

/**
 * Class for word count
 * @author TvPad
 *
 */
public class WordCount extends Word implements Comparable<WordCount> {
	
	private int occurrences;

	public WordCount(String type) {
		super(type);
		occurrences = 0;
	}	
	
	public WordCount(String type, int occurrences) {
		super(type);
		this.occurrences = occurrences;
	}

	public int getOccurrences() {
		return occurrences;
	}


	@Override
	public int compareTo(WordCount o) {
		
		int c = Integer.compare(o.getOccurrences(), this.getOccurrences());
	    if (c != 0) return c;

	    else return this.getType().compareTo(o.getType());
	}

}
