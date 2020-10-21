package main.java;

import java.sql.SQLException;

public class Author {
    private int idAUTHOR;
    private String firstName;
    private String lastName;
    private String biography;

    public Author(int idAUTHOR, String firstName, String lastName, String biography) {
        this.idAUTHOR = idAUTHOR;
        this.firstName = firstName;
        this.lastName = lastName;
        this.biography = biography;
    }

    public int getID() { return idAUTHOR; }

    public String toString() {
        return String.format("*\t%-15d%-45s%-45s%-45s\n", idAUTHOR, firstName, lastName, biography);
    }
}
