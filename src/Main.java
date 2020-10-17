import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {

        // Work with existing file or create one if necessary
        File blockchainFile = new File("Blockchain.txt");
        if (!blockchainFile.isFile()) { // If file doesn't exist, create a file
            try {
                blockchainFile.createNewFile();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        // Load blockchain from file
        try {
            Blockchain.blockchain = (LinkedList<Block>) SerializationUtils.deserialize("Blockchain.txt");
            if (!Blockchain.validateRecursive()) {
                System.out.println("Loaded blockchain not valid, created new empty one");
                Blockchain.blockchain = new LinkedList<>();
            }
        } catch (EOFException eof) {
            System.out.println("FILE IS EMPTY");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("EXCEPTION");
            e.printStackTrace();
        }

        // Submit several CreateBlockTasks to an Executor to mimic multiple miners
        int miners = 5;
        Blockchain.blockGoal = 10;

        ExecutorUtils.startMining(miners);
        ExecutorUtils.shutdownAndAwaitTermination();

        Blockchain.clearBlockchain();
    }
}
