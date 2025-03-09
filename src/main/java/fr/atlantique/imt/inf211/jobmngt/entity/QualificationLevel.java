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

    private int idqualification;
    private String labelqualification;
    private Set<OffreEmploi> offreemplois = new HashSet<OffreEmploi>(0);
    private Set<Candidature> candidatures = new HashSet<Candidature>(0);

    public QualificationLevel() {
    }

    public QualificationLevel(int idqualification, String labelqualification) {
        this.idqualification = idqualification;
        this.labelqualification = labelqualification;
    }
    
    public QualificationLevel(int idqualification, String labelqualification, Set<OffreEmploi> offreemplois, Set<Candidature> candidatures) {
       this.idqualification = idqualification;
       this.labelqualification = labelqualification;
       this.offreemplois = offreemplois;
       this.candidatures = candidatures;
    }
   
    @Id
    @SequenceGenerator(name = "QUALIFICATIONLEVEL_ID_GENERATOR", sequenceName = "QUALIFICATIONLEVEL_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "QUALIFICATIONLEVEL_ID_GENERATOR")
    @Column(name = "idqualification", unique = true, nullable = false)
    public int getIdqualification() {
        return this.idqualification;
    }
    
    public void setIdqualification(int idqualification) {
        this.idqualification = idqualification;
    }

    @Column(name = "labelqualification", unique = true, nullable = false, length = 50)
    public String getLabelqualification() {
        return this.labelqualification;
    }
    
    public void setLabelqualification(String labelqualification) {
        this.labelqualification = labelqualification;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "qualificationLevel")
    public Set<OffreEmploi> getOffreemplois() {
        return this.offreemplois;
    }
    
    public void setOffreemplois(Set<OffreEmploi> offreemplois) {
        this.offreemplois = offreemplois;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "qualificationLevel")
    public Set<Candidature> getCandidatures() {
        return this.candidatures;
    }
    
    public void setCandidatures(Set<Candidature> candidatures) {
        this.candidatures = candidatures;
    }
}
