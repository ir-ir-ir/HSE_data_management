import javax.swing.*;
import java.sql.*;

public class Main {
    public static String role = "";
    private static final String Admin = "postgres";
    private static final String AdminPassword = "postgres123";
    private static final String Guest = "guest";
    private static final String GuestPassword = "guest123";
    public static Connection current = null;
// пример хранимой процедуры + ее вызов
    public static boolean createGuest(Connection a, GUI gui){
        String ProcedureSQL = """
                CREATE OR REPLACE FUNCTION createGuestRoleIfNotExists()
                RETURNS void
                AS $$
                BEGIN
                -- перепроверь
                    -- создание роли, если ее нет
                    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'guest123') THEN
                        CREATE ROLE admin123 WITH CREATEDB LOGIN PASSWORD 'guest123';
                    END IF;
                    -- разрешение на подключение к новым бд
                    GRANT CONNECT ON DATABASE new_database TO guest;
                    GRANT USAGE ON SCHEMA public TO guest;
                    GRANT SELECT ON ALL TABLES IN SCHEMA public TO guest;
                END;
                $$
                LANGUAGE plpgsql;
                """;
        try{
            // Создание процедуры
            Statement st = null;
            st = a.createStatement();
            st.execute(ProcedureSQL);
            // Вызов процедуры
            CallableStatement cst = null;
            cst = a.prepareCall("{call createAdminRoleIfNotExists()}");
            cst.execute();
            //Закрытие
            st.close(); cst.close();
        }
        catch (SQLException ex){
            JOptionPane.showMessageDialog(gui, "Ошибка при подключении",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static void main(String[] args) {

        GUI gui = new GUI("Data Base");
        gui.setLocationRelativeTo(null);
        gui.setVisible(true);
        role = Access.showAccessDialog();
        if (role == null) System.exit(0);
        // подключаемся к бд postgres c базовой ролью postgres (у нее есть все права доступа)
        // по факту - admin
        if (role.equals("Admin")) {
            // протокол
            String url = "jdbc:postgresql://127.0.0.1:5432/postgres";
            try {
                Class.forName("org.postgresql.Driver");
                current = DriverManager.getConnection(url, Admin, AdminPassword);
                JOptionPane.showMessageDialog(gui, "Подключение установлено!",
                        "Успешное подключение",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (ClassNotFoundException | SQLException e) {
                JOptionPane.showMessageDialog(gui, "Ошибка при подключении",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                //System.out.println( e.getMessage());
                System.exit(0);
            }
        }

    }
}
