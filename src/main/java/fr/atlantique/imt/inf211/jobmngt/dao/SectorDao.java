package fr.atlantique.imt.inf211.jobmngt.dao;
// default package
// Generated 9 mars 2025, 15:48:50 by Hibernate Tools 5.6.15.Final


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Repository;
import fr.atlantique.imt.inf211.jobmngt.entity.*;

/**
 * Home object for domain model class Sector.
 * @see .Sector
 * @author Hibernate Tools
 */
@Repository
public class SectorDao {

    private static final Logger logger = Logger.getLogger(SectorDao.class.getName());

    @PersistenceContext private EntityManager entityManager;
    @Transactional
    public void persist(Sector transientInstance) {
        logger.log(Level.INFO, "persisting Sector instance");
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
    public void remove(Sector persistentInstance) {
        logger.log(Level.INFO, "removing Sector instance");
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
    public Sector merge(Sector detachedInstance) {
        logger.log(Level.INFO, "merging Sector instance");
        try {
            Sector result = entityManager.merge(detachedInstance);
            logger.log(Level.INFO, "merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "merge failed", re);
            throw re;
        }
    }
    
    @Transactional(readOnly = true)
    public Sector findById( int id) {
        logger.log(Level.INFO, "getting Sector instance with id: " + id);
        try {
            Sector instance = entityManager.find(Sector.class, id);
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
        logger.log(Level.INFO, "counting Sector instances");
        try {
            long result = entityManager.createQuery("select count(*) from Sector", Long.class).getSingleResult();
            logger.log(Level.INFO, "count successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "count failed", re);
            throw re;
        }
    }

    @Transactional(readOnly = true)
    public List<Sector> findAll(String orderField, String order) {
        logger.log(Level.INFO, "getting all Sector instances");
        try {
            String r = "select s from Sector s order by s." + orderField + " " + order;
            TypedQuery<Sector> q = entityManager.createQuery(r, Sector.class);
            List<Sector> result = q.getResultList();
            logger.log(Level.INFO, "get successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "get failed", re);
            throw re;
        }
    }

    @Transactional(readOnly = true)
    public Sector findByLabel(String labelSecteur) {
        logger.log(Level.INFO, "getting Sector instance with labelSecteur: " + labelSecteur);
        try {
            Sector instance = entityManager.createQuery("SELECT s FROM Sector s WHERE s.labelSecteur = :labelSecteur", Sector.class)
                .setParameter("labelSecteur", labelSecteur)
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

