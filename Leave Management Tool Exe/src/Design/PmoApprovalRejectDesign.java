package Design;


import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import Functionality.PmoApproveRejectFunctionality;

@SuppressWarnings("rawtypes")
public class PmoApprovalRejectDesign {
	public static JFrame pmoApprovalFrame;
	public static JPanel pnlMain;
	public static JLabel lblTLApprovalForm;
	public static JLabel lblStatusDisplay;
	public static JLabel lblFilter;
	public static JLabel lblCmbName;
	public static JLabel lblCmbLveType;

	public static JTable table;
	public static JScrollPane scrollpane;

	public static JButton btnApprove;
	public static JButton btnSelectAll;
	public static JButton btnReject;
	public static JButton btnRejectApproved;
	public static JButton btnTlWaiting;
	public static JButton btnBack;
	public static JButton btnHome;
	
	public static JComboBox cmbName;
	public static JComboBox cmbLveType;
	public static JComboBox cmbFilter;
	public static JCheckBox selectAll;
	public static Map<String, Object> valueMap;
	
	@SuppressWarnings({ "static-access" })
	public PmoApprovalRejectDesign(Map<String, Object> valueMap){
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
		pmoApprovalFrame = new JFrame("PMO Apporval");
		pnlMain = new JPanel();
		lblTLApprovalForm = new JLabel("<html><u>Approval Form</u></html>", SwingConstants.CENTER);
		
		lblStatusDisplay = new JLabel("Waiting For Approval :", SwingConstants.CENTER);
		lblFilter = new JLabel("Filter :", SwingConstants.CENTER);		
		
		btnSelectAll = new JButton("Select All");
		btnApprove = new JButton("Approve");
		btnReject = new JButton("Reject");
		btnRejectApproved = new JButton("Revert Approved/Rejected");
		btnTlWaiting = new JButton("Waiting Leave");
		btnBack = new JButton("Back");
		btnHome = new JButton("Home");

		cmbName = new JComboBox();
		cmbLveType = new JComboBox();
		cmbFilter = new JComboBox();
		
		lblCmbName = new JLabel("Applied Names : ", SwingConstants.CENTER);
		lblCmbLveType = new JLabel("Leave Type : ", SwingConstants.CENTER);
		
		selectAll = new JCheckBox("Select");
		
		PmoApproveRejectFunctionality.loadTable((String) valueMap.get("tlFilterComboValue"));

		this.heightWidth();
	}

	public void heightWidth(){

		pmoApprovalFrame.setBounds(0, 0, 900, 700);
		pnlMain.setLayout(null);
		
		lblTLApprovalForm.setBounds(1, 50, 899, 50);
		lblTLApprovalForm.setFont(new Font("calibri light", Font.BOLD, 20));
		
		lblStatusDisplay.setBounds(10, 207, 170, 40);
		lblStatusDisplay.setFont(new Font("calibri light", Font.PLAIN, 16));
		
		lblFilter.setBounds(633, 205, 50, 40);
		lblFilter.setFont(new Font("calibri light", Font.BOLD, 16));
		cmbFilter.setBounds(700, 207, 135, 30);
		
		lblCmbName.setBounds(270, 139, 150, 40);
		lblCmbName.setFont(new Font("calibri light", Font.BOLD, 16));
		cmbName.setBounds(440, 139, 170, 30);
		
		lblCmbLveType.setBounds(297, 205, 125, 40);
		lblCmbLveType.setFont(new Font("calibri light", Font.BOLD, 16));
		cmbLveType.setBounds(440, 207, 110, 30);
		
		btnSelectAll.setBounds(150, 480, 125, 40);
		btnApprove.setBounds(300, 480, 125, 40);
		btnReject.setBounds(450, 480, 125, 40);
		btnRejectApproved.setBounds(600, 480, 185, 40);
		btnTlWaiting.setBounds(240, 550, 135, 40);
		btnHome.setBounds(400, 550, 125, 40);
		btnBack.setBounds(550, 550, 125, 40);

		pnlMain.add(lblTLApprovalForm);
		pnlMain.add(lblStatusDisplay);
		
		pnlMain.add(lblFilter);
		pnlMain.add(cmbFilter);
		
		pnlMain.add(lblCmbName);
		pnlMain.add(cmbName);
		pnlMain.add(lblCmbLveType);
		pnlMain.add(cmbLveType);
		
		pnlMain.add(btnHome);
		pnlMain.add(btnSelectAll);
		pnlMain.add(btnApprove);
		pnlMain.add(btnReject);
		pnlMain.add(btnRejectApproved);
		pnlMain.add(btnTlWaiting);
		pnlMain.add(btnBack);

		pmoApprovalFrame.add(pnlMain);

		pmoApprovalFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int confirmed = JOptionPane.showConfirmDialog(null, 
						"Are you sure you want to exit this program?", "Exit",
						JOptionPane.YES_NO_OPTION);

				if (confirmed == JOptionPane.YES_OPTION) {
					pmoApprovalFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}else{
					pmoApprovalFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				}
			}
		});
		
		pmoApprovalFrame.setVisible(true);
		pmoApprovalFrame.setResizable(false);
		pmoApprovalFrame.setLocationRelativeTo(null);

		pnlMain.updateUI();
	}
}
