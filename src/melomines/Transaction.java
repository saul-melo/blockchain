package melomines;

import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Properties;

public class Transaction implements Serializable {
    private static int idCounter = 1;

    private final int id;
    private final Miner sender;
    private final Miner recipient;
    private final double amountSent;
    private final String statement;
    private final byte[] signature;
    private PublicKey publicKey;

    public Transaction(Miner sender, Miner recipient, double amountSent) {
        if (sender.getId() == -1) { // Give reward transactions ID = -1 since separate from regular transactions
            id = -1;
        } else {
            this.id = idCounter++;
        }
        this.sender = sender;
        this.recipient = recipient;
        this.amountSent = amountSent;
        this.statement = this.sender + " sent " + this.amountSent + " to " + this.recipient;
        makeTransactionKeys();
        byte[] tempSignature;
        try {
            tempSignature = sign(statement);
        } catch (Exception e) {
            System.out.println(id + " - UNABLE TO MAKE SIGNATURE");
            tempSignature = new byte[0];
            e.printStackTrace();
        }
        this.signature = tempSignature;
    }

    public int getId() { return id; }
    public int getSenderID() { return sender.getId(); }
    public int getRecipientID() { return recipient.getId(); }
    public double getAmountSent() { return amountSent; }

    // SIGNING METHODS
    // Generate and store key pair for the transaction
    public void makeTransactionKeys() {
        GenerateKeys gk;
        try {
            gk = new GenerateKeys();
            gk.createKeys();
            publicKey = gk.getPublicKey();
            gk.writeToProperties(KeyType.PRIVATE, gk.getPrivateKey().getEncoded(), id);
            gk.writeToProperties(KeyType.PUBLIC, gk.getPublicKey().getEncoded(), id);
        } catch (NoSuchAlgorithmException | IOException e) {
            System.out.println(id + "FAILED TO MAKE KEYS");
            System.err.println(e.getMessage());
        }
    }

    // Signs the data using the private key that is stored in privateKeys.properties
    public byte[] sign(String data) throws Exception {
        Signature dsa = Signature.getInstance("SHA1withRSA");
        dsa.initSign(getPrivate());
        dsa.update(data.getBytes());
        return dsa.sign();
    }

    // Retrieve the Private Key from privateKeys.properties
    public PrivateKey getPrivate() throws Exception {
        Properties props = new Properties();
        FileInputStream stream = new FileInputStream("/Users/melo/Desktop/Code/GitHub/MyBlockchain/privateKeys.properties");
        props.load(stream);
        stream.close();
        byte[] keyBytes = Base64.decodeBase64(props.getProperty(String.valueOf(id)));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    // VERIFY METHODS
    public PublicKey getPublicKey() { return publicKey; }
    public String getStatement() { return statement; }
    public byte[] getSignature() { return signature; }

    @Override
    public String toString() {
        return statement;
    }
}
