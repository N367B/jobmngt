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
        Sector sector = sectorDao.findByLabel(secteurLabel);
        QualificationLevel qualification = qualificationLevelDao.findByLabel(qualificationLabel);
        
        if (sector != null && qualification != null) {
            return candidatureDao.findBySectorAndQualificationLevel(sector, qualification);
        }
        return listCandidatures();
    }
}