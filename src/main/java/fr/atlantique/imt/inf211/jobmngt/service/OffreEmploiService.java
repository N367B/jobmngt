package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.entity.Candidature;
import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;
import fr.atlantique.imt.inf211.jobmngt.entity.OffreEmploi;
import fr.atlantique.imt.inf211.jobmngt.entity.QualificationLevel;
import fr.atlantique.imt.inf211.jobmngt.entity.Sector;

import java.util.List;

public interface OffreEmploiService {
    List<OffreEmploi> listOffres();
    OffreEmploi getOffreById(int id);
    List<OffreEmploi> searchOffres(String secteur, String qualification);
    List<OffreEmploi> findByEntreprise(Entreprise entreprise);
    List<OffreEmploi> findBySectorAndQualificationLevel(Sector sector, QualificationLevel qualificationLevel);
    long countOffres();
    OffreEmploi saveOffre(OffreEmploi offre);
    boolean deleteOffre(int id);
    List<OffreEmploi> getMatchingOffres(Candidature candidature);
    boolean isMatchingCandidature(OffreEmploi offre, Candidature candidature);
}