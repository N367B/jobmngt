package fr.atlantique.imt.inf211.jobmngt.dto.offre;

import fr.atlantique.imt.inf211.jobmngt.dto.common.SectorDTO;
import fr.atlantique.imt.inf211.jobmngt.entity.OffreEmploi;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO pour les détails d'une offre d'emploi
 */
@Data
public class OffreEmploiDetailDTO {
    private int idOffreEmploi;
    private String title;
    private String taskDescription;
    private String entrepriseNom;
    private int entrepriseId;
    private List<SectorDTO> sectors;
    private String qualificationLevel;
    private Date publicationDate;
    private String entrepriseUrl;
    
    public static OffreEmploiDetailDTO fromOffreEmploi(OffreEmploi offre) {
        OffreEmploiDetailDTO dto = new OffreEmploiDetailDTO();
        dto.setIdOffreEmploi(offre.getIdOffreEmploi());
        dto.setTitle(offre.getTitle());
        dto.setTaskDescription(offre.getTaskDescription());
        dto.setEntrepriseNom(offre.getEntreprise().getDenomination());
        dto.setEntrepriseId(offre.getEntreprise().getIdEntreprise());
        dto.setEntrepriseUrl("/api/entreprises/" + offre.getEntreprise().getIdEntreprise());
        
        // Conversion des secteurs
        if (offre.getSectors() != null) {
            dto.setSectors(offre.getSectors().stream()
                .map(SectorDTO::fromSector)
                .collect(Collectors.toList()));
        }
        
        dto.setQualificationLevel(offre.getQualificationLevel().getLabelQualification());
        dto.setPublicationDate(offre.getPublicationDate());
        return dto;
    }
}