package kerberos;

// Simulation of Kerberos session with access on file server

import java.util.*;

public class Ticket extends Object {
	// constructor params
	private String myClientName;
	private String myServerName;
	private long myStartTime;
	private long myEndTime;
	private long mySessionKey;
	// secret key for encrypting ticket (simulated)
	private long myTicketKey;
	// current  status of object
	private boolean isEncryptedState;
	// calendar object for time conversion (testing purpose)
	private Calendar cal;

	// constructor
	public Ticket(String clientName, String serverName, long startTime, long endTime, long sessionKey) {
		myClientName = clientName;
		myServerName = serverName;
		myStartTime = startTime;
		myEndTime = endTime;
		mySessionKey = sessionKey;

		myTicketKey = -1;
		isEncryptedState = false;
		// for testing purpose
		cal = new GregorianCalendar();
	}

	public String getClientName() {
		if (isEncryptedState) {
			printError("Access to encrypted ticket (getClientName)");
		}
		return myClientName;
	}

	public String getServerName() {
		if (isEncryptedState) {
			printError("Access to encrypted ticket (getServerName)");
		}
		return myServerName;
	}

	public long getStartTime() {
		if (isEncryptedState) {
			printError("Access to encrypted ticket (getStartTime)");
		}
		return myStartTime;
	}

	public long getEndTime() {
		if (isEncryptedState) {
			printError("Access to encrypted ticket (getEndTime)");
		}
		return myEndTime;
	}

	public long getSessionKey() {
		if (isEncryptedState) {
			printError("Access to encrypted ticket (getSessionKey)");
		}
		return mySessionKey;
	}

	public boolean encrypt(long key) {
		// encrypt ticket with key
		// returns false if ticket is already encrypted
		boolean encOK = false;
		if (isEncryptedState) {
			printError("Ticket already encrypted");
		} else {
			myTicketKey = key;
			isEncryptedState = true;
			encOK = true;
		}
		return encOK;
	}

	public boolean decrypt(long key) {
		// decrypt ticket with key
		// returns false if key is wrong or ticket already decrypted
		boolean decOK = false;
		if (!isEncryptedState) {
			printError("Ticket already decrypted");
		}
		if (myTicketKey != key) {
			printError("Decrypting ticket with key " + key
					+ " failed");
		} else {
			isEncryptedState = false;
			decOK = true;
		}
		return decOK;
	}

	public boolean isEncrypted() {
		// encrypted = true, decrypted = false
		return isEncryptedState;
	}

	public void print() {
		System.out.println("********* Ticket for " + myClientName + " / "
				+ myServerName + " *******");
		System.out.println("StartTime: " + getDateString(myStartTime)
				+ " - EndTime: " + getDateString(myEndTime));
		System.out.println("Session key: " + mySessionKey);
		System.out.println("Ticket key: " + myTicketKey);
		if (isEncryptedState) {
			System.out.println("Ticket status: encrypted!");
		} else {
			System.out.println("Ticket status: decrypted!");
		}
	}

	public void printError(String message) {
		System.out.println("+++++++++++++++++++");
		System.out.println("+++++++++++++++++++ error +++++++++++++++++++ " + message + "! Ticket key: " + myTicketKey);
		System.out.println("+++++++++++++++++++");
	}

	private String getDateString(long time) {
		// converting time in milliseconds (since 1.1.1970) into date string
		String dateString;

		cal.setTimeInMillis(time);
		dateString = cal.get(Calendar.DAY_OF_MONTH) + "."
				+ (cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.YEAR)
				+ " " + cal.get(Calendar.HOUR_OF_DAY) + ":"
				+ cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND)
				+ ":" + cal.get(Calendar.MILLISECOND);
		return dateString;
	}
}
