package tweet.analyzer.core;
import com.vdurmont.emoji.EmojiParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import tweet.analyzer.core.Sentiment.SentimentName;
import tweet.analyzer.datawriter.DatabaseException;
import tweet.analyzer.datawriter.DatabaseService;
import tweet.analyzer.model.data.AnalysisInterval;
import tweet.analyzer.model.data.Request;
import tweet.analyzer.model.data.Statistic_Sentiment;
import tweet.analyzer.model.data.Statistic_SentimentDB;
import tweet.analyzer.model.data.Statistic_TopMostWord;
import tweet.analyzer.model.data.Statistic_TopMostWordDB;
import tweet.analyzer.model.data.Statistic_TrendingSubject;
import tweet.analyzer.model.data.Statistic_WordCloud;
import tweet.analyzer.model.data.Subject_Word;
import tweet.analyzer.pos.POS;
import tweet.analyzer.pos.Tokenizer;
import tweet.analyzer.properties.db.DatabasePropertiesReader;
import tweet.analyzer.utilities.DBConnector;
import tweet.analyzer.utilities.WordCount;
import weka.clusterers.FilteredClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.EuclideanDistance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

/**
 * Principal class of the Tweet Analyzer project contains all methods that may
 * be invoked by external applications
 * 
 * @author TvPad
 *
 */
@SuppressWarnings("unused")
public class TweetAnalyzer {
	/**
	 * Word Relevancy Threshold: a word is considered relevant if its relevancy
	 * score is higher than this value
	 */
	final static double WORD_RELEVANCY_THRESHOLD = 2.5;
	/**
	 * /** Wordset Relevancy Treshold: a set of words is considered relevant if
	 * the sum of the relevancy scores of the contained words is higher than
	 * this value
	 */
	final static double WORDSET_RELEVANCY_THRESHOLD = 20.0;

	private static final String BASE_CONFIG_URL = "config";

	private static List<Integer> listaIdIntervalli = new ArrayList<>();

	/**
	 * Main function: for testing purpose and to show examples of methods usage
	 * 
	 * @param args
	 *            main arguments
	 */
	public static void main(String[] args) throws DatabaseException {
		
		long tempoinizialetotale = System.currentTimeMillis();
		

		HashMap<String, String> paramMap = new HashMap<String, String>();
		// check args num
		/*
		if (args.length < 1) {
			System.exit(-1);
		}

		for (String param : args) {
			String[] data = param.split("\\=");
			paramMap.put(data[0], data[1]);
		}

		if (!paramMap.containsKey("ana_int")) {
			System.exit(-1);
		}

		// read ana_int
		
		try {
			String[] strArray = paramMap.get("ana_int").split(",");
			for (int i = 0; i < strArray.length; i++) {
				if (strArray[i] != null && strArray[i] != "") {
					listaIdIntervalli.add(Integer.parseInt(strArray[i]));
				}
			}
		} catch (NumberFormatException e) {
			System.exit(-1);
		}
		*/
		String intervalli = JOptionPane.showInputDialog("Inserisci gli intervalli di analisi:");
		String [] anaint = intervalli.split(",");
		for(int i = 0;i<anaint.length;i++){
			listaIdIntervalli.add(Integer.parseInt(anaint[i]));
		}
		
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
		DecimalFormat df = (DecimalFormat) nf;

		Properties config = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream("config.properties");
			config.load(in);
			in.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String corpusfile = config.getProperty("CorpusFile");
		String vectorsfile = config.getProperty("VectorsFile");
		String DBConn = config.getProperty("DBConn");
		String DBTable = config.getProperty("DBTable");

		// System.out.println("From: " + StartTime + " To:" + EndTime);

		///// Connect to DB ->Read
		DBConnector dbc = new DBConnector(DBConn);

		// Inizio nuova parte test
		// connect to database -> Write

		// load database properties
		DatabasePropertiesReader databasePropertiesReader = new DatabasePropertiesReader(BASE_CONFIG_URL + File.separator + "database.properties");
		try {
			databasePropertiesReader.loadProperties();
		} catch (Exception e) {
			System.exit(-1);
		}
		DatabaseService dbService = new DatabaseService();
		dbService.setDriver(databasePropertiesReader.getDatabaseDriver());
		dbService.setUrl(databasePropertiesReader.getDatabaseUrl());
		dbService.setUser(databasePropertiesReader.getUserName());
		dbService.setPassword(databasePropertiesReader.getPassword());

		try {
			dbService.connect();
		} catch (Exception e) {
			// System.exit(-1);
		}

		// Fine parte nuova test

		try {
			KnowledgeBase kb = new KnowledgeBase();
			kb.createFromFiles(corpusfile, vectorsfile);
			// Set<String> stopwords =
			// Utilities.wordSetFromTxtFile("res/stopwords/stopwordsIT.txt");
			String listaIdReqeustString1 = null;
			for (Integer integ : listaIdIntervalli) {
				if (listaIdReqeustString1 == null) {
					listaIdReqeustString1 = Integer.toString(integ);
				} else {
					listaIdReqeustString1 = listaIdReqeustString1 + ",";
					listaIdReqeustString1 = listaIdReqeustString1 + Integer.toString(integ);
				}
			}
			
			System.out.println();
			System.out.println("Caricamento dizionari sentiment in corso: attendere");
			Tweet.loadDictionaries();
			System.out.println("Caricamento dizionari sentiment completato.");
			System.out.println();
		/*	System.out.println("Caricamento stopwords: attendere");
			setStopwordsSet("res/stopwords/stopwordsIT.txt");
			System.out.println("Caricamento stopwords completato.");
*/

			System.out.println("Caricamento liste stopwords: attendere");
			Utilities.loadStopwords();
			System.out.println("Caricamento liste stopwords completato.");
			System.out.println();
			
			System.out.println("Analizzo i seguenti intervalli: " + listaIdReqeustString1);
			for (Integer idAanalysisInterval : listaIdIntervalli) {
				AnalysisInterval analysisInterval = null;
				try {
					analysisInterval = dbService.getAnalysisInterval(idAanalysisInterval);
				} catch (Exception e) {
					System.out.println("Nessun intervallo trovato");
					e.printStackTrace();
					
				}
				
				
				
				
				if (analysisInterval != null) {
					Date minDate = analysisInterval.getStartDate();
					Date maxDate = analysisInterval.getEndDate();
					Integer eventId = analysisInterval.getEvent().getId();
					List<Request> listaRequest = new ArrayList<>();
					try {
						// listaRequest =
						// dbService.extractAllRequestsOfEvent(eventId.toString());
						listaRequest = dbService.extractAllRequestsInAnalysisInterval(eventId.toString(), minDate.toString(), maxDate.toString());
					} catch (Exception e) {
						System.out.println("Nessuna richiesta trovata per l'evento associato all'intervallo di analisi");
						System.exit(-1);
						e.printStackTrace();
					}

					String listaIdReqeustString = null;
					String listaKeywordsString = null;
					for (Request req : listaRequest) {
						if (listaIdReqeustString == null) {
							listaIdReqeustString = Long.toString(req.getId());
							listaKeywordsString = req.getKeywordFilter();
						} else {
							listaIdReqeustString = listaIdReqeustString + ",";
							listaIdReqeustString = listaIdReqeustString + req.getId();

							listaKeywordsString = listaKeywordsString + ",";
							listaKeywordsString = listaKeywordsString + req.getKeywordFilter();
						}
					}
					List<String> keywords = new ArrayList<String>();
					if (listaKeywordsString != null) {
						String[] keyArr = listaKeywordsString.split(",");
						for (String chiave : keyArr) {
							if (chiave != "" && !keywords.contains(chiave)) {
								keywords.add(chiave);
							}
						}
					}

					
					// ESTRARRE DALLA TABLE DEL DB "REQUEST" IL CAMPO
					// "KEYWORD_FILTER" E AGGIUNGERE LE KEYWORD A UNA
					// LIST<STRING>
					// DA PASSARE AL METODO CONTROLRELEVANTTWEETS
					// List<String> keywords = new ArrayList<String>();;
					// String
					// nomeFile="C:/Users/Administrator/Desktop/Daniele/Keywords/6.txt";
					// Scanner key;
					// try{
					// key = new Scanner(new File(nomeFile));
					// while(key.hasNextLine()) {
					// String line = key.nextLine();
					// keywords.add(line);
					// }
					// }
					//
					// catch (NoSuchElementException e) {
					// System.out.println("errore di lettura file "+e);
					// } catch (FileNotFoundException e) {
					//
					// System.out.println("errore di lettura file "+e);
					// }
					long tempoinizio = System.currentTimeMillis();
					System.out.println("---------------------------------------------------------");
					System.out.println("STATISTICHE PER L'INTERVALLO DI ANALISI \"" + analysisInterval.getName()+"\" ("+analysisInterval.getId()+")");
					System.out.println("---------------------------------------------------------");
					System.out.println("Inizio estrazione tweet: attendere");
					List<Tweet> tlist1 = dbc.extractIntervalTweets(DBTable, listaIdReqeustString, minDate.toString(), maxDate.toString());
					System.out.println("Estrazione tweet completa.");
					System.out.println("L'intervallo contiene "+tlist1.size()+" tweet.");
					
			//	for(int i = 0;i<tlist1.size();i++){
			//			String ret = rs.getString(colName);

			//	        PrintStream out = new PrintStream(System.out, false, "UTF8");  //This is the key

			//	       out.println(ret); 
			//		     if(tlist1.get(i).getOriginaltext().contains("?"))
			//	        	System.out.println(tlist1.get(i).getOriginaltext()+"\t"+tlist1.get(i).getTweetID());
			//			if(tlist1.get(i).getOriginaltext().contains("IY Real Madrid-Barcelona 1-1")){
			//				System.out.println(EmojiParser.parseToAliases(tlist1.get(i).getOriginaltext()));
			//				System.out.println(tlist1.get(i).getLanguage());
			//			}
			//				}
						
	//				System.out.println("????!");
					if(!tlist1.isEmpty())
						
					{
//						System.exit(-1);
						
						//ASSEGNO UN PUNTEGGIO AD OGNI TWEET ESTRATTO: LO SCORE DIPENDE DAL NUMERO DI KEYWORDS CONTENUTE NEL TWEET
						System.out.println("Assegnazione scores ai tweet: attendere");
						tweetScore(tlist1, keywords);
						System.out.println("Scores assegnati con successo ai tweet.");
						
						
		

						/*
						 * for(int i = 0; i<tlist1.size(); i++) {
						 * if(tlist1.get(i).getOriginaltext().contains(
						 * ":green_heart:")) {
						 * 
						 * System.out.println(tlist1.get(i).getOriginaltext());
						 * String newtext =
						 * EmojiParser.parseToUnicode(tlist1.get(i).getOriginaltext(
						 * )); System.out.println(newtext); } }
						 * 
						 * List<String> a = new ArrayList<>(), b = new
						 * ArrayList<>(); a.add("ciao"); a.add("daniele");
						 * b.addAll(a);
						 * 
						 * System.out.println(wordIncluded("daniele", b.get(1)));
						 * 
						 * // String text_with_emoji =
						 * EmojiParser.parseToUnicode(this.originaltext);
						 * 
						 * String a = "aiushkskjdadkjheartaijsiosjasiojs";
						 * 
						 * 
						 * System.out.println("Quantità totale tweet: "
						 * +tlist1.size()); for(int i = 0;i<tlist1.size();i++) {
						 * 
						 * System.out.println(tlist1.get(i).getLike()); }
						 */


						System.out.println("---------------------------------------------------------");
						System.out.println("WORD CLOUD");
						try {
							dbService.deleteAllStatistic_WordCloudOfInterval(analysisInterval.getId());
						} catch (DatabaseException e1) {
							System.out.println("Impossibile eliminare le Statistic_TopMostWord dell'intervallo di analisi " + analysisInterval.getId());
						}
						// Set<String> stopwords =
						// Utilities.wordSetFromTxtFile("res/stopwords/stopwordsIT.txt");
				//		List<Tweet> tlist2 = languageFilter(tlist1);
						SortedSet<WeightedWord> relw1 = topmostWordsFromATweetSet(tlist1, kb, 40);
				//		List<Tweet> tlist3 = sampleOtherLanguage(tlist1, "it", 10000);
				//		SortedSet<WeightedWord> relw1 = topmostWordsFromATweetSet(tlist3, kb, 40);
				//		valore del font per ogni parola della wordcloud		
				//		assegnaFontWordCloud(relw1);
						Iterator<WeightedWord> wwit1 = relw1.iterator();

						while (wwit1.hasNext()) {
							WeightedWord w1 = wwit1.next();
							Statistic_WordCloud swc = new Statistic_WordCloud();
							swc.setAnalysisInterval(analysisInterval);
							swc.setWord(w1.getType());
							swc.setWeight((int) Math.round(w1.getWeight()));
						//TODO permettere di salvare questo valore
						//	swc.setFont(w1.getFontWordCloud());
							List<String> listIDs = tweetsContainingWord(w1.getType(), tlist1);
							swc.setTweetidlist(String.join(",", listIDs));
							try {
								dbService.storeStatisticWordCloud(swc);
							} catch (DatabaseException e) {
								System.out.println("Impossibile salvare la Statistic_WordCloud dell'intervallo di analisi " + analysisInterval.getId());
							}

							System.out.println(w1.getType() + "; TF: " + w1.getTf() + "; Weight:" + df.format(w1.getWeight()));
						}
						/*
						 * NON utilizzato ai fini del WebService System.out.println(
						 * "---------------------------------------------------------"
						 * ); System.out.println(
						 * "30 TOPMOST WORDS KEYWORDS EXCLUDED");
						 * 
						 * List<String> keywordList = new ArrayList<String>();
						 * 
						 * // INSERIRE QUI LE KEYWORDS CON
						 * keywordList.add("KEYWORD");
						 * keywordList.add("quartidifinale");
						 * keywordList.add("#quartidifinale");
						 * keywordList.add("qualificati");
						 * keywordList.add("#qualificati");
						 * keywordList.add("eliminati");
						 * keywordList.add("#eliminati");
						 * keywordList.add("eliminata");
						 * keywordList.add("#eliminata");
						 * keywordList.add("graziano");
						 * keywordList.add("#graziano"); keywordList.add(
						 * "fine partita"); keywordList.add("germania");
						 * keywordList.add("#germania"); keywordList.add(
						 * "due a zero"); keywordList.add("2-0");
						 * keywordList.add("#2-0");
						 * 
						 * SortedSet<WeightedWord> relw2 =
						 * topmostWordsFromATweetSetNoKeywords(tlist1, keywordList,
						 * kb, 30); Iterator<WeightedWord> wwit2 = relw2.iterator();
						 * 
						 * while (wwit2.hasNext()) { WeightedWord w1 = wwit2.next();
						 * System.out.println(w1.getType() + "; TF: " + w1.getTf() +
						 * "; Weight:" + df.format(w1.getWeight())); }
						 */

						System.out.println("---------------------------------------------------------");
						System.out.println("SENTIMENT FOR SUBJECT");
						try {
							dbService.deleteAllStatistic_SentimentOfInterval(analysisInterval.getId());
						} catch (DatabaseException e1) {
							System.out.println("Impossibile eliminare Statistic_sentiment relativi all'intervallo di analisi" + analysisInterval.getId());
						}

						try {
							dbService.deleteAllStatistic_TrendingSubjectOfInterval(analysisInterval.getId());
						} catch (DatabaseException e) {
							System.out.println("Impossibile eliminare le Statistic_TrendingWord dell'intervallo di analisi " + analysisInterval.getId());
						}
						
						try {
							dbService.deleteAllStatistic_TopMostWordOfInterval(analysisInterval.getId());
						} catch (DatabaseException e) {
							System.out.println("Impossibile eliminare le Statistic_TopMostWord dell'intervallo di analisi " + analysisInterval.getId());
						}

						/*
						ArrayList<Subject> subjectsApp = new ArrayList<Subject>();
						ArrayList<String> subject_names = new ArrayList<String>();
						// setSentiment(tlist1, positiveWords, insultingWords);
						// int conta_soggetti=0;
						List<Subject> listsub = new ArrayList<Subject>();
						int contatore=0;
						String [] tags = analysisInterval.getEvent().getSubjectTags().split(",");
						List<tweet.analyzer.model.data.Subject> sublist = dbc.extractSubjectsTag(tags);
						for(int i = 0;i<sublist.size();i++){
							System.out.println(sublist.get(i).getName());
						}
						
						System.out.println();
						System.out.println("TAG DELL'EVENTO: "+analysisInterval.getEvent().getSubjectTags());
						System.out.println();
						*/
						
						ArrayList<Subject> subjectsApp = new ArrayList<Subject>();

						ArrayList<String> subject_names = new ArrayList<String>();
						
						String [] tags = analysisInterval.getEvent().getSubjectTags().split(",");
						List<tweet.analyzer.model.data.SubjectsTag> sublist = dbc.extractSubjectsByTag(tags);
						
						// setSentiment(tlist1, positiveWords, insultingWords);
						// int conta_soggetti=0;
						List<Subject> listsub = new ArrayList<Subject>();
						int contatore=0;
			//			for (tweet.analyzer.model.data.Subject sub : analysisInterval.getEvent().getSubjects()) {
						for (tweet.analyzer.model.data.SubjectsTag sub : sublist) {
							// Popolo la lista delle parole associate al subject
							ArrayList<String> listWordAssociated = new ArrayList<String>();
				//			listWordAssociated.add(sub.getName());
					/*		for (Subject_Word sw : sub.getSubjectWords()) {
								listWordAssociated.add(sw.getWord());
							}
					 		*/
							
							listWordAssociated=dbc.extractSubjectsWords(sub);

							
							// Creo un nuovo Subject (istanza della classe
							// applicativa, non del Database)
					//		Subject subApp = new Subject(sub.getId(), sub.getName(), listWordAssociated);
					//		Subject subApp = new Subject(sub.getId(), sub.getName(), listWordAssociated, sub.getTag());
							Subject subApp;
					//		if(sub.getStopwords().length()<=0)
							if(sub.getStopwords()==null)
								subApp = new Subject(sub.getId(), sub.getName(), listWordAssociated, sub.getTag());
							else
								subApp = new Subject(sub.getId(), sub.getName(), listWordAssociated, sub.getTag(), sub.getStopwords());
					//		System.out.println("TAAAAAAG :" +sub.getTag());
							listsub.add(subApp);
							if (!subjectsApp.contains(subApp)) {
								subjectsApp.add(subApp);
								System.out.println("TAG DEL SOGGETTO: "+subApp.getTag());
				//				List<Tweet> newList = selectTweetsForSubject(tlist, s);
								
								
								Sentiment[] sent = setAndGetSentimentForSubject(tlist1, subApp);								
								/*
								LOVELIKEWOW 0%
								HILARIOUS 100%
								SAD 0%
								ANGRY 0%
								*/
								
								listsub.get(contatore).setLike(sent[0].getValue());
								listsub.get(contatore).setHilarious(sent[1].getValue());
								listsub.get(contatore).setSad(sent[2].getValue());
								listsub.get(contatore).setAngry(sent[3].getValue());
								contatore++;

								if ((subApp.getCanonicalForm().length() + 9) < 16)
									System.out.println("SUBJECT: " + subApp.getCanonicalForm() + "\t" + "\t" + "\t" + "Num. tweet: " + subApp.getQuantita_tweet());
								if ((subApp.getCanonicalForm().length() + 9) >= 16 && (subApp.getCanonicalForm().length() + 9) < 24)
									System.out.println("SUBJECT: " + subApp.getCanonicalForm() + "\t" + "\t" + "Num. tweet: " + subApp.getQuantita_tweet());
								if ((subApp.getCanonicalForm().length() + 9) >= 24 && (subApp.getCanonicalForm().length() + 9) < 32)
									System.out.println("SUBJECT: " + subApp.getCanonicalForm() + "\t" + "Num. tweet: " + subApp.getQuantita_tweet());

								// TOPMOST PER SOGGETTO
								List<Tweet> tlist4 = new ArrayList<Tweet>();
							//	tlist4=languageFilter(subApp.getSubjectTweetList());
								//	SortedSet<WeightedWord> topmostForSubject = topmostWordsFromATweetSet(subApp.subjectTweetList, kb, 10);
								tlist4=sampleOtherLanguage(subApp.getSubjectTweetList(), "it", 1000);
								SortedSet<WeightedWord> topmostForSubject = topmostWordsFromATweetSet(tlist4, kb, 10);
								Iterator<WeightedWord> wwit2 = topmostForSubject.iterator();

								System.out.println();
								System.out.println("TOPMOST FOR SUBJECT");

								while (wwit2.hasNext()) {
									WeightedWord w1 = wwit2.next();
									tweet.analyzer.model.data.Subject newsub = new tweet.analyzer.model.data.Subject();
									Statistic_TopMostWord stmw = new Statistic_TopMostWord();
									stmw.setAnalysisInterval(analysisInterval);
									//		stmw.setSubject(sub);
									newsub.setId(sub.getId());
									newsub.setTag(sub.getTag());
									newsub.setName(sub.getName());
									//		stmw.setSubject(new tweet.analyzer.model.data.Subject(sub.getId(), sub.getName(), sub.getTag()));
									stmw.setSubject(newsub);
									stmw.setSubjectname(sub.getName());
									stmw.setSubjectwords(String.join(",", listWordAssociated));
									stmw.setWord(w1.getType());
									stmw.setWeight((int) Math.round(w1.getWeight()));
								//	List<String> listIDs = tweetsContainingWord(w1.getType(), tlist1);
									List<String> listIDs = tweetsContainingWord(w1.getType(), subApp.getSubjectTweetList());
									
									stmw.setTweetidlist(String.join(",", listIDs));
									Statistic_TopMostWordDB st = Statistic_TopMostWordDB.convert(stmw);
									//dbService.storeStatisticTopMostWord(stmw);
									dbc.storeStatisticTopMostWordDB(st);
									System.out.println(w1.getType() + "; TF: " + w1.getTf() + "; Weight:" + df.format(w1.getWeight()));
								}

								System.out.println();
								System.out.println("SENTIMENT");
								
								

								System.out.println(sent[0].getName().toString() + " " + df.format(sent[0].getValue() * 100) + "%");
								System.out.println(sent[1].getName().toString() + " " + df.format(sent[1].getValue() * 100) + "%");
								System.out.println(sent[2].getName().toString() + " " + df.format(sent[2].getValue() * 100) + "%");
								System.out.println(sent[3].getName().toString() + " " + df.format(sent[3].getValue() * 100) + "%");
								System.out.println();
								System.out.println("****************");

								// ^^^^^^^^^^^^^^^ ^^^^^^^^^^^^^^^^
								// Nome del sentiment valore del sentiment

								// Creo e popolo la statistica per l'intervallo
								// in
								// corso

								Statistic_Sentiment statSent = new Statistic_Sentiment();
								statSent.setAnalysisInterval(analysisInterval);
								tweet.analyzer.model.data.Subject newsub = new tweet.analyzer.model.data.Subject();
								newsub.setId(sub.getId());
								newsub.setTag(sub.getTag());
								newsub.setName(sub.getName());
								//TODO		statSent.setSubject(sub);
								statSent.setSubject(newsub);
								statSent.setSubjectname(sub.getName());
								statSent.setSubjectwords(String.join(",", listWordAssociated));
								if (!Double.isNaN(sent[0].getValue())) {
									statSent.setLikevalue(new BigDecimal(df.format(sent[0].getValue() * 100)));
								} else {
									statSent.setLikevalue(new BigDecimal("0"));
								}
								if (!Double.isNaN(sent[1].getValue())) {
									statSent.setHilariousvalue(new BigDecimal(df.format(sent[1].getValue() * 100)));
								} else {
									statSent.setHilariousvalue(new BigDecimal("0"));
								}
								if (!Double.isNaN(sent[2].getValue())) {
									statSent.setSadvalue(new BigDecimal(df.format(sent[2].getValue() * 100)));
								} else {
									statSent.setSadvalue(new BigDecimal("0"));
								}
								if (!Double.isNaN(sent[3].getValue())) {
									statSent.setAngryvalue(new BigDecimal(df.format(sent[3].getValue() * 100)));
								} else {
									statSent.setAngryvalue(new BigDecimal("0"));
								}

								if (sent[0].getListaIdTweet().size() > 0) {
									statSent.setLiketweetidlist(String.join(",", sent[0].getListaIdTweet()));
								}
								if (sent[1].getListaIdTweet().size() > 0) {
									statSent.setHilarioustweetidlist(String.join(",", sent[1].getListaIdTweet()));
								}
								if (sent[2].getListaIdTweet().size() > 0) {
									statSent.setSadtweetidlist(String.join(",", sent[2].getListaIdTweet()));
								}
								if (sent[3].getListaIdTweet().size() > 0) {
									statSent.setAngrytweetidlist(String.join(",", sent[3].getListaIdTweet()));
								}
								//	dbService.storeStatistic_Sentiment(statSent);
								Statistic_SentimentDB st1 = Statistic_SentimentDB.convert(statSent);
								dbc.storeStatisticSentimentDB(st1);
							}
							System.out.println();
						}

						System.out.println("---------------------------------------------------------");
						System.out.println("Caricamento soggetti in tendenza: attendere:");
						System.out.println();
						System.out.println("TRENDING SUBJECTS");

						// int nparole = subject_names.size();
						// String[] words = new String[nparole];
						// ORA WORDS è LA LISTA DI SOGGETTI SUBJECT_NAMES
						// Parole fisse, calcolate sul totale dei tweet
						// words = trendingWordsRankings(relw1, nparole);

						// WordAndID wad = wordAndId(tlist1, kb, nparole);
						// String t = wad.word;
						// System.out.println("inizio "+minDate+"\t fine"+maxDate);
				
						rankSubjects(listsub);
						List<Subject> subjects1 = null;
						if(listsub.size()>5)
							subjects1 = cutList(listsub);
						else
							subjects1 = listsub;
						assegnaMedal(subjects1);
						
						
						
						int[][] ranks = intervalTrendingWordsRankings(DBConn, listaIdReqeustString, DBTable, kb, tlist1, subjects1, minDate, maxDate);

						Subject [] subjects = new Subject[subjects1.size()];
						for(int i = 0;i<subjects.length;i++)
							subjects[i]=subjects1.get(i);
						boolean ordinato;
						do
						{
							ordinato = true;
							for(int i=0; i<subjects.length-1; i++)
							{
								if(subjects[i].getPosFinale() > subjects[i+1].getPosFinale())
								{
									ordinato = false;
									scambia(subjects,i,i+1);
									scambiaRigheMatrice(ranks,i,i+1);
								}
							}
						}
						while(!ordinato);
						
						
					
						
						// int [][] ranks = intervalTrendingWordsRankings(DBConn,
						// listaIdReqeustString1, DBTable, kb, tlist1, subs,
						// minDate, maxDate );
						List<Subject> listsub1 = new ArrayList<Subject>();
						for (int i = 0; i < subjects.length; i++) {
							listsub1.add(subjects[i]);
							subject_names.add(listsub1.get(i).getCanonicalForm());
						}

						Iterator<Subject> itsub = listsub1.iterator();
						for (int i = 0; i < ranks.length; i++) {
							Subject sub4 = itsub.next();

							if ((sub4.getCanonicalForm().length()) < 8)
								System.out.print(sub4.getCanonicalForm() + "\t" + "\t" + "\t");
							if ((sub4.getCanonicalForm().length()) >= 8 && (sub4.getCanonicalForm().length()) < 16)
								System.out.print(sub4.getCanonicalForm() + "\t" + "\t");
							if ((sub4.getCanonicalForm().length()) >= 16)
								System.out.print(sub4.getCanonicalForm() + "\t");
							for (int j = 0; j < ranks[0].length; j++) {
								System.out.print(ranks[i][j] + "\t");
							}
							System.out.println();
						}

						// SALVATAGGIO TRENDING NEL DATABASE
						// vvvvvvvvvvvvvvvvvvvvvvvvvvvvv
						// listsub.remo;
						
						//USARE LISTSUB1 (LISTA DI SOGGETTI GIà TAGLIATA PER I TOP 5)
					//	for(int i = 0;i<listsub1.size();i++)
						
						
						
					
						
						itsub = listsub1.iterator();
						for (int i = 0; i < listsub1.size(); i++) {
							Subject sub4 = itsub.next();
							Statistic_TrendingSubject sw = new Statistic_TrendingSubject();
							sw.setAnalysisInterval(analysisInterval);
							sw.setIdSubject(sub4.getIdSubDB());

		
							sw.setMedal(sub4.getMedal());
					//		System.out.println(listsub1.get(i).getCanonicalForm()+":\t"+sub4.getMedal());
							
							//
							
							
							sw.setSubjectName(subject_names.get(i));

							// List<String> listIDs =
							// tweetsContainingWord(subject_names.get(i), tlist1);
							List<String> listIds = sub4.getTweetIDList();
							sw.setTweetidlist(String.join(",", listIds));

							for (int j = 0; j < 5; j++) {
								switch (j) {
								case 0:
									sw.setInterval1(ranks[i][j]);
									break;
								case 1:
									sw.setInterval2(ranks[i][j]);
									break;
								case 2:
									sw.setInterval3(ranks[i][j]);
									break;
								case 3:
									sw.setInterval4(ranks[i][j]);
									break;
								case 4:
									sw.setInterval5(ranks[i][j]);
									break;
								default:
									break;
								}

							}
							try {
								dbService.storeStatisticTrendingSubject(sw);
							} catch (DatabaseException e) {
								System.out.println("Impossibile salvare la Statistic_TrendingSubject dell'intervallo di analisi");
							}
						}

						/*
						 * System.out.println(
						 * "---------------------------------------------------------"
						 * ); System.out.println("SENTIMENT");
						 * 
						 * Set<String> positiveWords =
						 * Utilities.wordSetFromTxtFile("res/words/positive.txt");
						 * Set<String> insultingWords =
						 * Utilities.wordSetFromTxtFile("res/words/scurrili.txt");
						 * 
						 * setSentiment(tlist1, positiveWords, insultingWords);
						 * Sentiment[] s1 = getSentiment(tlist1);
						 * 
						 * for (int i = 0; i < s1.length; i++) {
						 * System.out.println(s1[i].getName().toString() + " " +
						 * df.format(s1[i].getValue() * 100) + "%"); }
						 */

						// Fine leggere Subject da DB

						// Iterator<Subject> subjit = subjects.iterator();
						// Iterator<Subject> subjit = subjectsApp.iterator();
						// while (subjit.hasNext()) {
						// Subject s = subjit.next();
						// System.out.println("SUBJECT: " + s.getCanonicalForm());
						// Sentiment[] sent = getSentimentForSubject(tlist1, s);
						// for (int i = 0; i < sent.length; i++) {
						// System.out.println(sent[i].getName().toString() + " " +
						// df.format(sent[i].getValue() * 100) + "%");
						// }
						//
						// System.out.println("*******************");
						// }
						/*
						 * System.out.println(
						 * "---------------------------------------------------------"
						 * ); System.out.println("TOPMOST FOR SUBJECT");
						 * 
						 * subjit = subjects.iterator(); while (subjit.hasNext()) {
						 * Subject s = subjit.next(); List<Tweet> stl =
						 * selectTweetsForSubject(tlist1, s); List<String>
						 * excludedWords = new ArrayList<String>();
						 * excludedWords.addAll(s.getSurfaceForms());
						 * excludedWords.addAll(keywordList);
						 * SortedSet<WeightedWord> topmost =
						 * topmostWordsFromATweetSetNoKeywords(stl, excludedWords,
						 * kb, 10);
						 * 
						 * System.out.println("10 topmost words for subject " +
						 * s.getCanonicalForm()); int i = 1;
						 * 
						 * Iterator<WeightedWord> topit = topmost.iterator(); while
						 * (topit.hasNext()) { WeightedWord ww = topit.next();
						 * System.out.println( +i + "- " + ww.getType() + "; TF: " +
						 * ww.getTf() + "; Weight:" + df.format(ww.getWeight()));
						 * i++; }
						 * 
						 * System.out.println("*******************");
						 * 
						 * }
						 * 
						 * System.out.println(
						 * "---------------------------------------------------------"
						 * ); System.out.println("CLUSTERING");
						 * 
						 * SortedSet<WeightedWord> topwords =
						 * topmostWordsFromATweetSetNoKeywords(tlist1, keywordList,
						 * kb, 100); Integer nclust = topwords.size() / 10;
						 * 
						 * System.out.println("WORDS: " + topwords.size() +
						 * "; CLUSTERS: " + nclust);
						 * 
						 * List<Cluster> clusters = new ArrayList<Cluster>();
						 * clusters = wordClusterer(topwords, kb, nclust);
						 * 
						 * System.out.println("Cluster Assignments"); for (int i =
						 * 0; i < clusters.size(); i++) {
						 * 
						 * Cluster cluster = clusters.get(i); List<WeightedWord>
						 * clusteredwords = new ArrayList<WeightedWord>();
						 * clusteredwords = cluster.getWords();
						 * cluster.setHighestWeightWord();
						 * 
						 * System.out.println("Cluster " + i +
						 * " highest weight word: " +
						 * cluster.getHighestWeightWord().getType() +
						 * " representative word: " +
						 * cluster.getRepresentativeWord().getType());
						 * 
						 * System.out.println("Words in cluster " + i + ": " +
						 * clusteredwords.size()); // print words
						 * Iterator<WeightedWord> itw = clusteredwords.iterator();
						 * while (itw.hasNext()) {
						 * 
						 * WeightedWord ww = itw.next();
						 * System.out.println(ww.getType()); }
						 * 
						 * }
						 * 
						 * System.out.println(
						 * "---------------------------------------------------------"
						 * );
						 * 
						 * System.out.println("POS-TAGGING");
						 * 
						 * POS postagger = new
						 * POS("res/opennlp/it-pos-perceptron.bin"); Tokenizer tk =
						 * new Tokenizer("res/opennlp/it-token.bin");
						 * 
						 * subjit = subjects.iterator(); while (subjit.hasNext()) {
						 * 
						 * Subject s = subjit.next();
						 * 
						 * List<Tweet> selectedTweets =
						 * selectTweetsForSubject(tlist1, s);
						 * 
						 * List<Subject> selectedSubj = new ArrayList<Subject>();
						 * selectedSubj.add(s);
						 * 
						 * System.out.println("Soggetto " + s.getCanonicalForm());
						 * String[] tagseq = { "N", "V" }; SortedSet<WeightedString>
						 * slist = sequenceForSubjects(selectedTweets, selectedSubj,
						 * tagseq, postagger, tk, kb, stopwords);
						 * Iterator<WeightedString> strit = slist.iterator(); while
						 * (strit.hasNext()) { WeightedString ws = strit.next();
						 * System.out.println( "[N V]" + ws.getString() + " TF " +
						 * ws.getTF() + "; Weight " + df.format(ws.getWeight())); }
						 * 
						 * tagseq[0] = "V"; tagseq[1] = "N"; slist =
						 * sequenceForSubjects(selectedTweets, selectedSubj, tagseq,
						 * postagger, tk, kb, stopwords); strit = slist.iterator();
						 * while (strit.hasNext()) { WeightedString ws =
						 * strit.next(); System.out.println( "[V N]" +
						 * ws.getString() + " TF " + ws.getTF() + "; Weight " +
						 * df.format(ws.getWeight())); }
						 * 
						 * tagseq[0] = "R"; tagseq[1] = "V"; slist =
						 * sequenceForSubjects(selectedTweets, selectedSubj, tagseq,
						 * postagger, tk, kb, stopwords); strit = slist.iterator();
						 * while (strit.hasNext()) { WeightedString ws =
						 * strit.next(); System.out.println( "[R V]" +
						 * ws.getString() + " TF " + ws.getTF() + "; Weight " +
						 * df.format(ws.getWeight())); }
						 * 
						 * tagseq[0] = "V"; tagseq[1] = "R"; slist =
						 * sequenceForSubjects(selectedTweets, selectedSubj, tagseq,
						 * postagger, tk, kb, stopwords); strit = slist.iterator();
						 * while (strit.hasNext()) { WeightedString ws =
						 * strit.next(); System.out.println( "[V R]" +
						 * ws.getString() + " TF " + ws.getTF() + "; Weight " +
						 * df.format(ws.getWeight())); }
						 * 
						 * tagseq[0] = "R"; tagseq[1] = "G"; slist =
						 * sequenceForSubjects(selectedTweets, selectedSubj, tagseq,
						 * postagger, tk, kb, stopwords); strit = slist.iterator();
						 * while (strit.hasNext()) { WeightedString ws =
						 * strit.next(); System.out.println( "[R G]" +
						 * ws.getString() + " TF " + ws.getTF() + "; Weight " +
						 * df.format(ws.getWeight())); }
						 * 
						 * tagseq[0] = "G"; tagseq[1] = "R"; slist =
						 * sequenceForSubjects(selectedTweets, selectedSubj, tagseq,
						 * postagger, tk, kb, stopwords); strit = slist.iterator();
						 * while (strit.hasNext()) { WeightedString ws =
						 * strit.next(); System.out.println( "[G R]" +
						 * ws.getString() + " TF " + ws.getTF() + "; Weight " +
						 * df.format(ws.getWeight())); }
						 * 
						 * String[] tagseq2 = { "R", "V", "D", "G" }; slist =
						 * sequenceForSubjects(selectedTweets, selectedSubj,
						 * tagseq2, postagger, tk, kb, stopwords); strit =
						 * slist.iterator(); while (strit.hasNext()) {
						 * WeightedString ws = strit.next(); System.out.println(
						 * "[R V D G]" + ws.getString() + " TF " + ws.getTF() +
						 * "; Weight " + df.format(ws.getWeight())); }
						 * System.out.println("******************");
						 * 
						 * }
						 */
						analysisInterval.setCompleted(true);
						try {
							dbService.storeAnalysisInterval(analysisInterval);
						} catch (DatabaseException e) {

							e.printStackTrace();
						}
					}
					
					long tempofine = System.currentTimeMillis();
					long tempototale = tempofine-tempoinizio;
					System.out.println();
					System.out.println("TEMPO IMPIEGATO");
					System.out.println("L'analisi dell'intervallo ha impiegato "+tempototale/1000+" secondi.");
					}
				
					
				
			}
			long tempototalefinale = System.currentTimeMillis();
			long tempototale=tempototalefinale-tempoinizialetotale;
			System.out.println();
			System.out.println("TEMPO IMPIEGATO");
			System.out.println("L'analisi totale ha impiegato circa "+tempototale/1000/60+" minuti ("+tempototale/1000+" secondi).");
			System.exit(0);
		} catch (IOException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Allows to have a tweet list made mostly by the selected language. The number of "foreign" tweets
	 * to be skipped is customizable, as is the preferred language.
	 * 
	 * @param 	tlist
	 * 			tweetlist to be checked	
	 * @param	n
	 * 			number of other-language tweets to be skipped
	 * @return 	a new tweetlist
	 */
private static List<Tweet> sampleOtherLanguage(List<Tweet> tlist, String language, int n) {
		List<Tweet> outlist = new ArrayList<Tweet>();
		Iterator<Tweet> it = tlist.iterator();
		int contatore = 0;
		while (it.hasNext()) {
			Tweet t = it.next();
			//foreign tweets
			if(!t.getLanguage().contains(language)){
				if(contatore==0)
					outlist.add(t);
				else{
					if(contatore%n==0)
						outlist.add(t);
						
				}
				contatore++;
			}
			else
				outlist.add(t);
				
		}
		
		return outlist;
	}

//	private static List<String> tweetsContainingWord(String type,  List<Tweet> tlist1)

	

	private static ArrayList<Tweet> languageFilter(List<Tweet> subjectTweetList) {
		
		ArrayList<Tweet> newlist = new ArrayList<Tweet>();
		for(int i = 0;i<subjectTweetList.size();i++){
			if(subjectTweetList.get(i).getLanguage().contains("it"))
			{
				newlist.add(subjectTweetList.get(i));
			//	System.out.println(subjectTweetList.get(i).getLanguage());
			}	
		
				
			
			
		}
		return newlist;
		
	}

	private static void assegnaMedal(List<Subject> listsub) {

		/*
		COMBINAZIONI
		1: TOP TWEETED
		2: TOP LIKE
		3: TOP ANGRY
		4: TOP LIKE+TOP TWEETED
		5: TOP LIKE+TOP ANGRY
		6: TOP TWEETED+TOP ANGRY
		7: TOP ANGRY + TOP LIKE + TOP TWEETED
		*/
		
		Subject toptweeted = null, topangry = null, toplike = null;
		int toptweet=0; double ntopangry=0, ntoplike=0;
		
		
		
		if(listsub.size()<5){
			for(int i = 0; i<listsub.size(); i++)
			{
				

				if(listsub.get(i).getQuantita_tweet()>toptweet) {
					toptweeted=listsub.get(i);
					toptweet=listsub.get(i).getQuantita_tweet();

				}
					
				if(listsub.get(i).getAngry()>ntopangry) {
					topangry=listsub.get(i);
					ntopangry=listsub.get(i).getAngry();

					
				}
					
				if(listsub.get(i).getLike()>ntoplike) {
		
					toplike=listsub.get(i);
					ntoplike=listsub.get(i).getLike();
					
				}
					
			}
			
		}
		
		else{
			for(int i = 0; i<5; i++)
			{
				

				if(listsub.get(i).getQuantita_tweet()>toptweet) {
					toptweeted=listsub.get(i);
					toptweet=listsub.get(i).getQuantita_tweet();

				}
					
				if(listsub.get(i).getAngry()>ntopangry) {
					topangry=listsub.get(i);
					ntopangry=listsub.get(i).getAngry();

					
				}
					
				if(listsub.get(i).getLike()>ntoplike) {
		
					toplike=listsub.get(i);
					ntoplike=listsub.get(i).getLike();
					
				}
					
			}
			
		}

		for(int i = 0; i<listsub.size(); i++) {
			
			if(listsub.get(i)==toptweeted)
				listsub.get(i).setMedal(1);
			
			if(listsub.get(i)==toplike)
				listsub.get(i).setMedal(2);
			
			if(listsub.get(i)==topangry)
				listsub.get(i).setMedal(3);
			
			if(listsub.get(i)==toptweeted && listsub.get(i)==toplike)
				listsub.get(i).setMedal(4);
			
			if(listsub.get(i)==toplike && listsub.get(i)==topangry)
				listsub.get(i).setMedal(5);
			
			if(listsub.get(i)==toptweeted && listsub.get(i)==topangry)
				listsub.get(i).setMedal(6);
			
			if(listsub.get(i)==toptweeted && listsub.get(i)==toplike && listsub.get(i)==topangry)
				listsub.get(i).setMedal(7);
			}
		
	}

	private static void tweetScore(List<Tweet> tlist1, List<String> keywords) {

		String[] arrayk = new String[keywords.size()];
		keywords.toArray(arrayk);
		for (int i = 0; i < tlist1.size(); i++) {

			for (int j = 0; j < arrayk.length; j++) {

				if (tlist1.get(i).getOriginaltext().contains(arrayk[j])) {

					if (tlist1.get(i).getScore() < 1) {
						tlist1.get(i).setScore(1);
					} else {
						if (tlist1.get(i).getScore() == 1) {
							tlist1.get(i).setScore(2);
						} else if (tlist1.get(i).getScore() > 1) {
							tlist1.get(i).setScore(tlist1.get(i).getScore() * tlist1.get(i).getScore());
						}
					}

				}

			}
		}

	}

	/**
	 * Check if a word is included in a tweet
	 * 
	 * @param word
	 *            that the tweet must contain
	 * @param t
	 *            tweet
	 * @return true/false if the word is/isn't included
	 */

	public static boolean wordIncluded(String word, Tweet t) {

		if (t.getOriginaltext().toLowerCase().contains(word))
			return true;
		else
			return false;

	}

	public static boolean wordIncluded(String word, String text) {

		if (text.contains(word))
			return true;
		else
			return false;

	}

	/**
	 * Returns a List of TWEET_ID containing a particular word
	 * 
	 * @param word
	 *            that the tweet must contains
	 * @param tweetList
	 *            tweets of the interval
	 * @return a List of strings (TWEET_ID)
	 */
	public static List<String> tweetsContainingWord(String word, List<Tweet> tweetList) {
		List<String> listIDs = new ArrayList<>();
		
	//	List<Tweet> sortedList = rankByScore(tweetList);
		rankByScore(tweetList);
		rankByLanguage(tweetList);
		Tweet t;
		for (int i = 0; i < tweetList.size(); i++) {
			t = tweetList.get(i);
			if (wordIncluded(word, t))
				listIDs.add(t.getTweetID());
		}

		return listIDs;
	}

	/**
	 * Returns a WordAndID object, which contains an array of words and the list
	 * of the tweets' ids containing the words
	 * 
	 * @param tlist
	 *            tweetlist
	 * @param kb
	 *            KnowledgeBase
	 * @param k
	 *            the first k topmost words to be saved
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @returns a WordAndID object
	 * 
	 */

	@SuppressWarnings("null")
	public static WordAndID wordAndId(List<Tweet> tlist, KnowledgeBase kb, int k) throws FileNotFoundException, IOException {

		WordAndID a = new WordAndID();
		List<List<String>> ids = null;
		String[] topmost = trendingWordsRankings(tlist, kb, k);
		for (int i = 0; i < topmost.length; i++) {
			ids.add(tweetsContainingWord(topmost[i], tlist));
		}
		a.idlist = ids;
		a.words = topmost;

		return a;
	}

	/**
	 * Returns an array of the first k most relevant words in a SortedSet
	 * 
	 * @param relw1
	 *            a sorted set of weighted words
	 * @param k
	 *            number of words to be sorted
	 * @return an array of words (their index shows the ranking in the defined
	 *         interval)
	 */

	public static String[] trendingWordsRankings(SortedSet<WeightedWord> relw1, int k) {

		Iterator<WeightedWord> wwit1 = relw1.iterator();
		String[] words = new String[k];
		int count = 0;
		while (wwit1.hasNext() && count < words.length) {
			WeightedWord w1 = wwit1.next();
			words[count] = w1.getType();
			count++;
		}

		return words;

		// CONFRONTO TRA STRINGHE: IF(STRINGA.EQUALS("STRINGADACONFRONTARE"));

	}

	/**
	 * Returns an integer of 4 numbers meaning the year of the date in input
	 * 
	 * @param date
	 *            the date where the output year comes from
	 * @return an integer meaning the year
	 */

	public static int getYear(String date) {
		int a = date.charAt(0), b = date.charAt(1), c = date.charAt(2), d = date.charAt(3);
		int a_numeric = Character.getNumericValue(a);
		int b_numeric = Character.getNumericValue(b);
		int c_numeric = Character.getNumericValue(c);
		int d_numeric = Character.getNumericValue(d);
		String data_year1 = "" + a_numeric + b_numeric + c_numeric + d_numeric;
		int data_year = Integer.parseInt(data_year1);
		return data_year;
	}

	/**
	 * Returns an integer of 2 numbers meaning the month of the date in input
	 * 
	 * @param date
	 *            the date where the output month comes from
	 * @return an integer meaning the month
	 */
	public static int getMonth(String date) {
		int a = date.charAt(5), b = date.charAt(6);
		int a_numeric = Character.getNumericValue(a);
		int b_numeric = Character.getNumericValue(b);
		String data_month1 = "" + a_numeric + b_numeric;
		int data_month = Integer.parseInt(data_month1);
		return data_month;
	}

	/**
	 * Returns an integer of 2 numbers meaning the day of the date in input
	 * 
	 * @param date
	 *            the date where the output day comes from
	 * @return an integer meaning the day
	 */
	public static int getDay(String date) {
		int a = date.charAt(8), b = date.charAt(9);
		int a_numeric = Character.getNumericValue(a);
		int b_numeric = Character.getNumericValue(b);
		String data_day1 = "" + a_numeric + b_numeric;
		int data_day = Integer.parseInt(data_day1);
		return data_day;
	}

	/**
	 * Returns an integer of 2 numbers meaning the minutes of the date in input
	 * 
	 * @param date
	 *            the date where the output minutes come from
	 * @return an integer meaning the minutes
	 */
	public static int getMinutes(String date) {
		int a = date.charAt(14), b = date.charAt(15);
		int a_numeric = Character.getNumericValue(a);
		int b_numeric = Character.getNumericValue(b);
		String data_minutes1 = "" + a_numeric + b_numeric;
		int data_minutes = Integer.parseInt(data_minutes1);
		return data_minutes;
	}

	/**
	 * Returns an integer of 2 numbers meaning the hour of the date in input
	 * 
	 * @param date
	 *            the date where the output hour comes from
	 * @return an integer meaning the hour
	 */
	public static int getHour(String date) {
		int a = date.charAt(11), b = date.charAt(12);
		int a_numeric = Character.getNumericValue(a);
		int b_numeric = Character.getNumericValue(b);
		String data_hour1 = "" + a_numeric + b_numeric;
		int data_hour = Integer.parseInt(data_hour1);
		return data_hour;
	}

	/**
	 * Returns an integer of 2 numbers meaning the seconds of the date in input
	 * 
	 * @param date
	 *            the date where the output minutes come from
	 * @return an integer meaning the minutes
	 */
	public static int getSeconds(String data) {
		int a = data.charAt(17), b = data.charAt(18);
		int a_numeric = Character.getNumericValue(a);
		int b_numeric = Character.getNumericValue(b);
		String data_seconds1 = "" + a_numeric + b_numeric;
		int data_seconds = Integer.parseInt(data_seconds1);
		return data_seconds;
	}

	/**
	 * Returns an array of the first k most relevant words in a tweetlist
	 * 
	 * @param tlist
	 *            a list of tweets
	 * @param kb
	 *            the reference knowledge based (needed to get IDF)
	 * @param k
	 *            number of words to be sorted
	 * @return an array of words (their index shows the ranking in the defined
	 *         interval)
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */

	public static String[] trendingWordsRankings(List<Tweet> tlist, KnowledgeBase kb, int k) throws FileNotFoundException, IOException {

		SortedSet<WeightedWord> relw1 = topmostWordsFromATweetSet(tlist, kb, k);
		Iterator<WeightedWord> wwit1 = relw1.iterator();
		String[] words = new String[k];
		int count = 0;
		while (wwit1.hasNext() && count < words.length) {
			WeightedWord w1 = wwit1.next();
			words[count] = w1.getType();
			count++;
		}

		return words;
	}

	// CONFRONTO TRA STRINGHE: IF(STRINGA.EQUALS("STRINGADACONFRONTARE")); }

	/**
	 * Returns a String containing a Date in the yyyy-mm-dd hh:mm:ss format
	 * 
	 * @param Date
	 *            date to be converted
	 * @return a String containing a Date in the yyyy-mm-dd hh:mm:ss format
	 */
	public static String dateToString(Date date) {

		String data_convertita = "";
		int anno, mese, giorno, ore, minuti, secondi;
		String year, month, day, hour, minutes, seconds;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		anno = cal.get(Calendar.YEAR);
		mese = cal.get(Calendar.MONTH) + 1;
		giorno = cal.get(Calendar.DAY_OF_MONTH);
		ore = cal.get(Calendar.HOUR_OF_DAY);
		minuti = cal.get(Calendar.MINUTE);
		secondi = cal.get(Calendar.SECOND);
		year = Integer.toString(anno);
		month = Integer.toString(mese);
		day = Integer.toString(giorno);
		hour = Integer.toString(ore);
		minutes = Integer.toString(minuti);
		seconds = Integer.toString(secondi);
		// date.get
		if (mese < 10)
			month = "0" + month;
		if (ore < 10)
			hour = "0" + hour;
		if (minuti < 10)
			minutes = "0" + minutes;
		if (secondi < 10)
			seconds = "0" + seconds;
		if (giorno < 10)
			day = "0" + day;
		data_convertita = year + "-" + "" + month + "" + "-" + "" + day + "" + " " + "" + hour + "" + ":" + "" + minutes + "" + ":" + "" + seconds;
		return data_convertita;

	}

	// DA COMPLETARE

	/**
	 * Returns a matrix that specifies the standings of the most relevant words
	 * in several time intervals
	 * 
	 * @param DBConn
	 *            the db connector
	 * @param requestID
	 *            value of the request_id field
	 * @param DBTable
	 *            the DB table to query
	 * @param kb
	 *            the reference knowledge based (needed to get IDF)
	 * @param startDate
	 *            Start date in the format 'YYYY-DD-MM hh:mm:ss' (warning,
	 *            locale settings affect date format!)
	 * @param endDate
	 *            End date in the format 'YYYY-DD-MM hh:mm:ss' (warning, locale
	 *            settings affect date format!)
	 * @return a matrix that specifies the standings of the most relevant words
	 *         in several time intervals
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	/*
	 * public static int[][] intervalTrendingWordsRankings(String DBConn, String
	 * requestID, String DBTable, KnowledgeBase kb, String startDate, String
	 * endDate, List<Tweet> tlist1, int nwords) throws ClassNotFoundException,
	 * SQLException {
	 * 
	 * Date start = convertDate(startDate), end = convertDate(endDate); //
	 * System.out.println(startDate+" "+endDate); int diff = minutesDiff(start,
	 * end); int intervals = 0;
	 * 
	 * if((diff%5)==0) intervals = 5; else if((diff%4)==0) intervals = 4; else {
	 * System.out.println("Intervallo non divisibile in 4 o 5 parziali.");
	 * return null; }
	 * 
	 * /* if ((diff % 4) == 0) { if ((diff % 5) == 0) intervals = 5; else
	 * intervals = 4; } else { if ((diff % 5) == 0) intervals = 5; else {
	 * System.out.println("Intervallo non divisibile in 4 o 5 parziali.");
	 * return null; }
	 * 
	 * 
	 * // }
	 * 
	 * int[][] words_ranks = new int[nwords][intervals]; String[] words = new
	 * String[nwords]; DBConnector dbc = new DBConnector(DBConn); int conta = 0;
	 * List<Tweet> tlist = dbc.extractIntervalTweets(DBTable, requestID,
	 * startDate, endDate); if (tlist.isEmpty()) { return null; } words =
	 * trendingWordsRankings(tlist, kb, nwords); // PAROLE FISSE, // CALCOLATE
	 * // SULL'INTERO // INTERVALLO DEI // TWEET String[] words_interval = new
	 * String[nwords]; // CLASSIFICA DELLE PAROLE // NEL RISPETTIVO //
	 * INTERVALLO //System.out.println(startDate+"--------------"+endDate); int
	 * k = 1; end = addMins(end, -(intervals - k) * (diff / intervals)); endDate
	 * = dateToString(end); // int conta2=0; int contaFal=0; boolean
	 * fallito=true; int [] falliti = new int [intervals]; for (int j = 0; j <
	 * intervals; j++) {
	 * 
	 * tlist = dbc.extractIntervalTweets(DBTable, requestID, startDate,
	 * endDate); words_interval = trendingWordsRankings(tlist, kb, nwords);
	 * 
	 * for (int i = 0; i < words.length; i++) { conta = 0; while (conta <
	 * words.length && conta<words_interval.length) { if(words[i]==null ||
	 * words_interval[conta]==null) { fallito=true; falliti[contaFal]=j;
	 * contaFal++; break;
	 * 
	 * }
	 * 
	 * if (words[i].compareTo(words_interval[conta]) == 0 ) words_ranks[i][j] =
	 * conta + 1; conta++; } if(fallito) { fallito = false; break; }
	 * 
	 * } end = convertDate(endDate); end = addMins(end, (diff / intervals));
	 * endDate = dateToString(end); start = convertDate(startDate); start =
	 * addMins(start, (diff / intervals)); if (k == 1) start = addSecs(start,
	 * 1); startDate = dateToString(start); k++; } return words_ranks; }
	 * 
	 * 
	 */

	public static void scambia(int[] v, int a, int b) {

		int temp = v[a];
		v[a] = v[b];
		v[b] = temp;

	}

	public static void scambia(String[] v, int a, int b) {

		String temp = v[a];
		v[a] = v[b];
		v[b] = temp;

	}
	
	public static void scambia(Subject[] v, int a, int b) {

		Subject temp = v[a];
		v[a] = v[b];
		v[b] = temp;

	}

	public static boolean ordinato(int[] v) {

		for (int i = 0; i < v.length; i++) {
			for (int j = 0; j < v.length; j++) {

				if (i < j)
					if (v[i] < v[j])
						return false;

			}

		}

		return true;

	}

	
	public static List<Subject> cutList(List<Subject> subjects){
		
		List<Subject> out = new ArrayList<Subject>();
		rankSubjects(subjects);
		for(int i = 0;i<5;i++)
			out.add(subjects.get(i));
		return out;
	}
	
	public static Subject[] cutList2(List<Subject> osubjects) {

		Subject[] out = new Subject[osubjects.size()];
		if (osubjects.size() <= 5) {
			return osubjects.toArray(out);
		}

		Subject sub1, sub2, sub3, sub4, sub5;
		int max = 0;
		int indice = 0;

		for (int i = 0; i < osubjects.size(); i++)

			if (osubjects.get(i).getQuantita_tweet() > max) {

				max = osubjects.get(i).getQuantita_tweet();
				indice = i;
			}

		sub1 = osubjects.get(indice);
		osubjects.remove(indice);

		max = 0;
		indice = 0;
		for (int i = 0; i < osubjects.size(); i++)

			if (osubjects.get(i).getQuantita_tweet() > max) {

				max = osubjects.get(i).getQuantita_tweet();
				indice = i;
			}
		sub2 = osubjects.get(indice);
		osubjects.remove(indice);

		max = 0;
		indice = 0;
		for (int i = 0; i < osubjects.size(); i++)

			if (osubjects.get(i).getQuantita_tweet() > max) {

				max = osubjects.get(i).getQuantita_tweet();
				indice = i;
			}
		sub3 = osubjects.get(indice);
		osubjects.remove(indice);

		max = 0;
		indice = 0;
		for (int i = 0; i < osubjects.size(); i++)

			if (osubjects.get(i).getQuantita_tweet() > max) {

				max = osubjects.get(i).getQuantita_tweet();
				indice = i;
			}
		sub4 = osubjects.get(indice);
		osubjects.remove(indice);

		max = 0;
		indice = 0;
		for (int i = 0; i < osubjects.size(); i++)

			if (osubjects.get(i).getQuantita_tweet() > max) {

				max = osubjects.get(i).getQuantita_tweet();
				indice = i;
			}
		sub5 = osubjects.get(indice);
		osubjects.remove(indice);
		/*
		 * 
		 * System.out.println("Soggetto: "+sub1.getCanonicalForm()+"\t Tweet: "
		 * +sub1.quantità_tweet); System.out.println("Soggetto: "
		 * +sub2.getCanonicalForm()+"\t Tweet: "+sub2.quantità_tweet);
		 * System.out.println("Soggetto: "+sub3.getCanonicalForm()+"\t Tweet: "
		 * +sub3.quantità_tweet); System.out.println("Soggetto: "
		 * +sub4.getCanonicalForm()+"\t Tweet: "+sub4.quantità_tweet);
		 * System.out.println("Soggetto: "+sub5.getCanonicalForm()+"\t Tweet: "
		 * +sub5.quantità_tweet);
		 */
		Subject[] subjects = { sub1, sub2, sub3, sub4, sub5 };
		return subjects;

	}
	
	
	public static void rankByLanguage(List<Tweet> tlist){
		
//		ArrayList<Tweet> outlist = new ArrayList<Tweet>();
		Collections.sort(tlist, new Comparator<Tweet>() {
			@Override
			public int compare(Tweet t1, Tweet t2) {
				   if (t1.getLanguage().contains("it") && !t2.getLanguage().contains("it"))
			            return -1;
			        if (!t1.getLanguage().contains("it") && t2.getLanguage().contains("it"))
			            return 1;
			        return 0;
			
		}
			});
		
		
//		return outlist;
		
		
	}
	
	
	public static void rankSubjects(List<Subject> slist){

		Collections.sort(slist, new Comparator<Subject>() {
			@Override
			public int compare(Subject s1, Subject s2) {
				   if (s1.getQuantita_tweet() > s2.getQuantita_tweet())
			            return -1;
			        if (s1.getQuantita_tweet() < s2.getQuantita_tweet())
			            return 1;
			        return 0;
			
		}
		});

		}
	
	public static void rankByScore(List<Tweet> tlist){
		
	//	ArrayList<Tweet> outlist = new ArrayList<Tweet>();
		Collections.sort(tlist, new Comparator<Tweet>() {
			@Override
			public int compare(Tweet t1, Tweet t2) {
				   if (t1.getScore()>t2.getScore())
			            return 1;
			        if (t1.getScore()<t2.getScore())
			            return -1;
			        return 0;
			
		}
			});
		
		Collections.reverse(tlist);

	//	return outlist;
		
	}

	

	/**
	 * Returns a matrix that specifies the standings of the most relevant words
	 * in several time intervals
	 * 
	 * @param DBConn
	 *            the db connector
	 * @param requestID
	 *            value of the request_id field
	 * @param DBTable
	 *            the DB table to query
	 * @param kb
	 *            the reference knowledge based (needed to get IDF)
	 * @param startDate
	 *            Start date in the format 'YYYY-DD-MM hh:mm:ss' (warning,
	 *            locale settings affect date format!)
	 * @param endDate
	 *            End date in the format 'YYYY-DD-MM hh:mm:ss' (warning, locale
	 *            settings affect date format!)
	 * @param Topmost
	 * @return a matrix that specifies the standings of the most relevant words
	 *         in several time intervals
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static int[][] intervalTrendingWordsRankings(String DBConn, String requestID, String DBTable, KnowledgeBase kb, List<Tweet> tlist, List<Subject> subjects, Date start,
			Date end) throws ClassNotFoundException, SQLException {

		if (tlist.isEmpty()) {
			return null;
		}
		int diff = secondsDiff(start, end);
		
		

		int intervals = 5;
		if (diff % 5 != 0) {
			return null;
		}
		
		
		@SuppressWarnings("unused")
		DBConnector dbc = new DBConnector(DBConn);

		int[][] words_ranks = new int[subjects.size()][intervals];
		int k = 1;

		// end = addSecs(end, (- intervals - k) * (diff / intervals));
		end = addSecs(start, diff / intervals);
		String endDate = dateToString(end);
		String startDate = dateToString(start);

		int[] appoggio = new int[5];
		int j = 0;
		while (j < intervals) {

			/*
			 * 
			 * // System.out.println("Inizio: "+start+"\t Fine: "+end);
			 * Iterator<Subject> sub = subjects.iterator(); List<Tweet> tlist1 =
			 * dbc.extractIntervalTweets(DBTable, requestID, startDate,
			 * endDate); int i = 0; while (i < subjects.size()) { Subject sub11
			 * = sub.next(); List<Tweet> tweetsoggetto =
			 * selectTweetsForSubject(tlist1, sub11); // words_ranks[i][j] =
			 * sub1.quantità_tweet; words_ranks[i][j] = tweetsoggetto.size();
			 * 
			 * ++i;
			 * 
			 */

	//		List<Tweet> tlist1 = dbc.extractIntervalTweets(DBTable, requestID, startDate, endDate);

			int i = 0; 	int conteggio=0;
		

			while (i < subjects.size()) {
			
				for(int b=0;b<subjects.get(i).getSubjectTweetList().size();b++){
				
					if(cercaTweetPerData(subjects.get(i).getSubjectTweetList().get(b), start, end))
						conteggio++;

						
				}
				subjects.get(i).setConteggioTweetPeriodo(conteggio);

		//		List<Tweet> tweetsoggetto = selectTweetsForSubject(tlist1, subjects.get(i));
				// words_ranks[i][j] = sub1.quantità_tweet;
				// words_ranks[i][j] = tweetsoggetto.size();
		//		appoggio[i] = tweetsoggetto.size();

				appoggio[i] = conteggio;
				
				++i; conteggio = 0;

			}

			for (int l = 0; l < subjects.size(); l++) {
				int mag = 0;
				for (int u = 0; u < subjects.size(); u++) {
					if (appoggio[l] > appoggio[u] && u != l)
						mag++;
					else if (appoggio[l] == appoggio[u] && u != l) {
						appoggio[l] = appoggio[l] + 1;
						mag++;

					}

				}
				words_ranks[l][j] = subjects.size() - mag;

			}
			end = convertDate(endDate);
			end = addSecs(end, diff / intervals);
			endDate = dateToString(end);
			start = convertDate(startDate);
			start = addSecs(start, diff / intervals);
			if (k == 1) {
				start = addSecs(start, 1);
			}
			startDate = dateToString(start);
			++k;
			++j;
			
		}
		
		for(int i = 0; i<words_ranks.length;i++){
			subjects.get(i).setPosFinale(words_ranks[i][4]);
			
		}
		
	


		return words_ranks;
	}
	
	
	@SuppressWarnings("unused")	//so far
	private static void assegnaFontWordCloud(SortedSet<WeightedWord> wordlist) {
		

		Iterator<WeightedWord> iter = wordlist.iterator();

		for(int i = 0; i<wordlist.size(); i++){
			
			WeightedWord w1 = iter.next();
			
			if(i==0) w1.setFontWordCloud(0);
			
			if(i==1) w1.setFontWordCloud(1);
			
			if(i==2) w1.setFontWordCloud(2);
			
			if(i>=3 && i<=5) w1.setFontWordCloud(3);
			
			if(i>=6 && i<=11) w1.setFontWordCloud(4);
			
			if(i>=12 && i<=19) w1.setFontWordCloud(5);
			
			if(i>=20 && i<=26) w1.setFontWordCloud(6);
			
			if(i>=27 && i<=29) w1.setFontWordCloud(7);
			
		}
	}
	
	private static void scambiaRigheMatrice(int[][]mat, int i, int j){
		
		int[] temp = new int[mat[i].length];
		temp=mat[i];
		mat[i]=mat[j];
		mat[j]=temp;
		
	}

	@SuppressWarnings("unused")
	private static List<Tweet> selectTweetsByTime(List<Tweet> tlist, Date start, Date end) {

		ArrayList<Tweet> outlist = new ArrayList<Tweet>();
		Iterator<Tweet> it = tlist.iterator();

		while (it.hasNext()) {
			Tweet t = it.next();
			// Date tdate = convertDate(t.getCreatedAt());

			if (secondsDiff(t.getCreatedAt(), start) >= 0 && secondsDiff(end, t.getCreatedAt()) <= 0)
				outlist.add(t);

		}

		return outlist;
	}

	/**
	 * Convert a String into a Date in the "yyyy-MM-dd HH:mm:ss" format
	 * 
	 * @param data
	 *            the String to be converted into Date
	 * @return a variable Date
	 */
	public static Date convertDate(String data) {

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(data);
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return date;
	}

	/**
	 * Returns a date with a number of minutes added to the previous date
	 * 
	 * @param oldDate
	 *            the starting date
	 * @param minutes
	 *            number of minutes to be added
	 * @return a date with a number of minutes added
	 */
	public static Date addMins(Date oldDate, int minutes) {

		long ONE_MINUTE_IN_MILLIS = 60000;// millisecs
		long t = oldDate.getTime();
		Date newDate = new Date(t + (minutes * ONE_MINUTE_IN_MILLIS));
		return newDate;

	}

	/**
	 * Returns a date with a number of seconds added to the previous date
	 * 
	 * @param oldDate
	 *            the starting date
	 * @param seconds
	 *            number of seconds to be added
	 * @return a date with a number of minutes seconds
	 */
	public static Date addSecs(Date oldDate, int seconds) {

		long ONE_SECOND_IN_MILLIS = 1000;// millisecs
		long t = oldDate.getTime();
		Date newDate = new Date(t + (seconds * ONE_SECOND_IN_MILLIS));
		return newDate;

	}

	/**
	 * Returns the time difference (in minutes) between two dates
	 * 
	 * @param earlierDate
	 *            the starting time
	 * @param laterDate
	 *            the final time
	 * @return the time difference (in minutes) between two dates
	 */

	public static int minutesDiff(Date earlierDate, Date laterDate) {
		if (earlierDate == null || laterDate == null)
			return 0;

		return (int) ((laterDate.getTime() / 60000) - (earlierDate.getTime() / 60000));
	}

	public static int secondsDiff(Date earlierDate, Date laterDate) {
		if (earlierDate == null || laterDate == null)
			return 0;

		return (int) ((laterDate.getTime() / 1000) - (earlierDate.getTime() / 1000));
	}

	/**
	 * Returns a tweetlist filtered by word relevancy (customizable threshold)
	 * 
	 * @param inputlist
	 *            tweetlist to be checked
	 * @param kb
	 *            the reference knowledge based (needed to get IDF)
	 * @param threshold
	 *            relevancy threshold
	 * @param relevantWords
	 *            a String array of relevant words
	 * @return a list of tweets with words that have a weight higher than the
	 *         relevancy threshold
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */

	public static List<Tweet> extractRelevantTweetlist(List<Tweet> inputlist, KnowledgeBase kb, double threshold, String[] relevantWords) throws FileNotFoundException, IOException {

		List<Tweet> outputlist;
		computeRelevantWords(inputlist, kb, threshold);
		outputlist = containingRelevantWords(inputlist, relevantWords);
		return outputlist;

	}

	/**
	 * Returns a tweetlist filtered by word relevancy (default threshold)
	 * 
	 * @param inputlist
	 *            tweetlist to be checked
	 * @param kb
	 *            the reference knowledge based (needed to get IDF)
	 * @param relevantWords
	 *            a String array of relevant words
	 * @return a list of tweets with words that have a weight higher than the
	 *         relevancy threshold
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */

	public static List<Tweet> extractRelevantTweetlist(List<Tweet> inputlist, KnowledgeBase kb, String[] relevantWords) throws FileNotFoundException, IOException {

		List<Tweet> outputlist;
		computeRelevantWords(inputlist, kb, WORD_RELEVANCY_THRESHOLD); // default
																		// threshold
		outputlist = containingRelevantWords(inputlist, relevantWords);
		return outputlist;

	}

	/**
	 * Returns all the relevant words in a tweet, uses the specified relevancy
	 * threshold
	 * 
	 * @param t
	 *            a tweet
	 * @param kb
	 *            the reference knowledge based (needed to get IDF)
	 * @param threshold
	 *            relevancy threshold
	 * @return a list of the word that have a weight higher than the relevancy
	 *         threshold
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static SortedSet<WeightedWord> relevantWords(Tweet t, KnowledgeBase kb, double threshold) throws FileNotFoundException, IOException {

		SortedSet<WeightedWord> outputlist = new TreeSet<WeightedWord>();

		// extract words from the tweet
		List<WeightedWord> words = t.getTweetWords();

		// get IDF
		Iterator<WeightedWord> it = words.iterator();
		while (it.hasNext()) {

			WeightedWord word = it.next();
			word.setIDFFromCorpus(kb);
			word.setWeight();

			if (word.getWeight() >= threshold)
				outputlist.add(word);
		}

		// return all the words for which tf*IDF>threshold

		return outputlist;
	}

	/**
	 * Returns all the relevant words in a tweet. Uses the default relevancy
	 * threshold defined in WORD_RELEVANCY_THRESHOLD
	 * 
	 * @param t
	 *            a tweet
	 * @param kb
	 *            the reference knowledge based (needed to get IDF)
	 * @return a list of the words that have a weight higher than the default
	 *         relevancy threshold
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static SortedSet<WeightedWord> relevantWords(Tweet t, KnowledgeBase kb) throws FileNotFoundException, IOException {

		return relevantWords(t, kb, WORD_RELEVANCY_THRESHOLD);
	}

	/**
	 * Extracts all the words from a set of tweets and computes their weight
	 * (tf*IDF)
	 * 
	 * @param tset
	 *            A set of tweets
	 * @param kb
	 *            The reference knowledge base (to compute IDF)
	 * @return all the Words from the set weighted and ordered by decreasing
	 *         weight
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static SortedSet<WeightedWord> weightedWordsFromATweetSet(List<Tweet> tset, KnowledgeBase kb) throws FileNotFoundException, IOException {

		HashMap<String, WeightedWord> outputset = new HashMap<String, WeightedWord>();

		// compare weighted words by weight. If equal compare alphabetically
		SortedSet<WeightedWord> outputlist = new TreeSet<WeightedWord>(new Comparator<WeightedWord>() {

			public int compare(WeightedWord w1, WeightedWord w2) {

				int c = Double.compare(w2.getWeight(), w1.getWeight());
				if (c != 0)
					return c;

				else
					return w1.getType().compareTo(w2.getType());
			}

		});

		// extract words from tweet list
		Iterator<Tweet> it = tset.iterator();

		while (it.hasNext()) {

			SortedSet<WeightedWord> wordlist = relevantWords(it.next(), kb, 0.0);

			Iterator<WeightedWord> it2 = wordlist.iterator();

			while (it2.hasNext()) {

				WeightedWord w = it2.next();
				String key = w.getType();

				if (outputset.containsKey(key)) {

					double weight = w.getWeight() + outputset.get(key).getWeight();
					w.setWeight(weight);
					w.setTf(w.getTf() + outputset.get(key).getTf());
					outputset.put(key, w);
				} else {
					outputset.put(key, w);
				}
			}
		}

		outputlist.addAll(outputset.values());

		// return all the words

		return outputlist;
	}

	/**
	 * Returns all the relevant words in a set of tweets
	 * 
	 * @param tset
	 *            a set of Tweets
	 * @param kb
	 *            the reference knownledge base used to compute IDF
	 * @param threshold
	 *            the relevancy threshold
	 * @return all the words with weight greater than the relevancy threshold
	 *         sorted by weight (descending order)
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static SortedSet<WeightedWord> relevantWordsFromATweetSet(List<Tweet> tset, KnowledgeBase kb, double threshold) throws FileNotFoundException, IOException {

		SortedSet<WeightedWord> wordlist = weightedWordsFromATweetSet(tset, kb);
		SortedSet<WeightedWord> outputlist = new TreeSet<WeightedWord>(wordlist);

		// remove words whose weight is less than the relevancy threshold
		Iterator<WeightedWord> it = wordlist.iterator();
		while (it.hasNext()) {
			WeightedWord word = it.next();
			if (word.getWeight() < threshold)
				outputlist.remove(word);
		}

		return outputlist;

	}

	/**
	 * Returns all the relevant words in a set of tweets. Uses the default value
	 * as relevancy threshold
	 * 
	 * @param tset
	 *            a set of Tweets
	 * @param kb
	 *            the reference knownledge base used to compute IDF
	 * @return all the words with weight greater than the relevancy threshold
	 *         sorted by weight (descending order)
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static SortedSet<WeightedWord> relevantWordsFromATweetSet(List<Tweet> tset, KnowledgeBase kb) throws FileNotFoundException, IOException {

		return relevantWordsFromATweetSet(tset, kb, WORDSET_RELEVANCY_THRESHOLD);

	}

	/**
	 * Computes relevant words for every tweet in the list. The field of the
	 * object tweet is valorized
	 * 
	 * @param tweetlist
	 *            a list of tweet
	 * @param kb
	 *            the reference knowledge base
	 * @param threshold
	 *            the relevancy threshold
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void computeRelevantWords(List<Tweet> tweetlist, KnowledgeBase kb, Double threshold) throws FileNotFoundException, IOException {

		Iterator<Tweet> it = tweetlist.iterator();
		while (it.hasNext()) {
			Tweet t = it.next();
			t.setRelevantWords(kb, threshold);

		}
	}

	/**
	 * Computes relevant words for every tweet in the list. The field of the
	 * object tweet is valorized. The default relevancy threshold is used.
	 * 
	 * @param tweetlist
	 *            a list of tweet
	 * @param kb
	 *            the reference knowledge base
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void computeRelevantWords(List<Tweet> tweetlist, KnowledgeBase kb) throws FileNotFoundException, IOException {

		computeRelevantWords(tweetlist, kb, WORD_RELEVANCY_THRESHOLD);
	}

	/**
	 * Most relevant words in a tweetset
	 * 
	 * @param tset
	 *            a set of tweets
	 * @param kb
	 *            he reference knowledge base (to obtain IDF)
	 * @param k
	 *            number of most relevant words to extract from the corpus
	 *            (higher tf*IDF)
	 * @return the k most relevant words in the list/corpus ordererd by
	 *         relevancy
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static SortedSet<WeightedWord> topmostWordsFromATweetSet(List<Tweet> tset, KnowledgeBase kb, int k) throws FileNotFoundException, IOException {

		SortedSet<WeightedWord> sortedlist = weightedWordsFromATweetSet(tset, kb);
		SortedSet<WeightedWord> outputlist = new TreeSet<WeightedWord>();
		int i = 0;

		// return first k most relevant words
		Iterator<WeightedWord> it = sortedlist.iterator();
		while (it.hasNext() && i < k) {
			i++;

			WeightedWord word = it.next();
			outputlist.add(word);
		}

		return outputlist;
	}

	/**
	 * Most relevant words in a tweetset, keywords excluded
	 * 
	 * @param tset
	 *            a set of tweets
	 * @param keywords
	 *            list of keywords to exclude
	 * @param kb
	 *            he reference knowledge base (to obtain IDF)
	 * @param k
	 *            number of most relevant words to extract from the corpus
	 *            (higher tf*IDF)
	 * @return the k most relevant words in the list/corpus ordererd by
	 *         relevancy
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static SortedSet<WeightedWord> topmostWordsFromATweetSetNoKeywords(List<Tweet> tset, List<String> keywords, KnowledgeBase kb, int k) throws FileNotFoundException, IOException {

		SortedSet<WeightedWord> sortedlist = weightedWordsFromATweetSet(tset, kb);
		SortedSet<WeightedWord> outputlist = new TreeSet<WeightedWord>();
		int i = 0;

		Iterator<WeightedWord> it = sortedlist.iterator();
		while (it.hasNext() && i < k) {

			WeightedWord word = it.next();
			boolean found = false;

			Iterator<String> kw_it = keywords.iterator();

			while (kw_it.hasNext()) {
				String keyword = kw_it.next();

				if (word.getType().equalsIgnoreCase(keyword) || word.getType().equalsIgnoreCase("#" + keyword)) {
					found = true;

				}
				if (found)
					break;
			}

			if (!found) {
				outputlist.add(word);
				i++;
			}
		}

		return outputlist;

	}

	/**
	 * Most relevant words in a list
	 * 
	 * @param words
	 *            string containing a list of words separated by spaces
	 * @param kb
	 *            the reference knowledge base (to obtain IDF)
	 * @param k
	 *            number of most relevant words to extract from the corpus
	 *            (higher tf*IDF)
	 * @return the k most relevant words in the tweetset sorted by relevancy
	 */
	public static SortedSet<WeightedWord> topmost(String words, KnowledgeBase kb, int k) {

		SortedSet<WeightedWord> outputlist = new TreeSet<WeightedWord>();
		String[] wordlist = words.toLowerCase().split("\\s+");

		// count single word occurencies (tf)
		HashMap<String, Integer> wordmap = new HashMap<String, Integer>();

		for (int i = 0; i < wordlist.length; i++) {

			String key = wordlist[i];

			if (wordmap.containsKey(key)) {
				wordmap.put(key, wordmap.get(key) + 1);
			} else {
				wordmap.put(key, 1);
			}
		}

		// save results

		Iterator<String> it = wordmap.keySet().iterator();
		while (it.hasNext()) {

			String word = it.next();
			Integer occurrencies = wordmap.get(word);
			double tf = (double) (occurrencies);

			WeightedWord w = new WeightedWord(word, tf);

			outputlist.add(w);
		}

		// get IDF from reference corpus and compute weight (tf*IDF)
		Iterator<WeightedWord> it2 = outputlist.iterator();
		while (it2.hasNext()) {

			WeightedWord word = it2.next();
			word.setIDFFromCorpus(kb);
			word.setWeight();
		}

		// return k most relevant words (sorted by weight=tf*IDF)

		Iterator<WeightedWord> it3 = outputlist.iterator();
		int i = 0;

		while (it3.hasNext()) {
			WeightedWord word = it3.next();
			i++;
			if (i > k)
				outputlist.remove(word);
		}

		return outputlist;

	}
	//

	/**
	 * Returns all the tweet from a list that contain the specified relevant
	 * word (the field relevantWords of the tweets in the list has to be set!)
	 * 
	 * @param tweetlist
	 *            the list of tweet
	 * @param relevantWord
	 *            the string representing the relevant word to search for
	 * @return the tweets from the input list containing the specified relevant
	 *         word
	 */
	public static List<Tweet> containingRelevantWord(List<Tweet> tweetlist, String relevantWord) {
		ArrayList<Tweet> outputlist = new ArrayList<Tweet>();

		relevantWord = relevantWord.toLowerCase();

		Iterator<Tweet> it = tweetlist.iterator();
		while (it.hasNext()) {
			Tweet t = it.next();
			List<WeightedWord> rwords = t.getRelevantWords();

			Iterator<WeightedWord> it2 = rwords.iterator();
			while (it2.hasNext()) {

				if (it2.next().getType().equals(relevantWord)) {
					outputlist.add(t);
					break;

				}
			}

		}

		return outputlist;
	}

	/**
	 * Returns all the tweet from a list that contain at least one of the
	 * specified relevant words (the field relevantWords of the tweets in the
	 * list has to be set!)
	 * 
	 * @param tweetlist
	 *            the list of tweets
	 * @param relevantWords
	 *            A string array representing the relevant words to search for
	 * @return the list of tweet containing at least one of the specified
	 *         relevant words
	 */
	public static List<Tweet> containingRelevantWords(List<Tweet> tweetlist, String[] relevantWords) {
		ArrayList<Tweet> outputlist = new ArrayList<Tweet>();

		Iterator<Tweet> it = tweetlist.iterator();
		while (it.hasNext()) {
			Tweet t = it.next();
			List<WeightedWord> rwords = t.getRelevantWords();

			Iterator<WeightedWord> it2 = rwords.iterator();

			while (it2.hasNext()) {

				boolean found = false;
				String word = it2.next().getType();

				for (int i = 0; i < relevantWords.length; i++) {

					if (word.equals(relevantWords[i].toLowerCase())) {
						outputlist.add(t);
						found = true;
						break;
					}
					if (found)
						break;
				}
			}

		}

		return outputlist;
	}

	/**
	 * Return the count of occurrences of a set of words in a set of tweets
	 * 
	 * @param tweetlist
	 *            the set of tweets
	 * @param wordlist
	 *            the set of words to be counted in the tweetset (as a list of
	 *            strings)
	 * @return an HashMap having a string (word) as key, and the word count in
	 *         the tweetset as value
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static HashMap<String, Integer> countWords(List<Tweet> tweetlist, List<String> wordlist) throws FileNotFoundException, IOException {

		HashMap<String, Integer> result = new HashMap<String, Integer>();

		Iterator<String> it = wordlist.iterator();

		// Initialize hashmap
		while (it.hasNext()) {
			String w = it.next();
			result.put(w, 0);
		}

		// Scan tweets
		Iterator<Tweet> itt = tweetlist.iterator();

		while (itt.hasNext()) {
			Tweet t = itt.next();

			// extract words from tweet
			List<WeightedWord> tweetwords = t.getTweetWords();
			Iterator<WeightedWord> it2 = tweetwords.iterator();

			while (it2.hasNext()) {
				WeightedWord w = it2.next();
				String key = w.getType();
				if (result.containsKey(key)) {

					int count = result.get(key); // word count in the tweetset
					int freq = (int) w.getTf(); // word frequency in the tweet
					result.put(key, count + freq);
				}
			}
		}

		return result;
	}

	/**
	 * Return the count of occurrences of a set of words in a set of tweets
	 * 
	 * @param tweetlist
	 *            the set of tweets
	 * @param wordlist
	 *            the set of words to be counted in the tweetset (as a list of
	 *            strings)
	 * @return an Set of WordCount, sorted by number of occurrences
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static SortedSet<WordCount> countWordsSorted(List<Tweet> tweetlist, List<String> wordlist) throws FileNotFoundException, IOException {

		SortedSet<WordCount> output = new TreeSet<WordCount>();

		HashMap<String, Integer> wordmap = new HashMap<String, Integer>();
		wordmap = countWords(tweetlist, wordlist);

		Set<String> keys = wordmap.keySet();
		Iterator<String> it = keys.iterator();

		while (it.hasNext()) {
			String s = it.next();
			WordCount wc = new WordCount(s, wordmap.get(s));
			output.add(wc);
		}

		return output;
	}

	/**
	 * Filters a list of tweets by subjects and relevant words (Warning:
	 * relevant words must be computed before invoking this method!)
	 * 
	 * @param inputlist
	 *            the list of tweets to filter
	 * @param subjects
	 *            subjects to search for (set null for no filtering on subjects)
	 * @param relevantWords
	 *            relevant words to search for (set null for no filtering on
	 *            relevant words)
	 * @return the filtered list of tweets
	 */
	public static List<Tweet> filterTweetsFromList(List<Tweet> inputlist, List<Subject> subjects, String[] relevantWords) {
		List<Tweet> outputlist = new ArrayList<Tweet>();
		boolean sfilter = false;

		// filters on subjects
		if (subjects != null && !subjects.isEmpty()) {
			outputlist = TweetAnalyzer.selectTweetsForSubjects(inputlist, subjects);
			sfilter = true;
		}

		// filters on relevant words
		if (relevantWords != null && relevantWords.length > 0) {
			if (sfilter)
				outputlist = TweetAnalyzer.containingRelevantWords(outputlist, relevantWords);
			else
				outputlist = TweetAnalyzer.containingRelevantWords(inputlist, relevantWords);
		}
		return outputlist;
	}

	/**
	 * Executes clustering on a set of words
	 * 
	 * @param wordset
	 *            the set of words to clusterize
	 * @param kb
	 *            the reference knowledge based from which derive word vectorial
	 *            representation
	 * @param nclust
	 *            the number of cluster (K-means)
	 * @return the list of clusters
	 */
	public static List<Cluster> wordClusterer(SortedSet<WeightedWord> wordset, KnowledgeBase kb, Integer nclust) {

		List<Cluster> clusters = new ArrayList<Cluster>();
		List<WeightedWord> wordlist = new ArrayList<WeightedWord>(wordset);

		Iterator<WeightedWord> it = wordlist.iterator();
		int size = kb.getVectorSize();

		FastVector fvWekaAttributes = new FastVector(size + 1); // +1 for type
																// field
		for (int i = 0; i < size; i++) {
			Attribute att = new Attribute(Integer.toString(i));
			fvWekaAttributes.addElement(att);
		}
		Attribute type = new Attribute("wordtype", (FastVector) null); // add
																		// string
																		// attribute
		fvWekaAttributes.addElement(type);

		Instances dataSet = new Instances("WordVectors", fvWekaAttributes, wordlist.size());

		while (it.hasNext()) {

			WeightedWord word = it.next();
			String key = word.getType();
			// System.out.println("KEY: "+key); //DEBUG

			ArrayList<Double> vector = new ArrayList<Double>();

			if (kb.getVectors().containsKey(key)) {
				vector = kb.getVectors().get(key);
			} else {
				vector = kb.getVectors().get("</s>"); // DEFAULT VALUE (WORD NOT
														// PRESENT IN
														// VOCABULARY)
			}
			// create weka instance
			Instance inst = new Instance(size + 1); // +1 se aggiungo type
			inst.setDataset(dataSet);

			// add features attributes
			for (int i = 0; i < size; i++) {
				inst.setValue(i, vector.get(i));
			}

			// add word type attribute
			inst.setValue(type, key);

			// add instance to dataset
			dataSet.add(inst);

			// System.out.println("The instance: " + inst); //DEBUG

		}

		// build k-means clusterer
		String[] options = new String[3];
		options[0] = "-O"; // preserve instance order
		options[1] = "-N"; // no. of clusters
		options[2] = nclust.toString();
		SimpleKMeans clusterer = new SimpleKMeans(); // new instance of
														// clusterer

		FilteredClusterer fclusterer = new FilteredClusterer();

		Remove rm = new Remove();
		rm.setAttributeIndices("last"); // remove last attribute (text filed)
										// for clustering
		fclusterer.setFilter(rm);

		try {
			clusterer.setOptions(options);
			fclusterer.setClusterer(clusterer);

			fclusterer.buildClusterer(dataSet); // build the clusterer

			// get assignments
			int[] assignments = clusterer.getAssignments();
			int numclusters = clusterer.getNumClusters();
			Instances centroids = clusterer.getClusterCentroids();

			// save into cluster objects
			for (int i = 0; i < numclusters; i++) {
				Cluster c = new Cluster();
				clusters.add(c);
				// add centroid to cluster object
				c.setCentroid(centroids.instance(i));

			}

			// add words to cluster objects
			for (int i = 0; i < assignments.length; i++) {

				Cluster assignedCluster = clusters.get(assignments[i]);

				// recover weighted word from the list (works only if order is
				// preserved!!!!)
				// assignedCluster.addWord(wordlist.get(i));
				assignedCluster.addWord(wordlist.get(i));

				// //DEBUG Verifica che l'ordine delle parole è lo stesso!
				// if(dataSet.instance(i).stringValue(size).equals(wordlist.get(i).getType()))
				// System.out.println("OK");
				// else
				// System.out.println("WRONG WORD ORDER!");

				// System.out.println(dataSet.instance(i).stringValue(size)+" ->
				// Cluster "+assignments[i]);
			}

			// computes the instance (word) nearest to the centroid for each
			// cluster//////////
			for (int i = 0; i < numclusters; i++) {

				double dist = Double.POSITIVE_INFINITY;
				WeightedWord representative = null;

				for (int j = 0; j < assignments.length; j++) {

					if (assignments[j] == i) {

						EuclideanDistance ed = new EuclideanDistance();
						ed.setInstances(dataSet);
						double newdist = ed.distance(centroids.instance(i), dataSet.instance(j));

						// System.out.println("Distance: " + newdist);

						if (newdist < dist) {
							representative = wordlist.get(j);
							dist = newdist;

						}

					}

				}
				clusters.get(i).setRepresentativeWord(representative);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return clusters;
	}

	/**
	 * Executes clustering on a set of tweets. Considers only relevant words
	 * using default relevancy threshold
	 * 
	 * @param tweetlist
	 *            the list of tweet containing words to be clustered
	 * @param kb
	 *            the reference knowledge based from which derive word vectorial
	 *            representation
	 * @param nclust
	 *            the number of cluster (K-means)
	 * @return the list of clusters
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static List<Cluster> tweetClusterer(List<Tweet> tweetlist, KnowledgeBase kb, Integer nclust) throws FileNotFoundException, IOException {

		SortedSet<WeightedWord> wordsToCluster = new TreeSet<WeightedWord>();
		wordsToCluster.addAll(relevantWordsFromATweetSet(tweetlist, kb));

		return wordClusterer(wordsToCluster, kb, nclust);

	}

	/**
	 * Executes clustering on a set of tweets. Considers only relevant words
	 * using the input relevancy threshold
	 * 
	 * @param tweetlist
	 *            the list of tweet containing words to be clustered
	 * @param kb
	 *            the reference knowledge based from which derive word vectorial
	 *            representation
	 * @param nclust
	 *            the number of cluster (K-means)
	 * @param threshold
	 *            relevancy threshold
	 * @return the list of clusters
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static List<Cluster> tweetClusterer(List<Tweet> tweetlist, KnowledgeBase kb, Integer nclust, double threshold) throws FileNotFoundException, IOException {

		SortedSet<WeightedWord> wordsToCluster = new TreeSet<WeightedWord>();
		wordsToCluster.addAll(relevantWordsFromATweetSet(tweetlist, kb, threshold));

		return wordClusterer(wordsToCluster, kb, nclust);

	}

	/**
	 * Update clusters adding the words in input to current clusters
	 * 
	 * @param oldclusters
	 *            existing clusters
	 * @param wordset
	 *            new words to cluster
	 * @param kb
	 *            the reference knowledge based from which derive word vectorial
	 *            representation
	 * @return list of updated clusters
	 */
	public static List<Cluster> wordClustererUpdate(List<Cluster> oldclusters, SortedSet<WeightedWord> wordset, KnowledgeBase kb) {

		Integer nclust = oldclusters.size();

		Iterator<Cluster> itc = oldclusters.iterator();
		while (itc.hasNext()) {
			Cluster c = itc.next();
			wordset.addAll(c.getWords());
		}

		return wordClusterer(wordset, kb, nclust);
	}

	/**
	 * Update Clusters adding relevant words from a list of tweets to current
	 * clusters
	 * 
	 * @param oldclusters
	 *            existing clusters
	 * @param tweetlist
	 *            list of tweet to cluster
	 * @param kb
	 *            the reference knowledge based from which derive word vectorial
	 *            representation
	 * @return list of updated clusters
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static List<Cluster> tweetClustererUpdate(List<Cluster> oldclusters, List<Tweet> tweetlist, KnowledgeBase kb) throws FileNotFoundException, IOException {

		SortedSet<WeightedWord> wordsToCluster = new TreeSet<WeightedWord>();
		wordsToCluster.addAll(relevantWordsFromATweetSet(tweetlist, kb));

		return wordClustererUpdate(oldclusters, wordsToCluster, kb);

	}

	/**
	 * Selects all the tweets from a list that contain the specified subject
	 * 
	 * @param tlist
	 *            a list of tweets
	 * @param s
	 *            the subject (in all its surface forms)
	 * @return the tweet that contains at least one of the forms of the subject
	 *         (the comparison is case insensitive)
	 */
	public static List<Tweet> selectTweetsForSubject(List<Tweet> tlist, Subject s) {

		ArrayList<Tweet> outlist = new ArrayList<Tweet>();
		String [] subjectStopwords;
		if(s.getStopwords()!=null && s.getStopwords().length>0)
			subjectStopwords = s.getStopwords();
		else
			subjectStopwords = null;

		Iterator<Tweet> it = tlist.iterator();
		while (it.hasNext()) {
			
			Tweet t = it.next();
			if (t.getSubject().contains(s.getCanonicalForm())) {
				continue;
			}
			else {
				
				String text = t.getProcessedtext();
				boolean esito = false;
				if(subjectStopwords!=null && subjectStopwords.length>0){
					if(subjectStopwords[0]!=""){
						for(int i = 0;i<subjectStopwords.length;i++){
							if(text.contains(subjectStopwords[i])){
								esito=true;
								break;
							}
								
						}
					}
					if(esito)
						continue;
				}

				Iterator<String> s_it = s.getSurfaceForms().iterator();
				while (s_it.hasNext() && !t.getSubject().contains(s.getCanonicalForm())) {

					if (text.contains(s_it.next().toLowerCase()))	 // case
																	// insensitive!
					{
						outlist.add(t);
						s.getTweetIDList().add(t.getTweetID());
						if (t.getSubject() == "")
							t.setSubject(s.getCanonicalForm());
						else
							t.setSubject(t.getSubject() + "," + s.getCanonicalForm());

					}

				}

			}

		}
		s.setQuantita_tweet(outlist.size());
		return outlist;

	}
	
	/**
	 * Checks if the tweet is included between startDate and endDate
	 * 
	 * @param Tweet the tweet to be checked
	 * @param Date inizio the startDate
	 * @param Date fine the endDate
	 * @return true/false if the tweet is/isn't included between startDate and endDate
	 */
	
	public static boolean cercaTweetPerData(Tweet t, Date inizio, Date fine){
		
		if(t.getCreatedAt().after(inizio) && t.getCreatedAt().before(fine))
			return true;
		return false;
	}

	public static int countTweetsForSubject(List<Tweet> tlist, tweet.analyzer.model.data.Subject s) {

		int contatweet = 0;
		Iterator<Tweet> it = tlist.iterator();
		boolean controllo = true;
		while (it.hasNext()) {
			Tweet t = it.next();
			controllo = true;
			if (t.getSubject().contains(s.getName())) {
				controllo = false;
			}
			if (controllo) {

				String text = t.getProcessedtext();

				Iterator<Subject_Word> s_it = s.getSubjectWords().iterator();
				while (s_it.hasNext()) {

					if (text.contains(s_it.next().getWord().toLowerCase())) // case
																			// insensitive!
					{
						if (t.getSubject() == "")
							t.setSubject(s.getName());
						else
							t.setSubject(t.getSubject() + "," + s.getName());
						contatweet++;

					}

				}

			}

		}

		return contatweet;

	}

	/**
	 * Selects all the tweets from a list that contains at least one of the
	 * specified subjects
	 * 
	 * @param tlist the tweet list
	 * @param slist the subject list
	 * @return a list of tweets containing the specified subjects
	 */

	public static List<Tweet> selectTweetsForSubjects(List<Tweet> tlist, List<Subject> slist) {

		ArrayList<Tweet> outlist = new ArrayList<Tweet>();
		Iterator<Subject> it = slist.iterator();
		while (it.hasNext()) {

			outlist.addAll(selectTweetsForSubject(tlist, it.next()));
		}

		return outlist;

	}

	/**
	 * Get the sentiment of a list of tweets
	 * 
	 * @param tweetlist
	 *            a list of tweets
	 * @return array of Sentiment (SentimentName, Value)
	 */
	public static Sentiment[] getSentiment(List<Tweet> tweetlist) {

		//////////////////////////////////////////
		/////////////////////////////////////////
		//////// VECCHIO METODO///////////////////
		////////////////////////////////////////

		/*
		 * 
		 * Iterator<Tweet> it = tweetlist.iterator(); Integer totTweets =
		 * tweetlist.size(); Integer posCount = 0; Integer insCount = 0; Integer
		 * linkCount = 0; List<String> listaIdTweetPositivi = new ArrayList<>();
		 * List<String> listaIdTweetNegativi = new ArrayList<>(); List<String>
		 * listaIdTweetConLink = new ArrayList<>();
		 * 
		 * while (it.hasNext()) { Tweet t = it.next();
		 * 
		 * if (t.hasLinks()) { linkCount += t.getLinks().size();
		 * listaIdTweetConLink.add(t.getTweetID()); } if (t.hasPositive()) {
		 * posCount += 1; listaIdTweetPositivi.add(t.getTweetID()); } if
		 * (t.hasInsulting()) { insCount += 1;
		 * listaIdTweetNegativi.add(t.getTweetID()); } }
		 * 
		 * Sentiment s1 = new Sentiment(SentimentName.POSITIVE,
		 * posCount.doubleValue() / totTweets.doubleValue(),
		 * listaIdTweetPositivi); Sentiment s2 = new
		 * Sentiment(SentimentName.INSULTING, insCount.doubleValue() /
		 * totTweets.doubleValue(), listaIdTweetNegativi); Sentiment s3 = new
		 * Sentiment(SentimentName.WITH_LINKS, linkCount.doubleValue() /
		 * totTweets.doubleValue(), listaIdTweetConLink);
		 * 
		 * Sentiment[] s = { s1, s2, s3 };
		 * 
		 * return s;
		 * 
		 * 
		 */

		////////////////////////////////////////////////
		////////////////////////////////////////////////
		/////////////// NUOVO METODO/////////////////////
		/////////////////////////////////////////////////

		Iterator<Tweet> it = tweetlist.iterator();
		Integer totTweets = tweetlist.size();
		Integer likeCount = 0;
		Integer hilariousCount = 0;
		Integer sadCount = 0;
		Integer angryCount = 0;
		List<String> listaIdTweetLike = new ArrayList<>();
		List<String> listaIdTweetHilarious = new ArrayList<>();
		List<String> listaIdTweetSad = new ArrayList<>();
		List<String> listaIdTweetAngry = new ArrayList<>();

		while (it.hasNext()) {

			Tweet t = it.next();
			if (t.getLike() && !t.getAmbiguo()) {
				listaIdTweetLike.add(t.getTweetID());
				likeCount += 1;

			}

			if (t.getHilarious() && !t.getAmbiguo()) {
				listaIdTweetHilarious.add(t.getTweetID());
				hilariousCount += 1;
			}

			if (t.getAngry() && !t.getAmbiguo()) {
				listaIdTweetAngry.add(t.getTweetID());
				angryCount += 1;

			}
			if (t.getSad() && !t.getAmbiguo()) {
				listaIdTweetSad.add(t.getTweetID());
				sadCount += 1;

			}

		}

		/*
		 * while (it.hasNext()) { Tweet t = it.next();
		 * 
		 * if (t.hasLike()) { likeCount += 1;
		 * listaIdTweetLike.add(t.getTweetID()); } if (t.hasHilarious()) {
		 * hilariousCount += 1; listaIdTweetHilarious.add(t.getTweetID()); } if
		 * (t.hasSad()) { sadCount += 1; listaIdTweetSad.add(t.getTweetID()); }
		 * 
		 * if (t.hasAngry()) { angryCount += 1;
		 * listaIdTweetAngry.add(t.getTweetID()); }
		 * 
		 * 
		 * }
		 */

		double likepc, hilpc, sadpc, angrypc;
		likepc = likeCount.doubleValue() / totTweets.doubleValue();
		hilpc = hilariousCount.doubleValue() / totTweets.doubleValue();
		sadpc = sadCount.doubleValue() / totTweets.doubleValue();
		angrypc = angryCount.doubleValue() / totTweets.doubleValue();
		double somma = likepc + hilpc + sadpc + angrypc;
		double likerate, hilrate, sadrate, angryrate;
		likerate = likepc / somma;
		hilrate = hilpc / somma;
		sadrate = sadpc / somma;
		angryrate = angrypc / somma;

		Sentiment s1 = new Sentiment(SentimentName.LOVELIKEWOW, likerate, listaIdTweetLike);
		Sentiment s2 = new Sentiment(SentimentName.HILARIOUS, hilrate, listaIdTweetHilarious);
		Sentiment s3 = new Sentiment(SentimentName.SAD, sadrate, listaIdTweetSad);
		Sentiment s4 = new Sentiment(SentimentName.ANGRY, angryrate, listaIdTweetAngry);

		/*
		 * 
		 * Sentiment s1 = new Sentiment(SentimentName.LOVELIKEWOW,
		 * likeCount.doubleValue() / totTweets.doubleValue(), listaIdTweetLike);
		 * Sentiment s2 = new Sentiment(SentimentName.HILARIOUS,
		 * hilariousCount.doubleValue() / totTweets.doubleValue(),
		 * listaIdTweetHilarious); Sentiment s3 = new
		 * Sentiment(SentimentName.SAD, sadCount.doubleValue() /
		 * totTweets.doubleValue(), listaIdTweetSad); Sentiment s4 = new
		 * Sentiment(SentimentName.ANGRY, angryCount.doubleValue() /
		 * totTweets.doubleValue(), listaIdTweetAngry);
		 */

		// Sentiment s1 = new Sentiment(SentimentName.LOVELIKEWOW,
		// likeCount.doubleValue() / totTweets.doubleValue());
		// Sentiment s2 = new Sentiment(SentimentName.HILARIOUS,
		// hilariousCount.doubleValue() / totTweets.doubleValue());
		// Sentiment s3 = new Sentiment(SentimentName.SAD,
		// sadCount.doubleValue() / totTweets.doubleValue());
		// Sentiment s4 = new Sentiment(SentimentName.ANGRY,
		// angryCount.doubleValue() / totTweets.doubleValue());

		Sentiment[] s = { s1, s2, s3, s4 };

		return s;

	}
	
	
	
	public static Set<String> getStopwordsSet() {
		return stopwordsSet;
	}

	public static void setStopwordsSet(String file) throws FileNotFoundException, IOException {
		stopwordsSet = Utilities.wordSetFromTxtFile(file);
	}
	
	private static Set<String> stopwordsSet;
	

	/**
	 * Returns the sentiment of a list of tweets filtered by subject
	 * 
	 * @param tlist
	 *            the list of tweets
	 * @param s
	 *            the subject
	 * @return an array of Sentiment (SentimentName, Value)
	 */
	public static Sentiment[] getSentimentForSubject(List<Tweet> tlist, Subject s) {

		List<Tweet> newList = selectTweetsForSubject(tlist, s);

		return getSentiment(newList);

	}

	/**
	 * Compute and returns the sentiment of a list of tweets filtered by subject
	 * 
	 * @param tlist
	 *            the list of tweets
	 * @param s
	 *            the subject
	 * @return an array of Sentiment (SentimentName, Value)
	 * @throws IOException
	 */

	public static Sentiment[] setAndGetSentimentForSubject(List<Tweet> tlist, Subject s)
			throws IOException {

		List<Tweet> newList = selectTweetsForSubject(tlist, s);
		s.setSubjectTweetList(newList);
		rankByLanguage(s.getSubjectTweetList());
		// s.quantità_tweet=tlist.size();
		setSentiment(newList); 
		
		for (int i = 0; i < tlist.size(); i++) {
			Tweet t = tlist.get(i);
			t.setTextToEmoji();
			//BUGFIX: the "soccer" emoji turns the tweet to "Angry" sentiment
			if(t.getTextToEmoji().contains("soccer"))
				t.setAmbiguo(true);
		/*	if(t.getTextToEmoji().contains(":sob:") && t.getLike()){
				t.setAmbiguo(false);
				t.setSad(false);
				
			}*/
				
			
			if(t.getAmbiguo())
				continue;
			else{
				if (t.getLike())
					s.getLikeTweetList().add(t.getTweetID());
				if (t.getHilarious())
					s.getHilariousTweetList().add(t.getTweetID());
				if (t.getAngry())
					s.getAngryTweetList().add(t.getTweetID());
				if (t.getSad())
					s.getSadTweetList().add(t.getTweetID());
			}
			

		}

		return getSentiment(newList);

	}

	/**
	 * Set the sentiment attribute for all the tweets in a list
	 * 
	 * @param tweetlist
	 *            a list of tweet
	 * @param positiveWords
	 *            the list of positive words
	 * @param insultingWords
	 *            the list of insulting words
	 */
	public static void setSentiment(List<Tweet> tweetlist, Set<String> positiveWords, Set<String> insultingWords) {
		Iterator<Tweet> it = tweetlist.iterator();

		while (it.hasNext()) {
			it.next().setSentiment(positiveWords, insultingWords);
		}
	}

	/**
	 * Set the sentiment attribute for all the tweets in a list
	 * 
	 * @param tweetlist a list of tweet
	 * @param loveLike
	 * @param hilarious
	 * @param sad
	 * @param angry
	 * 
	 * 
	 */

	public static void setSentiment(List<Tweet> tweetlist) throws IOException {

		Iterator<Tweet> it = tweetlist.iterator();

		while (it.hasNext()) {
			Tweet t = it.next();
			t.setSentiment(t.getLanguage());
			}

	}
	
	/**
	 * Filters tweets by sentiment (Warning: sentiment must be computed before
	 * invoking this method!)
	 * 
	 * @param tweetlist
	 *            a list of tweet
	 * @param sentiment
	 *            a string, allowed values ={"POSITIVE", "INSULTING",
	 *            "WITHLINKS"}
	 * @return the tweets in the original list filtered by sentiment
	 */
	public static List<Tweet> filterTweetsBySentiment(List<Tweet> tweetlist, String sentiment) {

		if (sentiment.equals("POSITIVE") || sentiment.equals("INSULTING") || sentiment.equals("WITHLINKS")) {

			List<Tweet> outputlist = new ArrayList<Tweet>();
			Iterator<Tweet> it = tweetlist.iterator();

			while (it.hasNext()) {
				Tweet t = it.next();

				switch (sentiment) {
				case "POSITIVE":
					if (t.hasPositive())
						outputlist.add(t);
					break;
				case "INSULTING":
					if (t.hasInsulting())
						outputlist.add(t);
					break;
				case "WITHLINKS":
					if (t.hasLinks())
						outputlist.add(t);
					break;
				}
			}
			return outputlist;

		}

		return null;

	}

	/**
	 * Finds sequences in the input string containing the pos tag sequence and
	 * the subjects specified
	 * 
	 * @param input
	 *            input string
	 * @param subjects
	 *            list of subjects to search for
	 * @param tagSequence
	 *            sequence of tags to search for; ex: ["N","V"] or ["R","G"]
	 * @param pos
	 *            a pos tagger object to execute pos-tagging
	 * @param tknz
	 *            a tokenizer object to tokenize the input string
	 * @param KB
	 *            the reference knowledge base
	 * @param stopwords
	 *            the list of stopwords to exclude
	 * @return all the sequences from input string containing the specified
	 *         pos-tags sequence and subjects
	 */
	public static List<WeightedString> sequenceForSubjects(String input, List<Subject> subjects, String[] tagSequence, POS pos, Tokenizer tknz, KnowledgeBase KB,
			Set<String> stopwords) {

		List<String> tmp = new ArrayList<String>();
		List<WeightedString> weightedOutput = new ArrayList<WeightedString>();

		// remove hashtags
		input = input.replaceAll("#", "");

		String[] tokens = tknz.tokenize(input);
		List<String[]> sequences = pos.findPOSSequence(tokens, tagSequence);

		Iterator<String[]> it = sequences.iterator();

		// copy arrays elements in a single string
		while (it.hasNext()) {
			String[] strArr = it.next();

			String newString = Arrays.toString(strArr);
			// replace starting "[" and ending "]" and ","
			newString = newString.substring(1, newString.length() - 1).replaceAll(",", "");

			// remove stopwords
			newString = Utilities.removeStopWords(newString, stopwords);
			String trimmed = newString.trim();
			int words = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;

			// add string only if composed by 2 or more words
			if (words > 1)
				tmp.add(newString);

		}

		// check for sequences containing subjects
		Iterator<String> itstr = tmp.iterator();
		while (itstr.hasNext()) {
			String originalstr = itstr.next();
			String str = originalstr.toLowerCase();
			boolean found = false;

			Iterator<Subject> itsub = subjects.iterator();
			while (itsub.hasNext()) {
				Subject s = itsub.next();

				Iterator<String> s_it = s.getSurfaceForms().iterator();
				while (s_it.hasNext()) {
					// if(str.contains(s_it.next().toLowerCase())) //match also
					// for substring!
					if (str.matches(".*\\b#*" + s_it.next().toLowerCase() + "\\b.*")) // match
																						// exact
																						// word
						found = true;
				}
			}
			if (found) {
				WeightedString ws = new WeightedString(originalstr);
				ws.computeWeight(KB);
				weightedOutput.add(ws);
			}

		}

		return weightedOutput;

	}

	/**
	 * Extracts pos-sequences from a tweet.
	 * 
	 * @param inputTweet
	 *            the input tweet
	 * @param subjects
	 *            list of subjects to search for
	 * @param tagSequence
	 *            sequence of tags to search for; ex: ["N","V"] or ["R","G"]
	 * @param pos
	 *            a pos tagger object to execute pos-tagging
	 * @param tknz
	 *            a tokenizer object to tokenize the input string
	 * @param KB
	 *            the reference knowledge base
	 * @param stopwords
	 *            the list of stopwords to exclude
	 * @return all the sequences from the input tweet containing the specified
	 *         pos-tags sequence and subjects
	 */
	public static List<WeightedString> sequenceForSubjects(Tweet inputTweet, List<Subject> subjects, String[] tagSequence, POS pos, Tokenizer tknz, KnowledgeBase KB,
			Set<String> stopwords) {

		return sequenceForSubjects(inputTweet.getOriginaltext(), subjects, tagSequence, pos, tknz, KB, stopwords);

	}

	/**
	 * Extracts pos-sequences from a list of tweets. Results are ordered by
	 * relevancy
	 * 
	 * @param tlist
	 *            the input tweet list
	 * @param subjects
	 *            list of subjects to search for
	 * @param tagSequence
	 *            sequence of tags to search for; ex: ["N","V"] or ["R","G"]
	 * @param pos
	 *            a pos tagger object to execute pos-tagging
	 * @param tknz
	 *            a tokenizer object to tokenize the input string
	 * @param KB
	 *            the reference knowledge base
	 * @param stopwords
	 *            the list of stopwords to exclude
	 * @return all the sequences from the input tweets containing the specified
	 *         pos-tags sequence and subjects sorted by relevancy (descending)
	 */
	public static SortedSet<WeightedString> sequenceForSubjects(List<Tweet> tlist, List<Subject> subjects, String[] tagSequence, POS pos, Tokenizer tknz, KnowledgeBase KB,
			Set<String> stopwords) {

		SortedSet<WeightedString> output = new TreeSet<WeightedString>();
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		Iterator<Tweet> it = tlist.iterator();

		while (it.hasNext()) {

			Tweet t = it.next();
			List<WeightedString> list = sequenceForSubjects(t, subjects, tagSequence, pos, tknz, KB, stopwords);

			// compute frequency
			if (list.size() > 0) {

				Iterator<WeightedString> sit = list.iterator();
				while (sit.hasNext()) {
					WeightedString ws = sit.next();
					String key = ws.getString();

					if (map.containsKey(key)) {
						int count = (map.get(key) + 1);
						map.put(key, count);

					} else
						map.put(key, 1);

				}
			}

		}

		// compute weight
		Iterator<String> kit = map.keySet().iterator();
		while (kit.hasNext()) {
			String str = kit.next();
			WeightedString ws = new WeightedString(str, (double) map.get(str));
			ws.computeWeight(KB);
			output.add(ws);
		}

		return output;
	}

	/*
	 * public static int partiziona(int[][] v, int from, int to) { int []
	 * separatore=v[from]; int sep int i=from+1; int j=to; while(i<j) {
	 * while(i<j && v[i]<=separatore) i++; // ora i indica un valore maggiore di
	 * separatore // oppure è maggiore di j while(i<j && v[j]>=separatore) j--;
	 * // ora j indica un valore minore di separatore // oppure è minore o
	 * uguale a i if(i<j) { int app=v[i]; v[i]=v[j]; v[j]=app; } }
	 * if(v[i]>separatore) { i--; } // posiziona correttamente il separatore int
	 * app=v[i]; v[i]=v[from]; v[from]=app; return i; }
	 * 
	 * 
	 * 
	 * public static void quick(int[] v, int from, int to) { if(from>=to)
	 * return; else { int separazione=partiziona(v, from, to); quick(v, from,
	 * separazione-1); quick(v, separazione+1, to); } }
	 * 
	 * 
	 * 
	 * 
	 * public static void quickSort(int[] v) { quick(v, 0, v.length-1); }
	 */

}
