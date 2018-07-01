package Design;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.toedter.calendar.JDateChooser;

@SuppressWarnings("rawtypes")
public class SearchAndExportDesign {
	public static JFrame filterFrame;
	public static JPanel pnlMain;
	public static JLabel lblDisplayInfo;
	public static JLabel lblId;
	public static JTextField txtEmpId;
	public static JLabel lblName;
	public static JTextField txtEmpName;
	public static JLabel lblTl;
	public static JTextField txtTl;
	public static JLabel lblYear;
	public static JComboBox cmbYear;
	public static JLabel lblMonth;
	public static JComboBox cmbMonth;
	public static JCheckBox yearMonth;
	public static JLabel lblFromDate;
	public static JDateChooser fromDate;
	public static JLabel lblMandatoryFrom;
	public static JLabel lblToDate;
	public static JDateChooser toDate;
	public static JLabel lblLeaveType;
	public static JComboBox cmbLeaveType;
	public static JLabel lblMandatoryLeaveType;
	public static JButton btnLop;
	public static JButton btnExport;
	public static JButton btnBack;
	public static JTable table;
	public static JScrollPane scrollpane;
	public static DefaultTableModel model;
	Map<String, Object> valueMap;
	
	public SearchAndExportDesign(Map<String, Object> valueMap){
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
		filterFrame = new JFrame("Search Employee Detail");
		pnlMain = new JPanel();
		
		lblDisplayInfo = new JLabel("Search", SwingConstants.CENTER);
		
		lblId = new JLabel("Id : ", SwingConstants.CENTER);
		txtEmpId = new JTextField("");
		
		lblName = new JLabel("Name : ", SwingConstants.CENTER);
		txtEmpName = new JTextField("");
		
		lblTl = new JLabel("Tl : ", SwingConstants.CENTER);
		txtTl = new JTextField("");
		
		lblYear = new JLabel("Year : ", SwingConstants.CENTER);
		cmbYear = new JComboBox();
		cmbYear.setEnabled(false);
		lblMonth = new JLabel("Month : ", SwingConstants.CENTER);
		cmbMonth = new JComboBox();
		yearMonth = new JCheckBox("No Year and Month", true);
		cmbMonth.setEnabled(false);
		lblFromDate = new JLabel("From Date : ", SwingConstants.CENTER);
		fromDate = new JDateChooser();
		fromDate.setDateFormatString("yyyy-MM-dd");
		lblMandatoryFrom = new JLabel("*", SwingConstants.CENTER);
		lblMandatoryFrom.setForeground(Color.RED);
		lblToDate = new JLabel("To Date : ", SwingConstants.CENTER);
		toDate = new JDateChooser();
		toDate.setDateFormatString("yyyy-MM-dd");
		lblLeaveType = new JLabel("Leave Type : ", SwingConstants.CENTER);
		cmbLeaveType = new JComboBox();
		lblMandatoryLeaveType = new JLabel("*", SwingConstants.CENTER);
		lblMandatoryLeaveType.setForeground(Color.RED);
		btnLop = new JButton("LOP");
		btnExport = new JButton("Export");
		btnBack = new JButton("Back");
		filterFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int confirmed = JOptionPane.showConfirmDialog(null, 
						"Are you sure you want to exit this program?", "Exit",
						JOptionPane.YES_NO_OPTION);

				if (confirmed == JOptionPane.YES_OPTION) {
					filterFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}else{
					filterFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				}
			}
		});
		filterFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		loadTable();
		
		heightWidth();
	}
	
	public void heightWidth(){
		filterFrame.setResizable(false);
		filterFrame.setVisible(true);
		filterFrame.setBounds(0, 0, 900, 700);
		filterFrame.setLocationRelativeTo(null);
		
		pnlMain.setLayout(null);
		
		lblDisplayInfo.setBounds(0, 30, 900, 50);
		lblDisplayInfo.setFont(new Font("calibri light", Font.BOLD, 20));
		
		lblId.setBounds(315, 100, 120, 30);
		lblId.setFont(new Font("calibri light", Font.PLAIN, 16));
		txtEmpId.setBounds(405, 102, 125, 25);
		
		lblName.setBounds(301, 155, 120, 30);
		lblName.setFont(new Font("calibri light", Font.PLAIN, 16));
		txtEmpName.setBounds(405, 157, 125, 25);
		
		lblTl.setBounds(315, 205, 120, 30);
		lblTl.setFont(new Font("calibri light", Font.PLAIN, 16));
		txtTl.setBounds(405, 207, 125, 25);
		
		lblYear.setBounds(306, 255, 120, 30);
		lblYear.setFont(new Font("calibri light", Font.PLAIN, 16));
		cmbYear.setBounds(405, 257, 125, 25);
		
		lblMonth.setBounds(299, 305, 120, 30);
		lblMonth.setFont(new Font("calibri light", Font.PLAIN, 16));
		cmbMonth.setBounds(405, 307, 125, 25);
		
		yearMonth.setBounds(550, 310, 150, 25);
		
		lblFromDate.setBounds(286, 355, 120, 30);
		lblFromDate.setFont(new Font("calibri light", Font.PLAIN, 16));
		fromDate.setBounds(405, 357, 110, 30);
		
		lblMandatoryFrom.setBounds(517, 354, 20, 30);
		lblMandatoryFrom.setFont(new Font("calibri light", Font.BOLD, 25));
		
		lblToDate.setBounds(295, 405, 120, 30);
		lblToDate.setFont(new Font("calibri light", Font.PLAIN, 16));
		toDate.setBounds(405, 407, 110, 30);
		
		lblLeaveType.setBounds(284, 455, 120, 30);
		lblLeaveType.setFont(new Font("calibri light", Font.PLAIN, 16));
		cmbLeaveType.setBounds(405, 457, 125, 25);
		
		lblMandatoryLeaveType.setBounds(533, 454, 20, 30);
		lblMandatoryLeaveType.setFont(new Font("calibri light", Font.BOLD, 25));
		
		btnLop.setBounds(250, 520, 125, 40);
		btnExport.setBounds(400, 520, 125, 40);
		btnBack.setBounds(550, 520, 125, 40);
		
		pnlMain.add(lblDisplayInfo);
		pnlMain.add(lblId);
		pnlMain.add(txtEmpId);
		pnlMain.add(lblName);
		pnlMain.add(txtEmpName);
		pnlMain.add(lblTl);
		pnlMain.add(txtTl);
		pnlMain.add(lblYear);
		pnlMain.add(cmbYear);
		pnlMain.add(lblMonth);
		pnlMain.add(cmbMonth);
		pnlMain.add(yearMonth);
		pnlMain.add(lblFromDate);
		pnlMain.add(fromDate);
		pnlMain.add(lblMandatoryFrom);
		pnlMain.add(lblToDate);
		pnlMain.add(toDate);
		pnlMain.add(lblLeaveType);
		pnlMain.add(cmbLeaveType);
		pnlMain.add(lblMandatoryLeaveType);
		pnlMain.add(btnLop);
		pnlMain.add(btnExport);
		pnlMain.add(btnBack);
		
		filterFrame.add(pnlMain);
	}
	
	@SuppressWarnings("serial")
	public void loadTable(){
		
		scrollpane = new JScrollPane(table);
		scrollpane.setBounds(570, 75, 300, 183);
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
}
