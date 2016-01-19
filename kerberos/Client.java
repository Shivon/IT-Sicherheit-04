package kerberos;

// Simulation of Kerberos session with access on file server

import java.util.*;

public class Client extends Object {
	// constructor param
	private KDC myKDC;
	// needs to be saved at login
	private String currentUser;
	private Ticket tgsTicket = null;
	private String tgsServer;
	// K(C,TGS)
	private long tgsSessionKey;

	// constructor
	public Client(KDC kdc) {
		myKDC = kdc;
	}

	public boolean login(String userName, char[] password) {
		// Get ticketResponse
		this.tgsServer = "myTGS";
		long nonce = generateNonce();
		TicketResponse ticketResponse = myKDC.requestTGSTicket(userName, tgsServer, nonce);
		if (ticketResponse == null) {
			System.out.println("Invalid login.");
			return false;
		}
		this.currentUser = userName;

		// "decrypt"
		long decryptKey = generateSimpleKeyFromPassword(password);
		if (!ticketResponse.decrypt(decryptKey)) {
			System.out.println("Decrypting with key " + decryptKey + " failed.");
			return false;
		}

		// check for replay attack
		if (nonce != ticketResponse.getNonce()) {
			System.out.println("Nonce invalid.");
			return false;
		}
		
		this.tgsTicket = ticketResponse.getResponseTicket();
		this.tgsSessionKey = ticketResponse.getSessionKey();
		return true;
	}

	public boolean showFile(Server fileServer, String filePath) {
		/* ToDo */
		return false;
	}

	/* *********** helping methods **************************** */

	private long generateSimpleKeyFromPassword(char[] passwd) {
		// returns distinct key derived from password (simulated as long value)
		long pwKey = 0;
		if (passwd != null) {
			for (int i = 0; i < passwd.length; i++) {
				pwKey = pwKey + passwd[i];
			}
		}
		return pwKey;
	}

	private long generateNonce() {
		// returns new random value
		long rand = (long) (100000000 * Math.random());
		return rand;
	}
}
