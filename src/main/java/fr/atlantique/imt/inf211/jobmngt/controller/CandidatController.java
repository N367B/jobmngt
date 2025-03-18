package fr.atlantique.imt.inf211.jobmngt.controller;

import fr.atlantique.imt.inf211.jobmngt.entity.AppUser;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidat;
import fr.atlantique.imt.inf211.jobmngt.service.CandidatService;
import fr.atlantique.imt.inf211.jobmngt.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/candidates")
public class CandidatController {

    @Autowired
    private CandidatService candidatService;

    @Autowired
    private AuthenticationService authService;

    @GetMapping
    public ModelAndView listCandidates() {
        ModelAndView modelAndView = new ModelAndView("candidate/candidateList");
        List<Candidat> candidates = candidatService.listCandidats();
        modelAndView.addObject("candidateslist", candidates);
        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView viewCandidate(@PathVariable int id) {
        ModelAndView modelAndView = new ModelAndView("candidate/candidateView");
        Candidat candidate = candidatService.getCandidatById(id);
        
        if (candidate == null) {
            return new ModelAndView("redirect:/candidates");
        }
        
        modelAndView.addObject("candidate", candidate);
        return modelAndView;
    }

    @GetMapping("/create")
    public ModelAndView createCandidateForm() {
        ModelAndView modelAndView = new ModelAndView("candidate/candidateForm");
        Candidat candidate = new Candidat();
        candidate.setAppUser(new AppUser());
        modelAndView.addObject("candidate", candidate);
        modelAndView.addObject("action", "create");
        return modelAndView;
    }

    @PostMapping("/create")
    public ModelAndView createCandidate(@ModelAttribute Candidat candidate) {
        ModelAndView modelAndView = new ModelAndView();
        
        try {
            // Délégation de la validation et création au service
            Candidat savedCandidate = candidatService.createCandidat(candidate);
            return new ModelAndView("redirect:/candidates");
        } catch (IllegalArgumentException e) {
            // Gestion des erreurs de validation
            modelAndView.setViewName("candidate/candidateForm");
            modelAndView.addObject("candidate", candidate);
            modelAndView.addObject("error", e.getMessage());
            modelAndView.addObject("action", "create");
            return modelAndView;
        } catch (Exception e) {
            // Pour les autres erreurs
            modelAndView.setViewName("candidate/candidateForm");
            modelAndView.addObject("candidate", candidate);
            modelAndView.addObject("error", "Une erreur est survenue lors de la création du candidat : " + e.getMessage());
            modelAndView.addObject("action", "create");
            return modelAndView;
        }
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editCandidateForm(@PathVariable int id, HttpServletRequest request) {

        if (!authService.checkCandidatAccess(request.getSession(), id)) {
            return new ModelAndView("redirect:/error/403");
        }    
        
        ModelAndView modelAndView = new ModelAndView("candidate/candidateForm");
        Candidat candidate = candidatService.getCandidatById(id);
        
        // Vérification que le candidat existe
        if (candidate == null) {
            return new ModelAndView("redirect:/candidates");
        }
        
        modelAndView.addObject("candidate", candidate);
        modelAndView.addObject("action", "edit");
        return modelAndView;
    }

@PostMapping("/{id}/edit")
public ModelAndView updateCandidate(@PathVariable int id, @ModelAttribute Candidat candidate, HttpServletRequest request) {
    // Vérification des permissions
    if (!authService.checkCandidatAccess(request.getSession(), id)) {
        return new ModelAndView("redirect:/error/403");
    }
    
    ModelAndView modelAndView = new ModelAndView();
    
    try {
        // Délégation de la validation et mise à jour au service
        candidatService.updateCandidat(id, candidate);
        return new ModelAndView("redirect:/candidates/" + id);
    } catch (IllegalArgumentException e) {
        // Gestion des erreurs de validation
        modelAndView.setViewName("candidate/candidateForm");
        modelAndView.addObject("candidate", candidate);
        modelAndView.addObject("error", e.getMessage());
        modelAndView.addObject("action", "edit");
        return modelAndView;
    } catch (Exception e) {
        // Pour les autres erreurs
        modelAndView.setViewName("candidate/candidateForm");
        modelAndView.addObject("candidate", candidate);
        modelAndView.addObject("error", "Une erreur est survenue lors de la mise à jour du candidat : " + e.getMessage());
        modelAndView.addObject("action", "edit");
        return modelAndView;
    }
}

    @GetMapping("/{id}/delete")
    public ModelAndView deleteCandidate(@PathVariable int id, HttpServletRequest request) {
        if (!authService.checkCandidatAccess(request.getSession(), id)) {
            return new ModelAndView("redirect:/error/403");
        }    
        candidatService.deleteCandidat(id);
        return new ModelAndView("redirect:/candidates");
    }
}