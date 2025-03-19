package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.dao.AppUserDao;
import fr.atlantique.imt.inf211.jobmngt.dao.CandidatDao;
import fr.atlantique.imt.inf211.jobmngt.entity.AppUser;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidat;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CandidatServiceImpl implements CandidatService {

    @Autowired
    private CandidatDao candidatDao;

    @Autowired
    private AppUserDao appUserDao;
    
    @Override
    public List<Candidat> listCandidats() {
        return candidatDao.findAll();
    }

    @Override
    public Candidat getCandidatById(int id) {
        return candidatDao.findById(id);
    }
    
    @Override
    public long countCandidats() {
        return candidatDao.findAll().size();
    }

    @Override
    @Transactional
    public Candidat saveCandidat(Candidat candidat) {
        // Vérifier si un utilisateur avec cet email existe déjà
        Optional<AppUser> existingUser = appUserDao.findByMail(candidat.getAppUser().getMail());
        if (existingUser.isPresent() && existingUser.get().getIdUser() != candidat.getAppUser().getIdUser()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé par un autre utilisateur.");
        }

        if (candidat.getIdCandidat() == 0) {
            // Nouveau candidat, persister directement
            candidatDao.persist(candidat);
        } else {
            // Candidat existant, conserver les informations non modifiées
            Candidat existingCandidat = candidatDao.findById(candidat.getIdCandidat());
            if (existingCandidat != null) {
                AppUser existingAppUser = existingCandidat.getAppUser();

                // Mettre à jour les champs de l'utilisateur associé
                existingAppUser.setMail(candidat.getAppUser().getMail());
                existingAppUser.setPassword(candidat.getAppUser().getPassword());
                existingAppUser.setCity(candidat.getAppUser().getCity());

                // Associer l'utilisateur mis à jour au candidat
                candidat.setAppUser(existingAppUser);
            }

            // Mettre à jour le candidat
            candidat = candidatDao.merge(candidat);
        }
        return candidat;
    }

    @Override
    @Transactional
    public boolean deleteCandidat(int id) {
        // Récupérer le candidat par son ID
        Candidat candidat = candidatDao.findById(id);
        if (candidat != null) {
            // Récupérer l'utilisateur associé
            AppUser appUser = candidat.getAppUser();
            
            // Supprimer le candidat
            candidatDao.remove(candidat);
            
            // Supprimer l'utilisateur associé si nécessaire
            if (appUser != null) {
                appUserDao.remove(appUser);
            }
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Candidat createCandidat(Candidat candidat) throws IllegalArgumentException {
        // Validation du mot de passe
        if (candidat.getAppUser().getPassword() == null || candidat.getAppUser().getPassword().length() < 4) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 4 caractères.");
        }
        
        // Vérification de l'email
        Optional<AppUser> existingUser = appUserDao.findByMail(candidat.getAppUser().getMail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà.");
        }
        
        // Configuration du type d'utilisateur
        candidat.getAppUser().setUserType("candidat");
        
        // Sauvegarde
        return saveCandidat(candidat);
    }

    @Override
    @Transactional
    public Candidat updateCandidat(int id, Candidat candidat) throws IllegalArgumentException {
        Candidat existingCandidat = getCandidatById(id);
        if (existingCandidat == null) {
            throw new IllegalArgumentException("Candidat non trouvé");
        }
        
        // Validation du mot de passe s'il est fourni
        String password = candidat.getAppUser().getPassword();
        if (password != null && !password.isEmpty()) {
            if (password.length() < 4) {
                throw new IllegalArgumentException("Le mot de passe doit contenir au moins 4 caractères.");
            }
        } else {
            // Conserver l'ancien mot de passe
            candidat.getAppUser().setPassword(existingCandidat.getAppUser().getPassword());
        }
        
        // Vérification de l'email
        Optional<AppUser> existingUser = appUserDao.findByMail(candidat.getAppUser().getMail());
        if (existingUser.isPresent() && existingUser.get().getIdUser() != existingCandidat.getAppUser().getIdUser()) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà.");
        }
        
        // Configuration des IDs
        candidat.setIdCandidat(id);
        candidat.getAppUser().setIdUser(existingCandidat.getAppUser().getIdUser());
        candidat.getAppUser().setUserType("candidat");
        
        // Sauvegarde
        return saveCandidat(candidat);
    }

}