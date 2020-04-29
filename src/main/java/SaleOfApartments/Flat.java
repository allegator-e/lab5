package SaleOfApartments;

import java.time.LocalDateTime;
import java.util.Objects;

public class Flat implements Comparable<Flat>{
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private long area; //Значение поля должно быть больше 0
    private Integer numberOfRooms; //Значение поля должно быть больше 0
    private Furnish furnish; //Поле может быть null
    private View view; //Поле может быть null
    private Transport transport; //Поле не может быть null
    private House house; //Поле не может быть null

    public Flat(Integer id, String name, Coordinates coordinates, LocalDateTime creationDate, long area, Integer numberOfRooms, Furnish furnish, View view, Transport transport, House house){
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.area = area;
        this.numberOfRooms = numberOfRooms;
        this.furnish = furnish;
        this.view = view;
        this.transport = transport;
        this.house = house;
    }

    @Override
    public int compareTo(Flat e) {
        if (area != e.getArea()) return (int) (area - e.getArea());
        if (!numberOfRooms.equals(e.numberOfRooms)) return numberOfRooms - e.getNumberOfRooms();
        if (transport.compareTo(e.getTransport()) != 0) return -(transport.compareTo(e.getTransport()));
        if (furnish != null && e.getFurnish() == null) return 1;
        else if (furnish == null && e.getFurnish() != null) return -1;
        else if (furnish != null && e.getFurnish() != null && furnish.compareTo(e.getFurnish()) != 0) return furnish.compareTo(e.getFurnish());
        if (view != null && e.getView() == null) return 1;
        else if (view == null && e.getView() != null) return -1;
        else if (view != null && e.getView() != null && view.compareTo(e.getView()) != 0) return view.compareTo(e.getView());
        if (coordinates.compareTo(e.getCoordinates()) != 0) return coordinates.compareTo(e.getCoordinates());
        if (house.compareTo(e.getHouse()) != 0) return house.compareTo(e.getHouse());
        return name.compareTo(e.getName());
    }

    public Integer getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public Coordinates getCoordinates(){
        return coordinates;
    }
    public long getArea(){
        return area;
    }
    public Integer getNumberOfRooms(){
        return numberOfRooms;
    }
    public View getView(){
        return view;
    }
    public Furnish getFurnish(){
        return furnish;
    }
    public Transport getTransport() {
        return transport;
    }
    public House getHouse(){
        return house;
    }
    public LocalDateTime getCreationDate(){
        return creationDate;
    }

    @Override
    public String toString() {
        return "Flat {" +
                "id:" + id + ", name:" + name + ", " + coordinates.toString() + ", creation date:" + creationDate + ", area:"
                + area + ", number of rooms:" + numberOfRooms + ", view:" + view +
                ", furnish:" + furnish + ", transport:" + transport + ", " + house.toString() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Flat)) return false;
        Flat flat = (Flat) o;
        return area == flat.area &&
                name.equals(flat.name) &&
                coordinates.equals(flat.coordinates) &&
                creationDate.equals(flat.creationDate) &&
                id.equals(flat.id) &&
                numberOfRooms.equals(flat.numberOfRooms) &&
                view.equals(flat.view) &&
                furnish.equals(flat.furnish) &&
                transport.equals(flat.transport) &&
                house.equals(flat.house);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, area, numberOfRooms, view, transport, house);
    }
}