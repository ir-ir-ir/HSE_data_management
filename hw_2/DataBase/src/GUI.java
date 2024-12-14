import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.StringTokenizer;

public class GUI extends JFrame{
    // поля
    private ImageIcon icon;
    private JPanel buttonsPanel, buttonsPanelr;
    private JButton create, open, save, clear, delete, createBack, back, change, search, delete_z, create_z;
    private final int width, height;
    private int countStr;
    private JTable dataBase;
    private DefaultTableModel model;
    private boolean saveChanges;

    // конструктор
    public GUI (String winTitle) {
        super(winTitle); // конструктор суперкласса
        width = 1300;
        height = 700;
        icon = new ImageIcon(GUI.class.getResource("logo.png"));
        setIconImage(icon.getImage());
        setSize(width, height);
        buttonsPanel = new JPanel(); // верхняя панель
        buttonsPanelr = new JPanel(); // боковая панель
        countStr = 0;
        saveChanges = false;

        create = new JButton("Создать");
        open = new JButton("Открыть");
        delete = new JButton("Удалить");
        save = new JButton("Сохранить");
        clear = new JButton("Очистить");
        createBack = new JButton("Создать backup-файл");
        back = new JButton("Восстановить из backup-файла");
        change = new JButton("Изменить запись");
        search = new JButton("Поиск");
        delete_z = new JButton("Удалить запись");
        create_z = new JButton("Создать запись");


        ActionListener myListener = new Listener();
        // добавляем команды
        create.setActionCommand("Создать");
        open.setActionCommand("Открыть");
        delete.setActionCommand("Удалить");
        save.setActionCommand("Сохранить");
        clear.setActionCommand("Очистить");
        createBack.setActionCommand("Создать backup-файл");
        back.setActionCommand("Восстановить из backup-файла");
        change.setActionCommand("Изменить запись");
        search.setActionCommand("Поиск");
        delete_z.setActionCommand("Удалить запись");
        create_z.setActionCommand("Создать запись");
        // каждой кнопоке добавляем слушателя
        create.addActionListener(myListener);
        open.addActionListener(myListener);
        delete.addActionListener(myListener);
        save.addActionListener(myListener);
        clear.addActionListener(myListener);
        createBack.addActionListener(myListener);
        back.addActionListener(myListener);
        change.addActionListener(myListener);
        search.addActionListener(myListener);
        delete_z.addActionListener(myListener);
        create_z.addActionListener(myListener);

        // добавляем кнопки на панель
        buttonsPanel.add(create);
        buttonsPanel.add(open);
        buttonsPanel.add(save);
        buttonsPanel.add(clear);
        buttonsPanel.add(delete);
        buttonsPanel.add(createBack);
        buttonsPanel.add(back);
        buttonsPanelr.add(create_z);
        buttonsPanelr.add(change);
        buttonsPanelr.add(search);
        buttonsPanelr.add(delete_z);

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

                Main.id = 0;
                if (Main.deleteBackup().equals("not ok")){
                    JOptionPane.showMessageDialog(GUI.this,
                            "Произошла ошибка при закрытии!",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                }
                if ((Main.filePath != null) && (saveChanges == false)) {
                    int ans = JOptionPane.showConfirmDialog(GUI.this,
                            "Сохранить изменения в текущем файле?",
                            "Закрытие приложения",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (ans == 0 ){

                        String response = Main.saveFile();
                        if (response.equals("Изменения успешно сохранены!")){
                            JOptionPane.showMessageDialog(GUI.this, response,
                                    "Успешное выполнение",
                                    JOptionPane.PLAIN_MESSAGE);
                        }
                        else{
                            JOptionPane.showMessageDialog(GUI.this, response,
                                    "Ошибка",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                        File fc = new File(Main.copyFilePath);
                        if (!fc.delete()){
                            JOptionPane.showMessageDialog(GUI.this,
                                    "Произошла ошибка при закрытии!",
                                    "Ошибка",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        else {System.exit(0);}
                    }

                    if (ans == 1) {
                        File fc = new File(Main.copyFilePath);
                        if (!fc.delete()){
                            JOptionPane.showMessageDialog(GUI.this,
                                    "Произошла ошибка при закрытии!",
                                    "Ошибка",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        else System.exit(0);
                    }
                }
                if ((Main.filePath == null) || (saveChanges == true)) {
                    File ff = new File(Main.copyFilePath);
                    if (ff.isFile()){
                        if (!ff.delete()){
                            System.out.println("Произошла ошибка при закрытии!");
                        }
                        else {System.exit(0);}
                    }
                    System.exit(0);
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

    //количество строк
    private void getCountStr(){
        try(FileReader rf = new FileReader(Main.filePath);
            BufferedReader bf = new BufferedReader(rf)) {
            String theLine;
            while ((theLine = bf.readLine()) != null){
                this.countStr += 1;
            }
        }
        catch(Exception ex){}
    }

    private void clear(){
        ((DefaultTableModel) dataBase.getModel()).setRowCount(0);
        System.out.println("clear");
    }

    private String[] parsing(String a){
        StringTokenizer str = new StringTokenizer (a, ";");
        String[] answer = new String[6];
        int i = 0;
        while (str.hasMoreTokens()){
            answer[i] = str.nextToken();
            i += 1;
        }
        return answer;
    }
    // при открытии и создании выводим таблицу
    private void table(){
        // визуализация таблицы
        try(FileReader rf = new FileReader(Main.filePath);
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
    }

    private boolean notExistError(){
        if (Main.filePath == null){
            JOptionPane.showMessageDialog(GUI.this,
                    "Необходимо сначала открыть или создать файл!",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private class Listener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent act) {
            // получаем и анализируем команду
            String command = act.getActionCommand();

            if (command.equals("Создать")) {
                int ans = 10;
                if ((Main.filePath != null) && (saveChanges == false)) {
                    ans = JOptionPane.showConfirmDialog(GUI.this,
                            "Сохранить изменения в текущем файле?",
                            "Закрытие теблицы",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                }
                if (ans == 1) {
                    Main.filePath = null;
                    clear();
                }
                if (ans == 0 ) {

                    String resp = Main.saveFile();
                    if (!resp.equals("Изменения успешно сохранены!")){
                        JOptionPane.showMessageDialog(GUI.this, "Произошла ошибка при сохранении изменений!",
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (resp.equals("Изменения успешно сохранены!")){
                        JOptionPane.showMessageDialog(GUI.this, resp,
                                "Успешное выполнение",
                                JOptionPane.PLAIN_MESSAGE);
                    }

                    File fc = new File(Main.copyFilePath);
                    if (!fc.delete()) {
                        System.out.println("Произошла ошибка при создании!");
                    }
                    Main.filePath = null;
                }
                if ((Main.filePath == null ) || (saveChanges == true)){
                    String input_path = JOptionPane.showInputDialog(GUI.this,
                            "Введите путь до папки, в которой хотите создать файл: ");
                    if (input_path == null) return;
                    String input_name = JOptionPane.showInputDialog(GUI.this,
                            "Введите название файла в формате .txt: ");
                    if (input_name == null) return;

                    String response = Main.createFile(input_path, input_name);
                    if (response.equals("Файл создан!")) {
                        JOptionPane.showMessageDialog(GUI.this, response,
                                "Успешное выполнение",
                                JOptionPane.PLAIN_MESSAGE);
                        clear();
                        saveChanges = false;
                        table();
                    } else {
                        JOptionPane.showMessageDialog(GUI.this, response,
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            if (command.equals("Открыть")) {
                int ans = 10;

                if ((Main.filePath != null) && (saveChanges == false)){
                    ans = JOptionPane.showConfirmDialog(GUI.this,
                            "Сохранить изменения в текущем файле?",
                            "Закрытие теблицы",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                }
                if (ans == 1) {

                    Main.filePath = null;

                    // удаление копии
                    File fc = new File(Main.copyFilePath);
                    if (!fc.delete()) {
                        JOptionPane.showMessageDialog(GUI.this, "Произошла ошибка при открытии!",
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    clear();
                }
                if (ans == 0) {
                    String resp = Main.saveFile();
                    if (!resp.equals("Изменения успешно сохранены!")){
                        JOptionPane.showMessageDialog(GUI.this, "Произошла ошибка при сохранении изменений!",
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (resp.equals("Изменения успешно сохранены!")){
                        JOptionPane.showMessageDialog(GUI.this, resp,
                                "Успешное выполнение",
                                JOptionPane.PLAIN_MESSAGE);
                        clear();
                    }

                    // удаление копии
                    File fc = new File(Main.copyFilePath);
                    if (!fc.delete()) {
                        JOptionPane.showMessageDialog(GUI.this, "Произошла ошибка при открытии!",
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Main.filePath = null;
                    String input_str = JOptionPane.showInputDialog(GUI.this, "Введите путь до файла: ");
                    if (input_str == null) return;
                    String response = Main.openFile(input_str);
                    if (!(response.equals("ok"))) {
                        JOptionPane.showMessageDialog(GUI.this, response,
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    clear();
                    table();
                }
                if ((Main.filePath == null) || (saveChanges == true)){
                    String input_str = JOptionPane.showInputDialog(GUI.this, "Введите путь до файла: ");
                    if (input_str == null) return;
                    String response = Main.openFile(input_str);
                    if (!(response.equals("ok"))) {
                        JOptionPane.showMessageDialog(GUI.this, response,
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (saveChanges == true) {
                        saveChanges = false;
                        clear();
                    }
                    table();
                }
            }

            if (command.equals("Удалить")) {
                int ans = JOptionPane.showConfirmDialog(GUI.this,
                        "Удалить файл?",
                        "Удаление файла",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (ans == 0) {
                    String response = Main.deleteFile();
                    if (response.equals("Файл успешно удалён!")) {
                        JOptionPane.showMessageDialog(GUI.this, response,
                                "Успешное выполнение",
                                JOptionPane.PLAIN_MESSAGE);
                        clear();
                    } else {
                        JOptionPane.showMessageDialog(GUI.this, response,
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            if (command.equals("Сохранить")) {
                String response = Main.saveFile();
                if (response.equals("Изменения успешно сохранены!")) {
                    JOptionPane.showMessageDialog(GUI.this, response,
                            "Успешное выполнение",
                            JOptionPane.PLAIN_MESSAGE);
                    saveChanges = true;
                } else {
                    JOptionPane.showMessageDialog(GUI.this, response,
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

            if (command.equals("Очистить")) {
                int ans = JOptionPane.showConfirmDialog(GUI.this,
                        "Очистить содержимое файла?",
                        "Очистка",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (ans == 0) {
                    String response = Main.clearFile();
                    if (!(response.equals("ok"))) {
                        JOptionPane.showMessageDialog(GUI.this, response,
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(GUI.this,
                                "Содержимое файла очищено!",
                                "Успешное выполнение",
                                JOptionPane.PLAIN_MESSAGE);
                        clear();
                    }
                }
            }

            if (command.equals("Создать backup-файл")) {
                String response = Main.createBackUpFile();
                if (response.equals("Backup-файл успешно создан!")) {
                    JOptionPane.showMessageDialog(GUI.this, response,
                            "Успешное выполнение",
                            JOptionPane.PLAIN_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(GUI.this, response,
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

            if (command.equals("Восстановить из backup-файла")) {
                int ans = JOptionPane.showConfirmDialog(GUI.this,
                        "Вернуться к backup-файлу?",
                        "Восстановление",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (ans == 0) {
                    String response = Main.back();
                    if (response.equals("Восстановление завершено!")) {
                        clear();
                        table();
                        JOptionPane.showMessageDialog(GUI.this, response,
                                "Успешное выполнение",
                                JOptionPane.PLAIN_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(GUI.this, response,
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            if (command.equals("Создать запись")){

                if (notExistError() == false) return;

                JDialog dialog = new JDialog(GUI.this, "Создание записи", true);
                dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                dialog.setSize(600, 280);
                dialog.setLocationRelativeTo(null);
                dialog.setResizable(true);

                JTextField fio = new JTextField(20);
                JTextField city = new JTextField(20);
                JTextField school = new JTextField(20);
                JTextField score = new JTextField(20);
                JTextField subjects = new JTextField(20);

                JLabel fioLabel = new JLabel("ФИО: ");
                JLabel cityLabel = new JLabel("Город: ");
                JLabel schoolLabel = new JLabel("Школа: ");
                JLabel scoreLabel = new JLabel("Средний балл ЕГЭ: ");
                JLabel subjectshLabel = new JLabel("Список предметов: ");


                JPanel inputPanel = new JPanel();
                BoxLayout boxlayout = new BoxLayout(inputPanel, BoxLayout.Y_AXIS);
                inputPanel.setLayout(boxlayout);
                inputPanel.add(fioLabel);
                inputPanel.add(fio);
                inputPanel.add(Box.createVerticalGlue());
                inputPanel.add(cityLabel);
                inputPanel.add(city);
                inputPanel.add(Box.createVerticalGlue());
                inputPanel.add(schoolLabel);
                inputPanel.add(school);
                inputPanel.add(Box.createVerticalGlue());
                inputPanel.add(scoreLabel);
                inputPanel.add(score);
                inputPanel.add(Box.createVerticalGlue());
                inputPanel.add(subjectshLabel);
                inputPanel.add(subjects);
                dialog.getContentPane().add(BorderLayout.NORTH, inputPanel);

                JButton create_nz, cancel;
                create_nz = new JButton("Создать запись");
                cancel = new JButton("Отмена");
                create_nz.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        String inputFio = (fio.getText()).replace(";","");
                        String inputBirthday = (city.getText()).replace(";","");
                        String inputSeria = (school.getText()).replace(";","");
                        String inputNumber = (score.getText()).replace(";","");
                        String inputSubjects = (subjects.getText()).replace(";","");
                        //System.out.println(Main.id);
                        Main.id += 1;
                        //System.out.println(Main.id);
                        String writing = Integer.toString(Main.id) + "; " + inputFio + "; " + inputBirthday + "; " + inputSeria + "; " + inputNumber + "; " + inputSubjects + ";";
                        int ans = Main.checkFileString(writing, 0);
                        if (ans == 0){
                            JOptionPane.showMessageDialog(GUI.this,
                                    "Неверный формат!",
                                    "Ошибка",
                                    JOptionPane.ERROR_MESSAGE);
                            Main.id -= 1;
                            dialog.dispose();
                        }
                        else {
                            try(FileWriter f = new FileWriter("copy.txt", true)){
                                f.write(writing);
                                f.write('\n');
                            }
                            catch(Exception ex){
                                return;
                            }
                            model.addRow(parsing(writing));
                            JOptionPane.showMessageDialog(GUI.this,
                                    "Запись создана!",
                                    "Успешное выполнение",
                                    JOptionPane.PLAIN_MESSAGE);
                            dialog.dispose();
                        }
                    }
                });
                cancel.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose();
                    }
                });
                JPanel buttonsPanel = new JPanel();
                buttonsPanel.add(create_nz);
                buttonsPanel.add(cancel);
                dialog.getContentPane().add(BorderLayout.SOUTH, buttonsPanel);
                dialog.setVisible(true);
            }

            if (command.equals("Поиск")){

                JDialog dialog = new JDialog(GUI.this, "Поиск", true);
                dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                dialog.setSize(300, 180);
                dialog.setLocationRelativeTo(null);
                dialog.setResizable(true);

                JTextField coloumn = new JTextField(20);
                JTextField value = new JTextField(20);

                JLabel coloumnLabel = new JLabel("Номер столбца (от 1 до 6): ");
                JLabel valueLabel = new JLabel("Искомое значение: ");


                JPanel inputPanel = new JPanel();
                BoxLayout boxlayout = new BoxLayout(inputPanel, BoxLayout.Y_AXIS);
                inputPanel.setLayout(boxlayout);
                inputPanel.add(coloumnLabel);
                inputPanel.add(coloumn);
                inputPanel.add(Box.createVerticalGlue());
                inputPanel.add(valueLabel);
                inputPanel.add(value);
                dialog.getContentPane().add(BorderLayout.NORTH, inputPanel);

                JButton search_, cancel;
                search_ = new JButton("Поиск");
                cancel = new JButton("Отмена");
                search_.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        String inputColoumn = (coloumn.getText()).replace(".","");
                        String inputValue = (value.getText()).replace(";","");

                        if ((Integer.parseInt(inputColoumn) < 1) || (Integer.parseInt(inputColoumn) > 6)) {
                            JOptionPane.showMessageDialog(GUI.this,
                                    "Некорректный номер столбца!",
                                    "Ошибка",
                                    JOptionPane.ERROR_MESSAGE);
                            dialog.dispose();
                            return;
                        }

                        JDialog dialogAns = new JDialog(GUI.this, "Найденные строки", true);
                        dialogAns.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                        dialogAns.setSize(1250, 280);
                        dialogAns.setLocationRelativeTo(null);
                        dialogAns.setResizable(true);


                        DefaultTableModel modelSerch = new DefaultTableModel();
                        modelSerch.addColumn("id");
                        modelSerch.addColumn("ФИО");
                        modelSerch.addColumn("Город");
                        modelSerch.addColumn("Школа");
                        modelSerch.addColumn("Средний балл ЕГЭ");
                        modelSerch.addColumn("Список предметов");
                        JTable Ans = new JTable(modelSerch);
                        Ans.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                        Ans.getColumnModel().getColumn(0).setPreferredWidth(20);
                        Ans.getColumnModel().getColumn(1).setPreferredWidth(200);
                        Ans.getColumnModel().getColumn(2).setPreferredWidth(100);
                        Ans.getColumnModel().getColumn(3).setPreferredWidth(100);
                        Ans.getColumnModel().getColumn(4).setPreferredWidth(20);
                        Ans.getColumnModel().getColumn(5).setPreferredWidth(250);
                        JScrollPane scrollPane = new JScrollPane(Ans);
                        dialogAns.add(scrollPane);


                        try(FileReader ff = new FileReader("copy.txt"); BufferedReader bp = new BufferedReader(ff);){
                            String theLine;
                            while((theLine = bp.readLine()) != null){
                                String[] prom = parsing(theLine);
                                if ((prom[Integer.parseInt(inputColoumn) - 1].trim()).equals(inputValue.trim())){
                                    modelSerch.addRow(prom);
                                }
                            }
                        }
                        catch(Exception ex){
                            return;
                        }
                        dialogAns.setVisible(true);
                    }
                });
                cancel.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose();
                    }
                });

                inputPanel.add(search_);
                inputPanel.add(cancel);
                dialog.getContentPane().add(BorderLayout.WEST, inputPanel);
                dialog.setVisible(true);
            }

            if (command.equals("Изменить запись")){
                JDialog dialog = new JDialog(GUI.this, "Поиск", true);
                dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                dialog.setSize(300, 180);
                dialog.setLocationRelativeTo(null);
                dialog.setResizable(true);

                JTextField id_ = new JTextField(20);
                JTextField text = new JTextField(20);

                JLabel idLabel = new JLabel("id: ");
                JLabel textLabel = new JLabel("Новое значение строки (с текущим id, через ;): ");


                JPanel inputPanel = new JPanel();
                BoxLayout boxlayout = new BoxLayout(inputPanel, BoxLayout.Y_AXIS);
                inputPanel.setLayout(boxlayout);
                inputPanel.add(idLabel);
                inputPanel.add(id_);
                inputPanel.add(Box.createVerticalGlue());
                inputPanel.add(textLabel);
                inputPanel.add(text);
                dialog.getContentPane().add(BorderLayout.NORTH, inputPanel);

                JButton search_, cancel;
                search_ = new JButton("Изменить");
                cancel = new JButton("Отмена");
                search_.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        String inputId = (id_.getText()).replace(".","");
                        String inputText = text.getText();

                        int resp = Main.checkFileString(inputText, 0);
                        if (resp == 0){
                            JOptionPane.showMessageDialog(GUI.this,
                                    "Неверный формат!",
                                    "Ошибка",
                                    JOptionPane.ERROR_MESSAGE);
                            dialog.dispose();
                            return;
                        }

                        File f = new File("copy.txt");
                        File ff = new File("copy1.txt");
                        try(FileReader rf = new FileReader("copy.txt");
                            BufferedReader bf = new BufferedReader(rf);
                            FileWriter wf = new FileWriter("copy1.txt")){

                            String theLine;
                            while ((theLine = bf.readLine()) != null) {
                                String[] prom = parsing(theLine);
                                if ((prom[0].trim()).equals(inputId.trim())){
                                    wf.write(inputText);
                                    wf.write('\n');
                                    continue;
                                }
                                else {
                                    wf.write(theLine);
                                    wf.write('\n');
                                }
                            }

                        } catch (Exception ex) {
                            System.out.println("Problem reading file.");
                            return;
                        }
                        f.delete();
                        if (ff.renameTo(f)) {System.out.println("ok");};
                        clear();
                        table_();
                        JOptionPane.showMessageDialog(GUI.this,
                                    "Изменения произведены!",
                                    "Успешное выполнение",
                                    JOptionPane.PLAIN_MESSAGE);
                            dialog.dispose();
                    }
                });
                cancel.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose();
                    }
                });

                inputPanel.add(search_);
                inputPanel.add(cancel);
                dialog.getContentPane().add(BorderLayout.WEST, inputPanel);
                dialog.setVisible(true);
            }

        }
    }
}

