import javax.swing.*;

public class Access {
    public static String showAccessDialog() {
        // Создание диалога с пользовательскими опциями
        String[] options = {"Администратор", "Гость", "Отмена"};
        int choice = JOptionPane.showOptionDialog(
                null, // Родительское окно (null означает, что диалог будет центрирован на экране)
                "Выберите режим доступа","Режим доступа",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, // null - используется стандартная иконка
                options,
                null ); // Кнопка по умолчанию
        if (choice == 0) return "Admin";
        else if (choice == 1) return "Guest";
        else return null;
    }
}
