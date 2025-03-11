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
            // Nous avons besoin d'implémenter la méthode findAll dans CandidatDao
            // Pour l'instant, on peut simuler une méthode à implémenter
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
            candidatDao.persist(candidat);
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
            candidat.getAppUser().setMail(candidatDetails.getAppUser().getMail());
            candidat.getAppUser().setPassword(candidatDetails.getAppUser().getPassword());
            candidat.getAppUser().setCity(candidatDetails.getAppUser().getCity());
            candidat.setFirstName(candidatDetails.getFirstName());
            candidat.setLastName(candidatDetails.getLastName());
            
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
            // Création d'un nouvel utilisateur
            AppUser user = new AppUser();
            user.setMail("candidat@example.com");
            user.setPassword("password123");
            user.setCity("Nantes");
            user.setUserType("candidat");
            
            // Création d'un candidat associé
            Candidat candidat = new Candidat(user, "Jean", "Dupont");
            
            // Persister l'utilisateur d'abord puis le candidat
            appUserDao.persist(user);
            candidatDao.persist(candidat);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(candidat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}