package fr.atlantique.imt.inf211.jobmngt.controller;

import fr.atlantique.imt.inf211.jobmngt.entity.Candidature;
import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;
import fr.atlantique.imt.inf211.jobmngt.entity.OffreEmploi;
import fr.atlantique.imt.inf211.jobmngt.entity.QualificationLevel;
import fr.atlantique.imt.inf211.jobmngt.entity.Sector;
import fr.atlantique.imt.inf211.jobmngt.service.EntrepriseService;
import fr.atlantique.imt.inf211.jobmngt.service.OffreEmploiService;
import fr.atlantique.imt.inf211.jobmngt.service.QualificationLevelService;
import fr.atlantique.imt.inf211.jobmngt.service.AuthenticationService;
import fr.atlantique.imt.inf211.jobmngt.service.CandidatureService;
import fr.atlantique.imt.inf211.jobmngt.service.SectorService;
import fr.atlantique.imt.inf211.jobmngt.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/jobs")
public class OffreController {

    @Autowired
    private OffreEmploiService offreEmploiService;

    @Autowired
    private EntrepriseService entrepriseService;

    @Autowired
    private QualificationLevelService qualificationLevelService;

    @Autowired
    private SectorService sectorService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private MessageService messageService;

    @GetMapping
    public ModelAndView listJobs() {
        ModelAndView modelAndView = new ModelAndView("job/jobList");
        
        List<OffreEmploi> jobs = offreEmploiService.listOffres();
        modelAndView.addObject("jobslist", jobs);
        
        modelAndView.addObject("sectors", sectorService.listOfSectors());
        modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
        
        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView viewJob(@PathVariable int id) {
        ModelAndView modelAndView = new ModelAndView("job/jobView");
        OffreEmploi job = offreEmploiService.getOffreById(id);
        
        if (job == null) {
            return new ModelAndView("redirect:/jobs");
        }
        
        modelAndView.addObject("job", job);
        return modelAndView;
    }

    @GetMapping("/create")
    public ModelAndView createJobForm(HttpServletRequest request) {
        if (!authenticationService.isEntreprise(request.getSession()) || !authenticationService.isAuthenticated(request.getSession())) {
            return new ModelAndView("redirect:/error/403");
        }
        
        ModelAndView modelAndView = new ModelAndView("job/jobForm");
        OffreEmploi job = new OffreEmploi();
        
        // Récupérer l'entreprise liée à l'utilisateur connecté
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        Entreprise entreprise = entrepriseService.getEntrepriseById(uid);
        job.setEntreprise(entreprise);
        
        modelAndView.addObject("job", job);
        modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
        modelAndView.addObject("sectors", sectorService.listOfSectors());
        modelAndView.addObject("action", "create");
        return modelAndView;
    }

    @PostMapping("/create")
    public ModelAndView createJob(@ModelAttribute OffreEmploi job, 
                                 @RequestParam("selectedSectors") List<Integer> selectedSectorIds,
                                 @RequestParam(value = "notificationMessage", required = false) String notificationMessage,
                                 HttpServletRequest request) {
        // Vérifier que l'utilisateur est connecté et est une entreprise
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        
        if (uid == null || !"entreprise".equals(session.getAttribute("usertype"))) {
            return new ModelAndView("redirect:/error/403");
        }
        
        // Récupérer l'entreprise et l'associer à l'offre
        Entreprise entreprise = entrepriseService.getEntrepriseById(uid);
        if (entreprise == null) {
            ModelAndView modelAndView = new ModelAndView("job/jobForm");
            modelAndView.addObject("job", job);
            modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
            modelAndView.addObject("sectors", sectorService.listOfSectors());
            modelAndView.addObject("action", "create");
            modelAndView.addObject("error", "Une erreur est survenue : entreprise non trouvée.");
            return modelAndView;
        }
        
        // Associer l'entreprise à l'offre
        job.setEntreprise(entreprise);
        
        try {
            // Déléguer au service la création avec gestion des secteurs
            OffreEmploi savedJob = offreEmploiService.createOffreWithSectors(job, selectedSectorIds);
            
            // Gérer les notifications si nécessaire
            if (notificationMessage != null && !notificationMessage.trim().isEmpty()) {
                try {
                    int notificationCount = offreEmploiService.sendNotificationsForJob(savedJob, notificationMessage);
                    return new ModelAndView("redirect:/jobs/notification-result?id=" + savedJob.getIdOffreEmploi() + "&count=" + notificationCount);
                } catch (Exception e) {
                    // Continuer même si l'envoi échoue, juste logger l'erreur
                    System.err.println("ERREUR lors de l'envoi des notifications: " + e.getMessage());
                }
            }
            
            return new ModelAndView("redirect:/jobs/" + savedJob.getIdOffreEmploi());
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView("job/jobForm");
            modelAndView.addObject("job", job);
            modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
            modelAndView.addObject("sectors", sectorService.listOfSectors());
            modelAndView.addObject("action", "create");
            modelAndView.addObject("error", "Une erreur est survenue : " + e.getMessage());
            return modelAndView;
        }
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editJobForm(@PathVariable int id, HttpServletRequest request) {
        if (!authenticationService.isAuthenticated(request.getSession())) {
            return new ModelAndView("redirect:/error/403");
        }
        
        ModelAndView modelAndView = new ModelAndView("job/jobForm");
        OffreEmploi job = offreEmploiService.getOffreById(id);
        
        // Vérification que l'offre existe
        if (job == null) {
            return new ModelAndView("redirect:/jobs");
        }
        modelAndView.addObject("job", job);
        modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
        modelAndView.addObject("sectors", sectorService.listOfSectors());
        modelAndView.addObject("action", "edit");
        return modelAndView;
    }

    @PostMapping("/{id}/edit")
    public ModelAndView updateJob(@PathVariable int id, 
                                 @ModelAttribute OffreEmploi job, 
                                 @RequestParam("selectedSectors") List<Integer> selectedSectorIds,
                                 HttpServletRequest request) {
    
        OffreEmploi existingJob = offreEmploiService.getOffreById(id);
        if (existingJob == null) {
            return new ModelAndView("redirect:/jobs");
        }
        
        if (!authenticationService.checkEntrepriseAccess(request.getSession(), existingJob.getEntreprise().getIdEntreprise())) {
            return new ModelAndView("redirect:/error/403");
        }
        
        try {
            // Déléguer la mise à jour au service
            OffreEmploi updatedJob = offreEmploiService.updateOffreWithSectors(id, job, selectedSectorIds);
            return new ModelAndView("redirect:/jobs/" + updatedJob.getIdOffreEmploi());
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView("job/jobForm");
            modelAndView.addObject("job", job);
            modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
            modelAndView.addObject("sectors", sectorService.listOfSectors());
            modelAndView.addObject("action", "edit");
            modelAndView.addObject("error", "Une erreur est survenue lors de la mise à jour : " + e.getMessage());
            return modelAndView;
        }
    }
    @GetMapping("/{id}/delete")
    public ModelAndView deleteJob(@PathVariable int id, HttpServletRequest request) {
        if (!authenticationService.checkEntrepriseAccess(request.getSession(), offreEmploiService.getOffreById(id).getEntreprise().getIdEntreprise())) {
            return new ModelAndView("redirect:/error/403");
        }
        
        offreEmploiService.deleteOffre(id);
        return new ModelAndView("redirect:/jobs");
    }


    @GetMapping("/search")
    public ModelAndView searchJobs(
            @RequestParam(required = false) String sector, 
            @RequestParam(required = false) String qualification) {
        // Nettoyage des paramètres
        if (sector != null && sector.trim().isEmpty()) {
            sector = null;
        }
        if (qualification != null && qualification.trim().isEmpty()) {
            qualification = null;
        }
        
        // Vérifiez ici les valeurs des paramètres
        System.out.println("Recherche - Secteur: " + sector + ", Qualification: " + qualification);
        
        List<OffreEmploi> jobs = offreEmploiService.searchOffres(sector, qualification);
        
        // Vérifiez le nombre de résultats
        System.out.println("Nombre de résultats: " + jobs.size());
        
        ModelAndView modelAndView = new ModelAndView("job/jobList");
        modelAndView.addObject("jobslist", jobs);  // Utiliser "jobslist" au lieu de "jobs"
        
        // Ajouter ces attributs pour que les filtres de recherche fonctionnent
        modelAndView.addObject("sectors", sectorService.listOfSectors());
        modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
        modelAndView.addObject("selectedSector", sector);
        modelAndView.addObject("selectedQualification", qualification);
        
        return modelAndView;
    }

    @Autowired
    private CandidatureService candidatureService;

    @GetMapping("/{id}/matchingCandidatures")
    public ModelAndView viewMatchingCandidatures(@PathVariable int id, HttpServletRequest request) {
        // Vérifier que l'utilisateur est authentifié et est une entreprise
        if (!authenticationService.isAuthenticated(request.getSession()) || 
            !authenticationService.isEntreprise(request.getSession())) {
            return new ModelAndView("redirect:/error/403");
        }
        
        // Récupérer l'offre d'emploi
        OffreEmploi offre = offreEmploiService.getOffreById(id);
        if (offre == null) {
            return new ModelAndView("redirect:/jobs");
        }
        
        // Vérifier que l'utilisateur connecté est l'entreprise qui a créé l'offre
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        
        if (!authenticationService.isAuthenticated(request.getSession()) || 
            !authenticationService.isEntreprise(request.getSession())) {
            return new ModelAndView("redirect:/error/403");
        }
        
        // Récupérer les candidatures correspondantes
        List<Candidature> matchingCandidatures = candidatureService.getMatchingCandidatures(offre);
        
        ModelAndView modelAndView = new ModelAndView("job/matchingCandidatures");
        modelAndView.addObject("offre", offre);
        modelAndView.addObject("matchingCandidatures", matchingCandidatures);
        
        return modelAndView;
    }

    @GetMapping("/notification-result")
    public ModelAndView showNotificationResult(@RequestParam("id") Integer id, @RequestParam("count") Integer count) {
        ModelAndView modelAndView = new ModelAndView("job/notification-result");
        
        OffreEmploi job = offreEmploiService.getOffreById(id);
        List<Candidature> notifiedCandidatures = candidatureService.getMatchingCandidatures(job);
        
        modelAndView.addObject("job", job);
        modelAndView.addObject("count", count);
        modelAndView.addObject("notifiedCandidatures", notifiedCandidatures);
        
        return modelAndView;
    }
}