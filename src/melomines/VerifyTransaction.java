package melomines;
import java.security.*;

public class VerifyTransaction {
    private Transaction transaction;

    @SuppressWarnings("unchecked")
    //Take in a transaction and prints it only if the signature is verified.
    public VerifyTransaction(Transaction transaction) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        this.transaction = transaction;
        System.out.println(verifySignature() ? "VERIFIED TRANSACTION\n" + transaction.getStatement() : "NOT VERIFIED");
    }

    //Method for signature verification that initializes with the Public Key, updates the data to be verified and then verifies it using the signature
    public boolean verifySignature() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(transaction.getPublicKey());
        sig.update(transaction.getStatement().getBytes());
        return sig.verify(transaction.getSignature());
    }
}
