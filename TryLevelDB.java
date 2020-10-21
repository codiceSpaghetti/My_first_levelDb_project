package main.java;
import static org.iq80.leveldb.impl.Iq80DBFactory.*;
import org.iq80.leveldb.*;
import java.io.*;


import java.io.File;
import java.io.IOException;


public class TryLevelDB {
    private static DB db = null;

    public static void main(String[] args){

        openDB();
        try {
            addEmployee("Alice", "Manager", "34");
            addEmployee("Bob", "Specialist", "28");
            addEmployee("Trudy", "Intern", "24");
            addEmployee("Igor", "Owner", "45");

            addExam(1, "Performance Evaluation", "21/01/2021", "26");
            addExam(1, "Large Scale and Distributed System", "02/0/2021", "30");
            addExam(1, "Electronics and Comunication System", "24/02/2021", "28");

            Snapshot snapshot = db.getSnapshot();

            //change alice
            changeAttr("employee:1:age", "32");
            changeAttr("employee:3:role", "Junior member");

            deleteEmployee(2);


            System.out.println("-------------------- SHOWING UPDATED DB --------------------");
            showDb();
            System.out.println("-------------------- SHOWING SNAPSHOT --------------------");
            showSnapshot(snapshot);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeDB();
        }
    }

    private static void deleteEmployee(int id) {
        String[] attributes = {"name", "role", "age"};
        try (WriteBatch batch = db.createWriteBatch()) {

            for (String attr : attributes)
                batch.delete(bytes("employee:" + id + ":" + attr));
            db.write(batch);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        private static void addExam(int student, String course, String date, String mark) throws IOException {
        int id = nextId("studExam", 2);
        try (WriteBatch batch = db.createWriteBatch()) {
            batch.put(bytes("studExam:" + student + ":" + id + ":Course"), bytes(course));
            batch.put(bytes("studExam:" + student + ":" + id + ":Date"), bytes(date));
            batch.put(bytes("studExam:" + student + ":" + id + ":Mark"), bytes(mark));

            db.write(batch);
        }catch(Exception ex) {ex.printStackTrace();}

    }


    private static void addEmployee(String name, String role, String age) throws IOException {
        int id = nextId("employee", 1);
        try (WriteBatch batch = db.createWriteBatch()) {
            batch.put(bytes("employee:" + id + ":name"), bytes(name));
            batch.put(bytes("employee:" + id + ":role"), bytes(role));
            batch.put(bytes("employee:" + id + ":age"), bytes(age));

            db.write(batch);
        }catch(Exception ex) {ex.printStackTrace();}

    }

    private static int nextId(String entity, int posId) throws IOException {
        int id = 1;

        DBIterator keyIterator = db.iterator();
        keyIterator.seek(bytes(entity)); // moves the iterator to the keys starting with "employee"

        try {
            String actualKey;
            String [] actualKeySplit, prevKeySplit = null;
            while (keyIterator.hasNext()) {
                actualKey = asString(keyIterator.peekNext().getKey()); // key arrangement : employee:$employee_id:$attribute_name = $value
                actualKeySplit = actualKey.split(":"); // split the key

                keyIterator.next();

                if (!actualKeySplit[0].equals(entity) || !keyIterator.hasNext()) {  // breaking condition : prefix is not "employee"
                    id = Integer.parseInt(prevKeySplit[posId]) + 1;
                    break;
                }
                prevKeySplit = actualKeySplit;
            }
        } finally {
            keyIterator.close();
        }
        //id = (id==0)?1:id;
        return id; // return resulted employee ids
    }

    private static void changeAttr(String key, String value){
        db.delete(bytes(key));
        db.put(bytes(key), bytes(value));
    }

    private static void showDb(){
        showSnapshot(null);

    }

    private static void showSnapshot(Snapshot snapshot){
        DBIterator iterator = null;
        try{
            if(snapshot != null)
                iterator = db.iterator(new ReadOptions().snapshot(snapshot));
            else iterator = db.iterator();

            int i = 0;
            for(iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());
                String value = asString(iterator.peekNext().getValue());
                System.out.println(key + " = " + value);
                i++;
                if(i == 3){
                    i = 0;
                    System.out.print("\n");
                }
            }
            iterator.close();
        }catch(Exception ex) { ex.printStackTrace();}
    }

    private static void openDB(){
        Options options = new Options();
        options.createIfMissing(true);
        try{
            db = factory.open(new File("myFirstLevelDb"), options);
        }catch(IOException ioe) { closeDB(); }
    }

    private static void closeDB(){
        try{
            if(db != null) db.close();
        }catch(IOException ioe) { ioe.printStackTrace(); }
    }

    public void putValue(String key, String value){
        db.put(bytes(key), bytes(value));
    }

    public String getValue(String key){
        return asString(db.get(bytes(key)));
    }

    public void deleteValue(String key){
        db.delete(bytes(key));
    }

}
