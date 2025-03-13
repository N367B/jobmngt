package fr.atlantique.imt.inf211.jobmngt.dto.offre;

import fr.atlantique.imt.inf211.jobmngt.dto.common.SectorDTO;
import fr.atlantique.imt.inf211.jobmngt.entity.OffreEmploi;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO pour la liste des offres d'emploi (vue simplifiée)
 */
@Data
public class OffreEmploiListDTO {
    private int idOffreEmploi;
    private String title;
    private String entrepriseNom;
    private int entrepriseId;
    private List<SectorDTO> sectors;
    private String qualificationLevel;
    private Date publicationDate;
    private String urlDetails;
    
    public static OffreEmploiListDTO fromOffreEmploi(OffreEmploi offre) {
        OffreEmploiListDTO dto = new OffreEmploiListDTO();
        dto.setIdOffreEmploi(offre.getIdOffreEmploi());
        dto.setTitle(offre.getTitle());
        dto.setEntrepriseNom(offre.getEntreprise().getDenomination());
        dto.setEntrepriseId(offre.getEntreprise().getIdEntreprise());
        
        // Conversion des secteurs
        if (offre.getSectors() != null) {
            dto.setSectors(offre.getSectors().stream()
                .map(SectorDTO::fromSector)
                .collect(Collectors.toList()));
        }
        
        dto.setQualificationLevel(offre.getQualificationLevel().getLabelQualification());
        dto.setPublicationDate(offre.getPublicationDate());
        dto.setUrlDetails("/api/offres/" + offre.getIdOffreEmploi());
        return dto;
    }
}