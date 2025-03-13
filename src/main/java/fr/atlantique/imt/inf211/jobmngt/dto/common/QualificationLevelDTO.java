package fr.atlantique.imt.inf211.jobmngt.dto.common;

import fr.atlantique.imt.inf211.jobmngt.entity.QualificationLevel;
import lombok.Data;

/**
 * DTO pour les niveaux de qualification
 */
@Data
public class QualificationLevelDTO {
    private int idQualification;
    private String labelQualification;
    
    public static QualificationLevelDTO fromQualificationLevel(QualificationLevel qualificationLevel) {
        QualificationLevelDTO dto = new QualificationLevelDTO();
        dto.setIdQualification(qualificationLevel.getIdQualification());
        dto.setLabelQualification(qualificationLevel.getLabelQualification());
        return dto;
    }
}