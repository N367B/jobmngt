package fr.atlantique.imt.inf211.jobmngt.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fr.atlantique.imt.inf211.jobmngt.dao.CandidatDao;
import fr.atlantique.imt.inf211.jobmngt.dao.AppUserDao;
import fr.atlantique.imt.inf211.jobmngt.entity.AppUser;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidat;

@RestController
@RequestMapping("/api/candidats")
public class TestCandidatDaoController {

    @Autowired
    private CandidatDao candidatDao;
    
    @Autowired
    private AppUserDao appUserDao;
    
    /**
     * Liste tous les candidats existants
     * @return Liste des candidats
     */
    @GetMapping
    public ResponseEntity<List<Candidat>> getAllCandidats() {
        try {
            List<Candidat> candidats = candidatDao.findAll();
            return ResponseEntity.ok(candidats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Récupère les informations d'un candidat par son ID
     * @param id Identifiant du candidat
     * @return Le candidat ou une erreur 404 si non trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<Candidat> getCandidatById(@PathVariable int id) {
        Candidat candidat = candidatDao.findById(id);
        if (candidat != null) {
            return ResponseEntity.ok(candidat);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Crée un nouveau candidat
     * @param candidat Le candidat à créer
     * @return Le candidat créé avec son ID
     */
    @PostMapping
    public ResponseEntity<Candidat> createCandidat(@RequestBody Candidat candidat) {
        try {
            // Vérifier si l'utilisateur existe déjà
            if (candidat.getAppUser() != null && candidat.getAppUser().getMail() != null) {
                Optional<AppUser> existingUser = appUserDao.findByMail(candidat.getAppUser().getMail());
                if (existingUser.isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(null);
                }
            }
            
            // Persister l'utilisateur si nouveau
            if (candidat.getAppUser() != null && candidat.getAppUser().getIdUser() == 0) {
                AppUser user = candidat.getAppUser();
                user.setUserType("candidat");
                appUserDao.persist(user);
            }
            
            // Persister le candidat
            candidatDao.persist(candidat);
            
            // Mettre à jour la relation bidirectionnelle
            AppUser user = candidat.getAppUser();
            user.setCandidat(candidat);
            appUserDao.merge(user);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(candidat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Met à jour un candidat existant
     * @param id Identifiant du candidat à modifier
     * @param candidatDetails Nouvelles informations du candidat
     * @return Le candidat mis à jour ou une erreur 404 si non trouvé
     */
    @PutMapping("/{id}")
    public ResponseEntity<Candidat> updateCandidat(@PathVariable int id, @RequestBody Candidat candidatDetails) {
        Candidat candidat = candidatDao.findById(id);
        if (candidat != null) {
            // Mise à jour des champs
            if (candidatDetails.getAppUser() != null) {
                if (candidatDetails.getAppUser().getMail() != null) {
                    candidat.getAppUser().setMail(candidatDetails.getAppUser().getMail());
                }
                if (candidatDetails.getAppUser().getPassword() != null) {
                    candidat.getAppUser().setPassword(candidatDetails.getAppUser().getPassword());
                }
                if (candidatDetails.getAppUser().getCity() != null) {
                    candidat.getAppUser().setCity(candidatDetails.getAppUser().getCity());
                }
            }
            
            if (candidatDetails.getFirstName() != null) {
                candidat.setFirstName(candidatDetails.getFirstName());
            }
            if (candidatDetails.getLastName() != null) {
                candidat.setLastName(candidatDetails.getLastName());
            }
            
            // Sauvegarde des modifications
            candidat = candidatDao.merge(candidat);
            return ResponseEntity.ok(candidat);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Supprime un candidat
     * @param id Identifiant du candidat à supprimer
     * @return 204 No Content si succès, 404 si non trouvé
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidat(@PathVariable int id) {
        Candidat candidat = candidatDao.findById(id);
        if (candidat != null) {
            candidatDao.remove(candidat);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Méthode de test pour créer un candidat avec des données codées en dur
     * @return Le candidat créé
     */
    @PostMapping("/test")
    public ResponseEntity<Candidat> createTestCandidat() {
        try {
            // Recherche d'un utilisateur existant
            Optional<AppUser> userOpt = appUserDao.findByMail("candidat@example.com");
            if (userOpt.isPresent()) {
                System.out.println("User found: " + userOpt.get().getMail());
                // Supprimer l'utilisateur existant
                AppUser user = userOpt.get();
                // Supprimer d'abord le candidat s'il existe
                if (user.getCandidat() != null) {
                    System.out.println("Removing candidat: " + user.getCandidat().getFirstName() + " " + user.getCandidat().getLastName());
                    candidatDao.remove(user.getCandidat());
                }
                System.out.println("Removing user: " + user.getMail());
                appUserDao.remove(user);
            }
            
            // Création d'un nouvel utilisateur
            AppUser newUser = new AppUser();
            newUser.setMail("candidat@example.com");
            newUser.setPassword("password123");
            newUser.setCity("Nantes");
            newUser.setUserType("candidat");
            
            // Persister l'utilisateur
            appUserDao.persist(newUser);
            
            // Créer le candidat associé
            Candidat newCandidat = new Candidat();
            newCandidat.setAppUser(newUser);
            newCandidat.setFirstName("Jean");
            newCandidat.setLastName("Dupont");
            
            // Persister le candidat
            candidatDao.persist(newCandidat);
            
            // Mettre à jour la relation bidirectionnelle
            newUser.setCandidat(newCandidat);
            appUserDao.merge(newUser);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(newCandidat);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}