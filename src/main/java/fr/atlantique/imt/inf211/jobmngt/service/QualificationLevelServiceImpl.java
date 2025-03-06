package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.dao.QualificationLevelDao;
import fr.atlantique.imt.inf211.jobmngt.entity.QualificationLevel;

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

}
