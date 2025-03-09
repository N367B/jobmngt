package fr.atlantique.imt.inf211.jobmngt.service;

import java.util.List;
import java.util.Optional;

import fr.atlantique.imt.inf211.jobmngt.entity.*;;
public interface AppUserService {

    public List<AppUser> listOfUsers();

    public AppUser getUserapp(Integer id);

    public Long nbUsers();

    public Optional<AppUser> checkLogin(AppUser u);
}
