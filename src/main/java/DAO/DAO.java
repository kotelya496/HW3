package DAO;

import entity.User;

import java.util.List;

public interface DAO {

    User saveUser(User user);
    void deleteUser(Long id);
    User getById(Long id);
    List<User> allUsers();
    User updateUser(User user);
}
