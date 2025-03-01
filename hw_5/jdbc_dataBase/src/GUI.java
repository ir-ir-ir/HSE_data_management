import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.sql.SQLException;

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

    private void clear() {
        ((DefaultTableModel) dataBase.getModel()).setRowCount(0);
        System.out.println("clear");
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
                // при создании не нужно создавать новую роль админа,
                // тк администратор имеет право на создание и управление(с правами администратора)бд
                // создаем бд, подключаемся к ней
                // закрываем текущее соединение
                try {
                    Main.current.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(GUI.this, "Ошибка при подключении",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                }
                // создаем все хранимые процедуры
            }

            if (command.equals("Подключиться к бд")) {
                // не нужно создавать новую роль гостя, тк
                // изначально выдадим гостю права на подключение к другим бд (с такими же правами доступа)

            }
        }
    }
}