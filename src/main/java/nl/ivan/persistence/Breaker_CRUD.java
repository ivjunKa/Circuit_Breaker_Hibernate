package nl.ivan.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import org.hibernate.query.Query;

public class Breaker_CRUD implements Observer {
	private SessionFactory sessionFactory;
	private List<Observable> listWithObservableBreakers = new ArrayList<>();

	public Breaker_CRUD() {
		setSessionFactory();
	}

	private void setSessionFactory() {
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
		try {
			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		} catch (Exception ex) {
			System.out.println(ex);
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}

	public void insert(Breaker breaker) {
		System.out.println("inserting breaker....");
		// needs to receive actual breaker and read all the data from it
		if (breaker.getState() == null || breaker.getFunctionWrapped() == null || breaker.getPrefix() == null) {
			throw new NullPointerException("One of the params were never setted");
		}
		Session session = sessionFactory.openSession();
		Query ifExists = session
				.createQuery("from Breaker where prefix= :prefix and functionwrapped= :functionwrapped");
		ifExists.setParameter("prefix", breaker.getPrefix());
		ifExists.setParameter("functionwrapped", breaker.getFunctionWrapped());
		List<Breaker> listWithBreakers = ifExists.list();
		session.beginTransaction();
		if (listWithBreakers.isEmpty()) {
			System.out.println("There is no breaker like that, saving new one....");
			session.save(breaker);
		} else {
			for (Breaker br : listWithBreakers) {
				System.out.println("There is an existing breaker, trying to get state and update status....");
				br.setStatus("checked_in");
				session.saveOrUpdate(br);
			}
		}
		session.getTransaction().commit();
		session.close();
	}

	public void delete() {

	}

	public void update(Breaker breaker) {
		System.out.println("updating breaker");
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(breaker);
		session.getTransaction().commit();
		session.close();
	}

	public Breaker get(long breakerID) {
		Session session = sessionFactory.openSession();
		Breaker breaker = session.get(Breaker.class, breakerID);

		System.out.println("ID: " + breaker.getId());
		System.out.println("Last known state: " + breaker.getState());
		System.out.println("Assotiated function: " + breaker.getFunctionWrapped());

		session.close();
		return null;
	}

	// This cleans-up the following breakers:
	// 1. That were commented out
	// 2. Where the name of the 'wrapped' function was changed
	public void cleanupBreakers() {
		System.out.println("Cleaning things up");
		Session session = sessionFactory.openSession();
		Query<Breaker> query = session.createQuery("from Breaker where status= :status");
		query.setParameter("status", "checked_in");
		List<Breaker> listWithBreakers = query.list();
		// now get everything that 'checked_in'
		for (Breaker br : listWithBreakers) {
			System.out.println(
					"Here are the breakers that are checked in: " + br.getFunctionWrapped() + " " + br.getPrefix());
		}
		// delete 'old' breakers (i.e. all that are not checked_in)
		session.beginTransaction();
		Query<Breaker> querydeleteOldBreakers = session.createQuery("delete from Breaker where status= :status");
		querydeleteOldBreakers.setParameter("status", "loaded");
		int resultDeleted = querydeleteOldBreakers.executeUpdate();
		System.out.println("Breaker Delete Status=" + resultDeleted);
		// update database - mark all checked_in as _loaded so that they need to do
		// checkin next time
		Query<Breaker> queryUpdateStatus = session
				.createQuery("update Breaker set status= :status where status='checked_in'");
		queryUpdateStatus.setParameter("status", "loaded");
		int resultUpdated = queryUpdateStatus.executeUpdate();
		System.out.println("Breaker Update Status=" + resultUpdated);
		session.getTransaction().commit();
		session.close();
	}

	public void registerBreakerListener(Breaker b) {
		b.addObserver(this);
		insert(b);
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Implement save(or update) routine here
		System.out.println("observer triggered");
	}

}
