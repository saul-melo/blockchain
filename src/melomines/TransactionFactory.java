package melomines;

import java.util.Random;

public class TransactionFactory {

    public static void generateTransaction() {
        Random r = new Random();
        int ranA = r.nextInt(Miner.getMinerCount()) + 1;
        int ranB = r.nextInt(Miner.getMinerCount()) + 1;
        double amount = Math.round(r.nextDouble() * 10 * 100.0) / 100.0; // Amount is rounded to three significant figures
//        System.out.println(Miner.getMiner(ranA) + " > " + am + " > " + Miner.getMiner(ranB));
        Miner.sendCoin(Miner.getMiner(ranA), Miner.getMiner(ranB), amount);
    }

    public static void transactionSim() {
        while (Blockchain.transactions.size() < 10) { // Cap each block at 10 transactions
            try { // Allow the Blockchain to get started before starting the transaction simulator
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            generateTransaction();
        }
    }
}