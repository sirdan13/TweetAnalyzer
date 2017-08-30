package tweet.analyzer.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import weka.core.Instance;

/**
 * Word Cluster class
 * @author TvPad
 *
 */
public class Cluster {
	
	
	/**
	 * All the words in the cluster
	 */
	private List<WeightedWord> words; 
	/**
	 * Word representative of the cluster (nearest to the cluster centroid)
	 */
	WeightedWord representativeWord; 
	/**
	 * Word with highest weight in the cluster
	 */
	WeightedWord highestWeightWord; 
	
	/**
	 * Cluster Centroid
	 */
	Instance centroid; 
	
	
	public Cluster() {
		words = new ArrayList<WeightedWord>();
		centroid = null;
		representativeWord = null;
	}
	
	
	public List<WeightedWord> getWords() {
		return words;
	}
	
	/**
	 * Add a word to the cluster
	 * @param word to add
	 */
	public void addWord(WeightedWord word) {
		this.words.add(word);
	}


	public WeightedWord getRepresentativeWord() {
		return representativeWord;
	}


	public Instance getCentroid() {
		return centroid;
	}


	public void setCentroid(Instance centroid) {
		this.centroid = centroid;
	}
	
	/**
	 * Computes and set the word with the highest weight in the cluster
	 */
	public void setHighestWeightWord(){
		
		WeightedWord wWord = null;
		double maxweight = 0;
		
		Iterator<WeightedWord> it = this.words.iterator();
		while(it.hasNext()){
			WeightedWord w = it.next();
			
			if(w.getWeight()>maxweight){
				maxweight = w.getWeight();
				wWord = w;			
			}
		}
		
		this.highestWeightWord = wWord;
		
	}


	public WeightedWord getHighestWeightWord() {
		return highestWeightWord;
	}


	public void setHighestWeightWord(WeightedWord highestWeightWord) {
		this.highestWeightWord = highestWeightWord;
	}


	public void setRepresentativeWord(WeightedWord representativeWord) {
		this.representativeWord = representativeWord;
	}
	
	

}
