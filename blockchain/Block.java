package blockchain;

import java.util.ArrayList;
import java.util.Date;

public class Block {

	public String hash, prevHash, merkleRoot;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); // Data will be a simple message.
	public long timeStamp; // Number of milliseconds since 1/1/1970.
	public int nonce;

	// Constructor.
	public Block(String previousHash) {
		this.prevHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash(); // Making sure to do this after setting the other values.
	}

	// Calculate new hash based on blocks contents.
	public String calculateHash() {
		return StringUtil.applySha256(prevHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
	}

	// Increases nonce value until hash target is reached.
	public void mineBlock(int difficulty) {
		merkleRoot = StringUtil.getMerkleRoot(transactions);
		String target = StringUtil.getDifficultyString(difficulty); // Create a string with difficulty * "0"

		while (!hash.substring(0, difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
		}
		System.out.println("Block Mined: " + hash);
	}

	// Add transactions to this block.
	public boolean addTransaction(Transaction transaction) {
		// Process transaction and check if valid; ignore if genesis block.
		if (transaction == null)
			return false;
		if ((!"0".equals(prevHash)) && (transaction.processTransaction() != true)) {
			System.out.println("Transaction failed to process. Discarded.");
			return false;
		}

		transactions.add(transaction);
		System.out.println("Transaction Successfully added to Block");
		return true;
	}
}