package tweet.analyzer.pos;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

/**
 * Tokenizer Class. Based on Apache OpenNLP tokenizer
 * @author TvPad
 *
 */
public class Tokenizer {
	
	/**
	 * Apache OpenNLP tokenizer model
	 */
	private TokenizerModel model;

	/**
	 * Constructor using model file
	 * @param modelFilePath path of the file containing the tokenizer model (bin file)
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public Tokenizer(String modelFilePath) throws InvalidFormatException, IOException{
		
		InputStream modelIn = new FileInputStream(modelFilePath);
		this.model = new TokenizerModel(modelIn);
	}
	
	public Tokenizer(TokenizerModel model) {
		this.model = model;
	}
	
	/**
	 * Tokenize the input string using the Apache OpenNLP tokenizer
	 * and the model specified calling the constructor 
	 * @param input string to tokenize
	 * @return tokens extracted from the input string
	 */
	public String[] tokenize(String input){
		input = preprocessTokens(input);
		TokenizerME tokenizer = new TokenizerME(model);
		return tokenizer.tokenize(input);
		
	}
	
	/**
	 * PreProcess the  text to be tokenized replacing urls, usernames, and hashtags
	 */
	private String preprocessTokens(String input){
		
		//remove CR and LF
		input=input.replaceAll("(\\r|\\n)", "");
		//replace URLs
		input=input.replaceAll("https?://[^ ]*", "collegamento");
		//remove usernames
		input=input.replaceAll("@[^ ]*", "utente");		
		//separate hashtags from previous word
		input=input.replaceAll("#", " #");
		//replace hashtags
		input=input.replaceAll("#[^ ]*", "nome");
		//remove extra spaces
		input=input.replaceAll("  *", " ");
		//remove spaces at the beginning of the text 
		input=input.replaceAll("^ ", "");
		
		return input;
		
	}
	
	

}
