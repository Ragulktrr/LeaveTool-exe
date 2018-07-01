package Functionality;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import Design.TlFormDesign;

@SuppressWarnings({ "static-access", "unchecked" })
public class TlFormFunctionality extends TlFormDesign implements ActionListener {

	public static Map<String, Object> valueMap;
	public static List<String> leaveTypeList;
	
	public TlFormFunctionality(Map<String, Object> valueMap){
		super(valueMap);
		this.valueMap = valueMap;
		leaveTypeList = new ArrayList<String>();
		cmbStatus.addItem("--Choose--");

		tlFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int confirmed = JOptionPane.showConfirmDialog(null, 
						"Are you sure you want to exit this program?", "Exit",
						JOptionPane.YES_NO_OPTION);

				if (confirmed == JOptionPane.YES_OPTION) {
					tlFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}else{
					tlFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				}
			}
		});

		btnStatus.setActionCommand("btnStatus");
		btnStatus.addActionListener(this);
		btnLveRequest.setActionCommand("btnLveRequest");
		btnLveRequest.addActionListener(this);
		btnLveSummary.setActionCommand("btnLveSummary");
		btnLveSummary.addActionListener(this);
		btnApplyRequest.setActionCommand("btnApplyRequest");
		btnApplyRequest.addActionListener(this);
		btnEdtProfile.setActionCommand("btnEdtProfile");
		btnEdtProfile.addActionListener(this);
		btnSignout.setActionCommand("btnSignout");
		btnSignout.addActionListener(this);

		functionality();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		String selectedCmb = String.valueOf(cmbStatus.getSelectedItem());
		if(actionCommand.equals("btnStatus")){
			if(selectedCmb.equals("--Choose--")){
				JOptionPane.showMessageDialog(null, "Please Choose a Correct Combo Value!!!", "Error", JOptionPane.ERROR_MESSAGE);
			}else{
				tlFrame.setVisible(false);
				valueMap.put("screenName", "TL");
				valueMap.put("tlFilterComboValue", selectedCmb);
				new TlApprovalFunctionality(valueMap);
			}
		}else if(actionCommand.equals("btnLveRequest")){
			tlFrame.setVisible(false);
			valueMap.put("loginAs", 0);
			valueMap.put("back", "1");
			new EmpLeaveApply(valueMap);
		}else if(actionCommand.equals("btnLveSummary")){
			tlFrame.setVisible(false);
			valueMap.put("screenName", "TL");
			new LeaveSummaryFunctionality(valueMap);
		}else if(actionCommand.equals("btnApplyRequest")){
			valueMap.put("loginAs", 1);
			valueMap.put("back", "1");
			tlFrame.setVisible(false);
			new TeamNameDialog(valueMap);
		}else if(actionCommand.equals("btnEdtProfile")){
			JOptionPane.showMessageDialog(null,"Future update");
		}else if(actionCommand.equals("btnSignout")){
			tlFrame.setVisible(false);
			new LoginFunctionality();
		}
	}

	public void functionality(){
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveapprovallogin","root","root");
			statement = (Statement) connection.createStatement();
			resultSet = statement.executeQuery("select count(tl) from applyleave where  tl = '"+valueMap.get("empName")+"' and leave_status = '0' and ((curdate() <= fddate or curdate() <= hddate) or curdate() <= perdate)");
			if(resultSet.next()){
				lblCount.setText("No. of Incoming Request : "+resultSet.getInt(1));
			}
			resultSet = statement.executeQuery("select DISTINCT leave_type from applyleave where  tl = '"+valueMap.get("empName")+"' and leave_status = '0' and ((leave_type = 'Casual Leave' or leave_type = 'Sick Leave') or leave_type = 'Permission') order by leave_type ASC");
			while(resultSet.next()){
				cmbStatus.addItem(resultSet.getString("leave_type"));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} 
			if(connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

	}
}
