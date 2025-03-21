package fr.atlantique.imt.inf211.jobmngt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import fr.atlantique.imt.inf211.jobmngt.entity.Candidat;
import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;
import fr.atlantique.imt.inf211.jobmngt.service.PageService;
import fr.atlantique.imt.inf211.jobmngt.service.OffreEmploiService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

@Controller
public class PagesController {

    @Autowired
    private PageService pageService;
    
    @Autowired
    private OffreEmploiService offreEmploiService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView welcomePage(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("index");

        // Récupérer les statistiques globales depuis le service
        Map<String, Long> statistics = pageService.getGlobalStatistics();
        
        // Ajouter les statistiques au modèle
        for (Map.Entry<String, Long> entry : statistics.entrySet()) {
            modelAndView.addObject(entry.getKey(), entry.getValue());
        }
        
        // Récupérer les informations de session
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        String userType = (String) session.getAttribute("usertype");
        
        // Affichage spécifique selon le type d'utilisateur
        if (uid != null) {
            if ("entreprise".equals(userType)) {
                Entreprise entreprise = pageService.getEntrepriseWithOffers(uid);
                if (entreprise != null) {
                    //modelAndView.addObject("userCompany", entreprise);
                    modelAndView.addObject("userOffers", offreEmploiService.findByEntreprise(entreprise));
                }
            } else if ("candidat".equals(userType)) {
                Candidat candidat = pageService.getCandidatInfo(uid);
                if (candidat != null) {
                    modelAndView.addObject("userCandidate", candidat);
                }
            }
        }

        return modelAndView;
    }
}