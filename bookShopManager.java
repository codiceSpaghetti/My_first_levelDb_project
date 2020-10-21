package main.java;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;
import org.iq80.leveldb.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

public class bookShopManager {

    private static DB openDB() {
        DB db = null;
        Options options = new Options();
        options.createIfMissing(true);
        try {
            db = factory.open(new File("myFirstLevelDb"), options);
        } catch (IOException ioe) {
            log("Connection Failed!\n");
        } finally {
            return db;
        }
    }

    //-----------------------------------------------PUBLISHER----------------------------------------------------------
    public static Map<Integer, Publisher> browsePublishers() {
        Map<Integer, Publisher> publisherMap = new HashMap<>();

        try (DB db = openDB();
             DBIterator iterator = db.iterator()) {

            // extract and view the list of Publishers
            log("**************************************************" +
                    "PUBLISHERS**************************************************\n");
            log(String.format("*\t%-15s%-45s%-45s\n", "Publisher ID", "Name", "Location"));

            int i = 0;

            iterator.seek(bytes("publisher"));
            String[] attributes = new String[2];
            for(iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());
                attributes[i] = asString(iterator.peekNext().getValue());

                String[] keySplit = key.split(":");
                if(!keySplit[0].equals("publisher"))
                    continue;
                i++;
                if(i == 2) {
                    i = 0;
                    publisherMap.put(Integer.parseInt(keySplit[1]), viewPublisher(Integer.parseInt(keySplit[1]), attributes[1], attributes[0]));
               }

            }
            log("**********************************************************************" +
                    "****************************************\n");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return publisherMap;
    }


    private static Publisher findPublisher(DB db, int idPUBLISHER) {
        Publisher publisher = null;

        String[] attributes = getAttributesById(db, "publisher", idPUBLISHER, 2);

        log(String.format("*\t%-15s%-45s%-45s\n", "Publisher ID", "Name", "Location"));
        publisher = viewPublisher(idPUBLISHER, attributes[0], attributes[1]);

        return publisher;
    }


    public static Publisher addPublisher(String name, String location) {
        Publisher newPublisher = null;
        int id = nextId("publisher", 1);

        try (   DB db = openDB();
                WriteBatch batch = db.createWriteBatch()) {
            batch.put(bytes("publisher:" + id + ":name"), bytes(name));
            batch.put(bytes("publisher:" + id + ":location"), bytes(location));

            db.write(batch);
            log("**************************************************" +
                    "PUBLISHER***************************************************\n");

            log(String.format("*\t%-15s%-45s%-45s\n", "Publisher ID", "Name", "Location"));
            newPublisher = viewPublisher(id, name, location);
            log("**********************************************************************" +
                    "****************************************\n");

        }catch(Exception ex) {ex.printStackTrace();}

        return newPublisher;
    }


    private static Publisher viewPublisher(int idPUBLISHER, String name, String location) {
        Publisher publisher = new Publisher(idPUBLISHER, name, location);

        log(publisher.toString());
        return publisher;
    }


    protected static void deletePublisher(int idPUBLISHER) {
        String[] attributes = {"name", "location"};

        try (DB db = openDB();
             WriteBatch batch = db.createWriteBatch();
             DBIterator iterator = db.iterator()){

            for (String attr: attributes) {
                batch.delete(bytes("publisher:" + idPUBLISHER + ":" + attr));
                db.write(batch);
            }
            // delete all Books related to Publisher

            for (iterator.seek(bytes("book")); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());
                String[] keySplit = key.split(":");

                if(!keySplit[0].equals("book"))
                    break;

                int idBook = Integer.parseInt(keySplit[1]);
                String attributeName = keySplit[2];
                String value = asString(iterator.peekNext().getValue());

                if(attributeName.equals("idPublisher") && Integer.parseInt(value) == idPUBLISHER)
                    deleteBook(db, idBook);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        //-----------------------------------------------AUTHOR-------------------------------------------------------------

    public static Map<Integer, Author> browseAuthors() {
            Map<Integer, Author> authorMap = new HashMap<>();

            try (DB db = openDB();
             DBIterator iterator = db.iterator()) {

            log("*************************************************************************" +
                    "AUTHORS************************************************************************\n");
            log(String.format("*\t%-15s%-45s%-45s%-45s\n", "Author ID", "First Name", "Last Name", "Biography"));

            int i = 0;

            iterator.seek(bytes("author"));
            String[] attributes = new String[3];
            for(iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());
                attributes[i] = asString(iterator.peekNext().getValue());
                String[] keySplit = key.split(":");
                if(!keySplit[0].equals("author"))
                    return authorMap;
                i++;
                if(i == 3) {
                    i = 0;
                    authorMap.put(Integer.parseInt(keySplit[1]), viewAuthor(Integer.parseInt(keySplit[1]), attributes[2], attributes[1], attributes[0]));
               }

            }
            log("********************************************************************************" +
                    "************************************************************************\n");

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            return authorMap;
        }


    public static Author addAuthor(String firstName, String lastName, String biography) {
        Author newAuthor = null;
        int id = nextId("author", 1);

        try (   DB db = openDB();
                WriteBatch batch = db.createWriteBatch()) {
            batch.put(bytes("author:" + id + ":firstName"), bytes(firstName));
            batch.put(bytes("author:" + id + ":lastName"), bytes(lastName));
            batch.put(bytes("author:" + id + ":biography"), bytes(biography));

            db.write(batch);
            log("**************************************************" +
                    "PUBLISHER***************************************************\n");

            log(String.format("*\t%-15s%-45s%-45s\n", "Publisher ID", "Name", "Location"));
            newAuthor = viewAuthor(id, firstName, lastName, biography);
            log("**********************************************************************" +
                    "****************************************\n");

        }catch(Exception ex) {ex.printStackTrace();}

        return newAuthor;
    }


    public static void deleteAuthor(int idAUTHOR) {
        String[] attributes = {"firstName", "lastName", "biography"};
        try (DB db = openDB();
             WriteBatch batch = db.createWriteBatch();) {

            for (String attr: attributes) {
                batch.delete(bytes("author:" + idAUTHOR + ":" + attr));
                db.write(batch);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static Author viewAuthor(int idAUTHOR, String firstName, String lastName, String biography) {
        Author author = new Author(idAUTHOR, firstName, lastName, biography);

        log(author.toString());
        return author;
    }


    private static Author findAuthor(DB db, int idAUTHOR) {
        Author author = null;

        String[] attributes = getAttributesById(db, "author", idAUTHOR, 3);

        log(String.format("*\t%-15s%-45s%-45s%-45s\n", "Author ID", "First Name", "Last Name", "Biography"));
        author = viewAuthor(idAUTHOR, attributes[2], attributes[1], attributes[0]);

        return author;
    }

            //-----------------------------------------------BOOK---------------------------------------------------------------


    public static Map<Integer, Book> browseBooks() {
        Map<Integer, Book> bookMap = new HashMap<>();

        try (DB db = openDB();
             DBIterator iterator = db.iterator()) {

        // extract and view the list of Books
            log("***************************************************************************" +
                    "BOOK****************************************************************************" +
                    "****************************************\n");

            int i = 0;
            String[] attributes = new String[7];

            for(iterator.seek(bytes("book")); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());
                attributes[i] = asString(iterator.peekNext().getValue());
                String[] keySplit = key.split(":");
                if(!keySplit[0].equals("book"))
                    return bookMap;

                i++;
                if(i == 7) {
                    i = 0;
                    bookMap.put(Integer.parseInt(keySplit[1]), viewBook(db, Integer.parseInt(keySplit[1]), attributes[6], Float.parseFloat(attributes[3]), attributes[0], Integer.parseInt(attributes[4]), Integer.parseInt(attributes[2]), Integer.parseInt(attributes[5]), Integer.parseInt(attributes[1])));
                }

            }

            log("********************************************************************************" +
            "*************************************************************************************" +
            "******************************\n");

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return bookMap;
    }


    private static Book viewBook(DB db, int idBOOK,
                                 String title,
                                 float price,
                                 String category,
                                 int publicationYear,
                                 int numPages,
                                 int quantity,
                                 int idPUBLISHER) {

        Publisher publisher = null;
        // view Authors information
        List<Author> authors = new ArrayList<>();

        try (
             DBIterator iterator = getIterator(db,"book", idBOOK)
        ) {
            // view Publisher information
            publisher = findPublisher(db, idPUBLISHER);

            DBIterator authorIterator = getIterator(db, "bookAuthor", idBOOK);
            while (authorIterator.hasNext()) {
                String key = asString(authorIterator.peekNext().getKey());
                String[] keySplit = key.split(":");
                if (!keySplit[0].equals("bookAuthor")) {
                    break;
                } else if (Integer.parseInt(keySplit[1]) != idBOOK) {
                    authorIterator.next();
                    continue;
                } else {
                    String val = asString(authorIterator.peekNext().getValue());
                    String[] value = val.split(",");
                    for(int i = 0; i < Integer.parseInt(keySplit[2]); i++) {
                        authors.add(findAuthor(db, Integer.parseInt(value[i])));
                    }
                    break;
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // create new book
        Book book = new Book(idBOOK, title, price, category, publicationYear, numPages,
                quantity, publisher, authors);

        // view Book information
        log(String.format("*\t%-15s%-30s%-30s%-30s%-20s%-10s%-10s\n", "Book ID", "Title", "Price", "Category",
                "Publication Year", "Pages Number", "Quantity"));
        log(book.toString());
        return book;
    }


    public static Book addBook(String title,
                               float price,
                               String category,
                               int publicationYear,
                               int numPages,
                               int quantity,
                               int idPUBLISHER,
                               List<Integer> idAUTHORS) {
        Book newBook = null;
        int id = nextId("book", 1);

        try (DB db = openDB();
             WriteBatch batch = db.createWriteBatch();
        DBIterator iterator = db.iterator()) {

            // insert the Book
            batch.put(bytes("book:" + id + ":title"), bytes(title));
            batch.put(bytes("book:" + id + ":price"), bytes(String.valueOf(price)));
            batch.put(bytes("book:" + id + ":category"), bytes(category));
            batch.put(bytes("book:" + id + ":publicationYear"), bytes(String.valueOf(publicationYear)));
            batch.put(bytes("book:" + id + ":numPages"), bytes(String.valueOf(numPages)));
            batch.put(bytes("book:" + id + ":quantity"), bytes(String.valueOf(quantity)));
            batch.put(bytes("book:" + id + ":idPublisher"), bytes(String.valueOf(idPUBLISHER)));


            // insert the Authors of the Book
            int count = 0;
            String listAuthor = "";
            for (Integer idAUTHOR : idAUTHORS) {
                listAuthor += idAUTHOR;

                count++;
                if(count < idAUTHORS.size())
                    listAuthor += ",";
            }
            batch.put(bytes("bookAuthor:" + id + ":" + count), bytes(String.valueOf(listAuthor)));


            db.write(batch);

            for(iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());
                log(key + "\n");
            }

            log("***************************************************************************" +
                    "BOOK****************************************************************************" +
                    "****************************************\n");
            newBook = viewBook(db, id, title, price, category, publicationYear, numPages, quantity, idPUBLISHER);
            log("********************************************************************************" +
                    "*************************************************************************************" +
                    "******************************\n");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return newBook;
    }


    public static void deleteBook(int idBOOK) {
        try (DB db = openDB()) {

            deleteBook(db, idBOOK);
        } catch (IOException ioe) { ioe.printStackTrace(); }
    }


    public static void deleteBook(DB db, int idBOOK) {
        String[] attributes = {"title", "price", "category", "publicationYear", "numPages", "quantity", "idPublisher"};
        try (WriteBatch batch = db.createWriteBatch();
             DBIterator iterator = db.iterator()) {

            for (String attr: attributes)
                batch.delete(bytes("book:" + idBOOK + ":" + attr));

            db.write(batch);

            int i = 1;
            for(iterator.seek(bytes("bookAuthor:" + idBOOK)); iterator.hasNext(); iterator.next()){
                batch.delete(bytes("bookAuthor:" + idBOOK + ":" + i));
                i++;
                String key = asString(iterator.peekNext().getKey());
                if(key.equals("author:" + idBOOK + ":" + i))
                    break;
            }

            db.write(batch);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected static void setQuantity(Book book, int newQuantity) throws IllegalArgumentException {
        if(newQuantity < 0)
            throw new IllegalArgumentException("> Can't have negative quantity for a book.\n");

        try (DB db = openDB(); ) {
            db.put(bytes("book:" + book.getID() + ":quantity"), bytes(String.valueOf(newQuantity)));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private static DBIterator getIterator(DB db, String entity, int id){
        try (DBIterator iterator = db.iterator()
        ) {
            iterator.seek(bytes(entity));
            for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());
                String[] keySplit = key.split(":");
                if (keySplit[0].equals(entity) && Integer.parseInt(keySplit[1]) == id) {
                    return iterator;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static String[] getAttributesById(DB db, String entity, int id, int numAttr) {
        String[] attributes = new String[numAttr];
        try (DBIterator iterator = getIterator(db, entity, id)
        ) {
            for (int i = 0; i < numAttr; i++) {
                attributes[i] = asString(iterator.peekNext().getValue());
                iterator.next();
            }
            return attributes;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static int nextId(String entity, int posId){
        int id = 1;

        try (   DB db = openDB();
                DBIterator keyIterator = db.iterator()) {

            keyIterator.seek(bytes(entity)); // moves the iterator to the keys starting with "employee"
            String actualKey;
            String[] actualKeySplit, prevKeySplit = null;
            while (keyIterator.hasNext()) {
                actualKey = asString(keyIterator.peekNext().getKey()); // key arrangement : employee:$employee_id:$attribute_name = $value
                actualKeySplit = actualKey.split(":"); // split the key

                keyIterator.next();

                if (!actualKeySplit[0].equals(entity) || !keyIterator.hasNext()) {  // breaking condition : prefix is not "employee"
                    if(prevKeySplit == null)
                        id = 1;
                    else
                        id = Integer.parseInt(prevKeySplit[posId]) + 1;
                    break;
                }
                prevKeySplit = actualKeySplit;
            }
        } catch (IOException ex){ex.printStackTrace();}

        return id; // return resulted employee ids
    }


    private static void log(String stringToLog) {
        System.out.print(stringToLog);
    }

}
