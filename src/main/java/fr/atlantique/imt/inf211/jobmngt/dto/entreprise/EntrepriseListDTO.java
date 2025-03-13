package fr.atlantique.imt.inf211.jobmngt.dto.entreprise;

import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;
import lombok.Data;

/**
 * DTO pour la liste des entreprises (vue simplifiée)
 */
@Data
public class EntrepriseListDTO {
    private int idEntreprise;
    private String denomination;
    private String ville;
    private String description;
    private int nombreOffres;
    private String urlDetails;
    
    public static EntrepriseListDTO fromEntreprise(Entreprise entreprise) {
        EntrepriseListDTO dto = new EntrepriseListDTO();
        dto.setIdEntreprise(entreprise.getIdEntreprise());
        dto.setDenomination(entreprise.getDenomination());
        dto.setVille(entreprise.getAppUser().getCity());
        dto.setDescription(entreprise.getDescription());
        dto.setNombreOffres(entreprise.getOffreEmplois() != null ? entreprise.getOffreEmplois().size() : 0);
        dto.setUrlDetails("/api/entreprises/" + entreprise.getIdEntreprise());
        return dto;
    }
}