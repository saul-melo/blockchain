import java.io.Serializable;

class Block implements Serializable {
    private final String prevHash;
    private final int id;
    private final long timeStamp;
    private int magicNum;
    private int hashingTime;
    private final String miner;

    public Block(String prevHash, int id, long timeStamp, int magicNum) {
        this.prevHash = prevHash;
        this.id = id;
        this.timeStamp = timeStamp;
        this.magicNum = magicNum;
        String threadNumber = Thread.currentThread().getName();
        miner = threadNumber.substring(threadNumber.length() - 1);
    }

    public String getPrevHash() { return prevHash; }
    public void setMagicNum(int magicNum) { this.magicNum = magicNum; }
    public void setHashingTime(int hashingTime) { this.hashingTime = hashingTime; }

    public String hashableString() { return prevHash + id + timeStamp + magicNum; }

    @Override
    public String toString() {
        return String.format("Block:\nCreated by miner # %s\nId: %d\nTimestamp: %d\nMagic number: %d\nHash of the previous block:\n%s\nHash of the block:\n%s\nBlock was generating for %d seconds",
                miner, id, timeStamp, magicNum, prevHash, StringUtil.applySha256(hashableString()), hashingTime);
    }
}