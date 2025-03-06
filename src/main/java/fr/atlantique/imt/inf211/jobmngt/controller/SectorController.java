package fr.atlantique.imt.inf211.jobmngt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;

import fr.atlantique.imt.inf211.jobmngt.service.SectorService;

@Controller
public class SectorController {

    @Autowired
    private SectorService sectorService;

    @RequestMapping(value = "/sectors", method = RequestMethod.GET)
    public ModelAndView listOfSectors() {
        ModelAndView modelAndView = new ModelAndView("sector/sectorList");
        modelAndView.addObject("sectorlist", sectorService.listOfSectors());
        return modelAndView;
    }

}