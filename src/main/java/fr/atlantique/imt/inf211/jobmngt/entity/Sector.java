package fr.atlantique.imt.inf211.jobmngt.entity;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "sector", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = "labelsecteur"))
public class Sector implements java.io.Serializable {

    private int idsecteur;
    private String labelsecteur;
    private Set<OffreEmploi> offreemplois = new HashSet<OffreEmploi>(0);
    private Set<Candidature> candidatures = new HashSet<Candidature>(0);

    public Sector() {
    }

    public Sector(int idsecteur, String labelsecteur) {
        this.idsecteur = idsecteur;
        this.labelsecteur = labelsecteur;
    }

    public Sector(int idsecteur, String labelsecteur, Set<OffreEmploi> offreemplois, Set<Candidature> candidatures) {
       this.idsecteur = idsecteur;
       this.labelsecteur = labelsecteur;
       this.offreemplois = offreemplois;
       this.candidatures = candidatures;
    }
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idsecteur", unique = true, nullable = false)
    public int getIdsecteur() {
        return this.idsecteur;
    }
    
    public void setIdsecteur(int idsecteur) {
        this.idsecteur = idsecteur;
    }

    @Column(name = "labelsecteur", unique = true, nullable = false, length = 50)
    public String getLabelsecteur() {
        return this.labelsecteur;
    }
    
    public void setLabelsecteur(String labelsecteur) {
        this.labelsecteur = labelsecteur;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "sectors")
    public Set<OffreEmploi> getOffreemplois() {
        return this.offreemplois;
    }
    
    public void setOffreemplois(Set<OffreEmploi> offreemplois) {
        this.offreemplois = offreemplois;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "sectors")
    public Set<Candidature> getCandidatures() {
        return this.candidatures;
    }
    
    public void setCandidatures(Set<Candidature> candidatures) {
        this.candidatures = candidatures;
    }
}
