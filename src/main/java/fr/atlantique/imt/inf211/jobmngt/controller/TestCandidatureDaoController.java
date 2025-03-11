package fr.atlantique.imt.inf211.jobmngt.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fr.atlantique.imt.inf211.jobmngt.dao.CandidatureDao;
import fr.atlantique.imt.inf211.jobmngt.dao.CandidatDao;
import fr.atlantique.imt.inf211.jobmngt.dao.QualificationLevelDao;
import fr.atlantique.imt.inf211.jobmngt.dao.SectorDao;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidature;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidat;
import fr.atlantique.imt.inf211.jobmngt.entity.QualificationLevel;
import fr.atlantique.imt.inf211.jobmngt.entity.Sector;

@RestController
@RequestMapping("/api/candidatures")
public class TestCandidatureDaoController {
    
    @Autowired
    private CandidatureDao candidatureDao;
    
    @Autowired
    private CandidatDao candidatDao;
    
    @Autowired
    private QualificationLevelDao qualificationLevelDao;
    
    @Autowired
    private SectorDao sectorDao;
    
    /**
     * Liste toutes les candidatures disponibles
     * @return Liste des candidatures
     */
    @GetMapping
    public ResponseEntity<List<Candidature>> getAllCandidatures() {
        try {
            List<Candidature> candidatures = candidatureDao.findAll();
            return ResponseEntity.ok(candidatures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Récupère les informations d'une candidature par son ID
     * @param id Identifiant de la candidature
     * @return La candidature ou une erreur 404 si non trouvée
     */
    @GetMapping("/{id}")
    public ResponseEntity<Candidature> getCandidatureById(@PathVariable int id) {
        Candidature candidature = candidatureDao.findById(id);
        if (candidature != null) {
            return ResponseEntity.ok(candidature);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Recherche des candidatures par secteur et niveau de qualification
     * @param idSecteur ID du secteur
     * @param idQualification ID du niveau de qualification
     * @return Liste des candidatures correspondant aux critères
     */
    @GetMapping("/search")
    public ResponseEntity<List<Candidature>> searchCandidatures(
            @RequestParam("secteur") int idSecteur,
            @RequestParam("qualification") int idQualification) {
        try {
            Sector sector = sectorDao.findById(idSecteur);
            QualificationLevel qualificationLevel = qualificationLevelDao.findById(idQualification);
            
            if (sector == null || qualificationLevel == null) {
                return ResponseEntity.badRequest().build();
            }
            
            List<Candidature> candidatures = candidatureDao.findBySectorAndQualificationLevel(sector, qualificationLevel);
            return ResponseEntity.ok(candidatures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Crée une nouvelle candidature
     * @param candidature La candidature à créer
     * @return La candidature créée avec son ID
     */
    @PostMapping
    public ResponseEntity<Candidature> createCandidature(@RequestBody Candidature candidature) {
        try {
            // Vérification que le candidat existe
            Candidat candidat = candidature.getCandidat();
            if (candidat == null || candidatDao.findById(candidat.getIdCandidat()) == null) {
                return ResponseEntity.badRequest().body(null);
            }
            
            // Vérification que le niveau de qualification existe
            QualificationLevel qualificationLevel = candidature.getQualificationLevel();
            if (qualificationLevel == null || qualificationLevelDao.findById(qualificationLevel.getIdQualification()) == null) {
                return ResponseEntity.badRequest().body(null);
            }
            
            // Définition de la date de candidature si non fournie
            if (candidature.getAppDate() == null) {
                candidature.setAppDate(new Date());
            }
            
            candidatureDao.persist(candidature);
            return ResponseEntity.status(HttpStatus.CREATED).body(candidature);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Met à jour une candidature existante
     * @param id Identifiant de la candidature à modifier
     * @param candidatureDetails Nouvelles informations de la candidature
     * @return La candidature mise à jour ou une erreur 404 si non trouvée
     */
    @PutMapping("/{id}")
    public ResponseEntity<Candidature> updateCandidature(@PathVariable int id, @RequestBody Candidature candidatureDetails) {
        Candidature candidature = candidatureDao.findById(id);
        if (candidature != null) {
            // Mise à jour des champs
            if (candidatureDetails.getQualificationLevel() != null) {
                candidature.setQualificationLevel(candidatureDetails.getQualificationLevel());
            }
            if (candidatureDetails.getCv() != null) {
                candidature.setCv(candidatureDetails.getCv());
            }
            if (candidatureDetails.getSectors() != null && !candidatureDetails.getSectors().isEmpty()) {
                candidature.setSectors(candidatureDetails.getSectors());
            }
            
            // Sauvegarde des modifications
            candidature = candidatureDao.merge(candidature);
            return ResponseEntity.ok(candidature);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Méthode de test pour créer une candidature avec des données codées en dur
     * @return La candidature créée
     */
    @PostMapping("/test")
    public ResponseEntity<Candidature> createTestCandidature() {
        try {
            // Récupération d'un candidat existant
            Candidat candidat = candidatDao.findById(2); // Par exemple le candidat avec l'ID 2
            if (candidat == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            // Récupération d'un niveau de qualification
            QualificationLevel qualificationLevel = qualificationLevelDao.findById(2); // Par exemple le niveau avec l'ID 2
            if (qualificationLevel == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            // Récupération d'un secteur
            Sector sector = sectorDao.findById(1); // Par exemple le secteur avec l'ID 1
            if (sector == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            // Création de la candidature
            Candidature candidature = new Candidature();
            candidature.setCandidat(candidat);
            candidature.setQualificationLevel(qualificationLevel);
            candidature.setCv("cv_test.pdf");
            candidature.setAppDate(new Date());
            
            // Ajout du secteur
            Set<Sector> sectors = new HashSet<>();
            sectors.add(sector);
            candidature.setSectors(sectors);
            
            candidatureDao.persist(candidature);
            return ResponseEntity.status(HttpStatus.CREATED).body(candidature);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Supprime une candidature
     * @param id Identifiant de la candidature à supprimer
     * @return 204 No Content si succès, 404 si non trouvée
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidature(@PathVariable int id) {
        Candidature candidature = candidatureDao.findById(id);
        if (candidature != null) {
            candidatureDao.remove(candidature);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}