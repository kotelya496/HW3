package DAO;

import entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HibernateUtil;
import java.util.Collections;
import java.util.List;

public class DaoUserImpl implements DAO{

    private static final Logger logger = LoggerFactory.getLogger(DaoUserImpl.class);
    private final SessionFactory sessionFactory;

    public DaoUserImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public DaoUserImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User saveUser(User user){
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
            logger.info("Сохранение User: {}", user);
        }catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Ошибка при сохранении User: {}", e.getMessage());
            throw new RuntimeException("Ошибка при сохранении User", e);
        }
        return user;
    }

    @Override
    public void deleteUser(Long id){
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null){
                session.remove(user);
                logger.info("Удален User: {}", user);
            }else {
                logger.warn("Ненайден User по ID: {}", id);
            }
            session.getTransaction().commit();
        }catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Ошибка при удалении User по ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Ошибка при удалении User", e);
        }
    }

    @Override
    public User getById(Long id){
        User user = null;
        try(Session session = sessionFactory.openSession()) {
            user = session.get(User.class, id);
            logger.info("User найдет по ID: {}", id);
        }catch (Exception e) {
            logger.error("Ошибка при поиске User по ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Ошибка при поиске User по ID", e);
        }finally {
            return user;
        }
    }

    @Override
    public List<User> allUsers(){
        List<User> allUsers;
        try(Session session = sessionFactory.openSession()) {
            allUsers = session.createQuery("FROM User", User.class).list();
            logger.info("Найдено User в количестве: {}", allUsers.size());
        }catch (Exception e) {
            logger.error("Ошибка при поиске всех User: {}", e.getMessage());
            return Collections.emptyList();
        }
        return allUsers;
    }

    @Override
    public User updateUser(User user){
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            user = session.merge(user);
            session.getTransaction().commit();
            logger.info("Обновлен User: {}", user);
            return user;
        }catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Ошибка при обновлении User: {}", e.getMessage());
            throw new RuntimeException("Ошибка при обновлении", e);
        }
    }
}
