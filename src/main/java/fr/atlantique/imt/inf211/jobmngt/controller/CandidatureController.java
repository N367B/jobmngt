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
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        
        // Vérification que l'utilisateur est connecté
        if (uid == null) {
            return new ModelAndView("redirect:/login");
        }
        
        ModelAndView modelAndView = new ModelAndView("application/applicationForm");
        Candidature application = candidatureService.getCandidatureById(id);
        
        // Vérification que la candidature existe
        if (application == null) {
            return new ModelAndView("redirect:/applications");
        }
        
        // Vérification que l'utilisateur est le candidat concerné ou un admin
        String userType = (String) session.getAttribute("usertype");
        if (!"admin".equals(userType) && (application.getCandidat() == null || application.getCandidat().getIdCandidat() != uid)) {
            return new ModelAndView("redirect:/applications");
        }
        
        modelAndView.addObject("application", application);
        modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
        modelAndView.addObject("sectors", sectorService.listOfSectors());
        modelAndView.addObject("action", "edit");
        return modelAndView;
    }

    @PostMapping("/{id}/edit")
    public ModelAndView updateApplication(
            @PathVariable int id, 
            @ModelAttribute Candidature application, 
            @RequestParam("selectedSectors") List<Integer> selectedSectorIds) {
        
        Candidature existingApplication = candidatureService.getCandidatureById(id);
        if (existingApplication != null) {
            application.setIdCandidature(id);
            
            // Conserver les relations qui ne sont pas dans le formulaire
            application.setCandidat(existingApplication.getCandidat());
            
            // Mise à jour des secteurs sélectionnés
            Set<Sector> sectors = new HashSet<>();
            for (Integer sectorId : selectedSectorIds) {
                Sector sector = sectorService.getSectorById(sectorId);
                if (sector != null) {
                    sectors.add(sector);
                }
            }
            application.setSectors(sectors);
            
            // Enregistrer la candidature mise à jour
            candidatureService.saveCandidature(application);
        }
        return new ModelAndView("redirect:/applications/" + id);
    }

    @GetMapping("/{id}/delete")
    public ModelAndView deleteApplication(@PathVariable int id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        String userType = (String) session.getAttribute("usertype");
        
        // Vérification que l'utilisateur est connecté
        if (uid == null) {
            return new ModelAndView("redirect:/login");
        }
        
        Candidature application = candidatureService.getCandidatureById(id);
        if (application != null) {
            // Vérification que l'utilisateur est le candidat concerné ou un admin
            if ("admin".equals(userType) || 
                (application.getCandidat() != null && application.getCandidat().getIdCandidat() == uid)) {
                candidatureService.deleteCandidature(id);
            }
        }
        
        return new ModelAndView("redirect:/applications");
    }

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
        
        return modelAndView;
    }
}