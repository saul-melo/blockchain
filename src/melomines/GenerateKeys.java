package melomines;

import org.apache.commons.codec.binary.Base64;

import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Properties;

public class GenerateKeys {
    private KeyPairGenerator keyGen;
    private KeyPair pair;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public static Properties props;

    public GenerateKeys() throws NoSuchAlgorithmException {
        this.keyGen = KeyPairGenerator.getInstance("RSA");
        this.keyGen.initialize(1024);
    }

    public void createKeys() {
        this.pair = this.keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public void writeToProperties(KeyType type, byte[] key, int id) throws IOException {
        PropertiesUtils.loadKeys(type);
        String keyString = Base64.encodeBase64String(key);
        props.setProperty(String.valueOf(id), keyString);
        if (type == KeyType.PRIVATE) {
            props.store(new FileWriter("privateKeys.properties"), "Private Keys");
//            System.out.println("PRIVATE AFTER STORING:");
//            PropertiesUtils.printPrivateKeySet();
        } else {
            props.store(new FileWriter("publicKeys.properties"), "Public Keys");
//            System.out.println("PUBLIC AFTER STORING:");
//            PropertiesUtils.printPublicKeySet();
        }
    }
}

