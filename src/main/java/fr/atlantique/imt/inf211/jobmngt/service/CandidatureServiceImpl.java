package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.dao.CandidatureDao;
import fr.atlantique.imt.inf211.jobmngt.dao.QualificationLevelDao;
import fr.atlantique.imt.inf211.jobmngt.dao.SectorDao;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidature;
import fr.atlantique.imt.inf211.jobmngt.entity.QualificationLevel;
import fr.atlantique.imt.inf211.jobmngt.entity.Sector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
}