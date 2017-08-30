package tweet.analyzer.pos;

/**
 * Class for tag mapping from standard Opener Project Notation to Italian tags
 * @author TvPad
 *
 */
public class TagsetMappingItalian {

	/**
	 * Convert POS-tags from standard Opener Project Notation to Italian tags
	 * @param ptb original POS-tag
	 * @return corresponding Italian POS-tag
	 * V Verbo
	 * N Nome Comune
	 * R Nome Proprio
	 * D Articolo
	 * G Aggettivo
	 * A Avverbio
	 * Q Congiunzione
	 * P Preposizione
	 * Q Pronome
	 * O.Z Numero
	 * O Altro
	 */
	public static String convert(String ptb) {

		if (ptb.startsWith("V")) //verbo
			return "V"; 
		if (ptb.startsWith("NOU~C")) //nome comune
			return "N";
		if (ptb.startsWith("NOU~P")) //nome proprio
			return "R";
		if (ptb.startsWith("ART")) //articolo
			return "D";
		if (ptb.startsWith("ADJ")) //aggettivo
			return "G";
		if (ptb.startsWith("ADVB")) //avverbio
			return "A";
		if (ptb.startsWith("CONJ")) //congiunzione
			return "Q";
		if (ptb.startsWith("PREP")) // preposizione
			return "P";
		if (ptb.startsWith("PRO")) //pronome
			return "Q";
		if (ptb.startsWith("NUM")) //numero
			return "O.Z";
		return "O";  //altro
	}

}
