package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.entity.*;
import java.util.List;

public interface MessageService {
    
    // --- Méthodes pour MessageCandidature (messages de candidats vers entreprises) ---
    
    /**
     * Envoie un message d'un candidat vers une entreprise concernant une candidature
     */
    MessageCandidature sendMessageToCandidature(Candidature candidature, OffreEmploi offreEmploi, String message);
    
    /**
     * Récupère les messages envoyés par un candidat pour une candidature spécifique
     */
    List<MessageCandidature> findCandidatureMessagesByCandidature(Candidature candidature);
    
    /**
     * Récupère les messages concernant une offre d'emploi spécifique
     */
    List<MessageCandidature> findCandidatureMessagesByOffreEmploi(OffreEmploi offreEmploi);
    
    /**
     * Récupère un message par son ID
     */
    MessageCandidature getMessageCandidatureById(int id);
    
    /**
     * Envoie des notifications aux entreprises pour une nouvelle candidature
     */
    int sendNotificationsForApplication(Candidature candidature, String customMessage);
    
    // --- Méthodes pour MessageOffre (messages d'entreprises vers candidats) ---
    
    /**
     * Envoie un message d'une entreprise vers un candidat concernant une offre
     */
    MessageOffre sendMessageToOffre(OffreEmploi offreEmploi, Candidature candidature, String message);
    
    /**
     * Récupère les messages reçus par un candidat pour une candidature spécifique
     */
    List<MessageOffre> findOffreMessagesByCandidature(Candidature candidature);
    
    /**
     * Récupère les messages envoyés par une entreprise pour une offre spécifique
     */
    List<MessageOffre> findOffreMessagesByOffreEmploi(OffreEmploi offreEmploi);
    
    /**
     * Récupère un message par son ID
     */
    MessageOffre getMessageOffreById(int id);
    
    /**
     * Envoie des notifications aux candidats pour une nouvelle offre d'emploi
     */
    int sendNotificationsForJob(OffreEmploi offreEmploi, String customMessage);
        
    /**
     * Récupère tous les messages associés à une candidature (envoyés et reçus)
     */
    List<Object> getAllMessagesForCandidature(Candidature candidature);
    
    /**
     * Récupère tous les messages associés à une offre d'emploi (envoyés et reçus)
     */
    List<Object> getAllMessagesForOffreEmploi(OffreEmploi offreEmploi);
}