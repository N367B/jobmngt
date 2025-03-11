package fr.atlantique.imt.inf211.jobmngt.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class,
  property = "idUser")
@Entity
@Table(name = "appuser", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = "mail"))
public class AppUser implements java.io.Serializable {

    private int idUser;
    private String mail;
    private String password;
    private String userType;
    private String city;
    private Candidat candidat;
    private Entreprise entreprise;


    public AppUser() {
    }

    public AppUser(int idUser, String mail, String password, String userType, String city) {
        this.idUser = idUser;
        this.mail = mail;
        this.password = password;
        this.userType = userType;
        this.city = city;
    }

    public AppUser(int idUser, String mail, String password, String userType, String city
    , Candidat candidat, Entreprise entreprise
    ) {
        this.idUser = idUser;
        this.mail = mail;
        this.password = password;
        this.userType = userType;
        this.city = city;
        this.candidat = candidat;
        this.entreprise = entreprise;
    }
   

    /*
    * ID GENERATED NOT FROM DB SO CAN CAUSE SOME ISSUES (starting from 1 even though 1 already present sometimes)
    NEED TO BE FIXED
     */
    @Id 
    @SequenceGenerator(name = "APPUSER_ID_GENERATOR", sequenceName = "APPUSER_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APPUSER_ID_GENERATOR")
    @Column(name = "iduser", unique = true, nullable = false)
    public int getIdUser() {
        return this.idUser;
    }
    
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    @Column(name = "mail", unique = true, nullable = false, length = 50)
    public String getMail() {
        return this.mail;
    }
    
    public void setMail(String mail) {
        this.mail = mail;
    }

    @Column(name = "password", nullable = false, length = 50)
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "usertype", length = 10)
    public String getUserType() {
        return this.userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Column(name = "city", length = 50)
    public String getCity() {
        return this.city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }

    
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "appUser")
    public Candidat getCandidat() {
        return this.candidat;
    }
    
    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "appUser")
    public Entreprise getEntreprise() {
        return this.entreprise;
    }
    
    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }

    /*     @PrePersist
    @PreUpdate
    private void validateUserRole() {
        boolean isCandidateSet = this.candidat != null;
        boolean isEntrepriseSet = this.entreprise != null;
        if ((isCandidateSet && isEntrepriseSet) || (!isCandidateSet && !isEntrepriseSet)) {
            throw new RuntimeException("An AppUser must be either a candidate or an enterprise, but not both or neither.");
        }
    }*/

}
