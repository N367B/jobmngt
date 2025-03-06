package fr.atlantique.imt.inf211.jobmngt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.stereotype.Controller;

@Controller
public class PagesController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView welcomePage() {
		ModelAndView modelAndView = new ModelAndView("index");
		modelAndView.addObject("countCompanies", 0);
		modelAndView.addObject("countCandidates", 0);

		return modelAndView;

	}
}