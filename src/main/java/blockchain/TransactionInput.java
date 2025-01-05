package blockchain;

public class TransactionInput {

    public String transactionOutputId; // Reference to TransactionOutputs -> transactionId
    public TransactionOutput UTXO; // Contains the Unspent transaction output

    // Constructor to initialize the transaction output ID
    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
