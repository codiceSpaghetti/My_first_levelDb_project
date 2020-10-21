package main.java;

import java.sql.SQLException;

public class Publisher {
    private int idPUBLISHER;
    private String name;
    private String location;

    public Publisher(int idPUBLISHER, String name, String location) {
        this.idPUBLISHER = idPUBLISHER;
        this.name = name;
        this.location = location;
    }

    public int getID() { return idPUBLISHER; }

    public String toString() {
        return String.format("*\t%-15d%-45s%-45s\n", idPUBLISHER, name, location);
    }
}
