package kerberos;

// Simulation of Kerberos session with access on file server

public class TicketResponse extends Object {
	// constructor params
	private long mySessionKey;
	private long myNonce;
	private Ticket myResponseTicket;
	// secret key for encrypting this response (simulated)
	private long myResponseKey;
	// current status of object
	private boolean isEncryptedState;

	// constructor
	public TicketResponse(long sessionKey, long nonce, Ticket responseTicket) {
		mySessionKey = sessionKey;
		myNonce = nonce;
		myResponseTicket = responseTicket;
		myResponseKey = -1;
		isEncryptedState = false;
	}

	public long getSessionKey() {
		if (isEncryptedState) {
			printError("Access to encrypted ticket response (getSessionKey)");
		}
		return mySessionKey;
	}

	public long getNonce() {
		if (isEncryptedState) {
			printError("Access to encrypted ticket response (getNonce)");
		}
		return myNonce;
	}

	public Ticket getResponseTicket() {
		if (isEncryptedState) {
			printError("Access to encrypted ticket response (getResponseTicket)");
		}
		return myResponseTicket;
	}

	public boolean encrypt(long key) {
		// encrypt ticketResponse with key
		// returns false if ticketResponse is already encrypted
		boolean encOK = false;
		if (isEncryptedState) {
			printError("TicketResponse is already encrypted");
		} else {
			myResponseKey = key;
			isEncryptedState = true;
			encOK = true;
		}
		return encOK;
	}

	public boolean decrypt(long key) {
		// decrypt ticketResponse with key
		// returns false if key is wrong or ticketResponse already decrypted
		boolean decOK = false;
		if (!isEncryptedState) {
			printError("TicketResponse is already decrypted");
		}
		if (myResponseKey != key) {
			printError("Decrypting ticketResponse with key " + key
					+ " failed");
		} else {
			isEncryptedState = false;
			decOK = true;
		}
		return decOK;
	}

	public boolean isEncrypted() {
		// encrypted = true / decrypted = false
		return isEncryptedState;
	}

	public void printError(String message) {
		System.out.println("+++++++++++++++++++");
		System.out.println("+++++++++++++++++++ error +++++++++++++++++++ "
				+ message + "! TicketResponse key: " + myResponseKey);
		System.out.println("+++++++++++++++++++");
	}

	public void print() {
		System.out.println("********* TicketResponse *******");
		System.out.println("Session key: " + mySessionKey);
		System.out.println("Nonce: " + myNonce);
		myResponseTicket.print();
		System.out.println("Response key: " + myResponseKey);
		if (isEncryptedState) {
			System.out.println("TicketResponse status: encrypted!");
		} else {
			System.out.println("TicketResponse status: decrypted!");
		}
		System.out.println();
	}
}
