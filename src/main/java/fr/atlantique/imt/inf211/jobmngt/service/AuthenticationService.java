package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.dao.AppUserDao;
import fr.atlantique.imt.inf211.jobmngt.entity.AppUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private AppUserDao appUserDao;

    /**
     * Authentifie un utilisateur et initialise sa session
     */
    public boolean authenticate(String email, String password, HttpSession session) {
        Optional<AppUser> userOpt = appUserDao.findByMail(email);

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            AppUser user = userOpt.get();
            session.setAttribute("uid", user.getIdUser());
            session.setAttribute("usertype", user.getUserType());
            session.setAttribute("email", user.getMail());

            // Attributs supplémentaires selon le type d'utilisateur
            if ("entreprise".equals(user.getUserType()) && user.getEntreprise() != null) {
                session.setAttribute("userName", user.getEntreprise().getDenomination());
            } else if ("candidat".equals(user.getUserType()) && user.getCandidat() != null) {
                session.setAttribute("userName", user.getCandidat().getFirstName() + " " + user.getCandidat().getLastName());
            }

            return true;
        }
        return false;
    }

    /**
     * Déconnecte l'utilisateur en invalidant sa session
     */
    public void logout(HttpSession session) {
        session.invalidate();
    }

    /**
     * Vérifie si l'utilisateur est authentifié
     */
    public boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("uid") != null;
    }

    /**
     * Vérifie si l'utilisateur est une entreprise
     */
    public boolean isEntreprise(HttpSession session) {
        return "entreprise".equals(session.getAttribute("usertype"));
    }

    /**
     * Vérifie si l'utilisateur est un candidat
     */
    public boolean isCandidat(HttpSession session) {
        return "candidat".equals(session.getAttribute("usertype"));
    }

    /**
     * Vérifie si l'utilisateur est un administrateur
     */
    public boolean isAdmin(HttpSession session) {
        return "admin".equals(session.getAttribute("usertype"));
    }

    /**
     * Vérifie si l'utilisateur est le propriétaire d'une ressource
     */
    public boolean ownsResource(HttpSession session, int resourceOwnerId) {
        Integer uid = (Integer) session.getAttribute("uid");
        return uid != null && (uid == resourceOwnerId || isAdmin(session));
    }
}