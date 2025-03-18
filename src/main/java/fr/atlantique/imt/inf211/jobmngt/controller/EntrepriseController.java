package fr.atlantique.imt.inf211.jobmngt.controller;

import fr.atlantique.imt.inf211.jobmngt.entity.AppUser;
import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;
import fr.atlantique.imt.inf211.jobmngt.service.EntrepriseService;
import fr.atlantique.imt.inf211.jobmngt.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/companies")
public class EntrepriseController {

    @Autowired
    private EntrepriseService entrepriseService;

    @Autowired
    private AuthenticationService authService;

    @GetMapping
    public ModelAndView listCompanies() {
        ModelAndView modelAndView = new ModelAndView("company/companyList");
        modelAndView.addObject("companieslist", entrepriseService.listEntreprises());
        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView viewCompany(@PathVariable int id) {
        ModelAndView modelAndView = new ModelAndView("company/companyView");
        Entreprise company = entrepriseService.getEntrepriseById(id);
        
        if (company == null) {
            return new ModelAndView("redirect:/companies");
        }
        
        modelAndView.addObject("company", company);
        return modelAndView;
    }

    @GetMapping("/create")
    public ModelAndView createCompanyForm() {
        ModelAndView modelAndView = new ModelAndView("company/companyForm");
        Entreprise company = new Entreprise();
        company.setAppUser(new AppUser());
        modelAndView.addObject("company", company);
        modelAndView.addObject("action", "create");
        return modelAndView;
    }

    @PostMapping("/create")
    public ModelAndView createCompany(@ModelAttribute Entreprise company) {
        ModelAndView modelAndView = new ModelAndView();
        
        try {
            // La validation du mot de passe sera faite dans le service
            Entreprise savedCompany = entrepriseService.createEntreprise(company);
            return new ModelAndView("redirect:/companies");
        } catch (IllegalArgumentException e) {
            modelAndView.setViewName("company/companyForm");
            modelAndView.addObject("company", company);
            modelAndView.addObject("error", e.getMessage());
            modelAndView.addObject("action", "create");
            return modelAndView;
        } catch (Exception e) {
            modelAndView.setViewName("company/companyForm");
            modelAndView.addObject("company", company);
            modelAndView.addObject("error", "Une erreur est survenue: " + e.getMessage());
            modelAndView.addObject("action", "create");
            return modelAndView;
        }
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editCompanyForm(@PathVariable int id, HttpServletRequest request) {
        if (!authService.checkEntrepriseAccess(request.getSession(), id)) {
            return new ModelAndView("redirect:/error/403");
        }
        
        ModelAndView modelAndView = new ModelAndView("company/companyForm");
        modelAndView.addObject("company", entrepriseService.getEntrepriseById(id));
        modelAndView.addObject("action", "edit");
        return modelAndView;
    }

    @PostMapping("/{id}/edit")
    public ModelAndView updateCompany(@PathVariable int id, @ModelAttribute Entreprise company, HttpServletRequest request) {
        if (!authService.checkEntrepriseAccess(request.getSession(), id)) {
            return new ModelAndView("redirect:/error/403");
        }
        
        ModelAndView modelAndView = new ModelAndView();
        
        try {
            entrepriseService.updateEntreprise(id, company);
            return new ModelAndView("redirect:/companies/" + id);
        } catch (IllegalArgumentException e) {
            modelAndView.setViewName("company/companyForm");
            modelAndView.addObject("company", company);
            modelAndView.addObject("error", e.getMessage());
            modelAndView.addObject("action", "edit");
            return modelAndView;
        } catch (Exception e) {
            modelAndView.setViewName("company/companyForm");
            modelAndView.addObject("company", company);
            modelAndView.addObject("error", "Une erreur est survenue: " + e.getMessage());
            modelAndView.addObject("action", "edit");
            return modelAndView;
        }
    }

    @GetMapping("/{id}/delete")
    public ModelAndView deleteCompany(@PathVariable int id, HttpServletRequest request) {
        if (!authService.checkEntrepriseAccess(request.getSession(), id)) {
            return new ModelAndView("redirect:/error/403");
        }
        entrepriseService.deleteEntreprise(id);
        return new ModelAndView("redirect:/companies");
    }
}