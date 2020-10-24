package melomines;
import java.security.*;

public class VerifyMessage {
    private Message message;

    @SuppressWarnings("unchecked")
    //The constructor of VerifyMessage class takes in a message and prints the message only if the signature is verified.
    public VerifyMessage(Message message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        this.message = message;
        System.out.println(verifySignature() ? "VERIFIED MESSAGE\n" + message.getContent() : "NOT VERIFIED");
    }

    //Method for signature verification that initializes with the Public Key, updates the data to be verified and then verifies it using the signature
    public boolean verifySignature() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(message.getPublicKey());
        sig.update(message.getContent().getBytes());
        return sig.verify(message.getSignature());
    }
}
