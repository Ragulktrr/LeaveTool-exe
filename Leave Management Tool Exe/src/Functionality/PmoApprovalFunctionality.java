package Functionality;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.swing.JOptionPane;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import Design.PmoApprovalDesign;

public class PmoApprovalFunctionality extends PmoApprovalDesign implements ActionListener {

	Map<String, Object> valueMap;

	public PmoApprovalFunctionality(Map<String, Object> valueMap){
		super(valueMap);
		this.valueMap = valueMap;

		cmbTlList.setActionCommand("cmbTlList");
		cmbTlList.addActionListener(this);

		btnTlForm.setActionCommand("btnTlForm");
		btnTlForm.addActionListener(this);

		btnStatus.setActionCommand("btnStatus");
		btnStatus.addActionListener(this);

		btnleaveSummary.setActionCommand("btnleaveSummary");
		btnleaveSummary.addActionListener(this);

		btnTeamRequest.setActionCommand("btnTeamRequest");
		btnTeamRequest.addActionListener(this);

		btnSearch.setActionCommand("btnSearch");
		btnSearch.addActionListener(this);

		btnSignout.setActionCommand("btnSignout");
		btnSignout.addActionListener(this);

		functionality();
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String actionCommand = evt.getActionCommand();
		if(actionCommand.equals("btnTlForm")){
			if(cmbLeaveType.getSelectedItem().equals("Sick Leave")){
				JOptionPane.showMessageDialog(null, "Sick leaves are waiting for your approval \n Please press Approve Status Button!!!");
			}else{
				valueMap.put("PmoAsTl", "PMOasTL");
				valueMap.put("tlFilterComboValue", cmbLeaveType.getSelectedItem().toString());
				valueMap.put("screenName", "PMO");
				try {
					Class.forName("com.mysql.jdbc.Driver");
					connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveapprovallogin","root","root");
					statement = (Statement) connection.createStatement();
					resultSet = statement.executeQuery("select * from infoviewLogin where emp_Name = '"+cmbTlList.getSelectedItem()+"'");
					while(resultSet.next()){
						valueMap.put("emp_IdDB", resultSet.getInt("emp_Id"));
						valueMap.put("empName", resultSet.getString("emp_Name"));
						valueMap.put("password", resultSet.getString("pwd"));
						valueMap.put("TL", resultSet.getString("TL"));
						valueMap.put("PMO", resultSet.getString("PMO"));
						valueMap.put("clleft", (Double)resultSet.getDouble("cl_left"));
						valueMap.put("slleft", (Double)resultSet.getDouble("sl_left"));
						valueMap.put("perleft", (Double)resultSet.getDouble("per_left"));
						valueMap.put("LOP", (Double)resultSet.getDouble("LOP"));
					}
					valueMap.put("level", 2);
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
				pmoFrame.setVisible(false);
				new TlApprovalFunctionality(valueMap);
			}
		}else if(actionCommand.equals("btnStatus")){
			pmoFrame.setVisible(false);
			valueMap.put("screenName", "PMO");
			valueMap.put("tlFilterComboValue", cmbLeaveType.getSelectedItem().toString());
			new PmoApproveRejectFunctionality(valueMap);
		}else if(actionCommand.equals("btnleaveSummary")){
			pmoFrame.setVisible(false);
			valueMap.put("screenName", "PMO");
			new LeaveSummaryFunctionality(valueMap);
		}else if(actionCommand.equals("btnTeamRequest")){
			valueMap.put("levelCheck", 2);
			valueMap.put("loginAs", 2);
			valueMap.put("back", "2");
			pmoFrame.setVisible(false);
			new TeamNameDialog(valueMap);
		}else if(actionCommand.equals("btnSearch")){
			pmoFrame.setVisible(false);
			new SearchAndExportFunctionality(valueMap);
		}else if(actionCommand.equals("btnSignout")){
			pmoFrame.setVisible(false);
			new LoginFunctionality();
		}
	}

	@SuppressWarnings("unchecked")
	public void functionality(){
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveapprovallogin","root","root");
			statement = (Statement) connection.createStatement();
			resultSet = statement.executeQuery("select count(pmo) from applyleave where  pmo = '"+valueMap.get("empName")+"' and leave_status = 1 and leave_type != 'Permission'");
			if(resultSet.next()){
				lblCount.setText("No. of Incoming Request from TL : "+resultSet.getInt(1));
			}
			resultSet = statement.executeQuery("select DISTINCT tl from applyleave where  pmo = '"+valueMap.get("empName")+"' and tl != '-' and leave_status = 0 and ((leave_type = 'Casual Leave' or leave_type = 'Sick Leave') or leave_type = 'Permission') order by tl ASC");
			while(resultSet.next()){
				cmbTlList.addItem(resultSet.getString("tl"));
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
