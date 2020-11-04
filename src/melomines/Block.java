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

    private String transactionData = "No transactions\n";
    private List<Transaction> blockTransactions = new ArrayList<>();

    public Block(String prevHash, int id, long timeStamp, int magicNum) {
        this.prevHash = prevHash;
        this.id = id;
        this.timeStamp = timeStamp;
        this.magicNum = magicNum;
        String threadName = Thread.currentThread().getName();
        minerNumber = Integer.parseInt(threadName.substring(threadName.length() - 1));
    }

    public String getPrevHash() { return prevHash; }
    public List<Transaction> getBlockTransactions() { return blockTransactions; }
    public int getRewardedMinerID() { return reward.getRecipientID(); }
    public void setMagicNum(int magicNum) { this.magicNum = magicNum; }
    public void setHashingTime(int hashingTime) { this.hashingTime = hashingTime; }

    public void addTransactions() {
        // Verify that all transactions being added to the block have valid transaction IDs
        int highestPrevTransactionID = -1;
        int curIndex = Blockchain.blockchain.size() - 1;
        List<Transaction> blockTransactionList;
        for (int i = curIndex; i >= 0; i--) {
            blockTransactionList = Blockchain.blockchain.get(i).getBlockTransactions();
            if (!blockTransactionList.isEmpty()) {
                highestPrevTransactionID = blockTransactionList.get(blockTransactionList.size() - 1).getId(); // Assuming transactions are entered into blockTransactions in ascending ID order
                break;
            }
        }
        if (highestPrevTransactionID != -1) { // Remove any transactions in the Blockchain transaction list that have an ID lower than the ID of the most recent transaction in a previous block
            for (Transaction m : Blockchain.transactions) {
                if (m.getId() <= highestPrevTransactionID) {
                    Blockchain.transactions.remove(m);
                    System.out.println("REMOVED DUE TO INVALID TRANSACTION ID:");
                    System.out.println(m);
                }
            }
        }
        blockTransactions = new ArrayList<>(Blockchain.transactions);
        transactionData = "";
        for (Transaction m : blockTransactions) {
            transactionData = transactionData.concat(m.toString() + "\n");
        }
        Blockchain.transactions.clear();
    }

    public void giveBlockReward() {
        reward = Miner.rewardMiner(Miner.getMiner(minerNumber));
    }

    public String hashableString() { return prevHash + id + timeStamp + magicNum; }

    @Override
    public String toString() {
        return String.format("Block:\nCreated by: miner%d\n%s\nId: %d\nTimestamp: %d\nMagic number: %d\nHash of the previous block:\n%s\nHash of the block:\n%s\nBlock data:\n%sBlock was generating for %d seconds",
                minerNumber, reward.getStatement(), id, timeStamp, magicNum, prevHash, StringUtil.applySha256(hashableString()), transactionData, hashingTime);
    }
}