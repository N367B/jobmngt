package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.dao.AppUserDao;
import fr.atlantique.imt.inf211.jobmngt.entity.AppUser;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Component
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private AppUserDao appUserDao;

    public List<AppUser> listOfUsers() {
        return appUserDao.findAll("name", "ASC");
    }

    public AppUser getUserapp(Integer id) {
        return appUserDao.findById(id);
    }

    public Long nbUsers() {
        return appUserDao.count();
    }

    public Optional<AppUser> checkLogin(AppUser u) {
        return appUserDao.checkLogin(u);
    }
}
