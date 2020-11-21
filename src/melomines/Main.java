package melomines;

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
            System.out.println("LOAD BLOCKCHAIN EXCEPTION");
            e.printStackTrace();
        }

        // Submit several CreateBlockTasks to an Executor to simulate multiple miners
        int minersCount = 5;
        Blockchain.blockGoal = 10;

        Miner.createMiners();

        ExecutorUtils.startMining(minersCount);
        ExecutorUtils.shutdownAndAwaitTermination();

        System.out.println();
        Blockchain.validateTransactions();
        Blockchain.clearBlockchain();
        PropertiesUtils.clearPrivateFile();
        PropertiesUtils.clearPublicFile();
    }
}
