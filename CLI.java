package main.java;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Map;

public class CLI {

    private static String allCommandsInfo =
            "> Commands available:\n" +
            "- addPublisher\n" +
            "- browsePublishers\n" +
            "- addAuthor\n" +
            "- browseAuthors\n" +
            "- addBook\n" +
            "- browseBooks\n" +
            "- help\n" +
            "- quit";

    private static Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    public static void main(String[] argv) {
        String command;
        boolean quit = false;

        while (!quit) {
            log(allCommandsInfo + "\n\n");
            log("> ");
            command = getCommand();
            switch (command) {
                case "addpublisher":
                    log("\t> Insert publisher name: ");
                    String publisherName = getString();
                    log("\t> Insert publisher location: ");
                    String publisherLocation = getString();

                    Publisher publisher = bookShopManager.addPublisher(publisherName, publisherLocation);
                    while(quit == false) {
                        log("\t> Possible operations on the publisher:\n" +
                                        "\t- delete\n" +
                                        "\t- quit\n");

                        log("\t> ");
                        command = getCommand();
                        switch (command) {
                            case "delete":
                                bookShopManager.deletePublisher(publisher.getID());
                                quit = true;
                                break;
                            case "quit":
                                quit = true;
                                break;
                        }
                    }
                    quit = false;
                    log("\n");
                    break;
                case "browsepublishers":
                    Map<Integer, Publisher> publisherMap = bookShopManager.browsePublishers();

                    if(publisherMap.size() == 0) {
                        log("\tThere are no publishers in the database\n\n");
                        break;
                    }

                    while(quit == false) {
                        log("\t> Possible operations on the publishers:\n" +
                                "\t- delete\n" +
                                "\t- quit\n");

                        log("\t> ");
                        command = getCommand();
                        switch (command) {
                            case "delete":
                                log("\t\t> Insert the ID of the publisher to be deleted: ");
                                int publisherID = getInt();
                                if(publisherMap.containsKey(publisherID)) {
                                    bookShopManager.deletePublisher(publisherMap.get(publisherID).getID());
                                    publisherMap.remove(publisherID);
                                    log("\tpublisher deleted!\n");
                                }
                                else {
                                    log("\tthe publisher is not present in the database\n");
                                }
                                break;
                            case "quit":
                                quit = true;
                                log("\n");
                                break;
                        }
                    }
                    quit = false;
                    break;
                case "addauthor":
                    log("\t> Insert author first name: ");
                    String authorFirstName = getString();
                    log("\t> Insert author last name: ");
                    String authorLastName = getString();
                    log("\t> Insert author biography: ");
                    String authorBiography = getString();

                    Author author = bookShopManager.addAuthor(authorFirstName, authorLastName, authorBiography);
                    while(quit == false) {
                        log("\t> Possible operations on the author:\n" +
                                "\t- delete\n" +
                                "\t- quit\n");

                        log("\t> ");
                        command = getCommand();
                        switch (command) {
                            case "delete":
                                bookShopManager.deleteAuthor(author.getID());
                                quit = true;
                                break;
                            case "quit":
                                quit = true;
                                break;
                        }
                    }
                    quit = false;
                    log("\n");
                    break;
                case "browseauthors":
                    Map<Integer, Author> authorMap = bookShopManager.browseAuthors();

                    if(authorMap.size() == 0) {
                        log("\tThere are no authors in the database\n\n");
                        break;
                    }

                    while(quit == false) {
                        log("\t> Possible operations on the authors:\n" +
                                "\t- delete\n" +
                                "\t- quit\n");

                        log("\t> ");
                        command = getCommand();
                        switch (command) {
                            case "delete":
                                log("\t\t> Insert the ID of the author to be deleted: ");
                                int authorID = getInt();
                                if(authorMap.containsKey(authorID)) {
                                    bookShopManager.deleteAuthor(authorMap.get(authorID).getID());
                                    authorMap.remove(authorID);
                                    log("\tauthor deleted!\n");
                                }
                                else {
                                    log("\tthe author is not present in the database\n");
                                }
                                break;
                            case "quit":
                                quit = true;
                                log("\n");
                                break;
                        }
                    }
                    quit = false;
                    break;
                case "addbook":
                    log("\t> Insert book title: ");
                    String bookTitle = getString();
                    log("\t> Insert book price: ");
                    float bookPrice = getFloat();
                    log("\t> Insert book category: ");
                    String bookCategory = getString();
                    log("\t> Insert book publication year: ");
                    int bookPublicationYear = getInt();
                    log("\t> Insert book pages number: ");
                    int bookNumPages = getInt();
                    log("\t> Insert book quantity: ");
                    int bookQuantity = getInt();
                    log("\t> Insert ID of the publisher of the book or \"quit\" if you don't know it: ");
                    String bookPublisherIDstring = getString().toLowerCase();
                    if(bookPublisherIDstring.compareTo("quit") == 0) {
                        log("\n");
                        break;
                    }
                    int bookPublisherID = Integer.parseInt(bookPublisherIDstring);

                    log("\t> Insert IDs of the authors of the book or \"quit\" if you don't know them\n");
                    log("\tInsert \"done\" when you are done inserting the IDs: \n");
                    List<Integer> bookAuthorIDs = new ArrayList<>();
                    boolean done = false;
                    while (quit == false && done == false) {
                        log("\t> ");
                        String bookAuthorID = getString().toLowerCase();
                        switch (bookAuthorID) {
                            case "done":
                                done = true;
                                break;
                            case "quit":
                                quit = true;
                                break;
                            default:
                                bookAuthorIDs.add(Integer.parseInt(bookAuthorID));
                        }
                    }

                    if(quit == true) {
                        quit = false;
                        log("\n");
                        break;
                    }

                    Book book = bookShopManager.addBook(
                            bookTitle,
                            bookPrice,
                            bookCategory,
                            bookPublicationYear,
                            bookNumPages,
                            bookQuantity,
                            bookPublisherID,
                            bookAuthorIDs);

                    while(quit == false) {
                        log("\t> Possible operations on the book:\n" +
                                "\t- setQuantity\n" +
                                "\t- increaseQuantity\n" +
                                "\t- decreaseQuantity\n" +
                                "\t- delete\n" +
                                "\t- quit\n");

                        log("\t> ");
                        command = getCommand();

                        try {
                            switch (command) {
                                case "setquantity":
                                    log("\t\t> Insert the new quantity: ");
                                    int newQuantity = getInt();
                                    bookShopManager.setQuantity(book, newQuantity);
                                    log("\tbook quantity updtated!\n");
                                    break;
                                case "increasequantity":
                                    bookShopManager.setQuantity(book, book.getQuantity() + 1);
                                    log("\tbook quantity increased!\n");
                                    break;
                                case "decreasequantity":
                                    bookShopManager.setQuantity(book, book.getQuantity() - 1);
                                    log("\tbook quantity decreased!\n");
                                    break;
                                case "delete":
                                    bookShopManager.deleteBook(book.getID());
                                    quit = true;
                                    break;
                                case "quit":
                                    quit = true;
                                    break;
                            }
                        } catch (IllegalArgumentException iaex) {
                            log("\t" + iaex.getMessage() + "\n");
                        }
                    }
                    quit = false;
                    log("\n");
                    break;
                case "browsebooks":
                    Map<Integer, Book> bookMap = bookShopManager.browseBooks();

                    if(bookMap.size() == 0) {
                        log("\tThere are no books in the database\n\n");
                        break;
                    }

                    while(quit == false) {
                        log("\t> Possible operations on the book:\n" +
                                "\t- setQuantity\n" +
                                "\t- increaseQuantity\n" +
                                "\t- decreaseQuantity\n" +
                                "\t- delete\n" +
                                "\t- quit\n");

                        log("> ");
                        command = getCommand();
                        int bookID;
                        switch (command) {
                            case "setquantity":
                                log("\t\t> Insert the ID of the book to be updated: ");
                                bookID = getInt();
                                if(bookMap.containsKey(bookID)) {
                                    log("\t\t> Insert the new quantity: ");
                                    int newQuantity = getInt();
                                    bookShopManager.setQuantity(bookMap.get(bookID), newQuantity);
                                    log("\tbook quantity updated!\n");
                                }
                                else {
                                    log("\tthe book is not present in the database\n");
                                }
                                break;
                            case "increasequantity":
                                log("\t\t> Insert the ID of the book to be updated: ");
                                bookID = getInt();
                                if(bookMap.containsKey(bookID)) {
                                    Book bookRetrieved = bookMap.get(bookID);
                                    bookShopManager.setQuantity(bookRetrieved, bookRetrieved.getQuantity()+1);
                                    log("\tbook quantity increased!\n");
                                }
                                else {
                                    log("\tthe book is not present in the database\n");
                                }
                                break;
                            case "decreasequantity":
                                log("\t\t> Insert the ID of the book to be updated: ");
                                bookID = getInt();
                                if(bookMap.containsKey(bookID)) {
                                    Book bookRetrieved = bookMap.get(bookID);
                                    bookShopManager.setQuantity(bookRetrieved, bookRetrieved.getQuantity()-1);
                                    log("\tbook quantity decreased!\n");
                                }
                                else {
                                    log("\tthe book is not present in the database\n");
                                }
                                break;
                            case "delete":
                                log("\t\t> Insert the ID of the book to be updated: ");
                                bookID = getInt();
                                if(bookMap.containsKey(bookID)) {
                                    bookShopManager.deleteBook(bookMap.get(bookID).getID());
                                    log("\tbook deleted!\n");
                                }
                                else {
                                    log("\tthe book is not present in the database\n");
                                }
                                break;
                            case "quit":
                                quit = true;
                                break;
                        }
                    }
                    quit = false;
                    break;
                case "quit":
                    quit = true;
                    break;
                case "help":
                default:
                    break;
            }
        }
    }

    private static void log(String stringToLog) {
        System.out.print(stringToLog);
    }

    private static String getCommand() {
        return scanner.next().toLowerCase();
    }

    private static String getString() {
        return scanner.next();
    }

    private static int getInt() {
        return scanner.nextInt();
    }

    private static float getFloat() {
        return scanner.nextFloat();
    }
}
