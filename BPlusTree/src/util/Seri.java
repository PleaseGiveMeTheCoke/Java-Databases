package util;

import tableDef.Table;

import java.io.*;

public  class Seri {
    public static void Serialize(Table t,String fileName) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
        oos.writeObject(t);
        oos.close();
    }
    public static Table deSerialize(String fileName) throws IOException, ClassNotFoundException {
        File file = new File(fileName);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        Table t = (Table)ois.readObject();
        return t;

    }
}
