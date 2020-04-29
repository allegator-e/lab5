package SaleOfApartments;

import java.io.IOException;

/**
 * @autors Kurashov Oleg, Savon Galina
 * @version 19.0
 * @year 2020
 */
public class Main {
    public static void main(String[] args) throws IOException{
        try {
            String s = args[0];
            Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Работа программы завершена!")));
            //String s = "C:\\Users\\Hannah\\Desktop\\proga\\lab5_3\\Collection.xml";
            Commander commander = new Commander(new CollectionManager(s));
            commander.interactiveMod();
        } catch(ArrayIndexOutOfBoundsException ex) {
            System.out.println("Путь до файла xml нужно передать через аргумент командной строки.");
            System.exit(1);
        }
    }
}
