package melomines;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Block implements Serializable {
    private final String prevHash;
    private final int id;
    private final long timeStamp;
    private int magicNum;
    private int hashingTime;
    private final int minerNumber;
    private Transaction reward; // Reward transactions have ID = -1
    private boolean rewardGiven = false; // Ensure reward only given once

    private String transactionData = "No transactions\n";
    private List<Transaction> blockTransactions = new ArrayList<>();

    private double[] minerBalances = new double[Miner.getMinerCount()]; // Stores updated Miner balances as Transactions are screened

    public Block(String prevHash, int id, long timeStamp, int magicNum) {
        this.prevHash = prevHash;
        this.id = id;
        this.timeStamp = timeStamp;
        this.magicNum = magicNum;
        String threadName = Thread.currentThread().getName();
        minerNumber = Integer.parseInt(threadName.substring(threadName.length() - 1)) - 1; // -1 since Thread 1 is Transaction simulator
    }

    public String getPrevHash() { return prevHash; }
    public List<Transaction> getBlockTransactions() { return blockTransactions; }
    public int getRewardedMinerID() { return reward.getRecipientID(); }
    public void setMagicNum(int magicNum) { this.magicNum = magicNum; }
    public void setHashingTime(int hashingTime) { this.hashingTime = hashingTime; }

    private void calculateMinerBalances() {
        for (int i = 0; i < Miner.getMiners().size(); i++) {
            minerBalances[i] = Blockchain.calculateBalance(Miner.getMiner(i + 1));
        }
    }

    // Delete any Transactions that:
    // 1. Have a Transaction ID < highest previous Transaction ID (CASE: Bad actor inserting their own Transaction)
    // 2. Have a sender with insufficient funds to send (CASE: Current block's Transactions not accounted for when Miner.sendCoin() is called during mining)
    public void addTransactions() {
        int highestPrevTransactionID = -1; // CHECK #1
        int curIndex = Blockchain.blockchain.size() - 1;
        List<Transaction> blockTransactionList;
        for (int i = curIndex; i >= 0; i--) { // Calculate Transaction ID of most recent Transaction not in current block's Transactions
            blockTransactionList = Blockchain.blockchain.get(i).getBlockTransactions();
            if (!blockTransactionList.isEmpty()) {
                highestPrevTransactionID = blockTransactionList.get(blockTransactionList.size() - 1).getId(); // Assuming transactions are entered into blockTransactions in ascending ID order
                break;
            }
        }
        if (highestPrevTransactionID != -1) { // Only do Check #1 if Blockchain contains previous Transactions
            for (Transaction t : Blockchain.transactions) {
                if (t.getId() <= highestPrevTransactionID) {
                    Blockchain.transactions.remove(t);
                    System.out.println("REMOVED DUE TO INVALID TRANSACTION ID:");
                    System.out.println(t);
                    System.out.println();
                }
            }
        }

        calculateMinerBalances(); // CHECK #2
        for (Transaction t : Blockchain.transactions) {
            if (minerBalances[t.getSenderID() - 1] - t.getAmountSent() < 0) {
                Blockchain.transactions.remove(t);
                System.out.println("REMOVED DUE TO INSUFFICIENT FUNDS:");
                System.out.println(t);
                System.out.println();
                continue;
            }
            minerBalances[t.getSenderID() - 1] = minerBalances[t.getSenderID() - 1] - t.getAmountSent();
            minerBalances[t.getRecipientID() - 1] = minerBalances[t.getRecipientID() - 1] + t.getAmountSent();
        }

        // Valid Transactions added to Block's blockTransactions list, transactionData String updated
        blockTransactions = new ArrayList<>(Blockchain.transactions);
        transactionData = "";
        for (Transaction t : blockTransactions) {
            transactionData = transactionData.concat(t.toString() + "\n");
        }
        Blockchain.transactions.clear();
    }

    public void giveBlockReward() { // Reward given after current block's transactions are added
        if (!rewardGiven) {
            reward = Miner.rewardMiner(Miner.getMiner(minerNumber));
            rewardGiven = true;
        }
    }

    public String hashableString() { return prevHash + id + timeStamp + magicNum; }

    @Override
    public String toString() {
        return String.format("Block:\nCreated by: miner%d\n%s\nId: %d\nTimestamp: %d\nMagic number: %d\nHash of the previous block:\n%s\nHash of the block:\n%s\nBlock data:\n%sBlock was generating for %d seconds",
                minerNumber, reward.getStatement(), id, timeStamp, magicNum, prevHash, StringUtil.applySha256(hashableString()), transactionData, hashingTime);
    }
}