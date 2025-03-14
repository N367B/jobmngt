package fr.atlantique.imt.inf211.jobmngt.dao;
// default package
// Generated 9 mars 2025, 15:48:50 by Hibernate Tools 5.6.15.Final


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Repository;
import fr.atlantique.imt.inf211.jobmngt.entity.*;

/**
 * Home object for domain model class Candidat.
 * @see .Candidat
 * @author Hibernate Tools
 */
@Repository
public class CandidatDao {

    private static final Logger logger = Logger.getLogger(CandidatDao.class.getName());

    @PersistenceContext private EntityManager entityManager;
    @Transactional
    public void persist(Candidat transientInstance) {
        logger.log(Level.INFO, "persisting Candidat instance");
        try {
            entityManager.persist(transientInstance);
            logger.log(Level.INFO, "persist successful");
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "persist failed", re);
            throw re;
        }
    }
    
    @Transactional(readOnly = true)
    public Candidat findByUserMail(String mail) {
    logger.log(Level.INFO, "getting Candidat instance with AppUser mail: " + mail);
    try {
        return entityManager.createQuery(
                "SELECT c FROM Candidat c JOIN c.appUser u WHERE u.mail = :mail", 
                Candidat.class)
                .setParameter("mail", mail)
                .getSingleResult();
    } catch (NoResultException nre) {
        logger.log(Level.INFO, "No candidat found with user mail: " + mail);
        return null;
    } catch (RuntimeException re) {
        logger.log(Level.SEVERE, "findByUserMail failed", re);
        throw re;
    }
}

    @Transactional
    public void remove(Candidat persistentInstance) {
        logger.log(Level.INFO, "removing Candidat instance");
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
    public Candidat merge(Candidat detachedInstance) {
        logger.log(Level.INFO, "merging Candidat instance");
        try {
            Candidat result = entityManager.merge(detachedInstance);
            logger.log(Level.INFO, "merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "merge failed", re);
            throw re;
        }
    }
    
    @Transactional(readOnly = true)
    public Candidat findById( int id) {
        logger.log(Level.INFO, "getting Candidat instance with id: " + id);
        try {
            Candidat instance = entityManager.find(Candidat.class, id);
            logger.log(Level.INFO, "get successful");
            return instance;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "get failed", re);
            throw re;
        }
    }

    @Transactional(readOnly = true)
    public List<Candidat> findAll() {
        logger.log(Level.INFO, "getting all Candidat instances");
        try {
            List<Candidat> instances = entityManager.createQuery("select c from Candidat c", Candidat.class).getResultList();
            logger.log(Level.INFO, "get successful");
            return instances;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "get failed", re);
            throw re;
        }
    }
}

