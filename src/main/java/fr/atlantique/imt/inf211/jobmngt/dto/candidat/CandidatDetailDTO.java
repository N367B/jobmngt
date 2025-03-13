package fr.atlantique.imt.inf211.jobmngt.dto.candidat;

import fr.atlantique.imt.inf211.jobmngt.dto.candidature.CandidatureSimpleDTO;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidat;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO pour les détails d'un candidat
 */
@Data
public class CandidatDetailDTO {
    private int idCandidat;
    private String nom;
    private String prenom;
    private String ville;
    private String email;
    private List<CandidatureSimpleDTO> candidatures;
    
    public static CandidatDetailDTO fromCandidat(Candidat candidat) {
        CandidatDetailDTO dto = new CandidatDetailDTO();
        dto.setIdCandidat(candidat.getIdCandidat());
        dto.setNom(candidat.getLastName());
        dto.setPrenom(candidat.getFirstName());
        dto.setVille(candidat.getAppUser().getCity());
        dto.setEmail(candidat.getAppUser().getMail());
        
        // Transformation des candidatures en DTOs simplifiés
        if (candidat.getCandidatures() != null) {
            dto.setCandidatures(candidat.getCandidatures().stream()
                .map(CandidatureSimpleDTO::fromCandidature)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
}