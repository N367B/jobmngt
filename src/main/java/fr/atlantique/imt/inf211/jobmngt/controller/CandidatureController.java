package fr.atlantique.imt.inf211.jobmngt.controller;

import fr.atlantique.imt.inf211.jobmngt.dto.candidature.CandidatureDTO;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidat;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidature;
import fr.atlantique.imt.inf211.jobmngt.entity.OffreEmploi;
import fr.atlantique.imt.inf211.jobmngt.entity.QualificationLevel;
import fr.atlantique.imt.inf211.jobmngt.entity.Sector;
import fr.atlantique.imt.inf211.jobmngt.service.CandidatService;
import fr.atlantique.imt.inf211.jobmngt.service.CandidatureService;
import fr.atlantique.imt.inf211.jobmngt.service.MessageService;
import fr.atlantique.imt.inf211.jobmngt.service.OffreEmploiService;
import fr.atlantique.imt.inf211.jobmngt.service.AuthenticationService;
import fr.atlantique.imt.inf211.jobmngt.service.QualificationLevelService;
import fr.atlantique.imt.inf211.jobmngt.service.SectorService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/applications")
public class CandidatureController {

    @Autowired
    private CandidatureService candidatureService;
    
    @Autowired
    private CandidatService candidatService;
    
    @Autowired
    private OffreEmploiService offreEmploiService;
    
    @Autowired
    private QualificationLevelService qualificationLevelService;
    
    @Autowired
    private SectorService sectorService;

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private MessageService messageService;
    
    @GetMapping
    public ModelAndView listApplications() {
        ModelAndView modelAndView = new ModelAndView("application/applicationList");
        
        List<Candidature> applications = candidatureService.listCandidatures();
        modelAndView.addObject("applicationslist", applications);
        
        modelAndView.addObject("sectors", sectorService.listOfSectors());
        modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
        
        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView viewApplication(@PathVariable int id) {
        ModelAndView modelAndView = new ModelAndView("application/applicationView");
        Candidature application = candidatureService.getCandidatureById(id);
        
        if (application == null) {
            return new ModelAndView("redirect:/applications");
        }
        
        // Charger explicitement les relations pour éviter les problèmes de lazy loading
        application.getCandidat().getAppUser(); // Force le chargement
        application.getQualificationLevel(); // Force le chargement des qualifications
        application.getSectors(); // Force le chargement des secteurs
        
        modelAndView.addObject("app", application);
        
        return modelAndView;
    }

    @GetMapping("/create")
    public ModelAndView createApplicationForm(@RequestParam(required = false) Integer jobId, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        String userType = (String) session.getAttribute("usertype");
        
        // Vérification que l'utilisateur est connecté et est un candidat
        if (uid == null || !"candidat".equals(userType)) {
            return new ModelAndView("redirect:/error/403");
        }
        
        ModelAndView modelAndView = new ModelAndView("application/applicationForm");
        Candidature application = new Candidature();
        
        // Récupérer le candidat lié à l'utilisateur connecté
        Candidat candidat = candidatService.getCandidatById(uid);
        
        // Vérifier que le candidat existe
        if (candidat == null) {
            return new ModelAndView("redirect:/error").addObject("message", 
                "Votre profil candidat est introuvable. Veuillez contacter l'administrateur.");
        }
        
        application.setCandidat(candidat);
        application.setAppDate(new Date());
        
        // Si un ID d'offre d'emploi est fourni, récupérer l'offre
        if (jobId != null) {
            OffreEmploi job = offreEmploiService.getOffreById(jobId);
            if (job != null) {
                modelAndView.addObject("job", job);
            }
        }
        
        modelAndView.addObject("app", application);
        modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
        modelAndView.addObject("sectors", sectorService.listOfSectors());
        modelAndView.addObject("action", "create");
        
        return modelAndView;
    }

    @PostMapping("/create")
    public ModelAndView createApplication(
            @ModelAttribute Candidature application, 
            @RequestParam("selectedSectors") List<Integer> selectedSectorIds,
            @RequestParam(value = "notificationMessage", required = false) String notificationMessage) {       
        
        try {
            // Déléguer la création au service
            Candidature savedApplication = candidatureService.createCandidatureWithSectors(
                application, selectedSectorIds);
            
            // Gérer les notifications si nécessaire
            if (notificationMessage != null && !notificationMessage.trim().isEmpty()) {
                try {
                    int notificationCount = messageService.sendNotificationsForApplication(
                        savedApplication, notificationMessage);
                    return new ModelAndView("redirect:/applications/notification-result?id=" + 
                        savedApplication.getIdCandidature() + "&count=" + notificationCount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            return new ModelAndView("redirect:/applications/" + savedApplication.getIdCandidature());
        } catch (Exception e) {
            e.printStackTrace();
            ModelAndView modelAndView = new ModelAndView("application/applicationForm");
            modelAndView.addObject("app", application);
            modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
            modelAndView.addObject("sectors", sectorService.listOfSectors());
            modelAndView.addObject("action", "create");
            modelAndView.addObject("error", "Une erreur est survenue : " + e.getMessage());
            return modelAndView;
        }
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editApplicationForm(@PathVariable int id, HttpServletRequest request) {
        // Vérification que l'utilisateur est authentifié (sans vérifier son type)
        if (!authService.isAuthenticated(request.getSession())) {
            return new ModelAndView("redirect:/error/403");
        }
        
        ModelAndView modelAndView = new ModelAndView("application/applicationForm");
        Candidature application = candidatureService.getCandidatureById(id);
        
        // Vérification que la candidature existe
        if (application == null) {
            return new ModelAndView("redirect:/applications");
        }
        
        modelAndView.addObject("app", application);
        modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
        modelAndView.addObject("sectors", sectorService.listOfSectors());
        modelAndView.addObject("action", "edit");
        return modelAndView;
    }
    
    @PostMapping("/{id}/edit")
    public ModelAndView updateApplication(
            @PathVariable int id, 
            @ModelAttribute Candidature formApplication, 
            @RequestParam("selectedSectors") List<Integer> selectedSectorIds,
            HttpServletRequest request) {
        
        // Récupérer l'application existante pour vérification des droits
        Candidature existingApplication = candidatureService.getCandidatureById(id);
        
        // Vérification que la candidature existe
        if (existingApplication == null) {
            return new ModelAndView("redirect:/applications");
        }
        
        // Vérification des droits d'accès
        if (!authService.checkCandidatAccess(request.getSession(), existingApplication.getCandidat().getIdCandidat())) {
            return new ModelAndView("redirect:/error/403");
        }
        
        try {
            // Déléguer la mise à jour au service
            Candidature updatedApplication = candidatureService.updateCandidatureWithSectors(id, formApplication, selectedSectorIds);
            
            return new ModelAndView("redirect:/applications/" + id);
        } catch (Exception e) {
            // Gestion des erreurs
            ModelAndView modelAndView = new ModelAndView("application/applicationForm");
            modelAndView.addObject("app", formApplication);
            modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
            modelAndView.addObject("sectors", sectorService.listOfSectors());
            modelAndView.addObject("action", "edit");
            modelAndView.addObject("error", "Une erreur est survenue lors de la mise à jour : " + e.getMessage());
            return modelAndView;
        }
    }
    
    @GetMapping("/{id}/delete")
    public ModelAndView deleteApplication(@PathVariable int id, HttpServletRequest request) {
        Candidature application = candidatureService.getCandidatureById(id);
        
        // Vérification que la candidature existe
        if (application == null) {
            return new ModelAndView("redirect:/applications");
        }
        
        // Vérification que l'utilisateur est le candidat propriétaire
        if (!authService.checkCandidatAccess(request.getSession(), application.getCandidat().getIdCandidat())) {
            return new ModelAndView("redirect:/error/403");
        }
        
        candidatureService.deleteCandidature(id);
        return new ModelAndView("redirect:/applications");
    }

    @GetMapping("/search")
    public ModelAndView searchApplications(
            @RequestParam(required = false) String sector, 
            @RequestParam(required = false) String qualification) {
        
        // Nettoyage des paramètres
        if (sector != null && sector.trim().isEmpty()) {
            sector = null;
        }
        if (qualification != null && qualification.trim().isEmpty()) {
            qualification = null;
        }
        
        // Vérification des valeurs des paramètres (pour débogage)
        System.out.println("Recherche - Secteur: " + sector + ", Qualification: " + qualification);
        
        // Utilisation du service pour effectuer la recherche
        List<Candidature> applications = candidatureService.searchCandidatures(sector, qualification);
        
        // Vérification du nombre de résultats (pour débogage)
        System.out.println("Nombre de résultats: " + applications.size());
        
        ModelAndView modelAndView = new ModelAndView("application/applicationList");
        modelAndView.addObject("applicationslist", applications);
        modelAndView.addObject("sectors", sectorService.listOfSectors());
        modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
        modelAndView.addObject("selectedSector", sector);
        modelAndView.addObject("selectedQualification", qualification);
        
        return modelAndView;
    }

    @GetMapping("/{id}/matchingOffers")
    public ModelAndView viewMatchingOffers(@PathVariable int id, HttpServletRequest request) {
        // Vérifier que l'utilisateur est authentifié
        if (!authService.isAuthenticated(request.getSession())) {
            return new ModelAndView("redirect:/login?redirect=/applications/" + id + "/matchingOffers");
        }
        
        // Récupérer la candidature
        Candidature candidature = candidatureService.getCandidatureById(id);
        if (candidature == null) {
            return new ModelAndView("redirect:/applications");
        }
        
        // Vérifier que l'utilisateur est le propriétaire de la candidature ou un admin
        if (!authService.checkCandidatAccess(request.getSession(), candidature.getCandidat().getIdCandidat())) {
            return new ModelAndView("redirect:/error/403");
        }
        
        // Récupérer les offres correspondantes
        List<OffreEmploi> matchingOffers = offreEmploiService.getMatchingOffres(candidature);
        
        ModelAndView modelAndView = new ModelAndView("application/matchingOffers");
        modelAndView.addObject("candidature", candidature);
        modelAndView.addObject("matchingOffers", matchingOffers);
        
        return modelAndView;
    }
    
    @GetMapping("/notification-result")
    public ModelAndView showNotificationResult(
            @RequestParam("id") Integer id, 
            @RequestParam("count") Integer count) {
        ModelAndView modelAndView = new ModelAndView("application/notification-result");
        
        Candidature application = candidatureService.getCandidatureById(id);
        
        if (application != null) {
            // Force le chargement des relations
            application.getCandidat().getAppUser();
            application.getQualificationLevel();
            application.getSectors();
        }
        
        List<OffreEmploi> notifiedOffers = offreEmploiService.getMatchingOffres(application);
        
        modelAndView.addObject("app", application);
        modelAndView.addObject("count", count);
        modelAndView.addObject("notifiedOffers", notifiedOffers);
        
        return modelAndView;
    }
}