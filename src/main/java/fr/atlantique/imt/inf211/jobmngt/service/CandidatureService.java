package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.entity.Candidature;
import fr.atlantique.imt.inf211.jobmngt.entity.OffreEmploi;

import java.util.List;

public interface CandidatureService {
    
    List<Candidature> listCandidatures();
    
    Candidature getCandidatureById(int id);
    
    long countCandidatures();
    
    Candidature saveCandidature(Candidature candidature);
    
    boolean deleteCandidature(int id);
    
    List<Candidature> searchCandidatures(String secteur, String qualification);

    List<Candidature> getMatchingCandidatures(OffreEmploi offre);
    boolean isMatchingOffreEmploi(Candidature candidature, OffreEmploi offre);


}