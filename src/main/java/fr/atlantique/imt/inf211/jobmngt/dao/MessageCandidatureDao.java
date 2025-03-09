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
 * Home object for domain model class Messagecandidature.
 * @see .Messagecandidature
 * @author Hibernate Tools
 */
@Repository
public class MessageCandidatureDao {

    private static final Logger logger = Logger.getLogger(MessageCandidatureDao.class.getName());

    @PersistenceContext private EntityManager entityManager;
    @Transactional
    public void persist(MessageCandidature transientInstance) {
        logger.log(Level.INFO, "persisting Messagecandidature instance");
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
    public void remove(MessageCandidature persistentInstance) {
        logger.log(Level.INFO, "removing Messagecandidature instance");
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
    public MessageCandidature merge(MessageCandidature detachedInstance) {
        logger.log(Level.INFO, "merging Messagecandidature instance");
        try {
            MessageCandidature result = entityManager.merge(detachedInstance);
            logger.log(Level.INFO, "merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "merge failed", re);
            throw re;
        }
    }
    
    @Transactional(readOnly = true)
    public MessageCandidature findById( int id) {
        logger.log(Level.INFO, "getting Messagecandidature instance with id: " + id);
        try {
            MessageCandidature instance = entityManager.find(MessageCandidature.class, id);
            logger.log(Level.INFO, "get successful");
            return instance;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "get failed", re);
            throw re;
        }
    }
}

