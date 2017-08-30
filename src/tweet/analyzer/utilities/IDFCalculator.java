package tweet.analyzer.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Class to compute IDF
 * @author TvPad
 *
 */
public class IDFCalculator {
	
	int totalDocuments = 0;
	
	
	/**
	 * Computes IDF for all the words in a corpus 
	 * @param corpusFile the corpus file (txt file with one tweet/doc per line)
	 * @param preprocess text (set false is input file is already preprocessed)
	 * @return an hashmap where the key is the word and the value are the word stats (occurrences, IDF)
	 * @throws IOException
	 */
	public HashMap<String,WordStats> computeIDFFromCorpus(String corpusFile, boolean preprocess) throws IOException{
	
	
		HashMap<String, WordStats> statistics = new HashMap<String, WordStats>();
		
		//open vocabulary (text) file
		File inputfile = new File(corpusFile);
				
		BufferedReader in = new BufferedReader(
				   new InputStreamReader(
		                      new FileInputStream(inputfile), "UTF8")); 
		 
				String str;
				int countIterations = 0;
				
				//save documents to arraylist
				while ((str = in.readLine()) != null) {
					
					countIterations++;
					if(countIterations%100==0)
						System.out.println("Processed docs: "+countIterations);
						
					
					//Preprocess
					if(preprocess)
					str = preprocessText(str);
					
					//Tokenize
					String[] words = str.split("\\s+");
					
					HashMap<String,Integer> wordlist = new HashMap<String,Integer>();
					
					//compute word occurrences
					for(int i = 0; i < words.length; i++){
									
						if(wordlist.containsKey(words[i])){
						     wordlist.put(words[i], wordlist.get(words[i])+1);
						}
						else
						{
						    wordlist.put(words[i], 1);
						}				
					}
					
					//save results
					Iterator<String> it = wordlist.keySet().iterator();
					
					while (it.hasNext()){
						
						String key = it.next();
						
						if(statistics.containsKey(key)){
							WordStats word = statistics.get(key);
							int count = wordlist.get(key) + word.getOccurrences();
							int numDoc = 1 + word.getNumOfDocs();
							WordStats ws = new WordStats(count,numDoc);
							statistics.put(key, ws);
						}
						else
						{
							
							WordStats ws = new WordStats(1,1);
						    statistics.put(key, ws);
						}	
					
					}
											
				}
				
				in.close();
			
		//DEBUG
		System.out.println("TEST");
		
		this.totalDocuments=countIterations;
		return statistics;

				
	}
	
	
	/**
	 * Save the word stats (occurrences, # of doc in which a word appears, IDF) for all the words
	 * in a corpus in a text file
	 * @param corpusfile The txt file containing the reference corpus (one doc per line)
	 * @param outputfile The outputfile (text file)
	 * @param preprocess text (set false is input file is already preprocessed)
	 * @throws IOException
	 */
	public void writeWordStatsToFile(String corpusfile, String outputfile, boolean preprocess) throws IOException{
		
		HashMap<String,WordStats> stats = this.computeIDFFromCorpus(corpusfile,preprocess);
		int totalDocuments = this.getTotalDocuments();
		
		
		//PrintWriter out = new PrintWriter("IDF.txt");
		Writer out = new BufferedWriter(new OutputStreamWriter(
			    new FileOutputStream(outputfile), "UTF-8"));
		
		// PRINT RESULTS
		Iterator<String> it = stats.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();
			WordStats ws = stats.get(key);
			ws.setIDF(Math.log10((double)totalDocuments / (double)ws.getNumOfDocs()));
			//System.out.println(key+" "+ws.getOccurrences()+" "+ws.getNumOfDocs()+" "+ws.getIDF());
			
			//SAVE TO FILE
			out.write(key+"\t"+ws.getOccurrences()+"\t"+ws.getNumOfDocs()+"\t"+ws.getIDF()+"\n");
			//out.write(key+";"+ws.getOccurrences()+";"+ws.getNumOfDocs()+";"+ws.getIDF()+"\n"); //CSV
			//out.write(key+";"+ws.getIDF()+"\n"); //
		}	
			
		
		out.close();
		
	}
	
	

	public int getTotalDocuments() {
		return totalDocuments;
	}
	
	public static String removeAccents(String text) {
	    return text == null ? null :
	        Normalizer.normalize(text, Form.NFD)
	            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
	
	
	public String preprocessText(String str){
		
		//remove CR and LF?
		//remove URLs
		str=str.replaceAll("https?://t.co/[^ ]*", "");
		//remove usernames
		str=str.replaceAll("@[^ ]*", "");
		//remove accents
		str = removeAccents(str);
		//lowercase
		str=str.toLowerCase();
		//remove non alphanumeric characters (# excluded!)
		str=str.replaceAll("[^a-z A-Z0-9#]", "");
		//separate hashtags from previous word
		str=str.replaceAll("#", " #");
		//remove extra spaces
		str=str.replaceAll("  *", " ");
		//remove spaces at the beginning of the text 
		str=str.replaceAll("^ ", "");
		
		return str;
		
	}

}
