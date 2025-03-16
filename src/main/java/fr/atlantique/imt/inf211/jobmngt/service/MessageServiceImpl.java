package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.dao.MessageCandidatureDao;
import fr.atlantique.imt.inf211.jobmngt.dao.MessageOffreDao;
import fr.atlantique.imt.inf211.jobmngt.dao.CandidatureDao;
import fr.atlantique.imt.inf211.jobmngt.dao.OffreEmploiDao;
import fr.atlantique.imt.inf211.jobmngt.entity.*;
import fr.atlantique.imt.inf211.jobmngt.dto.common.MessageDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageCandidatureDao messageCandidatureDao;
    
    @Autowired
    private MessageOffreDao messageOffreDao;
    
    @Autowired
    private CandidatureDao candidatureDao;
    
    @Autowired
    private OffreEmploiDao offreEmploiDao;
    
    @Override
    public MessageCandidature sendMessageToCandidature(Candidature candidature, OffreEmploi offreEmploi, String message) {
        MessageCandidature messageCandidature = new MessageCandidature();
        messageCandidature.setCandidature(candidature);
        messageCandidature.setOffreEmploi(offreEmploi);
        messageCandidature.setMessage(message);
        messageCandidature.setPublicationDate(new Date());
        
        messageCandidatureDao.persist(messageCandidature);
        return messageCandidature;
    }

    @Override
    public List<MessageCandidature> findCandidatureMessagesByCandidature(Candidature candidature) {
        return messageCandidatureDao.findByCandidature(candidature);
    }

    @Override
    public List<MessageCandidature> findCandidatureMessagesByOffreEmploi(OffreEmploi offreEmploi) {
        return messageCandidatureDao.findByOffreEmploi(offreEmploi);
    }
    
    @Override
    public int sendNotificationsForApplication(Candidature candidature) {
        int count = 0;
        // Trouver toutes les offres qui correspondent à cette candidature
        List<OffreEmploi> matchingJobs = offreEmploiDao.findMatchingCandidature(candidature);
        
        // Pour chaque offre correspondante, envoyer une notification
        for (OffreEmploi job : matchingJobs) {
            String message = "Une nouvelle candidature correspondant à votre offre a été publiée. " +
                     "Candidature ID: " + candidature.getIdCandidature() +
                     ", Profil: " + candidature.getQualificationLevel().getLabelQualification() +
                     ", Secteurs: " + candidature.getSectors().stream()
                                      .map(Sector::getLabelSecteur)
                                      .collect(Collectors.joining(", "));
                                      
            sendMessageToCandidature(candidature, job, message);
            count++;
        }
        
        return count;
    }

    @Override
    public MessageOffre sendMessageToOffre(OffreEmploi offreEmploi, Candidature candidature, String message) {
        MessageOffre messageOffre = new MessageOffre();
        messageOffre.setOffreEmploi(offreEmploi);
        messageOffre.setCandidature(candidature);
        messageOffre.setMessage(message);
        messageOffre.setPublicationDate(new Date());
        
        messageOffreDao.persist(messageOffre);
        return messageOffre;
    }

    @Override
    public List<MessageOffre> findOffreMessagesByCandidature(Candidature candidature) {
        return messageOffreDao.findByCandidature(candidature);
    }

    @Override
    public List<MessageOffre> findOffreMessagesByOffreEmploi(OffreEmploi offreEmploi) {
        return messageOffreDao.findByOffreEmploi(offreEmploi);
    }
    
    @Override
    public int sendNotificationsForJob(OffreEmploi offreEmploi) {
        int count = 0;
        // Trouver toutes les candidatures qui correspondent à cette offre
        List<Candidature> matchingCandidatures = candidatureDao.findMatchingOffreEmploi(offreEmploi);
        
        // Pour chaque candidature correspondante, envoyer une notification
        for (Candidature candidature : matchingCandidatures) {
            String message = "Une nouvelle offre d'emploi correspondant à votre profil a été publiée. " +
                     "Offre ID: " + offreEmploi.getIdOffreEmploi() +
                     ", Titre: " + offreEmploi.getTitle() +
                     ", Entreprise: " + offreEmploi.getEntreprise().getDenomination();
                     
            sendMessageToOffre(offreEmploi, candidature, message);
            count++;
        }
        
        return count;
    }
    
    @Override
    public List<MessageDTO> getAllMessagesForCandidature(Candidature candidature) {
        List<MessageDTO> allMessages = new ArrayList<>();
        
        // Ajouter les messages envoyés par le candidat (MessageCandidature)
        List<MessageCandidature> sentMessages = findCandidatureMessagesByCandidature(candidature);
        for (MessageCandidature message : sentMessages) {
            allMessages.add(MessageDTO.fromMessageCandidature(message));
        }
        
        // Ajouter les messages reçus par le candidat (MessageOffre)
        List<MessageOffre> receivedMessages = findOffreMessagesByCandidature(candidature);
        for (MessageOffre message : receivedMessages) {
            allMessages.add(MessageDTO.fromMessageOffre(message));
        }
        
        // Trier par date, du plus récent au plus ancien
        allMessages.sort((m1, m2) -> m2.getPublicationDate().compareTo(m1.getPublicationDate()));
        
        return allMessages;
    }
    
    @Override
    public List<MessageDTO> getAllMessagesForOffreEmploi(OffreEmploi offreEmploi) {
        List<MessageDTO> allMessages = new ArrayList<>();
        
        // Ajouter les messages envoyés par l'entreprise (MessageOffre)
        List<MessageOffre> sentMessages = findOffreMessagesByOffreEmploi(offreEmploi);
        for (MessageOffre message : sentMessages) {
            allMessages.add(MessageDTO.fromMessageOffre(message));
        }
        
        // Ajouter les messages reçus par l'entreprise (MessageCandidature)
        List<MessageCandidature> receivedMessages = findCandidatureMessagesByOffreEmploi(offreEmploi);
        for (MessageCandidature message : receivedMessages) {
            allMessages.add(MessageDTO.fromMessageCandidature(message));
        }
        
        // Trier par date, du plus récent au plus ancien
        allMessages.sort((m1, m2) -> m2.getPublicationDate().compareTo(m1.getPublicationDate()));
        
        return allMessages;
    }

    @Override
    public MessageCandidature getMessageCandidatureById(int id) {
        return messageCandidatureDao.findById(id);
    }

    @Override
    public MessageOffre getMessageOffreById(int id) {
        return messageOffreDao.findById(id);
    }

}