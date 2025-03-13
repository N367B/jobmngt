package fr.atlantique.imt.inf211.jobmngt.dto.common;

import fr.atlantique.imt.inf211.jobmngt.entity.Sector;
import lombok.Data;

/**
 * DTO pour les secteurs d'activité
 */
@Data
public class SectorDTO {
    private int idSecteur;
    private String labelSecteur;
    
    public static SectorDTO fromSector(Sector sector) {
        SectorDTO dto = new SectorDTO();
        dto.setIdSecteur(sector.getIdSecteur());
        dto.setLabelSecteur(sector.getLabelSecteur());
        return dto;
    }
}