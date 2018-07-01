package Functionality;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class TeamNameDialog implements ActionListener {
	public static JFrame dialogueFrame;
	JPanel pnlMain;
	JLabel lblTeamList;
	@SuppressWarnings("rawtypes")
	JComboBox teamNames;
	JButton btnOk;
	JButton btnBack;
	public int selection;
	public static Map<String, Object> valueMap;

	@SuppressWarnings({ "rawtypes", "static-access", "unchecked" })
	public TeamNameDialog(Map<String, Object> valueMap){
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			Class.forName("com.mysql.jdbc.Driver");
			this.valueMap = valueMap;
			dialogueFrame = new JFrame("Choose Name");
			pnlMain = new JPanel();
			pnlMain.setLayout(null);
			lblTeamList = new JLabel("Team Name-List : ", SwingConstants.CENTER);

			teamNames = new JComboBox();

			btnOk = new JButton("OK");
			btnBack = new JButton("Back");

			dialogueFrame.setResizable(false);
			dialogueFrame.setVisible(true);

			lblTeamList.setBounds(5, 15, 150, 30);

			teamNames.setBounds(50, 55, 150, 40);

			btnOk.setBounds(50, 110, 80, 30);
			btnBack.setBounds(170, 110, 80, 30);

			dialogueFrame.setBounds(0, 0, 300, 200);
			dialogueFrame.setLocationRelativeTo(null);

			btnOk.setActionCommand("btnOk");
			btnOk.addActionListener(this);
			btnBack.setActionCommand("btnBack");
			btnBack.addActionListener(this);

			dialogueFrame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					if(valueMap.get("back").equals("1")){
						new TlFormFunctionality(valueMap);
					}else if(valueMap.get("back").equals("2")){
						new PmoApprovalFunctionality(valueMap);
					}
				}
			});

			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			if(valueMap.get("back").equals("1")){
				resultSet = statement.executeQuery("select emp_name from infoviewLogin where tl = '"+valueMap.get("empName")+"' order by emp_name ASC");
				teamNames.addItem("--Choose--");
				while(resultSet.next()){
					teamNames.addItem(resultSet.getString("emp_name"));
				}
			}else if(valueMap.get("back").equals("2")){
				resultSet = statement.executeQuery("select emp_name from infoviewLogin where pmo = '"+valueMap.get("empName")+"' order by emp_name ASC");
				teamNames.addItem("--Choose--");
				while(resultSet.next()){
					teamNames.addItem(resultSet.getString("emp_name"));
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
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
		pnlMain.add(lblTeamList);
		pnlMain.add(teamNames);
		pnlMain.add(btnOk);
		pnlMain.add(btnBack);
		dialogueFrame.add(pnlMain);
	}

	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		String selectedCmb = String.valueOf(teamNames.getSelectedItem());
		Connection connection = null;
		Statement statement = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
			statement = (Statement) connection.createStatement();
			ResultSet resultSet = null;
			if(actionCommand.equals("btnOk")){
				if(selectedCmb.equals("--Choose--")){
					JOptionPane.showMessageDialog(null, "Pick Any Values!!!", "Error", JOptionPane.ERROR_MESSAGE);
				}else{
					resultSet = statement.executeQuery("select * from infoviewLogin where emp_Name = '"+teamNames.getSelectedItem()+"'");
					while(resultSet.next()){
						valueMap.put("emp_IdDB", resultSet.getInt("emp_Id"));
						valueMap.put("empName", resultSet.getString("emp_Name"));
						valueMap.put("password", resultSet.getString("pwd"));
						valueMap.put("level", resultSet.getInt("work_level"));
						valueMap.put("TL", resultSet.getString("TL"));
						valueMap.put("PMO", resultSet.getString("PMO"));
						valueMap.put("clleft", (Double)resultSet.getDouble("cl_left"));
						valueMap.put("slleft", (Double)resultSet.getDouble("sl_left"));
						valueMap.put("perleft", (Double)resultSet.getDouble("per_left"));
						valueMap.put("LOP", (Double)resultSet.getDouble("LOP"));
					}
					dialogueFrame.setVisible(false);
					new EmpLeaveApply(valueMap);
				}
			}else if(actionCommand.equals("btnBack")){
				if(valueMap.get("back").equals("1")){
					dialogueFrame.setVisible(false);
					new TlFormFunctionality(valueMap);
				}else if(valueMap.get("back").equals("2")){
					dialogueFrame.setVisible(false);
					new PmoApprovalFunctionality(valueMap);
				}
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
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
				} catch (SQLException e1) {
					e1.printStackTrace();
				} 
		}
	}
}
