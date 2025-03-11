package fr.atlantique.imt.inf211.jobmngt.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fr.atlantique.imt.inf211.jobmngt.dao.EntrepriseDao;
import fr.atlantique.imt.inf211.jobmngt.dao.OffreEmploiDao;
import fr.atlantique.imt.inf211.jobmngt.dao.QualificationLevelDao;
import fr.atlantique.imt.inf211.jobmngt.dao.SectorDao;
import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;
import fr.atlantique.imt.inf211.jobmngt.entity.OffreEmploi;
import fr.atlantique.imt.inf211.jobmngt.entity.QualificationLevel;
import fr.atlantique.imt.inf211.jobmngt.entity.Sector;

@RestController
@RequestMapping("/api/offres")
public class TestOffreEmploiDaoController {
    
    @Autowired
    private OffreEmploiDao offreEmploiDao;
    
    @Autowired
    private SectorDao sectorDao;
    
    @Autowired
    private QualificationLevelDao qualificationLevelDao;
    
    @Autowired
    private EntrepriseDao entrepriseDao;
    
    /**
     * Liste toutes les offres d'emploi disponibles
     * @return Liste des offres d'emploi
     */
    @GetMapping
    public ResponseEntity<List<OffreEmploi>> getAllOffres() {
        try {
            List<OffreEmploi> offres = offreEmploiDao.findAll();
            return ResponseEntity.ok(offres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Récupère les informations d'une offre d'emploi par son ID
     * @param id Identifiant de l'offre d'emploi
     * @return L'offre d'emploi ou une erreur 404 si non trouvée
     */
    @GetMapping("/{id}")
    public ResponseEntity<OffreEmploi> getOffreById(@PathVariable int id) {
        OffreEmploi offre = offreEmploiDao.findById(id);
        if (offre != null) {
            return ResponseEntity.ok(offre);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Recherche des offres par secteur et niveau de qualification
     * @param idSecteur ID du secteur
     * @param idQualification ID du niveau de qualification
     * @return Liste des offres correspondant aux critères
     */
    @GetMapping("/search")
    public ResponseEntity<List<OffreEmploi>> searchOffres(
            @RequestParam("secteur") int idSecteur,
            @RequestParam("qualification") int idQualification) {
        try {
            Sector sector = sectorDao.findById(idSecteur);
            QualificationLevel qualificationLevel = qualificationLevelDao.findById(idQualification);
            
            if (sector == null || qualificationLevel == null) {
                return ResponseEntity.badRequest().build();
            }
            
            List<OffreEmploi> offres = offreEmploiDao.findBySectorAndQualificationLevel(sector, qualificationLevel);
            return ResponseEntity.ok(offres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Crée une nouvelle offre d'emploi
     * @param offre L'offre d'emploi à créer
     * @return L'offre d'emploi créée avec son ID
     */
    /* 
    @PostMapping
    public ResponseEntity<OffreEmploi> createOffre(@RequestBody OffreEmploi offre) {
        try {
            offreEmploiDao.persist(offre);
            return ResponseEntity.status(HttpStatus.CREATED).body(offre);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }*/
    @PostMapping
    public ResponseEntity<OffreEmploi> createOffre(@RequestBody OffreEmploi offre) {
        try {
            // 1. Gestion de l'entreprise (par ID ou email)
            Entreprise entreprise = null;
            if (offre.getEntreprise() != null) {
                // Recherche par ID
                if (offre.getEntreprise().getIdEntreprise() > 0) {
                    entreprise = entrepriseDao.findById(offre.getEntreprise().getIdEntreprise());
                } 
                // Recherche par mail de l'utilisateur
                else if (offre.getEntreprise().getAppUser() != null && 
                    offre.getEntreprise().getAppUser().getMail() != null && 
                    !offre.getEntreprise().getAppUser().getMail().isEmpty()) {
                    entreprise = entrepriseDao.findByUserMail(offre.getEntreprise().getAppUser().getMail());
                }
            }
            
            if (entreprise == null) {
                return ResponseEntity.badRequest().body(null);
            }
            offre.setEntreprise(entreprise);
            
            // 2. Gestion du niveau de qualification (par ID ou label)
            QualificationLevel qualificationLevel = null;
            if (offre.getQualificationLevel() != null) {
                // Recherche par ID
                if (offre.getQualificationLevel().getIdQualification() > 0) {
                    qualificationLevel = qualificationLevelDao.findById(offre.getQualificationLevel().getIdQualification());
                } 
                // Recherche ou création par label
                else if (offre.getQualificationLevel().getLabelQualification() != null && 
                        !offre.getQualificationLevel().getLabelQualification().isEmpty()) {
                    qualificationLevel = qualificationLevelDao.findByLabel(offre.getQualificationLevel().getLabelQualification());
                    
                    // Création si le niveau de qualification n'existe pas
                    if (qualificationLevel == null) {
                        QualificationLevel newQualLevel = new QualificationLevel();
                        newQualLevel.setLabelQualification(offre.getQualificationLevel().getLabelQualification());
                        qualificationLevelDao.persist(newQualLevel);
                        qualificationLevel = newQualLevel;
                        System.out.println("Nouveau niveau de qualification créé: " + 
                        newQualLevel.getLabelQualification() + " avec ID: " + newQualLevel.getIdQualification());
                    }
                }
            }
            
            if (qualificationLevel == null) {
                return ResponseEntity.badRequest().body(null);
            }
            offre.setQualificationLevel(qualificationLevel);
            
            // 3. Gestion des secteurs (par ID ou label)
            if (offre.getSectors() != null && !offre.getSectors().isEmpty()) {
                Set<Sector> detachedSectors = new HashSet<>(offre.getSectors());
                Set<Sector> managedSectors = new HashSet<>();
                
                for (Sector sector : detachedSectors) {
                    Sector managedSector = null;
                    
                    // Recherche par ID
                    if (sector.getIdSecteur() > 0) {
                        managedSector = sectorDao.findById(sector.getIdSecteur());
                    } 
                    // Recherche ou création par label
                    else if (sector.getLabelSecteur() != null && !sector.getLabelSecteur().isEmpty()) {
                        managedSector = sectorDao.findByLabel(sector.getLabelSecteur());
                        
                        // Création si le secteur n'existe pas
                        if (managedSector == null) {
                            Sector newSector = new Sector();
                            newSector.setLabelSecteur(sector.getLabelSecteur());
                            sectorDao.persist(newSector);
                            managedSector = newSector;
                            System.out.println("Nouveau secteur créé: " + 
                            newSector.getLabelSecteur() + " avec ID: " + newSector.getIdSecteur());
                        }
                    }
                    
                    if (managedSector != null) {
                        managedSectors.add(managedSector);
                    }
                }
                
                offre.setSectors(managedSectors);
            }
            
            // 4. Définir la date si non fournie
            if (offre.getPublicationDate() == null) {
                offre.setPublicationDate(new Date());
            }
            
            // 5. Persister l'offre
            offreEmploiDao.persist(offre);
            return ResponseEntity.status(HttpStatus.CREATED).body(offre);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }    
    
    /**
     * Met à jour une offre d'emploi existante
     * @param id Identifiant de l'offre à modifier
     * @param offreDetails Nouvelles informations de l'offre
     * @return L'offre mise à jour ou une erreur 404 si non trouvée
     */
    @PutMapping("/{id}")
    public ResponseEntity<OffreEmploi> updateOffre(@PathVariable int id, @RequestBody OffreEmploi offreDetails) {
        OffreEmploi offre = offreEmploiDao.findById(id);
        if (offre != null) {
            // Mise à jour des champs
            if (offreDetails.getTitle() != null) {
                offre.setTitle(offreDetails.getTitle());
            }
            if (offreDetails.getTaskDescription() != null) {
                offre.setTaskDescription(offreDetails.getTaskDescription());
            }
            if (offreDetails.getPublicationDate() != null) {
                offre.setPublicationDate(offreDetails.getPublicationDate());
            }
            if (offreDetails.getQualificationLevel() != null) {
                offre.setQualificationLevel(offreDetails.getQualificationLevel());
            }
            if (offreDetails.getSectors() != null && !offreDetails.getSectors().isEmpty()) {
                offre.setSectors(offreDetails.getSectors());
            }
            
            // Sauvegarde des modifications
            offre = offreEmploiDao.merge(offre);
            return ResponseEntity.ok(offre);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Méthode de test pour créer une offre avec des données codées en dur
     * @return L'offre créée
     */
    @PostMapping("/test")
    public ResponseEntity<OffreEmploi> createTestOffre() {
        try {
            // Récupération d'une entreprise
            Entreprise entreprise = entrepriseDao.findById(1); // Par exemple l'entreprise avec l'ID 1
            if (entreprise == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            // Récupération d'un niveau de qualification
            QualificationLevel qualificationLevel = qualificationLevelDao.findById(1); // Par exemple le niveau avec l'ID 1
            if (qualificationLevel == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            // Récupération d'un secteur
            Sector sector = sectorDao.findById(1); // Par exemple le secteur avec l'ID 1
            if (sector == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            // Création de l'offre
            OffreEmploi offre = new OffreEmploi();
            offre.setTitle("Développeur Java Senior");
            offre.setTaskDescription("Développement d'applications web avec Spring Boot");
            offre.setPublicationDate(new Date());
            offre.setEntreprise(entreprise);
            offre.setQualificationLevel(qualificationLevel);
            
            // Ajouter le secteur
            Set<Sector> sectors = new HashSet<>();
            sectors.add(sector);
            offre.setSectors(sectors);
            
            offreEmploiDao.persist(offre);
            return ResponseEntity.status(HttpStatus.CREATED).body(offre);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Supprime une offre d'emploi
     * @param id Identifiant de l'offre à supprimer
     * @return 204 No Content si succès, 404 si non trouvée
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffre(@PathVariable int id) {
        OffreEmploi offre = offreEmploiDao.findById(id);
        if (offre != null) {
            offreEmploiDao.remove(offre);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}