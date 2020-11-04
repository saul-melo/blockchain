package melomines;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtils {
    public static void loadKeys(KeyType type) throws IOException {
        GenerateKeys.props = new Properties();
        String path = "/Users/melo/Desktop/Code/GitHub/MyBlockchain/";
        if (type == KeyType.PRIVATE) {
            path = path.concat("privateKeys.properties");
        } else {
            path = path.concat("publicKeys.properties");
        }
        FileInputStream str = new FileInputStream(path);
        GenerateKeys.props.load(str);
        str.close();
    }

    public static void printPublicKeySet() throws IOException {
        Properties p = new Properties();
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream("/Users/melo/Desktop/Code/GitHub/MyBlockchain/publicKeys.properties"));
        p.load(stream);
        p.entrySet().forEach(System.out::println);
    }

    public static void printPrivateKeySet() throws IOException {
        Properties p = new Properties();
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream("/Users/melo/Desktop/Code/GitHub/MyBlockchain/privateKeys.properties"));
        p.load(stream);
        p.entrySet().forEach(System.out::println);
    }

    public static void clearPrivateFile() {
        try (FileWriter fileWriter = new FileWriter("privateKeys.properties")) {
            Properties p = new Properties();
            p.store(fileWriter, "Private Keys");
            System.out.println("PRIVATE KEY FILE CLEARED");
        } catch (IOException ioe) {
            System.out.println("UNABLE TO CLEAR PRIVATE KEY FILE");
            ioe.getStackTrace();
        }
    }

    public static void clearPublicFile() {
        try (FileWriter fileWriter = new FileWriter("publicKeys.properties")) {
            Properties p = new Properties();
            p.store(fileWriter, "Public Keys");
            System.out.println("PUBLIC KEY FILE CLEARED");
        } catch (IOException ioe) {
            System.out.println("UNABLE TO CLEAR PUBLIC KEY FILE");
            ioe.getStackTrace();
        }
    }
}

enum KeyType { PRIVATE, PUBLIC }

