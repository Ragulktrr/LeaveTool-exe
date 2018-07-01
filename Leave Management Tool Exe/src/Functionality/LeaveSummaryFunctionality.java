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

import Design.LeaveSummaryDesign;

public class LeaveSummaryFunctionality extends LeaveSummaryDesign implements ActionListener {

	Map<String, Object> valueMap;
	@SuppressWarnings("unchecked")
	public LeaveSummaryFunctionality(Map<String, Object> valueMap){
		super(valueMap);
		this.valueMap = valueMap;

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			if(valueMap.get("level").equals(1)){
				resultSet = statement.executeQuery("select emp_name from infoviewLogin where tl = '"+valueMap.get("empName")+"' order by emp_name ASC");
				cmbTeamList.addItem(valueMap.get("empName"));
				while(resultSet.next()){
					cmbTeamList.addItem(resultSet.getString("emp_name"));
				}
			}else if(valueMap.get("level").equals(2)){
				resultSet = statement.executeQuery("select distinct tl from infoviewLogin where pmo = '"+valueMap.get("empName")+"' and tl != '-' order by tl ASC");
				while(resultSet.next()){
					cmbTlList.addItem(resultSet.getString("tl"));
				}
				cmbTeamList.addItem(cmbTlList.getSelectedItem());
				resultSet = statement.executeQuery("select emp_name from infoviewLogin where tl = '"+cmbTeamList.getSelectedItem()+"' order by emp_name ASC");
				while(resultSet.next()){
					cmbTeamList.addItem(resultSet.getString("emp_name"));
				}
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

		loadTable(cmbStatus.getSelectedItem().toString());

		btnLoad.setActionCommand("btnLoad");
		btnLoad.addActionListener(this);

		btnBack.setActionCommand("btnBack");
		btnBack.addActionListener(this);

		btnAllLeaves.setActionCommand("btnAllLeaves");
		btnAllLeaves.addActionListener(this);

		btnCheckStatus.setActionCommand("btnCheckStatus");
		btnCheckStatus.addActionListener(this);

		btnCancelLeaves.setActionCommand("btnCancelLeaves");
		btnCancelLeaves.addActionListener(this);

		cmbTlList.setActionCommand("cmbTlList");
		cmbTlList.addActionListener(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent evt) {
		String actionCommand = evt.getActionCommand();
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			String leaveType = null;
			String teamName = null;
			String tlName = null;
			if(actionCommand.equals("btnLoad")){
				btnCancelLeaves.setEnabled(true);
				leaveType = cmbStatus.getSelectedItem().toString();
				if(valueMap.get("level").equals(0)){
					loadGrid(leaveType, teamName, tlName);
				} else if(valueMap.get("level").equals(1)){
					teamName = cmbTeamList.getSelectedItem().toString();
					loadGrid(leaveType, teamName, tlName);
				} else if(valueMap.get("level").equals(2)){
					teamName = cmbTeamList.getSelectedItem().toString();
					tlName = cmbTlList.getSelectedItem().toString();
					loadGrid(leaveType, teamName, tlName);
				}
			} else if(actionCommand.equals("btnBack")){
				if(valueMap.get("screenName").equals("emp")){
					summaryFrame.setVisible(false);
					new EmpLeaveApply(valueMap);
				} else if(valueMap.get("screenName").equals("TL")){
					summaryFrame.setVisible(false);
					new TlFormFunctionality(valueMap);
				} else if(valueMap.get("screenName").equals("PMO")){
					summaryFrame.setVisible(false);
					new PmoApprovalFunctionality(valueMap);
				}
			} else if(actionCommand.equals("btnAllLeaves")){
				if(cmbTeamList.getSelectedItem() != null){
					if(valueMap.get("empName").equals(cmbTeamList.getSelectedItem())){
						btnCancelLeaves.setEnabled(false);
					}else{
						btnCancelLeaves.setEnabled(true);
					}
				}else{
					btnCancelLeaves.setEnabled(false);
				}
				btnAllLeaves();
			} else if(actionCommand.equals("btnCheckStatus")){
				btnCancelLeaves.setEnabled(true);
				btnCheckStatus();
			} else if(actionCommand.equals("btnCancelLeaves")){
				btnCancelLeaves();
			} else if(actionCommand.equals("cmbTlList")){
				cmbTeamList.removeAllItems();
				cmbTeamList.addItem(cmbTlList.getSelectedItem());
				resultSet = statement.executeQuery("select emp_name from infoviewLogin where tl = '"+cmbTlList.getSelectedItem()+"' order by emp_name ASC");
				while(resultSet.next()){
					cmbTeamList.addItem(resultSet.getString("emp_name"));
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
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
	public void btnAllLeaves(){
		int i = 0;
		Connection connection = null;
		Statement statement = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			ResultSet rs = null;

			String leaveType = null;
			String teamName = null;
			String tlName = null;

			model.setColumnCount(0);
			model.setRowCount(0);
			model.addColumn("Select");
			model.addColumn("ID");
			model.addColumn("Name");

			leaveType = cmbStatus.getSelectedItem().toString();
			if(valueMap.get("level").equals(1)){
				teamName = cmbTeamList.getSelectedItem().toString();
			} else if(valueMap.get("level").equals(2)){
				teamName = cmbTeamList.getSelectedItem().toString();
				tlName = cmbTlList.getSelectedItem().toString();
			}
			if(leaveType != null && teamName != null && (tlName != null || tlName == null)){
				if(leaveType.equals("Permission")){
					rs = statement.executeQuery("select emp_id, emp_name, perdate, day, per_applied_for, leave_status from applyleave where emp_name = '"+teamName+"' and leave_type = '"+leaveType+"' order by perdate ASC");
				}else{
					rs = statement.executeQuery("select * from (select emp_id, emp_name, fddate as LeaveDate, session, day, lop_date, leave_status, reason from applyleave where emp_name = '"+teamName+"' and leave_type = '"+leaveType+"' and fddate != '-' and fddate != '' Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, leave_status, reason from applyleave where emp_name = '"+teamName+"' and leave_type = '"+leaveType+"' and hddate != '-' and hddate != '') as leavedd order by leavedd.leaveDate Asc");
				}
			}else if(leaveType != null && teamName == null && tlName == null){
				if(leaveType.equals("Permission")){
					rs = statement.executeQuery("select emp_id, emp_name, perdate, day, per_applied_for, leave_status from applyleave where emp_name = '"+valueMap.get("empName")+"' and leave_type = '"+leaveType+"' order by perdate ASC");
				}else{
					rs = statement.executeQuery("select * from (select emp_id, emp_name, fddate as LeaveDate, session, day, lop_date, leave_status, reason from applyleave where emp_name = '"+valueMap.get("empName")+"' and leave_type = '"+leaveType+"' and fddate != '-' and fddate != '' Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, leave_status, reason from applyleave where emp_name = '"+valueMap.get("empName")+"' and leave_type = '"+leaveType+"' and hddate != '-' and hddate != '') as leavedd order by leavedd.leaveDate Asc");
				}
			}
			if(leaveType.equals("Casual Leave")){
				i = 0;
				model.addColumn("CL_Date");
				model.addColumn("Session");
				model.addColumn("Day");
				model.addColumn("Lop");
				model.addColumn("Waiting/Confirmed");
				model.addColumn("Reason");
				while(rs.next()){
					model.addRow(new Object[0]);
					model.setValueAt(false, i, 0);
					model.setValueAt(rs.getString("emp_id"), i, 1);
					model.setValueAt(rs.getString("emp_name"), i, 2);
					model.setValueAt(rs.getString("LeaveDate"), i, 3);
					model.setValueAt(rs.getString("session"), i, 4);
					model.setValueAt(rs.getString("day"), i, 5);
					if(rs.getString("lop_date").equals("1")){
						model.setValueAt(true, i, 6);
					}else{
						model.setValueAt(false, i, 6);
					}
					if(rs.getString("leave_status").equals("0")){
						model.setValueAt("TL Approval", i, 7);
					}else if(rs.getString("leave_status").equals("1")){
						model.setValueAt("PMO Approval", i, 7);
					}else if(rs.getString("leave_status").equals("2")){
						model.setValueAt("Confirmed", i, 7);
					}else if(rs.getString("leave_status").equals("1")){
						model.setValueAt("Approved", i, 7);
					}else if(rs.getString("leave_status").equals("-1")){
						model.setValueAt("Rejected", i, 7);
					}
					model.setValueAt(rs.getString("reason"), i, 8);
					i++;
				}
			}else if(leaveType.equals("Sick Leave")){
				i = 0;
				model.addColumn("SL_Date");
				model.addColumn("Session");
				model.addColumn("Day");
				model.addColumn("Lop");
				model.addColumn("Waiting/Confirmed");
				model.addColumn("Reason");
				while(rs.next()){
					model.addRow(new Object[0]);
					model.setValueAt(false, i, 0);
					model.setValueAt(rs.getString("emp_id"), i, 1);
					model.setValueAt(rs.getString("emp_name"), i, 2);
					model.setValueAt(rs.getString("LeaveDate"), i, 3);
					model.setValueAt(rs.getString("session"), i, 4);
					model.setValueAt(rs.getString("day"), i, 5);
					if(rs.getString("lop_date").equals("1")){
						model.setValueAt(true, i, 6);
					}else{
						model.setValueAt(false, i, 6);
					}
					if(rs.getString("leave_status").equals("0")){
						model.setValueAt("TL Approval", i, 7);
					}else if(rs.getString("leave_status").equals("1")){
						model.setValueAt("PMO Approval", i, 7);
					}else if(rs.getString("leave_status").equals("2")){
						model.setValueAt("Confirmed", i, 7);
					}else if(rs.getString("leave_status").equals("1")){
						model.setValueAt("Approved", i, 7);
					}else if(rs.getString("leave_status").equals("-1")){
						model.setValueAt("Rejected", i, 7);
					}
					model.setValueAt(rs.getString("reason"), i, 8);
					i++;
				}
			}else if(leaveType.equals("Permission")){
				i = 0;
				model.addColumn("Permission_Date");
				model.addColumn("Day");
				model.addColumn("Hours");
				model.addColumn("Waiting/Confirmed");
				while(rs.next()){
					model.addRow(new Object[0]);
					model.setValueAt(false, i, 0);
					model.setValueAt(rs.getString("emp_id"), i, 1);
					model.setValueAt(rs.getString("emp_name"), i, 2);
					model.setValueAt(rs.getString("perdate"), i, 3);
					model.setValueAt(rs.getString("day"), i, 4);
					model.setValueAt(rs.getString("per_applied_for"), i, 5);
					if(rs.getString("leave_status").equals("0")){
						model.setValueAt("TL Approval", i, 6);
					}else if(rs.getString("leave_status").equals("2")){
						model.setValueAt("Confirmed", i, 6);
					}else if(rs.getString("leave_status").equals("-1")){
						model.setValueAt("Rejected", i, 6);
					}
					i++;
				}
			}
		} catch(ClassNotFoundException e1){
			JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
		} catch(SQLException e1){
			JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
		} finally{
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
	public void btnCheckStatus(){
		int i = 0;
		Connection connection = null;
		Statement statement = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			ResultSet rs = null;

			String leaveType = null;
			String teamName = null;
			String tlName = null;

			model.setColumnCount(0);
			model.setRowCount(0);
			model.addColumn("Select");
			model.addColumn("ID");
			model.addColumn("Name");

			leaveType = cmbStatus.getSelectedItem().toString();
			if(valueMap.get("level").equals(1)){
				teamName = cmbTeamList.getSelectedItem().toString();
			} else if(valueMap.get("level").equals(2)){
				teamName = cmbTeamList.getSelectedItem().toString();
				tlName = cmbTlList.getSelectedItem().toString();
			}
			if(leaveType != null && teamName != null && (tlName != null || tlName == null)){
				if(leaveType.equals("Permission")){
					rs = statement.executeQuery("select emp_id, emp_name, perdate, day, per_applied_for, leave_status from applyleave where emp_name = '"+teamName+"' and (leave_type = '"+leaveType+"' and curdate() <= perdate) order by perdate ASC");
				}else{
					rs = statement.executeQuery("select * from (select emp_id, emp_name, fddate as LeaveDate, session, day, lop_date, leave_status, reason from applyleave where emp_name = '"+teamName+"' and (leave_type = '"+leaveType+"' and curdate() <= fddate) Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, leave_status, reason from applyleave where emp_name = '"+teamName+"' and (leave_type = '"+leaveType+"' and  curdate() <= hddate)) as leavedd order by leavedd.leaveDate Asc");
				}
			}else if(leaveType != null && teamName == null && tlName == null){
				if(leaveType.equals("Permission")){
					rs = statement.executeQuery("select emp_id, emp_name, perdate, day, per_applied_for, leave_status from applyleave where emp_name = '"+valueMap.get("empName")+"' and (leave_type = '"+leaveType+"' and curdate() <= perdate) order by perdate ASC");
				}else{
					rs = statement.executeQuery("select * from (select emp_id, emp_name, fddate as LeaveDate, session, day, lop_date, leave_status, reason from applyleave where emp_name = '"+valueMap.get("empName")+"' and (leave_type = '"+leaveType+"' and curdate() <= fddate) Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, leave_status, reason from applyleave where emp_name = '"+valueMap.get("empName")+"' and (leave_type = '"+leaveType+"' and  curdate() <= hddate)) as leavedd order by leavedd.leaveDate Asc");
				}
			}
			if(leaveType.equals("Casual Leave")){
				i = 0;
				model.addColumn("CL_Date");
				model.addColumn("Session");
				model.addColumn("Day");
				model.addColumn("Lop");
				model.addColumn("Waiting/Confirmed");
				model.addColumn("Reason");
				while(rs.next()){
					model.addRow(new Object[0]);
					model.setValueAt(false, i, 0);
					model.setValueAt(rs.getString("emp_id"), i, 1);
					model.setValueAt(rs.getString("emp_name"), i, 2);
					model.setValueAt(rs.getString("LeaveDate"), i, 3);
					model.setValueAt(rs.getString("session"), i, 4);
					model.setValueAt(rs.getString("day"), i, 5);
					if(rs.getString("lop_date").equals("1")){
						model.setValueAt(true, i, 6);
					}else{
						model.setValueAt(false, i, 6);
					}
					if(rs.getString("leave_status").equals("0")){
						model.setValueAt("TL Approval", i, 7);
					}else if(rs.getString("leave_status").equals("1")){
						model.setValueAt("PMO Approval", i, 7);
					}else if(rs.getString("leave_status").equals("2")){
						model.setValueAt("Confirmed", i, 7);
					}else if(rs.getString("leave_status").equals("1")){
						model.setValueAt("Approved", i, 7);
					}else if(rs.getString("leave_status").equals("-1")){
						model.setValueAt("Rejected", i, 7);
					}
					model.setValueAt(rs.getString("reason"), i, 8);
					i++;
				}
			}else if(leaveType.equals("Sick Leave")){
				i = 0;
				model.addColumn("SL_Date");
				model.addColumn("Session");
				model.addColumn("Day");
				model.addColumn("Lop");
				model.addColumn("Waiting/Confirmed");
				model.addColumn("Reason");
				while(rs.next()){
					model.addRow(new Object[0]);
					model.setValueAt(false, i, 0);
					model.setValueAt(rs.getString("emp_id"), i, 1);
					model.setValueAt(rs.getString("emp_name"), i, 2);
					model.setValueAt(rs.getString("LeaveDate"), i, 3);
					model.setValueAt(rs.getString("session"), i, 4);
					model.setValueAt(rs.getString("day"), i, 5);
					if(rs.getString("lop_date").equals("1")){
						model.setValueAt(true, i, 6);
					}else{
						model.setValueAt(false, i, 6);
					}
					if(rs.getString("leave_status").equals("0")){
						model.setValueAt("TL Approval", i, 7);
					}else if(rs.getString("leave_status").equals("1")){
						model.setValueAt("PMO Approval", i, 7);
					}else if(rs.getString("leave_status").equals("2")){
						model.setValueAt("Confirmed", i, 7);
					}else if(rs.getString("leave_status").equals("1")){
						model.setValueAt("Approved", i, 7);
					}else if(rs.getString("leave_status").equals("-1")){
						model.setValueAt("Rejected", i, 7);
					}
					model.setValueAt(rs.getString("reason"), i, 8);
					i++;
				}
			}else if(leaveType.equals("Permission")){
				i = 0;
				model.addColumn("Permission_Date");
				model.addColumn("Day");
				model.addColumn("Hours");
				model.addColumn("Waiting/Confirmed");
				while(rs.next()){
					model.addRow(new Object[0]);
					model.setValueAt(false, i, 0);
					model.setValueAt(rs.getString("emp_id"), i, 1);
					model.setValueAt(rs.getString("emp_name"), i, 2);
					model.setValueAt(rs.getString("perdate"), i, 3);
					model.setValueAt(rs.getString("day"), i, 4);
					model.setValueAt(rs.getString("per_applied_for"), i, 5);
					if(rs.getString("leave_status").equals("0")){
						model.setValueAt("TL Approval", i, 6);
					}else if(rs.getString("leave_status").equals("2")){
						model.setValueAt("Confirmed", i, 6);
					}else if(rs.getString("leave_status").equals("-1")){
						model.setValueAt("Rejected", i, 6);
					}
					i++;
				}
			}
		} catch(ClassNotFoundException e1){
			JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
		} catch(SQLException e1){
			JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
		} finally{
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

	public void btnCancelLeaves(){
		Connection connection = null;
		Statement statement = null;
		int i = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			ResultSet rs = null;
			Double tempClLeft = 0.0;
			Double tempSlLeft = 0.0;
			Double tempPer = 0.0;
			Double tempLop = 0.0;
			if(table.getColumnCount() == 9 || table.getColumnCount() == 8){
				for(i = 0; i < table.getRowCount(); i++){
					Boolean checked = Boolean.valueOf(table.getValueAt(i, 0).toString());
					Integer id = Integer.parseInt((String) table.getValueAt(i, 1));
					String fdDate = table.getValueAt(i, 3).toString();
					String session = table.getValueAt(i, 4).toString();
					Boolean lop = Boolean.valueOf(table.getValueAt(i, 6).toString());
					if(checked){
						rs = statement.executeQuery("select cl_left, sl_left, per_left, LOP from infoviewlogin where emp_Id = '"+id+"'");
						while(rs.next()){
							tempClLeft = rs.getDouble("cl_left");
							tempSlLeft = rs.getDouble("sl_left");
							tempPer = rs.getDouble("per_left");
							tempLop = rs.getDouble("LOP");
						}
						if(lop){
							if(session.equals("-")){
								tempLop = tempLop - 1;
								statement.executeUpdate("DELETE FROM applyleave WHERE emp_id = '"+id+"' and fddate = '"+fdDate+"'");
							}else{
								tempLop = tempLop - 0.5;
								statement.executeUpdate("DELETE FROM applyleave WHERE emp_id = '"+id+"' and (hddate = '"+fdDate+"' and session = '"+session+"')");
							}
						}else{
							if(table.getColumnName(3).equals("CL_Date")){
								if(session.equals("-")){
									tempClLeft = tempClLeft + 1;
									statement.executeUpdate("DELETE FROM applyleave WHERE emp_id = '"+id+"' and fddate = '"+fdDate+"'");
								}else{
									tempClLeft = tempClLeft + 0.5;
									statement.executeUpdate("DELETE FROM applyleave WHERE emp_id = '"+id+"' and (hddate = '"+fdDate+"' and session = '"+session+"')");
								}
							}else if(table.getColumnName(3).equals("SL_Date")){
								if(session.equals("-")){
									tempSlLeft = tempSlLeft + 1;
									statement.executeUpdate("DELETE FROM applyleave WHERE emp_id = '"+id+"' and fddate = '"+fdDate+"'");
								}else{
									tempSlLeft = tempSlLeft + 0.5;
									statement.executeUpdate("DELETE FROM applyleave WHERE emp_id = '"+id+"' and (hddate = '"+fdDate+"' and session = '"+session+"')");
								}
							}
						}
						statement.executeUpdate("UPDATE infoviewlogin SET cl_left = '"+tempClLeft+"', sl_left = '"+tempSlLeft+"', per_left = '"+tempPer+"', LOP = '"+tempLop+"' WHERE emp_Id = "+id+"");
					}
				}
			}else if(table.getColumnCount() == 7 || table.getColumnCount() == 6){
				for(i = 0; i < table.getRowCount(); i++){
					Boolean checked = Boolean.valueOf(table.getValueAt(i, 0).toString());
					Integer id = Integer.parseInt((String) table.getValueAt(i, 1));
					String perDate = table.getValueAt(i, 3).toString();
					Integer hours = Integer.parseInt((String) table.getValueAt(i, 5));
					if(checked){
						rs = statement.executeQuery("select per_left from infoviewlogin where emp_Id = '"+id+"'");
						while(rs.next()){
							tempPer = rs.getDouble("per_left");
						}
						tempPer = tempPer + hours;
						statement.executeUpdate("DELETE FROM applyleave WHERE emp_id = '"+id+"' and perdate = '"+perDate+"'");
						statement.executeUpdate("UPDATE infoviewlogin SET per_left = '"+tempPer+"' WHERE emp_Id = "+id+"");
					}
				}
			}
			if(table.getColumnCount() != 0){
				for(i = 0; i < table.getRowCount(); i++){
					Boolean checked = Boolean.valueOf(table.getValueAt(i, 0).toString());
					if(checked){
						btnLoad.doClick();
						JOptionPane.showMessageDialog(null, "Sucessfully Cancelled!!!");
					}
				}
			}else{
				JOptionPane.showMessageDialog(null, "Please Load Some values in table!!!");
			}
		} catch(ClassNotFoundException e1){
			JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
		} catch(SQLException e1){
			JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
		} finally {
			if(statement != null)
				try {
					statement.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				} 
			if(connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} 
		}
	}

	public void loadGrid(String leaveTypeParam, String teamNameParam, String tlNameParam) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		int i = 0;
		model.setColumnCount(0);
		model.setRowCount(0);
		model.addColumn("Select");
		model.addColumn("ID");
		model.addColumn("Name");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			String leaveType = leaveTypeParam; 
			String teamName = teamNameParam; 
			String tlName = tlNameParam;
			if(leaveType != null && teamName != null && tlName != null){
				i = 0;
				if(!leaveType.equals("Permission")){
					if(leaveType.equals("Casual Leave")){
						model.addColumn("CL_Date");
					}else if(leaveType.equals("Sick Leave")){
						model.addColumn("SL_Date");	
					}
					model.addColumn("Session");
					model.addColumn("Day");
					model.addColumn("Lop");
					model.addColumn("Reason");
					resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, reason from applyleave where (emp_name = '"+teamName+"' and (tl = '"+tlName+"' or tl = '-')) and (leave_type = '"+leaveType+"' and curdate() <= fddate ) Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, reason from applyleave where (emp_name = '"+teamName+"' and (tl = '"+tlName+"' or tl = '-')) and (leave_type = '"+leaveType+"' and  curdate() <= hddate)) as leavedd	order by leavedd.leaveDate ASC");
					while(resultSet.next()){
						model.addRow(new Object[0]);
						model.setValueAt(false, i, 0);
						model.setValueAt(resultSet.getString("emp_id"), i, 1);
						model.setValueAt(resultSet.getString("emp_name"), i, 2);
						model.setValueAt(resultSet.getString("leaveDate"), i, 3);
						model.setValueAt(resultSet.getString("session"), i, 4);
						model.setValueAt(resultSet.getString("day"), i, 5);
						if(resultSet.getString("lop_date").equals("1")){
							model.setValueAt(true, i, 6);
						}else{
							model.setValueAt(false, i, 6);
						}
						model.setValueAt(resultSet.getString("reason"), i, 7);
						i++;
					}
				} else {
					model.addColumn("Permission_Date");	
					model.addColumn("Day");
					model.addColumn("Hours");
					resultSet = statement.executeQuery("select emp_id, emp_name, perdate, day, per_applied_for from applyleave where (emp_name = '"+teamName+"' and (tl = '"+tlName+"' or tl = '-')) and (leave_type = '"+leaveType+"' and curdate() <= perdate) order by perdate ASC");
					while(resultSet.next()){
						model.addRow(new Object[0]);
						model.setValueAt(false, i, 0);
						model.setValueAt(resultSet.getString("emp_id"), i, 1);
						model.setValueAt(resultSet.getString("emp_name"), i, 2);
						model.setValueAt(resultSet.getString("perdate"), i, 3);
						model.setValueAt(resultSet.getString("day"), i, 4);
						model.setValueAt(resultSet.getString("per_applied_for"), i, 5);
						i++;
					}
				}
			}else if(leaveType != null && teamName != null && tlName == null){
				i = 0;
				if(!leaveType.equals("Permission")){
					if(leaveType.equals("Casual Leave")){
						model.addColumn("CL_Date");
					}else if(leaveType.equals("Sick Leave")){
						model.addColumn("SL_Date");	
					}
					model.addColumn("Session");
					model.addColumn("Day");
					model.addColumn("Lop");
					model.addColumn("Reason");
					resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, reason from applyleave where emp_name = '"+teamName+"' and (leave_type = '"+leaveType+"' and curdate() <= fddate ) Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, reason from applyleave where emp_name = '"+teamName+"' and (leave_type = '"+leaveType+"' and  curdate() <= hddate)) as leavedd	order by leavedd.leaveDate ASC");
					while(resultSet.next()){
						model.addRow(new Object[0]);
						model.setValueAt(false, i, 0);
						model.setValueAt(resultSet.getString("emp_id"), i, 1);
						model.setValueAt(resultSet.getString("emp_name"), i, 2);
						model.setValueAt(resultSet.getString("leaveDate"), i, 3);
						model.setValueAt(resultSet.getString("session"), i, 4);
						model.setValueAt(resultSet.getString("day"), i, 5);
						if(resultSet.getString("lop_date").equals("1")){
							model.setValueAt(true, i, 6);
						}else{
							model.setValueAt(false, i, 6);
						}
						model.setValueAt(resultSet.getString("reason"), i, 7);
						i++;
					}
				} else {
					model.addColumn("Permission_Date");	
					model.addColumn("Day");
					model.addColumn("Hours");
					resultSet = statement.executeQuery("select emp_id, emp_name, perdate, day, per_applied_for from applyleave where emp_name = '"+teamName+"' and (leave_type = '"+leaveType+"' and curdate() <= perdate) order by perdate ASC");
					while(resultSet.next()){
						model.addRow(new Object[0]);
						model.setValueAt(false, i, 0);
						model.setValueAt(resultSet.getString("emp_id"), i, 1);
						model.setValueAt(resultSet.getString("emp_name"), i, 2);
						model.setValueAt(resultSet.getString("perdate"), i, 3);
						model.setValueAt(resultSet.getString("day"), i, 4);
						model.setValueAt(resultSet.getString("per_applied_for"), i, 5);
						i++;
					}
				}
			}else if(leaveType != null && teamName == null && tlName == null){
				i = 0;
				if(!leaveType.equals("Permission")){
					if(leaveType.equals("Casual Leave")){
						model.addColumn("CL_Date");
					}else if(leaveType.equals("Sick Leave")){
						model.addColumn("SL_Date");	
					}
					model.addColumn("Session");
					model.addColumn("Day");
					model.addColumn("Lop");
					model.addColumn("Reason");
					resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, reason from applyleave where emp_name = '"+valueMap.get("empName")+"' and (leave_type = '"+leaveType+"' and curdate() <= fddate ) Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, reason from applyleave where emp_name = '"+valueMap.get("empName")+"' and (leave_type = '"+leaveType+"' and  curdate() <= hddate)) as leavedd	order by leavedd.leaveDate ASC");
					while(resultSet.next()){
						model.addRow(new Object[0]);
						model.setValueAt(false, i, 0);
						model.setValueAt(resultSet.getString("emp_id"), i, 1);
						model.setValueAt(resultSet.getString("emp_name"), i, 2);
						model.setValueAt(resultSet.getString("leaveDate"), i, 3);
						model.setValueAt(resultSet.getString("session"), i, 4);
						model.setValueAt(resultSet.getString("day"), i, 5);
						if(resultSet.getString("lop_date").equals("1")){
							model.setValueAt(true, i, 6);
						}else{
							model.setValueAt(false, i, 6);
						}
						model.setValueAt(resultSet.getString("reason"), i, 7);
						i++;
					}
				} else {
					model.addColumn("Permission_Date");	
					model.addColumn("Day");
					model.addColumn("Hours");
					resultSet = statement.executeQuery("select emp_id, emp_name, perdate, day, per_applied_for from applyleave where emp_name = '"+valueMap.get("empName")+"' and (leave_type = '"+leaveType+"' and curdate() <= perdate) order by perdate ASC");
					while(resultSet.next()){
						model.addRow(new Object[0]);
						model.setValueAt(false, i, 0);
						model.setValueAt(resultSet.getString("emp_id"), i, 1);
						model.setValueAt(resultSet.getString("emp_name"), i, 2);
						model.setValueAt(resultSet.getString("perdate"), i, 3);
						model.setValueAt(resultSet.getString("day"), i, 4);
						model.setValueAt(resultSet.getString("per_applied_for"), i, 5);
						i++;
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
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
