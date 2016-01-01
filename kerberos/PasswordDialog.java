package kerberos;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

// show window with password entry field as modal dialog
public class PasswordDialog extends JDialog {
	private static final long serialVersionUID = 873795915516658285L;

	private JPasswordField passwortField = null;
	private boolean status = false;

	// ----------------- access to results -----------------
	public boolean statusOK() {
		// returns status: OK = true, cancel = false
		return status;
	}

	public char[] getPassword() {
		// returns password (array reference)
		return passwortField.getPassword();
	}

	// ----------------- constructors -----------------
	public PasswordDialog(String userName) {
		// determine modal dialog via params
		super((Frame) null, "HAW Department Informatik WP IT-Sicherheit", true);

		String okLabel = "  OK  ";
		String cancelLabel = "Cancel";

		// set window params
		setLocation(200, 100);
		Container contentPanel = getContentPane();
		contentPanel.setLayout(new BorderLayout(100, 20));
		// display text
		JLabel labelMessage1 = new JLabel("<html><body><font size=\"+1\">"
				+ "<em>Please enter password for " + userName
				+ " : </em></font><br>" + "</body></html>",
				SwingConstants.CENTER);
		contentPanel.add(labelMessage1, BorderLayout.NORTH);

		// fill areas left and right
		JLabel leftDist = new JLabel(" ");
		contentPanel.add(leftDist, BorderLayout.WEST);
		JLabel rightDist = new JLabel(" ");
		contentPanel.add(rightDist, BorderLayout.EAST);

		passwortField = new JPasswordField(15);

		// add passwortField to window
		contentPanel.add(passwortField, BorderLayout.CENTER);

		// generate panel for OK/Cancel-buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		// OK-button
		JButton okButton = new JButton(okLabel);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				status = true;
				closeDialog();
			}
		});
		buttonPanel.add(okButton);

		// cancel-button
		JButton cancelButton = new JButton(cancelLabel);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				status = false;
				closeDialog();
			}
		});
		buttonPanel.add(cancelButton);

		// add buttons to window
		contentPanel.add(buttonPanel, BorderLayout.SOUTH);

		// display
		pack();
		setVisible(true);
	}

	private void closeDialog() {
		// close window
		setVisible(false);
		dispose();
	}

	public static void main(String argv[]) throws Exception {
		// test method!
		PasswordDialog myClient = new PasswordDialog("Testuser");
		System.out.println("Status: " + myClient.statusOK());
		System.out.println("Password: " + new String(myClient.getPassword()));
	}
}
