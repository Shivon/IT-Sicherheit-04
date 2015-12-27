package kerberos;

// Simulation of Kerberos session with access on file server

import java.util.*;

public class Auth extends Object {
	// constructor params
	private String myClientName;
	private long myCurrentTime;
	// secret key with which the authentication is encrypted (simulated)
	private long myAuthKey;
	// current status of the object
	private boolean isEncryptedState;
	// calendar object for time conversion (testing purpose)
	private Calendar cal;

	// constructor
	public Auth(String clientName, long currentTime) {
		myClientName = clientName;
		myCurrentTime = currentTime;
		myAuthKey = -1;
		isEncryptedState = false;
		// for test purposes
		cal = new GregorianCalendar();
	}

	public String getClientName() {
		if (isEncryptedState) {
			printError("Access to encrypted authentication (getClientName)");
		}
		return myClientName;
	}

	public long getCurrentTime() {
		if (isEncryptedState) {
			printError("Access to encrypted authentication (getCurrentTime)");
		}
		return myCurrentTime;
	}

	public boolean encrypt(long key) {
		// encrypts authentication with key
		// returns false if authentication is already encrypted
		boolean encOK = false;
		if (isEncryptedState) {
			printError("Authentication already encrypted");
		} else {
			myAuthKey = key;
			isEncryptedState = true;
			encOK = true;
		}
		return encOK;
	}

	public boolean decrypt(long key) {
		// decrypts authentication with key
		// returns false if key is wrong or authentication already decrypted
		boolean decOK = false;
		if (!isEncryptedState) {
			printError("Authentication already decrypted");
		}
		if (myAuthKey != key) {
			printError("Decryption of authentication with key " + key
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
		System.out.println("+++++++++++++++++++ Error +++++++++++++++++++ "
				+ message + "! Authentication key: " + myAuthKey);
		System.out.println("+++++++++++++++++++");
	}

	public void print() {
		System.out.println("********* Authentication for " + myClientName
				+ " *******");
		System.out.println("CurrentTime: " + getDateString(myCurrentTime));
		System.out.println("Auth key: " + myAuthKey);
		if (isEncryptedState) {
			System.out.println("Auth state: encrypted!");
		} else {
			System.out.println("Auth state: decrypted!");
		}
		System.out.println();
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
