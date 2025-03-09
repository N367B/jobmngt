package fr.atlantique.imt.inf211.jobmngt.entity;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "qualificationlevel", schema = "public", 
       uniqueConstraints = @UniqueConstraint(columnNames = "labelqualification"))
public class QualificationLevel implements java.io.Serializable {

    private int idQualification;
    private String labelQualification;
    private Set<OffreEmploi> offreEmplois = new HashSet<OffreEmploi>(0);
    private Set<Candidature> candidatures = new HashSet<Candidature>(0);

    public QualificationLevel() {
    }

    public QualificationLevel(int idQualification, String labelQualification) {
        this.idQualification = idQualification;
        this.labelQualification = labelQualification;
    }
    
    public QualificationLevel(int idQualification, String labelQualification, Set<OffreEmploi> offreEmplois, Set<Candidature> candidatures) {
       this.idQualification = idQualification;
       this.labelQualification = labelQualification;
       this.offreEmplois = offreEmplois;
       this.candidatures = candidatures;
    }
   
    @Id
    @SequenceGenerator(name = "QUALIFICATIONLEVEL_ID_GENERATOR", sequenceName = "QUALIFICATIONLEVEL_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "QUALIFICATIONLEVEL_ID_GENERATOR")
    @Column(name = "idqualification", unique = true, nullable = false)
    public int getIdQualification() {
        return this.idQualification;
    }
    
    public void setIdQualification(int idQualification) {
        this.idQualification = idQualification;
    }

    @Column(name = "labelqualification", unique = true, nullable = false, length = 50)
    public String getLabelQualification() {
        return this.labelQualification;
    }
    
    public void setLabelQualification(String labelQualification) {
        this.labelQualification = labelQualification;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "qualificationLevel")
    public Set<OffreEmploi> getOffreEmplois() {
        return this.offreEmplois;
    }
    
    public void setOffreEmplois(Set<OffreEmploi> offreEmplois) {
        this.offreEmplois = offreEmplois;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "qualificationLevel")
    public Set<Candidature> getCandidatures() {
        return this.candidatures;
    }
    
    public void setCandidatures(Set<Candidature> candidatures) {
        this.candidatures = candidatures;
    }
}
