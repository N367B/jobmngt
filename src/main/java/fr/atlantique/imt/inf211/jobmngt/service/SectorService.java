package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.entity.Sector;
import java.util.List;


public interface SectorService {

    public List<Sector> listOfSectors();

    public long countSectors();
    
}
