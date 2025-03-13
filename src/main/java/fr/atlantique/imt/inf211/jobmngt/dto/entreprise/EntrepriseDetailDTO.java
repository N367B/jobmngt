package fr.atlantique.imt.inf211.jobmngt.dto.entreprise;

import fr.atlantique.imt.inf211.jobmngt.dto.offre.OffreEmploiSimpleDTO;
import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;
import fr.atlantique.imt.inf211.jobmngt.entity.OffreEmploi;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO pour les détails d'une entreprise
 */
@Data
public class EntrepriseDetailDTO {
    private int idEntreprise;
    private String denomination;
    private String description;
    private String ville;
    private String email;
    private List<OffreEmploiSimpleDTO> offres;
    
    public static EntrepriseDetailDTO fromEntreprise(Entreprise entreprise) {
        EntrepriseDetailDTO dto = new EntrepriseDetailDTO();
        dto.setIdEntreprise(entreprise.getIdEntreprise());
        dto.setDenomination(entreprise.getDenomination());
        dto.setDescription(entreprise.getDescription());
        dto.setVille(entreprise.getAppUser().getCity());
        dto.setEmail(entreprise.getAppUser().getMail());
        
        // Conversion des offres d'emploi en DTOs simplifiés
        if (entreprise.getOffreEmplois() != null) {
            dto.setOffres(entreprise.getOffreEmplois().stream()
                .map(OffreEmploiSimpleDTO::fromOffreEmploi)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
}