package service;


import DAO.DAO;
import DAO.DaoUserImpl;
import entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HibernateUtil;

import java.util.List;


public class UserService {

    private final DAO dao;

    public UserService() {
        this.dao = new DaoUserImpl();
    }

    public UserService(DAO dao) {
        this.dao = dao;
    }

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User saveUser(String name, String email, int age) {

        logger.info("Сохранение User с параметрами: name = {}, email = {}, age = {}", name, email, age);
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не должно быть пустым");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("email не должн быть пустым");
        }
        if (age < 0 || age > 100) {
            throw new IllegalArgumentException("Возвраст в деапозоне от 1 до 99");
        }

        User user = new User(name, email, age);
        return dao.saveUser(user);
    }

    public void deleteUser(Long id){
        logger.info("Удаление User по ID: {}", id);
        dao.deleteUser(id);
    }

    public User getById(Long id){
        logger.info("Поиск User по ID: {}", id);
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID User большьше 0");
        }
        return dao.getById(id);
    }

    public List<User> allUsers(){
        logger.info("Поиск всех Users");
        return dao.allUsers();
    }

    public User updateUser(Long id, String name, String email, int age) {
        logger.info("Обновление User по ID: {}", id);
        User user = getById(id);

        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
        }else {
            throw new IllegalArgumentException("Имя не должно быть пустым");
        }
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
        }else {
            throw new IllegalArgumentException("email не должн быть пустым");
        }
        if (age > 0 && age < 100) {
            user.setAge(age);
        }else {
            throw new IllegalArgumentException("Возвраст в деапозоне от 1 до 99");
        }

        return dao.updateUser(user);
    }
}
