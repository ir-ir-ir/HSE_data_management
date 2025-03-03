import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

public class GUI extends JFrame {
    private ImageIcon icon;
    private JPanel buttonsPanel, buttonsPanelr;
    private JButton create, open, clear, delete, change, search, create_z;
    private final int width, height;
    private JTable dataBase;
    private DefaultTableModel model;

    // конструктор
    public GUI(String winTitle) {
        super(winTitle); // конструктор суперкласса
        width = 1300;
        height = 700;
        icon = new ImageIcon(GUI.class.getResource("logo.png"));
        setIconImage(icon.getImage());
        setSize(width, height);
        buttonsPanel = new JPanel(); // верхняя панель
        buttonsPanelr = new JPanel(); // боковая панель

        create = new JButton("Создать бд");
        open = new JButton("Подключиться к бд");
        delete = new JButton("Удалить бд");
        clear = new JButton("Очистить таблицу");
        search = new JButton("Поиск");
        change = new JButton("Обновление кортежа");
        create_z = new JButton("Создать запись");


        ActionListener myListener = new Listener();
        // добавляем команды
        create.setActionCommand("Создать бд");
        open.setActionCommand("Подключиться к бд");
        delete.setActionCommand("Удалить бд");
        clear.setActionCommand("Очистить таблицу");
        change.setActionCommand("Обновление кортежа");
        search.setActionCommand("Поиск");
        create_z.setActionCommand("Создать запись");
        // каждой кнопоке добавляем слушателя
        create.addActionListener(myListener);
        open.addActionListener(myListener);
        delete.addActionListener(myListener);
        clear.addActionListener(myListener);
        change.addActionListener(myListener);
        search.addActionListener(myListener);
        create_z.addActionListener(myListener);

        buttonsPanel.add(create);
        buttonsPanel.add(open);
        buttonsPanel.add(clear);
        buttonsPanel.add(delete);
        buttonsPanelr.add(create_z);
        buttonsPanelr.add(change);
        buttonsPanelr.add(search);

        getContentPane().add(BorderLayout.NORTH, buttonsPanel);
        getContentPane().add(BorderLayout.SOUTH, buttonsPanelr);

        //кнопки в диалоговых окнах
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");
        UIManager.put("OptionPane.cancelButtonText", "Отмена");

        // закрытие (кнопка крестика)
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int ans = JOptionPane.showConfirmDialog(GUI.this,
                        "Вы уверены, что хотите выйти?",
                        "Закрытие приложения",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (ans == 0) {
                    try {
                        Main.current.close();
                        System.exit(0);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(GUI.this, "Ошибка при закрытии",
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JPanel tablePanel = new JPanel(); // создание панели
        //таблица
        model = new DefaultTableModel();
        model.addColumn("id");
        model.addColumn("ФИО");
        model.addColumn("Город");
        model.addColumn("Школа");
        model.addColumn("Средний балл ЕГЭ");
        model.addColumn("Список предметов");
        dataBase = new JTable(model);
        dataBase.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        dataBase.getColumnModel().getColumn(0).setPreferredWidth(20);
        dataBase.getColumnModel().getColumn(1).setPreferredWidth(200);
        dataBase.getColumnModel().getColumn(2).setPreferredWidth(100);
        dataBase.getColumnModel().getColumn(3).setPreferredWidth(100);
        dataBase.getColumnModel().getColumn(4).setPreferredWidth(20);
        dataBase.getColumnModel().getColumn(5).setPreferredWidth(250);
        tablePanel.add(dataBase);
        getContentPane().add(BorderLayout.CENTER, tablePanel);
        add(new JScrollPane(dataBase));
    }

    private boolean createProcedures (Connection a, GUI gui){
        String createTable = """
                CREATE OR REPLACE FUNCTION createTable()
                RETURNS void
                AS $$
                BEGIN
                    -- Проверка на существование таблицы GIA
                    IF NOT EXISTS (
                        SELECT 1
                        FROM information_schema.tables
                        WHERE table_name = 'gia'
                    ) THEN
                        -- Создание таблицы
                        CREATE TABLE GIA (
                            id SERIAL PRIMARY KEY,
                            FIO TEXT NOT NULL,
                            City TEXT NOT NULL,
                            School TEXT NOT NULL,
                            AverageScore FLOAT NOT NULL,
                            ListOfSubjects TEXT NOT NULL
                        );
                        RAISE INFO 'Таблица создана';
                    ELSE RAISE INFO 'Таблица уже существует';
                    END IF;
                END;
                $$ LANGUAGE plpgsql;
                """;
        String clearTable = """
                CREATE OR REPLACE FUNCTION clearTableIfExists()
                RETURNS void
                AS $$
                BEGIN
                    IF EXISTS (
                        SELECT 1
                        FROM information_schema.tables
                        WHERE table_name = 'gia'
                          AND table_schema = 'public'
                    ) THEN
                        TRUNCATE TABLE gia;
                        RAISE INFO 'Таблица очищена';
                    ELSE
                        RAISE INFO 'Таблица не существует';
                    END IF;
                END;
                $$ LANGUAGE plpgsql;
                """;
        // исправить на дблинк или не использовать
        String dropDB = """
                CREATE OR REPLACE FUNCTION dropDataBaseIfExists(dbname TEXT)
                RETURNS VOID
                AS $$
                BEGIN
                    IF EXISTS (
                        SELECT 1
                        FROM pg_database
                        WHERE datname = dbname
                    ) THEN
                        EXECUTE format('DROP DATABASE %I', dbname);
                        RAISE INFO 'База данных "%" удалена.', dbname;
                    ELSE
                        RAISE INFO 'База данных "%" не существует.', dbname;
                    END IF;
                END;
                $$ LANGUAGE plpgsql;
                """;
        String selectAllTable = """
                CREATE OR REPLACE FUNCTION selectAllTable()
                RETURNS TABLE (
                    id INT,
                    FIO TEXT,
                    City TEXT,
                    School TEXT,
                    AverageScore FLOAT,
                    ListOfSubject TEXT
                ) AS $$
                BEGIN
                    RETURN QUERY SELECT * FROM gia;
                END;
                $$ LANGUAGE plpgsql;
                """;

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
            st.execute(createTable);
            st.execute(clearTable);
            st.execute(dropDB);
            st.execute(selectAllTable);
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

    private static boolean createDB(Connection a, String nameBD, GUI gui){
        boolean dbExists = false;
        try {
            ResultSet rs = a.getMetaData().getCatalogs();
            while (rs.next()) {
                String existingDbName = rs.getString(1);
                if (existingDbName.equals(nameBD)) {
                    dbExists = true;
                    break;}
            }
            if (!dbExists) {
                Statement stmt = null;
                stmt = a.createStatement();
                String sql = "CREATE DATABASE " + nameBD;
                stmt.executeUpdate(sql);
                JOptionPane.showMessageDialog(gui, "База данных успешно создана!",
                        "Успешное выполнение",
                        JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(gui, "База данных с таким названием уже существует",
                        "Запрос не выполнен",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }}
        catch (SQLException ex){
            JOptionPane.showMessageDialog(gui, "Ошибка при создании",
                    "Запрос не выполнен",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println(ex.getMessage());
            return false;
        }
        return true;
    }
    private static boolean establishConnectionCurrent(String nameDB, String login, String password, GUI gui){
        String url = "jdbc:postgresql://127.0.0.1:5432/" + nameDB;
        try {
            Class.forName("org.postgresql.Driver");
            Main.current = DriverManager.getConnection(url, login, password);
        }
        catch (SQLException |ClassNotFoundException ex){
            JOptionPane.showMessageDialog(gui, "Ошибка при подключении",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println(ex.getMessage());
            return false;
        }
        return true;
    }
    private static boolean closeConnection (GUI gui, Connection conn){
        try {
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(gui, "Ошибка при подключении",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    private static boolean establishPostgresConnection(GUI gui, String nameBD){
        String url = "jdbc:postgresql://127.0.0.1:5432/" + nameBD;
        try {
            Class.forName("org.postgresql.Driver");
            Main.connForPostgres = DriverManager.getConnection(url, "postgres", "postgres123");
        }
        catch (SQLException |ClassNotFoundException ex){
            JOptionPane.showMessageDialog(gui, "Ошибка при подключении",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    private static boolean callSqlFunction (String functionName, GUI gui){
        CallableStatement cst = null;
        try {
            cst = Main.current.prepareCall(functionName);
            cst.execute();
            cst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(gui, "Ошибка при выполнении операции",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    // визуализация таблицы
    private void cleanTable() {
        DefaultTableModel model = (DefaultTableModel)dataBase.getModel();
        model.setRowCount(0);
    }
    private void addRow(String id, String FIO, String City, String School, String AverageScore, String ListOfSubject) {
        DefaultTableModel model = (DefaultTableModel)dataBase.getModel();
        model.addRow(new Object[]{id, FIO, City, School, AverageScore, ListOfSubject});
    }
    private void updateTable() {
        cleanTable();
        try(CallableStatement cst = Main.current.prepareCall("{call selectAllTable ()}")){
            cst.execute();
            try (ResultSet rs = cst.getResultSet()) {
                cleanTable();
                while (rs.next()) {
                    String id = rs.getString("id");
                    String FIO = rs.getString("FIO");
                    String City = rs.getString("City");
                    String School = rs.getString("School");
                    String AverageScore = rs.getString("AverageScore");
                    String ListOfSubject = rs.getString("ListOfSubject");

                    addRow(id, FIO, City, School, AverageScore, ListOfSubject);
                }
            }
        }
        catch (SQLException ex){
            JOptionPane.showMessageDialog(GUI.this, "Ошибка при отображении таблицы",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println(ex.getMessage());
        }
    }

    private class Listener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent act) {
            // получаем и анализируем команду
            String command = act.getActionCommand();

            if (command.equals("Создать бд")) {
                // пользователь вводит название новой бд
                String nameNewDB =
                JOptionPane.showInputDialog(GUI.this,
                        "Введите название новой базы данных");
                if (nameNewDB == null) return;
                // подключаемся к postgres от имени postgres, чтобы
                //        выдать администратору право на выполнение хранимых функций
                if (!establishPostgresConnection(GUI.this, "postgres")) return;
                try (Statement st = Main.connForPostgres.createStatement()){
                    st.execute("GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO admin;" );
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(GUI.this, "Ошибка при создании",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                    System.out.println(e.getMessage());
                    return;
                }
                // админ создает бд
                if (!createDB(Main.current, nameNewDB, GUI.this)) return; //см строку 354

                // если где-то ниже ошибка - вызвать удаление бд,

                // отключаем админа от postgres
                if (!closeConnection(GUI.this, Main.current)) return;
                // отключаем postgres от бд postgres
                if (!closeConnection(GUI.this, Main.connForPostgres)) return;
                // подключаем postgres к новой бд
                if (!establishPostgresConnection(GUI.this, nameNewDB)) return;
                // создаем все хранимые процедуры
                if (!createProcedures(Main.connForPostgres, GUI.this)) return;
                // забираем права у админа на выполнение некоторых хранимых процедур
                if (!Main.revokeExecuteFromAdmin(Main.connForPostgres, GUI.this)) return;
                try(CallableStatement cst = Main.connForPostgres.prepareCall("{call revokeExecuteFromAdmin ()}")){
                    cst.execute();
                }
                catch (SQLException ex){
                    JOptionPane.showMessageDialog(GUI.this, "Ошибка при создании",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                    System.out.println(ex.getMessage());
                    return;
                }
                // отключаем postgres от новой бд
                if (!closeConnection(GUI.this, Main.connForPostgres)) return;
                // подключаем админа к новой бд
                if (!establishConnectionCurrent(nameNewDB,Main.Admin, Main.AdminPassword, GUI.this)) return;
                // создаем таблицу
                if (!callSqlFunction("{call createTable()}", GUI.this)) return;
            }

            if (command.equals("Подключиться к бд")) {

                String nameNewDB =
                        JOptionPane.showInputDialog(GUI.this,
                                "Введите название базы данных");
                if (nameNewDB == null)  return;

                /*String password =
                        JOptionPane.showInputDialog(GUI.this,
                                "Введите пароль");
                if (password == null)  return;*/

                // подключаем postgres к новой бд
                if (Main.role.equals("guest")) {if (!establishPostgresConnection(GUI.this, nameNewDB)) return;}
                // даем гостю право на подключение к бд
                if (Main.role.equals("guest")){
                    try(CallableStatement cst = Main.connForPostgres.prepareCall("{call rightToConnectForGuest (?)}")){
                        cst.setString(1, nameNewDB);
                        cst.execute();
                    }
                    catch (SQLException ex){
                        JOptionPane.showMessageDialog(GUI.this, "Ошибка при подключении",
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                        System.out.println(ex.getMessage());
                        return;
                    }
                }
                // даем гостю право на select
                if (Main.role.equals("guest")){
                    try(CallableStatement cst = Main.connForPostgres.prepareCall("{call rightToSelectForGuest ()}")){
                        cst.execute();
                    }
                    catch (SQLException ex){
                        JOptionPane.showMessageDialog(GUI.this, "Ошибка при подключении",
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                        System.out.println(ex.getMessage());
                        return;
                    }
                }
                // отключаем postgres
                if (Main.role.equals("guest")) {if (!closeConnection(GUI.this, Main.connForPostgres)) return;}
                // подключаем пользователя к новой бд
                if (Main.role.equals("guest")){
                    if (!closeConnection(GUI.this, Main.current)) return;
                    if (!establishConnectionCurrent(nameNewDB,Main.Guest, Main.GuestPassword, GUI.this)) return;
                    JOptionPane.showMessageDialog(GUI.this, "Подключение установлено!",
                            "Успешное подключение",
                            JOptionPane.PLAIN_MESSAGE);
                }
                else if (Main.role.equals("admin")){
                    if (!closeConnection(GUI.this, Main.current)) return;
                    if (!establishConnectionCurrent(nameNewDB,Main.Admin, Main.AdminPassword, GUI.this)) return;
                    JOptionPane.showMessageDialog(GUI.this, "Подключение установлено!",
                            "Успешное подключение",
                            JOptionPane.PLAIN_MESSAGE);
                }

                updateTable();
            }

            if (command.equals("Удалить бд")){

                int ans = JOptionPane.showConfirmDialog(GUI.this,
                        "Вы уверены, что хотите удалить текущую бд?",
                        "Удаление бд",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (ans == 0) {
                    String currentDB = "";
                    try {
                        currentDB = Main.current.getCatalog();
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(GUI.this, "Ошибка при удалении",
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                        System.out.println(e.getMessage());
                        return;
                    }

                    // подключаем пользователя к постгрес, чтобы была возможность удалить текущую бд
                    closeConnection(GUI.this, Main.current);
                    String url = "jdbc:postgresql://127.0.0.1:5432/postgres";
                    // устанавливаем соответствующий логин и пароль
                    String login; String password;
                    if (Main.role.equals("guest")) {login = Main.Guest; password = Main.GuestPassword;}
                    else {login = Main.Admin; password = Main.AdminPassword;}
                    try {
                        Class.forName("org.postgresql.Driver");
                        Main.current = DriverManager.getConnection(url, login,password);
                        // удаление
                        // возможно добавить дблинк
                        String sql = "DROP DATABASE " + currentDB;
                        try (Statement st = Main.current.createStatement()) {
                            st.executeUpdate(sql);
                            JOptionPane.showMessageDialog(GUI.this, "Бд удалена!",
                                    "Успешное выполнение",
                                    JOptionPane.PLAIN_MESSAGE);
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(GUI.this, "Ошибка при удалении",
                                    "Ошибка",
                                    JOptionPane.ERROR_MESSAGE);
                            System.out.println(ex.getMessage());
                        }
                    }
                    catch (SQLException |ClassNotFoundException ex){
                        JOptionPane.showMessageDialog(GUI.this, "Ошибка при удалении",
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    // удаление
                    // возможно добавить дблинк
                    /*try(CallableStatement cst = Main.current.prepareCall("{call dropDataBaseIfExists (?)}")){
                    cst.setString(1, nameDB);
                    cst.execute();
                    SQLWarning message = cst.getWarnings();
                    String mes =  message.getMessage();
                    JOptionPane.showMessageDialog(GUI.this, mes,
                            "Удаление базы данных",
                            JOptionPane.ERROR_MESSAGE);
                }
                catch (SQLException ex){
                    JOptionPane.showMessageDialog(GUI.this, "Ошибка при удалении",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                    System.out.println(ex.getMessage());
                    return;
                }*/
                }

            }
        }
    }
}

