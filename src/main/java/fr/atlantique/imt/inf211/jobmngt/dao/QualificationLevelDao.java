package fr.atlantique.imt.inf211.jobmngt.dao;
// default package
// Generated 9 mars 2025, 15:48:50 by Hibernate Tools 5.6.15.Final


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import fr.atlantique.imt.inf211.jobmngt.entity.*;
import org.springframework.transaction.annotation.Transactional;

/**
 * Home object for domain model class Qualificationlevel.
 * @see .Qualificationlevel
 * @author Hibernate Tools
 */
@Repository
public class QualificationLevelDao {

    private static final Logger logger = Logger.getLogger(QualificationLevelDao.class.getName());

    @PersistenceContext private EntityManager entityManager;
    @Transactional
    public void persist(QualificationLevel transientInstance) {
        logger.log(Level.INFO, "persisting Qualificationlevel instance");
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
    public void remove(QualificationLevel persistentInstance) {
        logger.log(Level.INFO, "removing Qualificationlevel instance");
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
    public QualificationLevel merge(QualificationLevel detachedInstance) {
        logger.log(Level.INFO, "merging Qualificationlevel instance");
        try {
            QualificationLevel result = entityManager.merge(detachedInstance);
            logger.log(Level.INFO, "merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "merge failed", re);
            throw re;
        }
    }
    
    @Transactional(readOnly = true)
    public QualificationLevel findById( int id) {
        logger.log(Level.INFO, "getting Qualificationlevel instance with id: " + id);
        try {
            QualificationLevel instance = entityManager.find(QualificationLevel.class, id);
            logger.log(Level.INFO, "get successful");
            return instance;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "get failed", re);
            throw re;
        }
    }

    @Transactional(readOnly = true)
    public long count() {
        return entityManager.createQuery("SELECT COUNT(q) FROM QualificationLevel q", Long.class).getSingleResult();
    }

    @Transactional(readOnly = true)
    public List<QualificationLevel> findAll(String orderField, String order) {
        return entityManager.createQuery("SELECT q FROM QualificationLevel q ORDER BY q." + orderField + " " + order, QualificationLevel.class).getResultList();
    }

    @Transactional(readOnly = true)
    public List<QualificationLevel> findAll() {
        return entityManager.createQuery("SELECT q FROM QualificationLevel q", QualificationLevel.class).getResultList();
    }

    @Transactional(readOnly = true)
    public QualificationLevel findByLabel(String label) {
        logger.log(Level.INFO, "getting QualificationLevel instance with label: " + label);
        try {
            return entityManager.createQuery(
                    "SELECT q FROM QualificationLevel q WHERE LOWER(q.labelQualification) = LOWER(:label)", 
                    QualificationLevel.class)
                    .setParameter("label", label)
                    .getSingleResult();
        } catch (NoResultException nre) {
            logger.log(Level.INFO, "No qualification level found with label: " + label);
            return null;
        } catch (RuntimeException re) {
            logger.log(Level.SEVERE, "findByLabel failed", re);
            throw re;
        }
    }


}

