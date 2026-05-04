package DAO;

import entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
class DaoUserImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb");


    private static SessionFactory sessionFactory;
    private DaoUserImpl daoUser;

    @BeforeAll
    static void initSessionFactory() {

        Configuration config = new Configuration();
        config.configure("hibernate.test.cfg.xml");
        config.setProperty("hibernate.connection.url", postgres.getJdbcUrl());

        config.addAnnotatedClass(User.class);

        sessionFactory = config.buildSessionFactory();
    }

    @AfterAll
    static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void setUp() {
        daoUser = new DaoUserImpl(sessionFactory);

        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void saveUser_shouldSaveAndGenerateId() {

        User user = new User ("Vasya", "vasya@mail.ru", 15);


        User saved = daoUser.saveUser(user);


        assertNotNull(saved.getId());
        assertEquals("Vasya", saved.getName());
        assertEquals("vasya@mail.ru", saved.getEmail());

        User fromDb = daoUser.getById(saved.getId());
        assertNotNull(fromDb);
        assertEquals(saved.getId(), fromDb.getId());
    }

    @Test
    void saveUser_shouldThrowException_whenEmailDuplicate() {

        User user = createTestUser ("Vasya", "vasya@mail.ru", 15);

        User duplicateEmail = new User("vasek", "vasya@mail.ru",15);

        assertThrows(RuntimeException.class, () -> daoUser.saveUser(duplicateEmail));
    }

    @Test
    void saveUser_shouldThrowException_whenNameIsNull() {

        User userNoName = new User(null, "vasya@mail.ru", 15);

        assertThrows(RuntimeException.class, () -> daoUser.saveUser(userNoName));
    }

    @Test
    void getById_shouldReturnUser_whenExists() {

        User user = createTestUser ("Vasya", "vasya@mail.ru", 15);

        User found = daoUser.getById(user.getId());

        assertNotNull(found);
        assertEquals(user.getId(), found.getId());
        assertEquals("Vasya", found.getName());
        assertEquals("vasya@mail.ru", found.getEmail());
    }

    @Test
    void getById_shouldReturnNull_whenUserDoesNotExist() {
        User user = createTestUser ("Vasya", "vasya@mail.ru", 15);

        User found = daoUser.getById(999L);

        assertNull(found);
    }

    @Test
    void allUsers_shouldReturnEmptyList_whenNoUsersExist() {
        // When
        List<User> users = daoUser.allUsers();

        // Then
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void allUsers_shouldReturnAllSavedUsers() {

        createTestUser ("Vasya", "vasya@mail.ru", 15);
        createTestUser ("Ivan", "ivan@mail.ru", 15);
        createTestUser ("Nastya", "nastya@mail.ru", 15);
        List<String> usersExpected = Arrays.asList("Vasya", "Ivan", "Nastya");

        List<User> users = daoUser.allUsers();
        List<String> userNames = users.stream().map(User::getName).collect(Collectors.toList());

        assertEquals(3, users.size());
        assertEquals(usersExpected,userNames);
    }

    @Test
    void updateUser_shouldModifyExistingUser() {

        User user = createTestUser ("Vasya", "vasya@mail.ru", 15);

        user.setName("Ivan");
        user.setEmail("ivan@mail.ru");
        User updated = daoUser.updateUser(user);

        assertEquals("Ivan", updated.getName());
        assertEquals("ivan@mail.ru", updated.getEmail());

        User fromDb = daoUser.getById(user.getId());
        assertEquals("Ivan", fromDb.getName());
        assertEquals("ivan@mail.ru", fromDb.getEmail());
    }

    @Test
    void updateUser_shouldThrowException_whenUpdatingNonExistentUser() {

        User user = createTestUser ("Vasya", "vasya@mail.ru", 15);
        user.setId(999L);

        assertThrows(RuntimeException.class, () -> daoUser.updateUser(user));
    }

    @Test
    void deleteUser_shouldRemoveExistingUser() {

        User user = createTestUser ("Vasya", "vasya@mail.ru", 15);
        assertNotNull(daoUser.getById(user.getId()));

        daoUser.deleteUser(user.getId());

        assertNull(daoUser.getById(user.getId()));
    }

    @Test
    void deleteUser_shouldNotThrowException_whenUserDoesNotExist() {

        assertDoesNotThrow(() -> daoUser.deleteUser(999L));
    }

    private User createTestUser(String name, String email, int age) {
        User user = new User(name, email, age);
        return daoUser.saveUser(user);
    }
}
