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
    public Entreprise createEntreprise(Entreprise entreprise) {
        // Vérifie si une entreprise avec le même email ou dénomination existe déjà
        Entreprise existingEntreprises = entrepriseDao.findByUserMail(entreprise.getAppUser().getMail());
        
        if (existingEntreprises !=null) {
            throw new IllegalArgumentException("Une entreprise avec cet email existe déjà.");
        }

        // Persiste la nouvelle entreprise
        entrepriseDao.persist(entreprise);
        return entreprise;
    }


    @Override
    public Entreprise saveEntreprise(Entreprise entreprise) {
        // Vérifier si un utilisateur (candidat OU entreprise) avec cet email existe déjà
        Optional<AppUser> existingUser = appUserDao.findByMail(entreprise.getAppUser().getMail());
        
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé par un autre utilisateur.");
        }
        
        if (entreprise.getIdEntreprise() == 0) {
            entrepriseDao.persist(entreprise);
        } else {
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
}