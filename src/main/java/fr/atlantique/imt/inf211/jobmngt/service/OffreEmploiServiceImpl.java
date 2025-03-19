package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.dao.OffreEmploiDao;
import fr.atlantique.imt.inf211.jobmngt.dao.QualificationLevelDao;
import fr.atlantique.imt.inf211.jobmngt.dao.SectorDao;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidature;
import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;
import fr.atlantique.imt.inf211.jobmngt.entity.OffreEmploi;
import fr.atlantique.imt.inf211.jobmngt.entity.QualificationLevel;
import fr.atlantique.imt.inf211.jobmngt.entity.Sector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Component
public class OffreEmploiServiceImpl implements OffreEmploiService {

    @Autowired
    private OffreEmploiDao offreEmploiDao;
    
    @Autowired
    private SectorDao sectorDao;
    
    @Autowired
    private QualificationLevelDao qualificationLevelDao;

    @Autowired
    @Lazy
    private MessageService messageService;

    @Override
    public List<OffreEmploi> listOffres() {
        return offreEmploiDao.findAll();
    }

    @Override
    public OffreEmploi getOffreById(int id) {
        return offreEmploiDao.findById(id);
    }

    @Override
    public List<OffreEmploi> searchOffres(String secteurParam, String qualificationParam) {
        try {
            // Cas 1: Les deux critères sont renseignés
            if (secteurParam != null && !secteurParam.isEmpty() 
                    && qualificationParam != null && !qualificationParam.isEmpty()) {
                Sector sector = getSectorFromParam(secteurParam);
                QualificationLevel qualificationLevel = getQualificationFromParam(qualificationParam);
                
                if (sector != null && qualificationLevel != null) {
                    return offreEmploiDao.findBySectorAndQualificationLevel(sector, qualificationLevel);
                }
            }
            // Cas 2: Uniquement le secteur est renseigné
            else if (secteurParam != null && !secteurParam.isEmpty()) {
                Sector sector = getSectorFromParam(secteurParam);
                if (sector != null) {
                    return offreEmploiDao.findBySector(sector);
                }
            }
            // Cas 3: Uniquement la qualification est renseignée
            else if (qualificationParam != null && !qualificationParam.isEmpty()) {
                QualificationLevel qualificationLevel = getQualificationFromParam(qualificationParam);
                if (qualificationLevel != null) {
                    return offreEmploiDao.findByQualificationLevel(qualificationLevel);
                }
            }
            
            // Si aucun critère valide n'est fourni ou erreur, retourner la liste complète
            return listOffres();
        } catch (Exception e) {
            e.printStackTrace();
            return listOffres();
        }
    }
    
    // Méthodes utilitaires pour extraire les entités à partir des paramètres
    private Sector getSectorFromParam(String param) {
        try {
            int id = Integer.parseInt(param);
            return sectorDao.findById(id);
        } catch (NumberFormatException e) {
            return sectorDao.findByLabel(param);
        }
    }
    
    private QualificationLevel getQualificationFromParam(String param) {
        try {
            int id = Integer.parseInt(param);
            return qualificationLevelDao.findById(id);
        } catch (NumberFormatException e) {
            return qualificationLevelDao.findByLabel(param);
        }
    }

    @Override
    public List<OffreEmploi> findByEntreprise(Entreprise entreprise) {
        return offreEmploiDao.findByEntreprise(entreprise);
    }

    @Override
    public List<OffreEmploi> findBySectorAndQualificationLevel(Sector sector, QualificationLevel qualificationLevel) {
        return offreEmploiDao.findBySectorAndQualificationLevel(sector, qualificationLevel);
    }

    @Override
    public long countOffres() {
        return offreEmploiDao.findAll().size();
    }

    @Override
    public OffreEmploi saveOffre(OffreEmploi offre) {
        // Définir la date si non fournie
        if (offre.getPublicationDate() == null) {
            offre.setPublicationDate(new Date());
        }
        
        if (offre.getIdOffreEmploi() == 0) {
            offreEmploiDao.persist(offre);
        } else {
            offre = offreEmploiDao.merge(offre);
        }
        return offre;
    }

    @Override
    public boolean deleteOffre(int id) {
        OffreEmploi offre = offreEmploiDao.findById(id);
        if (offre != null) {
            offreEmploiDao.remove(offre);
            return true;
        }
        return false;
    }

    @Override
    public List<OffreEmploi> getMatchingOffres(Candidature candidature) {
        // Au lieu d'utiliser offreEmploiDao.findMatchingCandidature(candidature)
        List<OffreEmploi> allOffres = offreEmploiDao.findAll();
        List<OffreEmploi> matchingOffres = new ArrayList<>();
        
        for (OffreEmploi offre : allOffres) {
            if (isMatchingCandidature(offre, candidature)) {
                matchingOffres.add(offre);
            }
        }
        
        return matchingOffres;
    }

    @Override
    public boolean isMatchingCandidature(OffreEmploi offre, Candidature candidature) {
        // Une offre correspond à une candidature si:
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
    public OffreEmploi createOffreWithSectors(OffreEmploi offre, List<Integer> selectedSectorIds) {
        // Gérer les secteurs sélectionnés
        if (selectedSectorIds != null && !selectedSectorIds.isEmpty()) {
            HashSet<Sector> sectors = new HashSet<>();
            for (Integer sectorId : selectedSectorIds) {
                Sector sector = sectorDao.findById(sectorId);
                if (sector != null) {
                    sectors.add(sector);
                }
            }
            offre.setSectors(sectors);
        }
        
        // Sauvegarder l'offre
        return saveOffre(offre);
    }

    @Override
    public OffreEmploi updateOffreWithSectors(int id, OffreEmploi offre, List<Integer> selectedSectorIds) {
        OffreEmploi existingOffre = getOffreById(id);
        if (existingOffre == null) {
            throw new IllegalArgumentException("Offre non trouvée avec l'ID: " + id);
        }
        
        // Préserver l'ID et l'entreprise
        offre.setIdOffreEmploi(id);
        offre.setEntreprise(existingOffre.getEntreprise());
        
        // Gérer les secteurs sélectionnés
        if (selectedSectorIds != null && !selectedSectorIds.isEmpty()) {
            HashSet<Sector> sectors = new HashSet<>();
            for (Integer sectorId : selectedSectorIds) {
                Sector sector = sectorDao.findById(sectorId);
                if (sector != null) {
                    sectors.add(sector);
                }
            }
            offre.setSectors(sectors);
        }
        
        // Sauvegarder l'offre
        return saveOffre(offre);
    }

    @Override
    public int sendNotificationsForJob(OffreEmploi offre, String message) {
        return messageService.sendNotificationsForJob(offre, message);
    }

}