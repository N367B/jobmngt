package fr.atlantique.imt.inf211.jobmngt.service;

import java.util.List;

import fr.atlantique.imt.inf211.jobmngt.entity.*;


public interface SectorService {

    public List<Sector> listOfSectors();

    public long countSectors();

    public Sector getSectorById(int id);
    public Sector findByLabel(String label);
    public void saveSector(Sector sector);
    public boolean deleteSector(int id);
    
}
