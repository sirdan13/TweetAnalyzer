package tweet.analyzer.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject class
 * 
 * @author TvPad
 * 
 */
public class Subject {
	
	
	List<Tweet> tweetlist;
//	ArrayList<String> listWordAssociated;
	

	/**
	 * The canonical form of a subject. Ex: Mario Balotelli
	 */
	private String canonicalForm;
	/**
	 * All the surface forms of a subject. Ex: Supermario, Balotelli, Balo,...
	 * The canonical form must be added also as surface form to be found in
	 * search
	 */
	private ArrayList<String> surfaceForms;
	

	private String tag;
	
	private String stopwords;
	
public int posFinale;
	
	public int getPosFinale(){
		
		return posFinale;
	}
	
	public void setPosFinale(int x){
		
		posFinale=x;
	}
	

	private int quantita_tweet;
	
	private int medal;
	
	public void setMedal(int val)
	{
		this.medal=val;
		
	}
	
	public int getMedal(){
		return this.medal;
	}
	
	private double like, angry, hilarious, sad;
	

	private int conteggioTweetPeriodo;
	
	private int idSubDB;

	private List<String> tweetIDList = new ArrayList<String>();

	private List<Tweet> subjectTweetList = new ArrayList<Tweet>();

	private List<String> likeTweetList = new ArrayList<String>();

	private List<String> hilariousTweetList = new ArrayList<String>();

	private List<String> angryTweetList = new ArrayList<String>();

	private List<String> sadTweetList = new ArrayList<String>();
	
	

	public Subject(String canonicalForm) {
		this.canonicalForm = canonicalForm;
	}

	public Subject(String canonicalForm, ArrayList<String> surfaceForms) {
		this.canonicalForm = canonicalForm;
		this.surfaceForms = surfaceForms;
	}

	public Subject(Integer idSub, String canonicalForm, ArrayList<String> surfaceForms) {
		this.setIdSubDB(idSub);
		this.canonicalForm = canonicalForm;
		this.surfaceForms = surfaceForms;
	}
	
	public Subject(Integer idSub, String canonicalForm, ArrayList<String> surfaceForms, String tag) {
		this.setIdSubDB(idSub);
		this.canonicalForm = canonicalForm;
		this.surfaceForms = surfaceForms;
		this.tag=tag;
	}
	
	public Subject(Integer idSub, String canonicalForm, ArrayList<String> surfaceForms, String tag, String stopwords) {
		this.setIdSubDB(idSub);
		this.canonicalForm = canonicalForm;
		this.surfaceForms = surfaceForms;
		this.tag=tag;
		this.setStopwords(stopwords);
	}

	public String getCanonicalForm() {
		return canonicalForm;
	}

	public void setCanonicalForm(String canonicalForm) {
		this.canonicalForm = canonicalForm;
	}

	public ArrayList<String> getSurfaceForms() {
		return surfaceForms;
	}

	public void setSurfaceForms(ArrayList<String> surfaceForms) {
		this.surfaceForms = surfaceForms;
	}

	@Override
	public boolean equals(Object v) {
		boolean retVal = false;

		if (v instanceof Subject) {
			Subject ptr = (Subject) v;
			retVal = ptr.canonicalForm.equals(this.canonicalForm);
		}

		return retVal;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 17 * hash + (this.canonicalForm != null ? this.canonicalForm.hashCode() : 0);
		return hash;
	}

	public int getQuantita_tweet() {
		return quantita_tweet;
	}

	public void setQuantita_tweet(int quantità_tweet) {
		this.quantita_tweet = quantità_tweet;
	}

	public int getConteggioTweetPeriodo() {
		return conteggioTweetPeriodo;
	}

	public void setConteggioTweetPeriodo(int conteggioTweetPeriodo) {
		this.conteggioTweetPeriodo = conteggioTweetPeriodo;
	}

	public double getLike() {
		return like;
	}

	public void setLike(double like) {
		this.like = like;
	}

	public double getAngry() {
		return angry;
	}

	public void setAngry(double angry) {
		this.angry = angry;
	}

	public double getHilarious() {
		return hilarious;
	}

	public void setHilarious(double hilarious) {
		this.hilarious = hilarious;
	}

	public double getSad() {
		return sad;
	}

	public void setSad(double sad) {
		this.sad = sad;
	}

	public int getIdSubDB() {
		return idSubDB;
	}

	public void setIdSubDB(int idSubDB) {
		this.idSubDB = idSubDB;
	}

	public List<String> getTweetIDList() {
		return tweetIDList;
	}

	public void setTweetIDList(List<String> tweetIDList) {
		this.tweetIDList = tweetIDList;
	}

	public List<Tweet> getSubjectTweetList() {
		return subjectTweetList;
	}

	public void setSubjectTweetList(List<Tweet> subjectTweetList) {
		this.subjectTweetList = subjectTweetList;
	}

	public List<String> getLikeTweetList() {
		return likeTweetList;
	}

	public void setLikeTweetList(List<String> likeTweetList) {
		this.likeTweetList = likeTweetList;
	}

	public List<String> getHilariousTweetList() {
		return hilariousTweetList;
	}

	public void setHilariousTweetList(List<String> hilariousTweetList) {
		this.hilariousTweetList = hilariousTweetList;
	}

	public List<String> getAngryTweetList() {
		return angryTweetList;
	}

	public void setAngryTweetList(List<String> angryTweetList) {
		this.angryTweetList = angryTweetList;
	}

	public List<String> getSadTweetList() {
		return sadTweetList;
	}

	public void setSadTweetList(List<String> sadTweetList) {
		this.sadTweetList = sadTweetList;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String [] getStopwords() {
		if(stopwords!=null)
			return stopwords.split(",");
		else 
			return null;
	}

	public void setStopwords(String stopwords) {
		this.stopwords = stopwords;
	}

	
}
