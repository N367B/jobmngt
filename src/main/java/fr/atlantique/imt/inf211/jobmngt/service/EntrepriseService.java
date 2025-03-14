package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;
import java.util.List;

public interface EntrepriseService {
    List<Entreprise> listEntreprises();
    Entreprise getEntrepriseById(int id);
    long countEntreprises();
    Entreprise saveEntreprise(Entreprise entreprise);
    boolean deleteEntreprise(int id);
    Entreprise createEntreprise(Entreprise entreprise);
}