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
    /*
    @Transactional(readOnly = true)
    public Candidature findById(int id) {
        logger.log(Level.INFO, "getting Candidature instance with id: " + id);
        try {
            // Utiliser une requête JPQL avec fetch join pour charger les relations importantes
            Candidature instance = entityManager.createQuery(
                    "SELECT c FROM Candidature c " +
                    "LEFT JOIN FETCH c.candidat " +
                    "LEFT JOIN FETCH c.candidat.appUser " +
                    "LEFT JOIN FETCH c.qualificationLevel " +
                    "LEFT JOIN FETCH c.sectors " +
                    "LEFT JOIN FETCH c.messageOffres " +
                    "WHERE c.idCandidature = :id", Candidature.class)
                    .setParameter("id", id)
                    .getSingleResult();
            logger.log(Level.INFO, "get successful");
            return instance;
        } catch (jakarta.persistence.NoResultException nre) {
            logger.log(Level.INFO, "get failed - no result", nre);
            return null;
        } catch (RuntimeException re) {
            logger.log(Level.SEVERE, "get failed", re);
            throw re;
        }
    }*/


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

    @Transactional(readOnly = true)
    public List<Candidature> findBySector(Sector sector) {
        logger.log(Level.INFO, "getting Candidature instances with sector: " + sector);
        try {
            List<Candidature> instances = entityManager.createQuery(
                "SELECT c FROM Candidature c JOIN c.sectors s WHERE s = :sector", 
                Candidature.class)
                .setParameter("sector", sector)
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
    public List<Candidature> findByQualificationLevel(QualificationLevel qualificationLevel) {
        logger.log(Level.INFO, "getting Candidature instances with qualification level: " + qualificationLevel);
        try {
            List<Candidature> instances = entityManager.createQuery(
                "SELECT c FROM Candidature c WHERE c.qualificationLevel = :qualificationLevel", 
                Candidature.class)
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
    public List<Candidature> findMatchingOffreEmploi(OffreEmploi offreEmploi) {
        logger.log(Level.INFO, "getting matching Candidature for offreEmploi: " + offreEmploi.getIdOffreEmploi());
        try {
            // Requête pour trouver les candidatures correspondant à une offre
            String jpql = "SELECT DISTINCT c FROM Candidature c " +
                        "JOIN c.sectors cs " +
                        "WHERE cs IN :offreSectors " +
                        "AND c.qualificationLevel = :qualificationLevel";
            
            List<Candidature> candidatures = entityManager.createQuery(jpql, Candidature.class)
                .setParameter("offreSectors", offreEmploi.getSectors())
                .setParameter("qualificationLevel", offreEmploi.getQualificationLevel())
                .getResultList();
            return candidatures;
        } catch (RuntimeException re) {
            logger.log(Level.SEVERE, "get failed", re);
            throw re;
        }
    }

}