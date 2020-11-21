package melomines;

import java.util.Random;

public class TransactionFactory {

    public static void generateTransaction() {
        Random r = new Random();
        int ranA = getRandomNumberInRange(1, Miner.getMinerCount());
        int ranB = getRandomNumberInRange(1, Miner.getMinerCount());
        double amount = Math.round(r.nextDouble() * 10); // Rounded to generate whole numbers
        Miner.sendCoin(Miner.getMiner(ranA), Miner.getMiner(ranB), amount);
    }

    public static void transactionSim() {
        while (Blockchain.transactions.size() < 10) { // Cap each block at 10 transactions
            try { // Allow the Blockchain to get started before starting the Transaction simulator
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            generateTransaction();
        }
    }

    private static int getRandomNumberInRange(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("MAX NOT > MIN");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}