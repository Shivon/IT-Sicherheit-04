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
		// get tgsTicketResponse
		this.tgsServer = "myTGS";
		long nonce = generateNonce();
		TicketResponse tgsTicketResponse = myKDC.requestTGSTicket(userName, tgsServer, nonce);
		if (tgsTicketResponse == null) {
			tgsTicketResponse.printError("Invalid login.");
			return false;
		}
		this.currentUser = userName;

		// "decrypt"
		long decryptKey = generateSimpleKeyFromPassword(password);
		if (!tgsTicketResponse.decrypt(decryptKey)) {
			tgsTicketResponse.printError("Decrypting with key " + decryptKey + " failed.");
			return false;
		}

		// check for replay attack
		if (nonce != tgsTicketResponse.getNonce()) {
			tgsTicketResponse.printError("Invalid nonce.");
			return false;
		}

		this.tgsTicket = tgsTicketResponse.getResponseTicket();
		this.tgsSessionKey = tgsTicketResponse.getSessionKey();
		tgsTicketResponse.print();
		return true;
	}

	public boolean showFile(Server fileServer, String filePath) {
		// get serverTicketResponse
		long nonce = generateNonce();
		Auth auth = new Auth(currentUser, System.currentTimeMillis());
		auth.encrypt(tgsSessionKey);
		auth.print();
		TicketResponse serverTicketResponse = myKDC.requestServerTicket(tgsTicket, auth, fileServer.getName(), nonce);

		if (serverTicketResponse == null) {
			serverTicketResponse.printError("Invalid login.");
			return false;
		}

		if (nonce != serverTicketResponse.getNonce()) {
			serverTicketResponse.printError("Invalid nonce.");
			return false;
		}
		serverTicketResponse.print();

		// approach server and show file
		Auth serverAuth = new Auth(currentUser, System.currentTimeMillis());
		serverAuth.encrypt(serverTicketResponse.getSessionKey());
		serverAuth.print();
		return fileServer.requestService(serverTicketResponse.getResponseTicket(), serverAuth, "showFile", filePath);
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
