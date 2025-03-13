package fr.atlantique.imt.inf211.jobmngt.dto.candidat;

import fr.atlantique.imt.inf211.jobmngt.entity.Candidat;
import lombok.Data;

/**
 * DTO pour la liste des candidats (vue simplifiée)
 */
@Data
public class CandidatListDTO {
    private int idCandidat;
    private String nom;
    private String prenom;
    private String ville;
    private int nombreCandidatures;
    private String urlDetails;
    
    public static CandidatListDTO fromCandidat(Candidat candidat) {
        CandidatListDTO dto = new CandidatListDTO();
        dto.setIdCandidat(candidat.getIdCandidat());
        dto.setNom(candidat.getLastName());
        dto.setPrenom(candidat.getFirstName());
        dto.setVille(candidat.getAppUser().getCity());
        dto.setNombreCandidatures(candidat.getCandidatures() != null ? candidat.getCandidatures().size() : 0);
        dto.setUrlDetails("/api/candidats/" + candidat.getIdCandidat());
        return dto;
    }
}