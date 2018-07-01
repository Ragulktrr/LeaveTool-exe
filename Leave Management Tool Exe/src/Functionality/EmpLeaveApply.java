package Functionality;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import com.toedter.calendar.JTextFieldDateEditor;

import Design.EmpDesign;

public class EmpLeaveApply extends EmpDesign implements ActionListener {
	StringBuilder appendLeaveDates;
	Map<String, Object> valueMap;
	GregorianCalendar clslFromCal;
	GregorianCalendar clslToCal;
	Date clslFromDate;
	Date clslToDate;
	GregorianCalendar hdFromCal;
	Double clLeaveLeft;
	Double slLeaveLeft;
	Double perLeft;
	Double spinnerValue;
	Double lop;
	Date hdDateFunc;
	Date perDateFunc;
	GregorianCalendar perFromCal;
	SimpleDateFormat dateFormat;
	SimpleDateFormat dayFormatLet;
	SimpleDateFormat fullMonth;
	SimpleDateFormat fullDay;
	SimpleDateFormat dayFormat;
	SimpleDateFormat yearFormat;
	SimpleDateFormat sqlFormat;
	String textAreaReason;
	double clCount = 0;
	double slCount = 0;
	double perCount = 0;
	Connection connection;
	Statement statement;
	ResultSet resultSet;
	final String FILENAME;
	BufferedReader br;
	FileReader fr;
	String result;
	String sCurrentLine;
	List<String> govHoliday;

	public EmpLeaveApply(Map<String, Object> valueMap) {
		super(valueMap);
		this.valueMap = valueMap;
		connection = null;
		statement = null;
		resultSet = null;
		govHoliday = new ArrayList<String>();
		FILENAME = "C:/Users/KTRR/Desktop/CSV/Government_Holiday.txt";
		br = null;
		fr = null;
		try {
			br = new BufferedReader(new FileReader(FILENAME));
			while ((sCurrentLine = br.readLine()) != null) {
				StringTokenizer str = new StringTokenizer(sCurrentLine,",");
				while(str.hasMoreElements()){
					result = String.valueOf(str.nextElement());
					govHoliday.add(result.trim());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		clLeaveLeft = (Double) valueMap.get("clleft");
		slLeaveLeft = (Double) valueMap.get("slleft");
		lop = (Double) valueMap.get("LOP");
		perLeft = (Double) valueMap.get("perleft");
		dateFormat = new SimpleDateFormat("dd");
		dayFormat = new SimpleDateFormat("MM");
		fullMonth = new SimpleDateFormat("MMMM");
		dayFormatLet = new SimpleDateFormat("EE");
		fullDay = new SimpleDateFormat("EEEE");
		yearFormat = new SimpleDateFormat("yyyy");
		sqlFormat = new SimpleDateFormat("yyyy-MM-dd");

		clslFromCal = new GregorianCalendar();
		clslToCal = new GregorianCalendar();
		hdFromCal = new GregorianCalendar();
		perFromCal = new GregorianCalendar();

		cmbLeave.setActionCommand("combo");
		cmbLeave.addActionListener(this);
		btnApply.setActionCommand("apply");
		btnApply.addActionListener(this);
		btnLeaveSummary.setActionCommand("summary");
		btnLeaveSummary.addActionListener(this);
		btnSignout.setActionCommand("signout");
		btnSignout.addActionListener(this);
		btnBack.setActionCommand("Back");
		btnBack.addActionListener(this);
		cmbLeave.requestFocus();
		if(valueMap.get("levelCheck").equals(0) && valueMap.get("loginAs").equals(0) || valueMap.get("levelCheck").equals(1) && valueMap.get("loginAs").equals(0)){
			HdDate.getJCalendar().setMinSelectableDate(new Date());
			PerDate.getJCalendar().setMinSelectableDate(new Date());
		}
		double addClLeft = 0.0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveapprovallogin","root","root");
			statement  = (Statement) connection.createStatement();
			resultSet = statement.executeQuery("select cl_left, year from infoviewlogin where emp_id = '"+valueMap.get("emp_IdDB")+"'");
			if(resultSet.next()){
				if(Integer.parseInt(yearFormat.format(new Date())) > resultSet.getInt("year")){
					addClLeft = resultSet.getDouble("cl_left") + 12;
					if(addClLeft >= 36){
						addClLeft = 36;
					}
					statement.executeUpdate("update infoviewlogin SET cl_left = "+addClLeft+", sl_left = '6', year = '"+yearFormat.format(new Date())+"' where emp_id = '"+valueMap.get("emp_IdDB")+"'");
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

		clFromdte.addPropertyChangeListener("date", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				clTodte.getJCalendar().setMinSelectableDate(clFromdte.getDate());
				clCount = 0;
				if(clTodte.getDate() == null){
					lblCountLeave.setText("*Please pick To-Date");
					lblCountLeave.setForeground(Color.RED);
				}else{
					clslFromCal.setTime(clFromdte.getDate());
					while(clslFromCal.getTime().before(clTodte.getDate()) || clslFromCal.getTime().equals(clTodte.getDate())){
						if(!(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun"))){
							if(!govHoliday.contains(sqlFormat.format(clslFromCal.getTime()).toString())){
								clCount = clCount + 1;
							}
							clslFromCal.add(Calendar.DATE, 1);
						}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun")){
							clslFromCal.add(Calendar.DATE, 1);
						}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat")){
							clslFromCal.add(Calendar.DATE, 2);
						}
					}
					if(clCount > 0){
						if(clCount == 1){
							lblCountLeave.setText(clCount+" - Day picked");
						}else{
							lblCountLeave.setText(clCount+" - Days picked");
						}
						lblCountLeave.setForeground(new Color(0, 153, 0));
					}else{
						lblCountLeave.setText("*Please Pick Valid Date");
						lblCountLeave.setForeground(Color.RED);
					}
				}
			}
		});

		clTodte.addPropertyChangeListener("date", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				clCount = 0;
				if(clFromdte.getDate() == null){
					lblCountLeave.setText("*Please pick From-Date");
					lblCountLeave.setForeground(Color.RED);
					editor.setText(null);
				}else{
					clslFromCal.setTime(clFromdte.getDate());
					while(clslFromCal.getTime().before(clTodte.getDate()) || clslFromCal.getTime().equals(clTodte.getDate())){
						if(!(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun"))){
							if(!govHoliday.contains(sqlFormat.format(clslFromCal.getTime()).toString())){
								clCount = clCount + 1;
							}
							clslFromCal.add(Calendar.DATE, 1);
						}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun")){
							clslFromCal.add(Calendar.DATE, 1);
						}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat")){
							clslFromCal.add(Calendar.DATE, 2);
						}
					}
					if(clCount > 0){
						if(clCount == 1){
							lblCountLeave.setText(clCount+" - Day picked");
						}else{
							lblCountLeave.setText(clCount+" - Days picked");
						}
						lblCountLeave.setForeground(new Color(0, 153, 0));
					}else{
						lblCountLeave.setText("*Please Pick Valid Date");
						lblCountLeave.setForeground(Color.RED);
					}
					clFromdte.getJCalendar().setMaxSelectableDate(clTodte.getDate());
				}
			}
		});

		slFromdte.addPropertyChangeListener("date", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				slCount = 0;
				slTodte.getJCalendar().setMinSelectableDate(slFromdte.getDate());
				if(slTodte.getDate() == null){
					lblCountLeave.setText("*Please pick To-Date");
					lblCountLeave.setForeground(Color.RED);
				}else{
					clslFromCal.setTime(slFromdte.getDate());
					while(clslFromCal.getTime().before(slTodte.getDate()) || clslFromCal.getTime().equals(slTodte.getDate())){
						if(!(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun"))){
							if(!govHoliday.contains(sqlFormat.format(clslFromCal.getTime()).toString())){
								slCount = slCount + 1;
							}
							clslFromCal.add(Calendar.DATE, 1);
						}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun")){
							clslFromCal.add(Calendar.DATE, 1);
						}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat")){
							clslFromCal.add(Calendar.DATE, 2);
						}
					}
					if(slCount > 0){
						if(slCount == 1){
							lblCountLeave.setText(slCount+" - Day picked");
						}else{
							lblCountLeave.setText(slCount+" - Days picked");
						}
						lblCountLeave.setForeground(new Color(0, 153, 0));
					}else{
						lblCountLeave.setText("*Please Pick Valid Date");
						lblCountLeave.setForeground(Color.RED);
					}
				}
			}
		});

		slTodte.addPropertyChangeListener("date", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				slCount = 0;
				if(slFromdte.getDate() == null){
					lblCountLeave.setText("Choose From Date");
					lblCountLeave.setForeground(Color.RED);
					editor.setText(null);
				}else{
					clslFromCal.setTime(slFromdte.getDate());
					while(clslFromCal.getTime().before(slTodte.getDate()) || clslFromCal.getTime().equals(slTodte.getDate())){
						if(!(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun"))){
							if(!govHoliday.contains(sqlFormat.format(clslFromCal.getTime()).toString())){
								slCount = slCount + 1;
							}
							clslFromCal.add(Calendar.DATE, 1);
						}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun")){
							clslFromCal.add(Calendar.DATE, 1);
						}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat")){
							clslFromCal.add(Calendar.DATE, 2);
						}
					}
					if(slCount > 0){
						if(slCount == 1){
							lblCountLeave.setText(slCount+" - Day picked");
						}else{
							lblCountLeave.setText(slCount+" - Days picked");
						}
						lblCountLeave.setForeground(new Color(0, 153, 0));
					}else{
						lblCountLeave.setText("*Please Pick Valid Date");
						lblCountLeave.setForeground(Color.RED);
					}
					slFromdte.getJCalendar().setMaxSelectableDate(slTodte.getDate());
				}

			}
		});

		HdDate.addPropertyChangeListener("date", new PropertyChangeListener() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				lblCountHDLeave.setText("");
				hdFromCal.setTime(HdDate.getDate());
				if(dayFormatLet.format(hdFromCal.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(hdFromCal.getTime()).toString().equalsIgnoreCase("Sun") || govHoliday.contains(sqlFormat.format(hdFromCal.getTime()).toString())){
					lblCountHDLeave.setText("*Check picked date");
					lblCountHDLeave.setForeground(Color.RED);
				}
				if(valueMap.get("loginAs").equals(0)){
					if(Calendar.getInstance().getTime().toString().substring(0, 10).equalsIgnoreCase(HdDate.getDate().toString().substring(0, 10))){
						cmbSession.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"PM"}));
						sickrbtn.setVisible(true);
					}else{
						cmbSession.setModel(new javax.swing.DefaultComboBoxModel(session));
						casualrbtn.setSelected(true);
						sickrbtn.setVisible(false);
					}
				}
			}
		});

		PerDate.addPropertyChangeListener("date", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				lblPermissionCount.setText("");
				perFromCal.setTime(PerDate.getDate());
				if(dayFormatLet.format(perFromCal.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(perFromCal.getTime()).toString().equalsIgnoreCase("Sun") || govHoliday.contains(sqlFormat.format(perFromCal.getTime()).toString())){
					lblPermissionCount.setText("*Check entered date");
					lblPermissionCount.setForeground(Color.RED);
				}
			}
		});

		empleave.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int confirmed = JOptionPane.showConfirmDialog(null, 
						"Are you sure you want to exit this program?", "Exit",
						JOptionPane.YES_NO_OPTION);

				if (confirmed == JOptionPane.YES_OPTION) {
					empleave.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}else{
					empleave.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				}
			}
		});
		labelValueSetting();
	}

	public void labelValueSetting() {
		int sqlCount = 0;
		int TLA = 0, PMOA = 0, Approved = 0, Rejected = 0;
		StringBuilder leaveStatusSB = new StringBuilder();
		connection = null;
		statement = null;
		resultSet = null;
		leaveStatusSB.append("<html><u>Leave Status :</u>");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveapprovallogin","root","root");
			statement  = (Statement) connection.createStatement();
			resultSet = statement .executeQuery("select count(leave_status) from applyleave where (leave_type = 'Casual Leave' or leave_type = 'Half-Day Leave') and (curdate() <= fddate or curdate() <= hddate) and emp_id = "+valueMap.get("emp_IdDB")+"");
			if(resultSet.next()){
				sqlCount = resultSet.getInt(1);
			}
			if(sqlCount > 0){
				resultSet = statement .executeQuery("select leave_status, fddate, hddate from applyleave where ((leave_type = 'Casual Leave' or leave_type = 'Sick Leave') or leave_type = 'Half-Day Leave') and emp_id = '"+valueMap.get("emp_IdDB")+"'");
				if(!resultSet.next()){
					leaveStatusSB.append("<br> - No Leaves Applied");
				}
				resultSet.beforeFirst();
				while(resultSet.next()){
					if(resultSet.getString("leave_status").equals("0")){
						TLA = TLA + 1;
					}
					if(resultSet.getString("leave_status").equals("1")){
						PMOA = PMOA + 1;
					}
					if(resultSet.getString("leave_status").equals("2")){
						Approved = Approved + 1;
					}
					if(resultSet.getString("leave_status").equals("-1")){
						Rejected = Rejected + 1;
					}
				}
			}
			if(sqlCount == 0){
				leaveStatusSB.append("<br> - No Leaves Applied");
			}else{
				if(TLA > 0){
					leaveStatusSB.append("<br>- Waiting For TL Approval  =>  "+TLA);
				}
				if(PMOA > 0){
					leaveStatusSB.append("<br>- Waiting For PMO Approval  =>  "+PMOA);
				}
				if(Approved > 0){
					leaveStatusSB.append("<br>- Confirmed  =>  "+Approved);
				}
				if(Rejected > 0){
					leaveStatusSB.append("<br>- Rejected  =>  "+Rejected);
				}
			}
			leaveStatusSB.append("</html>");
			lblLeaveStatus.setText(leaveStatusSB.toString());
			leaveStatusSB.delete(0, leaveStatusSB.length());
			resultSet = statement.executeQuery("select * from infoviewLogin where emp_id = '"+valueMap.get("emp_IdDB")+"'");
			while(resultSet.next()){
				leaveStatusSB.append("<html><u>Leave Balance : </u>");
				leaveStatusSB.append("<br>- Casual Leave : "+resultSet.getDouble("cl_left"));
				leaveStatusSB.append("<br>- Sick Leave : "+resultSet.getDouble("sl_left"));
				leaveStatusSB.append("<br>- Permission : "+resultSet.getDouble("per_left"));
				leaveStatusSB.append("<br>- LOP : "+resultSet.getDouble("LOP"));
			}
			lblBalanceLeave.setText(leaveStatusSB.toString());
			resultSet = statement.executeQuery("select * from infoviewLogin where emp_id = '"+valueMap.get("emp_IdDB")+"'");
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
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		String selectedCmb = String.valueOf(cmbLeave.getSelectedItem());
		if(actionCommand.equals("combo")){
			pnlHolder.removeAll();
			if(selectedCmb.equals("Casual Leave")){
				pnlClSl.setLayout(null);
				pnlClSl.setBounds(0, 0, 900, 435);
				clFromdte.setVisible(true);
				clTodte.setVisible(true);
				slFromdte.setVisible(false);
				slTodte.setVisible(false);
				pnlClSl.add(lblFrmDte);
				pnlClSl.add(clFromdte);
				pnlClSl.add(lblCountLeave);
				pnlClSl.add(lblToDte);
				pnlClSl.add(clTodte);
				try {
					clDate = sqlFormat.parse(sqlFormat.format(new Date()));
					clConstantCalendar = Calendar.getInstance();
					clConstantCalendar.setTime(clDate);
					clConstantCalendar.add(Calendar.DATE, 1);
					while(dayFormatLet.format(clConstantCalendar.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(clConstantCalendar.getTime()).toString().equalsIgnoreCase("Sun") || govHoliday.contains(sqlFormat.format(clConstantCalendar.getTime()).toString())){
						clConstantCalendar.add(Calendar.DATE, 1);
					}
					clDate = sqlFormat.parse(sqlFormat.format(clConstantCalendar.getTime()));
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				if(valueMap.get("levelCheck").equals(0) && valueMap.get("loginAs").equals(0) || valueMap.get("levelCheck").equals(1) && valueMap.get("loginAs").equals(0)){
					clFromdte.getJCalendar().setMinSelectableDate(clDate);
					clTodte.getJCalendar().setMinSelectableDate(clDate);
				}
				editor = (JTextFieldDateEditor) clFromdte.getDateEditor();
				editor.setEditable(false);
				editor.setText(sqlFormat.format(clConstantCalendar.getTime()));
				editor = (JTextFieldDateEditor) clTodte.getDateEditor();
				editor.setEditable(false);
				editor.setText(sqlFormat.format(clConstantCalendar.getTime()));
				pnlClSl.add(lblReason);
				pnlClSl.add(txtAreascroll);
				pnlClSl.add(btnSignout);
				pnlClSl.add(btnApply);
				pnlClSl.add(btnLeaveSummary);
				clFromdte.setDate(clDate);
				if(clCount > 0){
					if(clCount == 1){
						lblCountLeave.setText(clCount+" - Day picked");
					}else{
						lblCountLeave.setText(clCount+" - Days picked");
					}
					lblCountLeave.setForeground(new Color(0, 153, 0));
				}
				btnLeaveSummary.setBounds(250, 250, 135, 40);
				btnApply.setBounds(435, 250, 125, 40);
				btnSignout.setBounds(610, 250, 125, 40);
				if(valueMap.get("back") != null){
					pnlClSl.add(btnBack);
					btnLeaveSummary.setBounds(150, 250, 135, 40);
					btnApply.setBounds(325, 250, 125, 40);
					btnSignout.setBounds(500, 250, 125, 40);
					btnBack.setBounds(675, 250, 125, 40);
				}
				pnlHolder.add(pnlClSl);
			}else if(selectedCmb.equals("Sick Leave")){
				pnlClSl.setLayout(null);
				pnlClSl.setBounds(0, 0, 900, 435);
				clFromdte.setVisible(false);
				clTodte.setVisible(false);
				slFromdte.setVisible(true);
				slTodte.setVisible(true);
				clConstantCalendar = Calendar.getInstance();
				clConstantCalendar.setTime(slDate);
				while(dayFormatLet.format(clConstantCalendar.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(clConstantCalendar.getTime()).toString().equalsIgnoreCase("Sun") || govHoliday.contains(sqlFormat.format(clConstantCalendar.getTime()).toString())){
					clConstantCalendar.add(Calendar.DATE, 1);
				}
				try {
					slDate = sqlFormat.parse(sqlFormat.format(clConstantCalendar.getTime()));
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				if(valueMap.get("levelCheck").equals(0) && valueMap.get("loginAs").equals(0) || valueMap.get("levelCheck").equals(1) && valueMap.get("loginAs").equals(0)){
					slFromdte.getJCalendar().setMinSelectableDate(slDate);
					slTodte.getJCalendar().setMinSelectableDate(slDate);
				}
				editor = (JTextFieldDateEditor) slFromdte.getDateEditor();
				editor.setEditable(false);
				editor.setText(sqlFormat.format(clConstantCalendar.getTime()));
				editor = (JTextFieldDateEditor) slTodte.getDateEditor();
				editor.setEditable(false);
				editor.setText(sqlFormat.format(clConstantCalendar.getTime()));
				pnlClSl.add(lblFrmDte);
				pnlClSl.add(slFromdte);
				pnlClSl.add(lblCountLeave);
				pnlClSl.add(lblToDte);
				pnlClSl.add(slTodte);
				pnlClSl.add(lblReason);
				pnlClSl.add(txtAreascroll);
				pnlClSl.add(btnSignout);
				pnlClSl.add(btnApply);
				pnlClSl.add(btnLeaveSummary);
				slFromdte.setDate(slDate);
				if(slCount > 0){
					if(slCount == 1){
						lblCountLeave.setText(slCount+" - Day picked");
					}else{
						lblCountLeave.setText(slCount+" - Days picked");
					}
					lblCountLeave.setForeground(new Color(0, 153, 0));
				}
				btnLeaveSummary.setBounds(250, 250, 135, 40);
				btnApply.setBounds(435, 250, 125, 40);
				btnSignout.setBounds(610, 250, 125, 40);
				if(valueMap.get("back") != null){
					pnlClSl.add(btnBack);
					btnLeaveSummary.setBounds(150, 250, 135, 40);
					btnApply.setBounds(325, 250, 125, 40);
					btnSignout.setBounds(500, 250, 125, 40);
					btnBack.setBounds(675, 250, 125, 40);
				}
				pnlHolder.add(pnlClSl);
			}else if(selectedCmb.equals("Half-Day Leave")){
				lblCountLeave.setText("");
				bindclsl.add(casualrbtn);
				bindclsl.add(sickrbtn);
				pnlHd.setLayout(null);
				pnlHd.setBounds(0, 0, 900, 435);
				pnlHd.add(casualrbtn);
				pnlHd.add(sickrbtn);
				pnlHd.add(lblHdDate);
				pnlHd.add(HdDate);
				editor = (JTextFieldDateEditor) HdDate.getDateEditor();
				editor.setEditable(false);
				pnlHd.add(lblCountHDLeave);
				pnlHd.add(lblHdSession);
				pnlHd.add(cmbSession);
				pnlHd.add(lblReason);
				pnlHd.add(txtAreaHDscroll);
				pnlHd.add(btnSignout);
				pnlHd.add(btnApply);
				pnlHd.add(btnLeaveSummary);
				btnLeaveSummary.setBounds(250, 250, 135, 40);
				btnApply.setBounds(435, 250, 125, 40);
				btnSignout.setBounds(610, 250, 125, 40);
				if(valueMap.get("back") != null){
					pnlHd.add(btnBack);
					btnBack.setBounds(675, 250, 125, 40);
					btnLeaveSummary.setBounds(150, 250, 135, 40);
					btnApply.setBounds(325, 250, 125, 40);
					btnSignout.setBounds(500, 250, 125, 40);
				}
				pnlHolder.add(pnlHd);
			}else if(selectedCmb.equals("Permission")){
				lblCountLeave.setText("");
				pnlPermission.setLayout(null);
				pnlPermission.setBounds(0, 0, 900, 435);
				pnlPermission.add(lblPerLeave);
				pnlPermission.add(PerDate);
				editor = (JTextFieldDateEditor) PerDate.getDateEditor();
				editor.setEditable(false);
				pnlPermission.add(lblFromTime);
				pnlPermission.add(spinner);
				pnlPermission.add(lblPermissionCount);
				pnlPermission.add(btnLeaveSummary);
				pnlPermission.add(btnApply);
				pnlPermission.add(btnSignout);
				Date converted;
				Calendar cal;
				try {
					converted = sqlFormat.parse(sqlFormat.format(new Date()));
					cal = Calendar.getInstance();
					cal.setTime(converted);
					cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
					while(dayFormatLet.format(cal.getTime()).equals("Sat") || dayFormatLet.format(cal.getTime()).equals("Sun") || govHoliday.contains(sqlFormat.format(cal.getTime()).toString())){
						cal.add(Calendar.DATE, -1);
					}
					if(sqlFormat.format(cal.getTime()).equals(sqlFormat.format(new Date()))){
						cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
						cal.add(Calendar.DATE, 1);
						while(dayFormatLet.format(cal.getTime()).equals("Sat") || dayFormatLet.format(cal.getTime()).equals("Sun") || govHoliday.contains(sqlFormat.format(cal.getTime()).toString())){
							cal.add(Calendar.DATE, 1);
						}
					}
					converted.setTime(cal.getTimeInMillis());
					if(valueMap.get("levelCheck").equals(0) && valueMap.get("loginAs").equals(0) || valueMap.get("levelCheck").equals(1) && valueMap.get("loginAs").equals(0)){
						PerDate.getJCalendar().setMaxSelectableDate(converted);
					}
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				btnLeaveSummary.setBounds(200, 190, 135, 40);
				btnApply.setBounds(385, 190, 125, 40);
				btnSignout.setBounds(560, 190, 125, 40);
				if(valueMap.get("back") != null){
					btnLeaveSummary.setBounds(110, 190, 135, 40);
					btnApply.setBounds(300, 190, 125, 40);
					btnSignout.setBounds(480, 190, 125, 40);
					btnBack.setBounds(660, 190, 125, 40);
					pnlPermission.add(btnBack);
				}
				pnlHolder.add(pnlPermission);
			}else{
				lblCountLeave.setText("");
				btnLeaveSummary.setBounds(315, 80, 135, 40);
				btnSignout.setBounds(475, 80, 125, 40);

				if(valueMap.get("back") != null){
					btnLeaveSummary.setBounds(255, 80, 135, 40);
					btnSignout.setBounds(415, 80, 125, 40);
					btnBack.setBounds(565, 80, 125, 40);
					pnlHolder.add(btnBack);
				}
				pnlHolder.add(btnLeaveSummary);
				pnlHolder.add(btnSignout);
			}

			pnlMain.updateUI();
		}else if(actionCommand.equals("apply")){
			connection = null;
			statement  = null;
			resultSet = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveapprovallogin","root","root");
				statement  = (Statement) connection.createStatement();

				appendLeaveDates = new StringBuilder();
				if(selectedCmb.equals("Casual Leave")){
					textAreaReason = leaveReason.getText();
					textAreaReason = textAreaReason.replace("'","''");
					clslFromDate = (Date) clFromdte.getDate();
					clslToDate = (Date) clTodte.getDate();
					if(clslFromDate == null || clslToDate == null || leaveReason.getText().equals("")){
						lblCountLeave.setText("*All fields are mandatory");
						lblCountLeave.setForeground(Color.RED);
					}else{
						clslFromCal.setTime(clslFromDate);
						clslToCal.setTime(clslToDate);
						resultSet = statement .executeQuery("select fddate, hddate, perdate from `leaveapprovallogin`.`applyleave` where emp_id ='"+valueMap.get("emp_IdDB")+"'");
						while(resultSet.next()){
							clslFromCal.setTime(clslFromDate);
							while(!clslFromCal.getTime().after(clTodte.getDate())){
								if(resultSet.getString("fddate").equals(sqlFormat.format(clslFromCal.getTime())) || resultSet.getString("hddate").equals(sqlFormat.format(clslFromCal.getTime())) || resultSet.getString("perdate").equals(sqlFormat.format(clslFromCal.getTime()))){
									appendLeaveDates.append(sqlFormat.format(clslFromCal.getTime())+"\n");
									clslFromCal.add(Calendar.DATE, 1);
									break;
								}else{
									clslFromCal.add(Calendar.DATE, 1);
								}
							}
						}
						clslFromCal.setTime(clslFromDate);
						if(appendLeaveDates.length() > 0){
							JOptionPane.showMessageDialog(null, "Already Leave(s) has been applied for the following dates: \n"+appendLeaveDates, "Duplicate Dates", JOptionPane.ERROR_MESSAGE);
						}else{
							if(!clslFromCal.getTime().after(clTodte.getDate())){
								if(clCount == clLeaveLeft){
									if (JOptionPane.showConfirmDialog(null, "You Don't have casual leaves in future\nAre you sure want to apply for "+clCount+" days leave ?", "Confirmation",
											JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
										while(!clslFromCal.getTime().after(clTodte.getDate())){
											if(!(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun"))){
												if(!govHoliday.contains(sqlFormat.format(clslFromCal.getTime()).toString())){
													statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
															+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, `leave_status`, `leave_type`, `fddate`,`session`, `reason`, `lop_date`, `unplanned_leave`, `hddate`, `perdate`, `per_applied_for`) "
															+ "VALUES "
															+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', '"+selectedCmb+"', '"+sqlFormat.format(clslFromCal.getTime())+"', '-', '"+textAreaReason+"', 0, '"+unplannedLeave+"', '-', '-', '0')");
												}
												clslFromCal.add(Calendar.DATE, 1);
											}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun")){
												clslFromCal.add(Calendar.DATE, 1);
											}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat")){
												clslFromCal.add(Calendar.DATE, 2);
											}
										}
										clLeaveLeft = clLeaveLeft - clCount;
										slLeaveLeft = (Double) valueMap.get("slleft");
										statement .executeUpdate("UPDATE `leaveapprovallogin`.`infoviewlogin` SET cl_left = "+clLeaveLeft+", sl_left = "+slLeaveLeft+", LOP = "+lop+" WHERE emp_id = '"+valueMap.get("emp_IdDB")+"'");
										JOptionPane.showMessageDialog(null, "Sucessfully Applied!!!\nBalance:\n       - CL Left = "+clLeaveLeft+"\n       - SL Left = "+slLeaveLeft+"\n       - LOP = "+lop);
									}
								}else if(clCount < clLeaveLeft){
									if (JOptionPane.showConfirmDialog(null, "Are you sure want to apply for "+clCount+" days leave ?", "Confirmation",
											JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
										while(!clslFromCal.getTime().after(clTodte.getDate())){
											if(!(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun"))){
												if(!govHoliday.contains(sqlFormat.format(clslFromCal.getTime()).toString())){

													statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
															+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, `leave_status`, `leave_type`, `fddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `hddate`, `perdate`, `per_applied_for`) "
															+ "VALUES "
															+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', '"+selectedCmb+"', '"+sqlFormat.format(clslFromCal.getTime())+"', '-', '"+textAreaReason+"', 0, '"+unplannedLeave+"', '-', '-', '0')");
												}
												clslFromCal.add(Calendar.DATE, 1);
											}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun")){
												clslFromCal.add(Calendar.DATE, 1);
											}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat")){
												clslFromCal.add(Calendar.DATE, 2);
											}
										}
										clLeaveLeft = clLeaveLeft - clCount;
										slLeaveLeft = (Double) valueMap.get("slleft");
										statement .executeUpdate("UPDATE `leaveapprovallogin`.`infoviewlogin` SET cl_left = "+clLeaveLeft+", sl_left = "+slLeaveLeft+", LOP = "+lop+" WHERE emp_id = '"+valueMap.get("emp_IdDB")+"';");
										JOptionPane.showMessageDialog(null, "Sucessfully Applied!!!\nBalance:\n       - CL Left = "+clLeaveLeft+"\n       - SL Left = "+slLeaveLeft+"\n       - LOP = "+lop);
									}
								}else if(clCount > clLeaveLeft){
									if (JOptionPane.showConfirmDialog(null, "You Have only "+clLeaveLeft+" leaves!!!\nAre you sure want to apply for "+clCount+" days leave ?", "Confirmation",
											JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
										slLeaveLeft = (Double) valueMap.get("slleft");
										clLeaveLeft = (Double) valueMap.get("clleft");
										while(!clslFromCal.getTime().after(clTodte.getDate())){
											if(!(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun"))){
												if(!govHoliday.contains(sqlFormat.format(clslFromCal.getTime()).toString())){
													if(clLeaveLeft > 0){
														if(clLeaveLeft == 0.5){
															statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
																	+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
																	+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
																	+ "VALUES "
																	+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', 'Casual Leave', '"+sqlFormat.format(clslFromCal.getTime())+"', 'AM', '"+textAreaReason+"', 0, '"+unplannedLeave+"', '-', '-', '0')");
															clCount = clCount - 0.5;
															clLeaveLeft = clLeaveLeft - 0.5;
															if(slLeaveLeft > 0){
																statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
																		+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
																		+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
																		+ "VALUES "
																		+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', 'Sick Leave', '"+sqlFormat.format(clslFromCal.getTime())+"', 'PM', '"+textAreaReason+"', 0, '"+unplannedLeave+"', '-', '-', '0')");
																clCount = clCount - 0.5;
																slLeaveLeft = slLeaveLeft - 0.5;
															}else{
																statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
																		+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
																		+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
																		+ "VALUES "
																		+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', 'Casual Leave', '"+sqlFormat.format(clslFromCal.getTime())+"', 'PM', '"+textAreaReason+"', 1, 1, '-', '-', '0')");
																clCount = clCount - 0.5;
																lop = lop + 0.5;
															}
														}else{
															statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
																	+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
																	+ "`leave_status`, `leave_type`, `fddate`,`session`, `reason`, `lop_date`, `unplanned_leave`, `hddate`, `perdate`, `per_applied_for`) "
																	+ "VALUES "
																	+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', 'Casual Leave', '"+sqlFormat.format(clslFromCal.getTime())+"', '-', '"+textAreaReason+"', 0, '"+unplannedLeave+"', '-', '-', '0')");
															clCount = clCount - 1;
															clLeaveLeft = clLeaveLeft - 1;
														}
													}else if(slLeaveLeft > 0){
														if(slLeaveLeft == 0.5){
															statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
																	+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
																	+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
																	+ "VALUES "
																	+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', 'Sick Leave', '"+sqlFormat.format(clslFromCal.getTime())+"', 'AM', '"+textAreaReason+"', 0, '"+unplannedLeave+"', '-', '-', '0')");
															clCount = clCount - 0.5;
															slLeaveLeft = slLeaveLeft - 0.5;
															if(clCount >= 0.5){
																statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
																		+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
																		+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
																		+ "VALUES "
																		+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', 'Casual Leave', '"+sqlFormat.format(clslFromCal.getTime())+"', 'PM', '"+textAreaReason+"', 1, 1, '-', '-', '0')");
																clCount = clCount - 0.5;
																lop = lop + 0.5;
															}
														}else{
															statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
																	+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
																	+ "`leave_status`, `leave_type`, `fddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `hddate`, `perdate`, `per_applied_for`) "
																	+ "VALUES "
																	+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', 'Sick Leave', '"+sqlFormat.format(clslFromCal.getTime())+"', '-', '"+textAreaReason+"', 0, '"+unplannedLeave+"', '-', '-', '0')");
															clCount = clCount - 1;
															slLeaveLeft = slLeaveLeft - 1;
														}
													}else{
														if(clCount == 0.5){
															statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
																	+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
																	+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
																	+ "VALUES "
																	+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', 'Casual Leave', '"+sqlFormat.format(clslFromCal.getTime())+"', 'AM', '"+textAreaReason+"', 1, 1, '-', '-', '0')");
															clCount = clCount - 0.5;
															lop = lop + 0.5;
														}else{
															statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
																	+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
																	+ "`leave_status`, `leave_type`, `fddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `hddate`, `perdate`, `per_applied_for`) "
																	+ "VALUES "
																	+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', 'Casual Leave', '"+sqlFormat.format(clslFromCal.getTime())+"', '-', '"+textAreaReason+"', 1, 1, '-', '-', '0')");
															clCount = clCount - 1;
															lop = lop + 1;
														}
													}
												}
												clslFromCal.add(Calendar.DATE, 1);
											}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun")){
												clslFromCal.add(Calendar.DATE, 1);
											}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat")){
												clslFromCal.add(Calendar.DATE, 2);
											}
										}
										statement .executeUpdate("UPDATE `leaveapprovallogin`.`infoviewlogin` SET cl_left = "+clLeaveLeft+", sl_left = "+slLeaveLeft+", LOP = "+lop+" WHERE emp_id = '"+valueMap.get("emp_IdDB")+"';");
										JOptionPane.showMessageDialog(null, "Sucessfully Applied!!!\nBalance:\n       - CL Left = "+clLeaveLeft+"\n       - SL Left = "+slLeaveLeft+"\n       - LOP = "+lop);
									}
								}
							}else{
								JOptionPane.showMessageDialog(null, "Please Pick Valid Date!!!", "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
					labelValueSetting();
				}else if(selectedCmb.equals("Sick Leave")){
					textAreaReason = leaveReason.getText();
					textAreaReason = textAreaReason.replace("'","''");
					clslFromDate = (Date) slFromdte.getDate();
					clslToDate = (Date) slTodte.getDate();
					if(clslFromDate == null || clslToDate == null || leaveReason.getText().equals("")){
						lblCountLeave.setText("*All fields are mandatory");
						lblCountLeave.setForeground(Color.RED);
					}else{
						clslFromCal.setTime(clslFromDate);
						clslToCal.setTime(clslToDate);
						resultSet = statement .executeQuery("select fddate, hddate, perdate from `leaveapprovallogin`.`applyleave` where emp_id ='"+valueMap.get("emp_IdDB")+"'");
						while(resultSet.next()){
							clslFromCal.setTime(clslFromDate);
							while(!clslFromCal.getTime().after(slTodte.getDate())){
								if(resultSet.getString("fddate").equals(sqlFormat.format(clslFromCal.getTime())) || resultSet.getString("hddate").equals(sqlFormat.format(clslFromCal.getTime())) || resultSet.getString("perdate").equals(sqlFormat.format(clslFromCal.getTime()))){
									appendLeaveDates.append(sqlFormat.format(clslFromCal.getTime())+"\n");
									clslFromCal.add(Calendar.DATE, 1);
									break;
								}else{
									clslFromCal.add(Calendar.DATE, 1);
								}
							}
						}
						clslFromCal.setTime(clslFromDate);
						if(appendLeaveDates.length() > 0){
							JOptionPane.showMessageDialog(null, "Already Leave(s) has been applied for the following dates: \n"+appendLeaveDates, "Duplicate Dates", JOptionPane.ERROR_MESSAGE);
						}else{
							if(!clslFromCal.getTime().after(slTodte.getDate())){
								if(slCount == slLeaveLeft){
									if (JOptionPane.showConfirmDialog(null, "You Don't have sick leaves in future\nAre you sure want to apply for "+slCount+" days leave ?", "Confirmation",
											JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
										while(!clslFromCal.getTime().after(slTodte.getDate())){
											if(!(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun"))){
												if(!govHoliday.contains(sqlFormat.format(clslFromCal.getTime()).toString())){

													statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
															+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, `leave_status`, `leave_type`, `fddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `hddate`, `perdate`, `per_applied_for`) "
															+ "VALUES "
															+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', '"+selectedCmb+"', '"+sqlFormat.format(clslFromCal.getTime())+"', '-', '"+textAreaReason+"', 0, '"+unplannedLeave+"', '-', '-', '0')");
												}
												clslFromCal.add(Calendar.DATE, 1);
											}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun")){
												clslFromCal.add(Calendar.DATE, 1);
											}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat")){
												clslFromCal.add(Calendar.DATE, 2);
											}
										}
										clLeaveLeft = (Double) valueMap.get("clleft");
										slLeaveLeft = slLeaveLeft - slCount;
										statement .executeUpdate("UPDATE `leaveapprovallogin`.`infoviewlogin` SET cl_left = "+clLeaveLeft+", sl_left = "+slLeaveLeft+", LOP = "+lop+" WHERE emp_id = '"+valueMap.get("emp_IdDB")+"'");
										JOptionPane.showMessageDialog(null, "Sucessfully Applied!!!\nBalance:\n       - CL Left = "+clLeaveLeft+"\n       - SL Left = "+slLeaveLeft+"\n       - LOP = "+lop);
									}
								}else if(slCount < slLeaveLeft){
									if (JOptionPane.showConfirmDialog(null, "Are you sure want to apply for "+slCount+" days leave ?", "Confirmation",
											JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
										while(!clslFromCal.getTime().after(slTodte.getDate())){
											if(!(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun"))){
												if(!govHoliday.contains(sqlFormat.format(clslFromCal.getTime()).toString())){

													statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
															+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, `leave_status`, `leave_type`, `fddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `hddate`, `perdate`, `per_applied_for`) "
															+ "VALUES "
															+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', '"+selectedCmb+"', '"+sqlFormat.format(clslFromCal.getTime())+"', '-', '"+textAreaReason+"', 0, '"+unplannedLeave+"', '-', '-', '0')");
												}
												clslFromCal.add(Calendar.DATE, 1);
											}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun")){
												clslFromCal.add(Calendar.DATE, 1);
											}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat")){
												clslFromCal.add(Calendar.DATE, 2);
											}
										}
										clLeaveLeft = (Double) valueMap.get("clleft");
										slLeaveLeft = slLeaveLeft - slCount;
										statement .executeUpdate("UPDATE `leaveapprovallogin`.`infoviewlogin` SET cl_left = "+clLeaveLeft+", sl_left = "+slLeaveLeft+", LOP = "+lop+" WHERE emp_id = '"+valueMap.get("emp_IdDB")+"'");
										JOptionPane.showMessageDialog(null, "Sucessfully Applied!!!\nBalance:\n       - CL Left = "+clLeaveLeft+"\n       - SL Left = "+slLeaveLeft+"\n       - LOP = "+lop);
									}
								}else if(slCount > slLeaveLeft){
									if (JOptionPane.showConfirmDialog(null, "You Have only "+slLeaveLeft+" leaves!!!\nAre you sure want to apply for "+slCount+" days leave ?", "Confirmation",
											JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
										while(!clslFromCal.getTime().after(slTodte.getDate())){
											if(!(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun"))){
												if(!govHoliday.contains(sqlFormat.format(clslFromCal.getTime()).toString())){
													if(slLeaveLeft > 0){
														if(slLeaveLeft == 0.5){
															statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
																	+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
																	+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
																	+ "VALUES "
																	+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', 'Sick Leave', '"+sqlFormat.format(clslFromCal.getTime())+"', 'AM', '"+textAreaReason+"', 0, '"+unplannedLeave+"', '-', '-', '0')");
															slCount = slCount - 0.5;
															slLeaveLeft = slLeaveLeft - 0.5;
															if(clLeaveLeft > 0){
																statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
																		+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
																		+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
																		+ "VALUES "
																		+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', 'Casual Leave', '"+sqlFormat.format(clslFromCal.getTime())+"', 'PM', '"+textAreaReason+"', 0, '"+unplannedLeave+"', '-', '-', '0')");
																slCount = slCount - 0.5;
																clLeaveLeft = clLeaveLeft - 0.5;
															}else{
																statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
																		+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
																		+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
																		+ "VALUES "
																		+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', 'Casual Leave', '"+sqlFormat.format(clslFromCal.getTime())+"', 'PM', '"+textAreaReason+"', 1, 1, '-', '-', '0')");
																slCount = slCount - 0.5;
																lop = lop + 0.5;
															}
														}else{
															statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
																	+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
																	+ "`leave_status`, `leave_type`, `fddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `hddate`, `perdate`, `per_applied_for`) "
																	+ "VALUES "
																	+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', 'Sick Leave', '"+sqlFormat.format(clslFromCal.getTime())+"', '-', '"+textAreaReason+"', 0, '"+unplannedLeave+"', '-', '-', '0')");
															slCount = slCount - 1;
															slLeaveLeft = slLeaveLeft - 1;
														}
													}else if(clLeaveLeft > 0){
														if(clLeaveLeft == 0.5){
															statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
																	+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
																	+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
																	+ "VALUES "
																	+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', 'Casual Leave', '"+sqlFormat.format(clslFromCal.getTime())+"', 'AM', '"+textAreaReason+"', 0, '"+unplannedLeave+"', '-', '-', '0')");
															slCount = slCount - 0.5;
															clLeaveLeft = clLeaveLeft - 0.5;
															if(slCount >= 0.5){
																statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
																		+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
																		+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
																		+ "VALUES "
																		+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', 'Casual Leave', '"+sqlFormat.format(clslFromCal.getTime())+"', 'PM', '"+textAreaReason+"', 1, 1, '-', '-', '0')");
																slCount = slCount - 0.5;
																lop = lop + 0.5;
															}
														}else{
															statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
																	+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
																	+ "`leave_status`, `leave_type`, `fddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `hddate`, `perdate`, `per_applied_for`) "
																	+ "VALUES "
																	+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', 'Casual Leave', '"+sqlFormat.format(clslFromCal.getTime())+"', '-', '"+textAreaReason+"', 0, '"+unplannedLeave+"', '-', '-', '0')");
															slCount = slCount - 1;
															clLeaveLeft = clLeaveLeft - 1;
														}
													}else{
														if(slCount == 0.5){
															statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
																	+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
																	+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
																	+ "VALUES "
																	+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', 'Casual Leave', '"+sqlFormat.format(clslFromCal.getTime())+"', 'AM', '"+textAreaReason+"', 1, 1, '-', '-', '0')");
															slCount = slCount - 0.5;
															lop = lop + 0.5;
														}else{
															statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
																	+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
																	+ "`leave_status`, `leave_type`, `fddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `hddate`, `perdate`, `per_applied_for`) "
																	+ "VALUES "
																	+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(clslFromCal.getTime())+"', '"+yearFormat.format(clslFromCal.getTime())+"', '"+fullDay.format(clslFromCal.getTime())+"', '"+loginAs+"', 'Casual Leave', '"+sqlFormat.format(clslFromCal.getTime())+"', '-', '"+textAreaReason+"', 1, 1, '-', '-', '0')");
															slCount = slCount - 1;
															lop = lop + 1;
														}
													}
												}
												clslFromCal.add(Calendar.DATE, 1);
											}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sun")){
												clslFromCal.add(Calendar.DATE, 1);
											}else if(dayFormatLet.format(clslFromCal.getTime()).toString().equalsIgnoreCase("Sat")){
												clslFromCal.add(Calendar.DATE, 2);
											}
										}
										statement .executeUpdate("UPDATE `leaveapprovallogin`.`infoviewlogin` SET cl_left = "+clLeaveLeft+", sl_left = "+slLeaveLeft+", LOP = "+lop+" WHERE emp_id = '"+valueMap.get("emp_IdDB")+"'");
										JOptionPane.showMessageDialog(null, "Sucessfully Applied!!!\nBalance:\n       - CL Left = "+clLeaveLeft+"\n       - SL Left = "+slLeaveLeft+"\n       - LOP = "+lop);
									}
								}
							}else{
								JOptionPane.showMessageDialog(null, "Please Pick Valid Date!!!", "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
					labelValueSetting();
				}else if(selectedCmb.equals("Half-Day Leave")){
					String sessioncmb = cmbSession.getSelectedItem().toString();
					textAreaReason = leaveHDReason.getText();
					textAreaReason = textAreaReason.replace("'","''");
					hdDateFunc = (Date) HdDate.getDate();
					if(hdDateFunc == null || leaveHDReason.getText().equals("") || sessioncmb.equals("--Choose--")){
						lblCountHDLeave.setText("*All fields are mandatory");
						lblCountHDLeave.setForeground(Color.RED);
					}else{
						hdFromCal.setTime(hdDateFunc);
						resultSet = statement .executeQuery("select fddate, hddate, session from `leaveapprovallogin`.`applyleave` where emp_id ='"+valueMap.get("emp_IdDB")+"'");
						while(resultSet.next()){
							if(resultSet.getString("fddate").equals(sqlFormat.format(hdFromCal.getTime()))){
								appendLeaveDates.append(sqlFormat.format(hdFromCal.getTime())+"\n");
								break;
							}else if(resultSet.getString("hddate").equals(sqlFormat.format(hdFromCal.getTime())) && resultSet.getString("session").equals(sessioncmb)){
								appendLeaveDates.append(sqlFormat.format(hdFromCal.getTime())+"\n");
								break;
							}
						}
						hdFromCal.setTime(hdDateFunc);
						lblCountHDLeave.setText("");
						if(appendLeaveDates.length() > 0){
							JOptionPane.showMessageDialog(null, "Already Leave(s) has been applied for the following dates: \n"+appendLeaveDates, "Duplicate Dates", JOptionPane.ERROR_MESSAGE);
						}else{
							if(!(dayFormatLet.format(hdFromCal.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(hdFromCal.getTime()).toString().equalsIgnoreCase("Sun") || govHoliday.contains(sqlFormat.format(hdFromCal.getTime()).toString()))){
								if(casualrbtn.isSelected() == true){
									if(clLeaveLeft >= 0.5){
										if (JOptionPane.showConfirmDialog(null, "Are you sure want to apply for Half-Day as a Casual Leave ?", "Confirmation",
												JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
											statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
													+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
													+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
													+ "VALUES "
													+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(hdFromCal.getTime())+"', '"+yearFormat.format(hdFromCal.getTime())+"', '"+fullDay.format(hdFromCal.getTime())+"', '"+loginAs+"', 'Casual Leave', '"+sqlFormat.format(hdFromCal.getTime())+"', '"+sessioncmb+"', '"+textAreaReason+"', 0, '"+unplannedLeave+"', '-', '-', '0')");
											clLeaveLeft = clLeaveLeft - 0.5;
											statement .executeUpdate("UPDATE `leaveapprovallogin`.`infoviewlogin` SET cl_left = "+clLeaveLeft+", sl_left = "+slLeaveLeft+", LOP = "+lop+" WHERE emp_id = '"+valueMap.get("emp_IdDB")+"'");
											JOptionPane.showMessageDialog(null, "Sucessfully Applied!!!\nBalance:\n       - CL Left = "+clLeaveLeft+"\n       - SL Left = "+slLeaveLeft+"\n       - LOP = "+lop);
										}
									}else if(slLeaveLeft >= 0.5){
										if (JOptionPane.showConfirmDialog(null, "You don't have Casual Leave!!!\nAre you sure want to apply for Half-Day as a Sick Leave ?", "Confirmation",
												JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
											statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
													+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
													+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
													+ "VALUES "
													+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(hdFromCal.getTime())+"', '"+yearFormat.format(hdFromCal.getTime())+"', '"+fullDay.format(hdFromCal.getTime())+"', '"+loginAs+"', 'Sick Leave', '"+sqlFormat.format(hdFromCal.getTime())+"', '"+sessioncmb+"', '"+textAreaReason+"', 0, '"+unplannedLeave+"', '-', '-', '0')");
											slLeaveLeft = slLeaveLeft - 0.5;
											statement .executeUpdate("UPDATE `leaveapprovallogin`.`infoviewlogin` SET cl_left = "+clLeaveLeft+", sl_left = "+slLeaveLeft+", LOP = "+lop+" WHERE emp_id = '"+valueMap.get("emp_IdDB")+"'");
											JOptionPane.showMessageDialog(null, "Sucessfully Applied!!!\nBalance:\n       - CL Left = "+clLeaveLeft+"\n       - SL Left = "+slLeaveLeft+"\n       - LOP = "+lop);
										}
									}else{
										if (JOptionPane.showConfirmDialog(null, "You don't have Casual Leave and Sick Leave!!!\nAre you sure want to apply for Half-Day as LOP ?", "Confirmation",
												JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
											statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
													+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
													+ "VALUES "
													+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(hdFromCal.getTime())+"', '"+yearFormat.format(hdFromCal.getTime())+"', '"+fullDay.format(hdFromCal.getTime())+"', '"+loginAs+"', 'Casual Leave', '"+sqlFormat.format(hdFromCal.getTime())+"', '"+sessioncmb+"', '"+textAreaReason+"', 1, 1, '-', '-', '0')");
											lop = lop + 0.5;
											statement .executeUpdate("UPDATE `leaveapprovallogin`.`infoviewlogin` SET cl_left = "+clLeaveLeft+", sl_left = "+slLeaveLeft+", LOP = "+lop+" WHERE emp_id = '"+valueMap.get("emp_IdDB")+"'");
											JOptionPane.showMessageDialog(null, "Sucessfully Applied!!!\nBalance:\n       - CL Left = "+clLeaveLeft+"\n       - SL Left = "+slLeaveLeft+"\n       - LOP = "+lop);
										}
									}
								}else{
									if(slLeaveLeft >= 0.5){
										if (JOptionPane.showConfirmDialog(null, "Are you sure want to apply for Half-Day as a Sick Leave ?", "Confirmation",
												JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
											statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
													+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
													+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
													+ "VALUES "
													+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(hdFromCal.getTime())+"', '"+yearFormat.format(hdFromCal.getTime())+"', '"+fullDay.format(hdFromCal.getTime())+"', '"+loginAs+"', 'Sick Leave', '"+sqlFormat.format(hdFromCal.getTime())+"', '"+sessioncmb+"', '"+textAreaReason+"', 0, '"+unplannedLeave+"', '-', '-', '0')");
											slLeaveLeft = slLeaveLeft - 0.5;
											statement .executeUpdate("UPDATE `leaveapprovallogin`.`infoviewlogin` SET cl_left = "+clLeaveLeft+", sl_left = "+slLeaveLeft+", LOP = "+lop+" WHERE emp_id = '"+valueMap.get("emp_IdDB")+"'");
											JOptionPane.showMessageDialog(null, "Sucessfully Applied!!!\nBalance:\n       - CL Left = "+clLeaveLeft+"\n       - SL Left = "+slLeaveLeft+"\n       - LOP = "+lop);
										}
									}else if(clLeaveLeft >= 0.5){
										if (JOptionPane.showConfirmDialog(null, "You don't have Casual Leave!!!\nAre you sure want to apply for Half-Day as a Casual Leave ?", "Confirmation",
												JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
											statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
													+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
													+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
													+ "VALUES "
													+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(hdFromCal.getTime())+"', '"+yearFormat.format(hdFromCal.getTime())+"', '"+fullDay.format(hdFromCal.getTime())+"', '"+loginAs+"', 'Casual Leave', '"+sqlFormat.format(hdFromCal.getTime())+"', '"+sessioncmb+"', '"+textAreaReason+"', 0, '"+unplannedLeave+"', '-', '-', '0')");
											clLeaveLeft = clLeaveLeft - 0.5;
											statement .executeUpdate("UPDATE `leaveapprovallogin`.`infoviewlogin` SET cl_left = "+clLeaveLeft+", sl_left = "+slLeaveLeft+", LOP = "+lop+" WHERE emp_id = '"+valueMap.get("emp_IdDB")+"'");
											JOptionPane.showMessageDialog(null, "Sucessfully Applied!!!\nBalance:\n       - CL Left = "+clLeaveLeft+"\n       - SL Left = "+slLeaveLeft+"\n       - LOP = "+lop);
										}
									}else{
										if (JOptionPane.showConfirmDialog(null, "You don't have Casual Leave and Sick Leave!!!\nAre you sure want to apply for Half-Day as LOP ?", "Confirmation",
												JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
											statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
													+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
													+ "VALUES "
													+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(hdFromCal.getTime())+"', '"+yearFormat.format(hdFromCal.getTime())+"', '"+fullDay.format(hdFromCal.getTime())+"', '"+loginAs+"', 'Casual Leave', '"+sqlFormat.format(hdFromCal.getTime())+"', '"+sessioncmb+"', '"+textAreaReason+"', 1, 1, '-', '-', '0')");
											lop = lop + 0.5;
											statement .executeUpdate("UPDATE `leaveapprovallogin`.`infoviewlogin` SET cl_left = "+clLeaveLeft+", sl_left = "+slLeaveLeft+", LOP = "+lop+" WHERE emp_id = '"+valueMap.get("emp_IdDB")+"'");
											JOptionPane.showMessageDialog(null, "Sucessfully Applied!!!\nBalance:\n       - CL Left = "+clLeaveLeft+"\n       - SL Left = "+slLeaveLeft+"\n       - LOP = "+lop);
										}
									}
								}
							}else{
								lblCountHDLeave.setText("*Please Pick Valid Date");
								lblCountHDLeave.setForeground(Color.RED);
								JOptionPane.showMessageDialog(null, "Please Pick Valid Date!!!", "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
					labelValueSetting();
				}else if(selectedCmb.equals("Permission")){
					spinnerValue = (Double) spinner.getValue();
					if(PerDate.getDate() != null && !spinnerValue.toString().equals("0.0")){
						perDateFunc = sqlFormat.parse(sqlFormat.format(PerDate.getDate()));
						if(perDateFunc == null || spinnerValue.toString().equals("0.0")){
							lblPermissionCount.setText("*All fields are mandatory");
							lblPermissionCount.setForeground(Color.RED);
						}else{
							perFromCal.setTime(perDateFunc);
							resultSet = statement .executeQuery("select count(per_applied_for) from applyleave where emp_id = '"+valueMap.get("emp_IdDB")+"' and leave_type = 'Permission' and (month = '"+fullMonth.format(perFromCal.getTime())+"' and year = '"+yearFormat.format(perFromCal.getTime())+"')");
							if(resultSet.next()){
								perCount = resultSet.getInt(1);
							}
							if(perCount == 0){
								statement .executeUpdate("UPDATE `leaveapprovallogin`.`infoviewlogin` SET per_left = '3' WHERE emp_id = '"+valueMap.get("emp_IdDB")+"'");
							}
							resultSet = statement .executeQuery("select fddate, hddate, perdate from `leaveapprovallogin`.`applyleave` where emp_id ='"+valueMap.get("emp_IdDB")+"'");
							while(resultSet.next()){
								if(resultSet.getString("fddate").equals(sqlFormat.format(perFromCal.getTime())) || resultSet.getString("perdate").equals(sqlFormat.format(perFromCal.getTime()))){
									appendLeaveDates.append(sqlFormat.format(perFromCal.getTime())+"\n");
									break;
								}
							}
							lblCountHDLeave.setText("");
							if(perCount == 0){
								resultSet = statement .executeQuery("select per_left from `leaveapprovallogin`.`infoviewlogin` where emp_id = '"+valueMap.get("emp_IdDB")+"'");
								if(resultSet.next()){
									perLeft = resultSet.getDouble(1);
								}
							}
							if(appendLeaveDates.length() > 0){
								JOptionPane.showMessageDialog(null, "Already Leave(s) has been applied for the following dates: \n"+appendLeaveDates, "Duplicate Dates", JOptionPane.ERROR_MESSAGE);
							}else{
								lblPermissionCount.setText("");
								if(perCount < 3){
									if(spinnerValue.equals(perLeft)){
										if(!(dayFormatLet.format(perFromCal.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(perFromCal.getTime()).toString().equalsIgnoreCase("Sun") || govHoliday.contains(sqlFormat.format(perFromCal.getTime()).toString()))){
											if (JOptionPane.showConfirmDialog(null, "If you applied now then You won't have permissions for this month.\nAre you sure want to apply for "+spinnerValue+" hour(s) Permission ?", "Confirmation",
													JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
												if(!(dayFormatLet.format(perFromCal.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(perFromCal.getTime()).toString().equalsIgnoreCase("Sun") || govHoliday.contains(sqlFormat.format(perFromCal.getTime()).toString()))){
													statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
															+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
															+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
															+ "VALUES "
															+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(perFromCal.getTime())+"', '"+yearFormat.format(perFromCal.getTime())+"', '"+fullDay.format(perFromCal.getTime())
															+"', '"+loginAs+"', '"+selectedCmb+"', '-', '-', '-', 0, '"+unplannedLeave+"', '-', '"+sqlFormat.format(perFromCal.getTime())+"', '"+spinnerValue+"')");
													perLeft = perLeft - spinnerValue;
													statement .executeUpdate("UPDATE `leaveapprovallogin`.`infoviewlogin` SET per_left = '"+perLeft+"' WHERE emp_id = '"+valueMap.get("emp_IdDB")+"'");
												}else{
													lblPermissionCount.setText("*Check entered date");
													lblPermissionCount.setForeground(Color.RED);
												}
												JOptionPane.showMessageDialog(null, (3 - perCount - 1)+" Permission(s) left");
											}
										}else{
											JOptionPane.showMessageDialog(null, "Please Pick valid date !!", "No Permission", JOptionPane.ERROR_MESSAGE);
										}
									}else if(spinnerValue < perLeft){
										if(!(dayFormatLet.format(perFromCal.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(perFromCal.getTime()).toString().equalsIgnoreCase("Sun") || govHoliday.contains(sqlFormat.format(perFromCal.getTime()).toString()))){
											if (JOptionPane.showConfirmDialog(null, "Are you sure want to apply for "+spinnerValue+" hour(s) Permission ?", "Confirmation",
													JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
												if(!(dayFormatLet.format(perFromCal.getTime()).toString().equalsIgnoreCase("Sat") || dayFormatLet.format(perFromCal.getTime()).toString().equalsIgnoreCase("Sun") || govHoliday.contains(sqlFormat.format(perFromCal.getTime()).toString()))){
													statement .executeUpdate("INSERT INTO `leaveapprovallogin`.`applyleave` "
															+ "(`emp_id`, `emp_name`, `tl`, `pmo`, `month`, `year`, `day`, "
															+ "`leave_status`, `leave_type`, `hddate`, `session`, `reason`, `lop_date`, `unplanned_leave`, `fddate`, `perdate`, `per_applied_for`) "
															+ "VALUES "
															+ "('"+valueMap.get("emp_IdDB")+"', '"+valueMap.get("empName")+"', '"+valueMap.get("TL")+"', '"+valueMap.get("PMO")+"', '"+fullMonth.format(perFromCal.getTime())+"', '"+yearFormat.format(perFromCal.getTime())+"', '"+fullDay.format(perFromCal.getTime())
															+"', '"+loginAs+"', '"+selectedCmb+"', '-', '-', '-', 0, '"+unplannedLeave+"', '-', '"+sqlFormat.format(perFromCal.getTime())+"', '"+spinnerValue+"')");
													perLeft = perLeft - spinnerValue;
													statement .executeUpdate("UPDATE `leaveapprovallogin`.`infoviewlogin` SET per_left = '"+perLeft+"' WHERE emp_id = '"+valueMap.get("emp_IdDB")+"'");
												}else{
													lblPermissionCount.setText("*Check entered date");
													lblPermissionCount.setForeground(Color.RED);
												}
												JOptionPane.showMessageDialog(null, (3 - perCount - 1)+" Permission(s) left");
											}
										}else{
											JOptionPane.showMessageDialog(null, "Please Pick valid date !!", "No Permission", JOptionPane.ERROR_MESSAGE);
										}
									}else if(spinnerValue > perLeft){
										JOptionPane.showMessageDialog(null, "You Don't have permission for this month.\nYou have only "+perLeft+" hour(s). ", "No Permissions", JOptionPane.ERROR_MESSAGE);
									}
									labelValueSetting();
								}else{
									JOptionPane.showMessageDialog(null, "Three times used!!", "No Permissions", JOptionPane.ERROR_MESSAGE);
								}
							}
						}
					}else{
						JOptionPane.showMessageDialog(null, "Mandatory Fields are missing !!", "No Permission", JOptionPane.ERROR_MESSAGE);
					}
				}
			} catch(ClassNotFoundException e1){
				JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
			} catch(SQLException e1){
				JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
			} catch(ParseException e1){
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
					} catch (SQLException e1) {
						e1.printStackTrace();
					} 
			}
		}else if(actionCommand.equals("signout")){
			empleave.setVisible(false);
			new LoginFunctionality();
		}else if(actionCommand.equals("Back")){
			connection = null;
			statement = null;
			resultSet = null;
			try{
				Class.forName("com.mysql.jdbc.Driver");
				connection = (Connection) DriverManager.getConnection("jdbc:Mysql://localhost/leaveApprovalLogin","root","root");
				statement = (Statement) connection.createStatement();
				if(valueMap.get("back").equals("1")){
					resultSet = statement.executeQuery("select * from infoviewLogin where emp_Name = '"+valueMap.get("TL")+"'");
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
					empleave.setVisible(false);
					new TlFormFunctionality(valueMap);
				}else if(valueMap.get("back").equals("2")){
					resultSet = statement.executeQuery("select * from infoviewLogin where emp_Name = '"+valueMap.get("PMO")+"'");
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
					valueMap.put("levelCheck", 2);
					valueMap.put("back", 2);
					valueMap.put("loginAs", 2);
					empleave.setVisible(false);
					new PmoApprovalFunctionality(valueMap);
				}
			}catch(ClassNotFoundException e1){
				JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
			}catch(SQLException e1){
				JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
			} finally{
				if(statement != null)
					try {
						statement.close();
					} catch (SQLException e2) {
						e2.printStackTrace();
					} 
				if(connection != null)
					try {
						connection.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					} 
			}
		}else if(actionCommand.equals("summary")){
			empleave.setVisible(false);
			valueMap.put("screenName", "emp");
			new LeaveSummaryFunctionality(valueMap);
		}

	}
}
