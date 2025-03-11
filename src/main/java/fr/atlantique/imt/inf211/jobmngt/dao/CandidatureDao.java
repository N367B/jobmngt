package fr.atlantique.imt.inf211.jobmngt.dao;
// default package
// Generated 9 mars 2025, 15:48:50 by Hibernate Tools 5.6.15.Final


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import fr.atlantique.imt.inf211.jobmngt.entity.*;
import org.springframework.transaction.annotation.Transactional;

/**
 * Home object for domain model class Candidature.
 * @see .Candidature
 * @author Hibernate Tools
 */
@Repository
public class CandidatureDao {

    private static final Logger logger = Logger.getLogger(CandidatureDao.class.getName());

    @PersistenceContext private EntityManager entityManager;
    @Transactional
    public void persist(Candidature transientInstance) {
        logger.log(Level.INFO, "persisting Candidature instance");
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
    public void remove(Candidature persistentInstance) {
        logger.log(Level.INFO, "removing Candidature instance");
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
    public Candidature merge(Candidature detachedInstance) {
        logger.log(Level.INFO, "merging Candidature instance");
        try {
            Candidature result = entityManager.merge(detachedInstance);
            logger.log(Level.INFO, "merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "merge failed", re);
            throw re;
        }
    }
    
    @Transactional(readOnly = true)
    public Candidature findById( int id) {
        logger.log(Level.INFO, "getting Candidature instance with id: " + id);
        try {
            Candidature instance = entityManager.find(Candidature.class, id);
            logger.log(Level.INFO, "get successful");
            return instance;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "get failed", re);
            throw re;
        }
    }

    @Transactional(readOnly = true)
    public List<Candidature> findBySectorAndQualificationLevel(Sector sector, QualificationLevel qualificationLevel) {
        logger.log(Level.INFO, "getting Candidature instance with sector: " + sector + " and qualification level: " + qualificationLevel);
        try {
            List<Candidature> instances = entityManager.createQuery("SELECT c FROM Candidature c JOIN c.sectors s WHERE s = :sector AND c.qualificationLevel = :qualificationLevel", Candidature.class)
            .setParameter("sector", sector)
            .setParameter("qualificationLevel", qualificationLevel)
            .getResultList();
            logger.log(Level.INFO, "get successful");
            return instances;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "get failed", re);
            throw re;
        }
    }

    @Transactional(readOnly = true)
    public List<Candidature> findAll() {
        logger.log(Level.INFO, "getting all Candidature instances");
        try {
            List<Candidature> instances = entityManager.createQuery("select c from Candidature c", Candidature.class).getResultList();
            logger.log(Level.INFO, "get successful");
            return instances;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "get failed", re);
            throw re;
        }
    }


}