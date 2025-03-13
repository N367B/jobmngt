package fr.atlantique.imt.inf211.jobmngt.dto.candidature;

import fr.atlantique.imt.inf211.jobmngt.dto.common.SectorDTO;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidature;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO pour les détails d'une candidature
 */
@Data
public class CandidatureDetailDTO {
    private int idCandidature;
    private String nomCandidat;
    private String prenomCandidat;
    private String villeCandidat;
    private String emailCandidat;
    private String cv;
    private List<SectorDTO> sectors;
    private String qualificationLevel;
    private Date appDate;
    private String candidatUrl;
    
    public static CandidatureDetailDTO fromCandidature(Candidature candidature) {
        CandidatureDetailDTO dto = new CandidatureDetailDTO();
        dto.setIdCandidature(candidature.getIdCandidature());
        dto.setNomCandidat(candidature.getCandidat().getLastName());
        dto.setPrenomCandidat(candidature.getCandidat().getFirstName());
        dto.setVilleCandidat(candidature.getCandidat().getAppUser().getCity());
        dto.setEmailCandidat(candidature.getCandidat().getAppUser().getMail());
        dto.setCv(candidature.getCv());
        
        // Conversion des secteurs
        if (candidature.getSectors() != null) {
            dto.setSectors(candidature.getSectors().stream()
                .map(SectorDTO::fromSector)
                .collect(Collectors.toList()));
        }
        
        dto.setQualificationLevel(candidature.getQualificationLevel().getLabelQualification());
        dto.setAppDate(candidature.getAppDate());
        dto.setCandidatUrl("/api/candidats/" + candidature.getCandidat().getIdCandidat());
        return dto;
    }
}