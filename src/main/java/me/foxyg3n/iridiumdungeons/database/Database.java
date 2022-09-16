package me.foxyg3n.iridiumdungeons.database;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class Database {

    private SessionFactory sessionFactory;
    
    public Database(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSession() {
        return sessionFactory.openSession();
    }

    public boolean contains(Class<?> clazz, Serializable id) {
        return get(clazz, id) != null;
    }

    public void save(Object object) {
        Session session = getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.saveOrUpdate(object);
            transaction.commit();
        } catch(HibernateException e) {
            if(transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public <T> T get(Class<T> type, Serializable id) {
        Session session = getSession();
        Transaction transaction = null;
        T result = null;
        try {
            transaction = session.beginTransaction();
            result = session.get(type, id);
            transaction.commit();
        } catch(HibernateException e) {
            if(transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }

    public void delete(Object object) {
        Session session = getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.delete(object);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace(); 
        } finally {
            session.close(); 
        }
    }

    public <T> List<T> getList(Class<T> type, String query) {
        return getSession().createQuery(query, type).getResultList();
    }

    public <T> Query<T> createQuery(Class<T> type, String query) {
        return getSession().createQuery(query, type);
    }

}
