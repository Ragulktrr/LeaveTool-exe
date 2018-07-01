package Functionality;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import Design.PmoApprovalRejectDesign;

@SuppressWarnings({ "static-access", "unchecked" })
public class PmoApproveRejectFunctionality extends PmoApprovalRejectDesign implements ActionListener {
	public static Map<String, Object> valueMap;
	public String selectedCmb;
	public static DefaultTableModel model;
	int i = 0;
	int refresh = 0;
	public PmoApproveRejectFunctionality(Map<String, Object> valueMap){
		super(valueMap);
		this.valueMap = valueMap;
		cmbName.addItem("All");
		cmbFilter.addItem("<None>");
		cmbFilter.addItem("LOP");
		cmbFilter.addItem("Unplanned Leaves");

		btnApprove.setActionCommand("btnApprove");
		btnApprove.addActionListener(this);

		btnReject.setActionCommand("btnReject");
		btnReject.addActionListener(this);

		btnRejectApproved.setActionCommand("btnRejectApproved");
		btnRejectApproved.addActionListener(this);

		btnSelectAll.setActionCommand("btnSelectAll");
		btnSelectAll.addActionListener(this);

		btnTlWaiting.setActionCommand("btnTlWaiting");
		btnTlWaiting.addActionListener(this);
		
		btnBack.setActionCommand("btnBack");
		btnBack.addActionListener(this);

		btnHome.setActionCommand("btnHome");
		btnHome.addActionListener(this);

		cmbName.setActionCommand("cmbName");
		cmbName.addActionListener(this);

		cmbLveType.setActionCommand("cmbLveType");
		cmbLveType.addActionListener(this);

		cmbFilter.setActionCommand("cmbFilter");
		cmbFilter.addActionListener(this);

		btnSelectAll.requestFocus();

		queryFunctionality();
		if(valueMap.get("tlFilterComboValue").equals("Permission")){
			lblFilter.setVisible(false);
			cmbFilter.setVisible(false);
		}else{
			lblFilter.setVisible(true);
			cmbFilter.setVisible(true);
		}
		selectedCmb = String.valueOf(cmbName.getSelectedItem());
		if(selectedCmb.equals("All")){
			comboValueAll();
		}else{
			lblCmbLveType.setVisible(true);
			cmbLveType.setVisible(true);
		}
	}

	public void queryFunctionality(){
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			resultSet = statement.executeQuery("select DISTINCT emp_name from applyleave where  pmo = '"+valueMap.get("empName")+"' and ((curdate() <= fddate or curdate() <= hddate) or curdate() <= perdate) order by emp_name ASC");
			while(resultSet.next()){
				cmbName.addItem(resultSet.getString("emp_name"));
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

	@Override
	public void actionPerformed(ActionEvent evt) {
		Connection connection = null;
		Statement statement = null;
		try {
			refresh = 0;
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			ResultSet resultSet = null;
			String actionCommand = evt.getActionCommand();
			if(actionCommand.equals("btnApprove")){
				Double tempClLeft = 0.0;
				Double tempSlLeft = 0.0;
				Double tempPer = 0.0;
				Double tempLop = 0.0;
				if(table.getColumnCount() == 9){
					if(table.getColumnName(7).equals("Status")){
						for(i = 0; i < table.getRowCount(); i++){
							Boolean checked = Boolean.valueOf(table.getValueAt(i, 0).toString());
							Integer id = Integer.parseInt((String) table.getValueAt(i, 1));
							String session = table.getValueAt(i, 4).toString();
							String status = table.getValueAt(i, 7).toString();
							if(checked){
								if(status.equals("Rejected")){
									resultSet = statement.executeQuery("select cl_left, sl_left, per_left, LOP from infoviewlogin where emp_Id = '"+id+"'");
									while(resultSet.next()){
										tempClLeft = resultSet.getDouble("cl_left");
										tempSlLeft = resultSet.getDouble("sl_left");
										tempPer = resultSet.getDouble("per_left");
										tempLop = resultSet.getDouble("LOP");
									}
									if(table.getColumnName(3).equals("CL_Date")){
										if(session.equals("-")){
											tempClLeft = tempClLeft - 1;
										}else{
											tempClLeft = tempClLeft - 0.5;
										}
									}else if(table.getColumnName(3).equals("SL_Date")){
										if(session.equals("-")){
											tempSlLeft = tempSlLeft - 1;
										}else{
											tempSlLeft = tempSlLeft - 0.5;
										}
									}
									statement.executeUpdate("UPDATE infoviewlogin SET cl_left = '"+tempClLeft+"', sl_left = '"+tempSlLeft+"', per_left = '"+tempPer+"', LOP = '"+tempLop+"' WHERE emp_Id = "+id+"");
								}
							}
						}
						for(i = 0; i < table.getRowCount(); i++){
							Boolean checked = Boolean.valueOf(table.getValueAt(i, 0).toString());
							Integer id = Integer.parseInt((String) table.getValueAt(i, 1));
							String fdDate = table.getValueAt(i, 3).toString();
							String session = table.getValueAt(i, 4).toString();
							String status = table.getValueAt(i, 7).toString();
							if(checked){
								if(status.equals("Rejected")){
									if(table.getColumnName(3).equals("CL_Date")){
										if(session.equals("-")){
											statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = 2 WHERE emp_Id = "+id+" and fddate = '"+fdDate+"'");
										}else{
											statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = 2 WHERE emp_Id = "+id+" and (hddate = '"+fdDate+"' and session ='"+session+"')");
										}
									}else if(table.getColumnName(3).equals("SL_Date")){
										if(session.equals("-")){
											statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = 2 WHERE emp_Id = "+id+" and fddate = '"+fdDate+"'");
										}else{
											statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = 2 WHERE emp_Id = "+id+" and (hddate = '"+fdDate+"' and session ='"+session+"')");
										}
									}else if(table.getColumnName(3).equals("Permission_Date")){
										statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = 2 WHERE emp_Id = "+id+" and perdate = '"+fdDate+"'");
									}
								}
							}
						}
					}
				}else if(table.getColumnCount() == 8){
					for(i = 0; i < table.getRowCount(); i++){
						Boolean checked = Boolean.valueOf(table.getValueAt(i, 0).toString());
						Integer id = Integer.parseInt((String) table.getValueAt(i, 1));
						String fdDate = table.getValueAt(i, 3).toString();
						String session = table.getValueAt(i, 4).toString();
						if(checked){
							if(table.getColumnName(3).equals("CL_Date")){
								if(session.equals("-")){
									statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = 2 WHERE emp_Id = "+id+" and fddate = '"+fdDate+"'");
								}else{
									statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = 2 WHERE emp_Id = "+id+" and (hddate = '"+fdDate+"' and session ='"+session+"')");
								}
							}else if(table.getColumnName(3).equals("SL_Date")){
								if(session.equals("-")){
									statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = 2 WHERE emp_Id = "+id+" and fddate = '"+fdDate+"'");
								}else{
									statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = 2 WHERE emp_Id = "+id+" and (hddate = '"+fdDate+"' and session ='"+session+"')");
								}
							}else if(table.getColumnName(3).equals("Permission_Date")){
								statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = 2 WHERE emp_Id = "+id+" and perdate = '"+fdDate+"'");
							}
						}
					}
				}else if(table.getColumnCount() == 6){
					for(i = 0; i < table.getRowCount(); i++){
						Boolean checked = Boolean.valueOf(table.getValueAt(i, 0).toString());
						Integer id = Integer.parseInt((String) table.getValueAt(i, 1));
						String perDate = table.getValueAt(i, 3).toString();
						if(checked){
							statement.executeUpdate("UPDATE applyleave SET leave_status = '2' WHERE emp_id = "+id+" and perdate = '"+perDate+"'");
						}
					}
				}else if(table.getColumnCount() == 7){
					if(table.getColumnName(6).equals("Status")){
						for(i = 0; i < table.getRowCount(); i++){
							String status = table.getValueAt(i, 6).toString();
							if(status.equals("Rejected")){
								Boolean checked = Boolean.valueOf(table.getValueAt(i, 0).toString());
								Integer id = Integer.parseInt((String) table.getValueAt(i, 1));
								String perDate = table.getValueAt(i, 3).toString();
								double hours = Double.parseDouble((String) table.getValueAt(i, 5));
								if(checked){
									resultSet = statement.executeQuery("select per_left, LOP from infoviewlogin where emp_Id = '"+id+"'");
									while(resultSet.next()){
										tempPer = resultSet.getDouble("per_left");
									}
									tempPer = tempPer - hours;
									statement.executeUpdate("UPDATE infoviewlogin SET per_left = '"+tempPer+"' WHERE emp_Id = "+id+"");
									statement.executeUpdate("UPDATE applyleave SET leave_status = '2' WHERE emp_id = "+id+" and perdate = '"+perDate+"'");
								}
							}
						}
					}
				}
				for(int j = 0; j < table.getRowCount(); j++){
					Boolean checked = Boolean.valueOf(table.getValueAt(j, 0).toString());
					if(checked){
						refresh = 3;
					}
				}
				if(refresh == 3){
					if(table.getColumnCount() == 7 || table.getColumnCount() == 9){
						btnApproveRejectMethod();
					}else{
						if(selectedCmb.equals("All")){
							comboValueAll();
						}else{
							comboOtherValue();						
						}
					}
				}
			}else if(actionCommand.equals("btnReject")){
				refresh = 0;
				Double tempClLeft = 0.0;
				Double tempSlLeft = 0.0;
				Double tempPer = 0.0;
				Double tempLop = 0.0;
				if(!table.getColumnName(3).equals("Permission_Date")){
					if(table.getColumnCount() == 9){
						if(table.getColumnName(7).equals("Status")){
							for(i = 0; i < table.getRowCount(); i++){
								Boolean checked = Boolean.valueOf(table.getValueAt(i, 0).toString());
								Integer id = Integer.parseInt((String) table.getValueAt(i, 1));
								String fdDate = table.getValueAt(i, 3).toString();
								String session = table.getValueAt(i, 4).toString();
								Boolean lop = Boolean.valueOf(table.getValueAt(i, 6).toString());
								String status = table.getValueAt(i, 7).toString();
								if(checked){
									if(status.equals("Approved")){
										resultSet = statement.executeQuery("select cl_left, sl_left, per_left, LOP from infoviewlogin where emp_Id = '"+id+"'");
										while(resultSet.next()){
											tempClLeft = resultSet.getDouble("cl_left");
											tempSlLeft = resultSet.getDouble("sl_left");
											tempPer = resultSet.getDouble("per_left");
											tempLop = resultSet.getDouble("LOP");
										}
										if(lop == false){
											if(table.getColumnName(3).equals("CL_Date")){
												if(session.equals("-")){
													tempClLeft = tempClLeft + 1;
												}else{
													tempClLeft = tempClLeft + 0.5;
												}
											}else if(table.getColumnName(3).equals("SL_Date")){
												if(session.equals("-")){
													tempSlLeft = tempSlLeft + 1;
												}else{
													tempSlLeft = tempSlLeft + 0.5;
												}
											}
										}else if(lop == true){
											if(table.getColumnName(3).equals("CL_Date")){
												if(session.equals("-")){
													tempLop = tempLop - 1;
													statement.executeUpdate("DELETE FROM applyleave where emp_Id = '"+id+"' and fddate = '"+fdDate+"'");
												}else{
													tempLop = tempLop - 0.5;
													statement.executeUpdate("DELETE FROM applyleave where emp_Id = '"+id+"' and (hddate = '"+fdDate+"' and session = '"+session+"')");
												}
											}else if(table.getColumnName(3).equals("SL_Date")){
												if(session.equals("-")){
													tempLop = tempLop - 1;
													statement.executeUpdate("DELETE FROM applyleave where emp_Id = '"+id+"' and fddate = '"+fdDate+"'");
												}else{
													tempLop = tempLop - 0.5;
													statement.executeUpdate("DELETE FROM applyleave where emp_Id = '"+id+"' and (hddate = '"+fdDate+"' and session = '"+session+"')");
												}
											}
										}
										statement.executeUpdate("UPDATE infoviewlogin SET cl_left = '"+tempClLeft+"', sl_left = '"+tempSlLeft+"', per_left = '"+tempPer+"', LOP = '"+tempLop+"' WHERE emp_Id = "+id+"");
									}
								}
							}
							for(i = 0; i < table.getRowCount(); i++){
								Boolean checked = Boolean.valueOf(table.getValueAt(i, 0).toString());
								Integer id = Integer.parseInt((String) table.getValueAt(i, 1));
								String fdDate = table.getValueAt(i, 3).toString();
								String session = table.getValueAt(i, 4).toString();
								Boolean lop = Boolean.valueOf(table.getValueAt(i, 6).toString());
								String status = table.getValueAt(i, 7).toString();
								if(checked){
									if(status.equals("Approved")){
										if(!table.getColumnName(3).equals("Permission_Date")){
											if(lop == false){
												if(table.getColumnName(3).equals("CL_Date")){
													if(session.equals("-")){
														statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = -1 WHERE emp_Id = "+id+" and fddate = '"+fdDate+"'");
													}else{
														statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = -1 WHERE emp_Id = "+id+" and (hddate = '"+fdDate+"' and session ='"+session+"')");
													}
												}else if(table.getColumnName(3).equals("SL_Date")){
													if(session.equals("-")){
														statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = -1 WHERE emp_Id = "+id+" and fddate = '"+fdDate+"'");
													}else{
														statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = -1 WHERE emp_Id = "+id+" and (hddate = '"+fdDate+"' and session ='"+session+"')");
													}
												}
											}
										}else{
											statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = -1 WHERE emp_Id = "+id+" and perdate = '"+fdDate+"'");
										}
									}
								}
							}
						}
					}else if(table.getColumnCount() == 8){
						for(i = 0; i < table.getRowCount(); i++){
							Boolean checked = Boolean.valueOf(table.getValueAt(i, 0).toString());
							Integer id = Integer.parseInt((String) table.getValueAt(i, 1));
							String fdDate = table.getValueAt(i, 3).toString();
							String session = table.getValueAt(i, 4).toString();
							Boolean lop = Boolean.valueOf(table.getValueAt(i, 6).toString());
							if(checked){
								resultSet = statement.executeQuery("select cl_left, sl_left, per_left, LOP from infoviewlogin where emp_Id = '"+id+"'");
								while(resultSet.next()){
									tempClLeft = resultSet.getDouble("cl_left");
									tempSlLeft = resultSet.getDouble("sl_left");
									tempPer = resultSet.getDouble("per_left");
									tempLop = resultSet.getDouble("LOP");
								}
								if(lop == false){
									if(table.getColumnName(3).equals("CL_Date")){
										if(session.equals("-")){
											tempClLeft = tempClLeft + 1;
										}else{
											tempClLeft = tempClLeft + 0.5;
										}
									}else if(table.getColumnName(3).equals("SL_Date")){
										if(session.equals("-")){
											tempSlLeft = tempSlLeft + 1;
										}else{
											tempSlLeft = tempSlLeft + 0.5;
										}
									}
								}else if(lop == true){
									if(table.getColumnName(3).equals("CL_Date")){
										if(session.equals("-")){
											tempLop = tempLop - 1;
											statement.executeUpdate("DELETE FROM applyleave where emp_Id = '"+id+"' and fddate = '"+fdDate+"'");
										}else{
											tempLop = tempLop - 0.5;
											statement.executeUpdate("DELETE FROM applyleave where emp_Id = '"+id+"' and (hddate = '"+fdDate+"' and session = '"+session+"')");
										}
									}else if(table.getColumnName(3).equals("SL_Date")){
										if(session.equals("-")){
											tempLop = tempLop - 1;
											statement.executeUpdate("DELETE FROM applyleave where emp_Id = '"+id+"' and fddate = '"+fdDate+"'");
										}else{
											tempLop = tempLop - 0.5;
											statement.executeUpdate("DELETE FROM applyleave where emp_Id = '"+id+"' and (hddate = '"+fdDate+"' and session = '"+session+"')");
										}
									}
								}
								statement.executeUpdate("UPDATE infoviewlogin SET cl_left = '"+tempClLeft+"', sl_left = '"+tempSlLeft+"', per_left = '"+tempPer+"', LOP = '"+tempLop+"' WHERE emp_Id = "+id+"");
							}
						}
						for(i = 0; i < table.getRowCount(); i++){
							Boolean checked = Boolean.valueOf(table.getValueAt(i, 0).toString());
							Integer id = Integer.parseInt((String) table.getValueAt(i, 1));
							String fdDate = table.getValueAt(i, 3).toString();
							String session = table.getValueAt(i, 4).toString();
							Boolean lop = Boolean.valueOf(table.getValueAt(i, 6).toString());
							if(checked){
								if(!table.getColumnName(3).equals("Permission_Date")){
									if(lop == false){
										if(table.getColumnName(3).equals("CL_Date")){
											if(session.equals("-")){
												statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = -1 WHERE emp_Id = "+id+" and fddate = '"+fdDate+"'");
											}else{
												statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = -1 WHERE emp_Id = "+id+" and (hddate = '"+fdDate+"' and session ='"+session+"')");
											}
										}else if(table.getColumnName(3).equals("SL_Date")){
											if(session.equals("-")){
												statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = -1 WHERE emp_Id = "+id+" and fddate = '"+fdDate+"'");
											}else{
												statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = -1 WHERE emp_Id = "+id+" and (hddate = '"+fdDate+"' and session ='"+session+"')");
											}
										}
									}
								}else{
									statement.executeUpdate("UPDATE `leaveapprovallogin`.`applyleave` SET leave_status = -1 WHERE emp_Id = "+id+" and perdate = '"+fdDate+"'");
								}
							}
						}
					}
				}else if(table.getColumnName(3).equals("Permission_Date")){
					if(table.getColumnCount() == 7){
						if(table.getColumnName(6).equals("Status")){
							for(i = 0; i < table.getRowCount(); i++){
								String status = table.getValueAt(i, 6).toString();
								if(status.equals("Approved")){
									Boolean checked = Boolean.valueOf(table.getValueAt(i, 0).toString());
									Integer id = Integer.parseInt((String) table.getValueAt(i, 1));
									String perDate = table.getValueAt(i, 3).toString();
									double hours = Double.parseDouble((String) table.getValueAt(i, 5));
									if(checked){
										resultSet = statement.executeQuery("select per_left, LOP from infoviewlogin where emp_Id = '"+id+"'");
										while(resultSet.next()){
											tempPer = resultSet.getDouble("per_left");
										}
										tempPer = tempPer + hours;
										statement.executeUpdate("UPDATE infoviewlogin SET per_left = '"+tempPer+"' WHERE emp_Id = "+id+"");
										statement.executeUpdate("UPDATE applyleave SET leave_status = '-1' WHERE emp_id = "+id+" and perdate = '"+perDate+"'");
									}
								}
							}
						}
					}else if(table.getColumnCount() == 6){
						for(i = 0; i < table.getRowCount(); i++){
							Boolean checked = Boolean.valueOf(table.getValueAt(i, 0).toString());
							Integer id = Integer.parseInt((String) table.getValueAt(i, 1));
							String perDate = table.getValueAt(i, 3).toString();
							double hours = Double.parseDouble((String) table.getValueAt(i, 5));
							if(checked){
								resultSet = statement.executeQuery("select per_left, LOP from infoviewlogin where emp_Id = '"+id+"'");
								while(resultSet.next()){
									tempPer = resultSet.getDouble("per_left");
								}
								tempPer = tempPer + hours;
								statement.executeUpdate("UPDATE infoviewlogin SET per_left = '"+tempPer+"' WHERE emp_Id = "+id+"");
								statement.executeUpdate("UPDATE applyleave SET leave_status = '-1' WHERE emp_id = "+id+" and perdate = '"+perDate+"'");
							}
						}
					}
				}
				for(int j = 0; j < table.getRowCount(); j++){
					Boolean checked = Boolean.valueOf(table.getValueAt(j, 0).toString());
					if(checked){
						refresh = 3;
					}
				}
				if(refresh == 3){
					if(table.getColumnCount() == 7 || table.getColumnCount() == 9){
						btnApproveRejectMethod();
					}else{
						if(selectedCmb.equals("All")){
							comboValueAll();
						}else{
							comboOtherValue();						
						}
					}
				}
			}else if(actionCommand.equals("btnRejectApproved")){
				lblStatusDisplay.setText("Approved / Rejected List :");
				refresh = 3;
				btnApproveRejectMethod();
			}else if(actionCommand.equals("btnSelectAll")){
				if(table.getRowCount() > 0){
					if(btnSelectAll.getText().equals("Select All")){
						for(i = 0; i < table.getRowCount(); i++){
							Boolean checked = Boolean.valueOf(table.getValueAt(i, 0).toString());
							if(!checked){
								table.setValueAt(true, i, 0);
							}
						}
						btnSelectAll.setText("Select None");
					}else{
						for(i = 0; i < table.getRowCount(); i++){
							Boolean checked = Boolean.valueOf(table.getValueAt(i, 0).toString());
							if(checked){
								table.setValueAt(false, i, 0);
							}
						}
						btnSelectAll.setText("Select All");
					}
				}
			}else if(actionCommand.equals("btnTlWaiting")){
				lblStatusDisplay.setText("Waiting For TL Approval :");
				model.setColumnCount(0);
				model.setRowCount(0);
				model.addColumn("Select");
				model.addColumn("ID");
				model.addColumn("Name");
				if(selectedCmb.equals("All")){
					if(cmbLveType.getSelectedItem() == null){
						if(valueMap.get("tlFilterComboValue").equals("Casual Leave")){
							i = 0;
							model.addColumn("CL_Date");
							model.addColumn("Session");
							model.addColumn("Day");
							model.addColumn("Lop");
							model.addColumn("Reason");
							resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and fddate != '' and fddate != '-' and leave_type = 'Casual Leave' and leave_status = 0 Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and hddate != '' and hddate != '-' and leave_type = 'Casual Leave' and leave_status = 0) as leavedd order by leavedd.leaveDate Asc");
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
						}else if(valueMap.get("tlFilterComboValue").equals("Sick Leave")){
							i = 0;
							model.addColumn("SL_Date");
							model.addColumn("Session");
							model.addColumn("Day");
							model.addColumn("Lop");
							model.addColumn("Reason");
							resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and fddate != '' and fddate != '-' and leave_type = 'Sick Leave' and leave_status = 0 Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and hddate != '' and hddate != '-' and leave_type = 'Sick Leave' and leave_status = 0) as leavedd order by leavedd.leaveDate Asc");
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
						}else if(valueMap.get("tlFilterComboValue").equals("Permission")){
							i = 0;
							model.addColumn("Permission_Date");
							model.addColumn("Day");
							model.addColumn("Hours");
							resultSet = statement.executeQuery("select emp_id, emp_name, perdate, day, lop_date, per_applied_for from applyleave where pmo = '"+valueMap.get("empName")+"' and leave_type = 'Permission' and leave_status = 0 order by perdate ASC");
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
				}else{
					if(cmbLveType.getSelectedItem().equals("Casual Leave")){
						i = 0;
						model.addColumn("CL_Date");
						model.addColumn("Session");
						model.addColumn("Day");
						model.addColumn("Lop");
						model.addColumn("Reason");
						resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"' and fddate != '' and fddate != '-') and leave_type = 'Casual Leave' and leave_status = 0 Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"' and hddate != '' and hddate != '-') and leave_type = 'Casual Leave' and leave_status = 0) as leavedd order by leavedd.leaveDate Asc");
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
					}else if(cmbLveType.getSelectedItem().equals("Sick Leave")){
						i = 0;
						model.addColumn("SL_Date");
						model.addColumn("Session");
						model.addColumn("Day");
						model.addColumn("Lop");
						model.addColumn("Reason");
						resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"' and fddate != '' and fddate != '-') and leave_type = 'Sick Leave' and leave_status = 0 Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"' and hddate != '' and hddate != '-') and leave_type = 'Sick Leave' and leave_status = 0) as leavedd order by leavedd.leaveDate Asc");
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
					}else if(cmbLveType.getSelectedItem().equals("Permission")){
						i = 0;
						model.addColumn("Permission_Date");
						model.addColumn("Day");
						model.addColumn("Hours");
						resultSet = statement.executeQuery("select emp_id, emp_name, perdate, day, lop_date, per_applied_for from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and leave_type = 'Permission' and leave_status = 0 order by perdate ASC");
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
				lblFilter.setVisible(false);
				cmbFilter.setVisible(false);
			}else if(actionCommand.equals("btnBack")){
				if(valueMap.get("screenName").equals("TL")){
					pmoApprovalFrame.setVisible(false);
					new TlFormFunctionality(valueMap);
				}else if(valueMap.get("screenName").equals("PMO")){
					pmoApprovalFrame.setVisible(false);
					new PmoApprovalFunctionality(valueMap);
				}
			}else if(actionCommand.equals("btnHome")){
				pmoApprovalFrame.setVisible(false);
				new LoginFunctionality();
			}else if(actionCommand.equals("cmbName")){
				lblStatusDisplay.setText("Waiting For Approval :");
				selectedCmb = String.valueOf(cmbName.getSelectedItem());
				btnApprove.setEnabled(true);
				btnReject.setEnabled(true);
				btnRejectApproved.setEnabled(true);
				cmbLveType.removeAllItems();
				if(!selectedCmb.equals("All")){
					resultSet = statement.executeQuery("select DISTINCT leave_type from applyleave where  emp_name = '"+selectedCmb+"' and ((curdate() <= fddate or curdate() <= hddate) or curdate() <= perdate) order by leave_type ASC");
					while(resultSet.next()){
						cmbLveType.addItem(resultSet.getString("leave_type"));
					}
				}
				if(selectedCmb.equals("All")){
					lblFilter.setVisible(true);
					cmbFilter.setVisible(true);
					comboValueAll();
				}else{
					lblCmbLveType.setVisible(true);
					cmbLveType.setVisible(true);
					comboOtherValue();
				}
				cmbFilter.setSelectedIndex(0);
				btnSelectAll.requestFocus();
			}else if(actionCommand.equals("cmbLveType")){
				btnApprove.setEnabled(true);
				btnReject.setEnabled(true);
				btnRejectApproved.setEnabled(true);
				lblStatusDisplay.setText("Waiting For Approval :");
				if(cmbLveType.getSelectedItem() != null){
					if(cmbLveType.getSelectedItem().equals("Permission")){
						lblFilter.setVisible(false);
						cmbFilter.setVisible(false);
					}else{
						lblFilter.setVisible(true);
						cmbFilter.setVisible(true);
					}
				}
				if(cmbLveType.getSelectedItem() == null){
					comboValueAll();
				}else{
					comboOtherValue();
				}
				cmbFilter.setSelectedIndex(0);
			}else if(actionCommand.equals("cmbFilter")){
				btnApprove.setEnabled(true);
				btnReject.setEnabled(true);
				btnRejectApproved.setEnabled(true);
				comboValueAllLop();
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

	public void btnApproveRejectMethod(){
		Connection connection = null;
		Statement statement = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			ResultSet resultSet = null;
			model.setColumnCount(0);
			model.setRowCount(0);
			model.addColumn("Select");
			model.addColumn("ID");
			model.addColumn("Name");
			if(!selectedCmb.equals("All")){
				if(cmbLveType.getSelectedItem().equals("Casual Leave")){
					i = 0;
					model.addColumn("CL_Date");
					model.addColumn("Session");
					model.addColumn("Day");
					model.addColumn("Lop");
					model.addColumn("Status");
					model.addColumn("Reason");
					resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, leave_status, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Casual Leave' and curdate() <= fddate) and (leave_status = 2 or leave_status = -1) Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, leave_status, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Casual Leave' and curdate() <= hddate) and (leave_status = 2 or leave_status = -1)) as leavedd order by leavedd.leaveDate Asc");
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
						if(resultSet.getString("leave_status").equals("2")){
							model.setValueAt("Approved", i, 7);
						}else if(resultSet.getString("leave_status").equals("-1")){
							model.setValueAt("Rejected", i, 7);
						}
						model.setValueAt(resultSet.getString("reason"), i, 8);
						i++;
					}
				}else if(cmbLveType.getSelectedItem().equals("Sick Leave")){
					i = 0;
					model.addColumn("SL_Date");
					model.addColumn("Session");
					model.addColumn("Day");
					model.addColumn("Lop");
					model.addColumn("Status");
					model.addColumn("Reason");
					resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, leave_status, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Sick Leave' and curdate() <= fddate) and (leave_status = 2 or leave_status = -1) Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, leave_status, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Sick Leave' and curdate() <= hddate) and (leave_status = 2 or leave_status = -1)) as leavedd order by leavedd.leaveDate Asc");
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
						if(resultSet.getString("leave_status").equals("2")){
							model.setValueAt("Approved", i, 7);
						}else if(resultSet.getString("leave_status").equals("-1")){
							model.setValueAt("Rejected", i, 7);
						}
						model.setValueAt(resultSet.getString("reason"), i, 8);
						i++;
					}
				}else if(cmbLveType.getSelectedItem().equals("Permission")){
					i = 0;
					model.addColumn("Permission_Date");
					model.addColumn("Day");
					model.addColumn("Hours");
					model.addColumn("Status");
					resultSet = statement.executeQuery("select emp_id, emp_name, perdate, day, lop_date, per_applied_for, leave_status from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Permission' and curdate() <= perdate) and (leave_status = 2 or leave_status = -1) order by perdate ASC");
					while(resultSet.next()){
						model.addRow(new Object[0]);
						model.setValueAt(false, i, 0);
						model.setValueAt(resultSet.getString("emp_id"), i, 1);
						model.setValueAt(resultSet.getString("emp_name"), i, 2);
						model.setValueAt(resultSet.getString("perdate"), i, 3);
						model.setValueAt(resultSet.getString("day"), i, 4);
						model.setValueAt(resultSet.getString("per_applied_for"), i, 5);
						if(resultSet.getString("leave_status").equals("2")){
							model.setValueAt("Approved", i, 6);
						}else if(resultSet.getString("leave_status").equals("-1")){
							model.setValueAt("Rejected", i, 6);
						}
						i++;
					}
				}
			}else{
				if(cmbLveType.getSelectedItem() == null){
					if(valueMap.get("tlFilterComboValue").equals("Casual Leave")){
						i = 0;
						model.addColumn("CL_Date");
						model.addColumn("Session");
						model.addColumn("Day");
						model.addColumn("Lop");
						model.addColumn("Status");
						model.addColumn("Reason");
						resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, leave_status, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Casual Leave' and curdate() <= fddate) and (leave_status = 2 or leave_status = -1) Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, leave_status, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Casual Leave' and curdate() <= hddate) and (leave_status = 2 or leave_status = -1)) as leavedd order by leavedd.leaveDate Asc");
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
							if(resultSet.getString("leave_status").equals("2")){
								model.setValueAt("Approved", i, 7);
							}else if(resultSet.getString("leave_status").equals("-1")){
								model.setValueAt("Rejected", i, 7);
							}
							model.setValueAt(resultSet.getString("reason"), i, 8);
							i++;
						}
					}else if(valueMap.get("tlFilterComboValue").equals("Sick Leave")){
						i = 0;
						model.addColumn("SL_Date");
						model.addColumn("Session");
						model.addColumn("Day");
						model.addColumn("Lop");
						model.addColumn("Status");
						model.addColumn("Reason");
						resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, leave_status, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Sick Leave' and curdate() <= fddate) and (leave_status = 2 or leave_status = -1) Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, leave_status, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Sick Leave' and curdate() <= hddate) and (leave_status = 2 or leave_status = -1)) as leavedd order by leavedd.leaveDate Asc");
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
							if(resultSet.getString("leave_status").equals("2")){
								model.setValueAt("Approved", i, 7);
							}else if(resultSet.getString("leave_status").equals("-1")){
								model.setValueAt("Rejected", i, 7);
							}
							model.setValueAt(resultSet.getString("reason"), i, 8);
							i++;
						}
					}else if(valueMap.get("tlFilterComboValue").equals("Permission")){
						i = 0;
						model.addColumn("Permission_Date");
						model.addColumn("Day");
						model.addColumn("Hours");
						model.addColumn("Status");
						resultSet = statement.executeQuery("select emp_id, emp_name, perdate, day, lop_date, per_applied_for, leave_status from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Permission' and curdate() <= perdate) and (leave_status = 2 or leave_status = -1) order by perdate ASC");
						while(resultSet.next()){
							model.addRow(new Object[0]);
							model.setValueAt(false, i, 0);
							model.setValueAt(resultSet.getString("emp_id"), i, 1);
							model.setValueAt(resultSet.getString("emp_name"), i, 2);
							model.setValueAt(resultSet.getString("perdate"), i, 3);
							model.setValueAt(resultSet.getString("day"), i, 4);
							model.setValueAt(resultSet.getString("per_applied_for"), i, 5);
							if(resultSet.getString("leave_status").equals("2")){
								model.setValueAt("Approved", i, 6);
							}else if(resultSet.getString("leave_status").equals("-1")){
								model.setValueAt("Rejected", i, 6);
							}
							i++;
						}
					}
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

	public void comboOtherValue(){
		if(cmbLveType.getSelectedItem() != null){
			Connection connection = null;
			Statement statement = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
				statement = (Statement) connection.createStatement();
				ResultSet resultSet = null;
				model.setColumnCount(0);
				model.setRowCount(0);
				model.addColumn("Select");
				model.addColumn("ID");
				model.addColumn("Name");
				if(cmbLveType.getSelectedItem().equals("Casual Leave")){
					i = 0;
					model.addColumn("CL_Date");
					model.addColumn("Session");
					model.addColumn("Day");
					model.addColumn("Lop");
					model.addColumn("Reason");
					resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Casual Leave' and curdate() <= fddate) and leave_status = 1 Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Casual Leave' and curdate() <= hddate) and leave_status = 1) as leavedd order by leavedd.leaveDate Asc");
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
				}else if(cmbLveType.getSelectedItem().equals("Sick Leave")){
					i = 0;
					model.addColumn("SL_Date");
					model.addColumn("Session");
					model.addColumn("Day");
					model.addColumn("Lop");
					model.addColumn("Reason");
					resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Sick Leave' and curdate() <= fddate) and leave_status = 1 Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Sick Leave' and curdate() <= hddate) and leave_status = 1) as leavedd order by leavedd.leaveDate Asc");
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
				}else if(cmbLveType.getSelectedItem().equals("Permission")){
					i = 0;
					model.addColumn("Permission_Date");
					model.addColumn("Day");
					model.addColumn("Hours");
					resultSet = statement.executeQuery("select emp_id, emp_name, perdate, day, lop_date, per_applied_for from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Permission' and curdate() <= perdate) and leave_status = 0 order by perdate ASC");
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
	}

	public void comboValueAllLop(){
		model.setColumnCount(0);
		model.setRowCount(0);
		model.addColumn("Select");
		model.addColumn("ID");
		model.addColumn("Name");
		if(cmbFilter.getSelectedItem().equals("LOP")){
			allValueLop();
		}else if(cmbFilter.getSelectedItem().equals("Unplanned Leaves")){
			otherValueLop();
		}else{
			if(selectedCmb.equals("All")){
				comboValueAll();
			}else{
				comboOtherValue();
			}
		}
	}

	public void allValueLop(){
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			if(selectedCmb.equals("All")){
				if(valueMap.get("tlFilterComboValue").equals("Casual Leave")){
					i = 0;
					model.addColumn("CL_Date");
					model.addColumn("Session");
					model.addColumn("Day");
					model.addColumn("Lop");
					model.addColumn("Reason");
					resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Casual Leave' and curdate() <= fddate) and leave_status = 1 and lop_date = 1 Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Casual Leave' and curdate() <= hddate) and leave_status = 1 and lop_date = 1) as leavedd order by leavedd.leaveDate Asc");
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
				}else if(valueMap.get("tlFilterComboValue").equals("Sick Leave")){
					i = 0;
					model.addColumn("SL_Date");
					model.addColumn("Session");
					model.addColumn("Day");
					model.addColumn("Lop");
					model.addColumn("Reason");
					resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Sick Leave' and curdate() <= fddate) and leave_status = 1 and lop_date = 1 Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Sick Leave' and curdate() <= hddate) and leave_status = 1 and lop_date = 1) as leavedd order by leavedd.leaveDate Asc");
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
				}else if(valueMap.get("tlFilterComboValue").equals("Permission")){
					i = 0;
					model.addColumn("Permission_Date");
					model.addColumn("Day");
					model.addColumn("Hours");
					resultSet = statement.executeQuery("select emp_id, emp_name, perdate, day, per_applied_for from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Permission' and curdate() <= perdate) and leave_status = 0 order by perdate ASC");
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
				lblCmbLveType.setVisible(false);
				cmbLveType.setVisible(false);
			}else{
				if(cmbLveType.getSelectedItem().equals("Casual Leave")){
					i = 0;
					model.addColumn("CL_Date");
					model.addColumn("Session");
					model.addColumn("Day");
					model.addColumn("Lop");
					model.addColumn("Reason");
					resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Casual Leave' and curdate() <= fddate) and leave_status = 1 and lop_date = 1 Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Casual Leave' and curdate() <= hddate) and leave_status = 1 and lop_date = 1) as leavedd order by leavedd.leaveDate Asc");
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
				}else if(cmbLveType.getSelectedItem().equals("Sick Leave")){
					i = 0;
					model.addColumn("SL_Date");
					model.addColumn("Session");
					model.addColumn("Day");
					model.addColumn("Lop");
					model.addColumn("Reason");
					resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Sick Leave' and curdate() <= fddate) and leave_status = 1 and lop_date = 1 Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Sick Leave' and curdate() <= hddate) and leave_status = 1 and lop_date = 1) as leavedd order by leavedd.leaveDate Asc");
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
				}else if(cmbLveType.getSelectedItem().equals("Permission")){
					i = 0;
					model.addColumn("Permission_Date");
					model.addColumn("Day");
					model.addColumn("Hours");
					resultSet = statement.executeQuery("select emp_id, emp_name, perdate, day, lop_date, per_applied_for from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Permission' and curdate() <= perdate) and leave_status = 0 order by perdate ASC");
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

	public void otherValueLop(){
		Connection connection = null;
		Statement statement = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			ResultSet resultSet = null;
			if(selectedCmb.equals("All")){
				if(valueMap.get("tlFilterComboValue").equals("Casual Leave")){
					i = 0;
					model.addColumn("CL_Date");
					model.addColumn("Session");
					model.addColumn("Day");
					model.addColumn("Unplanned_Leave");
					model.addColumn("Reason");
					resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, unplanned_leave, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Casual Leave' and curdate() <= fddate) and leave_status = 1 and unplanned_leave = 1 Union All select emp_id, emp_name, hddate as leaveDate, session, day, unplanned_leave, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Casual Leave' and curdate() <= hddate) and leave_status = 1 and unplanned_leave = 1) as leavedd order by leavedd.leaveDate Asc");
					while(resultSet.next()){
						model.addRow(new Object[0]);
						model.setValueAt(false, i, 0);
						model.setValueAt(resultSet.getString("emp_id"), i, 1);
						model.setValueAt(resultSet.getString("emp_name"), i, 2);
						model.setValueAt(resultSet.getString("leaveDate"), i, 3);
						model.setValueAt(resultSet.getString("session"), i, 4);
						model.setValueAt(resultSet.getString("day"), i, 5);
						if(resultSet.getString("unplanned_leave").equals("1")){
							model.setValueAt(true, i, 6);
						}else{
							model.setValueAt(false, i, 6);
						}
						model.setValueAt(resultSet.getString("reason"), i, 7);
						i++;
					}
				}else if(valueMap.get("tlFilterComboValue").equals("Sick Leave")){
					i = 0;
					model.addColumn("SL_Date");
					model.addColumn("Session");
					model.addColumn("Day");
					model.addColumn("Unplanned_Leave");
					model.addColumn("Reason");
					resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, unplanned_leave, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Sick Leave' and curdate() <= fddate) and leave_status = 1 and unplanned_leave = 1 Union All select emp_id, emp_name, hddate as leaveDate, session, day, unplanned_leave, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Sick Leave' and curdate() <= hddate) and leave_status = 1 and unplanned_leave = 1) as leavedd order by leavedd.leaveDate Asc");
					while(resultSet.next()){
						model.addRow(new Object[0]);
						model.setValueAt(false, i, 0);
						model.setValueAt(resultSet.getString("emp_id"), i, 1);
						model.setValueAt(resultSet.getString("emp_name"), i, 2);
						model.setValueAt(resultSet.getString("leaveDate"), i, 3);
						model.setValueAt(resultSet.getString("session"), i, 4);
						model.setValueAt(resultSet.getString("day"), i, 5);
						if(resultSet.getString("unplanned_leave").equals("1")){
							model.setValueAt(true, i, 6);
						}else{
							model.setValueAt(false, i, 6);
						}
						model.setValueAt(resultSet.getString("reason"), i, 7);
						i++;
					}
				}else if(valueMap.get("tlFilterComboValue").equals("Permission")){
					i = 0;
					model.addColumn("Permission_Date");
					model.addColumn("Day");
					model.addColumn("Hours");
					resultSet = statement.executeQuery("select emp_id, emp_name, perdate, day, lop_date, per_applied_for from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Permission' and curdate() <= perdate) and leave_status = 0 order by perdate ASC");
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
			}else{
				if(cmbLveType.getSelectedItem().equals("Casual Leave")){
					i = 0;
					model.addColumn("CL_Date");
					model.addColumn("Session");
					model.addColumn("Day");
					model.addColumn("Unplanned_Leave");
					model.addColumn("Reason");
					resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, unplanned_leave, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Casual Leave' and curdate() <= fddate) and leave_status = 1 and unplanned_leave = 1 Union All select emp_id, emp_name, hddate as leaveDate, session, day, unplanned_leave, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Casual Leave' and curdate() <= hddate) and leave_status = 1 and unplanned_leave = 1) as leavedd order by leavedd.leaveDate Asc");
					while(resultSet.next()){
						model.addRow(new Object[0]);
						model.setValueAt(false, i, 0);
						model.setValueAt(resultSet.getString("emp_id"), i, 1);
						model.setValueAt(resultSet.getString("emp_name"), i, 2);
						model.setValueAt(resultSet.getString("leaveDate"), i, 3);
						model.setValueAt(resultSet.getString("session"), i, 4);
						model.setValueAt(resultSet.getString("day"), i, 5);
						if(resultSet.getString("unplanned_leave").equals("1")){
							model.setValueAt(true, i, 6);
						}else{
							model.setValueAt(false, i, 6);
						}
						model.setValueAt(resultSet.getString("reason"), i, 7);
						i++;
					}
				}else if(cmbLveType.getSelectedItem().equals("Sick Leave")){
					i = 0;
					model.addColumn("SL_Date");
					model.addColumn("Session");
					model.addColumn("Day");
					model.addColumn("Unplanned_Leave");
					model.addColumn("Reason");
					resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, unplanned_leave, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Sick Leave' and curdate() <= fddate) and leave_status = 1 and unplanned_leave = 1 Union All select emp_id, emp_name, hddate as leaveDate, session, day, unplanned_leave, reason from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Sick Leave' and curdate() <= hddate) and leave_status = 1 and unplanned_leave = 1) as leavedd order by leavedd.leaveDate Asc");
					while(resultSet.next()){
						model.addRow(new Object[0]);
						model.setValueAt(false, i, 0);
						model.setValueAt(resultSet.getString("emp_id"), i, 1);
						model.setValueAt(resultSet.getString("emp_name"), i, 2);
						model.setValueAt(resultSet.getString("leaveDate"), i, 3);
						model.setValueAt(resultSet.getString("session"), i, 4);
						model.setValueAt(resultSet.getString("day"), i, 5);
						if(resultSet.getString("unplanned_leave").equals("1")){
							model.setValueAt(true, i, 6);
						}else{
							model.setValueAt(false, i, 6);
						}
						model.setValueAt(resultSet.getString("reason"), i, 7);
						i++;
					}
				}else if(cmbLveType.getSelectedItem().equals("Permission")){
					i = 0;
					model.addColumn("Permission_Date");
					model.addColumn("Day");
					model.addColumn("Hours");
					resultSet = statement.executeQuery("select emp_id, emp_name, perdate, day, lop_date, per_applied_for from applyleave where (emp_name = '"+selectedCmb+"' and pmo = '"+valueMap.get("empName")+"') and (leave_type = 'Permission' and curdate() <= perdate) and leave_status = 0 order by perdate ASC");
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
		} catch(ClassNotFoundException e1){
			JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
		} catch(SQLException e1){
			JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
		} finally{
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

	public void comboValueAll(){
		Connection connection = null;
		Statement statement = null;
		model.setColumnCount(0);
		model.setRowCount(0);
		model.addColumn("Select");
		model.addColumn("ID");
		model.addColumn("Name");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			ResultSet resultSet = null;
			if(valueMap.get("tlFilterComboValue").equals("Casual Leave")){
				i = 0;
				model.addColumn("CL_Date");
				model.addColumn("Session");
				model.addColumn("Day");
				model.addColumn("Lop");
				model.addColumn("Reason");
				resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Casual Leave' and curdate() <= fddate) and leave_status = 1 Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Casual Leave' and curdate() <= hddate) and leave_status = 1) as leavedd order by leavedd.leaveDate Asc");
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
			}else if(valueMap.get("tlFilterComboValue").equals("Sick Leave")){
				i = 0;
				model.addColumn("SL_Date");
				model.addColumn("Session");
				model.addColumn("Day");
				model.addColumn("Lop");
				model.addColumn("Reason");
				resultSet = statement.executeQuery("select * from (select emp_id, emp_name, fddate as leaveDate, session, day, lop_date, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Sick Leave' and curdate() <= fddate) and leave_status = 1 Union All select emp_id, emp_name, hddate as leaveDate, session, day, lop_date, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Sick Leave' and curdate() <= hddate) and leave_status = 1) as leavedd order by leavedd.leaveDate Asc");
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
			}else if(valueMap.get("tlFilterComboValue").equals("Permission")){
				i = 0;
				model.addColumn("Permission_Date");
				model.addColumn("Day");
				model.addColumn("Hours");
				resultSet = statement.executeQuery("select emp_id, emp_name, perdate, day, lop_date, per_applied_for from applyleave where pmo = '"+valueMap.get("empName")+"' and (leave_type = 'Permission' and curdate() <= perdate) and leave_status = 0 order by perdate ASC");
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
			lblCmbLveType.setVisible(false);
			cmbLveType.setVisible(false);
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

	@SuppressWarnings("serial")
	public static void loadTable(final String leaveTypeParam){
		final String leaveType = leaveTypeParam;
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
		table = new JTable(){
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component comp = super.prepareRenderer(renderer, row, column);
				if(table.getColumnCount() == 8 || table.getColumnCount() == 9){
					String columnName = table.getColumnName(6);
					boolean lop = (Boolean) getModel().getValueAt(row, 6);
					if(columnName.equals("Lop")){
						if (lop) {
							comp.setBackground(new Color(255, 179, 179));
						}else{
							comp.setBackground(row % 2 == 0 ? new Color(168, 198, 255) : new Color(209, 209, 209));
						}
					}else if(columnName.equals("Unplanned_Leave")){
						if (lop) {
							comp.setBackground(new Color(255, 194, 124));
						}else{
							comp.setBackground(row % 2 == 0 ? new Color(168, 198, 255) : new Color(209, 209, 209));
						}
					}
				}else if(table.getColumnCount() == 6 || table.getColumnCount() == 7){
					comp.setBackground(row % 2 == 0 ? new Color(168, 198, 255) : new Color(209, 209, 209));
				}
				return comp;
			}
		};
		((JComponent) table.getDefaultRenderer(Boolean.class)).setOpaque(true);
		scrollpane = new JScrollPane(table);
		scrollpane.setBounds(1, 260, 895, 183);
		pnlMain.add(scrollpane);
		table.requestFocus(true);
		scrollpane.setViewportView(table);
		model = new DefaultTableModel(){
			public boolean isCellEditable(int row, int column){
				return column == 0 ? true : false;
			}
			public Class<?> getColumnClass(int column){
				switch(column){
				case 0:
					return Boolean.class;
				case 6:
					if(table.getColumnCount() == 7){
						return String.class;
					}else{
						if(leaveType.equals("Casual Leave") || leaveType.equals("Sick Leave")){
							return Boolean.class;
						}else if(leaveType.equals("Permission")){
							return Boolean.class;
						}
					}
				default:
					return String.class;
				}
			}
		};
		DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)table.getDefaultRenderer(Object.class);
		renderer.setHorizontalAlignment(JLabel.CENTER);
		table.setModel(model);
		table.getTableHeader().setReorderingAllowed(false);
	}
}
