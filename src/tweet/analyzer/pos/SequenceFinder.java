package tweet.analyzer.pos;

import java.util.ArrayList;
import java.util.List;

/**
 * Sequence finder class
 * @author TvPad
 *
 */
public class SequenceFinder {
	
	
	
	/**
	 * Finds sequences in a string array. Returns sequences strings
	 * @param input the string array to search for sequences
	 * @param seq the sequence to be found
	 * @return a list of sequences as String[]
	 */
	public static List<String[]> findSequence(String[] input, String[] seq){
		
		ArrayList<String[]> output = new ArrayList<String[]>();
		
		if(seq.length<= input.length){
			
			for(int i=0;i<input.length-seq.length;i++){
				int matches = 0;
				int j=i;
				int k=0;
				
				while(k<seq.length && seq[k].equals(input[j])){
					matches++;
					j++;
					k++;
				}
				if(matches==seq.length){
					String[] sequence = new String[seq.length];
					for(int x=0; x<seq.length; x++)
						sequence[x] = input[i+x];
					output.add(sequence);
				}
										
			}
				
			return output;
		}
		
		else return null;
				
	}
	
	/**
	 * Finds sequences in a string array. Returns indexes at which each sequence starts.
	 * @param input the string array to search for sequences
	 * @param seq the sequence to be found
	 * @return a list of integers each one identifying the index in the input String[] 
	 * where a sequence begin
	 */
	public static List<Integer> findSequenceIndex(String[] input, String[] seq){
		
		ArrayList<Integer> output = new ArrayList<Integer>();
		
		if(seq.length<= input.length){
			
			for(int i=0;i<=input.length-seq.length;i++){
				int matches = 0;
				int j=i;
				int k=0;
				
				while(k<seq.length && seq[k].equals(input[j])){
					matches++;
					j++;
					k++;
				}
				if(matches==seq.length){
					Integer seqno = i;

					output.add(seqno);
				}
							
			}
			
		}
		
		return output;
				
	}

}
