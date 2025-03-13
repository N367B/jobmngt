package fr.atlantique.imt.inf211.jobmngt.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fr.atlantique.imt.inf211.jobmngt.dao.CandidatureDao;
import fr.atlantique.imt.inf211.jobmngt.dao.AppUserDao;
import fr.atlantique.imt.inf211.jobmngt.dao.CandidatDao;
import fr.atlantique.imt.inf211.jobmngt.dao.QualificationLevelDao;
import fr.atlantique.imt.inf211.jobmngt.dao.SectorDao;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidature;
import fr.atlantique.imt.inf211.jobmngt.entity.AppUser;
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

    @Autowired
    private AppUserDao appUserDao;
    
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
            e.printStackTrace();
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
     * @param secteurParam ID ou label du secteur
     * @param qualificationParam ID ou label du niveau de qualification
     * @return Liste des candidatures correspondant aux critères
     */
    @GetMapping("/search")
    public ResponseEntity<List<Candidature>> searchCandidatures(
            @RequestParam("secteur") String secteurParam,
            @RequestParam("qualification") String qualificationParam) {
        try {
            Sector sector = null;
            QualificationLevel qualificationLevel = null;
            
            // Tenter de parser comme des IDs
            try {
                int idSecteur = Integer.parseInt(secteurParam);
                sector = sectorDao.findById(idSecteur);
            } catch (NumberFormatException e) {
                // Ce n'est pas un ID, on cherche par label
                sector = sectorDao.findByLabel(secteurParam);
            }
            
            try {
                int idQualification = Integer.parseInt(qualificationParam);
                qualificationLevel = qualificationLevelDao.findById(idQualification);
            } catch (NumberFormatException e) {
                // Ce n'est pas un ID, on cherche par label
                qualificationLevel = qualificationLevelDao.findByLabel(qualificationParam);
            }
            
            if (sector == null || qualificationLevel == null) {
                return ResponseEntity.badRequest().build();
            }
            
            List<Candidature> candidatures = candidatureDao.findBySectorAndQualificationLevel(sector, qualificationLevel);
            return ResponseEntity.ok(candidatures);
        } catch (Exception e) {
            e.printStackTrace();
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
        // Gestion du candidat
        Candidat candidat = null;
        if (candidature.getCandidat() != null) {
            // Recherche par ID
            if (candidature.getCandidat().getIdCandidat() > 0) {
                candidat = candidatDao.findById(candidature.getCandidat().getIdCandidat());
            } 
            // Recherche par mail de l'utilisateur
            else if (candidature.getCandidat().getAppUser() != null && 
                candidature.getCandidat().getAppUser().getMail() != null && 
                !candidature.getCandidat().getAppUser().getMail().isEmpty()) {
                // Il faut d'abord récupérer l'AppUser correspondant au mail
                Optional<AppUser> appUser = appUserDao.findByMail(
                    candidature.getCandidat().getAppUser().getMail());
                if (appUser.isPresent() && appUser.get().getCandidat() != null) {
                    candidat = candidatDao.findById(appUser.get().getCandidat().getIdCandidat());
                }
            }
        }
        
        if (candidat == null) {
            return ResponseEntity.badRequest().body(null);
        }
        candidature.setCandidat(candidat);

        // Gestion du CV - Si non fourni, on génère un nom automatiquement
        if (candidature.getCv() == null || candidature.getCv().trim().isEmpty()) {
            String firstName = candidat.getFirstName() != null ? candidat.getFirstName() : "inconnu";
            String lastName = candidat.getLastName() != null ? candidat.getLastName() : "inconnu";
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd").format(new Date());
            candidature.setCv("CV_" + firstName + "_" + lastName + "_" + timestamp + ".pdf");
        }
            
        // 2. Gestion du niveau de qualification
        QualificationLevel qualificationLevel = null;
        if (candidature.getQualificationLevel() != null) {
            // Recherche par ID
            if (candidature.getQualificationLevel().getIdQualification() > 0) {
                qualificationLevel = qualificationLevelDao.findById(candidature.getQualificationLevel().getIdQualification());
            } 
            // Recherche ou création par label
            else if (candidature.getQualificationLevel().getLabelQualification() != null && 
                    !candidature.getQualificationLevel().getLabelQualification().isEmpty()) {
                qualificationLevel = qualificationLevelDao.findByLabel(candidature.getQualificationLevel().getLabelQualification());
                
                // Création si le niveau n'existe pas
                if (qualificationLevel == null) {
                    QualificationLevel newQualLevel = new QualificationLevel();
                    newQualLevel.setLabelQualification(candidature.getQualificationLevel().getLabelQualification());
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
        candidature.setQualificationLevel(qualificationLevel);
        
        // Gestion des secteurs
        if (candidature.getSectors() != null && !candidature.getSectors().isEmpty()) {
            Set<Sector> detachedSectors = new HashSet<>(candidature.getSectors());
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
            
            candidature.setSectors(managedSectors);
        }
        
        // Définir la date si non fournie
        if (candidature.getAppDate() == null) {
            candidature.setAppDate(new Date());
        }
        
        // Persister la candidature
        candidatureDao.persist(candidature);
        return ResponseEntity.status(HttpStatus.CREATED).body(candidature);
    } catch (Exception e) {
        e.printStackTrace();
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
            // Mise à jour du candidat
            if (candidatureDetails.getCandidat() != null && candidatureDetails.getCandidat().getIdCandidat() > 0) {
                Candidat candidat = candidatDao.findById(candidatureDetails.getCandidat().getIdCandidat());
                if (candidat != null) {
                    candidature.setCandidat(candidat);
                }
            }
            
            // Mise à jour du niveau de qualification
            if (candidatureDetails.getQualificationLevel() != null) {
                if (candidatureDetails.getQualificationLevel().getIdQualification() > 0) {
                    QualificationLevel qualificationLevel = qualificationLevelDao.findById(
                        candidatureDetails.getQualificationLevel().getIdQualification());
                    if (qualificationLevel != null) {
                        candidature.setQualificationLevel(qualificationLevel);
                    }
                } else if (candidatureDetails.getQualificationLevel().getLabelQualification() != null) {
                    QualificationLevel qualificationLevel = qualificationLevelDao.findByLabel(
                        candidatureDetails.getQualificationLevel().getLabelQualification());
                    if (qualificationLevel != null) {
                        candidature.setQualificationLevel(qualificationLevel);
                    }
                }
            }
            
            // Mise à jour du CV
            if (candidatureDetails.getCv() != null) {
                candidature.setCv(candidatureDetails.getCv());
            }
            
            // Mise à jour de la date
            if (candidatureDetails.getAppDate() != null) {
                candidature.setAppDate(candidatureDetails.getAppDate());
            }
            
            // Mise à jour des secteurs
            if (candidatureDetails.getSectors() != null && !candidatureDetails.getSectors().isEmpty()) {
                Set<Sector> detachedSectors = new HashSet<>(candidatureDetails.getSectors());
                Set<Sector> managedSectors = new HashSet<>();
                
                for (Sector sector : detachedSectors) {
                    Sector managedSector = null;
                    
                    if (sector.getIdSecteur() > 0) {
                        managedSector = sectorDao.findById(sector.getIdSecteur());
                    } else if (sector.getLabelSecteur() != null && !sector.getLabelSecteur().isEmpty()) {
                        managedSector = sectorDao.findByLabel(sector.getLabelSecteur());
                        
                        if (managedSector == null) {
                            Sector newSector = new Sector();
                            newSector.setLabelSecteur(sector.getLabelSecteur());
                            sectorDao.persist(newSector);
                            managedSector = newSector;
                        }
                    }
                    
                    if (managedSector != null) {
                        managedSectors.add(managedSector);
                    }
                }
                
                candidature.setSectors(managedSectors);
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
            e.printStackTrace();
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