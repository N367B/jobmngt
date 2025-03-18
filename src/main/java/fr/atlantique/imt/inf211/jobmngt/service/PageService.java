package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.entity.Candidat;
import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;
import fr.atlantique.imt.inf211.jobmngt.entity.OffreEmploi;

import java.util.List;
import java.util.Map;

public interface PageService {
    
    /**
     * Récupère les statistiques globales pour la page d'accueil
     * @return Map contenant les différentes statistiques
     */
    Map<String, Long> getGlobalStatistics();
    
    /**
     * Récupère les informations spécifiques à l'entreprise connectée
     * @param uid ID de l'entreprise
     * @return L'entreprise avec ses offres d'emploi
     */
    Entreprise getEntrepriseWithOffers(Integer uid);
    
    /**
     * Récupère les informations spécifiques au candidat connecté
     * @param uid ID du candidat
     * @return Le candidat
     */
    Candidat getCandidatInfo(Integer uid);
}