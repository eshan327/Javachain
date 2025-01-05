package blockchain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

    public PrivateKey privateKey;
    public PublicKey publicKey;

    public HashMap<String, TransactionOutput> UTXOs = new HashMap<>(); // Only UTXOs owned by this wallet

    // Constructor
    public Wallet() {
        generateKeyPair();
    }

    // Generates a new key pair (private and public keys)
    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();

            // Set the public and private keys from the key pair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Returns the balance of this wallet by summing the values of all UTXOs owned by this wallet
    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : Javachain.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            if (UTXO.isMine(publicKey)) { // If output belongs to me (if coins belong to me)
                UTXOs.put(UTXO.id, UTXO); // Add it to our list of unspent transactions
                total += UTXO.value;
            }
        }
        return total;
    }

    // Generates and returns a new transaction from this wallet
    public Transaction sendFunds(PublicKey recipient, float value) {
        if (getBalance() < value) { // Gather balance and check funds
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }

        // Create array list of inputs
        ArrayList<TransactionInput> inputs = new ArrayList<>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if (total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        // Remove the used inputs from the UTXO list
        for (TransactionInput input : inputs) {
            UTXOs.remove(input.transactionOutputId);
        }

        return newTransaction;
    }
}
