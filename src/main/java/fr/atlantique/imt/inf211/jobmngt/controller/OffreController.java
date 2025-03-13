package fr.atlantique.imt.inf211.jobmngt.controller;

import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;
import fr.atlantique.imt.inf211.jobmngt.entity.OffreEmploi;
import fr.atlantique.imt.inf211.jobmngt.entity.QualificationLevel;
import fr.atlantique.imt.inf211.jobmngt.entity.Sector;
import fr.atlantique.imt.inf211.jobmngt.service.EntrepriseService;
import fr.atlantique.imt.inf211.jobmngt.service.OffreEmploiService;
import fr.atlantique.imt.inf211.jobmngt.service.QualificationLevelService;
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
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        String userType = (String) session.getAttribute("usertype");
        
        // Vérification que l'utilisateur est connecté et est une entreprise
        if (uid == null || !"entreprise".equals(userType)) {
            return new ModelAndView("redirect:/login");
        }
        
        ModelAndView modelAndView = new ModelAndView("job/jobForm");
        OffreEmploi job = new OffreEmploi();
        
        // Récupérer l'entreprise liée à l'utilisateur connecté
        Entreprise entreprise = entrepriseService.getEntrepriseById(uid);
        job.setEntreprise(entreprise);
        
        modelAndView.addObject("job", job);
        modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
        modelAndView.addObject("sectors", sectorService.listOfSectors());
        modelAndView.addObject("action", "create");
        return modelAndView;
    }

    @PostMapping("/create")
    public ModelAndView createJob(@ModelAttribute OffreEmploi job, @RequestParam("selectedSectors") List<Integer> selectedSectorIds) {
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
        
        OffreEmploi savedJob = offreEmploiService.saveOffre(job);
        return new ModelAndView("redirect:/jobs/" + savedJob.getIdOffreEmploi());
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editJobForm(@PathVariable int id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        String userType = (String) session.getAttribute("usertype");
        
        // Vérification que l'utilisateur est connecté
        if (uid == null) {
            return new ModelAndView("redirect:/login");
        }
        
        ModelAndView modelAndView = new ModelAndView("job/jobForm");
        OffreEmploi job = offreEmploiService.getOffreById(id);
        
        // Vérification que l'offre existe
        if (job == null) {
            return new ModelAndView("redirect:/jobs");
        }
        
        // Si l'utilisateur n'est pas admin et n'est pas l'entreprise qui a créé l'offre
        if (!"admin".equals(userType) && (job.getEntreprise() == null || job.getEntreprise().getIdEntreprise() != uid)) {
            return new ModelAndView("redirect:/jobs");
        }
        
        modelAndView.addObject("job", job);
        modelAndView.addObject("qualificationLevels", qualificationLevelService.listOfQualificationLevels());
        modelAndView.addObject("sectors", sectorService.listOfSectors());
        modelAndView.addObject("action", "edit");
        return modelAndView;
    }

    @PostMapping("/{id}/edit")
    public ModelAndView updateJob(@PathVariable int id, @ModelAttribute OffreEmploi job, @RequestParam("selectedSectors") List<Integer> selectedSectorIds) {
        OffreEmploi existingJob = offreEmploiService.getOffreById(id);
        if (existingJob != null) {
            job.setIdOffreEmploi(id);
            
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
            
            offreEmploiService.saveOffre(job);
        }
        return new ModelAndView("redirect:/jobs/" + id);
    }

    @GetMapping("/{id}/delete")
    public ModelAndView deleteJob(@PathVariable int id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        String userType = (String) session.getAttribute("usertype");
        
        // Vérification que l'utilisateur est connecté
        if (uid == null) {
            return new ModelAndView("redirect:/login");
        }
        
        OffreEmploi job = offreEmploiService.getOffreById(id);
        
        // Si l'utilisateur n'est pas admin et n'est pas l'entreprise qui a créé l'offre
        if (job != null && (!"admin".equals(userType)) && 
            (job.getEntreprise() == null || job.getEntreprise().getIdEntreprise() != uid)) {
            return new ModelAndView("redirect:/jobs");
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