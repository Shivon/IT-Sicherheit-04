package kerberos;

// Simulation of Kerberos session with access on file server

import java.util.*;

public class KDC extends Object {
	private final long tenHoursInMillis = 36000000;
	private final long fiveMinutesInMillis = 300000;

	/* *********** data base simulation **************************** */

	private String tgsName;
	// C
	private String user;
	// K(C)
	private long userPasswordKey;
	// S
	private String serverName;
	// K(S)
	private long serverKey;
	// K(TGS)
	private long tgsKey;

	// constructor
	public KDC(String name) {
		tgsName = name;
		// generate own key for TGS (top secret!!!)
		tgsKey = generateSimpleKey();
	}

	public String getName() {
		return tgsName;
	}

	/* *********** initialization methods **************************** */

	public long serverRegistration(String sName) {
		// register server in data base
		// returns new secret key for server
		serverName = sName;
		// generate own key for server (top secret!!!)
		serverKey = generateSimpleKey();
		return serverKey;
	}

	public void userRegistration(String userName, char[] password) {
		// register user --> set username in user data base
		user = userName;
		userPasswordKey = generateSimpleKeyForPassword(password);

		System.out.println("Principal: " + user);
		System.out.println("Password key: " + userPasswordKey);
	}

	/* *********** AS module: TGS - ticket request **************************** */

	public TicketResponse requestTGSTicket(String userName, String tgsServerName, long nonce) {
		// handle requirements of TGS ticket
		// returns TicketResponse for request

		TicketResponse tgsTicketResp = null;
		Ticket tgsTicket = null;
		long currentTime = 0;

		// assemble TGS response
		// search for username and password in data base
		if (userName.equals(user) && tgsServerName.equals(tgsName)) {
			// generate new session key for Client and TGS
			long tgsSessionKey = generateSimpleKey();
			// milliseconds since 1.1.1970
			currentTime = (new Date()).getTime();

			// build TGS ticket
			tgsTicket = new Ticket(user, tgsName, currentTime, currentTime + tenHoursInMillis, tgsSessionKey);

			// encrypt ticket
			tgsTicket.encrypt(tgsKey);

			// generate response
			tgsTicketResp = new TicketResponse(tgsSessionKey, nonce, tgsTicket);

			// encrypt response
			tgsTicketResp.encrypt(userPasswordKey);
		}
		return tgsTicketResp;
	}

	/* *********** TGS module: Server - ticket request **************************** */

	public TicketResponse requestServerTicket(Ticket tgsTicket, Auth tgsAuth, String serverName, long nonce) {
		// handle requirements of server ticket
		// returns TicketResponse for request

		// decrypt
		if (!tgsTicket.decrypt(tgsKey)) {
			tgsTicket.printError("TGS key invalid.");
			return null;
		}

		long tgsSessionKey = tgsTicket.getSessionKey();
		if (!tgsAuth.decrypt(tgsSessionKey)) {
			tgsAuth.printError("TGS session key invalid.");
			return null;
		}

		// authenticate
		if (!tgsAuth.getClientName().equals(tgsTicket.getClientName())) {
			tgsAuth.printError("Client invalid in authentication or TGS ticket.");
			return null;
		}

		if (!timeFresh(tgsAuth.getCurrentTime())) {
			tgsAuth.printError("Authentication time invalid.");
			return null;
		}

		if (!timeValid(tgsTicket.getStartTime(), tgsTicket.getEndTime())) {
			tgsTicket.printError("TGS ticket invalid.");
			return null;
		}

		// assemble server response
		long serverSessionKey = generateSimpleKey();
		long currentTime = System.currentTimeMillis();
		Ticket serverTicket = new Ticket(
				tgsAuth.getClientName(),
				serverName,
				currentTime,
				currentTime + fiveMinutesInMillis,
				serverSessionKey
		);
		serverTicket.encrypt(serverKey);

		// search for server in data base
		if (!serverName.equals(this.serverName)) {
			System.out.println("Server name not known.");
			return null;
		}

		// generate and encrypt serverTicketResponse
		TicketResponse serverTicketResponse = new TicketResponse(serverSessionKey, nonce, serverTicket);
		serverTicketResponse.encrypt(tgsSessionKey);
		return serverTicketResponse;
	}

	/* *********** helping methods **************************** */

	private long getServerKey(String sName) {
		// returns server key associated with server name
		// returns -1 if server name is not known
		if (sName.equalsIgnoreCase(serverName)) {
			System.out.println("Server key ok");
			return serverKey;
		} else {
			System.out.println("Server key unknown!!!!");
			return -1;
		}
	}

	private long generateSimpleKeyForPassword(char[] pw) {
		// returns key for password (simulated as long value)
		long pwKey = 0;
		for (int i = 0; i < pw.length; i++) {
			pwKey = pwKey + pw[i];
		}
		return pwKey;
	}

	private long generateSimpleKey() {
		// returns new secret key (simulated as long value)
		long sKey = (long) (100000000 * Math.random());
		return sKey;
	}

	boolean timeValid(long lowerBound, long upperBound) {
		// milliseconds since 1.1.1970
		long currentTime = (new Date()).getTime();
		if (currentTime >= lowerBound && currentTime <= upperBound) {
			return true;
		} else {
			System.out.println(
					"-------- Time not valid: " + currentTime + " not in (" + lowerBound + "," + upperBound + ")!");
			return false;
		}
	}

	boolean timeFresh(long testTime) {
		// returns true if given time doesn't vary more than 5 minutes from current time
		// milliseconds since 1.1.1970
		long currentTime = (new Date()).getTime();
		if (Math.abs(currentTime - testTime) < fiveMinutesInMillis) {
			return true;
		} else {
			System.out.println("-------- Time not fresh: " + currentTime + " is current, " + testTime + " is old!");
			return false;
		}
	}
}
