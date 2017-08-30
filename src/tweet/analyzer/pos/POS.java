package tweet.analyzer.pos;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.InvalidFormatException;

/**
 * Class for POS-tagging based on Apache OpenNLP pos-tagger
 * @author TvPad
 *
 */
public class POS {
	
	/**
	 * Apache OpenNLP pos-tagger model
	 */
	private POSModel model;
	
	
	/**
	 * Constructor using model file 
	 * @param modelFilePath path of the file containing the pos tagging model (bin file)
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public POS(String modelFilePath) throws InvalidFormatException, IOException {
		InputStream modelIn = new FileInputStream(modelFilePath);
		this.model = new POSModel(modelIn);
	}

	public POS(POSModel model) {
		this.model = model;
	}
	
	/**
	 * Executes the pos tagging using Apache OpenNLP pos-tagger and the
	 * posModel specified calling the constructor
	 * @param tokens text after the tokenization
	 * @return corresponding pos tags
	 */
	public String[] tag(String[] tokens){
		
		POSTaggerME tagger = new POSTaggerME(model);
		String[] tagged = tagger.tag(tokens);
		
		for(int i=0;i<tagged.length;i++){
			tagged[i] = TagsetMappingItalian.convert(tagged[i]);
		}
		return tagged;
		
	}
	
	/**
	 * Finds a POS sequence
	 * @param tokens tokenized input
	 * @param sequence sequence to find; e.g. [N V]
	 * @return all the sequences matching the one specified as input
	 */
	public List<String[]> findPOSSequence(String[] tokens, String[] sequence){
		
		ArrayList<String[]> output = new ArrayList<String[]>();
		
		String[] postagged = this.tag(tokens);
		List<Integer> indexes = SequenceFinder.findSequenceIndex(postagged, sequence);
		
		for(int i=0;i<indexes.size();i++){
			String[] s = new String[sequence.length];
			for(int j=0; j< sequence.length; j++){
				//System.out.println("index="+indexes.get(i)+";j="+j+";tokens[ind[i]+j]="+tokens[indexes.get(i)+j]);
				s[j]=tokens[indexes.get(i)+j];
			}
			output.add(s);
		}
		
		return output;	
	}
	
	
	
	

}
