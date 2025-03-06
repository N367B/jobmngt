package fr.atlantique.imt.inf211.jobmngt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;

import fr.atlantique.imt.inf211.jobmngt.service.QualificationLevelService;

@Controller
public class QualificationLevelController {

    @Autowired
    private QualificationLevelService qualificationLevelService;

    @RequestMapping(value = "/qualificationLevels", method = RequestMethod.GET)
    public ModelAndView listOfQualificationLevels() {
        ModelAndView modelAndView = new ModelAndView("qualificationLevel/qualificationLevelList");

        modelAndView.addObject("qualificationlevellist", qualificationLevelService.listOfQualificationLevels());
        return modelAndView;
    }

}