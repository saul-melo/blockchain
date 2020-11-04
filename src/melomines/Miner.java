package melomines;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Miner implements Serializable {
    private final int id;
    private double balance;
    private final double startingBalance;

    public static final Miner BC = new Miner(-1, 21_000_000); // This instance of Miner will act as the "Blockchain" and give out the block mining rewards
    private static List<Miner> miners;

    public Miner(int id, double startingBalance) {
        this.id = id;
        this.startingBalance = startingBalance;
        this.balance = startingBalance;
    }

    public int getId() { return id; }
    public double getStartingBalance() { return startingBalance; }
    public static List<Miner> getMiners() { return miners; }
    public static int getMinerCount() { return miners.size(); }

    public static void sendCoin(Miner sender, Miner recipient, double amount) {
        double senderBalance = Blockchain.calculateBalance(sender);
        if (amount > senderBalance) {
            System.out.printf("** INSUFFICIENT FUNDS: %s > %f > %s **\n\n", sender, amount, recipient);
            return;
        }
        sender.subtractCoin(amount);
        recipient.addCoin(amount);
        Blockchain.transactions.add(new Transaction(sender, recipient, amount));
    }

    public static Transaction rewardMiner(Miner recipient) {
        Miner.BC.subtractCoin(6.25);
        recipient.addCoin(6.25);
        return new Transaction(Miner.BC, recipient, 6.25);
    }

    private void addCoin(double amount) { balance += amount; }

    private void subtractCoin(double amount) { balance -= amount; }

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

    public static Miner getMiner(int id) { return miners.get(id - 1); }

    @Override
    public String toString() {
        if (id != -1) {
            return "miner" + id;
        } else {
            return "BC";
        }
    }
}
