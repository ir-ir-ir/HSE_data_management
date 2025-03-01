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

    private void textOfProcedures (){
        String createTable = """
                CREATE OR REPLACE FUNCTION createTable()
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
                            ListOfSubjects NOT NULL
                        );
                    END IF;
                END;
                $$ LANGUAGE plpgsql;
                """;
        String clearTable = """
                CREATE OR REPLACE FUNCTION clearTableIfExists()
                AS $$
                BEGIN
                    IF EXISTS (
                        SELECT 1
                        FROM information_schema.tables
                        WHERE table_name = 'gia'
                          AND table_schema = 'public'
                    ) THEN
                        EXECUTE format('TRUNCATE TABLE %I', 'gia' );
                        RAISE INFO 'Таблица очищена';
                    ELSE
                        RAISE INFO 'Таблица не существует';
                    END IF;
                END;
                $$ LANGUAGE plpgsql;
                """;
        String createDB = """
                CREATE OR REPLACE FUNCTION createDB(dbname TEXT)
                RETURNS VOID
                AS $$
                BEGIN
                    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = dbname) THEN
                    EXECUTE format('CREATE DATABASE %I', dbname);
                    ELSE
                        RAISE EXCEPTION 'База данных % уже существует', dbname;
                    END IF;
                END;
                $$
                LANGUAGE plpgsql;
                """;
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
                    -- завершение всех активных подключений к базе данных
                        PERFORM pg_terminate_backend(pg_stat_activity.pid)
                        FROM pg_stat_activity
                        WHERE pg_stat_activity.datname = dbname;
                
                        EXECUTE format('DROP DATABASE %I', dbname);
                        RAISE INFO 'База данных "%" удалена.', dbname;
                    ELSE
                        RAISE EXCEPTION 'База данных "%" не существует.', dbname;
                    END IF;
                END;
                $$ LANGUAGE plpgsql;
                """;
    }
    private boolean rightToConnectForGuest(){
        // право на подключение
        String rightToConnect = """
                        CREATE OR REPLACE FUNCTION rightToConnect(dbname TEXT)
                        RETURNS void
                        AS $$
                        BEGIN
                            GRANT CONNECT ON DATABASE dbname TO guest;
                        END;
                        $$
                        LANGUAGE plpgsql;
                        """;
        try {
            Statement st = null;
            st = Main.current.createStatement();
            st.execute(rightToConnect);
            st.close();}
        catch (SQLException ex){
            return false;
        }
        return true;
    }
    private boolean rightToSelectForGuest(){
        //право на просмотр
        String rightToSelect = """
                        CREATE OR REPLACE FUNCTION rightToSelect(dbname TEXT)
                        RETURNS void
                        AS $$
                        BEGIN
                            GRANT SELECT ON ALL TABLES IN SCHEMA public TO guest;
                        END;
                        $$
                        LANGUAGE plpgsql;
                        """;
        try {
            Statement st = null;
            st = Main.current.createStatement();
            st.execute(rightToSelect);
            st.close();}
        catch (SQLException ex){
            return false;
        }
        return true;
    }

    /*
    // при открытии и создании выводим таблицу
    private void table(){
        // визуализация таблицы
        try(FileReader rf = new FileReader(filePath);
            BufferedReader bf = new BufferedReader(rf)) {
            // заполнение массива строк
            String theLine;
            String first = bf.readLine();
            while ((theLine = bf.readLine()) != null){

                model.addRow(parsing(theLine));
            }
        }
        catch(Exception ex){
        }
    }
    private void table_(){
        // визуализация таблицы
        try(FileReader rf = new FileReader("copy.txt");
            BufferedReader bf = new BufferedReader(rf)) {
            // заполнение массива строк
            String theLine;
            String first = bf.readLine();
            while ((theLine = bf.readLine()) != null){

                model.addRow(parsing(theLine));
            }
        }
        catch(Exception ex){
        }
    }*/

    private class Listener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent act) {
            // получаем и анализируем команду
            String command = act.getActionCommand();

            if (command.equals("Создать бд")) {
                System.exit(0);
                // окно считывания имени
                String nameNewDB =
                JOptionPane.showInputDialog(GUI.this,
                        "Введите название новой базы данных");
                if (nameNewDB == null) {
                    return;
                }
                // создаем бд
                if (Main.createDB(Main.current,nameNewDB, GUI.this) == false){
                    return;
                }
                // закрываем текущее соединение
                try {
                    Main.current.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(GUI.this, "Ошибка при подключении",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // подключаемся к новой бд
                String url = "jdbc:postgresql://127.0.0.1:5432/" + nameNewDB;
                try {
                    Class.forName("org.postgresql.Driver");
                    Main.current = DriverManager.getConnection(url, Main.Admin, Main.AdminPassword);
                }
                catch (SQLException |ClassNotFoundException ex){
                    JOptionPane.showMessageDialog(GUI.this, "Ошибка при подключении",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // выдаем права администратору на новую бд
                // он создал и является владельцем, поэтому права у него уже есть
                // создаем все хранимые процедуры
            }

            if (command.equals("Подключиться к бд")) {
                String nameNewDB =
                        JOptionPane.showInputDialog(GUI.this,
                                "Введите название новой базы данных");
                if (nameNewDB == null) {
                    return;
                }
                String password =
                        JOptionPane.showInputDialog(GUI.this,
                                "Введите пароль");
                if (password == null) {
                    return;
                }

                // выполнение хранимой процедуры
                if (Main.role.equals("guest")){
                    try{
                        CallableStatement cst = null;
                        cst = Main.current.prepareCall("{call rightToConnect (?)}");
                        cst.setString(1, nameNewDB);
                        cst.execute();
                        cst.close();
                    }
                    catch (SQLException ex){
                        JOptionPane.showMessageDialog(GUI.this, "Ошибка при подключении",
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // подключение к бд
                String url = "jdbc:postgresql://127.0.0.1:5432/" + nameNewDB;
                try {
                    Class.forName("org.postgresql.Driver");
                    Connection effort = DriverManager.getConnection(url, Main.role, password);
                    JOptionPane.showMessageDialog(GUI.this, "Подключение установлено!",
                            "Успешное подключение",
                            JOptionPane.PLAIN_MESSAGE);
                    effort.close();
                    Main.current = DriverManager.getConnection(url, Main.role, password);
                } catch (ClassNotFoundException | SQLException e) {
                    JOptionPane.showMessageDialog(GUI.this, "Ошибка при подключении",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                    //System.out.println( e.getMessage());
                    return;
                }

                // выполнение хранимой процедуры
                if (Main.role.equals("guest")){
                    try{
                        CallableStatement cst = null;
                        cst = Main.current.prepareCall("{call rightToSelect (?)}");
                        cst.setString(1, nameNewDB);
                        cst.execute();
                        cst.close();
                    }
                    catch (SQLException ex){
                        JOptionPane.showMessageDialog(GUI.this, "Ошибка при подключении",
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // визуализация таблицы
                

            }
        }
    }
}

