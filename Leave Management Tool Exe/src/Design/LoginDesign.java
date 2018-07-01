package Design;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class LoginDesign {
	
	public static JFrame loginFrame;
	public static JPanel pnlMain;
	public static JLabel lblHeader;
	public static JLabel lblLogin;
	public static JLabel lblEmpId;
	public static JLabel lblEmpName;
	public static JLabel lblPwd;
	public static JTextField txtEmpId;
	public static JPasswordField txtPwd;
	public static JButton btnLogin;
	public static JButton btnExit;
	public static JLabel lblLastLogin;
	public static StringBuilder lastLoginDate;

	public LoginDesign(){
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} 
		loginFrame = new JFrame("Log-In");

		lastLoginDate = new StringBuilder("Last Login : ");
		pnlMain = new JPanel();
		lblHeader = new JLabel("Leave Form ", SwingConstants.CENTER);
		lblLogin = new JLabel("Log_In ", SwingConstants.CENTER);
		lblEmpId = new JLabel("Employee_ID : ", SwingConstants.CENTER);
		lblPwd = new JLabel("Password      : ", SwingConstants.CENTER);
		lblEmpName = new JLabel("");
		txtEmpId = new JTextField("");
		txtPwd = new JPasswordField("");

		btnLogin = new JButton("Sign-In");
		btnExit = new JButton("Exit");
		lblLastLogin = new JLabel("", SwingConstants.CENTER);
		
		this.heightWidth();
	}
	
	private void heightWidth() {
		loginFrame.setResizable(false);
		loginFrame.setVisible(true);

		loginFrame.setBounds(0, 0, 900, 700);
		loginFrame.setLocationRelativeTo(null);

		pnlMain.setLayout(null);
		pnlMain.setBounds(0, 0, 900, 700);

		lblHeader.setBounds(0, 20, 900, 100);
		lblHeader.setFont(new Font("calibri light", Font.BOLD, 36));

		lblLogin.setBounds(0, 120, 900, 50);
		lblLogin.setFont(new Font("calibri light", Font.BOLD, 18));

		lblEmpId.setBounds(250, 250, 150, 30);
		lblEmpId.setFont(new Font("calibri light", Font.BOLD, 18));

		lblPwd.setBounds(250, 350, 150, 30);
		lblPwd.setFont(new Font("calibri light", Font.BOLD, 18));

		txtEmpId.setBounds(410, 250, 180, 30);

		lblEmpName.setBounds(410, 300, 180, 30);
		lblEmpName.setFont(new Font("calibri light", Font.BOLD, 16));

		txtPwd.setBounds(410, 350, 180, 30);

		btnLogin.setBounds(270, 450, 125, 40);
		btnExit.setBounds(460, 450, 125, 40);
		
		lblLastLogin.setBounds(550, 575, 300, 30);
		lblLastLogin.setFont(new Font("calibri light", Font.BOLD, 14));
		lblLastLogin.setForeground(Color.BLUE);

		pnlMain.add(lblHeader);
		pnlMain.add(lblLogin);
		pnlMain.add(lblEmpId);
		pnlMain.add(lblEmpName);
		pnlMain.add(lblPwd);

		pnlMain.add(txtEmpId);
		pnlMain.add(txtPwd);

		pnlMain.add(btnLogin);
		pnlMain.add(btnExit);
		pnlMain.add(lblLastLogin);

		loginFrame.add(pnlMain);
	}
}
