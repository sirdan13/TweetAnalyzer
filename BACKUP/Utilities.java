package tweet.analyzer.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class containing utilities methods
 * @author TvPad
 *
 */
public class Utilities {
	
	

	

	/**
	 * Import data from text file
	 * @param filename input file path
	 * @return a list of tweets
	 * @throws IOException
	 */
	public static List<Tweet> importTweetsTextFromTxt(String filename) throws IOException{
		
		
		List<Tweet> tweets = new ArrayList<Tweet>();
		File inputfile = new File(filename);
		
		BufferedReader in = new BufferedReader(
				   new InputStreamReader(
		                      new FileInputStream(inputfile), "UTF8"));
		 
				String str;
		 
				while ((str = in.readLine()) != null) {
				    Tweet t = new Tweet(str);
				    tweets.add(t);
				}
				
				in.close();
		
		
		return tweets;
	}
	
	/**
	 * Preprocess a UTF-8 text file (one tweet/doc per row)
	 * @param inputfile input file path (one tweet/doc per row)
	 * @param outputfile output file path
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void preprocessTextFile(String inputfile, String outputfile) throws IOException, FileNotFoundException{
		
		Writer out = new BufferedWriter(new OutputStreamWriter(
			    new FileOutputStream(outputfile), "UTF-8"));
		
		
		File infile = new File(inputfile);
		
		BufferedReader in = new BufferedReader(
				   new InputStreamReader(
		                      new FileInputStream(infile), "UTF8")); 
		 
				String str;
				
				while ((str = in.readLine()) != null) {
					
					String newstr = preprocessText(str);
					out.write(newstr+"\n");
					
				}
				
			out.close();
			in.close();
		
	}
	
	/**
	 * Remove stopwords from a corpus text format (UTF8)
	 * @param inputfile a text file 
	 * @param outputfile a text file without stopwords
	 * @param swFile the path of the stopword list file
	 * @throws IOException
	 */
	public static void removeStopWordsFromFile(String inputfile, String outputfile,String swFile) throws IOException{
		
		Set<String> swList = wordSetFromTxtFile(swFile);
		
		Writer out = new BufferedWriter(new OutputStreamWriter(
			    new FileOutputStream(outputfile), "UTF-8"));
		
		
		File infile = new File(inputfile);
		
		BufferedReader in = new BufferedReader(
				   new InputStreamReader(
		                      new FileInputStream(infile), "UTF8")); 
		 
				String str;
				
				while ((str = in.readLine()) != null) {
					
					String newstr = removeStopWords(str,swList);
					out.write(newstr+"\n");
					
				}
				
		out.close();
		in.close();
			
	}
		
		

	
	/**
	 * PreProcess the string text removing urls, usernames, non alphanumeric characters,...
	 * @param input string to be processed
	 * @return processed string
	 */
	public static String preprocessText(String input){
		
		//remove CR and LF
		input=input.replaceAll("(\\r|\\n)", "");
		//remove URLs
		//this.text=text.replaceAll("https?://t.co/[^ ]*", "");
		input=input.replaceAll("https?://[^ ]*", "");
		//remove usernames
		input=input.replaceAll("@[^ ]*", "");
		//remove accents
		input=(removeAccents(input));
		//lowercase
		input=input.toLowerCase();
		//remove non alphanumeric characters (# excluded!)
		input=input.replaceAll("[^a-zA-Z0-9#]", " ");
		//separate hashtags from previous word
		input=input.replaceAll("#", " #");
		//remove extra spaces
		input=input.replaceAll("  *", " ");
		//remove spaces at the beginning of the text 
		input=input.replaceAll("^ ", "");
		
	//	input=Utilities.removeStopWords(input);
		
		return input;
		
	}
	
	/**
	 * Removes accented characters from a String
	 * @param text the input string
	 * @return the string without accented characters
	 */
	public static String removeAccents(String text) {
	    return text == null ? null :
	        Normalizer.normalize(text, Form.NFD)
	            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
	
	/**
	 * Removes stopwords from a string
	 * @param s the input string
	 * @param swList the stopword set
	 * @return the string without stopwords
	 */
	public static String removeStopWords(String s, Set<String> swList){
		
		
		String[] words = s.split("\\s+");
		String output = "";
		
		for(int i = 0; i < words.length; i++){
			
			String word = words[i];
			
			//remove single characters, numbers, stopwords
			if(!(word.length()<2 || word.matches("[-+]?\\d+([\\.,]\\d+)?") || swList.contains(word))){
				output=output+word+" ";
				
			}
			
		}
		
		if(output.length()>1)		
			return output.substring(0, output.length()-1); //remove last character (blank space)
		
		else return output;
	}
	
	/**
	 * Removes stopwords from a stirng
	 * @param s the input string
	 * @param swFile the text file containing the stopwords list (one words per row)
	 * @return the string without stopwords
	 */
	public static String removeStopWords(String s, String swFile){
		
		String output = s;
		try {
			Set<String> stopwordsList = Utilities.wordSetFromTxtFile("res/stopwords/stopwordsIT.txt");
			output = removeStopWords(s,stopwordsList);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return output;
		
		
	}
	
	/**
	 * Remove stopwords from a string. Uses default path for stopwords file "res/stopwords/stopwordsIT.txt"
	 * @param s the input string
	 * @return the string without stopwords
	 */
	public static String removeStopWords(String s){
		return removeStopWords(s,"C:/Users/Administrator/Desktop/WorkspaceTvPad/TweetAnalyzer/res/stopwords/stopwordsIT.txt");
	}
	
	
	
	/**
	 * Create a set of strings from a text file (one word per row)
	 * @param swFile the input file
	 * @return a set of strings
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static Set<String> wordSetFromTxtFile(String swFile) throws IOException, FileNotFoundException{
		
		TreeSet<String> swSet = new TreeSet<String>();
		String str = new String();
		
		File inputfile = new File(swFile);
		
		BufferedReader in = new BufferedReader(
				   new InputStreamReader(
		                      new FileInputStream(inputfile), "UTF8")); 
		 

		while ((str = in.readLine()) != null) {
			swSet.add(str.replace(" ", "" )); //remove white spaces
		}
		
		in.close();
				
		return swSet;
				
	}


}
