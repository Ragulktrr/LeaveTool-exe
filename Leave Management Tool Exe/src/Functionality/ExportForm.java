package Functionality;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class ExportForm implements ActionListener {
	public static JFrame exportFrame;
	public static JPanel pnlMain;
	public static JLabel lblDisplayInfo;
	public static JButton btnExportAll;
	public static JButton btnExportTlCount;
	public static JButton btnBack;
	public static JTable table;
	public static JScrollPane scrollpane;
	public static DefaultTableModel model;
	public static String COMMA_DELIMITER;
	public static String NEW_LINE_SEPARATOR;
	public static FileWriter fileWriter;
	public static SimpleDateFormat fileNameGenerate;
	List<String> tlCountList;
	Map<String, Object> tlCountMap;
	Map<String, Object> valueMap;

	public ExportForm(Map<String, Object> valueMap) {
		this.valueMap = valueMap;
		tlCountList = new ArrayList<String>();
		tlCountMap = new HashMap<String, Object>();
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
		COMMA_DELIMITER = ",";
		NEW_LINE_SEPARATOR = "\n";
		fileNameGenerate = new SimpleDateFormat("yyyyMMddHHmmss");
		btnExportAll = new JButton("Export Data");
		btnExportTlCount = new JButton("Export TL Count");
		btnBack = new JButton("Back");
		if(valueMap.get("filterBtnId").equals("btnLop")){
			exportFrame = new JFrame("Lop Detail");
			btnExportTlCount.setEnabled(false);
			lblDisplayInfo = new JLabel("LOP", SwingConstants.CENTER);
		}else{
			exportFrame = new JFrame("Export");
			btnExportTlCount.setEnabled(true);
			lblDisplayInfo = new JLabel("Export", SwingConstants.CENTER);
		}
		pnlMain = new JPanel();
		exportFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int confirmed = JOptionPane.showConfirmDialog(null, 
						"Are you sure you want to exit this program?", "Exit",
						JOptionPane.YES_NO_OPTION);
				if (confirmed == JOptionPane.YES_OPTION) {
					exportFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}else{
					exportFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				}
			}
		});
		exportFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		pnlMain.setLayout(null);

		lblDisplayInfo.setBounds(0, 30, 900, 50);
		lblDisplayInfo.setFont(new Font("calibri light", Font.BOLD, 20));

		btnExportAll.setBounds(250, 450, 125, 40);
		btnExportTlCount.setBounds(425, 450, 135, 40);
		btnBack.setBounds(610, 450, 135, 40);

		btnExportAll.setActionCommand("btnExportAll");
		btnExportAll.addActionListener(this);

		btnExportTlCount.setActionCommand("btnExportTlCount");
		btnExportTlCount.addActionListener(this);

		btnBack.setActionCommand("btnBack");
		btnBack.addActionListener(this);

		loadTable();

		exportFrame.setResizable(false);
		exportFrame.setVisible(true);
		exportFrame.setBounds(0, 0, 900, 700);
		exportFrame.setLocationRelativeTo(null);
		pnlMain.add(lblDisplayInfo);
		pnlMain.add(btnExportAll);
		pnlMain.add(btnExportTlCount);
		pnlMain.add(btnBack);
		exportFrame.add(pnlMain);
		loadGrid();
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		String actionCommand = evt.getActionCommand();
		int i = 0, j = 0;
		Integer count = 0;
		fileWriter = null;
		String tempVar;
		try {
			fileWriter = new FileWriter("C:/Users/KTRR/Desktop/CSV/"+fileNameGenerate.format(new Date())+".csv");
			if(actionCommand.equals("btnExportAll")){
				for(i = 0; i < table.getColumnCount(); i++){
					tempVar = table.getColumnName(i);
					if(tempVar.contains(" ")){
						tempVar = tempVar.replaceAll(" ", "_");
					}
					fileWriter.append(tempVar.toUpperCase());
					fileWriter.append(COMMA_DELIMITER);
				}
				fileWriter.append(NEW_LINE_SEPARATOR);
				for(i = 0; i < table.getRowCount(); i++){
					for(j = 0; j < table.getColumnCount(); j++){
						tempVar = table.getValueAt(i, j).toString();
						if(tempVar.contains(" ")){
							tempVar = tempVar.replaceAll(" ", "_");
						}
						fileWriter.append(tempVar);
						fileWriter.append(COMMA_DELIMITER);
					}
					fileWriter.append(NEW_LINE_SEPARATOR);
				}
				JOptionPane.showMessageDialog(null, "Export process Completed !!!");
			}else if(actionCommand.equals("btnExportTlCount")){
				for(i = 0; i < table.getRowCount(); i++){
					if(!table.getValueAt(i, 2).toString().equals("-")){
						tlCountList.add(table.getValueAt(i, 2).toString());
					}
				}
				fileWriter.append("TL");
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append("COUNT");
				fileWriter.append(NEW_LINE_SEPARATOR);
				for(i = 0; i < tlCountList.size(); i++){
					count = Collections.frequency(tlCountList, tlCountList.get(i));
					tlCountMap.put(tlCountList.get(i), count);
				}
				for(Map.Entry<String, Object> entry : tlCountMap.entrySet()){
					fileWriter.append(entry.getKey());
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(entry.getValue().toString());
					fileWriter.append(NEW_LINE_SEPARATOR);
				}
				JOptionPane.showMessageDialog(null, " TL Count export process Completed !!!");
			}else if(actionCommand.equals("btnBack")){
				exportFrame.setVisible(false);
				new SearchAndExportFunctionality(valueMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@SuppressWarnings("serial")
	public void loadTable(){
		scrollpane = new JScrollPane(table);
		scrollpane.setBounds(0, 150, 895, 250);
		pnlMain.add(scrollpane);
		table = new JTable(){
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component comp = super.prepareRenderer(renderer, row, column);
				comp.setBackground(row % 2 == 0 ? new Color(168, 198, 255) : new Color(209, 209, 209));
				return comp;
			}
		};
		((JComponent) table.getDefaultRenderer(Boolean.class)).setOpaque(true);
		scrollpane.setViewportView(table);
		model = new DefaultTableModel(){
			public boolean isCellEditable(int row, int column){
				return false;
			}
			public Class<?> getColumnClass(int column){
				return String.class;
			}
		};
		table.setModel(model);
		DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)table.getDefaultRenderer(Object.class);
		renderer.setHorizontalAlignment(JLabel.CENTER);
		table.getTableHeader().setReorderingAllowed(false);
	}

	public void loadGrid(){
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		model.setColumnCount(0);
		model.setRowCount(0);
		model.addColumn("ID");
		model.addColumn("Name");
		int i = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			resultSet = statement.executeQuery("select * from (select emp_id, emp_name, tl, fddate as leaveDate, session, leave_type, day, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and leave_status = 2 and fddate != '' and fddate != '-' and  lop_date = 1 Union All select emp_id, emp_name, tl, hddate as leaveDate, session, leave_type, day, reason from applyleave where pmo = '"+valueMap.get("empName")+"' and leave_status = 2 and hddate != '' and hddate != '-' and  lop_date = 1) as leavedd order by leavedd.leaveDate Asc");
			if(valueMap.get("filterBtnId").equals("btnLop")){
				model.addColumn("TL");
				model.addColumn("Date");
				model.addColumn("Session");
				model.addColumn("Leave Type");
				model.addColumn("Day");
				model.addColumn("Reason");
				while(resultSet.next()){
					model.addRow(new Object[0]);
					model.setValueAt(resultSet.getString("emp_id"), i, 0);
					model.setValueAt(resultSet.getString("emp_name"), i, 1);
					model.setValueAt(resultSet.getString("tl"), i, 2);
					model.setValueAt(resultSet.getString("leaveDate"), i, 3);
					model.setValueAt(resultSet.getString("session"), i, 4);
					model.setValueAt(resultSet.getString("leave_type"), i, 5);
					model.setValueAt(resultSet.getString("day"), i, 6);
					model.setValueAt(resultSet.getString("reason"), i, 7);
					i++;
				}
			}else if(valueMap.get("filterBtnId").equals("btnExport")){
				if(!valueMap.get("filterEmpId").equals("")){
					withId();
				}else if(!valueMap.get("filterTlName").equals("")){
					withTl();
				}else {
					if((boolean) valueMap.get("filterCheckBox")){
						checkedTrue();
					}else{
						checkedFalse();
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

	public void withId(){
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		model.setColumnCount(0);
		model.setRowCount(0);
		model.addColumn("ID");
		model.addColumn("Name");
		model.addColumn("TL");
		model.addColumn("Date");
		int i = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			if(valueMap.get("filterLeaveType").equals("Permission")){
				model.addColumn("Leave Type");
				model.addColumn("Day");
				model.addColumn("Hours");
				if((boolean) valueMap.get("filterCheckBox")){
					if(valueMap.get("filterFromDate") != null){
						if(valueMap.get("filterToDate") != null){
							resultSet = statement.executeQuery("select emp_id, emp_name, tl, perdate, leave_type, day, per_applied_for from applyleave where emp_id = '"+valueMap.get("filterEmpId")+"' and emp_name = '"+valueMap.get("filterEmpName")+"' and perdate >= '"+valueMap.get("filterFromDate")+"' and perdate <= '"+valueMap.get("filterToDate")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and perdate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 order by perdate Asc");
							while(resultSet.next()){
								model.addRow(new Object[0]);
								model.setValueAt(resultSet.getString("emp_id"), i, 0);
								model.setValueAt(resultSet.getString("emp_name"), i, 1);
								model.setValueAt(resultSet.getString("tl"), i, 2);
								model.setValueAt(resultSet.getString("perdate"), i, 3);
								model.setValueAt(resultSet.getString("leave_type"), i, 4);
								model.setValueAt(resultSet.getString("day"), i, 5);
								model.setValueAt(resultSet.getString("per_applied_for"), i, 6);
								i++;
							}
						}else{
							resultSet = statement.executeQuery("select emp_id, emp_name, tl, perdate, leave_type, day, per_applied_for from applyleave where emp_id = '"+valueMap.get("filterEmpId")+"' and emp_name = '"+valueMap.get("filterEmpName")+"' and perdate = '"+valueMap.get("filterFromDate")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and perdate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 order by perdate Asc");
							while(resultSet.next()){
								model.addRow(new Object[0]);
								model.setValueAt(resultSet.getString("emp_id"), i, 0);
								model.setValueAt(resultSet.getString("emp_name"), i, 1);
								model.setValueAt(resultSet.getString("tl"), i, 2);
								model.setValueAt(resultSet.getString("perdate"), i, 3);
								model.setValueAt(resultSet.getString("leave_type"), i, 4);
								model.setValueAt(resultSet.getString("day"), i, 5);
								model.setValueAt(resultSet.getString("per_applied_for"), i, 6);
								i++;
							}
						}
					}else{
						JOptionPane.showMessageDialog(null, "From Date is mandatory!!!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}else{
					resultSet = statement.executeQuery("select emp_id, emp_name, tl, perdate, leave_type, day, per_applied_for from applyleave where emp_id = '"+valueMap.get("filterEmpId")+"' and emp_name = '"+valueMap.get("filterEmpName")+"' and year = '"+valueMap.get("filterYear")+"' and month = '"+valueMap.get("filterMonth")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and perdate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 order by perdate Asc");
					while(resultSet.next()){
						model.addRow(new Object[0]);
						model.setValueAt(resultSet.getString("emp_id"), i, 0);
						model.setValueAt(resultSet.getString("emp_name"), i, 1);
						model.setValueAt(resultSet.getString("tl"), i, 2);
						model.setValueAt(resultSet.getString("perdate"), i, 3);
						model.setValueAt(resultSet.getString("leave_type"), i, 4);
						model.setValueAt(resultSet.getString("day"), i, 5);
						model.setValueAt(resultSet.getString("per_applied_for"), i, 6);
						i++;
					}
				}
			}else{
				model.addColumn("Session");
				model.addColumn("Leave Type");
				model.addColumn("Day");
				model.addColumn("Reason");
				if((boolean) valueMap.get("filterCheckBox")){
					if(valueMap.get("filterFromDate") != null){
						if(valueMap.get("filterToDate") != null){
							resultSet = statement.executeQuery("select * from (select emp_id, emp_name, tl, fddate as leaveDate, session, leave_type, day, reason from applyleave where emp_id = '"+valueMap.get("filterEmpId")+"' and emp_name = '"+valueMap.get("filterEmpName")+"'and fddate >= '"+valueMap.get("filterFromDate")+"' and fddate <= '"+valueMap.get("filterToDate")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and fddate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 Union All select emp_id, emp_name, tl, hddate as leaveDate, session, leave_type, day, reason from applyleave where emp_id = '"+valueMap.get("filterEmpId")+"' and emp_name = '"+valueMap.get("filterEmpName")+"'and hddate >= '"+valueMap.get("filterFromDate")+"' and hddate <= '"+valueMap.get("filterToDate")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and hddate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2) as leavedd order by leavedd.leaveDate Asc");
							while(resultSet.next()){
								model.addRow(new Object[0]);
								model.setValueAt(resultSet.getString("emp_id"), i, 0);
								model.setValueAt(resultSet.getString("emp_name"), i, 1);
								model.setValueAt(resultSet.getString("tl"), i, 2);
								model.setValueAt(resultSet.getString("leaveDate"), i, 3);
								model.setValueAt(resultSet.getString("session"), i, 4);
								model.setValueAt(resultSet.getString("leave_type"), i, 5);
								model.setValueAt(resultSet.getString("day"), i, 6);
								model.setValueAt(resultSet.getString("reason"), i, 7);
								i++;
							}
						}else{
							resultSet = statement.executeQuery("select * from (select emp_id, emp_name, tl, fddate as leaveDate, session, leave_type, day, reason from applyleave where emp_id = '"+valueMap.get("filterEmpId")+"' and emp_name = '"+valueMap.get("filterEmpName")+"' and fddate = '"+valueMap.get("filterFromDate")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and fddate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 Union All select emp_id, emp_name, tl, hddate as leaveDate, session, leave_type, day, reason from applyleave where emp_id = '"+valueMap.get("filterEmpId")+"' and emp_name = '"+valueMap.get("filterEmpName")+"' and hddate = '"+valueMap.get("filterFromDate")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and hddate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 and leave_status = 2) as leavedd order by leavedd.leaveDate Asc");
							while(resultSet.next()){
								model.addRow(new Object[0]);
								model.setValueAt(resultSet.getString("emp_id"), i, 0);
								model.setValueAt(resultSet.getString("emp_name"), i, 1);
								model.setValueAt(resultSet.getString("tl"), i, 2);
								model.setValueAt(resultSet.getString("leaveDate"), i, 3);
								model.setValueAt(resultSet.getString("session"), i, 4);
								model.setValueAt(resultSet.getString("leave_type"), i, 5);
								model.setValueAt(resultSet.getString("day"), i, 6);
								model.setValueAt(resultSet.getString("reason"), i, 7);
								i++;
							}
						}
					}else{
						JOptionPane.showMessageDialog(null, "From Date is mandatory!!!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}else{
					resultSet = statement.executeQuery("select * from (select emp_id, emp_name, tl, fddate as leaveDate, session, year, month, leave_type, day, reason from applyleave where emp_id = '"+valueMap.get("filterEmpId")+"' and emp_name = '"+valueMap.get("filterEmpName")+"' and year = '"+valueMap.get("filterYear")+"' and month = '"+valueMap.get("filterMonth")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and fddate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 Union All select emp_id, emp_name, tl, hddate as leaveDate, session, year, month, leave_type, day, reason from applyleave where emp_id = '"+valueMap.get("filterEmpId")+"' and emp_name = '"+valueMap.get("filterEmpName")+"' and year = '"+valueMap.get("filterYear")+"' and month = '"+valueMap.get("filterMonth")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and hddate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2) as leavedd order by leavedd.leaveDate Asc");
					while(resultSet.next()){
						model.addRow(new Object[0]);
						model.setValueAt(resultSet.getString("emp_id"), i, 0);
						model.setValueAt(resultSet.getString("emp_name"), i, 1);
						model.setValueAt(resultSet.getString("tl"), i, 2);
						model.setValueAt(resultSet.getString("leaveDate"), i, 3);
						model.setValueAt(resultSet.getString("session"), i, 4);
						model.setValueAt(resultSet.getString("leave_type"), i, 5);
						model.setValueAt(resultSet.getString("day"), i, 6);
						model.setValueAt(resultSet.getString("reason"), i, 7);
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

	public void withTl(){
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		model.setColumnCount(0);
		model.setRowCount(0);
		model.addColumn("ID");
		model.addColumn("Name");
		model.addColumn("TL");
		model.addColumn("Date");
		int i = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			if(valueMap.get("filterLeaveType").equals("Permission")){
				model.addColumn("Leave Type");
				model.addColumn("Day");
				model.addColumn("Hours");
				if((boolean) valueMap.get("filterCheckBox")){
					if(valueMap.get("filterFromDate") != null){
						if(valueMap.get("filterToDate") != null){
							resultSet = statement.executeQuery("select emp_id, emp_name, tl, perdate, leave_type, day, per_applied_for from applyleave where tl = '"+valueMap.get("filterTlName")+"' and perdate >= '"+valueMap.get("filterFromDate")+"' and perdate <= '"+valueMap.get("filterToDate")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and perdate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 order by perdate Asc");
							while(resultSet.next()){
								model.addRow(new Object[0]);
								model.setValueAt(resultSet.getString("emp_id"), i, 0);
								model.setValueAt(resultSet.getString("emp_name"), i, 1);
								model.setValueAt(resultSet.getString("tl"), i, 2);
								model.setValueAt(resultSet.getString("perdate"), i, 3);
								model.setValueAt(resultSet.getString("leave_type"), i, 4);
								model.setValueAt(resultSet.getString("day"), i, 5);
								model.setValueAt(resultSet.getString("per_applied_for"), i, 6);
								i++;
							}
						}else{
							resultSet = statement.executeQuery("select emp_id, emp_name, tl, perdate, leave_type, day, per_applied_for from applyleave where tl = '"+valueMap.get("filterTlName")+"' and perdate >= '"+valueMap.get("filterFromDate")+"' and perdate <= '"+valueMap.get("filterToDate")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and perdate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 order by perdate Asc");
							while(resultSet.next()){
								model.addRow(new Object[0]);
								model.setValueAt(resultSet.getString("emp_id"), i, 0);
								model.setValueAt(resultSet.getString("emp_name"), i, 1);
								model.setValueAt(resultSet.getString("tl"), i, 2);
								model.setValueAt(resultSet.getString("perdate"), i, 3);
								model.setValueAt(resultSet.getString("leave_type"), i, 4);
								model.setValueAt(resultSet.getString("day"), i, 5);
								model.setValueAt(resultSet.getString("per_applied_for"), i, 6);
								i++;
							}
						}
					}else{
						JOptionPane.showMessageDialog(null, "From Date is mandatory!!!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}else{
					resultSet = statement.executeQuery("select emp_id, emp_name, tl, perdate, leave_type, day, per_applied_for from applyleave where tl = '"+valueMap.get("filterTlName")+"' and year = '"+valueMap.get("filterYear")+"' and month = '"+valueMap.get("filterMonth")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and perdate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 order by perdate Asc");
					while(resultSet.next()){
						model.addRow(new Object[0]);
						model.setValueAt(resultSet.getString("emp_id"), i, 0);
						model.setValueAt(resultSet.getString("emp_name"), i, 1);
						model.setValueAt(resultSet.getString("tl"), i, 2);
						model.setValueAt(resultSet.getString("perdate"), i, 3);
						model.setValueAt(resultSet.getString("leave_type"), i, 4);
						model.setValueAt(resultSet.getString("day"), i, 5);
						model.setValueAt(resultSet.getString("per_applied_for"), i, 6);
						i++;
					}
				}
			}else{
				model.addColumn("Session");
				model.addColumn("Leave Type");
				model.addColumn("Day");
				model.addColumn("Reason");
				if((boolean) valueMap.get("filterCheckBox")){
					if(valueMap.get("filterFromDate") != null){
						if(valueMap.get("filterToDate") != null){
							resultSet = statement.executeQuery("select * from (select emp_id, emp_name, tl, fddate as leaveDate, session, leave_type, day, reason from applyleave where tl = '"+valueMap.get("filterTlName")+"' and fddate >= '"+valueMap.get("filterFromDate")+"' and fddate <= '"+valueMap.get("filterToDate")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and fddate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 Union All select emp_id, emp_name, tl, hddate as leaveDate, session, leave_type, day, reason from applyleave where tl = '"+valueMap.get("filterTlName")+"' and hddate >= '"+valueMap.get("filterFromDate")+"' and hddate <= '"+valueMap.get("filterToDate")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and hddate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2) as leavedd order by leavedd.leaveDate Asc");
							while(resultSet.next()){
								model.addRow(new Object[0]);
								model.setValueAt(resultSet.getString("emp_id"), i, 0);
								model.setValueAt(resultSet.getString("emp_name"), i, 1);
								model.setValueAt(resultSet.getString("tl"), i, 2);
								model.setValueAt(resultSet.getString("leaveDate"), i, 3);
								model.setValueAt(resultSet.getString("session"), i, 4);
								model.setValueAt(resultSet.getString("leave_type"), i, 5);
								model.setValueAt(resultSet.getString("day"), i, 6);
								model.setValueAt(resultSet.getString("reason"), i, 7);
								i++;
							}
						}else{
							resultSet = statement.executeQuery("select * from (select emp_id, emp_name, tl, fddate as leaveDate, session, leave_type, day, reason from applyleave where tl = '"+valueMap.get("filterTlName")+"' and fddate >= '"+valueMap.get("filterFromDate")+"' and fddate <= '"+valueMap.get("filterToDate")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and fddate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 Union All select emp_id, emp_name, tl, hddate as leaveDate, session, leave_type, day, reason from applyleave where tl = '"+valueMap.get("filterTlName")+"' and hddate >= '"+valueMap.get("filterFromDate")+"' and hddate <= '"+valueMap.get("filterToDate")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and hddate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2) as leavedd order by leavedd.leaveDate Asc");
							while(resultSet.next()){
								model.addRow(new Object[0]);
								model.setValueAt(resultSet.getString("emp_id"), i, 0);
								model.setValueAt(resultSet.getString("emp_name"), i, 1);
								model.setValueAt(resultSet.getString("tl"), i, 2);
								model.setValueAt(resultSet.getString("leaveDate"), i, 3);
								model.setValueAt(resultSet.getString("session"), i, 4);
								model.setValueAt(resultSet.getString("leave_type"), i, 5);
								model.setValueAt(resultSet.getString("day"), i, 6);
								model.setValueAt(resultSet.getString("reason"), i, 7);
								i++;
							}
						}
					}else{
						JOptionPane.showMessageDialog(null, "From Date is mandatory!!!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}else{
					resultSet = statement.executeQuery("select * from (select emp_id, emp_name, tl, fddate as leaveDate, session, year, month, leave_type, day, reason from applyleave where tl = '"+valueMap.get("filterTlName")+"' and year = '"+valueMap.get("filterYear")+"' and month = '"+valueMap.get("filterMonth")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and fddate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 Union All select emp_id, emp_name, tl, hddate as leaveDate, session, year, month, leave_type, day, reason from applyleave where tl = '"+valueMap.get("filterTlName")+"' and year = '"+valueMap.get("filterYear")+"' and month = '"+valueMap.get("filterMonth")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and hddate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2) as leavedd order by leavedd.leaveDate Asc");
					while(resultSet.next()){
						model.addRow(new Object[0]);
						model.setValueAt(resultSet.getString("emp_id"), i, 0);
						model.setValueAt(resultSet.getString("emp_name"), i, 1);
						model.setValueAt(resultSet.getString("tl"), i, 2);
						model.setValueAt(resultSet.getString("leaveDate"), i, 3);
						model.setValueAt(resultSet.getString("session"), i, 4);
						model.setValueAt(resultSet.getString("leave_type"), i, 5);
						model.setValueAt(resultSet.getString("day"), i, 6);
						model.setValueAt(resultSet.getString("reason"), i, 7);
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

	public void checkedTrue(){
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		model.setColumnCount(0);
		model.setRowCount(0);
		model.addColumn("ID");
		model.addColumn("Name");
		model.addColumn("TL");
		model.addColumn("Date");
		int i = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			if(valueMap.get("filterLeaveType").equals("Permission")){
				model.addColumn("Leave Type");
				model.addColumn("Day");
				model.addColumn("Hours");
				if(valueMap.get("filterFromDate") != null){
					if(valueMap.get("filterToDate") != null){
						resultSet = statement.executeQuery("select emp_id, emp_name, tl, perdate, leave_type, day, per_applied_for from applyleave where perdate >= '"+valueMap.get("filterFromDate")+"' and perdate <= '"+valueMap.get("filterToDate")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and perdate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 order by perdate Asc");
						while(resultSet.next()){
							model.addRow(new Object[0]);
							model.setValueAt(resultSet.getString("emp_id"), i, 0);
							model.setValueAt(resultSet.getString("emp_name"), i, 1);
							model.setValueAt(resultSet.getString("tl"), i, 2);
							model.setValueAt(resultSet.getString("perdate"), i, 3);
							model.setValueAt(resultSet.getString("leave_type"), i, 4);
							model.setValueAt(resultSet.getString("day"), i, 5);
							model.setValueAt(resultSet.getString("per_applied_for"), i, 6);
							i++;
						}
					}else{
						resultSet = statement.executeQuery("select emp_id, emp_name, tl, perdate, leave_type, day, per_applied_for from applyleave where perdate = '"+valueMap.get("filterFromDate")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and perdate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 order by perdate Asc");
						while(resultSet.next()){
							model.addRow(new Object[0]);
							model.setValueAt(resultSet.getString("emp_id"), i, 0);
							model.setValueAt(resultSet.getString("emp_name"), i, 1);
							model.setValueAt(resultSet.getString("tl"), i, 2);
							model.setValueAt(resultSet.getString("perdate"), i, 3);
							model.setValueAt(resultSet.getString("leave_type"), i, 4);
							model.setValueAt(resultSet.getString("day"), i, 5);
							model.setValueAt(resultSet.getString("per_applied_for"), i, 6);
							i++;
						}
					}
				}else{
					JOptionPane.showMessageDialog(null, "From Date is mandatory!!!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}else{
				model.addColumn("Session");
				model.addColumn("Leave Type");
				model.addColumn("Day");
				model.addColumn("Reason");
				if(valueMap.get("filterFromDate") != null){
					if(valueMap.get("filterToDate") != null){
						resultSet = statement.executeQuery("select * from (select emp_id, emp_name, tl, fddate as leaveDate, session, leave_type, day, reason from applyleave where fddate >= '"+valueMap.get("filterFromDate")+"' and fddate <= '"+valueMap.get("filterToDate")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and fddate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 Union All select emp_id, emp_name, tl, hddate as leaveDate, session, leave_type, day, reason from applyleave where hddate >= '"+valueMap.get("filterFromDate")+"' and hddate <= '"+valueMap.get("filterToDate")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and hddate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2) as leavedd order by leavedd.leaveDate Asc");
						while(resultSet.next()){
							model.addRow(new Object[0]);
							model.setValueAt(resultSet.getString("emp_id"), i, 0);
							model.setValueAt(resultSet.getString("emp_name"), i, 1);
							model.setValueAt(resultSet.getString("tl"), i, 2);
							model.setValueAt(resultSet.getString("leaveDate"), i, 3);
							model.setValueAt(resultSet.getString("session"), i, 4);
							model.setValueAt(resultSet.getString("leave_type"), i, 5);
							model.setValueAt(resultSet.getString("day"), i, 6);
							model.setValueAt(resultSet.getString("reason"), i, 7);
							i++;
						}
					}else{
						resultSet = statement.executeQuery("select * from (select emp_id, emp_name, tl, fddate as leaveDate, session, leave_type, day, reason from applyleave where fddate = '"+valueMap.get("filterFromDate")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and fddate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 Union All select emp_id, emp_name, tl, hddate as leaveDate, session, leave_type, day, reason from applyleave where hddate = '"+valueMap.get("filterFromDate")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and hddate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2) as leavedd order by leavedd.leaveDate Asc");
						while(resultSet.next()){
							model.addRow(new Object[0]);
							model.setValueAt(resultSet.getString("emp_id"), i, 0);
							model.setValueAt(resultSet.getString("emp_name"), i, 1);
							model.setValueAt(resultSet.getString("tl"), i, 2);
							model.setValueAt(resultSet.getString("leaveDate"), i, 3);
							model.setValueAt(resultSet.getString("session"), i, 4);
							model.setValueAt(resultSet.getString("leave_type"), i, 5);
							model.setValueAt(resultSet.getString("day"), i, 6);
							model.setValueAt(resultSet.getString("reason"), i, 7);
							i++;
						}
					}
				}else{
					JOptionPane.showMessageDialog(null, "From Date is mandatory!!!", "Error", JOptionPane.ERROR_MESSAGE);
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

	public void checkedFalse(){
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		model.setColumnCount(0);
		model.setRowCount(0);
		model.addColumn("ID");
		model.addColumn("Name");
		model.addColumn("TL");
		model.addColumn("Date");
		int i = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			if(valueMap.get("filterLeaveType").equals("Permission")){
				model.addColumn("Leave Type");
				model.addColumn("Day");
				model.addColumn("Hours");
				resultSet = statement.executeQuery("select emp_id, emp_name, tl, perdate, leave_type, day, per_applied_for from applyleave where year = '"+valueMap.get("filterYear")+"' and month = '"+valueMap.get("filterMonth")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and perdate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 order by perdate Asc");
				while(resultSet.next()){
					model.addRow(new Object[0]);
					model.setValueAt(resultSet.getString("emp_id"), i, 0);
					model.setValueAt(resultSet.getString("emp_name"), i, 1);
					model.setValueAt(resultSet.getString("tl"), i, 2);
					model.setValueAt(resultSet.getString("perdate"), i, 3);
					model.setValueAt(resultSet.getString("leave_type"), i, 4);
					model.setValueAt(resultSet.getString("day"), i, 5);
					model.setValueAt(resultSet.getString("per_applied_for"), i, 6);
					i++;
				}
			}else{
				model.addColumn("Session");
				model.addColumn("Leave Type");
				model.addColumn("Day");
				model.addColumn("Reason");
				resultSet = statement.executeQuery("select * from (select emp_id, emp_name, tl, fddate as leaveDate, session, year, month, leave_type, day, reason from applyleave where year = '"+valueMap.get("filterYear")+"' and month = '"+valueMap.get("filterMonth")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and fddate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2 Union All select emp_id, emp_name, tl, hddate as leaveDate, session, year, month, leave_type, day, reason from applyleave where year = '"+valueMap.get("filterYear")+"' and month = '"+valueMap.get("filterMonth")+"' and leave_type = '"+valueMap.get("filterLeaveType")+"' and hddate != '-' and pmo = '"+valueMap.get("empName")+"' and leave_status = 2) as leavedd order by leavedd.leaveDate Asc");
				while(resultSet.next()){
					model.addRow(new Object[0]);
					model.setValueAt(resultSet.getString("emp_id"), i, 0);
					model.setValueAt(resultSet.getString("emp_name"), i, 1);
					model.setValueAt(resultSet.getString("tl"), i, 2);
					model.setValueAt(resultSet.getString("leaveDate"), i, 3);
					model.setValueAt(resultSet.getString("session"), i, 4);
					model.setValueAt(resultSet.getString("leave_type"), i, 5);
					model.setValueAt(resultSet.getString("day"), i, 6);
					model.setValueAt(resultSet.getString("reason"), i, 7);
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
