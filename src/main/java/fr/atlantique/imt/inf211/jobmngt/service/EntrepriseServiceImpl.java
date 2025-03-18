package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.dao.AppUserDao;
import fr.atlantique.imt.inf211.jobmngt.dao.EntrepriseDao;
import fr.atlantique.imt.inf211.jobmngt.entity.AppUser;
import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;
import fr.atlantique.imt.inf211.jobmngt.service.EntrepriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EntrepriseServiceImpl implements EntrepriseService {

    @Autowired
    private EntrepriseDao entrepriseDao;

    @Autowired
    private AppUserDao appUserDao;

    @Override
    public List<Entreprise> listEntreprises() {
        return entrepriseDao.findAll("denomination", "ASC");
    }

    @Override
    public Entreprise getEntrepriseById(int id) {
        return entrepriseDao.findById(id);
    }
    
    @Override
    public long countEntreprises() {
        return entrepriseDao.findAll("denomination", "asc").size();
    }



    @Override
    public Entreprise saveEntreprise(Entreprise entreprise) {
    // Vérifier si un utilisateur avec cet email existe déjà
    Optional<AppUser> existingUser = appUserDao.findByMail(entreprise.getAppUser().getMail());
    if (existingUser.isPresent() && existingUser.get().getIdUser() != entreprise.getAppUser().getIdUser()) {
        throw new IllegalArgumentException("Cet email est déjà utilisé par un autre utilisateur.");
    }

    if (entreprise.getIdEntreprise() == 0) {
        // Nouvelle entreprise, persister directement
        entrepriseDao.persist(entreprise);
    } else {
        // Entreprise existante, conserver les informations non modifiées
        Entreprise existingEntreprise = entrepriseDao.findById(entreprise.getIdEntreprise());
        if (existingEntreprise != null) {
            AppUser existingAppUser = existingEntreprise.getAppUser();

            // Mettre à jour les champs de l'utilisateur associé
            existingAppUser.setMail(entreprise.getAppUser().getMail());
            existingAppUser.setPassword(entreprise.getAppUser().getPassword());
            existingAppUser.setCity(entreprise.getAppUser().getCity());

            // Associer l'utilisateur mis à jour à l'entreprise
            entreprise.setAppUser(existingAppUser);
        }

        // Mettre à jour l'entreprise
        entreprise = entrepriseDao.merge(entreprise);
    }
    return entreprise;
    }

    @Override
    public boolean deleteEntreprise(int id) {
    // Récupérer l'entreprise par son ID
    Entreprise entreprise = entrepriseDao.findById(id);
    if (entreprise != null) {
        // Récupérer l'utilisateur associé
        AppUser appUser = entreprise.getAppUser();
        
        // Supprimer l'entreprise
        entrepriseDao.remove(entreprise);
        
        // Supprimer l'utilisateur associé si nécessaire
        if (appUser != null) {
            appUserDao.remove(appUser);
        }
        return true;
    }
    return false;
    }

    @Override
    public Entreprise createEntreprise(Entreprise entreprise) throws IllegalArgumentException {
        // Validation du mot de passe
        if (entreprise.getAppUser().getPassword() == null || entreprise.getAppUser().getPassword().length() < 4) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 4 caractères.");
        }
        
        // Vérification de l'email
        Optional<AppUser> existingUser = appUserDao.findByMail(entreprise.getAppUser().getMail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà.");
        }
        
        // Configuration de l'entreprise
        entreprise.getAppUser().setUserType("entreprise");
        
        // Sauvegarde
        return saveEntreprise(entreprise);
    }

    @Override
    public Entreprise updateEntreprise(int id, Entreprise updatedEntreprise) throws IllegalArgumentException {
        Entreprise existingEntreprise = getEntrepriseById(id);
        if (existingEntreprise == null) {
            throw new IllegalArgumentException("Entreprise non trouvée");
        }
        
        // Validation du mot de passe s'il est fourni
        String password = updatedEntreprise.getAppUser().getPassword();
        if (password != null && !password.isEmpty()) {
            if (password.length() < 4) {
                throw new IllegalArgumentException("Le mot de passe doit contenir au moins 4 caractères.");
            }
        } else {
            // Conserver l'ancien mot de passe
            updatedEntreprise.getAppUser().setPassword(existingEntreprise.getAppUser().getPassword());
        }
        
        // Vérification de l'email
        Optional<AppUser> existingUser = appUserDao.findByMail(updatedEntreprise.getAppUser().getMail());
        if (existingUser.isPresent() && existingUser.get().getIdUser() != existingEntreprise.getAppUser().getIdUser()) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà.");
        }
        
        // Configuration des IDs
        updatedEntreprise.setIdEntreprise(id);
        updatedEntreprise.getAppUser().setIdUser(existingEntreprise.getAppUser().getIdUser());
        updatedEntreprise.getAppUser().setUserType("entreprise");
        
        // Sauvegarde
        return saveEntreprise(updatedEntreprise);
    }
}