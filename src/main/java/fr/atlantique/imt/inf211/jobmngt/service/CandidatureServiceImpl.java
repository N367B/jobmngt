package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.dao.CandidatureDao;
import fr.atlantique.imt.inf211.jobmngt.dao.QualificationLevelDao;
import fr.atlantique.imt.inf211.jobmngt.dao.SectorDao;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidature;
import fr.atlantique.imt.inf211.jobmngt.entity.OffreEmploi;
import fr.atlantique.imt.inf211.jobmngt.entity.QualificationLevel;
import fr.atlantique.imt.inf211.jobmngt.entity.Sector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CandidatureServiceImpl implements CandidatureService {

    @Autowired
    private CandidatureDao candidatureDao;
    
    @Autowired
    private SectorDao sectorDao;
    
    @Autowired
    private QualificationLevelDao qualificationLevelDao;

    @Override
    public List<Candidature> listCandidatures() {
        return candidatureDao.findAll();
    }

    
    @Override
    public Candidature getCandidatureById(int id) {
        return candidatureDao.findById(id);
    }

    /*
    @Override
    public Candidature getCandidatureById(int id) {
        Candidature candidature = candidatureDao.findById(id);
        
        // Force l'initialisation des propriétés lazy loaded
        if (candidature != null) {
            // Initialiser le candidat et ses propriétés
            if (candidature.getCandidat() != null) {
                // Accéder aux propriétés pour forcer l'initialisation
                String firstName = candidature.getCandidat().getFirstName();
                String lastName = candidature.getCandidat().getLastName();
                
                // Force l'initialisation de l'utilisateur associé
                if (candidature.getCandidat().getAppUser() != null) {
                    String city = candidature.getCandidat().getAppUser().getCity();
                }
            }
            
            // Force l'initialisation du niveau de qualification
            if (candidature.getQualificationLevel() != null) {
                String label = candidature.getQualificationLevel().getLabelQualification();
            }
            
            // Force l'initialisation des secteurs
            if (candidature.getSectors() != null && !candidature.getSectors().isEmpty()) {
                candidature.getSectors().size(); // Force l'initialisation de la collection
                for (Sector sector : candidature.getSectors()) {
                    String labelSecteur = sector.getLabelSecteur();
                }
            }
        }
        
        return candidature;
    }*/
    
    @Override
    public long countCandidatures() {
        return candidatureDao.findAll().size();
    }

    @Override
    public Candidature saveCandidature(Candidature candidature) {
        if (candidature.getIdCandidature() == 0) {
            candidatureDao.persist(candidature);
        } else {
            candidature = candidatureDao.merge(candidature);
        }
        return candidature;
    }

    @Override
    public boolean deleteCandidature(int id) {
        Candidature candidature = candidatureDao.findById(id);
        if (candidature != null) {
            candidatureDao.remove(candidature);
            return true;
        }
        return false;
    }
    
    @Override
    public List<Candidature> searchCandidatures(String secteurLabel, String qualificationLabel) {
        // Cas 1: Les deux critères sont renseignés
        if (secteurLabel != null && !secteurLabel.isEmpty() 
                && qualificationLabel != null && !qualificationLabel.isEmpty()) {
            Sector sector = sectorDao.findByLabel(secteurLabel);
            QualificationLevel qualification = qualificationLevelDao.findByLabel(qualificationLabel);
            
            if (sector != null && qualification != null) {
                return candidatureDao.findBySectorAndQualificationLevel(sector, qualification);
            }
        }
        // Cas 2: Uniquement le secteur est renseigné
        else if (secteurLabel != null && !secteurLabel.isEmpty()) {
            Sector sector = sectorDao.findByLabel(secteurLabel);
            if (sector != null) {
                return candidatureDao.findBySector(sector);
            }
        }
        // Cas 3: Uniquement la qualification est renseignée
        else if (qualificationLabel != null && !qualificationLabel.isEmpty()) {
            QualificationLevel qualification = qualificationLevelDao.findByLabel(qualificationLabel);
            if (qualification != null) {
                return candidatureDao.findByQualificationLevel(qualification);
            }
        }
        
        // Si aucun critère valide n'est fourni, retourner la liste complète
        return listCandidatures();
    }

    /*
    @Override
    public List<Candidature> getMatchingCandidatures(OffreEmploi offre) {
        return candidatureDao.findMatchingOffreEmploi(offre);
    }*/
    @Override
    public List<Candidature> getMatchingCandidatures(OffreEmploi offre) {
        // Au lieu d'utiliser candidatureDao.findMatchingOffreEmploi(offre)
        List<Candidature> allCandidatures = candidatureDao.findAll();
        List<Candidature> matchingCandidatures = new ArrayList<>();
        
        for (Candidature candidature : allCandidatures) {
            if (isMatchingOffreEmploi(candidature, offre)) {
                matchingCandidatures.add(candidature);
            }
        }
        
        return matchingCandidatures;
    }

    @Override
    public boolean isMatchingOffreEmploi(Candidature candidature, OffreEmploi offre) {
        // Une candidature correspond à une offre si:
        // 1. Ils ont le même niveau de qualification
        // 2. Ils ont au moins un secteur en commun
        if (candidature.getQualificationLevel().getIdQualification() != offre.getQualificationLevel().getIdQualification()) {
            return false;
        }
        
        // Vérifier s'il y a au moins un secteur en commun
        for (Sector candidatureSector : candidature.getSectors()) {
            for (Sector offreSector : offre.getSectors()) {
                if (candidatureSector.getIdSecteur() == offreSector.getIdSecteur()) {
                    return true;
                }
            }
        }
        
        return false;
    }

    @Override
    public Candidature createCandidatureWithSectors(Candidature candidature, List<Integer> selectedSectorIds) {
        // Mise à jour des secteurs sélectionnés
        Set<Sector> sectors = new HashSet<>();
        for (Integer sectorId : selectedSectorIds) {
            Sector sector = sectorDao.findById(sectorId);
            if (sector != null) {
                sectors.add(sector);
            }
        }
        candidature.setSectors(sectors);
        
        // Définir la date actuelle
        candidature.setAppDate(new Date());
        
        // Générer un nom de CV si nécessaire
        if (candidature.getCv() == null || candidature.getCv().trim().isEmpty()) {
            candidature.setCv(generateCvFilename(candidature));
        }
        
        // Persister et retourner la candidature
        return saveCandidature(candidature);
    }

    @Override
    public Candidature updateCandidatureWithSectors(int id, Candidature formCandidature, List<Integer> selectedSectorIds) {
        // Récupérer la candidature existante
        Candidature existingCandidature = getCandidatureById(id);
        if (existingCandidature == null) {
            throw new IllegalArgumentException("Candidature non trouvée avec l'ID: " + id);
        }
        
        // Mettre à jour les propriétés modifiables
        existingCandidature.setAppDate(new Date());
        existingCandidature.setCv(formCandidature.getCv());
        existingCandidature.setQualificationLevel(formCandidature.getQualificationLevel());
        
        // Mise à jour des secteurs
        Set<Sector> sectors = new HashSet<>();
        for (Integer sectorId : selectedSectorIds) {
            Sector sector = sectorDao.findById(sectorId);
            if (sector != null) {
                sectors.add(sector);
            }
        }
        existingCandidature.setSectors(sectors);
        
        // Persister et retourner la candidature
        return saveCandidature(existingCandidature);
    }

    @Override
    public String generateCvFilename(Candidature candidature) {
        String firstName = candidature.getCandidat().getFirstName();
        String lastName = candidature.getCandidat().getLastName();
        String timestamp = new java.text.SimpleDateFormat("yyyy_MM_dd").format(new Date());
        return "CV_" + firstName + "_" + lastName + "_" + timestamp + ".pdf";
    }
}