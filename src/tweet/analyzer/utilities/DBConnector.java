package tweet.analyzer.utilities;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import tweet.analyzer.core.KnowledgeBase;
import tweet.analyzer.core.Subject;
import tweet.analyzer.core.Tweet;
import tweet.analyzer.core.TweetAnalyzer;
import tweet.analyzer.model.data.AnalysisInterval;
import tweet.analyzer.model.data.Statistic_TopMostWord;
import tweet.analyzer.model.data.Statistic_TopMostWordDB;
import tweet.analyzer.model.data.SubjectsTag;
import tweet.analyzer.model.data.Statistic_SentimentDB;

/**
 * Class for DB interaction (connection, data retrieval)
 * 
 * @author TvPad
 *
 */
public class DBConnector {

	String connectionurl;

	public DBConnector(String connectionurl) {
		this.connectionurl = connectionurl;
	}

	public DBConnector(String serverpath, String dbname) {
		this.connectionurl = "jdbc:sqlserver://" + serverpath + ";databaseName=" + dbname + ";integratedSecurity=true";
	}

	public DBConnector() {

	}

	/**
	 * Extracts all the tweets contained in a specified DB table
	 * 
	 * @param dbtable
	 *            DB table to query
	 * @param requestID
	 *            value of the request_id field (may be null!)
	 * @return all the tweets in the specified table
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public List<Tweet> extractAllTweets(String dbtable, String requestID) throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {

		ArrayList<Tweet> outputlist = new ArrayList<Tweet>();

		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DriverManager.getConnection(connectionurl);

		Statement stat = conn.createStatement();

		String query = "SELECT * FROM " + dbtable;

		if (requestID != null && !requestID.isEmpty()) {
			query = query + " WHERE request_id = " + requestID;
		}

		ResultSet result = stat.executeQuery(query);

		while (result.next()) {

			Tweet t = tweetFromResultSet(result);
			outputlist.add(t);
		}
		result.close();
		conn.close();
		return outputlist;
	}
	
	public List<tweet.analyzer.model.data.SubjectsTag> extractSubjectsByTag(String[] tags) throws ClassNotFoundException, SQLException {

		ArrayList<tweet.analyzer.model.data.SubjectsTag> sublist = new ArrayList<tweet.analyzer.model.data.SubjectsTag>();

		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DriverManager.getConnection(connectionurl);

		Statement stat = conn.createStatement();

		String query = "SELECT * FROM Subject";

		if (tags != null && tags.length>0) {
			for(int i = 0;i<tags.length;i++){
				query = "SELECT * FROM Subject";
			//	System.out.println(tags[0]);
				query = query + " WHERE Tag like '%" + tags[i] + "%'";
				ResultSet result = stat.executeQuery(query);
				while (result.next()) {
					// AnalysisInterval t = analysisIntervalFromResultSet(result);
					// outputlist.add(t);
					String name = result.getString("Name");
					String tag = result.getString("Tag");
					int id = result.getInt("ID");
					String stopwords = result.getString("Stopwords");
					
					tweet.analyzer.model.data.SubjectsTag s = new tweet.analyzer.model.data.SubjectsTag(id, name, tag, stopwords);
					sublist.add(s);
				}
			}
			//result.close();
	//		conn.close();
			
		}

		
		return sublist;
	}
	
	
	
	public ArrayList<String> extractSubjectsWords(SubjectsTag sub) throws ClassNotFoundException, SQLException {

		ArrayList<String> outlist = new ArrayList<String>();

		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DriverManager.getConnection(connectionurl);

		Statement stat = conn.createStatement();
		
		String query = "SELECT s.Word FROM Subject_Word as s join Subject as s1 on (s.IDSubject=s1.ID)";

		if (sub != null) {
			
				query = query + " WHERE s1.ID="+sub.getId();
				ResultSet result = stat.executeQuery(query);
				while (result.next()) {
					String word = result.getString("Word");
					outlist.add(word);
				}
			
			//result.close();
	//		conn.close();
			
		}

		
		return outlist;
	}
	

	/**
	 * Extracts all the analysis intervals having the ID in the arrayList passed
	 * as argument
	 * 
	 * @param dbtable
	 *            DB table to query
	 * @param listID
	 *            list of the analysis intervals to retrieve
	 * @return all the analysis intervals in the specified table
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List<AnalysisInterval> extractAllAnalysisIntervals(String analysisIntervals) throws ClassNotFoundException, SQLException {

		ArrayList<AnalysisInterval> outputlist = new ArrayList<AnalysisInterval>();

		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DriverManager.getConnection(connectionurl);

		Statement stat = conn.createStatement();

		String query = "SELECT * FROM AnalysisInterval";

		if (analysisIntervals != null && !analysisIntervals.isEmpty()) {
			query = query + " WHERE ID in (" + analysisIntervals + ")";
		}

		ResultSet result = stat.executeQuery(query);

		while (result.next()) {
			// AnalysisInterval t = analysisIntervalFromResultSet(result);
			// outputlist.add(t);
		}
		result.close();
		conn.close();
		return outputlist;
	}

	/**
	 * Extract all tweets in the specified time interval
	 * 
	 * @param dbtable
	 *            the DB table to query
	 * @param requestID
	 *            value of the request_id field (may be null!)
	 * @param from
	 *            Start date in the format 'YYYY-DD-MM hh:mm:ss' (warning,
	 *            locale settings affect date format!)
	 * @param to
	 *            End date in the format 'YYYY-DD-MM hh:mm:ss' (warning, locale
	 *            settings affect date format!)
	 * @return all the tweets in the specified table created during the
	 *         specified time interval
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public List<Tweet> extractIntervalTweets(String dbtable, String requestID, String from, String to) throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {

		ArrayList<Tweet> outputlist = new ArrayList<Tweet>();

		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DriverManager.getConnection(connectionurl);

		Statement stat = conn.createStatement();

		String query = "SELECT * FROM " + dbtable + " WHERE creation_date >= '" + from + "' AND creation_date <= '" + to + "'";

		if (requestID != null && !requestID.isEmpty()) {
			query = query + " AND request_id in (" + requestID + ")";
		}

		ResultSet result = stat.executeQuery(query);

		while (result.next()) {
			Tweet t = tweetFromResultSet(result);
			outputlist.add(t);
		}

		result.close();
		conn.close();

		return outputlist;
	}

	/**
	 * Extracts last k tweets
	 * 
	 * @param dbtable
	 *            DB table to query
	 * @param k
	 *            maximum number of tweets to extract
	 * @param requestID
	 *            value of the request_id field (may be null!)
	 * @return last k tweets in the table filtered by request_id (if specified)
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public List<Tweet> extractLastKTweets(String dbtable, int k, String requestID) throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {

		ArrayList<Tweet> outputlist = new ArrayList<Tweet>();

		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DriverManager.getConnection(connectionurl);

		Statement stat = conn.createStatement();

		String query = "SELECT TOP " + k + " * FROM " + dbtable;

		if (requestID != null && !requestID.isEmpty()) {
			query = query + " WHERE request_id = " + requestID;
		}

		ResultSet result = stat.executeQuery(query);

		while (result.next()) {

			Tweet t = tweetFromResultSet(result);
			outputlist.add(t);

		}

		result.close();
		conn.close();

		return outputlist;

	}

	/**
	 * Extracts tweets from DB and filter by subjects and relevant words
	 * 
	 * @param dbtable
	 *            the DB table to query
	 * @param requestID
	 *            value of the request_id field (set null for no filtering on
	 *            request_id)
	 * @param from
	 *            Start date in the format 'YYYY-MM-DD hh:mm:ss' (set null for
	 *            no filtering on date)
	 * @param to
	 *            End date in the format 'YYYY-MM-DD hh:mm:ss' (set null for no
	 *            filtering on date)
	 * @param subjects
	 *            subjects to search for (set null for no filtering on subjects)
	 * @param relevantWords
	 *            relevant words to search for (set null for no filtering on
	 *            relevant words)
	 * @param KB
	 *            the reference KnowledgeBase
	 * @return all the tweets containing at least one of the specified subjects
	 *         and relevant words
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public List<Tweet> extractTweets(String dbtable, String requestID, String from, String to, List<Subject> subjects, String[] relevantWords, KnowledgeBase KB) throws FileNotFoundException, IOException {

		List<Tweet> output = new ArrayList<Tweet>();

		if (from != null && !from.isEmpty() && to != null && !to.isEmpty()) {
			try {
				output = extractIntervalTweets(dbtable, requestID, from, to);
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			try {
				output = extractAllTweets(dbtable, requestID);
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				return null;
			}
		}

		if (output != null && !output.isEmpty()) {
			// filters on subjects
			if (subjects != null && !subjects.isEmpty()) {
				output = TweetAnalyzer.selectTweetsForSubjects(output, subjects);
			}

			// filters on relevant words
			if (relevantWords != null && relevantWords.length > 0) {
				// TODO compute relevant words???
				TweetAnalyzer.computeRelevantWords(output, KB);
				output = TweetAnalyzer.containingRelevantWords(output, relevantWords);
			}

		}

		return output;

	}
	
	public java.util.Date timestampToDate(Timestamp t){
		java.util.Date date=null;
		String anno,mese,giorno,ora,minuti,secondi;
		String tsString= t.toString();
		/*	System.out.println(tsString);
		anno=String.valueOf(tsString.charAt(0))+String.valueOf(tsString.charAt(1))+String.valueOf(tsString.charAt(2))+String.valueOf(tsString.charAt(3));
		mese=String.valueOf(tsString.charAt(5))+String.valueOf(tsString.charAt(6));
		giorno=String.valueOf(tsString.charAt(8))+String.valueOf(tsString.charAt(9));
		ora=String.valueOf(tsString.charAt(11))+String.valueOf(tsString.charAt(12));
		minuti=String.valueOf(tsString.charAt(14))+String.valueOf(tsString.charAt(15));
		secondi=String.valueOf(tsString.charAt(17))+String.valueOf(tsString.charAt(18));*/
		date= TweetAnalyzer.convertDate(tsString);
		return date;
	}

	/**
	 * Builds a tweet object from a ResultSet object
	 * 
	 * @param result
	 *            the ResultSet object from a DB query
	 * @return a tweet object
	 * @throws SQLException
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public Tweet tweetFromResultSet(ResultSet result) throws SQLException, FileNotFoundException, IOException {

		String id = result.getString(1);
		String text = result.getString(7).replaceAll("(\\r|\\n)", ""); // fare
																		// preprocessing
																		// alla
																		// creazione?
		
		String language = result.getString(4);
		String longitude = result.getString(6);
		String latitude = result.getString(5);
//		Date createdAt = result.getDate(2);
//		Date createdAt = result.getTimestamp(2);
		Timestamp createdAt1 = result.getTimestamp(2);
		java.util.Date createdAt = timestampToDate(createdAt1);
		Integer favorites = result.getInt(3);
		String userId = result.getString(10);
		String userLocation = result.getString(12);
		Integer userListedCount = result.getInt(11);
		Integer userFollowersCount = result.getInt(9);
		String tweetID = result.getString(8);

		Tweet t = new Tweet(id, text, language, longitude, latitude, createdAt, favorites, userId, userLocation, userListedCount, userFollowersCount, tweetID);
		return t;

	}

	/**
	 * Create a corpus text file (one tweet per row) in UTF-8 format from a DB
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	public void corpusFileFromDB() throws ClassNotFoundException, SQLException, IOException {

		// PrintWriter out = new PrintWriter("BigCorpus.txt");
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("BigCorpus.txt"), "UTF-8"));

		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DriverManager.getConnection(connectionurl);

		Statement stat = conn.createStatement();

		String query = "SELECT * FROM tweet";
		ResultSet result = stat.executeQuery(query);

		while (result.next()) {
			out.write(result.getString(2).replaceAll("(\\r|\\n)", "") + "\n");
			// out.println(result.getString(2).replaceAll("(\\r|\\n)", ""));
		}
		out.close();

	}

	public void storeStatisticTopMostWordDB(Statistic_TopMostWordDB stmw) throws SQLException, ClassNotFoundException {
		
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DriverManager.getConnection(connectionurl);

		if(stmw==null)
			return;
		Statement stat = conn.createStatement();
		
		String query = "INSERT INTO Statistic_TopMostWord(IDAnalysisInterval, Word, Weight, TweetIdList, IDSubject, SubjectName, SubjectWords)";
		query=query+" VALUES ("+stmw.getIdAnalysisInterval()+",'"+stmw.getWord()+"',"+stmw.getWeight()+",'"+stmw.getTweetIDList()+"',"+stmw.getIdSubject()+",'"+stmw.getSubjectName()+"','"+stmw.getSubjectWords()+"')";
		
		stat.executeUpdate(query);

	}
	
public void storeStatisticSentimentDB(Statistic_SentimentDB stmw) throws SQLException, ClassNotFoundException {
		
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DriverManager.getConnection(connectionurl);

		if(stmw==null)
			return;
		Statement stat = conn.createStatement();
		
		String query = "INSERT INTO Statistic_Sentiment(IDAnalysisInterval, IDSubject, SubjectName, SubjectWords, LikeValue, LikeTweetIdList, HilariousValue, HilariousTweetIdList, SadValue, SadTweetIdList, AngryValue, AngryTweetIdList)";
		query=query+" VALUES ("+stmw.getIdAnalysisInterval()+","+stmw.getIdSubject()+",'"+stmw.getSubjectName()+"','"+stmw.getSubjectWords()+"',"+stmw.getLikeValue()+",'"+stmw.getLikeTweetIdList()+"',"+stmw.getHilariousValue()+",'"+stmw.getHilariousTweetIdList()+"',"+stmw.getSadValue()+",'"+stmw.getSadTweetIdList()+"',"+stmw.getAngryValue()+",'"+stmw.getAngryTweetIdList()+"')";
		
		stat.executeUpdate(query);

	}
}


