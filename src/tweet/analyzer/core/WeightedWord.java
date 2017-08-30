package tweet.analyzer.core;

/**
 * Class representing a word with its associated weight (tf*IDF)
 * 
 * @author TvPad
 * 
 */
public class WeightedWord extends Word implements Comparable<WeightedWord> {

	private double tf;
	private double IDF;
	/**
	 * Weight = tf*IDF
	 */
	private double weight;
	
	private int fontWordCloud;
	
	public WeightedWord(String type) {
		super(type);
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public void setWeight() {

		this.weight = tf * IDF;
	}

	public double getTf() {
		return tf;
	}	

	/**
	 * Set the word IDF from the corpus file
	 * 
	 * @param c
	 *            the reference KnowledgeBase
	 */
	public void setIDFFromCorpus(KnowledgeBase c) {

		double IDF;
		int occurrencesInDocs = 0;

		if (c.getStats().get(type) != null)
			occurrencesInDocs = c.getStats().get(type).getNumOfDocs();

		if (occurrencesInDocs > 0) {
			IDF = Math.log10(((double) c.getTotalDocs() / (double) occurrencesInDocs));
		}
		// word not in the corpus
		else
			IDF = Math.log10(c.getTotalDocs());

		this.IDF = IDF;
	}

	public void setTf(double tf) {
		this.tf = tf;
	}

	public double getIDF() {
		return IDF;
	}

	public void setIDF(double iDF) {
		IDF = iDF;
	}

	public WeightedWord(String type, double tf) {
		super(type);
		this.tf = tf;
	}	

	

	@Override
	public int compareTo(WeightedWord o) {

		int c = Double.compare(o.getWeight(), this.getWeight());
		if (c != 0)
			return c;

		else
			return this.getType().compareTo(o.getType());
	}

	public int getFontWordCloud() {
		return fontWordCloud;
	}

	public void setFontWordCloud(int fontWordCloud) {
		this.fontWordCloud = fontWordCloud;
	}

}
