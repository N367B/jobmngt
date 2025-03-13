package fr.atlantique.imt.inf211.jobmngt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import fr.atlantique.imt.inf211.jobmngt.service.*;
@Controller
public class PagesController {

	@Autowired
	private EntrepriseService entrepriseService;

	@Autowired
	private CandidatService candidatService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView welcomePage() {
		ModelAndView modelAndView = new ModelAndView("index");

		modelAndView.addObject("countCompanies", entrepriseService.countEntreprises());
		modelAndView.addObject("countCandidates", candidatService.countCandidats());

		return modelAndView;

	}
}