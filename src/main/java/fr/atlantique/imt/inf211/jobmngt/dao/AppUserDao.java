package fr.atlantique.imt.inf211.jobmngt.dao;
// Generated Jan 29, 2025, 4:45:04 PM by Hibernate Tools 5.6.15.Final

import fr.atlantique.imt.inf211.jobmngt.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Home object for domain model class AppUser.
 * 
 * @see .AppUser
 * @author Hibernate Tools
 */
@Repository
public class AppUserDao {

    private static final Logger logger = Logger.getLogger(AppUserDao.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void persist(AppUser transientInstance) {
        logger.log(Level.INFO, "persisting AppUser instance");
        try {
            entityManager.persist(transientInstance);
            logger.log(Level.INFO, "persist successful");
        } catch (RuntimeException re) {
            logger.log(Level.SEVERE, "persist failed", re);
            throw re;
        }
    }

    @Transactional
    public void remove(AppUser persistentInstance) {
        logger.log(Level.INFO, "removing AppUser instance");
        try {
            entityManager.remove(persistentInstance);
            logger.log(Level.INFO, "remove successful");
        } catch (RuntimeException re) {
            logger.log(Level.SEVERE, "remove failed", re);
            throw re;
        }
    }

    @Transactional
    public AppUser merge(AppUser detachedInstance) {
        logger.log(Level.INFO, "merging AppUser instance");
        try {
            AppUser result = entityManager.merge(detachedInstance);
            logger.log(Level.INFO, "merge successful");
            return result;
        } catch (RuntimeException re) {
            logger.log(Level.SEVERE, "merge failed", re);
            throw re;
        }
    }

    @Transactional(readOnly = true)
    public AppUser findById(int id) {
        logger.log(Level.INFO, "getting AppUser instance with id: " + id);
        try {
            AppUser instance = entityManager.find(AppUser.class, id);
            logger.log(Level.INFO, "get successful" + instance);
            return instance;
        } catch (RuntimeException re) {
            logger.log(Level.SEVERE, "get failed", re);
            throw re;
        }
    }

    @Transactional(readOnly = true)
    public Long count() {
        String r = "select count(*) from AppUser c";
        TypedQuery<Long> q = entityManager.createQuery(r, Long.class);
        return q.getSingleResult();
    }

    @Transactional(readOnly = true)
    public List<AppUser> findAll(String sort, String order) {
        String r = "SELECT s FROM AppUser s ORDER BY s." + sort;
        if (order.equals("asc")) {
            r += " ASC";
        } else {
            r += " DESC";
        }
        TypedQuery<AppUser> q = entityManager.createQuery(r, AppUser.class);
        return q.getResultList();
    }

    @Transactional(readOnly = true)
    public Optional<AppUser> checkLogin(AppUser user) {
        String r = "SELECT s FROM AppUser s WHERE s.mail = :login AND s.password = :pwd";
        TypedQuery<AppUser> q = entityManager.createQuery(r, AppUser.class);
        q.setParameter("login", user.getMail());
        q.setParameter("pwd", user.getPassword());
        if (q.getResultList().size() == 0) {
            return Optional.empty();
        }
        return Optional.of(q.getResultList().get(0));
    }

    @Transactional(readOnly = true)
    public Optional<AppUser> findByMail(String mail) {
        String r = "SELECT s FROM AppUser s WHERE s.mail = :mail";
        TypedQuery<AppUser> q = entityManager.createQuery(r, AppUser.class);
        q.setParameter("mail", mail);
        List<AppUser> res = q.getResultList();
        if (res.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(res.get(0));
    }
}
