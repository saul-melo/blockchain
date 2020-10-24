package melomines;
/**
 * A Blockchain implementation designed to be easily applied to a wide variety of applications
 *
 * @author: Saul Melo
 * Summer 2020
 */

import java.io.*;
import java.util.*;

public class Blockchain {
    public static LinkedList<Block> blockchain = new LinkedList<>();
    private static int hashZeros = 0; // The number of consecutive zeros required at the start of a hash. Initiated to 0, updated to maintain a hash duration between a min and max
    public static int blockGoal; // The desired length of blockchain, updated every time new blocks are set to be mined
    private final static int hashTimeMin = 1;
    private final static int hashTimeMax = 2;

    public static List<Message> messages = new ArrayList<>();

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
//        // IS THIS CHECK NECESSARY?
//        if (!blockchain.isEmpty()) { // Ensure that adding the next block will keep the Blockchain valid
//            if (!StringUtil.applySha256(blockchain.peekLast().hashableString()).equals(Objects.requireNonNull(bc.peekLast()).getPrevHash()) // Hash of the last block in Blockchain == Next block previous hash
//                    || !StringUtil.applySha256(Objects.requireNonNull(bc.peekLast()).hashableString()).substring(0, hashZeros).equals("0".repeat(hashZeros))) { // New block hash zeros == required zeros
//                return;
//            }
//        }
        blockchain.add(bc.removeLast()); // Add new block to Blockchain
        if (!messages.isEmpty()) Objects.requireNonNull(blockchain.peekLast()).addMessages(); // Add messages to new block if any were sent during mining
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
            System.out.println("UNABLE TO SAVE TO FILE\n");
            ioException.printStackTrace();
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

    // Check that each message in each block of the blockchain has a valid message ID, meaning it is greater than the highest messageID of the message in the previous block with messages
    public static void validateMessages() {
        List<Message> invalid = new ArrayList<>();
        int prevHighest = -1;
        int curHighest = -1;
        for (Block b : blockchain) {
            if (!b.getBlockMessages().isEmpty()) {
                for (Message m : b.getBlockMessages()) {
                    if (m.getId() <= prevHighest) {
                        invalid.add(m);
                        continue;
                    }
                    if (m.getId() > curHighest) {
                        curHighest = m.getId();
                    }
                }
                prevHighest = curHighest;
            }
        }
        System.out.println(invalid.isEmpty() ? "ALL MESSAGES HAVE VALID ID" : "INVALID MESSAGES:\n" + invalid);
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

    public static void printBlockchain() {
        for (Block b : blockchain) {
            System.out.println("vvvvvvvvvvv");
            System.out.print(b);
            System.out.println("\n^^^^^^^^^^^\n");
        }
    }
}
