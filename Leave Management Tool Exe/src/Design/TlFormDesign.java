package Design;

import java.awt.Font;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;

@SuppressWarnings("rawtypes")
public class TlFormDesign {
	
	public static JFrame tlFrame;
	public static JPanel pnlMain;
	public static JPanel pnlGeneral;
	public static JPanel pnlPersonal;
	public static JPanel pnlTeam;
	public static JComboBox cmbStatus;
	public static JButton btnStatus;
	public static JButton btnLveRequest;
	public static JButton btnLveSummary;
	public static JButton btnApplyRequest;
	public static JButton btnEdtProfile;
	public static JButton btnSignout;
	public static JLabel lblCount;
	public static JLabel lblID;
	public static JLabel lblPmo;
	public static JLabel lblTlName;
	public static JLabel lblTlID;
	public static JLabel lblTlPmo;
	Map<String, Object> valueMap;
	StringBuilder appendSb;
	
	public TlFormDesign(Map<String, Object> valueMap){
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
		this.valueMap = valueMap;
		appendSb = new StringBuilder();
		tlFrame = new JFrame("TL");

		lblCount = new JLabel("", SwingConstants.CENTER);

		lblID = new JLabel("ID  :", SwingConstants.CENTER);
		lblPmo = new JLabel("PMO  :", SwingConstants.CENTER);

		lblTlName = new JLabel("", SwingConstants.CENTER);
		appendSb.append("<html><u>"+valueMap.get("empName")+"</u></html>");
		lblTlName.setText(appendSb.toString());

		lblTlID = new JLabel("", SwingConstants.CENTER);
		lblTlID.setText(valueMap.get("emp_IdDB").toString());
		
		lblTlPmo = new JLabel("", SwingConstants.LEFT);
		lblTlPmo.setText(valueMap.get("PMO").toString());

		pnlMain = new JPanel();
		pnlGeneral = new JPanel();
		pnlPersonal = new JPanel();
		pnlTeam = new JPanel();

		cmbStatus = new JComboBox();
		btnStatus = new JButton("Approve Status");

		btnLveRequest = new JButton("Apply Leave");
		btnLveSummary = new JButton("Leave Summary");

		btnApplyRequest = new JButton("Team Request");
		btnEdtProfile = new JButton("Edit Profile");

		btnSignout = new JButton("Sign-Out");

		this.heightWidth();
	}
	public void heightWidth(){

		tlFrame.setBounds(0, 0, 900, 700);

		pnlMain.setLayout(null);

		lblTlName.setBounds(0, 0, 900, 75);
		lblTlName.setFont(new Font("calibri light", Font.BOLD, 20));

		lblID.setBounds(380, 60, 100, 50);
		lblID.setFont(new Font("calibri light", Font.BOLD, 16));

		lblTlID.setBounds(425, 60, 100, 50);
		lblTlID.setFont(new Font("calibri light", Font.PLAIN, 16));

		lblPmo.setBounds(371, 100, 100, 50);
		lblPmo.setFont(new Font("calibri light", Font.BOLD, 16));

		lblTlPmo.setBounds(462, 100, 100, 50);
		lblTlPmo.setFont(new Font("calibri light", Font.PLAIN, 16));

		lblCount.setBounds(345, 125, 250, 75);
		lblCount.setFont(new Font("calibri light", Font.BOLD, 16));

		pnlGeneral.setLayout(null);
		pnlGeneral.setBorder(new TitledBorder("General"));
		pnlGeneral.setBounds(300, 180, 320, 100);
		cmbStatus.setBounds(30, 37, 110, 30);
		btnStatus.setBounds(150, 30, 125, 40);

		pnlPersonal.setLayout(null);
		pnlPersonal.setBorder(new TitledBorder("Personal"));
		pnlPersonal.setBounds(300, 295, 320, 100);
		btnLveRequest.setBounds(20, 30, 125, 40);
		btnLveSummary.setBounds(170, 30, 130, 40);

		pnlTeam.setLayout(null);
		pnlTeam.setBorder(new TitledBorder("Team"));
		pnlTeam.setBounds(300, 405, 320, 100);
		btnApplyRequest.setBounds(20, 30, 125, 40);
		btnEdtProfile.setBounds(170, 30, 130, 40);

		btnSignout.setBounds(400, 530, 125, 40);

		pnlGeneral.add(cmbStatus);
		pnlGeneral.add(btnStatus);

		pnlPersonal.add(btnLveRequest);
		pnlPersonal.add(btnLveSummary);

		pnlTeam.add(btnApplyRequest);
		pnlTeam.add(btnEdtProfile);

		pnlMain.add(lblCount);

		pnlMain.add(lblID);
		pnlMain.add(lblPmo);

		pnlMain.add(lblTlName);
		pnlMain.add(lblTlID);
		pnlMain.add(lblTlPmo);

		pnlMain.add(btnSignout);
		pnlMain.add(pnlGeneral);
		pnlMain.add(pnlPersonal);
		pnlMain.add(pnlTeam);

		tlFrame.add(pnlMain);

		tlFrame.setVisible(true);
		tlFrame.setResizable(false);
		tlFrame.setLocationRelativeTo(null);

		pnlMain.updateUI();
	}
}
