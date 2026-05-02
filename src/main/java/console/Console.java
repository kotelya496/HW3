package console;

import entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;
import util.HibernateUtil;

import java.util.List;
import java.util.Scanner;

public class Console {

    private static final Logger logger = LoggerFactory.getLogger(Console.class);
    private static final UserService userService = new UserService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void showMenu(){
        while (true) {
            System.out.println("\nМеню");
            System.out.println("\n1. создать User");
            System.out.println("2. получить всех User");
            System.out.println("3. поиск по ID");
            System.out.println("4. обновить User");
            System.out.println("5. удалить User");
            System.out.println("0. Выод");
            System.out.print("\nВыберите операцию: ");

            int numberOperation;
            try {
                numberOperation = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("\nВведите номер операции.");
                continue;
            }

            switch (numberOperation) {
                case 1:
                    saveUser();
                    break;
                case 2:
                    viewAllUsers();
                    break;
                case 3:
                    getUserById();
                    break;
                case 4:
                    updateUser();
                    break;
                case 5:
                    deleteUser();
                    break;
                case 0:
                    HibernateUtil.shutdown();
                    logger.info("Приложение закрыто");
                    System.exit(0);
                    break;
                default:
                    System.out.println("\nВыберите операцию из предложенных");
            }
        }
    }

    private static void saveUser() {
        try {
            System.out.print("\nВведите имя: ");
            String name = scanner.nextLine();

            System.out.print("Введите email: ");
            String email = scanner.nextLine();

            System.out.print("Введите возраст: ");
            Integer age = Integer.parseInt(scanner.nextLine());

            User user = userService.saveUser(name, email, age);
            logger.info("Сохранение User: {}", user);
            System.out.println("\nUser сохране");
        } catch (IllegalArgumentException e) {
            logger.warn("Ошибка данных при сохранении User: {}", e.getMessage());
            System.out.println("Неправильный ввод: " + e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Ошибка при сохранении User: {}", e.getMessage());
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void viewAllUsers() {
        logger.info("Показать всех User");
        System.out.println("\n--- Все User ---");
        List<User> users = userService.allUsers();

        if (users.isEmpty()) {
            System.out.println("Список Users пуст.");
        } else {
            users.forEach(System.out::println);
            System.out.println("\nКоличество User: " + users.size());
        }
    }

    private static void getUserById() {
        try {
            System.out.print("\nВведите user ID: ");
            Long id = Long.parseLong(scanner.nextLine());

            User user = userService.getById(id);
            if (user != null) {
                System.out.println("User найден:" + user);
            } else {
                System.out.println("User не найден по ID: " + id);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
            logger.error("Ошибка при поиске User: {}", e.getMessage());
        }
    }

    private static void updateUser() {
        try {
            System.out.print("\nВведите ID для обновления user: ");
            Long id = Long.parseLong(scanner.nextLine());

            System.out.print("Введите новое имя: ");
            String name = scanner.nextLine();
            if (name.trim().isEmpty()) name = null;

            System.out.print("Введите новый email: ");
            String email = scanner.nextLine();
            if (email.trim().isEmpty()) email = null;

            System.out.print("Введите новый возраст: ");
            String ageStr = scanner.nextLine();
            Integer age = Integer.parseInt(ageStr);

            User user = userService.updateUser(id, name, email, age);
            System.out.println("User обновден");
            logger.info("Обновлен User: {}", user);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка введиете числового значения возраста");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }catch (RuntimeException e) {
            System.out.println("Ошибка: " + e.getMessage());
            logger.error("Ошибка при обновлении User: {}", e.getMessage());
        }
    }

    private static void deleteUser() {
        try {
            System.out.print("\nВведите ID для удаления user: ");
            Long id = Long.parseLong(scanner.nextLine());

            System.out.print("Подтвердить операцию (y/n): ");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("y")) {
                userService.deleteUser(id);
                System.out.println("User удален");
                logger.info("Удален User по ID: {}", id);
            } else {
                System.out.println("Удаление не удалось.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Не правильно введен ID");
        } catch (RuntimeException e) {
            System.out.println("Ошибка: " + e.getMessage());
            logger.error("Ошибка при удалении User: {}", e.getMessage());
        }
    }
}
