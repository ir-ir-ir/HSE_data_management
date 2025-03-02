import javax.swing.*;
import java.sql.*;

public class Main {
    public static String role = "";
    public static final String Admin = "admin";
    public static final String AdminPassword = "admin123";
    public static final String Guest = "guest";
    public static final String GuestPassword = "guest123";
    public static Connection current = null;
    public static Connection connForPostgres = null;
// пример хранимой процедуры + ее вызов
    public static boolean createGuest(Connection a){
        String ProcedureSQL = """
                CREATE OR REPLACE FUNCTION createGuestRoleIfNotExists()
                RETURNS void
                AS $$
                BEGIN
                    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'guest') THEN
                        CREATE ROLE guest WITH LOGIN PASSWORD 'guest123';
                    END IF;
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
            cst = a.prepareCall("{call createGuestRoleIfNotExists()}");
            cst.execute();
            //Закрытие
            st.close(); cst.close();
        }
        catch (SQLException ex){
            return false;
        }
        return true;
    }
    public static boolean createAdmin(Connection a){
        String ProcedureSQL = """
                CREATE OR REPLACE FUNCTION createAdminRoleIfNotExists()
                RETURNS void
                AS $$
                BEGIN
                    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'admin') THEN
                        CREATE ROLE admin WITH LOGIN PASSWORD 'admin123' CREATEDB;
                    END IF;
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
            return false;
        }
        return true;
    }
    public static boolean rightsForGuest(Connection a, GUI gui){
        String rightToConnectForGuest = """
                        CREATE OR REPLACE FUNCTION rightToConnectForGuest(dbname TEXT)
                        RETURNS void
                        AS $$
                        BEGIN
                             EXECUTE format('GRANT CONNECT ON DATABASE %I TO guest', dbname);
                        END;
                        $$
                        LANGUAGE plpgsql;
                        """;
        String rightToSelectForGuest = """
                        CREATE OR REPLACE FUNCTION rightToSelectForGuest()
                        RETURNS void
                        AS $$
                        BEGIN
                            GRANT SELECT ON ALL TABLES IN SCHEMA public TO guest;
                        END;
                        $$
                        LANGUAGE plpgsql;
                        """;
        try{
            // Создание процедуры
            Statement st = null;
            st = a.createStatement();
            st.execute(rightToConnectForGuest);
            st.execute(rightToSelectForGuest);
            //Закрытие
            st.close();
        }
        catch (SQLException ex){
            JOptionPane.showMessageDialog(gui, "Ошибка при создании",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println(ex.getMessage());
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
        // подключаемся к бд postgres
        else {
            // протокол
            String url = "jdbc:postgresql://127.0.0.1:5432/postgres";
            try {
                Class.forName("org.postgresql.Driver");
                current = DriverManager.getConnection(url, "postgres", "postgres123");
                if (role.equals("guest")){
                    if (createGuest(current) == true){
                        // создаем хранимые функции, чтобы потом через них можно было выдавать права гостю
                        if (!rightsForGuest(current, gui)) {
                            JOptionPane.showMessageDialog(gui, "Ошибка при подключении",
                                    "Ошибка",
                                    JOptionPane.ERROR_MESSAGE);
                            System.exit(0);
                        }
                        current.close();
                        current = DriverManager.getConnection(url, Guest, GuestPassword);
                    }
                }
                else if (createAdmin(current) == true){
                    current.close();
                    current = DriverManager.getConnection(url, Admin, AdminPassword);
                }
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
