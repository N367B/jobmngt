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

    @Transactional
    public Entreprise persistWithUser(Entreprise entreprise) {
        logger.log(Level.INFO, "Persisting Entreprise with its AppUser");
        try {
            // 1. Vérifier si l'AppUser existe déjà et a un ID
            AppUser appUser = entreprise.getAppUser();
            if (appUser == null) {
                throw new RuntimeException("L'entreprise doit avoir un utilisateur associé");
            }
            
            if (appUser.getIdUser() == 0) {
                throw new RuntimeException("L'utilisateur doit être persisté avant l'entreprise");
            }
            
            // 2. Définir explicitement l'ID de l'entreprise comme étant l'ID de l'utilisateur
            entreprise.setIdEntreprise(appUser.getIdUser());
            
            // 3. Persister l'entreprise avec persist (pas merge)
            entityManager.persist(entreprise);
            
            // 4. Mettre à jour la référence bidirectionnelle
            appUser.setEntreprise(entreprise);
            entityManager.merge(appUser);
            
            logger.log(Level.INFO, "Entreprise persistée avec succès");
            return entreprise;
        } catch (RuntimeException re) {
            logger.log(Level.SEVERE, "Échec de la persistance de l'Entreprise", re);
            throw re;
        }
    }
}

