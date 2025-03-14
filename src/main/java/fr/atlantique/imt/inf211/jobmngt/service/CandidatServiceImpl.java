package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.dao.CandidatDao;
import fr.atlantique.imt.inf211.jobmngt.entity.Candidat;
import fr.atlantique.imt.inf211.jobmngt.entity.Entreprise;
import fr.atlantique.imt.inf211.jobmngt.service.CandidatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CandidatServiceImpl implements CandidatService {

    @Autowired
    private CandidatDao candidatDao;

    @Override
    public List<Candidat> listCandidats() {
        return candidatDao.findAll();
    }

    @Override
    public Candidat getCandidatById(int id) {
        return candidatDao.findById(id);
    }
    
    @Override
    public long countCandidats() {
        return candidatDao.findAll().size();
    }

    @Override
    public Candidat saveCandidat(Candidat candidat) {
         Candidat existingCandidat = candidatDao.findByUserMail(candidat.getAppUser().getMail());
        
        if (existingCandidat !=null) {
            throw new IllegalArgumentException("Un candidat avec cet email existe déjà.");
        }
        if (candidat.getIdCandidat() == 0) {
            candidatDao.persist(candidat);
        } else {
            candidat = candidatDao.merge(candidat);
        }
        return candidat;
    }

    @Override
    public boolean deleteCandidat(int id) {
        Candidat candidat = candidatDao.findById(id);
        if (candidat != null) {
            candidatDao.remove(candidat);
            return true;
        }
        return false;
    }
}