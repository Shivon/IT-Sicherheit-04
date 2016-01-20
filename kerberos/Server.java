package kerberos;

// Simulation of Kerberos session with access on file server

import java.util.*;
import java.io.*;

public class Server extends Object {
	// 5 minutes in milliseconds
	private final long fiveMinutesInMillis = 300000;
	// constructor params
	private String myName;
	// saved at KDC registration
	private KDC myKDC;
	private long myKey;

	// constructor
	public Server(String name) {
		myName = name;
	}

	public String getName() {
		return myName;
	}

	public void setupService(KDC kdc) {
		// register server at KDC
		myKDC = kdc;
		myKey = myKDC.serverRegistration(myName);
		System.out.println("Server " + myName
				+ " successfully registered at KDC " + myKDC.getName()
				+ " with serverKey " + myKey);
	}

	public boolean requestService(Ticket serverTicket, Auth serverAuth, String command, String parameter) {
		serverTicket.print();
		serverAuth.print();

		// catch if another service than "showFile" is called
		if (!command.equals("showFile")) {
			System.out.println("Command not known - available: showFile");
			return false;
		}

		// decrypt
		if (!serverTicket.decrypt(myKey)) {
			serverTicket.printError("Server key invalid.");
			return false;
		}

		if (!serverAuth.decrypt(serverTicket.getSessionKey())) {
			serverAuth.printError("Session key invalid.");
			return false;
		}

		// authenticate
		if (!serverAuth.getClientName().equals(serverTicket.getClientName())) {
			serverAuth.printError("Client name invalid in authentication or ticket.");
			return false;
		}

		if (!timeFresh(serverAuth.getCurrentTime())) {
			serverAuth.printError("Time invalid.");
			return false;
		}

		if (!timeValid(serverTicket.getStartTime(), serverTicket.getEndTime())) {
			serverTicket.printError("Time invalid.");
			return false;
		}

		serverTicket.print();
		serverAuth.print();

		// write file to terminal
		return showFile(parameter);
	}

	/* *********** services **************************** */

	private boolean showFile(String filePath) {
		// show specified file on terminal
		// returns status of operation
		String lineBuf = null;
		File myFile = new File(filePath);
		boolean status = false;

		if (!myFile.exists()) {
			System.out.println("File " + filePath + " doesn't exist!");
		} else {
			try {
				// open file and read line by line
				BufferedReader inFile = new BufferedReader(
						new InputStreamReader(new FileInputStream(myFile)));
				lineBuf = inFile.readLine();
				while (lineBuf != null) {
					System.out.println(lineBuf);
					lineBuf = inFile.readLine();
				}
				inFile.close();
				status = true;
			} catch (IOException ex) {
				System.out.println("Error while reading file " + filePath + ex);
			}
		}
		return status;
	}

	/* *********** helping methods **************************** */

	private boolean timeValid(long lowerBound, long upperBound) {
		// returns true if current time is within given time boundaries
		// milliseconds since 1.1.1970
		long currentTime = (new Date()).getTime();
		if (currentTime >= lowerBound && currentTime <= upperBound) {
			return true;
		} else {
			System.out.println("-------- Time not valid: " + currentTime
					+ " not in (" + lowerBound + "," + upperBound + ")!");
			return false;
		}
	}

	boolean timeFresh(long testTime) {
		// returns true if given time doesn't differ more than 5 minutes from current time
		// milliseconds since 1.1.1970
		long currentTime = (new Date()).getTime();
		if (Math.abs(currentTime - testTime) < fiveMinutesInMillis) {
			return true;
		} else {
			System.out.println("-------- Time not fresh: " + currentTime
					+ " is current, " + testTime + " is old!");
			return false;
		}
	}
}
