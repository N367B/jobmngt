package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.entity.*;
import jakarta.transaction.Transactional;
import fr.atlantique.imt.inf211.jobmngt.dao.*;
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


    @Override
    public Sector getSectorById(int id) {
        return sectorDao.findById(id);
    }
    
    @Override
    public Sector findByLabel(String label) {
        return sectorDao.findByLabel(label);
    }

    @Override
    @Transactional
    public void saveSector(Sector sector) {
        if (sector.getIdSecteur() == 0) {
            sectorDao.persist(sector);
        } else {
            sectorDao.merge(sector);
        }
    }
    
    @Override
    @Transactional
    public boolean deleteSector(int id) {
        Sector sector = sectorDao.findById(id);
        if (sector != null) {
            sectorDao.remove(sector);
            return true;
        }
        return false;
    }



}
