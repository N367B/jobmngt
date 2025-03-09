package fr.atlantique.imt.inf211.jobmngt.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class,
  property = "idSecteur")
@Entity
@Table(name = "sector", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = "labelsecteur"))
public class Sector implements java.io.Serializable {

    private int idSecteur;
    private String labelSecteur;
    private Set<OffreEmploi> offreEmplois = new HashSet<OffreEmploi>(0);
    private Set<Candidature> candidatures = new HashSet<Candidature>(0);

    public Sector() {
    }

    public Sector(int idSecteur, String labelSecteur) {
        this.idSecteur = idSecteur;
        this.labelSecteur = labelSecteur;
    }

    public Sector(int idSecteur, String labelSecteur, Set<OffreEmploi> offreEmplois, Set<Candidature> candidatures) {
       this.idSecteur = idSecteur;
       this.labelSecteur = labelSecteur;
       this.offreEmplois = offreEmplois;
       this.candidatures = candidatures;
    }
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idsecteur", unique = true, nullable = false)
    public int getIdSecteur() {
        return this.idSecteur;
    }

    public void setIdSecteur(int idSecteur) {
        this.idSecteur = idSecteur;
    }


    @Column(name = "labelsecteur", unique = true, nullable = false, length = 50)
    public String getLabelSecteur() {
        return this.labelSecteur;
    }
    
    public void setLabelSecteur(String labelSecteur) {
        this.labelSecteur = labelSecteur;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "sectors")
    public Set<OffreEmploi> getOffreEmplois() {
        return this.offreEmplois;
    }
    
    public void setOffreEmplois(Set<OffreEmploi> offreEmplois) {
        this.offreEmplois = offreEmplois;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "sectors")
    public Set<Candidature> getCandidatures() {
        return this.candidatures;
    }
    
    public void setCandidatures(Set<Candidature> candidatures) {
        this.candidatures = candidatures;
    }
}
