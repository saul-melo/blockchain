import java.util.LinkedList;

class CreateBlockTask implements Runnable {

    @Override
    public void run() {
        while (Blockchain.blockchain.size() < Blockchain.blockGoal) {
            LinkedList<Block> blockchain = new LinkedList<>(Blockchain.blockchain);
            Blockchain.createBlock(blockchain);
        }
    }
}
