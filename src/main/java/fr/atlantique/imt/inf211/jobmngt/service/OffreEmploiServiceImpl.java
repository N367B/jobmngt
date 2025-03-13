package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.dao.OffreEmploiDao;
import fr.atlantique.imt.inf211.jobmngt.dao.QualificationLevelDao;
import fr.atlantique.imt.inf211.jobmngt.dao.SectorDao;
import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;
import fr.atlantique.imt.inf211.jobmngt.entity.OffreEmploi;
import fr.atlantique.imt.inf211.jobmngt.entity.QualificationLevel;
import fr.atlantique.imt.inf211.jobmngt.entity.Sector;
import fr.atlantique.imt.inf211.jobmngt.service.OffreEmploiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class OffreEmploiServiceImpl implements OffreEmploiService {

    @Autowired
    private OffreEmploiDao offreEmploiDao;
    
    @Autowired
    private SectorDao sectorDao;
    
    @Autowired
    private QualificationLevelDao qualificationLevelDao;

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
                return Collections.emptyList();
            }
            
            return offreEmploiDao.findBySectorAndQualificationLevel(sector, qualificationLevel);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
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
}