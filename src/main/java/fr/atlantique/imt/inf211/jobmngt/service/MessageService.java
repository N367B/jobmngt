package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.entity.*;
import fr.atlantique.imt.inf211.jobmngt.dto.common.MessageDTO;

import java.util.List;

public interface MessageService {
    
    // Méthodes pour MessageCandidature (messages de candidats vers entreprises)
    MessageCandidature sendMessageToCandidature(Candidature candidature, OffreEmploi offreEmploi, String message);
    List<MessageCandidature> findCandidatureMessagesByCandidature(Candidature candidature);
    List<MessageCandidature> findCandidatureMessagesByOffreEmploi(OffreEmploi offreEmploi);
    int sendNotificationsForApplication(Candidature candidature);
    MessageCandidature getMessageCandidatureById(int id);

    // Dans MessageService.java
int sendNotificationsForApplication(Candidature candidature, String customMessage);
    
    // Méthodes pour MessageOffre (messages d'entreprises vers candidats)
    MessageOffre sendMessageToOffre(OffreEmploi offreEmploi, Candidature candidature, String message);
    List<MessageOffre> findOffreMessagesByCandidature(Candidature candidature);
    List<MessageOffre> findOffreMessagesByOffreEmploi(OffreEmploi offreEmploi);
    int sendNotificationsForJob(OffreEmploi offreEmploi);
    MessageOffre getMessageOffreById(int id);
    
    // Méthodes génériques pour tous les messages (conversion vers DTO)
    List<MessageDTO> getAllMessagesForCandidature(Candidature candidature);
    List<MessageDTO> getAllMessagesForOffreEmploi(OffreEmploi offreEmploi);
}