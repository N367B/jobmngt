package fr.atlantique.imt.inf211.jobmngt.controller;

import fr.atlantique.imt.inf211.jobmngt.entity.AppUser;
import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;
import fr.atlantique.imt.inf211.jobmngt.service.EntrepriseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/companies")
public class EntrepriseController {

    @Autowired
    private EntrepriseService entrepriseService;

    @GetMapping
    public ModelAndView listCompanies() {
        ModelAndView modelAndView = new ModelAndView("company/companyList");
        List<Entreprise> companies = entrepriseService.listEntreprises();
        modelAndView.addObject("companieslist", companies);
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
        
        // Vérification de la longueur du mot de passe
        if (company.getAppUser().getPassword() == null || company.getAppUser().getPassword().length() < 4) {
            modelAndView.setViewName("company/companyForm");
            modelAndView.addObject("company", company);
            modelAndView.addObject("error", "Le mot de passe doit contenir au moins 4 caractères.");
            modelAndView.addObject("action", "create");
            return modelAndView;
        }
        
        try {
            company.getAppUser().setUserType("entreprise");
            Entreprise savedCompany = entrepriseService.saveEntreprise(company);
            return new ModelAndView("redirect:/companies/" + savedCompany.getIdEntreprise());
        } catch (IllegalArgumentException e) {
            // Capture l'exception lancée quand l'email existe déjà
            modelAndView.setViewName("company/companyForm");
            modelAndView.addObject("company", company);
            modelAndView.addObject("error", "Un utilisateur avec cet email existe déjà.");
            modelAndView.addObject("action", "create");
            return modelAndView;
        } catch (Exception e) {
            // Pour les autres erreurs
            modelAndView.setViewName("company/companyForm");
            modelAndView.addObject("company", company);
            modelAndView.addObject("error", "Une erreur est survenue lors de la création de l'entreprise : " + e.getMessage());
            modelAndView.addObject("action", "create");
            return modelAndView;
        }
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editCompanyForm(@PathVariable int id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        
        // Vérification que l'utilisateur est connecté
        if (uid == null) {
            return new ModelAndView("redirect:/login");
        }
        
        ModelAndView modelAndView = new ModelAndView("company/companyForm");
        Entreprise company = entrepriseService.getEntrepriseById(id);
        
        // Vérification que l'entreprise existe
        if (company == null) {
            return new ModelAndView("redirect:/companies");
        }
        
        modelAndView.addObject("company", company);
        modelAndView.addObject("action", "edit");
        return modelAndView;
    }

    @PostMapping("/{id}/edit")
    public ModelAndView updateCompany(@PathVariable int id, @ModelAttribute Entreprise company) {
        Entreprise existingCompany = entrepriseService.getEntrepriseById(id);
        if (existingCompany != null) {
            company.setIdEntreprise(id);
            company.getAppUser().setIdUser(existingCompany.getAppUser().getIdUser());
            entrepriseService.saveEntreprise(company);
        }
        return new ModelAndView("redirect:/companies/" + id);
    }

    @GetMapping("/{id}/delete")
    public ModelAndView deleteCompany(@PathVariable int id) {
        entrepriseService.deleteEntreprise(id);
        return new ModelAndView("redirect:/companies");
    }
}