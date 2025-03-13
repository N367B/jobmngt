package fr.atlantique.imt.inf211.jobmngt.dto.candidature;

import fr.atlantique.imt.inf211.jobmngt.dto.common.SectorDTO;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidature;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO pour la liste des candidatures (vue simplifiée)
 */
@Data
public class CandidatureListDTO {
    private int idCandidature;
    private String nomCandidat;
    private String prenomCandidat;
    private List<SectorDTO> sectors;
    private String qualificationLevel;
    private Date appDate;
    private String urlDetails;
    
    public static CandidatureListDTO fromCandidature(Candidature candidature) {
        CandidatureListDTO dto = new CandidatureListDTO();
        dto.setIdCandidature(candidature.getIdCandidature());
        dto.setNomCandidat(candidature.getCandidat().getLastName());
        dto.setPrenomCandidat(candidature.getCandidat().getFirstName());
        
        // Conversion des secteurs
        if (candidature.getSectors() != null) {
            dto.setSectors(candidature.getSectors().stream()
                .map(SectorDTO::fromSector)
                .collect(Collectors.toList()));
        }
        
        dto.setQualificationLevel(candidature.getQualificationLevel().getLabelQualification());
        dto.setAppDate(candidature.getAppDate());
        dto.setUrlDetails("/api/candidatures/" + candidature.getIdCandidature());
        return dto;
    }
}