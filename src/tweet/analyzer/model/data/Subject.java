package tweet.analyzer.model.data;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;


/**
 * The persistent class for the Subject database table.
 * 
 */
@Entity
@NamedQuery(name="Subject.findAll", query="SELECT s FROM Subject s")
public class Subject implements Serializable {
	private static final long serialVersionUID = 1L;

	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private int id;
	
	@Column(name="Name")
	private String name;
	
	@Column(name="Tag")
	private String tag;
	
	@Column(name="Stopwords")
	private String stopwords;
	
	// bi-directional many-to-one association to Event
	@ManyToOne
	@JoinColumn(name = "IDEvent")
	private Event event;
	
	/*
	@ManyToOne
	@Column(name="IDEvent")
	private Event event;
	*/
	//bi-directional many-to-one association to Subject_Word
	@OneToMany(mappedBy="subject")
	private List<Subject_Word> subjectWords;



	public Subject() {
	}
	
	//public int quantità_tweet;
	
//	private int id;

	public Subject(int id, String name, String tag) {
		this.id=id;
		this.name=name;
		this.tag=tag;
	}
	
	public Subject(int id, String name, String tag, String stopwords) {
		this.id=id;
		this.name=name;
		this.tag=tag;
		this.stopwords=stopwords;
	}
	
	public String getStopwords(){
		if(stopwords!=null)
			return stopwords;
		else
			return null;
	}
	
	public void setStopwords(String stopwords){
		this.stopwords=stopwords;
	}
	


	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}	
	
	public void setTag(String tag){
		this.tag=tag;
	}
	
	public String getTag(){
		return this.tag;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
	
	//public List<tweet.analyzer.core.Tweet> tweetlist;

	public List<Subject_Word> getSubjectWords() {
		return this.subjectWords;
	}

	public void setSubjectWords(List<Subject_Word> subjectWords) {
		this.subjectWords = subjectWords;
	}

	public Subject_Word addSubjectWord(Subject_Word subjectWord) {
		getSubjectWords().add(subjectWord);
		subjectWord.setSubject(this);

		return subjectWord;
	}

	public Subject_Word removeSubjectWord(Subject_Word subjectWord) {
		getSubjectWords().remove(subjectWord);
		subjectWord.setSubject(null);

		return subjectWord;
	}

	

}