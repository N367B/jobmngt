package fr.atlantique.imt.inf211.jobmngt.service;

import java.util.List;

import fr.atlantique.imt.inf211.jobmngt.entity.*;


public interface QualificationLevelService {

    public List<QualificationLevel> listOfQualificationLevels();

    public long countQualificationLevel();

    public QualificationLevel getQualificationLevelById(int id);
    public QualificationLevel findByLabel(String label);
    public void saveQualificationLevel(QualificationLevel qualificationLevel);
    public boolean deleteQualificationLevel(int id);

    
}
