package fr.atlantique.imt.inf211.jobmngt.controller;

import fr.atlantique.imt.inf211.jobmngt.dto.candidature.CandidatureDTO;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidat;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidature;
import fr.atlantique.imt.inf211.jobmngt.entity.OffreEmploi;
import fr.atlantique.imt.inf211.jobmngt.entity.QualificationLevel;
import fr.atlantique.imt.inf211.jobmngt.entity.Sector;
import fr.atlantique.imt.inf211.jobmngt.service.CandidatService;
import fr.atlantique.imt.inf211.jobmngt.service.CandidatureService;
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
    
    @GetMapping
    public ModelAndView listApplications() {
        ModelAndView modelAndView = new ModelAndView("application/applicationList");
        
        List<Candidature> applications = candidatureService.listCandidatures();
        modelAndView.addObject("applicationslist", applications);
        
        modelAndView.addObject("sectors", sectorService.listOfSectors());
        modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
        
        return modelAndView;
    }

    /* 
    @GetMapping("/{id}")
    public ModelAndView viewApplication(@PathVariable int id) {
        ModelAndView modelAndView = new ModelAndView("application/applicationView");
        Candidature application = candidatureService.getCandidatureById(id);
        
        if (application == null) {
            return new ModelAndView("redirect:/applications");
        }
        
        // Débogage
        System.out.println("Application ID: " + application.getIdCandidature());
        System.out.println("Candidat: " + (application.getCandidat() != null ? "Not null" : "NULL"));
        if (application.getCandidat() == null) {
            // Si candidat est null, utilisez un candidat par défaut pour éviter l'erreur
            Candidat defaultCandidat = new Candidat();
            defaultCandidat.setFirstName("Non");
            defaultCandidat.setLastName("Spécifié");
            application.setCandidat(defaultCandidat);
        } else {
            System.out.println("Candidat ID: " + application.getCandidat().getIdCandidat());
            System.out.println("Candidat Name: " + application.getCandidat().getFirstName() + " " + application.getCandidat().getLastName());
        }
        application.getCandidat().setFirstName("test");
        application.getCandidat().setLastName("Test");
        modelAndView.addObject("application", application);
        return modelAndView;
    }*/

    @GetMapping("/{id}")
    public ModelAndView viewApplication(@PathVariable int id) {
        ModelAndView modelAndView = new ModelAndView("application/applicationView");
        Candidature application = candidatureService.getCandidatureById(id);
        
        if (application == null) {
            return new ModelAndView("redirect:/applications");
        }
        
        // Convertir en DTO pour éviter les problèmes de lazy loading
        CandidatureDTO applicationDTO = new CandidatureDTO(application);
        
        // Passer à la fois le DTO et l'objet d'origine au modèle
        modelAndView.addObject("applicationDTO", applicationDTO);
        modelAndView.addObject("application", application);
        
        return modelAndView;
    }
    /*
    @GetMapping("/create")
    public ModelAndView createApplicationForm(@RequestParam(required = false) Integer jobId, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        String userType = (String) session.getAttribute("usertype");
        
        // Vérification que l'utilisateur est connecté et est un candidat
        if (uid == null || !"candidat".equals(userType)) {
            return new ModelAndView("redirect:/login");
        }
        
        ModelAndView modelAndView = new ModelAndView("application/applicationForm");
        Candidature application = new Candidature();
        
        // Récupérer le candidat lié à l'utilisateur connecté
        Candidat candidat = candidatService.getCandidatById(uid);
        application.setCandidat(candidat);
        
        // Si un ID d'offre d'emploi est fourni, récupérer l'offre
        if (jobId != null) {
            OffreEmploi job = offreEmploiService.getOffreById(jobId);
            if (job != null) {
                modelAndView.addObject("job", job);
            }
        }
        
        application.setAppDate(new Date());
        
        modelAndView.addObject("application", application);
        modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
        modelAndView.addObject("sectors", sectorService.listOfSectors());
        modelAndView.addObject("action", "create");
        
        return modelAndView;
    }*/

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
        application.setCandidat(candidat);
        
        // Si un ID d'offre d'emploi est fourni, récupérer l'offre
        if (jobId != null) {
            OffreEmploi job = offreEmploiService.getOffreById(jobId);
            if (job != null) {
                modelAndView.addObject("job", job);
            }
        }
        
        application.setAppDate(new Date());
        
        modelAndView.addObject("app", application);
        modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
        modelAndView.addObject("sectors", sectorService.listOfSectors());
        modelAndView.addObject("action", "create");
        
        return modelAndView;
    }

    @PostMapping("/create")
    public ModelAndView createApplication(
            @ModelAttribute Candidature application, 
            @RequestParam("selectedSectors") List<Integer> selectedSectorIds) {
        
        // Mise à jour des secteurs sélectionnés
        Set<Sector> sectors = new HashSet<>();
        for (Integer sectorId : selectedSectorIds) {
            Sector sector = sectorService.getSectorById(sectorId);
            if (sector != null) {
                sectors.add(sector);
            }
        }
        application.setSectors(sectors);
        application.setAppDate(new Date());
        // Vérifier que le CV a un nom, sinon en générer un
        if (application.getCv() == null || application.getCv().trim().isEmpty()) {
            String firstName = application.getCandidat().getFirstName();
            String lastName = application.getCandidat().getLastName();
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd").format(new Date());
            application.setCv("CV_" + firstName + "_" + lastName + "_" + timestamp + ".pdf");
        }
        
        // Enregistrer la candidature
        Candidature savedApplication = candidatureService.saveCandidature(application);
        
        return new ModelAndView("redirect:/applications/" + savedApplication.getIdCandidature());
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
        
        formApplication.setAppDate(new Date());
        // Afficher des informations sur l'objet reçu du formulaire
        System.out.println("Données du formulaire reçues:");
        System.out.println("ID: " + formApplication.getIdCandidature());
        System.out.println("CV: " + formApplication.getCv());
        System.out.println("Date: " + formApplication.getAppDate());
        System.out.println("Qualification: " + (formApplication.getQualificationLevel() != null ? 
                formApplication.getQualificationLevel().getLabelQualification() : "null"));
        System.out.println("Secteurs: " + selectedSectorIds);
        
        // Récupérer l'application existante depuis la base de données
        Candidature existingApplication = candidatureService.getCandidatureById(id);
        
        // Vérification que la candidature existe
        if (existingApplication == null) {
            System.out.println("Candidature non trouvée avec ID: " + id);
            return new ModelAndView("redirect:/applications");
        }
        
        // Afficher les informations sur l'objet existant
        System.out.println("Données existantes avant mise à jour:");
        System.out.println("ID: " + existingApplication.getIdCandidature());
        System.out.println("CV: " + existingApplication.getCv());
        System.out.println("Date: " + existingApplication.getAppDate());
        System.out.println("Qualification: " + (existingApplication.getQualificationLevel() != null ? 
                existingApplication.getQualificationLevel().getLabelQualification() : "null"));
        
        // Vérification que l'utilisateur est le candidat propriétaire
        if (!authService.checkCandidatAccess(request.getSession(), existingApplication.getCandidat().getIdCandidat())) {
            return new ModelAndView("redirect:/error/403");
        }
        
        // Mettre à jour les propriétés de l'entité existante avec les nouvelles valeurs
        existingApplication.setAppDate(formApplication.getAppDate());
        existingApplication.setCv(formApplication.getCv());
        existingApplication.setQualificationLevel(formApplication.getQualificationLevel());
        
        // Mise à jour des secteurs sélectionnés
        Set<Sector> sectors = new HashSet<>();
        for (Integer sectorId : selectedSectorIds) {
            Sector sector = sectorService.getSectorById(sectorId);
            if (sector != null) {
                sectors.add(sector);
                System.out.println("Ajout du secteur: " + sector.getLabelSecteur());
            }
        }
        existingApplication.setSectors(sectors);
        
        try {
            // Enregistrer la candidature mise à jour
            System.out.println("Tentative de sauvegarde...");
            Candidature savedApplication = candidatureService.saveCandidature(existingApplication);
            
            // Vérifier que la sauvegarde a bien fonctionné
            System.out.println("Sauvegarde effectuée, vérification de l'objet enregistré:");
            System.out.println("ID: " + savedApplication.getIdCandidature());
            System.out.println("CV: " + savedApplication.getCv());
            System.out.println("Date: " + savedApplication.getAppDate());
            System.out.println("Qualification: " + (savedApplication.getQualificationLevel() != null ? 
                    savedApplication.getQualificationLevel().getLabelQualification() : "null"));
            
            return new ModelAndView("redirect:/applications/" + id);
        } catch (Exception e) {
            System.out.println("Erreur lors de la sauvegarde: " + e.getMessage());
            e.printStackTrace();
            
            ModelAndView modelAndView = new ModelAndView("application/applicationForm");
            modelAndView.addObject("app", existingApplication);
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
    /*
    @GetMapping("/search")
    public ModelAndView searchApplications(
            @RequestParam(required = false) String sector, 
            @RequestParam(required = false) String qualification) {
        
        ModelAndView modelAndView = new ModelAndView("application/applicationList");
        List<Candidature> applications;
        
        if (sector != null && !sector.isEmpty() && qualification != null && !qualification.isEmpty()) {
            applications = candidatureService.searchCandidatures(sector, qualification);
        } else {
            applications = candidatureService.listCandidatures();
        }
        
        modelAndView.addObject("applicationslist", applications);
        modelAndView.addObject("sectors", sectorService.listOfSectors());
        modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
        modelAndView.addObject("selectedSector", sector);
        modelAndView.addObject("selectedQualification", qualification);
        
        return modelAndView;}
    */
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
    
}