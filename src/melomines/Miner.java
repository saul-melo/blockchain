package melomines;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Miner implements Serializable {
    private final int id; // Assigned in CreateMiners(), corresponds with Thread number minus 1
    private final double startingBalance;

    public static final Miner BC = new Miner(Integer.MIN_VALUE, 21_000_000); // This instance of Miner will act as the "Blockchain" and give out the block mining rewards
    private static List<Miner> miners;

    public Miner(int id, double startingBalance) {
        this.id = id;
        this.startingBalance = startingBalance;
    }

    public int getId() { return id; }
    public double getStartingBalance() { return startingBalance; }
    public static List<Miner> getMiners() { return miners; }
    public static int getMinerCount() { return miners.size(); }

    public static void sendCoin(Miner sender, Miner recipient, double amount) {
        double senderBalance = Blockchain.calculateBalance(sender);
        if (amount > senderBalance) { // Check if sender has enough coin to make transaction
            System.out.printf("** INSUFFICIENT FUNDS: %s > %f > %s **\n\n", sender, amount, recipient);
            return;
        }
        Blockchain.transactions.add(new Transaction(sender, recipient, amount));
    }

    public static Transaction rewardMiner(Miner recipient) {
        return new Transaction(Miner.BC, recipient, 6.25);
    }

    public static void createMiners() {
        miners = new ArrayList<>();
        Miner one = new Miner(1, 10);
        Miner two = new Miner(2, 10);
        Miner three = new Miner(3, 10);
        Miner four = new Miner(4, 10);
        Miner five = new Miner(5, 10);
        miners.add(one);
        miners.add(two);
        miners.add(three);
        miners.add(four);
        miners.add(five);
    }

    public static Miner getMiner(int id) {
        try {
            return miners.get(id - 1);
        } catch (Exception e) {
            System.out.println("INVALID MINER ID ENTERED TO GETMINER() | RETURNED DEFAULT: MINER 1\n");
            return miners.get(0);
        }
    }

    @Override
    public String toString() {
        if (id != Integer.MIN_VALUE) {
            return "miner" + id;
        } else {
            return "BC";
        }
    }
}
