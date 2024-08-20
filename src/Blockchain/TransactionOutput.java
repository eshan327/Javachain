package Blockchain;

import java.security.PublicKey;

public class TransactionOutput {
	public String id;
	public PublicKey recipient; // Rhe new owner of these coins.
	public float value; // The amount of coins they own.
	public String parentTransactionID; // The ID of the transaction this output was created in.

	// Constructor
	public TransactionOutput(PublicKey recipient, float value, String parentTransactionID) {
		this.recipient = recipient;
		this.value = value;
		this.parentTransactionID = parentTransactionID;
		this.id = StringUtil.applySha256(StringUtil.getStringFromKey(recipient) + Float.toString(value) + parentTransactionID);
	}

	// Check if a coin belongs to you.
	public boolean isMine(PublicKey publicKey) {
		return publicKey == recipient;
	}
}