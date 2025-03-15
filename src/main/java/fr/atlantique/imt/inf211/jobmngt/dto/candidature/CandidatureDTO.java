package fr.atlantique.imt.inf211.jobmngt.dto.candidature;

import fr.atlantique.imt.inf211.jobmngt.entity.Candidature;
import fr.atlantique.imt.inf211.jobmngt.entity.MessageOffre;
import fr.atlantique.imt.inf211.jobmngt.entity.Sector;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CandidatureDTO {
    private Integer idCandidature;
    private String cv;
    private Date appDate;
    private Integer idCandidat;
    private String candidatFirstName;
    private String candidatLastName;
    private String candidatCity;
    private String qualificationLabel;
    private Set<String> sectorLabels;
    private List<MessageOffreDTO> messages;
    private String mail;
    
    public CandidatureDTO(Candidature candidature) {
        this.idCandidature = candidature.getIdCandidature();
        this.cv = candidature.getCv();
        this.appDate = candidature.getAppDate();
        
        if (candidature.getCandidat() != null) {
            this.idCandidat = candidature.getCandidat().getIdCandidat();
            this.candidatFirstName = candidature.getCandidat().getFirstName();
            this.candidatLastName = candidature.getCandidat().getLastName();
            
            if (candidature.getCandidat().getAppUser() != null) {
                this.candidatCity = candidature.getCandidat().getAppUser().getCity();
                this.mail = candidature.getCandidat().getAppUser().getMail();
            }
        }
        
        if (candidature.getQualificationLevel() != null) {
            this.qualificationLabel = candidature.getQualificationLevel().getLabelQualification();
        }
        
        if (candidature.getSectors() != null) {
            this.sectorLabels = candidature.getSectors().stream()
                .map(Sector::getLabelSecteur)
                .collect(Collectors.toSet());
        }
        
        if (candidature.getMessageOffres() != null) {
            this.messages = candidature.getMessageOffres().stream()
                .map(MessageOffreDTO::new)
                .collect(Collectors.toList());
        }
    }
    
    // Getters (pas de setters nécessaires car c'est un DTO en lecture seule)
    public Integer getIdCandidature() { return idCandidature; }
    public String getCv() { return cv; }
    public Date getAppDate() { return appDate; }
    public Integer getIdCandidat() { return idCandidat; }
    public String getCandidatFirstName() { return candidatFirstName; }
    public String getCandidatLastName() { return candidatLastName; }
    public String getCandidatCity() { return candidatCity; }
    public String getQualificationLabel() { return qualificationLabel; }
    public Set<String> getSectorLabels() { return sectorLabels; }
    public List<MessageOffreDTO> getMessages() { return messages; }
    public String getMail() { return mail; }
    
    // DTO imbriqué pour MessageOffre
    public static class MessageOffreDTO {
        private Integer id;
        private String message;
        private Date publicationDate;
        private String offreEmploiTitle;
        private Integer offreEmploiId;
        
        public MessageOffreDTO(MessageOffre messageOffre) {
            this.message = messageOffre.getMessage();
            this.publicationDate = messageOffre.getPublicationDate();
            
            if (messageOffre.getOffreEmploi() != null) {
                this.offreEmploiTitle = messageOffre.getOffreEmploi().getTitle();
                this.offreEmploiId = messageOffre.getOffreEmploi().getIdOffreEmploi();
            }
        }
        
        // Getters
        public Integer getId() { return id; }
        public String getMessage() { return message; }
        public Date getPublicationDate() { return publicationDate; }
        public String getOffreEmploiTitle() { return offreEmploiTitle; }
        public Integer getOffreEmploiId() { return offreEmploiId; }
    }
}