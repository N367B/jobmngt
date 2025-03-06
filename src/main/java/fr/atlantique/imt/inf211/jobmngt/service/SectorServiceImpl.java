package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.dao.SectorDao;
import fr.atlantique.imt.inf211.jobmngt.entity.Sector;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Component
public class SectorServiceImpl implements SectorService {

    @Autowired
    SectorDao sectorDao;

    public List<Sector> listOfSectors() {
        return sectorDao.findAll("id", "ASC");
    }

    @Override
    public long countSectors() {
        return sectorDao.count();
    }

}
