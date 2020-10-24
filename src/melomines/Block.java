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
    private final String miner;

    private String messageData = "No messages\n";
    private List<Message> blockMessages = new ArrayList<>();

    public Block(String prevHash, int id, long timeStamp, int magicNum) {
        this.prevHash = prevHash;
        this.id = id;
        this.timeStamp = timeStamp;
        this.magicNum = magicNum;
        String threadNumber = Thread.currentThread().getName();
        miner = threadNumber.substring(threadNumber.length() - 1);
    }

    public String getPrevHash() { return prevHash; }
    public List<Message> getBlockMessages() { return blockMessages; }
    public void setMagicNum(int magicNum) { this.magicNum = magicNum; }
    public void setHashingTime(int hashingTime) { this.hashingTime = hashingTime; }

    public void addMessages() {
        // Verify that all messages being added to the block have valid message IDs
        int highestPrevMessageID = -1;
        int curIndex = Blockchain.blockchain.size() - 1;
        List<Message> blockMessageList;
        for (int i = curIndex; i >= 0; i--) {
            blockMessageList = Blockchain.blockchain.get(i).getBlockMessages();
            if (!blockMessageList.isEmpty()) {
                highestPrevMessageID = blockMessageList.get(blockMessageList.size() - 1).getId(); // Assuming messages are entered into blockMessages in order of message ID
                break;
            }
        }
        if (highestPrevMessageID != -1) { // Remove any messages in the Blockchain messages list that have an ID lower than the message ID of the most recent message in a previous block
            for (Message m : Blockchain.messages) {
                if (m.getId() < highestPrevMessageID) {
                    Blockchain.messages.remove(m);
                    System.out.println("REMOVED DUE TO INVALID MESSAGE ID:");
                    System.out.println(m);
                }
            }
        }
        blockMessages = new ArrayList<>(Blockchain.messages);
        messageData = "";
        for (Message m : blockMessages) {
            messageData = messageData.concat(m.toString() + "\n");
        }
        Blockchain.messages.clear();
    }

    public String hashableString() { return prevHash + id + timeStamp + magicNum; }

    @Override
    public String toString() {
        return String.format("Block:\nCreated by miner # %s\nId: %d\nTimestamp: %d\nMagic number: %d\nHash of the previous block:\n%s\nHash of the block:\n%s\nBlock data:\n%sBlock was generating for %d seconds",
                miner, id, timeStamp, magicNum, prevHash, StringUtil.applySha256(hashableString()), messageData, hashingTime);
    }
}