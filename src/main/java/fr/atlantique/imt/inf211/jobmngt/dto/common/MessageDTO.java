package fr.atlantique.imt.inf211.jobmngt.dto.common;

import fr.atlantique.imt.inf211.jobmngt.entity.MessageCandidature;
import fr.atlantique.imt.inf211.jobmngt.entity.MessageOffre;
import lombok.Data;

import java.util.Date;

/**
 * DTO pour les messages
 */
@Data
public class MessageDTO {
    private int idMessage;
    private int idOffreEmploi;
    private int idCandidature;
    private String message;
    private Date publicationDate;
    private String expediteur;
    private String destinataire;
    
    public static MessageDTO fromMessageCandidature(MessageCandidature message) {
        MessageDTO dto = new MessageDTO();
        dto.setIdMessage(message.getIdMessageCandidature());
        dto.setIdOffreEmploi(message.getOffreEmploi().getIdOffreEmploi());
        dto.setIdCandidature(message.getCandidature().getIdCandidature());
        dto.setMessage(message.getMessage());
        dto.setPublicationDate(message.getPublicationDate());
        dto.setExpediteur(message.getCandidature().getCandidat().getAppUser().getMail());
        dto.setDestinataire(message.getOffreEmploi().getEntreprise().getAppUser().getMail());
        return dto;
    }
    
    public static MessageDTO fromMessageOffre(MessageOffre message) {
        MessageDTO dto = new MessageDTO();
        dto.setIdMessage(message.getIdMessageOffre());
        dto.setIdOffreEmploi(message.getOffreEmploi().getIdOffreEmploi());
        dto.setIdCandidature(message.getCandidature().getIdCandidature());
        dto.setMessage(message.getMessage());
        dto.setPublicationDate(message.getPublicationDate());
        dto.setExpediteur(message.getOffreEmploi().getEntreprise().getAppUser().getMail());
        dto.setDestinataire(message.getCandidature().getCandidat().getAppUser().getMail());
        return dto;
    }
}