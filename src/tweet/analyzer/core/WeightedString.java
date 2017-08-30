package tweet.analyzer.core;

/**
 * Class representing a string with its weight (TF*Average IDF of the words in the string)
 * @author TvPad
 *
 */
public class WeightedString implements Comparable<WeightedString>{
	
	private String string;
	private String lowercase;
	private double weight;
	private double TF;
	
	
	public WeightedString(String string) {
		this.string = string;
		this.lowercase = string.toLowerCase();
		this.weight = 0.0;
		this.TF = 1.0;
	}
	
	
	public WeightedString(String string, double tF) {
		this.string = string;
		this.lowercase = string.toLowerCase();
		this.TF = tF;
		this.weight = 0.0;
	}



	/**
	 * Computes the total weight of the string (sum of the weights of all the words in the string)
	 * @param KB the reference Knowledge Base used to obtain IDF
	 */
	public void computeWeight(KnowledgeBase KB){
		
		double totalWeight = 0.0;
		String[] words = this.string.toLowerCase().split("\\s+");
		
		for(int i=0; i<words.length; i++){
			
			double IDF;
			int occurrencesInDocs = 0;

			if(KB.getStats().get(words[i])!=null)
				occurrencesInDocs = KB.getStats().get(words[i]).getNumOfDocs();
			
			if(occurrencesInDocs > 0){
				IDF = Math.log10(((double)KB.getTotalDocs() / (double)occurrencesInDocs));		
			}
			// word not in the corpus
			else
				IDF = Math.log10(KB.getTotalDocs());
			
			totalWeight += IDF;		
		}
		
		this.weight = (totalWeight/words.length)*(this.TF);; //average word weight
	}




	@Override
	public int compareTo(WeightedString o) {
		
		int c = Double.compare(o.getWeight(), this.getWeight());
	    if (c != 0) return c;

	    else return this.getLowercase().compareTo(o.getLowercase());
	}




	public double getWeight() {
		return weight;
	}




	public void setWeight(double weight) {
		this.weight = weight;
	}




	public double getTF() {
		return TF;
	}

	public void setTF(double d) {
		TF = d;
	}

	public String getString() {
		return string;
	}
	
	
	public String getLowercase() {
		return lowercase;
	}
	
	

}
