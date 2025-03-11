package fr.atlantique.imt.inf211.jobmngt.dao;
// default package
// Generated 9 mars 2025, 15:48:50 by Hibernate Tools 5.6.15.Final


import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import fr.atlantique.imt.inf211.jobmngt.entity.*;
import org.springframework.transaction.annotation.Transactional;

/**
 * Home object for domain model class Messageoffre.
 * @see .Messageoffre
 * @author Hibernate Tools
 */
@Repository
public class MessageOffreDao {

    private static final Logger logger = Logger.getLogger(MessageOffreDao.class.getName());

    @PersistenceContext private EntityManager entityManager;
    @Transactional
    public void persist(MessageOffre transientInstance) {
        logger.log(Level.INFO, "persisting Messageoffre instance");
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
    public void remove(MessageOffre persistentInstance) {
        logger.log(Level.INFO, "removing Messageoffre instance");
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
    public MessageOffre merge(MessageOffre detachedInstance) {
        logger.log(Level.INFO, "merging Messageoffre instance");
        try {
            MessageOffre result = entityManager.merge(detachedInstance);
            logger.log(Level.INFO, "merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "merge failed", re);
            throw re;
        }
    }
    
    @Transactional(readOnly = true)
    public MessageOffre findById( int id) {
        logger.log(Level.INFO, "getting Messageoffre instance with id: " + id);
        try {
            MessageOffre instance = entityManager.find(MessageOffre.class, id);
            logger.log(Level.INFO, "get successful");
            return instance;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "get failed", re);
            throw re;
        }
    }
}

