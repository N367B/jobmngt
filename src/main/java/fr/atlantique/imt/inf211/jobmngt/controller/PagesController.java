package fr.atlantique.imt.inf211.jobmngt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import fr.atlantique.imt.inf211.jobmngt.entity.Candidat;
import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;
import fr.atlantique.imt.inf211.jobmngt.entity.OffreEmploi;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidature;
import fr.atlantique.imt.inf211.jobmngt.service.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class PagesController {

    @Autowired
    private EntrepriseService entrepriseService;

    @Autowired
    private CandidatService candidatService;
    
    @Autowired
    private OffreEmploiService offreEmploiService;
    
    @Autowired
    private CandidatureService candidatureService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView welcomePage(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("index");

        // Statistiques globales
        modelAndView.addObject("countCompanies", entrepriseService.countEntreprises());
        modelAndView.addObject("countCandidates", candidatService.countCandidats());
        modelAndView.addObject("countJobs", offreEmploiService.countOffres());
        modelAndView.addObject("countApplications", candidatureService.countCandidatures());
        
        // Récupérer les informations de session
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        String userType = (String) session.getAttribute("usertype");
        
        // Affichage spécifique selon le type d'utilisateur
        if (uid != null) {
            if ("entreprise".equals(userType)) {
                // Si c'est une entreprise, afficher ses offres d'emploi
                Entreprise entreprise = entrepriseService.getEntrepriseById(uid);
                if (entreprise != null) {
                    List<OffreEmploi> offres = offreEmploiService.findByEntreprise(entreprise);
                    modelAndView.addObject("userCompany", entreprise);
                    modelAndView.addObject("userOffers", offres);
                }
            } else if ("candidat".equals(userType)) {
                // Si c'est un candidat, afficher ses candidatures
                Candidat candidat = candidatService.getCandidatById(uid);
                if (candidat != null) {
                    modelAndView.addObject("userCandidate", candidat);
                }
            }
        }

        return modelAndView;
    }
}