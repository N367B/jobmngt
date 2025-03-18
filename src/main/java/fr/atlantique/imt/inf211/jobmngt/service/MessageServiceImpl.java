package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.dao.MessageCandidatureDao;
import fr.atlantique.imt.inf211.jobmngt.dao.MessageOffreDao;
import fr.atlantique.imt.inf211.jobmngt.entity.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Component
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageCandidatureDao messageCandidatureDao;
    
    @Autowired
    private MessageOffreDao messageOffreDao;
    
    @Autowired
    private OffreEmploiService offreEmploiService;
    
    @Autowired
    private CandidatureService candidatureService;
    
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
    public int sendNotificationsForApplication(Candidature candidature, String customMessage) {
        int count = 0;
        // Trouver toutes les offres qui correspondent à cette candidature
        List<OffreEmploi> matchingJobs = offreEmploiService.getMatchingOffres(candidature);
        
        // Pour chaque offre correspondante, envoyer une notification
        for (OffreEmploi job : matchingJobs) {
            sendMessageToCandidature(candidature, job, customMessage);
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
    public int sendNotificationsForJob(OffreEmploi offreEmploi, String customMessage) {
        int count = 0;
        // Trouver toutes les candidatures qui correspondent à cette offre
        List<Candidature> matchingCandidatures = candidatureService.getMatchingCandidatures(offreEmploi);
        
        // Pour chaque candidature correspondante, envoyer une notification
        for (Candidature candidature : matchingCandidatures) {
            sendMessageToOffre(offreEmploi, candidature, customMessage);
            count++;
        }
        
        return count;
    }
    
    @Override
    public List<Object> getAllMessagesForCandidature(Candidature candidature) {
        List<Object> allMessages = new ArrayList<>();
        
        // Ajouter les messages envoyés par le candidat (MessageCandidature)
        List<MessageCandidature> sentMessages = findCandidatureMessagesByCandidature(candidature);
        allMessages.addAll(sentMessages);
        
        // Ajouter les messages reçus par le candidat (MessageOffre)
        List<MessageOffre> receivedMessages = findOffreMessagesByCandidature(candidature);
        allMessages.addAll(receivedMessages);
        
        // Trier par date, du plus récent au plus ancien
        Collections.sort(allMessages, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                Date date1 = getPublicationDate(o1);
                Date date2 = getPublicationDate(o2);
                return date2.compareTo(date1);
            }
        });
        
        return allMessages;
    }
    
    @Override
    public List<Object> getAllMessagesForOffreEmploi(OffreEmploi offreEmploi) {
        List<Object> allMessages = new ArrayList<>();
        
        // Ajouter les messages envoyés par l'entreprise (MessageOffre)
        List<MessageOffre> sentMessages = findOffreMessagesByOffreEmploi(offreEmploi);
        allMessages.addAll(sentMessages);
        
        // Ajouter les messages reçus par l'entreprise (MessageCandidature)
        List<MessageCandidature> receivedMessages = findCandidatureMessagesByOffreEmploi(offreEmploi);
        allMessages.addAll(receivedMessages);
        
        // Trier par date, du plus récent au plus ancien
        Collections.sort(allMessages, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                Date date1 = getPublicationDate(o1);
                Date date2 = getPublicationDate(o2);
                return date2.compareTo(date1);
            }
        });
        
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
    
    // Méthode utilitaire pour récupérer la date de publication d'un message
    private Date getPublicationDate(Object messageObject) {
        if (messageObject instanceof MessageCandidature) {
            return ((MessageCandidature) messageObject).getPublicationDate();
        } else if (messageObject instanceof MessageOffre) {
            return ((MessageOffre) messageObject).getPublicationDate();
        }
        return new Date(0); // Date par défaut si le type n'est pas reconnu
    }
}