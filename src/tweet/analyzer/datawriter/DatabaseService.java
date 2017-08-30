package tweet.analyzer.datawriter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import tweet.analyzer.model.data.AnalysisInterval;
import tweet.analyzer.model.data.Event;
import tweet.analyzer.model.data.Request;
import tweet.analyzer.model.data.Source;
import tweet.analyzer.model.data.SourcesRequest;
import tweet.analyzer.model.data.Statistic_Sentiment;
import tweet.analyzer.model.data.Statistic_TopMostWord;
import tweet.analyzer.model.data.Statistic_TrendingSubject;
import tweet.analyzer.model.data.Statistic_WordCloud;

public class DatabaseService {

	private static final String PERSISTENCE_UNIT_NAME = "TweetAnalyzer";
	private static EntityManagerFactory factory;
	private EntityManager entityManager;
	private String url;
	private String user;
	private String password;
	private String driver;

	// QUERYES ()

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public void connect() throws Exception {

		HashMap<String, String> dbPropertiesMap = new HashMap<String, String>();
		dbPropertiesMap.put("javax.persistence.jdbc.url", url);
		dbPropertiesMap.put("javax.persistence.jdbc.user", user);
		dbPropertiesMap.put("javax.persistence.jdbc.password", password);
		dbPropertiesMap.put("javax.persistence.jdbc.driver", driver);

		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, dbPropertiesMap);
		if (factory != null) {
			try {
				entityManager = factory.createEntityManager();
			} catch (PersistenceException e) {
			}

		} else {
			throw new Exception("No Persistence provider for EntityManager named:" + PERSISTENCE_UNIT_NAME);
		}
	}

	public void disconnect() {
		if (entityManager != null) {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	public Request getRequest(Long requestId) throws Exception {
		try {
			Request request = (Request) entityManager.createNamedQuery("Request.findById").setParameter("id", requestId).getSingleResult();
			return request;
		} catch (NoResultException e) {
			throw new Exception("Request with id " + requestId + " not found");
		}
	}

	@SuppressWarnings("unchecked")
	public List<Source> getSources(Request request) {
		List<SourcesRequest> resultList = entityManager.createNamedQuery("SourcesRequest.findByRequestId").setParameter("id", request.getId()).getResultList();
		for (SourcesRequest sr : resultList) {
			System.out.println(sr.getSource().getSocialNetwork());
		}
		return null;
	}

	public Event getEvent(Integer eventID) throws Exception {
		try {
			Event request = (Event) entityManager.createNamedQuery("Event.findById").setParameter("id", eventID).getSingleResult();
			return request;
		} catch (NoResultException e) {
			throw new Exception("Event with id " + eventID + " not found");
		}
	}

	public AnalysisInterval getAnalysisInterval(Integer analysisIntervalID) throws Exception {
		try {
			AnalysisInterval request = (AnalysisInterval) entityManager.createNamedQuery("AnalysisInterval.findById").setParameter("id", analysisIntervalID).getSingleResult();
			return request;
		} catch (NoResultException e) {
			throw new Exception("Event with id " + analysisIntervalID + " not found");
		}
	}

	public Statistic_Sentiment getStatistic_Sentiment(Integer statID) throws Exception {
		try {
			Statistic_Sentiment request = (Statistic_Sentiment) entityManager.createNamedQuery("Statistic_Sentiment.findById").setParameter("id", statID).getSingleResult();
			return request;
		} catch (NoResultException e) {
			throw new Exception("Event with id " + statID + " not found");
		}
	}

	public Statistic_TrendingSubject getStatistic_TrendingSubject(Integer statID) throws Exception {
		try {
			Statistic_TrendingSubject request = (Statistic_TrendingSubject) entityManager.createNamedQuery("Statistic_TrendingSubject.findById").setParameter("id", statID)
					.getSingleResult();
			return request;
		} catch (NoResultException e) {
			throw new Exception("Event with id " + statID + " not found");
		}
	}

	public void storeEvent(Event ev) throws DatabaseException {
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(ev);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public void storeStatisticTrendingSubject(Statistic_TrendingSubject stw) throws DatabaseException {
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(stw);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public void storeStatisticTopMostWord(Statistic_TopMostWord stmw) throws DatabaseException {
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(stmw);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		}
	}
	
	public void storeStatisticWordCloud(Statistic_WordCloud swc) throws DatabaseException {
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(swc);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public void storeAnalysisInterval(AnalysisInterval ai) throws DatabaseException {
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(ai);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public void storeStatistic_Sentiment(Statistic_Sentiment sent) throws DatabaseException {
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(sent);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public void storeStatistic_TrendingSubject(Statistic_TrendingSubject tw) throws DatabaseException {
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(tw);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public void editStatistic_Sentiment(Integer idSent, BigDecimal likevalue, BigDecimal hilariousvalue, BigDecimal sadvalue, BigDecimal angryvalue) throws DatabaseException {
		Statistic_Sentiment r;
		try {
			r = getStatistic_Sentiment(idSent);
			entityManager.getTransaction().begin();
			r.setLikevalue(likevalue);
			r.setHilariousvalue(hilariousvalue);
			r.setSadvalue(sadvalue);
			r.setAngryvalue(angryvalue);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public void editStatistic_TrendingSubject(Integer idSent, Integer interv1, Integer interv2, Integer interv3, Integer interv4, Integer interv5) throws DatabaseException {
		Statistic_TrendingSubject r;
		try {
			r = getStatistic_TrendingSubject(idSent);
			entityManager.getTransaction().begin();
			r.setInterval1(interv1);
			r.setInterval2(interv2);
			r.setInterval3(interv3);
			r.setInterval4(interv4);
			r.setInterval5(interv5);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public void deleteAllStatistic_TrendingSubjectOfInterval(Integer analysisIntervalID) throws DatabaseException {
		try {
			String selectQuery = "SELECT a FROM Statistic_TrendingSubject a WHERE a.analysisInterval.id=" + analysisIntervalID;
			List<Statistic_TrendingSubject> statsToRemove = entityManager.createQuery(selectQuery, Statistic_TrendingSubject.class).getResultList();
			for (Statistic_TrendingSubject m : statsToRemove) {
				entityManager.getTransaction().begin();
				entityManager.remove(m);
				entityManager.getTransaction().commit();
			}
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public void deleteAllStatistic_TopMostWordOfInterval(Integer analysisIntervalID) throws DatabaseException {
		try {
			String selectQuery = "SELECT a FROM Statistic_TopMostWord a WHERE a.analysisInterval.id=" + analysisIntervalID;
			List<Statistic_TopMostWord> statsToRemove = entityManager.createQuery(selectQuery, Statistic_TopMostWord.class).getResultList();
			for (Statistic_TopMostWord m : statsToRemove) {
				entityManager.getTransaction().begin();
				entityManager.remove(m);
				entityManager.getTransaction().commit();
			}
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		}
	}
	
	public void deleteAllStatistic_WordCloudOfInterval(Integer analysisIntervalID) throws DatabaseException {
		try {
			String selectQuery = "SELECT a FROM Statistic_WordCloud a WHERE a.analysisInterval.id=" + analysisIntervalID;
			List<Statistic_WordCloud> statsToRemove = entityManager.createQuery(selectQuery, Statistic_WordCloud.class).getResultList();
			for (Statistic_WordCloud m : statsToRemove) {
				entityManager.getTransaction().begin();
				entityManager.remove(m);
				entityManager.getTransaction().commit();
			}
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public void deleteAllStatistic_SentimentOfInterval(Integer analysisIntervalID) throws DatabaseException {
		try {
			String selectQuery = "SELECT a FROM Statistic_Sentiment a WHERE a.analysisInterval.id=" + analysisIntervalID;
			List<Statistic_Sentiment> statsToRemove = entityManager.createQuery(selectQuery, Statistic_Sentiment.class).getResultList();
			for (Statistic_Sentiment m : statsToRemove) {
				entityManager.getTransaction().begin();
				entityManager.remove(m);
				entityManager.getTransaction().commit();
			}
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	// public void deleteAllStatistic_Sentiment_TweetOfInterval(Integer
	// analysisIntervalID) throws DatabaseException {
	// try {
	// String selectQuery = "SELECT a FROM Statistic_Sentiment_Tweet a WHERE
	// a.statistic_sentiment.analysisInterval.id=" + analysisIntervalID;
	// List<Statistic_Sentiment_Tweet> statsToRemove =
	// entityManager.createQuery(selectQuery,
	// Statistic_Sentiment_Tweet.class).getResultList();
	// for (Statistic_Sentiment_Tweet m : statsToRemove) {
	// entityManager.getTransaction().begin();
	// entityManager.remove(m);
	// entityManager.getTransaction().commit();
	// }
	// } catch (Exception e) {
	// throw new DatabaseException(e.getMessage());
	// }
	// }

	public List<AnalysisInterval> extractAllAnalysisIntervals(String analysisIntervals) throws Exception {
		TypedQuery<AnalysisInterval> lQuery = entityManager.createQuery("SELECT a FROM AnalysisInterval a WHERE a.id in (" + analysisIntervals + ")", AnalysisInterval.class);
		return lQuery.getResultList();
	}

	public List<Request> extractAllRequestsOfEvent(String idEvent) throws Exception {
		TypedQuery<Request> lQuery = entityManager.createQuery("SELECT a FROM Request a WHERE a.event.id =" + idEvent, Request.class);
		return lQuery.getResultList();
	}

	public List<Request> extractAllRequestsInAnalysisInterval(String idEvent, String from, String to) throws Exception {
		TypedQuery<Request> lQuery = entityManager.createQuery("SELECT a FROM Request a WHERE a.event.id =" + idEvent +
				" AND ((a.startDate >= '" + from + "' AND a.stopDate <= '" + to + "')"+
				" OR (a.startDate <= '" + from + "' AND a.stopDate >= '" + to + "')"+
				" OR (a.startDate <= '" + from + "' AND a.stopDate >= '" + from + "' AND a.stopDate <= '" + to + "')"+
				" OR (a.startDate >= '" + from + "' AND a.startDate <= '" + to + "' AND a.stopDate >= '" + to + "'))", Request.class);
		return lQuery.getResultList();
	}
}
