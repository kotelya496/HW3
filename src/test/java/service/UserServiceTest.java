package service;

import DAO.*;
import entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.*;


@TestInstance(PER_CLASS)
public class UserServiceTest {

    DAO dao;
    UserService service;

    @BeforeEach
    void beforeEach(){
        dao = mock(DAO.class);
        service = new UserService(dao);
    }

    @Test
    void saveUserTest(){

        String name = "Vasya";
        String email = "vasya@mail.ru";
        int age = 15;
        when(dao.saveUser(any(User.class))).thenAnswer(inv -> inv.getArgument(0));;

        service.saveUser(name,email,age);

        verify(dao).saveUser(any(User.class));
    }

    @ParameterizedTest
    @CsvSource({
            " , vasya@mail.ru, 15",
            " Vasya, , 15",
            " Vasya, vasya@mail.ru, 200"
    })
    void saveUserTest(String name, String email, int age){

        when(dao.saveUser(any(User.class))).thenAnswer(inv -> inv.getArgument(0));;

        Assertions.assertThrows(IllegalArgumentException.class, ()->service.saveUser(name,email,age));
    }

    @Test
    void deleteUserTest(){

        service.deleteUser(1l);

        verify(dao,times(1)).deleteUser(1l);
    }

    @Test
    void allUsersTest(){
        service.allUsers();

        verify(dao,times(1)).allUsers();
    }

    @Test
    void getByIdTest(){
        String name = "Vasya";
        String email = "vasya@mail.ru";
        int age = 15;
        User userGiven = new User(name,email,age);
        when(dao.getById(1l)).thenReturn(userGiven);

        User userActual = service.getById(1l);

        Assertions.assertEquals(userActual, userGiven);
        verify(dao,times(1)).getById(1l);
    }

    @Test
    void updateUserTest(){
        String namefirst = "Vasya";
        String emailfirst = "vasya@mail.ru";
        int agefirst = 15;
        User userGiven = new User(namefirst,emailfirst,agefirst);
        when(dao.getById(1l)).thenReturn(userGiven);
        String namesecont = "Ivan";
        String emailsecont = "ivan@mail.ru";
        int agesecont = 20;
        when(dao.updateUser(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User userActual = service.updateUser(1l,namesecont,emailsecont,agesecont);

        Assertions.assertEquals(userActual, userGiven);
        verify(dao).updateUser(any(User.class));
    }
}
