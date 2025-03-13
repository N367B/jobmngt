package fr.atlantique.imt.inf211.jobmngt.dto.offre;

import fr.atlantique.imt.inf211.jobmngt.entity.OffreEmploi;
import lombok.Data;

import java.util.Date;

/**
 * DTO simplifié pour une offre d'emploi (utilisé dans les listes imbriquées)
 */
@Data
public class OffreEmploiSimpleDTO {
    private int idOffreEmploi;
    private String title;
    private String qualificationLevel;
    private Date publicationDate;
    private String urlDetails;
    
    public static OffreEmploiSimpleDTO fromOffreEmploi(OffreEmploi offre) {
        OffreEmploiSimpleDTO dto = new OffreEmploiSimpleDTO();
        dto.setIdOffreEmploi(offre.getIdOffreEmploi());
        dto.setTitle(offre.getTitle());
        dto.setQualificationLevel(offre.getQualificationLevel().getLabelQualification());
        dto.setPublicationDate(offre.getPublicationDate());
        dto.setUrlDetails("/api/offres/" + offre.getIdOffreEmploi());
        return dto;
    }
}