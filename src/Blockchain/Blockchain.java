package Blockchain;

//import java.security.Security;
import java.util.ArrayList;
//import java.util.Base64;
import java.util.HashMap;
//import com.google.gson.GsonBuilder;
//import java.util.Map;

public class Blockchain {

	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
	public static int difficulty = 3;
	public static float minimumTransaction = 0.1f;
	public static Wallet walletA, walletB;
	public static Transaction genesisTransaction;

	public static void main(String[] args) {
		// Add our blocks to the blockchain ArrayList:
		// Security.addProvider(new
		// org.bouncycastle.jce.provider.BouncyCastleProvider());
		// Set up Bouncy Castle as a security provider.

		// Create wallets:
		walletA = new Wallet();
		walletB = new Wallet();
		Wallet coinbase = new Wallet();

		// Create genesis transaction, which sends 100 to walletA:
		genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
		genesisTransaction.generateSignature(coinbase.privateKey); // Manually sign the genesis transaction.
		genesisTransaction.transactionId = "0"; // Manually set the transaction id.
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value,
				genesisTransaction.transactionId)); // Manually add the transaction's output.
		// Storing first transaction in UTXOs list:
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);

		// Primitive testing -- convert to JUnit testing later!
		Block block1 = new Block(genesis.hash);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
		addBlock(block1);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		Block block2 = new Block(block1.hash);
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
		addBlock(block2);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		Block block3 = new Block(block2.hash);
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20));
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		isChainValid();
	}

	public static Boolean isChainValid() {
		Block currentBlock, previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		// Create temporary list of unspent transactions for this given block:
		HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

		// Loop through blockchain to check hashes:
		for (int i = 1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i - 1);
			// Compare registered hash and calculated hash:
			if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
				System.out.println("#Current Hashes not equal.");
				return false;
			}
			// Compare previous hash and registered previous hash:
			if (!previousBlock.hash.equals(currentBlock.prevHash)) {
				System.out.println("#Previous Hashes not equal.");
				return false;
			}
			// Check if hash is solved:
			if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
				System.out.println("#This block has not been mined.");
				return false;
			}

			// Loop through blockchain transactions:
			TransactionOutput tempOutput;
			for (int t = 0; t < currentBlock.transactions.size(); t++) {
				Transaction currentTransaction = currentBlock.transactions.get(t);

				if (!currentTransaction.verifySignature()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false;
				}
				if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
					return false;
				}

				for (TransactionInput input : currentTransaction.inputs) {
					tempOutput = tempUTXOs.get(input.transactionOutputId);

					if (tempOutput == null) {
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}
					if (input.UTXO.value != tempOutput.value) {
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}

					tempUTXOs.remove(input.transactionOutputId);
				}

				for (TransactionOutput output : currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}

				if (currentTransaction.outputs.get(0).recipient != currentTransaction.recipient) {
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				}
				if (currentTransaction.outputs.get(1).recipient != currentTransaction.sender) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}
			}
		}

		System.out.println("Blockchain is valid");
		return true;
	}

	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}
}

/*
 * public static void main(String[] args) {
 * //add our blocks to the blockchain ArrayList:
 * Security.addProvider(new
 * org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncy castle
 * as a Security Provider
 * 
 * //walletA = new Wallet();
 * //walletB = new Wallet();
 * 
 * //System.out.println("Private and public keys:");
 * //System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
 * //System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
 * 
 * createGenesis();
 * 
 * //Transaction transaction = new Transaction(walletA.publicKey,
 * walletB.publicKey, 5);
 * //transaction.signature = transaction.generateSignature(walletA.privateKey);
 * 
 * //System.out.println("Is signature verified:");
 * //System.out.println(transaction.verifiySignature());
 * 
 * }
 */

// System.out.println("Trying to Mine block 1... ");
// addBlock(new Block("Hi im the first block", "0"));