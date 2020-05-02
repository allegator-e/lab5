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
    private Scanner reader = new Scanner(System.in);
    private Date initDate;
    private Iterator iterator;
    private Integer globalId;
    private Integer nowId;
    private LocalDateTime globalCreationDate;
    protected static HashMap<String, String> manual;

    {
        houses = new TreeMap<>();
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
        File file = new File(collectionPath);
        if (file.exists()) {
            this.xmlCollection = file;
            this.collectionPath = collectionPath;
        } else {
            System.out.println("Файл по указанному пути не существует.");
            System.exit(1);
        }
        this.load();
        this.initDate = new Date();
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
     * Вспомогательные методы для получения полей элемента
     */

    private String readString(String name) {
        System.out.print("Введите " + name +": ");
        return reader.nextLine();
    }

    private String readStringNotNull(String name) {
        System.out.print("Введите " + name +": ");
        String n = reader.nextLine();
        if (n.equals("")) {
            System.out.println("Поле не может быть null или пустой строкой ");
            return readStringNotNull(name);
        } else return n;
    }

    private Number readNumber(String name,String format) {
        String n = readStringNotNull(name);
        try {
            switch (format) {
                case "Float":
                    return Float.parseFloat(n);
                case "Integer":
                    return Integer.parseInt(n);
                case "Long":
                    return Long.parseLong(n);
                default:
                    return null;
            }
        } catch (NumberFormatException ex) {
            System.out.println("Аргумент не является значением типа " + format);
            return readNumber(name,format);
        }
    }

    private Enum readEnam(String name,String type) {
        String n = readString(name);
        try {
            switch (type) {
                case "Furnish":
                    if (n.equals("")) return null;
                    return Furnish.valueOf(n);
                case "View":
                    if (n.equals("")) return null;
                    return View.valueOf(n);
                case "Transport":
                    return Transport.valueOf(n);
                default:
                    return null;
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("Значение поля неверное");
            return readEnam(name,type);
        }
    }

    /**
     * Получает значения элемента в коллекции
     */
    public Flat newFlat() {
        Scanner reader = new Scanner(System.in);
        String name = readStringNotNull("name");
        System.out.println("Введите coordinates: ");
        float x = (Float) readNumber("x","Float");
        while (x <= -227) {
            System.out.println("Значение поля должно быть больше -227");
            x = (Float) readNumber("x","Float");
        }
        Long y = (Long) readNumber("y","Long");
        while (y > 769) {
            System.out.println("Значение поля должно быть меньше 769");
            y = (Long) readNumber("y", "Long");
        }
        long area = (Long) readNumber("area","Long");
        while (area < 0) {
            System.out.println("Значение поля должно быть больше 0");
            area = (Long) readNumber("area", "Long");
        }
        Integer numberOfRooms = (Integer) readNumber("numberOfRooms","Integer");
        while (numberOfRooms < 0) {
            System.out.println("Значение поля должно быть больше 0");
            numberOfRooms = (Integer) readNumber("numberOfRooms","Integer");
        }
        Furnish furnish = (Furnish) readEnam("Furnish (DESIGNER, FINE, LITTLE, BAD, NONE, null)","Furnish");
        View view = (View) readEnam("View (PARK, STREET, BAD, null)","View");
        Transport transport = (Transport) readEnam("Transport (ENOUGH, NORMAL, FEW, LITTLE, NONE)","Transport");
        System.out.println("Введите House: ");
        String nameHouse = readStringNotNull("name");
        int year = (Integer) readNumber("year","Integer");
        while (year < 0) {
            System.out.println("Значение поля должно быть больше 0");
            year = (Integer) readNumber("year","Integer");
        }
        int numberOfFloors = (Integer) readNumber("numberOfFloors","Integer");
        while (numberOfFloors < 0) {
            System.out.println("Значение поля должно быть больше 0");
            numberOfFloors = (Integer) readNumber("numberOfFloors","Integer");
        }
        long numberOfFlatsOnFloor = (Integer) readNumber("numberOfFlatsOnFloor","Integer");
        while (numberOfFlatsOnFloor < 0) {
            System.out.println("Значение поля должно быть больше 0");
            numberOfFlatsOnFloor = (Integer) readNumber("numberOfFlatsOnFloor","Integer");
        }

        int id;
        if (globalId == null) id = ++nowId;
        else id = globalId;
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
            if (!xmlCollection.canWrite())
                System.out.println("Файл защищён от записи. Невозможно сохранить коллекцию.");
            else{
                // Документ JDOM сформирован и готов к записи в файл
                XMLOutputter xmlWriter = new XMLOutputter(Format.getPrettyFormat());
                // сохнаряем в файл
                xmlWriter.output(doc, new FileOutputStream(xmlCollection));
                System.out.println("Коллекция успешно сохранена в файл.");
            }
        } catch (IOException ex) {
            System.out.println("Коллекция не может быть записана в файл");
        }
    }

    /**
     * Считывает и исполняет скрипт из указанного файла.
     * В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме
     */
    public void execute_script(String file, ArrayList<String> commands_of_script) {
        if (scripts.contains(file)) {
            Commander.last_commands.remove(Commander.last_commands.size() - 1);
            System.out.println("Могло произойти зацикливание при исполнении скрипта: " + file + "\nКоманда не будет выполнена. Переход к следующей команде");
        } else {
            File file1 = new File(file);
            if (!file1.exists())
                System.out.println("Файла с таким названием не существует.");
            else if (!file1.canRead())
                System.out.println("Файл защищён от чтения. Невозможно выполнить скрипт.");
            else {
                scripts.add(file);
                try (InputStreamReader commandReader = new InputStreamReader(new FileInputStream(file1))) {
                    StringBuilder s = new StringBuilder();
                    while (commandReader.ready()) s.append((char) commandReader.read());
                    String[] s1 = s.toString().split("\n");
                    commands_of_script.addAll(Arrays.asList(s1));
                } catch (IOException ex) {
                    System.out.println("Невозможно считать скрипт");
                    scripts.remove(scripts.size()-1);
                }
            }
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
        if (!xmlCollection.exists()) {
            System.out.println("Файла по указанному пути не существует.");
            System.exit(1);
        } else if (!xmlCollection.canRead() || !xmlCollection.canWrite()) {
            System.out.println("Файл защищён от чтения и/или записи. Для работы программы нужны оба разрешения.");
            System.exit(1);
        } else {
            if (xmlCollection.length() == 0) {
                System.out.println("Файл пуст.");
                System.exit(1);
            }
                System.out.println("Идёт загрузка коллекции " + xmlCollection.getAbsolutePath());
                // мы можем создать экземпляр JDOM Document из классов DOM, SAX и STAX Builder
            try {
                org.jdom2.Document jdomDocument = createJDOMusingSAXParser(collectionPath);
                Element root = jdomDocument.getRootElement();
                // получаем список всех элементов
                List<Element> labWorkListElements = root.getChildren("Flat");
                // список объектов Student, в которых будем хранить
                // считанные данные по каждому элементу
                Integer maxId = 0;
                for (Element lab : labWorkListElements) {
                    Integer key = Integer.parseInt(lab.getAttributeValue("key"));
                    Integer id = Integer.parseInt(lab.getChildText("id"));
                    if (id > maxId) maxId = id;
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
                System.out.println("Коллекция успешно загружена. Добавлено " + (houses.size() - beginSize) + " элементов.");
                nowId = maxId;
            } catch (IOException | JDOMException ex) {
                System.out.println("Коллекция не может быть загружена. Файл некорректен");
                System.exit(1);
            }
        }
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
