package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.entity.QualificationLevel;
import java.util.List;


public interface QualificationLevelService {

    public List<QualificationLevel> listOfQualificationLevels();

    public long countQualificationLevel();
    
}
