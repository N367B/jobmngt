package fr.atlantique.imt.inf211.jobmngt.controller;

import fr.atlantique.imt.inf211.jobmngt.entity.AppUser;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidat;
import fr.atlantique.imt.inf211.jobmngt.service.CandidatService;
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
        candidate.getAppUser().setUserType("candidat");
        Candidat savedCandidate = candidatService.saveCandidat(candidate);
        return new ModelAndView("redirect:/candidates/" + savedCandidate.getIdCandidat());
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editCandidateForm(@PathVariable int id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        
        // Vérification que l'utilisateur est connecté
        if (uid == null) {
            return new ModelAndView("redirect:/login");
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
    public ModelAndView updateCandidate(@PathVariable int id, @ModelAttribute Candidat candidate) {
        Candidat existingCandidate = candidatService.getCandidatById(id);
        if (existingCandidate != null) {
            candidate.setIdCandidat(id);
            candidate.getAppUser().setIdUser(existingCandidate.getAppUser().getIdUser());
            candidatService.saveCandidat(candidate);
        }
        return new ModelAndView("redirect:/candidates/" + id);
    }

    @GetMapping("/{id}/delete")
    public ModelAndView deleteCandidate(@PathVariable int id) {
        candidatService.deleteCandidat(id);
        return new ModelAndView("redirect:/candidates");
    }
}