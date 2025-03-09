package fr.atlantique.imt.inf211.jobmngt.dao;
// default package
// Generated 9 mars 2025, 15:48:50 by Hibernate Tools 5.6.15.Final


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import fr.atlantique.imt.inf211.jobmngt.entity.*;
import org.springframework.transaction.annotation.Transactional;

/**
 * Home object for domain model class Entreprise.
 * @see .Entreprise
 * @author Hibernate Tools
 */
@Repository
public class EntrepriseDao {

    private static final Logger logger = Logger.getLogger(EntrepriseDao.class.getName());

    @PersistenceContext private EntityManager entityManager;
    @Transactional
    public void persist(Entreprise transientInstance) {
        logger.log(Level.INFO, "persisting Entreprise instance");
        try {
            entityManager.persist(transientInstance);
            logger.log(Level.INFO, "persist successful");
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "persist failed", re);
            throw re;
        }
    }
    
    @Transactional
    public void remove(Entreprise persistentInstance) {
        logger.log(Level.INFO, "removing Entreprise instance");
        try {
            entityManager.remove(persistentInstance);
            logger.log(Level.INFO, "remove successful");
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "remove failed", re);
            throw re;
        }
    }
    
    @Transactional
    public Entreprise merge(Entreprise detachedInstance) {
        logger.log(Level.INFO, "merging Entreprise instance");
        try {
            Entreprise result = entityManager.merge(detachedInstance);
            logger.log(Level.INFO, "merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "merge failed", re);
            throw re;
        }
    }
    
    @Transactional(readOnly = true)
    public Entreprise findById( int id) {
        logger.log(Level.INFO, "getting Entreprise instance with id: " + id);
        try {
            Entreprise instance = entityManager.find(Entreprise.class, id);
            logger.log(Level.INFO, "get successful");
            return instance;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "get failed", re);
            throw re;
        }
    }

    @Transactional(readOnly = true)
    public List<Entreprise> findAll(String orderField, String order) {
        logger.log(Level.INFO, "finding all Entreprise instances");
        try {
            String queryString = "from Entreprise order by " + orderField + " " + order;
            Query query = (Query) entityManager.createQuery(queryString);
            logger.log(Level.INFO, "find all successful");
            return query.getResultList();
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "find all failed", re);
            throw re;
        }
    }

    @Transactional(readOnly = true)
    public Entreprise findByAppUser(AppUser appUser) {
        logger.log(Level.INFO, "getting Entreprise instance with appUser: " + appUser);
        try {
            Entreprise instance = entityManager.createQuery("SELECT e FROM Entreprise e WHERE e.appUser = :appUser", Entreprise.class)
            .setParameter("appUser", appUser)
            .getSingleResult();
            logger.log(Level.INFO, "get successful");
            return instance;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "get failed", re);
            throw re;
        }
    }
}

