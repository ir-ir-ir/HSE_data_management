import java.io.*;
import java.util.Scanner;
import java.util.StringTokenizer;


public class Main {

    static String filePath = null;
    static String copyFilePath = "copy.txt";
    static int id = 0;
    static int previousId = 0;

    static String createFile(String path, String name){

        filePath = path;
        // проверка названия файла
        int point = name.lastIndexOf('.');
        String strchech = "";
        if (point != -1){
            char[] arr = name.toCharArray();
            for (int i = point; i < arr.length; i ++){
                strchech += arr[i];
            }
        }
        if (!strchech.equals(".txt")){
            return "Был введен неверный формат!";
        }
        // формирование filePath
        if (filePath.charAt(filePath.length() - 1) != '\\' ) {
            filePath += "\\" + name;
        }
        else {
            filePath += name;
        }

        // создаем файл
        // если передан некорректный путь, то файл не создастся
        File file = new File(filePath);
        boolean exist;
        try {
            exist = file.createNewFile();
            if (!exist){
                filePath = null;
                return "Файл уже существует!";
            }
        }
        catch (Exception ex) {
            filePath = null;
            return "Передан некорректный путь. Файл не был создан!";
        }

        // создание шапки таблицы
        String start = "id; ФИО; Город; Школа; Средний балл ЕГЭ; Список предметов;";
        try(FileWriter init = new FileWriter  (filePath, true)){
            init.write(start);
        }
        catch(Exception ex){
            filePath = null;
            return ("Ошибка при создании файла!");
        }

        // cоздание копии
        try(FileReader rf = new FileReader(filePath);
            BufferedReader bf = new BufferedReader(rf);
            FileWriter wf = new FileWriter(copyFilePath)){
            String theLine;
            while((theLine = bf.readLine()) != null){
                wf.write(theLine);
                wf.write('\n');
            }
        }
        catch(Exception ex){
            filePath = null;
            return ("Ошибка при создании файла!");
        }
        if (deleteBackup().equals("not ok")) return ("Ошибка при создании файла!");
        // каким-то образом выводим на экран таблицу
        id = 0;
        return "Файл создан!";
    }
    static String deleteFile(){

        File check = new File(filePath);
        if (check.isFile()){
            boolean deletef = check.delete();
            File checkcopy = new File(copyFilePath);
            boolean deletec = checkcopy.delete();
            if ((deletec && deletef)&&(deleteBackup().equals("ok"))){
                filePath = null;
                id = 0;
                return ("Файл успешно удалён!");
            }
            else{
                filePath = null;
                return "Ошибка при удалении! Проверьте, что файл не открыт в другой программе.";
            }
        }
        else{
            return "Необходимо сначала открыть файл!";
        }
    }
    static String checkFileFormat(){
        File f = new File(filePath);
        if (!f.isFile()){
            filePath = null;
            return "Убедитесь, что такой файл существует!";
        }
        String strchech = "";
        for (int i = filePath.length() - 4; i < filePath.length(); i ++){
            strchech += filePath.charAt(i);
        }
        if (!strchech.equals(".txt")){
            filePath = null;
            return "Невалидный формат файла! Файл не может быть открыт!";
        }
        return "ok";
    }
    static int checkFileString(String a, int last) {
        // возвращает 0 - если ошибка. last(предыдущий id) - если все ок

        StringTokenizer str_a = new StringTokenizer(a, ";");
        String[] str_arr = new String[6];
        if (str_a.countTokens() != 6) return 0;
        int k = 0;
        while (str_a.hasMoreTokens()) {
            str_arr[k] = str_a.nextToken();
            k += 1;
        }

        // id
        if (!str_arr[0].matches("\\d*")){ return 0;}
        {if (Integer.parseInt(str_arr[0]) <= last) {return 0;}}

        // фио
        StringTokenizer str_a1 = new StringTokenizer(str_arr[1]);
        if (str_a1.countTokens() != 3) {return 0;}
        while (str_a1.hasMoreTokens()) {
            String check = str_a1.nextToken();
            check = check.replace('ё','е');
            if (!((check.matches("[А-Я][а-я]*")))) {
                return 0;}
        }

        // город
        if (!str_arr[2].matches("[ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮйцукенгшщзхъфывапролджэячсмитьбю\\- ]+")){
            return 0;
        }

        //средний балл
        String copystr = str_arr[4];
        if ((!str_arr[4].matches("[0123456789. ]+")) || (copystr.replaceAll("[^.]*", "").length() != 1)){
            return 0;
        }
        if (Double.parseDouble(str_arr[4]) < 0.0 || Double.parseDouble(str_arr[4]) > 100.0) return 0;


        // предметы
        if (!str_arr[5].matches("[ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮйцукенгшщзхъфывапролджэячсмитьбю, ]+")){
            return 0;
        }

        last = Integer.parseInt(str_arr[0]);
        return last;
    }
    static String checkFile(){
        String response = checkFileFormat();
        if (!(response.equals("ok"))){return response;}
        String theLine;
        try(FileReader fp = new FileReader(filePath); BufferedReader bp = new BufferedReader(fp);){
            int pr = 0;
            int last = 0;
            while((theLine = bp.readLine()) != null){
                pr += 1;
                if (pr == 1){
                    if (!theLine.equals("id; ФИО; Город; Школа; Средний балл ЕГЭ; Список предметов;")){
                        filePath = null;
                        return "Ошибка в формате: 1 строка";
                    }
                }
                else{
                    last = checkFileString(theLine, last);
                    if (last == 0){
                        filePath = null;
                        return "Ошибка в формате:" + pr + " строка";
                    }
                }
            }
        }
        catch(Exception ex){
            filePath = null;
            return("Файл не может быть открыт!");
        }
        id = 0;
        return "ok";
    }
    static String openFile(String path){

        filePath = path;
        String response = checkFile();
        if (!(response.equals("ok"))){return response;}
        // переписываем все в копию
        try(FileReader rf = new FileReader(filePath);
            BufferedReader bf = new BufferedReader(rf);
            FileWriter wf = new FileWriter(copyFilePath)){
            String theLine = bf.readLine();
            wf.write(theLine);
            wf.write('\n');
            while((theLine = bf.readLine()) != null){
                wf.write(theLine);
                String ind = "";
                char[] prom = theLine.toCharArray();
                for (int i = 0; i < theLine.length(); i++){
                    if (prom[i] != ';') ind += prom[i];
                    else break;
                }
                id = Integer.parseInt(ind);
                wf.write('\n');
            }
        }
        catch(Exception ex){
            id = 0;
            filePath = null;
            return("Ошибка при открытии файла!");
        }
        if (deleteBackup().equals("not ok")) {
            id = 0;
            return "Ошибка при открытии файла!";
        }
        return "ok";
    }
    static String saveFile(){
        // переписываем из копиии в оригинал
        try(FileReader rf = new FileReader(copyFilePath);
            BufferedReader bf = new BufferedReader(rf);
            FileWriter wf = new FileWriter(filePath)){
            String theLine;
            while((theLine = bf.readLine()) != null){
                wf.write(theLine);
                wf.write('\n');
            }
        }
        catch(Exception ex){
            return "Ошибка при сохранении файла! Убедитесь, что файл открыт.";
        }
        return "Изменения успешно сохранены!";
    }
    static String clearFile(){

        if (!filePath.isEmpty()) {
            try(FileWriter p = new FileWriter(copyFilePath)){
                p.write("id; ФИО; Город; Школа; Средний балл ЕГЭ; Список предметов;");
                id = 0;
                return "ok";
            }
            catch (Exception e) {
                return "Ошибка при попытке очистить файл! Файл изменён не был.";
            }
        }
        else{
            return "Необходимо сначала открыть файл!";
        }
    }
    static String createBackUpFile(){
        try(FileReader rf = new FileReader(filePath);
            BufferedReader bf = new BufferedReader(rf);
            FileWriter wf = new FileWriter("Back-up.txt")){
            String theLine;
            while((theLine = bf.readLine()) != null){
                wf.write(theLine);
                wf.write('\n');
            }
        }
        catch(Exception ex){
            filePath = null;
            return ("Сначала необходимо открыть или создать файл!");
        }
        previousId = id;
        return "Backup-файл успешно создан!";
    }
    static String back(){
        try(FileReader rf = new FileReader("Back-up.txt");
            BufferedReader bf = new BufferedReader(rf);
            FileWriter wf = new FileWriter(filePath)){
            String theLine;
            while((theLine = bf.readLine()) != null){
                wf.write(theLine);
                wf.write('\n');
            }
        }
        catch(Exception ex){
            filePath = null;
            return ("Вы не создавали backup-файл!");
        }
        id = previousId;
        return "Восстановление завершено!";
    }
    static String deleteBackup(){
        File backExist = new File("Back-up.txt");
        if (backExist.isFile()){
            if (!backExist.delete()){
               return "not ok";
            }
        }
        return "ok";
    }

    static void closeProgram(){
        AreYouSure();
        if (filePath.isEmpty()) {System.exit(0);}
        //удаляем копию
        File fc = new File(copyFilePath);
        if (!fc.delete()){
            System.out.println("Произошла ошибка при закрытии!");
        }
        else {System.exit(0);}
    }
    static void AreYouSure(){
        if (!filePath.isEmpty()){
            System.out.println("Сохранить изменения в текущем файле?");
            Scanner in = new Scanner(System.in);
            String answer = in.nextLine();
            if (answer.equals("Да")){
                saveFile();
            }
        }
    }

    public static void main(String[] args){


        GUI gui = new GUI("Data Base");
        gui.setLocationRelativeTo(null);
        gui.setVisible(true);
    }
}