package kerberos;

// Simulation of Kerberos session with access on file server

import java.util.*;

public class KerberosSim {

	private KDC myKDC;
	private Client myClient;
	private Server myFileserver;

	public void initKerberos(String userName, char[] password, String serverName, String tgsName) {
		// init KDC
		myKDC = new KDC(tgsName);

		// init server
		myFileserver = new Server(serverName);
		// generate and exchange key
		myFileserver.setupService(myKDC);

		// create user account and client
		myKDC.userRegistration(userName, password);
		myClient = new Client(myKDC);
	}

	public char[] readPasswd(String userName) {
		// enter password via modal dialog
		// returns null when user cancels interaction
		char[] password = null;
		PasswordDialog pwDialog = new PasswordDialog(userName);
		if (pwDialog.statusOK()) {
			password = pwDialog.getPassword();
		}
		return password;
	}

	public static void main(String args[]) {
		// simulation of user session: login and access to file server

		// -------- start init system ------------------
		String userName = "axz467";
		char[] password = { 'S', 'e', 'c', 'r', 'e', 't', '!' };
		String serverName = "myFileserver";
		String tgsName = "myTGS";
		// TODO: this path is for Windows...
		String filePath = "C:/Temp/ITS.txt";

		KerberosSim thisSession = new KerberosSim();

		// init KDC + all server + client
		thisSession.initKerberos(userName, password, serverName, tgsName);
		// -------- end init system ------------------

		// -------- simulate user session --------
		// get password of user
		System.out.println("Start login session for user: " + userName);
		password = thisSession.readPasswd(userName);
		if (password != null) {

			// user login at KDC
			boolean loginOK = thisSession.myClient.login(userName, password);

			// delete password from main memory (overwrite)!!
			Arrays.fill(password, ' ');

			if (!loginOK) {
				System.out.println("Login failed!");
			} else {
				System.out.println("Login successful!\n");

				// access file server
				boolean serviceOK = thisSession.myClient.showFile(thisSession.myFileserver, filePath);
				if (!serviceOK) {
					System.out.println("Access on server " + serverName + " failed!");
				}
			}
		}
	}

}
