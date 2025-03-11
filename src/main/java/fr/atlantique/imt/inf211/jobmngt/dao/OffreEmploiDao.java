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
 * Home object for domain model class Offreemploi.
 * @see .Offreemploi
 * @author Hibernate Tools
 */
@Repository
public class OffreEmploiDao {

    private static final Logger logger = Logger.getLogger(OffreEmploiDao.class.getName());

    @PersistenceContext private EntityManager entityManager;
    @Transactional
    public void persist(OffreEmploi transientInstance) {
        logger.log(Level.INFO, "persisting Offreemploi instance");
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
    public void remove(OffreEmploi persistentInstance) {
        logger.log(Level.INFO, "removing Offreemploi instance");
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
    public OffreEmploi merge(OffreEmploi detachedInstance) {
        logger.log(Level.INFO, "merging Offreemploi instance");
        try {
            OffreEmploi result = entityManager.merge(detachedInstance);
            logger.log(Level.INFO, "merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "merge failed", re);
            throw re;
        }
    }
    
    @Transactional(readOnly = true)
    public OffreEmploi findById( int id) {
        logger.log(Level.INFO, "getting Offreemploi instance with id: " + id);
        try {
            OffreEmploi instance = entityManager.find(OffreEmploi.class, id);
            logger.log(Level.INFO, "get successful");
            return instance;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "get failed", re);
            throw re;
        }
    }

    @Transactional(readOnly = true)
    public List<OffreEmploi> findAll() {
        logger.log(Level.INFO, "getting all Offreemploi instances");
        try {
            List<OffreEmploi> instances = entityManager.createQuery("select o from OffreEmploi o", OffreEmploi.class).getResultList();
            logger.log(Level.INFO, "get successful");
            return instances;
        }
        catch (RuntimeException re) {
            logger.log(Level.SEVERE, "get failed", re);
            throw re;
        }
    }

    @Transactional(readOnly = true)
    public List<OffreEmploi> findByEntreprise(Entreprise entreprise) {
        logger.log(Level.INFO, "getting OffreEmploi instance with entreprise: " + entreprise);
        try {
            List<OffreEmploi> instances = entityManager.createQuery("SELECT o FROM OffreEmploi o WHERE o.entreprise = :entreprise", OffreEmploi.class)
                .setParameter("entreprise", entreprise)
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
    public List<OffreEmploi> findBySectorAndQualificationLevel(Sector sector, QualificationLevel qualificationLevel) {
        logger.log(Level.INFO, "getting OffreEmploi instance with sector: " + sector + " and qualification level: " + qualificationLevel);
        try {
            List<OffreEmploi> instances = entityManager.createQuery("SELECT o FROM OffreEmploi o WHERE :sector MEMBER OF o.sectors AND o.qualificationLevel = :qualificationLevel", OffreEmploi.class)
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
}

