package Design;

import java.awt.Color;
import java.awt.Font;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

@SuppressWarnings("rawtypes")
public class EmpDesign {

	public static JFrame empleave;
	public static JPanel pnlMain;
	public static JPanel pnlHeader;
	public static JPanel pnlHolder;
	public static JPanel pnlClSl;
	public static JPanel pnlHd;
	public static JPanel pnlPermission;
	public static JLabel lblName;
	public static JLabel lblID;
	public static JLabel lblTL;
	public static JLabel lblPMO;
	public static JLabel lblLeave;
	public static JLabel lblEmpId;
	public static JLabel lblEmpTL;
	public static JLabel lblEmpPMO;
	public static JComboBox cmbLeave;
	public static JButton btnSignout;
	public static JButton btnBack;
	public static JButton btnLeaveSummary;
	public static JButton btnApply;
	public static JLabel lblFrmDte;
	public static JLabel lblToDte;
	public static JLabel lblReason;
	public static JDateChooser clFromdte;
	public static JDateChooser clTodte;
	public static JDateChooser slFromdte;
	public static JDateChooser slTodte;
	public static JTextArea leaveReason;
	public static JScrollPane txtAreascroll;
	public static JTextArea leaveHDReason;
	public static JScrollPane txtAreaHDscroll;
	public static JLabel lblCountLeave;
	public static JLabel lblCountHDLeave;
	public static JComboBox cmbSession;
	public static JLabel lblHdDate;
	public static JLabel lblHdSession;
	public static JDateChooser HdDate;
	public static JLabel lblFromTime;
	public static JLabel lblPerLeave;
	public static JDateChooser PerDate;
	public static SpinnerModel spinnerModel;
	public static JSpinner spinner;
	public static JLabel lblPermissionCount;
	public static Date clDate;
	public static Date slDate;
	public static Date hdDate;
	public static Date perDate;
	public static Calendar clConstantCalendar;
	public static JTextFieldDateEditor editor;
	public static JRadioButton casualrbtn;
	public static JRadioButton sickrbtn;
	public static ButtonGroup bindclsl;
	public static String leaveStatus[];
	public static String session[];
	public static JLabel lblBalanceLeave;
	public static JLabel lblLeaveStatus;
	Map<String, Object> valueMap;
	StringBuilder appendSb;
	public static int loginAs;
	public static int unplannedLeave;

	@SuppressWarnings("unchecked")
	public EmpDesign(Map<String, Object> valueMap){
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
		if(valueMap.get("levelCheck").equals(0) && valueMap.get("loginAs").equals(0)){
			loginAs = 0;
			unplannedLeave = 0;
		}else if(valueMap.get("levelCheck").equals(1) && valueMap.get("loginAs").equals(0)){
			loginAs = 1;
			unplannedLeave = 0;
		}else if(((Integer)valueMap.get("levelCheck")) >= 1 && ((Integer)valueMap.get("loginAs") >= 1)){
			loginAs = (Integer)valueMap.get("loginAs");
			unplannedLeave = 1;
		}
		appendSb = new StringBuilder();
		empleave = new JFrame("Employee Leave apply");
		pnlMain = new JPanel();
		pnlHeader = new JPanel();
		pnlHolder = new JPanel();
		pnlClSl = new JPanel();
		pnlHd = new JPanel();
		pnlPermission = new JPanel();
		if(((Integer) valueMap.get("loginAs")) > 0){
			leaveStatus = new String[]{"--Choose--", "Casual Leave", "Sick Leave", "Half-Day Leave", "Permission"};
		}else{
			leaveStatus = new String[]{"--Choose--", "Casual Leave", "Half-Day Leave", "Permission"};
		}
		session = new String[]{"--Choose--", "AM", "PM"};
		lblName = new JLabel("", SwingConstants.CENTER);
		lblID = new JLabel("ID  :", SwingConstants.CENTER);
		lblBalanceLeave = new JLabel("", SwingConstants.CENTER);
		lblLeaveStatus = new JLabel("", SwingConstants.CENTER);
		lblEmpId = new JLabel("");
		lblTL = new JLabel("TL  :", SwingConstants.CENTER);
		lblPMO = new JLabel("PMO  :", SwingConstants.CENTER);
		lblEmpTL = new JLabel("");
		lblEmpPMO = new JLabel("");
		lblLeave = new JLabel("Leave  :", SwingConstants.CENTER);
		lblFrmDte = new JLabel("From Date  :", SwingConstants.CENTER);
		lblToDte = new JLabel("To Date  :", SwingConstants.CENTER);
		lblReason = new JLabel("Reason  :", SwingConstants.CENTER);
		btnSignout = new JButton("Sign-Out");
		btnLeaveSummary = new JButton("Leave Summary");
		btnBack = new JButton("Back");

		lblCountLeave = new JLabel("", SwingConstants.CENTER);
		lblCountHDLeave = new JLabel("", SwingConstants.CENTER);
		leaveReason = new JTextArea();
		txtAreascroll = new JScrollPane(leaveReason);
		txtAreascroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		leaveHDReason = new JTextArea();
		txtAreaHDscroll = new JScrollPane(leaveHDReason);
		txtAreaHDscroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		btnApply = new JButton("Apply");
		clFromdte = new JDateChooser();
		clFromdte.setDateFormatString("yyyy-MM-dd");
		clTodte = new JDateChooser();
		clTodte.setDateFormatString("yyyy-MM-dd");
		clDate = new Date();
		slDate = new Date();
		hdDate = new Date();
		perDate = new Date();

		slFromdte = new JDateChooser();
		slFromdte.setDateFormatString("yyyy-MM-dd");
		slTodte = new JDateChooser();
		slTodte.setDateFormatString("yyyy-MM-dd");

		cmbLeave = new JComboBox(leaveStatus);
		cmbSession = new JComboBox(session);
		lblHdDate = new JLabel("Date  :", SwingConstants.CENTER);
		lblHdSession = new JLabel("Session  :", SwingConstants.CENTER);
		HdDate = new JDateChooser();
		HdDate.setDateFormatString("yyyy-MM-dd");

		casualrbtn = new JRadioButton("Casual Leave");
		sickrbtn = new JRadioButton("Sick Leave");
		bindclsl = new ButtonGroup();
		casualrbtn.setSelected(true);

		lblPerLeave = new JLabel("Date  :", SwingConstants.CENTER);
		PerDate = new JDateChooser();
		PerDate.setDateFormatString("yyyy-MM-dd");

		lblFromTime = new JLabel("Permission Hours  :", SwingConstants.CENTER);
		lblPermissionCount = new JLabel("", SwingConstants.CENTER);
		spinnerModel = new SpinnerNumberModel(0, 0, 2, 0.50);
		spinner = new JSpinner(spinnerModel);

		appendSb.append("<html><u>"+valueMap.get("empName")+"</u></html>");
		lblName.setText(appendSb.toString());
		lblEmpId.setText(valueMap.get("emp_IdDB").toString());
		lblEmpTL.setText(valueMap.get("TL").toString());
		lblEmpPMO.setText(valueMap.get("PMO").toString());

		this.heightWidth();
	}

	private void heightWidth(){

		empleave.setResizable(false);
		empleave.setVisible(true);

		empleave.setBounds(0, 0, 900, 700);
		empleave.setLocationRelativeTo(null);
		empleave.setVisible(true);

		pnlMain.setLayout(null);
		pnlMain.setBounds(0, 0, 900, 700);

		lblName.setBounds(0, 30, 850, 50);
		lblName.setFont(new Font("calibri light", Font.BOLD, 20));

		lblID.setBounds(350, 85, 100, 50);
		lblID.setFont(new Font("calibri light", Font.BOLD, 16));

		lblBalanceLeave.setBounds(40, 105, 300, 150);
		lblBalanceLeave.setFont(new Font("calibri light", Font.BOLD, 16));
		lblBalanceLeave.setForeground(new Color(0, 0, 204));

		lblLeaveStatus.setBounds(570, 85, 300, 150);
		lblLeaveStatus.setFont(new Font("calibri light", Font.BOLD, 16));
		lblLeaveStatus.setForeground(new Color(0, 0, 204));

		lblEmpId.setBounds(440, 98, 50, 25);
		lblEmpId.setFont(new Font("calibri light", Font.PLAIN, 16));

		lblTL.setBounds(350, 140, 100, 50);
		lblTL.setFont(new Font("calibri light", Font.BOLD, 16));

		lblEmpTL.setBounds(440, 140, 175, 50);
		lblEmpTL.setFont(new Font("calibri light", Font.PLAIN, 16));

		lblPMO.setBounds(341, 195, 100, 50);
		lblPMO.setFont(new Font("calibri light", Font.BOLD, 16));

		lblEmpPMO.setBounds(440, 195, 100, 50);
		lblEmpPMO.setFont(new Font("calibri light", Font.PLAIN, 16));

		lblLeave.setBounds(336, 250, 100, 50);
		lblLeave.setFont(new Font("calibri light", Font.BOLD, 16));

		cmbLeave.setBounds(440, 258, 110, 30);  

		lblFrmDte.setBounds(306, 10, 125, 40);
		lblFrmDte.setFont(new Font("calibri light", Font.BOLD, 16));

		lblCountLeave.setBounds(570, 10, 200, 40);
		lblCountLeave.setFont(new Font("calibri light", Font.BOLD, 16));

		lblCountHDLeave.setBounds(570, 10, 200, 40);
		lblCountHDLeave.setFont(new Font("calibri light", Font.BOLD, 16));

		clFromdte.setBounds(440, 17, 110, 30);
		clTodte.setBounds(440, 70, 110, 30);

		slFromdte.setBounds(440, 17, 110, 30);
		slTodte.setBounds(440, 70, 110, 30);

		leaveReason.setLineWrap(true);
		leaveReason.setWrapStyleWord(true);

		leaveHDReason.setLineWrap(true);
		leaveHDReason.setWrapStyleWord(true);
		txtAreaHDscroll.setBounds(440, 125, 200, 100);

		lblToDte.setBounds(316, 65, 125, 40);
		lblToDte.setFont(new Font("calibri light", Font.BOLD, 16));

		lblReason.setBounds(320, 120, 125, 40);
		lblReason.setFont(new Font("calibri light", Font.BOLD, 16));

		pnlHeader.setLayout(null);
		pnlHeader.setBounds(0, 0, 900, 305);

		pnlHolder.setLayout(null);
		pnlHolder.setBounds(0, 305, 900, 435);

		lblHdDate.setBounds(329, 10, 125, 40);
		lblHdDate.setFont(new Font("calibri light", Font.BOLD, 16));
		HdDate.setBounds(439, 17, 110, 30);
		lblHdSession.setBounds(319, 65, 125, 40);
		lblHdSession.setFont(new Font("calibri light", Font.BOLD, 16));
		cmbSession.setBounds(440, 70, 110, 30);
		lblReason.setBounds(320, 120, 125, 40);
		lblReason.setFont(new Font("calibri light", Font.BOLD, 16));
		txtAreascroll.setBounds(440, 125, 200, 100);

		casualrbtn.setFont(new Font("calibri light", Font.BOLD, 16));
		sickrbtn.setFont(new Font("calibri light", Font.BOLD, 16));
		casualrbtn.setBounds(575, 45, 150, 25);
		sickrbtn.setBounds(575, 70, 150, 25);

		btnLeaveSummary.setBounds(315, 80, 135, 40);
		btnSignout.setBounds(475, 80, 125, 40);

		lblPerLeave.setBounds(329, 10, 125, 40);
		lblPerLeave.setFont(new Font("calibri light", Font.BOLD, 16));
		PerDate.setBounds(439, 17, 110, 30);
		lblFromTime.setBounds(244, 65, 200, 40);
		lblFromTime.setFont(new Font("calibri light", Font.BOLD, 16));
		spinner.setBounds(440, 70, 50, 30);
		lblPermissionCount.setBounds(374, 122, 200, 40);
		lblPermissionCount.setFont(new Font("calibri light", Font.BOLD, 16));

		pnlHeader.add(lblName);
		pnlHeader.add(lblID);
		pnlHeader.add(lblBalanceLeave);
		pnlHeader.add(lblLeaveStatus);
		if(valueMap.get("back") != null){
			btnLeaveSummary.setBounds(255, 80, 135, 40);
			btnSignout.setBounds(415, 80, 125, 40);
			btnBack.setBounds(565, 80, 125, 40);
			pnlHolder.add(btnBack);
		}
		if(!valueMap.get("TL").equals("-")){
			pnlHeader.add(lblTL);
			pnlHeader.add(lblEmpTL);
		}else{
			lblPMO.setBounds(341, 168, 100, 50);
			lblPMO.setFont(new Font("calibri light", Font.BOLD, 16));

			lblEmpPMO.setBounds(440, 168, 100, 50);
			lblEmpPMO.setFont(new Font("calibri light", Font.PLAIN, 16));
		}
		if(!valueMap.get("levelCheck").equals(2)){
			pnlHeader.add(lblPMO);
			pnlHeader.add(lblEmpPMO);
		}else{
			lblTL.setBounds(341, 168, 100, 50);
			lblTL.setFont(new Font("calibri light", Font.BOLD, 16));

			lblEmpTL.setBounds(440, 168, 175, 50);
			lblEmpTL.setFont(new Font("calibri light", Font.PLAIN, 16));
		}
		pnlHeader.add(lblLeave);
		pnlHeader.add(cmbLeave);
		pnlHeader.add(lblEmpId);

		pnlHolder.add(btnLeaveSummary);
		pnlHolder.add(btnSignout);

		pnlMain.add(pnlHeader);
		pnlMain.add(pnlHolder);

		empleave.add(pnlMain);
	}

}
