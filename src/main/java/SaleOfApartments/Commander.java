package SaleOfApartments;

import java.io.*;
import java.util.*;


public class Commander {

    private CollectionManager manager;
    private String userCommand;
    private String[] finalUserCommand;
    private Scanner commandReader = new Scanner(System.in);
    private ArrayList<String> commands_of_script =new ArrayList<>();
    protected static ArrayList<String> last_commands = new ArrayList<>();

    {
        userCommand = "";
        for (int i = 0; i < 9; i++) {
            last_commands.add(".");
        }
    }

    public Commander(CollectionManager manager) {
        this.manager = manager;
    }

    public void interactiveMod() {
        try {
            while (!userCommand.equals("exit")) {
                System.out.print(">> ");
                userCommand = commandReader.nextLine();
                finalUserCommand = userCommand.trim().split(" ", 2);
                choseCommand();
            }
        } catch (NoSuchElementException ex){
            System.exit(1);
        }
    }
    public void choseCommand() {
        try {
            last_commands.add(finalUserCommand[0]);
            switch (finalUserCommand[0]) {
                case "":
                case "exit":
                    if (finalUserCommand.length > 1) {
                        if (!finalUserCommand[1].equals("")) throw new InputException();
                        else System.exit(1);
                    }
                    break;
                case "help":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) manager.help();
                        else throw new InputException();
                    } else manager.help();
                    break;
                case "info":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) System.out.println(manager.toString());
                        else throw new InputException();
                    } else System.out.println(manager.toString());
                    break;
                case "show":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) manager.show();
                        else throw new InputException();
                    } else manager.show();
                    break;
                case "insert":
                    manager.insert(finalUserCommand[1]);
                    break;
                case "execute_script":
                    ArrayList<String> commands_of_script =new ArrayList<>();
                    manager.execute_script(finalUserCommand[1], commands_of_script);
                    if (commands_of_script.size() != 0) {
                        for (String command : commands_of_script) {
                            System.out.println(">> " + command);
                            finalUserCommand = command.trim().split(" ", 2);
                            choseCommand();
                        }
                        CollectionManager.scripts.remove(CollectionManager.scripts.size()-1);
                    }
                    break;
                case "update":
                    manager.update(finalUserCommand[1]);
                    break;
                case "remove_key":
                    manager.remove_key(finalUserCommand[1]);
                    break;
                case "clear":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) manager.clear();
                        else throw new InputException();
                    } else manager.clear();
                    break;
                case "save":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) manager.save();
                        else throw new InputException();
                    } else manager.save();
                    break;
                case "remove_greater":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) manager.remove_greater();
                        else throw new InputException();
                    } else manager.remove_greater();
                    break;
                case "history":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) {
                            last_commands.remove(last_commands.size()-1);
                            for(int i = 0; i < 9; i++){
                                if(!last_commands.get(i).equals(".")) System.out.println(last_commands.get(i));
                            }
                            last_commands.add("history");
                        } else throw new InputException();
                    } else {
                        last_commands.remove(last_commands.size()-1);
                        for(int i = 0; i < last_commands.size(); i++){
                            if(!last_commands.get(i).equals(".")) System.out.println(last_commands.get(i));
                        }
                        last_commands.add("history");
                    }
                    break;
                case "remove_greater_key":
                    manager.remove_greater_key(finalUserCommand[1]);
                    break;
                case "average_of_number_of_rooms":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) manager.average_of_number_of_rooms();
                        else throw new InputException();
                    } else manager.average_of_number_of_rooms();
                    break;
                case "group_counting_by_creation_date":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) manager.group_counting_by_creation_date();
                        else throw new InputException();
                    } else manager.group_counting_by_creation_date();
                    break;
                case "count_by_transport":
                    manager.count_by_transport(finalUserCommand[1]);
                    break;
                default:
                    throw new InputException();
            }
            last_commands.remove(0);
        } catch (InputException e) {
            System.out.println("Неопознанная команда. Наберите 'help' для справки.");
            last_commands.remove(last_commands.size()-1);
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Отсутствует аргумент");
            last_commands.remove(last_commands.size()-1);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Commander)) return false;
        Commander commander = (Commander) o;
        return manager.equals(commander.manager);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(manager, userCommand);
        result = 31 * result + Arrays.hashCode(finalUserCommand);
        return result;
    }
}

