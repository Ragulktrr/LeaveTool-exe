package Functionality;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import Design.LoginDesign;

public class LoginFunctionality extends LoginDesign implements ActionListener, FocusListener, KeyListener {
	public static LoginFunctionality loginObj;
	Map<String, Object> valueMap;
	public LoginFunctionality(){
		super();
		loginFrame.setVisible(true);
		valueMap = new HashMap<String, Object>();
		valueMap.put("emp_IdDB", (Integer)0);
		valueMap.put("empName", "");
		valueMap.put("password", "");
		valueMap.put("level", (Integer)0);
		valueMap.put("TL", "");
		valueMap.put("PMO", "");
		valueMap.put("clleft", (Double)0.0);
		valueMap.put("slleft", (Double)0.0);
		valueMap.put("perleft", (Double)0.0);
		valueMap.put("LOP", (Double)0.0);

		txtEmpId.requestFocus(true);
		btnLogin.setActionCommand("SignIn");
		btnLogin.addActionListener(this);

		btnExit.setActionCommand("Exit");
		btnExit.addActionListener(this);

		txtEmpId.setActionCommand("txtempid");
		txtPwd.setActionCommand("txtpwd");

		txtEmpId.addFocusListener(this);
		txtPwd.addFocusListener(this);

		txtEmpId.addKeyListener(this);
		txtPwd.addKeyListener(this);

		btnLogin.setActionCommand("login");
		loginFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int confirmed = JOptionPane.showConfirmDialog(null, 
						"Are you sure you want to exit this program?", "Exit",
						JOptionPane.YES_NO_OPTION);

				if (confirmed == JOptionPane.YES_OPTION) {
					loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}else{
					loginFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				}
			}
		});
		pnlMain.updateUI();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent evt) {
		Connection connection = null;
		Statement statement = null;
		try {
			String actionCommand = evt.getActionCommand();
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveapprovallogin","root","root");
			statement  = (Statement) connection.createStatement();
			if(actionCommand.equals("login")){
				if(!(txtEmpId.getText().equalsIgnoreCase("") || txtPwd.getText().equalsIgnoreCase(""))){    
					if(valueMap.get("emp_IdDB").equals(Integer.parseInt(txtEmpId.getText()))){
						if(valueMap.get("password").equals(txtPwd.getText())){
							statement.executeUpdate("update infoviewlogin SET last_login = '"+new SimpleDateFormat("dd-MM-yyyy hh.mm.ss aa").format(new Date())+"' where emp_id = '"+valueMap.get("emp_IdDB")+"'");
							switch ((Integer) valueMap.get("level")) {
							case 0:
								valueMap.put("levelCheck", (Integer) 0);
								valueMap.put("loginAs", (Integer) valueMap.get("level"));
								loginFrame.setVisible(false);
								new EmpLeaveApply(valueMap);
								break;
							case 1:
								loginFrame.setVisible(false);
								valueMap.put("levelCheck", (Integer) 1);
								new TlFormFunctionality(valueMap);
								break;
							case 2:
								loginFrame.setVisible(false);
								valueMap.put("levelCheck", (Integer) 2);
								new PmoApprovalFunctionality(valueMap);
								break;
							default:
								break;
							}
						}else{
							JOptionPane.showMessageDialog(null, "Either Employee-ID (or) Password mismatch!!", "Error", JOptionPane.ERROR_MESSAGE);
							txtPwd.requestFocus();
						}
					}else{
						JOptionPane.showMessageDialog(null, "Either Employee-ID (or) Password mismatch!!", "Error", JOptionPane.ERROR_MESSAGE);
						txtEmpId.requestFocus();
					}
				}else{
					JOptionPane.showMessageDialog(null, "Either Employee-ID (or) Password mismatch!!", "Error", JOptionPane.ERROR_MESSAGE);
					txtEmpId.requestFocus();
				}
			}else if(actionCommand.equals("Exit")){
				int confirmed = JOptionPane.showConfirmDialog(null, 
						"Are you sure you want to exit this program?", "Exit",
						JOptionPane.YES_NO_OPTION);
				if (confirmed == JOptionPane.YES_OPTION) {
					System.exit(0);
				}else{
					loginFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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

	public void focusLost(FocusEvent evt) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		Integer empId;
		String focusOutElement = String.valueOf(evt.getComponent()).split("command=")[1].split(",")[0];
		if(focusOutElement.equals("txtempid")){
			if(txtEmpId.getText().equals("")){
				empId = 0;
			}else{
				empId = Integer.parseInt(txtEmpId.getText());
			}
			try{
				Class.forName("com.mysql.jdbc.Driver");
				connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
				statement = (Statement) connection.createStatement();
				resultSet = statement.executeQuery("select * from infoviewLogin where emp_Id = '"+empId+"'");
				if(resultSet.next()){
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
					lastLoginDate.append(resultSet.getString("last_login"));
				}
				if(!valueMap.get("empName").equals("")){
					lblLastLogin.setText(lastLoginDate.toString());
					lblEmpName.setForeground(new Color(0, 153, 0));
					lblEmpName.setText((String) valueMap.get("empName"));
				}else if(empId != 0 && valueMap.get("empName").equals("")){
					lblEmpName.setForeground(Color.RED);
					lblEmpName.setText("*Check Your ID");
					txtEmpId.requestFocus();
				}else{
					lblEmpName.setForeground(Color.RED);
					lblEmpName.setText("*Enter Your ID");
					txtEmpId.requestFocus();
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

	public void focusGained(FocusEvent evt) {
		String focusGainElement = String.valueOf(evt.getComponent()).split("command=")[1].split(",")[0];
		if(focusGainElement.equals("txtempid")){
			valueMap.put("emp_IdDB", (Integer)0);
			valueMap.put("empName", "");
			valueMap.put("password", "");
			valueMap.put("level", (Integer)0);
			valueMap.put("TL", "");
			valueMap.put("PMO", "");
			valueMap.put("clleft", (Double)0.0);
			valueMap.put("slleft", (Double)0.0);
			valueMap.put("perleft", (Double)0.0);
			valueMap.put("LOP", (Double)0.0);
			txtEmpId.selectAll();
		}else if(focusGainElement.equals("txtpwd")){
			txtPwd.selectAll();
		}
	}

	public void keyTyped(KeyEvent evt) {
		String currentElement = String.valueOf(evt.getComponent()).split("command=")[1].split(",")[0];
		char numberField = evt.getKeyChar();
		if(currentElement.equals("txtempid")){
			if(!(Character.isDigit(numberField) || numberField == KeyEvent.VK_BACK_SPACE || numberField == KeyEvent.VK_DELETE )){
				evt.consume();
			}else{
				txtPwd.setText("");
			}
			if(numberField == KeyEvent.VK_ENTER){
				txtPwd.requestFocus(true);
			}
		}else if(currentElement.equals("txtpwd")){
			if(numberField == KeyEvent.VK_ENTER){
				btnLogin.doClick();
			}
		}
	}
	public void keyPressed(KeyEvent evt) {}
	public void keyReleased(KeyEvent arg0) {}

	public static void main(String[] args){
		loginObj = new LoginFunctionality();
	}
}
