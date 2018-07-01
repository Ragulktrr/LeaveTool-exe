package Functionality;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import Design.SearchAndExportDesign;

public class SearchAndExportFunctionality extends SearchAndExportDesign implements ActionListener, KeyListener, FocusListener {
	Map<String, Object> valueMap;
	boolean focusOutFlag;
	@SuppressWarnings("unchecked")
	public SearchAndExportFunctionality(Map<String, Object> valueMap){
		super(valueMap);
		focusOutFlag = false;
		this.valueMap = valueMap;
		txtEmpId.requestFocus(true);

		txtEmpId.setActionCommand("txtEmpId");
		txtEmpName.setActionCommand("txtEmpName");
		txtTl.setActionCommand("txtTl");

		txtEmpId.addKeyListener(this);
		txtEmpName.addKeyListener(this);
		txtTl.addKeyListener(this);

		btnLop.setActionCommand("btnLop");
		btnLop.addActionListener(this);

		btnExport.setActionCommand("btnExport");
		btnExport.addActionListener(this);

		btnBack.setActionCommand("btnBack");
		btnBack.addActionListener(this);

		txtEmpId.addFocusListener(this);
		txtEmpName.addFocusListener(this);
		txtTl.addFocusListener(this);

		cmbLeaveType.addItem("Casual Leave");
		cmbLeaveType.addItem("Permission");
		cmbLeaveType.addItem("Sick Leave");
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			resultSet = statement.executeQuery("SELECT DISTINCT month FROM applyleave ORDER BY FIELD(month,'January','February','March','April','May','June','July','Augest','September','October','November','December')");
			while(resultSet.next()){
				cmbMonth.addItem(resultSet.getString("month"));
			}
			resultSet = statement.executeQuery("SELECT DISTINCT year FROM applyleave ORDER BY year ASC");
			while(resultSet.next()){
				cmbYear.addItem(resultSet.getString("year"));
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
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent event) {
				int selectedRowIndex = table.getSelectedRow();
				if(selectedRowIndex >= 0){
					if(table.getRowCount() > 0){
						if(table.getColumnCount() == 3){
							txtEmpId.setText(model.getValueAt(selectedRowIndex, 0).toString());
							txtEmpName.setText(model.getValueAt(selectedRowIndex, 1).toString());
							txtTl.setText(model.getValueAt(selectedRowIndex, 2).toString());
						} else if(table.getColumnCount() == 1){
							txtTl.setText(model.getValueAt(selectedRowIndex, 0).toString());
						}
					}
				}
			}
		});

		yearMonth.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == 1){
					cmbYear.setEnabled(false);
					cmbMonth.setEnabled(false);
					fromDate.setEnabled(true);
					toDate.setEnabled(true);
				} else {
					cmbYear.setEnabled(true);
					cmbMonth.setEnabled(true);
					fromDate.setEnabled(false);
					toDate.setEnabled(false);
				}
			}    
		});
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		String actionCommand = evt.getActionCommand();
		SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd");
		valueMap.put("filterEmpId", txtEmpId.getText());
		valueMap.put("filterEmpName", txtEmpName.getText());
		valueMap.put("filterTlName", txtTl.getText());
		valueMap.put("filterCheckBox", yearMonth.isSelected());
		if(yearMonth.isSelected()) {
			valueMap.put("filterYear", "");
			valueMap.put("filterMonth", "");
		}else {
			valueMap.put("filterYear", cmbYear.getSelectedItem().toString());
			valueMap.put("filterMonth", cmbMonth.getSelectedItem().toString());
		}
		if(fromDate.getDate() == null){
			valueMap.put("filterFromDate", null);
			valueMap.put("filterToDate", null);
		}else{
			valueMap.put("filterFromDate", sqlFormat.format(fromDate.getDate()));
			if(toDate.getDate() == null){
				valueMap.put("filterToDate", null);
			}else{
				valueMap.put("filterToDate", sqlFormat.format(toDate.getDate()));
			}
		}
		valueMap.put("filterLeaveType", cmbLeaveType.getSelectedItem());
		if(actionCommand.equals("btnLop")){
			valueMap.put("filterBtnId", "btnLop");
			filterFrame.setVisible(false);
			new ExportForm(valueMap);
		} else if(actionCommand.equals("btnExport")){
			valueMap.put("filterBtnId", "btnExport");
			if((boolean) valueMap.get("filterCheckBox")){
				if(valueMap.get("filterFromDate") != null){
					filterFrame.setVisible(false);
					new ExportForm(valueMap);
				}else{
					JOptionPane.showMessageDialog(null, "From Date is mandatory!!!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}else{
				filterFrame.setVisible(false);
				new ExportForm(valueMap);
			}
		} else if(actionCommand.equals("btnBack")){
			filterFrame.setVisible(false);
			new PmoApprovalFunctionality(valueMap);
		}
	}
	public void focusLost(FocusEvent evt) {
		String empId = txtEmpId.getText();
		String empName = txtEmpName.getText();
		String focusOutElement = String.valueOf(evt.getComponent()).split("command=")[1].split(",")[0];
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			if(focusOutElement.equals("txtEmpId")){
				if(!empId.equals("")){
					resultSet = statement.executeQuery("select * from infoviewLogin where emp_Id = '"+empId+"'");
					if(resultSet.next()){
						txtEmpName.setText(resultSet.getString("emp_Name"));
						txtTl.setText(resultSet.getString("TL"));
					}else{
						JOptionPane.showMessageDialog(null, "No Such Data found!!!", "Error", JOptionPane.ERROR_MESSAGE);
						txtEmpId.requestFocus(true);
					}
				}else{
					txtEmpName.setText("");
				}
			}else if(focusOutElement.equals("txtEmpName")){
				if(!empName.equals("")){
					resultSet = statement.executeQuery("select * from infoviewLogin where emp_Name = '"+empName+"'");
					if(resultSet.next()){
						txtEmpId.setText(resultSet.getString("emp_Id"));
						txtTl.setText(resultSet.getString("TL"));
					}else{
						JOptionPane.showMessageDialog(null, "No Such Data found!!!", "Error", JOptionPane.ERROR_MESSAGE);
						txtEmpName.requestFocus(true);
					}
				}else{
					txtEmpId.setText("");
				}
			}else if(focusOutElement.equals("txtTl")){
				if(focusOutFlag){
					String tlName = txtTl.getText();
					resultSet = statement.executeQuery("select distinct TL from infoviewlogin WHERE TL = '"+tlName+"' and TL != '-'");
					if(!resultSet.next()){
						JOptionPane.showMessageDialog(null, "No Such Data found!!!", "Error", JOptionPane.ERROR_MESSAGE);
						txtTl.requestFocus(true);
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

	public void keyTyped(KeyEvent evt) {
		String currentElement = String.valueOf(evt.getComponent()).split("command=")[1].split(",")[0];
		char numberField = evt.getKeyChar();
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String empId = "", empName = "", tlName = "";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			if(currentElement.equals("txtEmpId")){
				if(!(Character.isDigit(numberField) || numberField == KeyEvent.VK_BACK_SPACE || numberField == KeyEvent.VK_DELETE )){
					evt.consume();
				} else {
					int i = 0;
					empId = txtEmpId.getText();
					empName = txtEmpName.getText();
					tlName = txtTl.getText();
					model.setColumnCount(0);
					model.setRowCount(0);
					model.addColumn("ID");
					model.addColumn("Name");
					model.addColumn("TL");
					resultSet = statement.executeQuery("select emp_Id, emp_Name, TL from infoviewlogin WHERE emp_Id LIKE '"+empId+"%' and pmo != '-'");
					while(resultSet.next()){
						model.addRow(new Object[0]);
						model.setValueAt(resultSet.getString("emp_Id"), i, 0);
						model.setValueAt(resultSet.getString("emp_Name"), i, 1);
						model.setValueAt(resultSet.getString("TL"), i, 2);
						i++;
					}
				}
				if(numberField == KeyEvent.VK_ENTER){
					txtEmpName.requestFocus(true);
				}
			} else if(currentElement.equals("txtEmpName")){
				empName = txtEmpName.getText();
				int i = 0;
				model.setColumnCount(0);
				model.setRowCount(0);
				model.addColumn("ID");
				model.addColumn("Name");
				model.addColumn("TL");
				resultSet = statement.executeQuery("select emp_Id, emp_Name, TL from infoviewlogin WHERE emp_Name LIKE '"+empName+"%' and pmo != '-'");
				while(resultSet.next()){
					model.addRow(new Object[0]);
					model.setValueAt(resultSet.getString("emp_Id"), i, 0);
					model.setValueAt(resultSet.getString("emp_Name"), i, 1);
					model.setValueAt(resultSet.getString("TL"), i, 2);
					i++;
				}
				if(numberField == KeyEvent.VK_ENTER){
					txtTl.requestFocus(true);
				}
			} else if(currentElement.equals("txtTl")){
				tlName = txtTl.getText();
				if(numberField != KeyEvent.VK_ENTER){
					txtEmpName.setText("");
					txtEmpId.setText("");
					int i = 0;
					model.setColumnCount(0);
					model.setRowCount(0);
					model.addColumn("TL Name");
					resultSet = statement.executeQuery("select distinct TL from infoviewlogin WHERE TL LIKE '"+tlName+"%' and TL != '-' and pmo != '-'");
					while(resultSet.next()){
						model.addRow(new Object[0]);
						model.setValueAt(resultSet.getString("TL"), i, 0);
						i++;
					}
				}else if(!tlName.equals("-") && !tlName.equals("")){
					focusOutFlag = false;
					resultSet = statement.executeQuery("select distinct TL from infoviewlogin WHERE TL = '"+tlName+"' and TL != '-' and pmo != '-'");
					if(resultSet.next()){
						yearMonth.requestFocus(true);
					}else{
						JOptionPane.showMessageDialog(null, "No Such Data found!!!", "Error", JOptionPane.ERROR_MESSAGE);
						focusOutFlag = true;
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
	public void keyPressed(KeyEvent evt) {}
	public void keyReleased(KeyEvent evt) {}

	@Override
	public void focusGained(FocusEvent arg0) {}
}
