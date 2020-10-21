//try to change 
package main.java;

import java.sql.SQLException;
import java.util.List;

public class Book {

    private int idBOOK;
    private String title;
    private float price;
    private String category;
    private int publicationYear;
    private int numPages;
    private int quantity;
    private Publisher publisher;
    private List<Author> authors;

    public Book(int idBOOK,
                String title,
                float price,
                String category,
                int publicationYear,
                int numPages,
                int quantity,
                Publisher publisher,
                List<Author> authors) {
        this.idBOOK = idBOOK;
        this.title = title;
        this.price = price;
        this.category = category;
        this.publicationYear = publicationYear;
        this.numPages = numPages;
        this.quantity = quantity;
        this.publisher = publisher;
        this.authors = authors;
    }

    public int getID() { return idBOOK; }
    public String getTitle() { return title; }
    public float getPrice() { return price; }
    public String getCategory() { return category; }
    public int getPublicationYear() { return publicationYear; }
    public int getNumPages() { return numPages; }
    public int getQuantity() { return quantity; }
    public Publisher getPublisher() { return publisher; }
    public List<Author> getAuthors() { return authors; }

    public void setQuantity(int newQuantity) { quantity = newQuantity; }

    public String toString() {
        return String.format("*\t%-15d%-30s%-30.2f%-30s%-20s%-10d%-10d\n", idBOOK, title, price, category,
                publicationYear, numPages, quantity);
    }
}
