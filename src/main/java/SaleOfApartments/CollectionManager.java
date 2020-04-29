package SaleOfApartments;



import jdk.nashorn.internal.objects.Global;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.management.modelmbean.XMLParseException;
import java.io.*;
import java.time.*;
import java.util.*;

public class CollectionManager {
    private TreeMap<Integer, Flat> houses;
    protected static ArrayList<String> scripts = new ArrayList<>();
    private String collectionPath;
    private File xmlCollection;
    private Date initDate;
    private boolean wasStart;
    private Iterator iterator;
    private Integer globalId;
    private LocalDateTime globalCreationDate;
    protected static HashMap<String, String> manual;

    {
        houses = new TreeMap<>();
        iterator = houses.entrySet().iterator();
        globalId = null;
        globalCreationDate = null;
        manual = new HashMap<>();
        manual.put("help","вывести справку по доступным командам");
        manual.put("info","вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
        manual.put("show","вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        manual.put("insert null {element}","добавить новый элемент с заданным ключом");
        manual.put("update id {element}","обновить значение элемента коллекции, id которого равен заданному");
        manual.put("remove_key null","удалить элемент из коллекции по его ключу");
        manual.put("clear","очистить коллекцию");
        manual.put("save","сохранить коллекцию в файл");
        manual.put("execute_script file_name","считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме");
        manual.put("exit","завершить программу (без сохранения в файл)");
        manual.put("remove_greater {element}"," удалить из коллекции все элементы, превышающие заданный");
        manual.put("history","вывести последние 9 команд (без их аргументов)");
        manual.put("remove_greater_key null","удалить из коллекции все элементы, ключ которых превышает заданный");
        manual.put("average_of_number_of_rooms", "вывести среднее значение поля numberOfRooms для всех элементов коллекции");
        manual.put("group_counting_by_creation_date", "сгруппировать элементы коллекции по значению поля creationDate, вывести количество элементов в каждой группе");
        manual.put("count_by_transport transport", "вывести количество элементов, значение поля transport которых равно заданному");
    }

    public CollectionManager(String collectionPath)  {
        try {
            File file = new File(collectionPath);
            if (file.exists()) {
                this.xmlCollection = file;
                this.collectionPath = collectionPath;
            }
            else throw new FileNotFoundException();
        } catch (FileNotFoundException ex) {
            System.out.println("Путь до файла xml нужно передать через аргумент командной строки. Файл по указанному пути не существует.");
            System.exit(1);
        }
        this.load();
        this.initDate = new Date();
        wasStart = true;
    }


    /**
     * Выводит на экран список доступных для пользователя команд
     */
    public void help() {
        System.out.println("Доступные к использованию команды:");
        manual.keySet().forEach(p -> System.out.println(p + " - " + manual.get(p)));
    }

    /**
     * Выводит все элементы коллекции
     */
    public void show() {
        if (houses.size() != 0) {
            houses.forEach((k,p) -> System.out.println("key: " + k + ", " + p));
        }
        else System.out.println("В коллекции отсутствуют элементы. Выполнение команды невозможно.");
    }

    /**
     * Получает значения элемента в коллекции
     */
    public Flat newFlat() {
        Scanner reader = new Scanner(System.in);
        System.out.print("Введите name: ");
        String name = reader.nextLine();
        while (name.equals("")) {
            System.out.println("Поле не может быть null или пустой строкой ");
            System.out.print("Введите name: ");
            name = reader.nextLine();
        }
        System.out.println("Введите coordinates: ");
        String a;
        boolean p = false;
        float x = 0;
        while (!p) {
            System.out.print("Введите x: ");
            a = reader.nextLine();
            try {
                x = Float.parseFloat(a);
                if (x > -227)
                    p = true;
                else {
                    System.out.println("Значение поля должно быть больше -227");
                }
            } catch (NumberFormatException e) {
                System.out.println("Аргумент не является значением типа float");
            }
        }
        Long y = null;
        while (p) {
            System.out.print("Введите y: ");
            a = reader.nextLine();
            try {
                y = Long.parseLong(a);
                if (y <= 769)
                    p = false;
                else {
                    System.out.println("Значение поля должно быть меньше 769");
                }
            } catch (NumberFormatException e) {
                System.out.println("Аргумент не является значением типа Long");
            }
        }
        long area = 0;
        while (!p) {
            System.out.print("Введите area: ");
            a = reader.nextLine();
            try {
                area = Long.parseLong(a);
                if (area > 0)
                    p = true;
                else {
                    System.out.println("Значение поля должно быть больше 0");
                }
            } catch (NumberFormatException e) {
                System.out.println("Аргумент не является значением типа long");
            }
        }
        Integer numberOfRooms = null;
        while (p) {
            System.out.print("Введите numberOfRooms: ");
            a = reader.nextLine();
            try {
                numberOfRooms = Integer.parseInt(a);
                if (numberOfRooms > 0)
                    p = false;
                else {
                    System.out.println("Значение поля должно быть больше 0");
                }
            } catch (NumberFormatException e) {
                System.out.println("Аргумент не является значением типа Integer");
            }
        }
        System.out.print("Введите Furnish (DESIGNER, FINE, LITTLE, BAD, NONE, null): ");
        String furnish_s = reader.nextLine();
        while (!furnish_s.equals("") && !furnish_s.equals("DESIGNER") && !furnish_s.equals("FINE") && !furnish_s.equals("LITTLE") && !furnish_s.equals("BAD") && !furnish_s.equals("NONE")) {
            System.out.println("Значение поля неверное");
            System.out.print("Введите Furnish (DESIGNER, FINE, LITTLE, BAD, NONE, null): ");
            furnish_s = reader.nextLine();
        }
        Furnish furnish = null;
        if (!furnish_s.equals("")) furnish = Furnish.valueOf(furnish_s);
        System.out.print("Введите View (PARK, STREET, BAD, null): ");
        String view_s = reader.nextLine();
        while (!view_s.equals("") && !view_s.equals("STREET") && !view_s.equals("PARK") && !view_s.equals("BAD")) {
            System.out.println("Значение поля неверное");
            System.out.print("Введите View (PARK, STREET, BAD, null): ");
            view_s = reader.nextLine();
        }
        View view = null;
        if (!view_s.equals("")) view = View.valueOf(view_s);
        System.out.print("Введите Transport (ENOUGH, NORMAL, FEW, LITTLE, NONE): ");
        String transport_s = reader.nextLine();
        while (!transport_s .equals("FEW") && !transport_s .equals("NONE") && !transport_s .equals("LITTLE") && !transport_s .equals("NORMAL") && !transport_s .equals("ENOUGH")) {
            System.out.println("Значение поля неверное");
            System.out.print("Введите Transport (ENOUGH, NORMAL, FEW, LITTLE, NONE): ");
            transport_s  = reader.nextLine();
        }
        Transport transport = Transport.valueOf(transport_s);
        System.out.println("Введите House: ");
        System.out.print("Введите name: ");
        String nameHouse = reader.nextLine();
        while (nameHouse.equals("")) {
            System.out.println("Поле не может быть null");
            System.out.print("Введите name: ");
            nameHouse = reader.nextLine();
        }
        int year = 0;
        while (!p) {
            System.out.print("Введите year: ");
            a = reader.nextLine();
            try {
                year = Integer.parseInt(a);
                if (year > 0)
                    p = true;
                else {
                    System.out.println("Значение поля должно быть больше 0");
                }
            } catch (NumberFormatException e) {
                System.out.println("Аргумент не является значением типа int");
            }
        }
        int numberOfFloors = 0;
        while (p) {
            System.out.print("Введите numberOfFloors: ");
            a = reader.nextLine();
            try {
                numberOfFloors = Integer.parseInt(a);
                if (numberOfFloors> 0)
                    p = false;
                else {
                    System.out.println("Значение поля должно быть больше 0");
                }
            } catch (NumberFormatException e) {
                System.out.println("Аргумент не является значением типа int");
            }
        }
        long numberOfFlatsOnFloor = 0;
        while (!p) {
            System.out.print("Введите numberOfFlatsOnFloor: ");
            a = reader.nextLine();
            try {
                numberOfFlatsOnFloor = Long.parseLong(a);
                if (numberOfFlatsOnFloor > 0)
                    p = true;
                else {
                    System.out.println("Значение поля должно быть больше 0");
                }
            } catch (NumberFormatException e) {
                System.out.println("Аргумент не является значением типа long");
            }
        }

        int id = 0;
        if (globalId == null) {
            while (p) {
                p = false;
                Random random = new Random();
                id = random.nextInt(10000) + 1;
                for (Flat h : houses.values()) {
                    if (h.getId() == id) {
                        p = true;
                        break;
                    }
                }
            }
        } else id = globalId;
        LocalDateTime creationDate;
        if (globalCreationDate == null) {
            LocalDate d = LocalDate.now();
            LocalTime t = LocalTime.now();
            creationDate = LocalDateTime.of(d,t);
        } else creationDate = globalCreationDate;
        System.out.println("Все значения элемента успешно получены");
        return new Flat(id, name, new Coordinates(x, y), creationDate, area, numberOfRooms, furnish, view, transport, new House(nameHouse, year, numberOfFloors, numberOfFlatsOnFloor));
    }

    public void insert(String key) {
        try {
            Integer key_num = Integer.parseInt(key);
            if (!houses.containsKey(key_num)) {
                houses.put(key_num, newFlat());
                System.out.println("Элемент успешно добавлен");
            } else System.out.println("Элемент с данным ключом уже существует.");
        }catch (NumberFormatException ex){
            System.out.println("Аргумент не является значением типа Integer");
        }
    }

    /**
     * Обновляет значение элемента коллекции, id которого равен заданному
     * @param n : Id элемента, который требуется заменить
     */
    public void update(String n){
        if (houses.size() != 0) {
            try {
                Integer id = Integer.valueOf(n);
                boolean b = false;
                iterator = houses.keySet().iterator();
                globalId = id;
                while (iterator.hasNext()) {
                    Integer key = (Integer) iterator.next();
                    if (houses.get(key).getId().equals(id)) {
                        globalCreationDate = houses.get(key).getCreationDate();
                        houses.replace(key, newFlat());
                        System.out.println("Элемент коллекции успешно обновлен.");
                        b = true;
                        break;
                    }
                }
                globalId = null;
                globalCreationDate = null;
                if (!b) System.out.println("В коллекции не найдено элемента с указанным id.");
            } catch (NumberFormatException ex) {
                System.out.println("Аргумент не является значением типа Integer");
            }
        } else System.out.println("В коллекции отсутствуют элементы. Выполнение команды не возможно.");

    }

    /**
     * Удаляет элемент из коллекции по его ключу
     * @param n : ключ соответствующего элемента, который требуется удалить
     */
    public void remove_key(String n){
        if (houses.size() != 0) {
            try {
                boolean b = false;
                Integer n_num = Integer.parseInt(n);
                iterator = houses.keySet().iterator();
                while (iterator.hasNext()) {
                    Integer key = (Integer) iterator.next();
                    if (key.equals(n_num)) {
                        houses.remove(key);
                        System.out.println("Элемент коллекции успешно удален.");
                        b = true;
                        break;
                    }
                }
                if (!b) System.out.println("В коллекции не найдено элемента с указанным ключом.");
                else System.out.println("Команда успешно выполнена.");
            } catch (NumberFormatException e) {
                System.out.println("Аргумент не является значением типа Integer");
            }
        }
        else System.out.println("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
    }

    /**
     * Удаляет все элементы коллекции.
     */
    public void clear() {
        houses.clear();
        System.out.println("Коллекция очищена.");
    }

    /**
     * Сериализует коллекцию в файл json.
     */
    public void save() {
        try  {
            Document doc = new Document();
            // создаем корневой элемент с пространством имен
            doc.setRootElement(new Element("Flats"));
            // формируем JDOM документ из объектов Student
            for (Integer key : houses.keySet()) {
                Element element = new Element("Flat");
                element.setAttribute("key", String.valueOf(key));
                element.addContent(new Element("id").setText( String.valueOf(houses.get(key).getId())));
                element.addContent(new Element("name").setText(houses.get(key).getName()));
                Element element_c = new Element("Coordinates");
                element_c.addContent(new Element("x").setText(String.valueOf(houses.get(key).getCoordinates().getX())));
                element_c.addContent(new Element("y").setText(String.valueOf(houses.get(key).getCoordinates().getY())));
                element.addContent(element_c);
                element.addContent(new Element("creationDate").setText(String.valueOf(houses.get(key).getCreationDate())));
                element.addContent(new Element("area").setText(String.valueOf(houses.get(key).getArea())));
                element.addContent(new Element("numberOfRooms").setText(String.valueOf(houses.get(key).getNumberOfRooms())));
                element.addContent(new Element("furnish").setText(String.valueOf(houses.get(key).getFurnish())));
                element.addContent(new Element("view").setText(String.valueOf(houses.get(key).getView())));
                element.addContent(new Element("transport").setText(String.valueOf(houses.get(key).getTransport())));
                Element element_d = new Element("House");
                element_d.addContent(new Element("name").setText(houses.get(key).getHouse().getName()));
                element_d.addContent(new Element("year").setText(String.valueOf(houses.get(key).getHouse().getYear())));
                element_d.addContent(new Element("numberOfFloors").setText(String.valueOf(houses.get(key).getHouse().getNumberOfFloors())));
                element_d.addContent(new Element("numberOfFlatsOnFloor").setText(String.valueOf(houses.get(key).getHouse().getNumberOfFlatsOnFloor())));
                element.addContent(element_d);
                doc.getRootElement().addContent(element);
            }
            if (!xmlCollection.canWrite()) throw new SecurityException();
            // Документ JDOM сформирован и готов к записи в файл
            XMLOutputter xmlWriter = new XMLOutputter(Format.getPrettyFormat());
            // сохнаряем в файл
            xmlWriter.output(doc, new FileOutputStream(xmlCollection));
            System.out.println("Коллекция успешно сохранена в файл.");
        } catch (IOException ex) {
            System.out.println("Возникла непредвиденная ошибка. Коллекция не может быть записана в файл");
        } catch (SecurityException ex) {
            System.out.println("Файл защищён от записи. Невозможно сохранить коллекцию.");
        }
    }

    /**
     * Считывает и исполняет скрипт из указанного файла.
     * В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме
     */
    public void execute_script(String file, ArrayList<String> commands_of_script) throws IOException {
        try {
            if (file.charAt(1) != '/') file = System.getenv("PWD") + "/"+ file;
            if (scripts.contains(file)) throw new RecursiveException();
            File file1 = new File(file);
            if (!file1.canRead()) throw new SecurityException();
            scripts.add(file);
            try (InputStreamReader commandReader = new InputStreamReader(new FileInputStream(file1))) {
                StringBuilder s = new StringBuilder();
                while (commandReader.ready()) s.append((char)commandReader.read());
                String[] s1 = s.toString().split("\n");
                commands_of_script.addAll(Arrays.asList(s1));
            }
        } catch (SecurityException ex) {
            System.out.println("Файл защищён от чтения. Невозможно сохранить выполнить скрипт.");
        } catch (FileNotFoundException ex) {
            System.out.println("Скрипт по указанному пути не существует");
            scripts.remove(scripts.size()-1);
        } catch (RecursiveException ex) {
            Commander.last_commands.remove(Commander.last_commands.size()-1);
            System.out.println("Могло произойти зацикливание при исполнении скрипта: " + file + "\nКоманда не будет выполнена. Переход к следующей команде");
        }
    }

    /**
     * Удаляет из коллекции все элементы, превышающие заданный
     */
    public void remove_greater() {
        if (houses.size() != 0) {
            Flat o = newFlat();
            iterator = houses.keySet().iterator();
            while (iterator.hasNext()) {
                Integer i = (Integer) iterator.next();
                if (houses.get(i).compareTo(o) > 0) {
                    iterator.remove();
                    houses.remove(i);
                }
            }
            System.out.println("Команда успешно выполнена.");
        } else System.out.println("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
    }

    /**
     *  Удаляет из коллекции все элементы, ключ которых превышает заданный
     * @param n : ключ, относительно которого удалются все элементы с ключом большим чем этот
     */
    public void remove_greater_key(String n) {
        try {
            Integer n_num = Integer.parseInt(n);
            if (houses.size() != 0) {
                iterator = houses.keySet().iterator();
                while (iterator.hasNext()) {
                    Integer s = (Integer) iterator.next();
                    if (s.compareTo(n_num) > 0) {
                        iterator.remove();
                        houses.remove(s);
                    }
                }
                System.out.println("Команда успешно выполнена.");
            } else System.out.println("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
        }catch (NumberFormatException ex){
            System.out.println("Аргумент не является значением типа Integer");
        }
    }

    /**
     * Выводит среднее значение поля numberOfRooms для всех элементов коллекции
     */
    public void average_of_number_of_rooms() {
        if (houses.size() != 0) {
            float sum_number_of_rooms = 0;
            for (Integer key: houses.keySet()) {
                sum_number_of_rooms += houses.get(key).getNumberOfRooms();
            }
            System.out.println("Cреднее значение поля numberOfRooms для всех элементов коллекции: " + sum_number_of_rooms/houses.size());
        }
        else System.out.println("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
    }

    /**
     * Группирует элементы коллекции по значению поля creationDate, вывести количество элементов в каждой группе
     */
    public void group_counting_by_creation_date(){
        if (houses.size() != 0) {
            HashMap<LocalDateTime,Integer> creationDates = new HashMap<>();
            for (Integer key: houses.keySet()) {
                if (creationDates.containsKey(houses.get(key).getCreationDate())) {
                    creationDates.replace(houses.get(key).getCreationDate(),creationDates.get(houses.get(key).getCreationDate()) + 1);
                } else creationDates.put(houses.get(key).getCreationDate(),1);
            }
            creationDates.forEach((k,p) -> System.out.println(k + ": " + p));
        } else System.out.println("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
    }

    /**
     * Выводит количество элементов, значение поля transport которых равно заданному
     * @param transport_s : значение поля transport
     */
    public void count_by_transport(String transport_s){
        if (houses.size() != 0) {
            try {
                int count_by_transport = 0;
                Transport transport = Transport.valueOf(transport_s);
                for (Integer key : houses.keySet()) {
                    if (houses.get(key).getTransport().equals(transport))
                        count_by_transport++;
                }
                System.out.println("Количество элементов, значение поля transport которых равно " + transport_s + ": " + count_by_transport);
            } catch (IllegalArgumentException | NullPointerException ex) {
                System.out.println("Значение поля Transport некорректно. Возможные значения: FEW, NONE, LITTLE, NORMAL, ENOUGH.");
            }
        } else System.out.println("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
    }

    /**
    *  Десериализует коллекцию из файла json.
    */
    public void load() {
        int beginSize = houses.size();
        try {
            if (!xmlCollection.exists()) throw new FileNotFoundException();
        } catch (FileNotFoundException ex) {
            System.out.println("Файла по указанному пути не существует.");
            if (!wasStart) System.exit(1);
            else return;
        }
        try {
            if (!xmlCollection.canRead() || !xmlCollection.canWrite()) throw new SecurityException();
        } catch (SecurityException ex) {
            System.out.println("Файл защищён от чтения и/или записи. Для работы программы нужны оба разрешения.");
            if (!wasStart) System.exit(1);
            else return;
        }
        try {
            if (xmlCollection.length() == 0) throw new XMLParseException("");
        } catch (XMLParseException ex) {
            System.out.println("Файл пуст.");
            if (!wasStart) System.exit(1);
            else return;
        }
        try {
            System.out.println("Идёт загрузка коллекции " + xmlCollection.getAbsolutePath());
            // мы можем создать экземпляр JDOM Document из классов DOM, SAX и STAX Builder
            org.jdom2.Document jdomDocument = createJDOMusingSAXParser(collectionPath);
            Element root = jdomDocument.getRootElement();
            // получаем список всех элементов
            List<Element> labWorkListElements = root.getChildren("Flat");
            // список объектов Student, в которых будем хранить
            // считанные данные по каждому элементу
            for (Element lab : labWorkListElements) {

                Integer key = Integer.parseInt(lab.getAttributeValue("key"));
                Integer id = Integer.parseInt(lab.getChildText("id"));
                String name = lab.getChildText("name");
                List<Element> lab_c = lab.getChildren("Coordinates");
                float x = Float.parseFloat(lab_c.get(0).getChildText("x"));
                Long y = Long.parseLong(lab_c.get(0).getChildText("y"));
                LocalDateTime creationDate = LocalDateTime.parse(lab.getChildText("creationDate"));
                long area = Long.parseLong(lab.getChildText("area"));
                Integer numberOfRooms = Integer.parseInt(lab.getChildText("numberOfRooms"));
                Furnish furnish = null;
                String furnish_s = lab.getChildText("furnish");
                if (!furnish_s.equals("null")) furnish = Furnish.valueOf(furnish_s);
                View view = null;
                String view_s = lab.getChildText("view");
                if (!view_s.equals("null")) view = View.valueOf(view_s);
                Transport transport = Transport.valueOf(lab.getChildText("transport"));
                List<Element> lab_d = lab.getChildren("House");
                String nameHouse = lab_d.get(0).getChildText("name");
                int year = Integer.parseInt(lab_d.get(0).getChildText("year"));
                int numberOfFloors = Integer.parseInt(lab_d.get(0).getChildText("numberOfFloors"));
                long numberOfFlatsOnFloor = Long.parseLong(lab_d.get(0).getChildText("numberOfFlatsOnFloor"));
                houses.put(key, new Flat(id, name, new Coordinates(x, y), creationDate, area, numberOfRooms, furnish, view, transport, new House(nameHouse, year, numberOfFloors, numberOfFlatsOnFloor)));
            }
        }catch (Exception e) {
            System.out.println("Не удалось загрузить коллекцию. Всё очеь-очень плохо!");
            if (!wasStart) System.exit(1);
            else return;

        }
        System.out.println("Коллекция успешно загружена. Добавлено " + (houses.size() - beginSize) + " элементов.");
    }

    private static org.jdom2.Document createJDOMusingSAXParser(String fileName)
            throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        return saxBuilder.build(new File(fileName));
    }

    /**
     * Выводит информацию о коллекции.
     */
    @Override
    public String toString() {
        return "Тип коллекции: " + houses.getClass() +
                "\nДата инициализации: " + initDate +
                "\nКоличество элементов: " + houses.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CollectionManager)) return false;
        CollectionManager manager = (CollectionManager) o;
        return houses.equals(manager.houses) &&
                xmlCollection.equals(manager.xmlCollection) &&
                initDate.equals(manager.initDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(houses, initDate);
    }
}
