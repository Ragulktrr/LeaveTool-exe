package Design;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings("rawtypes")
public class PmoApprovalDesign {
	public static JFrame pmoFrame;
	public static JPanel pnlMain;
	
	public static JLabel lblPmoName;
	
	public static JLabel lblCount;
	public static JLabel lblTlName;
	public static JComboBox cmbTlList;
	
	public static JLabel lblLeaveType;
	public static JComboBox cmbLeaveType;
	
	public static JButton btnTlForm;
	public static JButton btnStatus;
	public static JButton btnleaveSummary;
	public static JButton btnTeamRequest;
	public static JButton btnSearch;
	public static JButton btnSignout;
	
	Map<String, Object> valueMap;
	StringBuilder appendSb;
	
	@SuppressWarnings("unchecked")
	public PmoApprovalDesign(Map<String, Object> valueMap){
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
		pmoFrame = new JFrame("PMO");
		this.valueMap = valueMap;
		
		pmoFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int confirmed = JOptionPane.showConfirmDialog(null, 
						"Are you sure you want to exit this program?", "Exit",
						JOptionPane.YES_NO_OPTION);

				if (confirmed == JOptionPane.YES_OPTION) {
					pmoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}else{
					pmoFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				}
			}
		});
		
		pnlMain = new JPanel();
		
		appendSb = new StringBuilder();
		lblPmoName = new JLabel("", SwingConstants.CENTER);
		appendSb.append("<html><u>"+valueMap.get("empName")+"</u></html>");
		lblPmoName.setText(appendSb.toString());
		
		lblCount = new JLabel("", SwingConstants.CENTER);
		
		lblTlName = new JLabel("Incoming Request for TL : ", SwingConstants.CENTER);
		cmbTlList = new JComboBox();
		
		lblLeaveType = new JLabel("Leave Type : ", SwingConstants.CENTER);
		cmbLeaveType = new JComboBox();
		
		cmbLeaveType.addItem("Casual Leave");
		cmbLeaveType.addItem("Sick Leave");
		cmbLeaveType.addItem("Permission");
		
		btnTlForm = new JButton("TL Form");
		btnStatus = new JButton("Approve Status");
		btnleaveSummary = new JButton("Leave Summary");
		btnTeamRequest = new JButton("Team Request");
		btnSearch = new JButton("Search");
		btnSignout = new JButton("Sign-Out");
		
		heightWidth();
	}
	
	public void heightWidth(){
		pmoFrame.setBounds(0, 0, 900, 700);

		pnlMain.setLayout(null);
		
		lblPmoName.setBounds(0, 30, 900, 75);
		lblPmoName.setFont(new Font("calibri light", Font.BOLD, 20));
		
		lblCount.setBounds(335, 100, 290, 75);
		lblCount.setFont(new Font("calibri light", Font.BOLD, 16));
		
		lblTlName.setBounds(277, 197, 190, 30);
		lblTlName.setFont(new Font("calibri light", Font.PLAIN, 16));
		
		cmbTlList.setBounds(480, 195, 130, 30);
		btnTlForm.setBounds(630, 195, 100, 30);
		
		lblLeaveType.setBounds(369, 272, 90, 30);
		lblLeaveType.setFont(new Font("calibri light", Font.PLAIN, 16));
		
		cmbLeaveType.setBounds(480, 270, 130, 30);
		
		btnStatus.setBounds(495, 345, 125, 40);
		btnleaveSummary.setBounds(325, 345, 125, 40);
		btnTeamRequest.setBounds(325, 445, 125, 40);
		btnSearch.setBounds(495, 445, 125, 40);
		btnSignout.setBounds(415, 545, 125, 40);
		
		pnlMain.add(lblPmoName);
		pnlMain.add(lblCount);
		
		pnlMain.add(lblTlName);
		pnlMain.add(cmbTlList);
		pnlMain.add(btnTlForm);
		
		pnlMain.add(lblLeaveType);
		pnlMain.add(cmbLeaveType);
		pnlMain.add(btnStatus);
		pnlMain.add(btnleaveSummary);
		pnlMain.add(btnTeamRequest);
		pnlMain.add(btnSearch);
		pnlMain.add(btnSignout);
		
		pmoFrame.add(pnlMain);

		pmoFrame.setVisible(true);
		pmoFrame.setResizable(false);
		pmoFrame.setLocationRelativeTo(null);
	}
}
