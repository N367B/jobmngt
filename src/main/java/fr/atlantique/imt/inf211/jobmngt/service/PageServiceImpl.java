package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.entity.Candidat;
import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private EntrepriseService entrepriseService;

    @Autowired
    private CandidatService candidatService;
    
    @Autowired
    private OffreEmploiService offreEmploiService;
    
    @Autowired
    private CandidatureService candidatureService;
    
    @Override
    public Map<String, Long> getGlobalStatistics() {
        Map<String, Long> statistics = new HashMap<>();
        
        statistics.put("countCompanies", entrepriseService.countEntreprises());
        statistics.put("countCandidates", candidatService.countCandidats());
        statistics.put("countJobs", offreEmploiService.countOffres());
        statistics.put("countApplications", candidatureService.countCandidatures());
        
        return statistics;
    }
    
    @Override
    public Entreprise getEntrepriseWithOffers(Integer uid) {
        Entreprise entreprise = entrepriseService.getEntrepriseById(uid);
        // Les offres sont déjà accessibles via entreprise.getOffreEmplois() grâce à la relation JPA
        return entreprise;
    }
    
    @Override
    public Candidat getCandidatInfo(Integer uid) {
        return candidatService.getCandidatById(uid);
    }
}