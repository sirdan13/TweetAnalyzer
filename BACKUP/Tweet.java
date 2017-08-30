package tweet.analyzer.core;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;

import com.vdurmont.emoji.EmojiParser;

/**
 * Tweet Class
 * @author TvPad
 * 
 */
public class Tweet {
	

	private String id;
	/**
	 * Original text of the tweet
	 */
	private String originaltext; 
	/**
	 * Text of the tweet after preprocessing
	 */
	private String processedtext; 
	private String language;
	private String longitude;
	private String latitude;
	private Date createdAt;
	private int favorites = 0;
	private String userId;
	private String userLocation;
	private int userListedCount;
	private int userFollowersCount;
	private String tweetID;
	

	/**
	 * List of insulting words in the tweet
	 */
	private List<String> insultingWords;
	/**
	 * List of positive words in the tweet
	 */
	private List<String> positiveWords;
	/**
	 * List of links in the tweet
	 */
	private List<String> links;
	
	
	private List<String> likeWords;
	private List<String> hilariousWords;
	private List<String> sadWords;
	private List<String> angryWords;
	
	private String subject ="";
	
	private int score=1;
	
	private boolean ambiguo=false;
	
	public void setAmbiguo(boolean esito) {
		this.ambiguo=esito;
	}
	
	public boolean getAmbiguo() {
		return this.ambiguo;
	}
	
	
	/**
	 * List of relevant words in the tweet
	 */
	private List<WeightedWord> relevantWords;
	
	
	
	public Tweet(String id, String text, String language, String longitude, String latitude, Date createdAt,
			int favorites, String userId, String userLocation, int userListedCount, int userFollowersCount, String tweetID) {
		this.id = id;
		this.originaltext = text;
		this.preprocessText();
		this.language = language;
		this.longitude = longitude;
		this.latitude = latitude;
		this.createdAt = createdAt;
		this.favorites = favorites;
		this.userId = userId;
		this.userLocation = userLocation;
		this.userListedCount = userListedCount;
		this.userFollowersCount = userFollowersCount;
		this.tweetID = tweetID;
		
		this.relevantWords = new ArrayList<WeightedWord>();
		this.positiveWords = new ArrayList<String>();
		this.insultingWords = new ArrayList<String>();
		this.links = new ArrayList<String>();
		this.likeWords = new ArrayList<String>();
		this.hilariousWords = new ArrayList<String>();
		this.sadWords = new ArrayList<String>();
		this.angryWords = new ArrayList<String>();

	}

	public List<WeightedWord> getRelevantWords() {
		return relevantWords;
	}

	public void setRelevantWords(List<WeightedWord> relevantWords) {
		this.relevantWords = relevantWords;
	}
	
	/**
	 * Set the relevant words for the tweet
	 * @param kb reference knowledge base (to get IDF)
	 * @param threshold relevancy threshold 
	 */
	public void setRelevantWords(KnowledgeBase kb, Double threshold){ 
		SortedSet<WeightedWord> wset = TweetAnalyzer.relevantWords(this, kb, threshold); 
		this.relevantWords.addAll(wset);
		
	}
	
	/**
	 * Set the relevant words for the tweet using default relevancy threshold
	 * @param kb reference knowledge base (to get IDF)
	 */
	public void setRelevantWords(KnowledgeBase kb){
		
		SortedSet<WeightedWord> wset = TweetAnalyzer.relevantWords(this, kb); 
		this.relevantWords.addAll(wset);
		
	}
	
	/**
	 * 
	 * @return the tweet's hashtags
	 */
	public String[] getHashtag() {
		
		int nHashtags = nHashtags(), conta=0;
		System.out.println(nHashtags+" "+this.originaltext);
		String text = this.originaltext;
		String [] parola = new String [nHashtags];
		for(int i = 0; i<text.length(); i++) {
			
			if(text.charAt(i)=='#' && i+1<text.length()) {
		//		int contatore_1 = i;
				for(int j = i+1; j<text.length() && text.charAt(j)!=' '  && text.charAt(j)!= '#'; j++) {
					if(parola[conta]!=null)
						parola[conta]=parola[conta]+text.charAt(j);
					else
						parola[conta]=""+text.charAt(j);
					
				}
				conta++;
			}
			
				 
		}
		return parola;
		
	}
	
	/** Checks the presence of hashtags
	 * 
	 * @return true if the tweet contains hashtags
	 */
	public boolean hasHashtags() {
		String text = this.originaltext; 
		for(int i = 0; i<text.length(); i++) 
			if(text.charAt(i)=='#' && i+1<text.length()-1)
				return true;
		return false;
	
		
		
	}
	
	
	/**
	 * 
	 * @return the tweet's hashtags number
	 */
	public int nHashtags() {
		String text = this.originaltext; int conta=0;
		for(int i = 0; i<text.length(); i++) 
			if(text.charAt(i)=='#')
				conta++;
		return conta;
		
	}
	

	/**
	 * PreProcess the tweet text removing urls, usernames, non alphanumeric characters and stopwords
	 */
	public void preprocessText(){
		
		//remove CR and LF
		this.processedtext=originaltext.replaceAll("(\\r|\\n)", "");
		//remove URLs
		//this.text=text.replaceAll("https?://t.co/[^ ]*", "");
		this.processedtext=processedtext.replaceAll("https?://[^ ]*", "");
		//remove usernames
		this.processedtext=processedtext.replaceAll("@[^ ]*", "");
		//remove accents
		this.setProcessedtext(Utilities.removeAccents(processedtext));
		//lowercase
		this.processedtext=processedtext.toLowerCase();
		//remove non alphanumeric characters (# excluded!)
		this.processedtext=processedtext.replaceAll("[^a-zA-Z0-9#]", " ");
		//separate hashtags from previous word
		//this.processedtext=processedtext.replaceAll("#", " #");
		
		//REMOVE HASHTAG
		this.processedtext=processedtext.replaceAll("#", " ");
		
		//remove extra spaces
		this.processedtext=processedtext.replaceAll("  *", " ");
		//remove spaces at the beginning of the text 
		this.processedtext=processedtext.replaceAll("^ ", "");
		//DEBUG
		//System.out.println("ProcessedText: "+text);
		
		//REMOVE STOPWORDS 
		this.processedtext=Utilities.removeStopWords(this.processedtext);
		/*
		List<String> stopwords = convertire("C:/Users/Administrator/Desktop/WorkspaceTvPad/TweetAnalyzer/res/stopwords/stopwordsIT.txt");
		for(int i = 0; i<stopwords.size();i++){
			if(this.processedtext.contains(stopwords.get(i)))
				this.processedtext.replaceAll(stopwords.get(i), " ");
				
				
		}
		*/
	}
	

	public Tweet(String text) {
		super();
		this.originaltext = text;
		this.preprocessText();
		this.relevantWords = new ArrayList<WeightedWord>();
		this.positiveWords = new ArrayList<String>();
		this.insultingWords = new ArrayList<String>();
		this.likeWords = new ArrayList<String>();
		this.hilariousWords = new ArrayList<String>();
		this.sadWords = new ArrayList<String>();
		this.angryWords = new ArrayList<String>();

		this.links = new ArrayList<String>();
	}
	
	public Tweet(String text, String id) {
		super();
		this.originaltext = text;
		this.preprocessText();
		this.id = id;
		this.relevantWords = new ArrayList<WeightedWord>();
		this.positiveWords = new ArrayList<String>();
		this.insultingWords = new ArrayList<String>();
		this.links = new ArrayList<String>();
		this.likeWords = new ArrayList<String>();
		this.hilariousWords = new ArrayList<String>();
		this.sadWords = new ArrayList<String>();
		this.angryWords = new ArrayList<String>();
	}

	
	
	public String getOriginaltext() {
		return originaltext;
	}

	public void setOriginaltext(String originaltext) {
		this.originaltext = originaltext;
	}

	public String getProcessedtext() {
		return processedtext;
	}

	public void setProcessedtext(String processedtext) {
		this.processedtext = processedtext;
	}
	
	public void setSubject(String name_subject) {
		
		this.subject = name_subject;
	}
	public String getSubject() {
		
		return this.subject;
	}
	
	public void setScore(int score) {
		
		this.score=score;
	}
	
	public int getScore() {
		
		return this.score;
	}

	/**
	 * Computes the tf for all the words in a tweet
	 * @return a list of weightedwords (the tf for every word is computed)
	 */
	public List<WeightedWord> getTweetWords(){
		
		this.preprocessText();
		
		List<WeightedWord> outputlist = new ArrayList<WeightedWord>();
		
		//extract single words
		String[] words = this.processedtext.split("\\s+");
	//	String[] words = this.originaltext.split("\\s+");
		
		
		//count single word occurences
		HashMap<String,Integer> wordlist = new HashMap<String,Integer>();
		
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
			
			String word = it.next();
			Integer occurrences = wordlist.get(word);
			double tf = (double) (occurrences);
			
			WeightedWord w = new WeightedWord(word, tf);
			
			outputlist.add(w);					
		}					
		return outputlist;			
	}
	

	
	/**
	 * Set the sentiment of a tweet (positive/insulting/containing links)
	 * the list of positive words/insulting words/ links is populated
	 * @param positiveWords a list of positive words
	 * @param insultingWords a list of insulting words
	 */
	public void setSentiment(Set<String> positiveWords, Set<String> insultingWords){
		
		
		String[] wordList = this.processedtext.split("\\s+");
		String[] unprocessedWords = this.originaltext.split("\\s+");
		
		
		for(int i=0; i<wordList.length;i++){
			
			//positive words
			if(positiveWords.contains(wordList[i])){
				this.positiveWords.add(wordList[i]);	
			}
			//insulting words
			if(insultingWords.contains(wordList[i])){
				this.insultingWords.add(wordList[i]);	
			}
		}
		
		//links
		for(int i=0; i<unprocessedWords.length;i++){
						
			if(unprocessedWords[i].matches("https?://[^ ]*")){
				this.links.add(unprocessedWords[i]);
				
			}				
		}
			
	}
	
	public boolean isAlias(String emojitext, int primapos, int secondapos) {
		
		String testo = "";
		for(int i = primapos+1;i<secondapos;i++) {
			if(Character.isWhitespace(emojitext.charAt(i)))
				return false;
			testo=testo+emojitext.charAt(i);
			
		}
		
		
				return true;

		
	}
	
	public List<Integer> emojiAlias(String emojitext) {
	
	//	boolean controllo=true;
	//	int count=0;
		
		List<Integer> list = new ArrayList<>();
		List<Integer> posizioniduepuntivalide = new ArrayList<>();
	//	String emojitext = EmojiParser.parseToAliases(this.originaltext);
		for(int i = 0; i<emojitext.length();i++)
			if(emojitext.charAt(i) == ':') 
				list.add(i);
	
				
		if(list.size()==0)
			return posizioniduepuntivalide;
		if((list.size()%2)!=0 && list.size()>1)
			list.remove(list.size()-1);
		if(list.size()==2) 
			if(isAlias(emojitext, list.get(0), list.get(1))){
				posizioniduepuntivalide.add(list.get(0));
				posizioniduepuntivalide.add(list.get(1)); 
				return posizioniduepuntivalide;
			}
		
		
		
		
		if(list.size()==4) {
			if(isAlias(emojitext, list.get(0), list.get(1))) {
				posizioniduepuntivalide.add(list.get(0));
				posizioniduepuntivalide.add(list.get(1));
			}
				

					
			if(isAlias(emojitext, list.get(2), list.get(3))){
				posizioniduepuntivalide.add(list.get(2));
				posizioniduepuntivalide.add(list.get(3));
			}
			
			return posizioniduepuntivalide;
			
		}
		
		if(list.size()==6) {
			if(isAlias(emojitext, list.get(0), list.get(1)))
			{
				
				posizioniduepuntivalide.add(list.get(0));
				posizioniduepuntivalide.add(list.get(1));
			}

					
			if(isAlias(emojitext, list.get(2), list.get(3)))
			{
				
				posizioniduepuntivalide.add(list.get(2));
				posizioniduepuntivalide.add(list.get(3));
			}
			
			if(isAlias(emojitext, list.get(4), list.get(5)))
			{
			
				posizioniduepuntivalide.add(list.get(4));
				posizioniduepuntivalide.add(list.get(5));
			}
			
			return posizioniduepuntivalide;
		}
			
		if(list.size()==8) {
			if(isAlias(emojitext, list.get(0), list.get(1)))
			{
				
				posizioniduepuntivalide.add(list.get(0));
				posizioniduepuntivalide.add(list.get(1));
			}

					
			if(isAlias(emojitext, list.get(2), list.get(3)))
			{
				
				posizioniduepuntivalide.add(list.get(2));
				posizioniduepuntivalide.add(list.get(3));
			}
			
			if(isAlias(emojitext, list.get(4), list.get(5)))
			{
			
				posizioniduepuntivalide.add(list.get(4));
				posizioniduepuntivalide.add(list.get(5));
			}
			
			if(isAlias(emojitext, list.get(6), list.get(7)))
			{
			
				posizioniduepuntivalide.add(list.get(6));
				posizioniduepuntivalide.add(list.get(7));
			}
			
			return posizioniduepuntivalide;
			
		}
		
		
		if(list.size()==10) {
			if(isAlias(emojitext, list.get(0), list.get(1)))
			{
				
				posizioniduepuntivalide.add(list.get(0));
				posizioniduepuntivalide.add(list.get(1));
			}

					
			if(isAlias(emojitext, list.get(2), list.get(3)))
			{
				
				posizioniduepuntivalide.add(list.get(2));
				posizioniduepuntivalide.add(list.get(3));
			}
			
			if(isAlias(emojitext, list.get(4), list.get(5)))
			{
			
				posizioniduepuntivalide.add(list.get(4));
				posizioniduepuntivalide.add(list.get(5));
			}
			
			if(isAlias(emojitext, list.get(6), list.get(7)))
			{
			
				posizioniduepuntivalide.add(list.get(6));
				posizioniduepuntivalide.add(list.get(7));
			}
			
			if(isAlias(emojitext, list.get(8), list.get(9)))
			{
			
				posizioniduepuntivalide.add(list.get(8));
				posizioniduepuntivalide.add(list.get(9));
			}
			
			return posizioniduepuntivalide;
			
		}
		
		
		if(list.size()==12) {
			if(isAlias(emojitext, list.get(0), list.get(1)))
			{
				
				posizioniduepuntivalide.add(list.get(0));
				posizioniduepuntivalide.add(list.get(1));
			}

					
			if(isAlias(emojitext, list.get(2), list.get(3)))
			{
				
				posizioniduepuntivalide.add(list.get(2));
				posizioniduepuntivalide.add(list.get(3));
			}
			
			if(isAlias(emojitext, list.get(4), list.get(5)))
			{
			
				posizioniduepuntivalide.add(list.get(4));
				posizioniduepuntivalide.add(list.get(5));
			}
			
			if(isAlias(emojitext, list.get(6), list.get(7)))
			{
			
				posizioniduepuntivalide.add(list.get(6));
				posizioniduepuntivalide.add(list.get(7));
			}
			
			if(isAlias(emojitext, list.get(8), list.get(9)))
			{
			
				posizioniduepuntivalide.add(list.get(8));
				posizioniduepuntivalide.add(list.get(9));
			}
			
			if(isAlias(emojitext, list.get(10), list.get(11)))
			{
			
				posizioniduepuntivalide.add(list.get(10));
				posizioniduepuntivalide.add(list.get(11));
			}
			
			return posizioniduepuntivalide;
			
		}
		
					
		
			return posizioniduepuntivalide;
	//	String emojitext = EmojiParser.parseToUnicode(this.originaltext);
		
	}
	
	public String extractEmojiAlias(String emojitext, int primapos, int secondapos) {
		

//		String emojitext = EmojiParser.parseToAliases(this.originaltext);
		String alias="";
		for(int i = primapos+1;i<secondapos;i++) 
			alias=alias+emojitext.charAt(i);
	
		return alias;

		
		
	}

	
public String extractTextEmoji(String originaltext) throws FileNotFoundException, IOException {	
	List<String> lovelist = new ArrayList<>(), hilariouslist = new ArrayList<>(), sadlist = new ArrayList<>(), angrylist = new ArrayList<>();
	lovelist = convertire("res/words/Emoji/Testuali/emojilike.txt");
	hilariouslist = convertire("res/words/Emoji/Testuali/emojihilarious.txt");
	sadlist = convertire("res/words/Emoji/Testuali/emojisad.txt");
	angrylist = convertire("res/words/Emoji/Testuali/emojiangry.txt");
	
//	boolean prova = TweetAnalyzer.wordIncluded("ciao", originaltext);
	
	for(int i = 0; i<originaltext.length();i++) {
		if(lovelist.size()>i && originaltext.toLowerCase().contains(lovelist.get(i)))
			return lovelist.get(i);
		if(hilariouslist.size()>i && originaltext.toLowerCase().contains(hilariouslist.get(i)))
			return hilariouslist.get(i);
		if(angrylist.size()>i && originaltext.toLowerCase().contains(angrylist.get(i)))
			return angrylist.get(i);
		if(sadlist.size()>i && originaltext.toLowerCase().contains(sadlist.get(i)))
			return sadlist.get(i);
			
		
	}
	return null;
	
}


public static List<String> convertire(String nomeFile)
{

	Scanner key;
	List<String> list = new ArrayList<>();
	String parola="";
	try{
		key = new Scanner(new File(nomeFile));
		while(key.hasNextLine()) {
			
			parola="";
			String line = key.nextLine();
			for(int j = 0;j<line.length();j++)
				parola=parola+line.charAt(j);
				
			list.add(parola);
		}
		
	key.close();
return list;
		
	}
	
	catch (NoSuchElementException e) {
		System.out.println("errore di lettura file "+e);	
	} catch (FileNotFoundException e) {

		System.out.println("errore di lettura file "+e);
	}
	return list;
	
}

public boolean alias;
public void setAlias(boolean esito) {
	this.alias=esito;
}

public boolean getAlias() {
	return this.alias;
}



public static List<String> extractList(String nomeFile)
{

	List<String> outlist = new ArrayList<>();
	Scanner key;
	try{
		key = new Scanner(new File(nomeFile));
		while(key.hasNextLine()) {

			outlist.add(key.nextLine());
		}
	
		
	}
	
	catch (NoSuchElementException e) {
		System.out.println("errore di lettura file "+e);	
	} catch (FileNotFoundException e) {

		System.out.println("errore di lettura file "+e);
	}
	return outlist;
	
}

	
public boolean emojiSentiment () throws FileNotFoundException, IOException {
		
		
		//E' PIU' EFFICIENTE CREARE DIZIONARI APPOSITI DI FACCETTE?
		//IN MODO DA NON SCORRERE COMPLETAMENTE TUTTI I DIZIONARI DI PAROLE
	Set<String> loveLike = Utilities.wordSetFromTxtFile("res/words/Emoji/emojilike.txt");
	Set<String> hilarious = Utilities.wordSetFromTxtFile("res/words/Emoji/emojihilarious.txt");
	Set<String> sad = Utilities.wordSetFromTxtFile("res/words/Emoji/emojisad.txt");
	Set<String> angry = Utilities.wordSetFromTxtFile("res/words/Emoji/emojiangry.txt");
//		Set<String> all = Utilities.wordSetFromTxtFile("C:/Users/Administrator/Desktop/TvPadManager/TweetAnalyzer/res/words/Emoji/allemoji.txt");
		
	List<String> lovelist = new ArrayList<>(), hilariouslist = new ArrayList<>(), sadlist = new ArrayList<>(), angrylist = new ArrayList<>();
	lovelist = convertire("res/words/Emoji/emojilike.txt");
	hilariouslist = convertire("res/words/Emoji/emojihilarious.txt");
	sadlist = convertire("res/words/Emoji/emojisad.txt");
	angrylist = convertire("res/words/Emoji/emojiangry.txt");
	
	List<String> loveprelim = new ArrayList<>(), hilariousprelim = new ArrayList<>(), sadprelim = new ArrayList<>(), angryprelim = new ArrayList<>(), stopsentiment = new ArrayList<>();
	loveprelim = extractList("res/words/Emoji/Preliminari/Love.txt");
	hilariousprelim = extractList("res/words/Emoji/Preliminari/Hilarious.txt");
	sadprelim = extractList("res/words/Emoji/Preliminari/Sad.txt");
	angryprelim = extractList("res/words/Emoji/Preliminari/Angry.txt");
	stopsentiment = extractList("res/words/Emoji/Preliminari/Escludere da sentiment.txt");

	
		String emojitext = EmojiParser.parseToAliases(this.originaltext);
		
		Iterator<String> it = loveprelim.iterator();
		Iterator<String> it2 = hilariousprelim.iterator();
		Iterator<String> it3 = sadprelim.iterator();
		Iterator<String> it4 = angryprelim.iterator();
		Iterator<String> it5 = stopsentiment.iterator();
		
		while(it5.hasNext()){
			String line = it5.next();
			
			if(emojitext.toLowerCase().contains(line)){
		
				return true;
			}
				
		}
		
		while(it.hasNext()){
			String line = it.next();
			if(emojitext.toLowerCase().contains(line)){
				this.likeWords.add(line);
				setLike(true);
				return true;
			}
		}
		
		while(it2.hasNext()){
			String line = it2.next();
			if(emojitext.toLowerCase().contains(line)){
				this.hilariousWords.add(line);
				setHilarious(true);
				return true;
			}
		}
		
		while(it3.hasNext()){
			String line = it3.next();
			if(emojitext.toLowerCase().contains(line)){
				this.sadWords.add(line);
				setSad(true);
				return true;
			}
		}
		
		while(it4.hasNext()){
			String line = it4.next();
			if(emojitext.toLowerCase().contains(line)){
				this.angryWords.add(line);
				setAngry(true);
				return true;
			}
		}
		

		List<Integer> aliasposizioni = new ArrayList<>();
		aliasposizioni=emojiAlias(emojitext);

		Collections.sort(aliasposizioni);
	//	System.out.println(emojitext);
			
		List<String> alias = new ArrayList<>();
		for(int i = 0;i<aliasposizioni.size();i++) {
			alias.add(extractEmojiAlias(emojitext, aliasposizioni.get(i), aliasposizioni.get(i+1)));
			i++;
			
		}
		
		alias = new ArrayList<String>(new LinkedHashSet<String>(alias));
		
		boolean emoji = false;
		
		for(int i = 0;i<alias.size();i++) {
			

			//like words
			for(int j=0; j<lovelist.size();j++) {
				if(emojitext.contains(lovelist.get(j)) || loveLike.contains(alias.get(i))){

					if(loveLike.contains(alias.get(i)) && !getAlias()) 
						this.likeWords.add(alias.get(i));

						
					if(emoji)
					emoji=true;
					if(getAngry() || getHilarious() || getSad())
						setAmbiguo(true);
				setLike(true);
				}
				
			}
			
			
			

			//hilarious words
			for(int j=0; j<hilariouslist.size();j++) {
				if(emojitext.contains(hilariouslist.get(j)) ||  hilarious.contains(alias.get(i))){
					if( alias.get(i).contains(hilariouslist.get(j)) && !getAlias())  
						this.hilariousWords.add(alias.get(i));

					if(emoji)
						emoji=true;
						if(getAngry() || getLike() || getSad())
							setAmbiguo(true);
					setHilarious(true);
				}
				
			}
			
		
			//sad words
			for(int j=0;j<sadlist.size();j++) {
				
				if(emojitext.contains(sadlist.get(j)) || sad.contains(alias.get(i))){

					if(sad.contains(alias.get(i)) && !getAlias()) 
						this.sadWords.add(alias.get(i));

					if(emoji)
						emoji=true;
						if(getAngry() || getHilarious() || getLike())
							setAmbiguo(true);
					setSad(true);
					
				}
			}
			

			//angry words
			for(int j = 0;j<angrylist.size();j++) {
				
				if(emojitext.contains(angrylist.get(j)) || angry.contains(alias.get(i))){

					if(angry.contains(alias.get(i)) && !getAlias()) 
						this.angryWords.add(alias.get(i));

						
					if(emoji)
						emoji=true;
						if(getLike() || getHilarious() || getSad())
							setAmbiguo(true);
					setAngry(true);
					
				}
			}
			
	
			
		}
		
		return emoji;
		 
	}
	



private boolean isHilarious = false;
private boolean isSad = false;
private boolean isAngry = false;
private boolean isLike = false;



public void setAngry(boolean esito) {
	this.isAngry=esito;
	
}

public void setSad(boolean esito) {
	this.isSad=esito;
	
}

public void setHilarious(boolean esito) {
	this.isHilarious=esito;
	
}

public void setLike(boolean esito) {
	this.isLike=esito;
	
}


public boolean getHilarious() {
	return this.isHilarious;
	
}

public boolean getLike() {
	return this.isLike;
	
}


public boolean getAngry() {
	return this.isAngry;
	
}
public boolean getSad() {
	return this.isSad;
	
}


	/**
	 * Set the sentiment of a tweet (like/hilarious/sad/angry)
	 * @param loveLike 
	 * @param hilarious
	 * @param sad
	 * @param angry
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
public void setSentiment(String language) throws FileNotFoundException, IOException {
	

	String folderPath = "res/words/"+language;
	
	Set<String> loveLike = Utilities.wordSetFromTxtFile(folderPath+"/Love-Like-Wow.txt");
	Set<String> hilarious = Utilities.wordSetFromTxtFile(folderPath+"/Hilarious.txt");
	Set<String> sad = Utilities.wordSetFromTxtFile(folderPath+"/Sad.txt");
	Set<String> angry = Utilities.wordSetFromTxtFile(folderPath+"/Angry.txt");
	
	String[] wordList = this.processedtext.split("\\s+");
	
//	String[] unprocessedWords = this.originaltext.split("\\s+");
	setLike(false); 

	setHilarious(false); setAngry(false); setSad(false); 

	setAmbiguo(false);
	

	
	//VERIFICO SE SONO PRESENTI FACCETTE
	if(emojiSentiment()) {}
	
	//ALTRIMENTI VADO A CONTROLLARE I DIZIONARI PER LE PAROLE
	else {
		
		for(int i=0; i<wordList.length;i++){
				
				
				//like words
				if(loveLike.contains(wordList[i].toLowerCase())){

					this.likeWords.add(wordList[i]);
					if(getAngry() || getHilarious() || getSad())
							setAmbiguo(true);
					setLike(true);
					
				}

				//hilarious words
				if(hilarious.contains(wordList[i].toLowerCase())){
					this.hilariousWords.add(wordList[i]);
					if(getAngry() || getLike() || getSad())
						setAmbiguo(true);
				setHilarious(true);
				
				}
				
				//sad words
				if(sad.contains(wordList[i])){
					
					this.sadWords.add(wordList[i].toLowerCase());
					if(getAngry() || getLike() || getHilarious())
						setAmbiguo(true);
				setSad(true);
				
				}
				
				//angry words
				if(angry.contains(wordList[i].toLowerCase())){
					this.angryWords.add(wordList[i]);
					if(getHilarious() || getLike() || getSad())
						setAmbiguo(true);
				setAngry(true);
				
					
				}
				
			
				
				
			}

		
				
		
	}

	
	
	//links
		/*
	for(int i=0; i<unprocessedWords.length;i++){
					
		if(unprocessedWords[i].matches("https?://[^ ]*")){
			this.links.add(unprocessedWords[i]);
			
		}				
	}
	*/
	}







	
	public String getId() {
		return id;
	}

	public String getLanguage() {
		return language;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public int getFavorites() {
		return favorites;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserLocation() {
		return userLocation;
	}

	public int getUserListedCount() {
		return userListedCount;
	}

	public int getUserFollowersCount() {
		return userFollowersCount;
	}

	public List<String> getLinks() {
		return links;
	}
	
	
	public List<String> getInsultingWords() {
		return insultingWords;
	}

	public List<String> getPositiveWords() {
		return positiveWords;
	}
	

	public String getTweetID() {
		return tweetID;
	}

	/**
	 * Checks if the tweet contains insulting words
	 * @return true if the tweet contains insulting words, false otherwise
	 */
	public boolean hasInsulting() {
		if(this.insultingWords != null && !(this.insultingWords.isEmpty()))
			return true;
		return false;
	}

	/**
	 * Checks if the tweet contains positive words
	 * @return true if the tweet contains positive words, false otherwise
	 */
	public boolean hasPositive() {
		if(this.positiveWords != null && !(this.positiveWords.isEmpty()))
			return true;
		return false;
	}

	/**
	 * Check if the tweet has links/attachments
	 * @return true if the tweet contains links, false otherwise
	 */
	public boolean hasLinks(){
		if(this.links != null && !(this.links.isEmpty()))
			return true;
		return false;
	}
	
	
	
	public boolean hasLike() {
		if(this.likeWords != null && !(this.likeWords.isEmpty()))
			return true;
		return false;
	}
	
	
	
	public boolean hasHilarious() {
		if(this.hilariousWords != null && !(this.hilariousWords.isEmpty()))
			return true;
		return false;
	}
	
	
	
	
	public boolean hasSad() {
		if(this.sadWords != null && !(this.sadWords.isEmpty()))
			return true;
		return false;
	}
	
	
	
	public boolean hasAngry() {
		if(this.angryWords != null && !(this.angryWords.isEmpty()))
			return true;
		return false;
	}
	
	


}
