package fr.atlantique.imt.inf211.jobmngt.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fr.atlantique.imt.inf211.jobmngt.dao.EntrepriseDao;
import fr.atlantique.imt.inf211.jobmngt.dao.AppUserDao;
import fr.atlantique.imt.inf211.jobmngt.entity.AppUser;
import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;

@RestController
@RequestMapping("/api/entreprises")
public class TestEntrepriseDaoController {

    @Autowired
    private EntrepriseDao entrepriseDao;
    
    @Autowired
    private AppUserDao appUserDao;
    
    /**
     * Liste toutes les entreprises existantes
     * @return Liste des entreprises
     */
    @GetMapping
    public ResponseEntity<List<Entreprise>> getAllEntreprises() {
        List<Entreprise> entreprises = entrepriseDao.findAll("denomination", "asc");
        return ResponseEntity.ok(entreprises);
    }
    
    /**
     * Récupère les informations d'une entreprise par son ID
     * @param id Identifiant de l'entreprise
     * @return L'entreprise ou une erreur 404 si non trouvée
     */
    @GetMapping("/{id}")
    public ResponseEntity<Entreprise> getEntrepriseById(@PathVariable int id) {
        Entreprise entreprise = entrepriseDao.findById(id);
        if (entreprise != null) {
            return ResponseEntity.ok(entreprise);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Crée une nouvelle entreprise
     * @param entreprise L'entreprise à créer
     * @return L'entreprise créée avec son ID
     */
    @PostMapping
    public ResponseEntity<Entreprise> createEntreprise(@RequestBody Entreprise entreprise) {
        try {
            entrepriseDao.persist(entreprise);
            return ResponseEntity.status(HttpStatus.CREATED).body(entreprise);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Met à jour une entreprise existante
     * @param id Identifiant de l'entreprise à modifier
     * @param entrepriseDetails Nouvelles informations de l'entreprise
     * @return L'entreprise mise à jour ou une erreur 404 si non trouvée
     */
    @PutMapping("/{id}")
    public ResponseEntity<Entreprise> updateEntreprise(@PathVariable int id, @RequestBody Entreprise entrepriseDetails) {
        Entreprise entreprise = entrepriseDao.findById(id);
        if (entreprise != null) {
            // Mise à jour des champs
            entreprise.getAppUser().setMail(entrepriseDetails.getAppUser().getMail());
            entreprise.getAppUser().setPassword(entrepriseDetails.getAppUser().getPassword());
            if (entrepriseDetails.getDenomination() != null) {
                entreprise.setDenomination(entrepriseDetails.getDenomination());
            }
            if (entrepriseDetails.getDescription() != null) {
                entreprise.setDescription(entrepriseDetails.getDescription());
            }
            
            // Sauvegarde des modifications
            entreprise = entrepriseDao.merge(entreprise);
            return ResponseEntity.ok(entreprise);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Supprime une entreprise
     * @param id Identifiant de l'entreprise à supprimer
     * @return 204 No Content si succès, 404 si non trouvée
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntreprise(@PathVariable int id) {
        Entreprise entreprise = entrepriseDao.findById(id);
        AppUser appUser = entreprise.getAppUser();
        if (entreprise != null && entreprise.getAppUser() != null) {
            entrepriseDao.remove(entreprise);
            // Supprimer AppUser associé
            appUserDao.remove(appUser);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/test")
    public ResponseEntity<Entreprise> createTestEntreprise() {
        try {
            // Recherche d'un utilisateur existant
            Optional<AppUser> userOpt = appUserDao.findByMail("admin@example.com");
            if (userOpt.isPresent()) {
                System.out.println("User found: " + userOpt.get().getMail());
                // Supprimer l'utilisateur existant
                AppUser user = userOpt.get();
                // Supprimer d'abord l'entreprise si elle existe
                if (user.getEntreprise() != null) {
                    System.out.println("Removing entreprise: " + user.getEntreprise().getDenomination());
                    entrepriseDao.remove(user.getEntreprise());
                }
                System.out.println("Removing user: " + user.getMail());
                appUserDao.remove(user);
            }
            
            // Créer d'abord un utilisateur
            AppUser newUser = new AppUser();
            newUser.setMail("admin@example.com");
            newUser.setPassword("password123");
            newUser.setCity("Brest");
            newUser.setUserType("entreprise");
            
            // Persister l'utilisateur
            appUserDao.persist(newUser);
            
            // Créer l'entreprise associée
            Entreprise newEntreprise = new Entreprise();
            newEntreprise.setAppUser(newUser);
            newEntreprise.setDenomination("Entreprise Test");
            newEntreprise.setDescription("Description de test pour l'entreprise");
            
            // Utiliser directement la méthode standard qui fonctionne maintenant
            entrepriseDao.persist(newEntreprise);
            
            // Mettre à jour la relation bidirectionnelle
            newUser.setEntreprise(newEntreprise);
            appUserDao.merge(newUser);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(newEntreprise);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}