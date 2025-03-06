package fr.atlantique.imt.inf211.jobmngt.dao;
// Generated Jan 29, 2025, 4:45:04 PM by Hibernate Tools 5.6.15.Final

import fr.atlantique.imt.inf211.jobmngt.entity.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

/**
 * Home object for domain model class Sector.
 * 
 * @see .Sector
 * @author Hibernate Tools
 */
@Repository
public class SectorDao {

    private static final Logger logger = Logger.getLogger(SectorDao.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void persist(Sector transientInstance) {
        logger.log(Level.INFO, "persisting Sector instance");
        try {
            entityManager.persist(transientInstance);
            logger.log(Level.INFO, "persist successful");
        } catch (RuntimeException re) {
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
        } catch (RuntimeException re) {
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
        } catch (RuntimeException re) {
            logger.log(Level.SEVERE, "merge failed", re);
            throw re;
        }
    }

    @Transactional(readOnly = true)
    public Sector findById(int id) {
        logger.log(Level.INFO, "getting Sector instance with id: " + id);
        try {
            Sector instance = entityManager.find(Sector.class, id);
            logger.log(Level.INFO, "get successful");
            return instance;
        } catch (RuntimeException re) {
            logger.log(Level.SEVERE, "get failed", re);
            throw re;
        }
    }

    @Transactional(readOnly = true)
    public List<Sector> findAll(String sort, String order) {
        String r = "SELECT s FROM Sector s ORDER BY s." + sort;
        if (order.equals("asc")) {
            r += " ASC";
        } else {
            r += " DESC";
        }
        TypedQuery<Sector> q = entityManager.createQuery(r, Sector.class);
        return q.getResultList();
    }

    @Transactional(readOnly = true)
    public Long count() {
        String r = "select count(*) from Sector s";
        TypedQuery<Long> q = entityManager.createQuery(r, Long.class);
        return q.getSingleResult();
    }


    @Transactional(readOnly = true)
    public Optional<Sector> findByLabel(String label) {
        Optional<Sector> retOptional = Optional.empty();
        String r = "SELECT s FROM Sector s WHERE s.label = :lab";
        TypedQuery<Sector> q = entityManager.createQuery(r, Sector.class);
        q.setParameter("lab", label);
        List<Sector> res = q.getResultList();
        if (res.size() > 0) {
            retOptional = Optional.of(res.get(0));
        }
        return retOptional;
    }
}
