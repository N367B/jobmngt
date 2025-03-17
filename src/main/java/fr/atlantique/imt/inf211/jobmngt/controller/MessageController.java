package fr.atlantique.imt.inf211.jobmngt.controller;

import fr.atlantique.imt.inf211.jobmngt.entity.Candidat;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidature;
import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;
import fr.atlantique.imt.inf211.jobmngt.entity.OffreEmploi;
import fr.atlantique.imt.inf211.jobmngt.entity.MessageCandidature;
import fr.atlantique.imt.inf211.jobmngt.entity.MessageOffre;
import fr.atlantique.imt.inf211.jobmngt.service.CandidatService;
import fr.atlantique.imt.inf211.jobmngt.service.CandidatureService;
import fr.atlantique.imt.inf211.jobmngt.service.EntrepriseService;
import fr.atlantique.imt.inf211.jobmngt.service.OffreEmploiService;
import fr.atlantique.imt.inf211.jobmngt.service.AuthenticationService;
import fr.atlantique.imt.inf211.jobmngt.service.MessageService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private CandidatureService candidatureService;
    
    @Autowired
    private OffreEmploiService offreEmploiService;
    
    @Autowired
    private CandidatService candidatService;
    
    @Autowired
    private EntrepriseService entrepriseService;
    
    @Autowired
    private AuthenticationService authService;
    
    /**
     * Liste tous les messages pour l'utilisateur connecté
     */
    @GetMapping
    public ModelAndView listMessages(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("message/messageList");
        
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        String userType = (String) session.getAttribute("usertype");
        
        if (uid == null) {
            return new ModelAndView("redirect:/error/403");
        }
        
        if ("candidat".equals(userType)) {
            Candidat candidat = candidatService.getCandidatById(uid);
            if (candidat != null && candidat.getCandidatures() != null) {
                modelAndView.addObject("candidat", candidat);
                modelAndView.addObject("isCandidat", true);
                // Pour chaque candidature, récupérer les messages associés
                for (Candidature candidature : candidat.getCandidatures()) {
                    List<MessageOffre> receivedMessages = messageService.findOffreMessagesByCandidature(candidature);
                    List<MessageCandidature> sentMessages = messageService.findCandidatureMessagesByCandidature(candidature);
                    modelAndView.addObject("receivedMessages_" + candidature.getIdCandidature(), receivedMessages);
                    modelAndView.addObject("sentMessages_" + candidature.getIdCandidature(), sentMessages);
                }
            }
        } else if ("entreprise".equals(userType)) {
            Entreprise entreprise = entrepriseService.getEntrepriseById(uid);
            if (entreprise != null && entreprise.getOffreEmplois() != null) {
                modelAndView.addObject("entreprise", entreprise);
                modelAndView.addObject("isEntreprise", true);
                // Pour chaque offre, récupérer les messages associés
                for (OffreEmploi offre : entreprise.getOffreEmplois()) {
                    List<MessageCandidature> receivedMessages = messageService.findCandidatureMessagesByOffreEmploi(offre);
                    List<MessageOffre> sentMessages = messageService.findOffreMessagesByOffreEmploi(offre);
                    modelAndView.addObject("receivedMessages_" + offre.getIdOffreEmploi(), receivedMessages);
                    modelAndView.addObject("sentMessages_" + offre.getIdOffreEmploi(), sentMessages);
                }
            }
        } else {
            return new ModelAndView("redirect:/error/403");
        }
        
        return modelAndView;
    }
    
    /**
     * Afficher le formulaire pour envoyer un message à propos d'une offre d'emploi
     */
    @GetMapping("/send/job/{jobId}")
    public ModelAndView sendMessageForJobForm(@PathVariable int jobId, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("message/sendMessage");
        
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        String userType = (String) session.getAttribute("usertype");
        
        OffreEmploi offre = offreEmploiService.getOffreById(jobId);
        if (offre == null) {
            return new ModelAndView("redirect:/jobs");
        }
        
        modelAndView.addObject("offre", offre);
        
        if ("candidat".equals(userType)) {
            // Cas d'un candidat qui veut envoyer un message à une entreprise concernant une offre
            Candidat candidat = candidatService.getCandidatById(uid);
            if (candidat != null && candidat.getCandidatures() != null && !candidat.getCandidatures().isEmpty()) {
                modelAndView.addObject("candidat", candidat);
                modelAndView.addObject("isCandidat", true);
                modelAndView.addObject("candidatures", candidat.getCandidatures());
                return modelAndView;
            }
        } else if ("entreprise".equals(userType)) {
            // Cas d'une entreprise qui veut envoyer un message aux candidats qui correspondent à une offre
            Entreprise entreprise = entrepriseService.getEntrepriseById(uid);
            if (entreprise != null && offre.getEntreprise().getIdEntreprise() == entreprise.getIdEntreprise()) {
                List<Candidature> matchingCandidatures = candidatureService.getMatchingCandidatures(offre);
                
                modelAndView.addObject("entreprise", entreprise);
                modelAndView.addObject("isEntreprise", true);
                modelAndView.addObject("matchingCandidatures", matchingCandidatures);
                return modelAndView;
            }
        }
        
        return new ModelAndView("redirect:/error/403");
    }
    
    /**
     * Traiter l'envoi d'un message à propos d'une offre d'emploi
     */
    @PostMapping("/send/job/{jobId}")
    public ModelAndView sendMessageForJob(
            @PathVariable int jobId,
            @RequestParam("candidatureId") Integer candidatureId,
            @RequestParam("message") String message,
            HttpServletRequest request) {
        
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        String userType = (String) session.getAttribute("usertype");
        
        OffreEmploi offre = offreEmploiService.getOffreById(jobId);
        Candidature candidature = candidatureService.getCandidatureById(candidatureId);
        
        if (offre == null || candidature == null) {
            return new ModelAndView("redirect:/jobs");
        }
        
        if ("candidat".equals(userType)) {
            // Vérifier que le candidat est bien le propriétaire de la candidature
            if (!authService.checkCandidatAccess(session, candidature.getCandidat().getIdCandidat())) {
                return new ModelAndView("redirect:/error/403");
            }
            
            // Envoi message candidat vers entreprise
            MessageCandidature messageCandidature = messageService.sendMessageToCandidature(candidature, offre, message);
            return new ModelAndView("redirect:/messages?sent=true&id=" + messageCandidature.getIdMessageCandidature());
        } else if ("entreprise".equals(userType)) {
            // Vérifier que l'entreprise est bien la propriétaire de l'offre
            if (!authService.checkEntrepriseAccess(session, offre.getEntreprise().getIdEntreprise())) {
                return new ModelAndView("redirect:/error/403");
            }
            
            // Envoi message entreprise vers candidat
            MessageOffre messageOffre = messageService.sendMessageToOffre(offre, candidature, message);
            return new ModelAndView("redirect:/messages?sent=true&id=" + messageOffre.getIdMessageOffre());
        }
        
        return new ModelAndView("redirect:/error/403");
    }
    
    /**
     * Afficher le formulaire pour envoyer un message à propos d'une candidature
     */
    @GetMapping("/send/application/{applicationId}")
    public ModelAndView sendMessageForApplicationForm(@PathVariable int applicationId, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("message/sendMessage");
        
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        String userType = (String) session.getAttribute("usertype");
        
        Candidature candidature = candidatureService.getCandidatureById(applicationId);
        if (candidature == null) {
            return new ModelAndView("redirect:/applications");
        }
        
        modelAndView.addObject("candidature", candidature);
        
        if ("candidat".equals(userType)) {
            // Vérifier que le candidat est bien le propriétaire de la candidature
            if (!authService.checkCandidatAccess(session, candidature.getCandidat().getIdCandidat())) {
                return new ModelAndView("redirect:/error/403");
            }
            
            // Le candidat peut envoyer des messages concernant sa candidature à des offres correspondantes
            List<OffreEmploi> matchingOffres = offreEmploiService.getMatchingOffres(candidature);
            modelAndView.addObject("candidat", candidature.getCandidat());
            modelAndView.addObject("isCandidat", true);
            modelAndView.addObject("matchingOffres", matchingOffres);
            return modelAndView;
        } else if ("entreprise".equals(userType)) {
            // Une entreprise peut consulter une candidature et envoyer un message si elle est liée à l'une de ses offres
            Entreprise entreprise = entrepriseService.getEntrepriseById(uid);
            if (entreprise != null) {
                List<OffreEmploi> entrepriseOffres = offreEmploiService.findByEntreprise(entreprise);
                boolean hasMatchingOffer = false;
                for (OffreEmploi offre : entrepriseOffres) {
                    if (candidatureService.isMatchingOffreEmploi(candidature, offre)) {
                        hasMatchingOffer = true;
                        break;
                    }
                }
                
                if (hasMatchingOffer) {
                    modelAndView.addObject("entreprise", entreprise);
                    modelAndView.addObject("isEntreprise", true);
                    modelAndView.addObject("entrepriseOffres", entrepriseOffres);
                    return modelAndView;
                }
            }
        }
        
        return new ModelAndView("redirect:/error/403");
    }
    
    /**
     * Traiter l'envoi d'un message à propos d'une candidature
     */
    @PostMapping("/send/application/{applicationId}")
    public ModelAndView sendMessageForApplication(
            @PathVariable int applicationId,
            @RequestParam("offreId") Integer offreId,
            @RequestParam("message") String message,
            HttpServletRequest request) {
        
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        String userType = (String) session.getAttribute("usertype");
        
        Candidature candidature = candidatureService.getCandidatureById(applicationId);
        OffreEmploi offre = offreEmploiService.getOffreById(offreId);
        
        if (offre == null || candidature == null) {
            return new ModelAndView("redirect:/applications");
        }
        
        if ("candidat".equals(userType)) {
            // Vérifier que le candidat est bien le propriétaire de la candidature
            if (!authService.checkCandidatAccess(session, candidature.getCandidat().getIdCandidat())) {
                return new ModelAndView("redirect:/error/403");
            }
            
            // Envoi message candidat vers entreprise
            MessageCandidature messageCandidature = messageService.sendMessageToCandidature(candidature, offre, message);
            return new ModelAndView("redirect:/messages?sent=true&id=" + messageCandidature.getIdMessageCandidature());
        } else if ("entreprise".equals(userType)) {
            // Vérifier que l'entreprise est bien la propriétaire de l'offre
            if (!authService.checkEntrepriseAccess(session, offre.getEntreprise().getIdEntreprise())) {
                return new ModelAndView("redirect:/error/403");
            }
            
            // Envoi message entreprise vers candidat
            MessageOffre messageOffre = messageService.sendMessageToOffre(offre, candidature, message);
            return new ModelAndView("redirect:/messages?sent=true&id=" + messageOffre.getIdMessageOffre());
        }
        
        return new ModelAndView("redirect:/error/403");
    }
    
    /**
     * Voir les détails d'un message d'une candidature
     */
    @GetMapping("/candidature/{id}")
    public ModelAndView viewCandidatureMessage(@PathVariable int id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("message/messageView");
        
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        String userType = (String) session.getAttribute("usertype");
        
        MessageCandidature message = messageService.getMessageCandidatureById(id);
        if (message == null) {
            return new ModelAndView("redirect:/messages");
        }
        
        // Vérifier que l'utilisateur a le droit de voir ce message
        if ("candidat".equals(userType)) {
            if (!authService.checkCandidatAccess(session, message.getCandidature().getCandidat().getIdCandidat())) {
                return new ModelAndView("redirect:/error/403");
            }
        } else if ("entreprise".equals(userType)) {
            if (message.getOffreEmploi() == null || !authService.checkEntrepriseAccess(session, message.getOffreEmploi().getEntreprise().getIdEntreprise())) {
                return new ModelAndView("redirect:/error/403");
            }
        } else {
            return new ModelAndView("redirect:/error/403");
        }
        
        modelAndView.addObject("message", message);
        modelAndView.addObject("isCandidatureMessage", true);
        return modelAndView;
    }
    
    /**
     * Voir les détails d'un message d'une offre
     */
    @GetMapping("/offre/{id}")
    public ModelAndView viewOffreMessage(@PathVariable int id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("message/messageView");
        
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        String userType = (String) session.getAttribute("usertype");
        
        MessageOffre message = messageService.getMessageOffreById(id);
        if (message == null) {
            return new ModelAndView("redirect:/messages");
        }
        
        // Vérifier que l'utilisateur a le droit de voir ce message
        if ("candidat".equals(userType)) {
            if (!authService.checkCandidatAccess(session, message.getCandidature().getCandidat().getIdCandidat())) {
                return new ModelAndView("redirect:/error/403");
            }
        } else if ("entreprise".equals(userType)) {
            if (message.getOffreEmploi() == null || !authService.checkEntrepriseAccess(session, message.getOffreEmploi().getEntreprise().getIdEntreprise())) {
                return new ModelAndView("redirect:/error/403");
            }
        } else {
            return new ModelAndView("redirect:/error/403");
        }
        
        modelAndView.addObject("message", message);
        modelAndView.addObject("isOffreMessage", true);
        return modelAndView;
    }

    /**
     * Affiche le formulaire pour répondre à un message
     */
    @GetMapping("/reply")
    public ModelAndView showReplyForm(
            @RequestParam("offreId") Integer offreId,
            @RequestParam("candidatureId") Integer candidatureId,
            HttpServletRequest request) {
        
        ModelAndView modelAndView = new ModelAndView("message/sendMessage");
        
        HttpSession session = request.getSession();
        String userType = (String) session.getAttribute("usertype");
        
        Candidature candidature = candidatureService.getCandidatureById(candidatureId);
        OffreEmploi offre = offreEmploiService.getOffreById(offreId);
        
        if (offre == null || candidature == null) {
            return new ModelAndView("redirect:/messages");
        }
        
        modelAndView.addObject("messageReply", true);
        modelAndView.addObject("offre", offre);
        modelAndView.addObject("candidature", candidature);
        
        return modelAndView;
    }
    /**
     * Traiter l'envoi d'une réponse à un message existant
     */
    @PostMapping("/reply")
    public ModelAndView sendReply(
            @RequestParam("offreId") Integer offreId,
            @RequestParam("candidatureId") Integer candidatureId,
            @RequestParam("message") String message,
            HttpServletRequest request) {
        
        HttpSession session = request.getSession();
        String userType = (String) session.getAttribute("usertype");
        
        Candidature candidature = candidatureService.getCandidatureById(candidatureId);
        OffreEmploi offre = offreEmploiService.getOffreById(offreId);
        
        if (offre == null || candidature == null) {
            return new ModelAndView("redirect:/messages");
        }
        
        // Selon le type d'utilisateur, envoyer le message approprié
        if ("candidat".equals(userType)) {
            // Vérifier accès
            if (!authService.checkCandidatAccess(session, candidature.getCandidat().getIdCandidat())) {
                return new ModelAndView("redirect:/error/403");
            }
            
            MessageCandidature messageCandidature = messageService.sendMessageToCandidature(candidature, offre, message);
            return new ModelAndView("redirect:/messages?sent=true&id=" + messageCandidature.getIdMessageCandidature());
        } else if ("entreprise".equals(userType)) {
            // Vérifier accès
            if (!authService.checkEntrepriseAccess(session, offre.getEntreprise().getIdEntreprise())) {
                return new ModelAndView("redirect:/error/403");
            }
            
            MessageOffre messageOffre = messageService.sendMessageToOffre(offre, candidature, message);
            return new ModelAndView("redirect:/messages?sent=true&id=" + messageOffre.getIdMessageOffre());
        }
        
        return new ModelAndView("redirect:/error/403");
    }
}