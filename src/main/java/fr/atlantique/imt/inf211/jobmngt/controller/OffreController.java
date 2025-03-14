package fr.atlantique.imt.inf211.jobmngt.controller;

import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;
import fr.atlantique.imt.inf211.jobmngt.entity.OffreEmploi;
import fr.atlantique.imt.inf211.jobmngt.entity.QualificationLevel;
import fr.atlantique.imt.inf211.jobmngt.entity.Sector;
import fr.atlantique.imt.inf211.jobmngt.service.EntrepriseService;
import fr.atlantique.imt.inf211.jobmngt.service.OffreEmploiService;
import fr.atlantique.imt.inf211.jobmngt.service.QualificationLevelService;
import fr.atlantique.imt.inf211.jobmngt.service.AuthenticationService;
import fr.atlantique.imt.inf211.jobmngt.service.SectorService;
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
        
        // Gérer les secteurs sélectionnés
        if (selectedSectorIds != null && !selectedSectorIds.isEmpty()) {
            HashSet<Sector> sectors = new HashSet<>();
            for (Integer sectorId : selectedSectorIds) {
                Sector sector = sectorService.getSectorById(sectorId);
                if (sector != null) {
                    sectors.add(sector);
                }
            }
            job.setSectors(sectors);
        }
        
        try {
            OffreEmploi savedJob = offreEmploiService.saveOffre(job);
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
        if (!authenticationService.checkEntrepriseAccess(request.getSession(), existingJob.getEntreprise().getIdEntreprise())) {
            return new ModelAndView("redirect:/error/403");
        }
        // Préserver l'ID et l'entreprise
        job.setIdOffreEmploi(id);
        job.setEntreprise(existingJob.getEntreprise());
        
        // Gérer les secteurs sélectionnés
        if (selectedSectorIds != null && !selectedSectorIds.isEmpty()) {
            HashSet<Sector> sectors = new HashSet<>();
            for (Integer sectorId : selectedSectorIds) {
                Sector sector = sectorService.getSectorById(sectorId);
                if (sector != null) {
                    sectors.add(sector);
                }
            }
            job.setSectors(sectors);
        }
        
        try {
            offreEmploiService.saveOffre(job);
            return new ModelAndView("redirect:/jobs/" + id);
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
    public ModelAndView searchJobs(@RequestParam(required = false) String sector, 
                                  @RequestParam(required = false) String qualification) {
        ModelAndView modelAndView = new ModelAndView("job/jobList");
        List<OffreEmploi> jobs;
        
        if (sector != null && !sector.isEmpty() && qualification != null && !qualification.isEmpty()) {
            jobs = offreEmploiService.searchOffres(sector, qualification);
        } else {
            jobs = offreEmploiService.listOffres();
        }
        
        modelAndView.addObject("jobslist", jobs);
        modelAndView.addObject("sectors", sectorService.listOfSectors());
        modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
        modelAndView.addObject("selectedSector", sector);
        modelAndView.addObject("selectedQualification", qualification);
        return modelAndView;
    }
}