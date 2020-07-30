/**
 * A Blockchain implementation designed to be easily applied to a wide variety of applications
 *
 * @author: Saul Melo
 * Summer 2020
 */

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Blockchain {
    public static LinkedList<Block> blockchain = new LinkedList<>();
    private static int hashZeros = 0; // The number of consecutive zeros required at the start of a hash. Initiated to 0, updated to maintain a hash duration between a min and max
    public static int blockGoal; // The total number of blocks desired
    private final static int hashTimeMin = 1;
    private final static int hashTimeMax = 2;

    // Given an instance of the Blockchain, mines for the next block to add to the Blockchain. Stops early if the next block is added by another call of createBlock before it succeeds.
    public synchronized static void createBlock(LinkedList<Block> bc) { // Synchronized to prevent simultaneous updating of the Blockchain
        if (!bc.isEmpty()) { // If blockchain is not empty, new block will start off with its prevHash as the hash of the previous block
            bc.add(new Block(StringUtil.applySha256(bc.peekLast().hashableString()), bc.size() + 1, new Date().getTime(), new Random().nextInt()));
        } else { // If blockchain is empty prevHash is set to 0
            bc.add(new Block("0", 1, new Date().getTime(), new Random().nextInt()));
        }
        long start = System.currentTimeMillis();
        // Try random "magic" numbers until the block hash starts with the required number of zeros
        while (!StringUtil.applySha256(Objects.requireNonNull(bc.peekLast()).hashableString()).substring(0, hashZeros).equals("0".repeat(hashZeros))) {
            if (blockchain.size() < bc.size()) { // Only keep mining if new block hasn't already been added to the Blockchain by another miner
                Objects.requireNonNull(bc.peekLast()).setMagicNum(new Random().nextInt());
            } else {
                return;
            }
        }
        int hashDuration = Math.toIntExact((System.currentTimeMillis() - start) / 1000);
        Objects.requireNonNull(bc.peekLast()).setHashingTime(Math.toIntExact(hashDuration));
        if (!blockchain.isEmpty()) { // Ensure that adding the next block will keep the Blockchain valid - USE METHOD TO VALIDATE???
            if (!StringUtil.applySha256(blockchain.peekLast().hashableString()).equals(Objects.requireNonNull(bc.peekLast()).getPrevHash()) // Blockchain last block hash == Next block previous hash
                    || !StringUtil.applySha256(Objects.requireNonNull(bc.peekLast()).hashableString()).substring(0, hashZeros).equals("0".repeat(hashZeros))) { // Next block hash zeros == required zeros
                return;
            }
        }
        blockchain.add(bc.removeLast());
        System.out.println(blockchain.peekLast());
        if (hashDuration < hashTimeMin) { // Regulate mining duration by updating required zeros at start of a hash
            hashZeros++;
            System.out.println("N was increased to " + hashZeros);
        } else if (hashDuration > hashTimeMax) {
            hashZeros--;
            System.out.println("N was decreased to " + hashZeros);
        } else {
            System.out.println("N stays the same");
        }
        System.out.println();
        try { // Blockchain.txt guaranteed to exist due to loading/creating the file before generating blocks
            SerializationUtils.serialize(blockchain, "Blockchain.txt");
        } catch (IOException ioException) {
            ioException.printStackTrace();
            System.out.println("UNABLE TO SAVE TO FILE");
        }
    }

    public static boolean validateRecursive() {
        if (blockchain.peekLast() == null) { // BC: Empty Blockchain
            return true;
        } else {
            return validateRecursiveHelper(blockchain.peekLast());
        }
    }

    private static boolean validateRecursiveHelper(Block curBlock) {
        if (curBlock.getPrevHash().equals("0")) {
            return true;
        } else {
            if (!curBlock.getPrevHash().equals(StringUtil.applySha256(blockchain.get(blockchain.indexOf(curBlock) - 1).hashableString()))) {
                return false;
            } else {
                return validateRecursiveHelper(blockchain.get(blockchain.indexOf(curBlock) - 1));
            }
        }
    }

    public static void clearBlockchain() {
        blockchain.clear();
        try {
            SerializationUtils.serialize(blockchain, "Blockchain.txt");
            System.out.println("BLOCKCHAIN CLEARED");
        } catch (IOException ioException) {
            ioException.printStackTrace();
            System.out.println("UNABLE TO SAVE TO FILE");
        }
    }

    public static void main(String[] args) {
        // Work with existing file or create one if necessary
        File blockchainFile = new File("Blockchain.txt");
        if (!blockchainFile.isFile()) { // If file doesn't exist, create a file
            try {
                System.out.println(blockchainFile.createNewFile() ? "FILE CREATED" : "UNABLE TO CREATE FILE");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        // Load blockchain from file
        try {
            blockchain = (LinkedList<Block>) SerializationUtils.deserialize("Blockchain.txt");
            if (!validateRecursive()) {
                blockchain = new LinkedList<>();
            }
        } catch (EOFException eof) {
            System.out.println("FILE IS EMPTY");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Mimic several entities mining at the same time by submitting several CreateBlockTasks to an Executor
        int miners = 5;
        blockGoal = 5;
        ExecutorService executor = Executors.newFixedThreadPool(miners);
        for (int i = 0; i < blockGoal; i++) {
            executor.submit(new CreateBlockTask());
        }
        executor.shutdown();

        clearBlockchain();
    }
}
