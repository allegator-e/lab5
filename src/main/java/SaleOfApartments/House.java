package SaleOfApartments;

import java.util.Objects;

public class House implements Comparable<House> {
    private String name; //Поле не может быть null
    private int year; //Значение поля должно быть больше 0
    private int numberOfFloors; //Значение поля должно быть больше 0
    private long numberOfFlatsOnFloor; //Значение поля должно быть больше 0

    House(String name, int year, int numberOfFloors, long numberOfFlatsOnFloor){
        this.name = name;
        this.year = year;
        this.numberOfFloors = numberOfFloors;
        this.numberOfFlatsOnFloor = numberOfFlatsOnFloor;
    }

    public String getName() {
        return name;
    }
    public int getYear() {
        return year;
    }
    public int getNumberOfFloors() {
        return numberOfFloors;
    }
    public long getNumberOfFlatsOnFloor() {
        return numberOfFlatsOnFloor;
    }

    @Override
    public String toString() {
        return "House {" +
                "name:" + name + ", year:" + year + ", number of floors:" + numberOfFloors + ", number of flats on floor:"
                + numberOfFlatsOnFloor + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof House)) return false;
        House house = (House) o;
        return year == house.getYear() &&
                numberOfFloors == house.getNumberOfFloors() &&
                numberOfFlatsOnFloor == house.getNumberOfFlatsOnFloor() &&
                name.equals(house.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, year, numberOfFloors, numberOfFlatsOnFloor);
    }

    @Override
    public int compareTo(House o) {
        if (year - o.getYear() != 0) return year - o.getYear();
        if (numberOfFloors - o.getNumberOfFloors() != 0) return numberOfFloors - o.getNumberOfFloors();
        if (numberOfFlatsOnFloor - o.getNumberOfFlatsOnFloor() != 0) return (int) (numberOfFlatsOnFloor - o.getNumberOfFlatsOnFloor());
        return name.compareTo(o.getName());
    }
}
