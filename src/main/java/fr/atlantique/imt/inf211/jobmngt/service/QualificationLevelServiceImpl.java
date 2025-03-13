package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.entity.*;
import fr.atlantique.imt.inf211.jobmngt.dao.*;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Component
public class QualificationLevelServiceImpl implements QualificationLevelService {

    @Autowired
    QualificationLevelDao qualificationLevelDao;

    public List<QualificationLevel> listOfQualificationLevels() {
        return qualificationLevelDao.findAll("id", "ASC");
    }

    @Override
    public long countQualificationLevel() {
        return qualificationLevelDao.count();
    }



    @Override
    public QualificationLevel getQualificationLevelById(int id) {
        return qualificationLevelDao.findById(id);
    }
    
    @Override
    public QualificationLevel findByLabel(String label) {
        return qualificationLevelDao.findByLabel(label);
    }

    @Override
    public void saveQualificationLevel(QualificationLevel qualificationLevel) {
        if (qualificationLevel.getIdQualification() == 0) {
            qualificationLevelDao.persist(qualificationLevel);
        } else {
            qualificationLevelDao.merge(qualificationLevel);
        }
    }
    
    @Override
    public boolean deleteQualificationLevel(int id) {
        QualificationLevel qualificationLevel = qualificationLevelDao.findById(id);
        if (qualificationLevel != null) {
            qualificationLevelDao.remove(qualificationLevel);
            return true;
        }
        return false;
    }


}
