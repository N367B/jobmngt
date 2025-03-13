package fr.atlantique.imt.inf211.jobmngt.dto.candidature;

import fr.atlantique.imt.inf211.jobmngt.entity.Candidature;
import lombok.Data;

import java.util.Date;

/**
 * DTO simplifié pour une candidature (utilisé dans les listes imbriquées)
 */
@Data
public class CandidatureSimpleDTO {
    private int idCandidature;
    private String cv;
    private String qualificationLevel;
    private Date appDate;
    private String urlDetails;
    
    public static CandidatureSimpleDTO fromCandidature(Candidature candidature) {
        CandidatureSimpleDTO dto = new CandidatureSimpleDTO();
        dto.setIdCandidature(candidature.getIdCandidature());
        dto.setCv(candidature.getCv());
        dto.setQualificationLevel(candidature.getQualificationLevel().getLabelQualification());
        dto.setAppDate(candidature.getAppDate());
        dto.setUrlDetails("/api/candidatures/" + candidature.getIdCandidature());
        return dto;
    }
}