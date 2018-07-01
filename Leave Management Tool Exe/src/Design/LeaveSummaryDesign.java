package Design;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
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

@SuppressWarnings("rawtypes")
public class LeaveSummaryDesign {
	public static JFrame summaryFrame;
	public static JPanel pnlMain;
	public static JLabel lblSummaryDisplay;
	public static JLabel lblTlList;
	public static JComboBox cmbTlList;
	public static JLabel lblTeamList;
	public static JComboBox cmbTeamList;
	public static JLabel lblLeaveType;
	public static JComboBox cmbStatus;

	public static JButton btnLoad;
	public static JButton btnBack;
	public static JButton btnAllLeaves;
	public static JButton btnCheckStatus;
	public static JButton btnCancelLeaves;

	public static DefaultTableModel model;
	public static JTable table;
	public static JScrollPane scrollpane;
	Map<String, Object> valueMap;

	@SuppressWarnings("unchecked")
	public LeaveSummaryDesign(Map<String, Object> valueMap){
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

		summaryFrame = new JFrame("Leave Summary");

		summaryFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int confirmed = JOptionPane.showConfirmDialog(null, 
						"Are you sure you want to exit this program?", "Exit",
						JOptionPane.YES_NO_OPTION);
				if (confirmed == JOptionPane.YES_OPTION) {
					summaryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}else{
					summaryFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				}
			}
		});

		summaryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		pnlMain = new JPanel();
		lblSummaryDisplay = new JLabel("<html><u>Leave Summary</u></html>", SwingConstants.CENTER);

		lblTlList = new JLabel("TL List : ", SwingConstants.CENTER);
		lblTeamList = new JLabel("Team Members : ", SwingConstants.CENTER);

		lblLeaveType = new JLabel("Leave Type : ", SwingConstants.CENTER);
		cmbStatus = new JComboBox();
		cmbStatus.addItem("Casual Leave");
		cmbStatus.addItem("Sick Leave");
		cmbStatus.addItem("Permission");

		cmbTeamList = new JComboBox();
		cmbTlList = new JComboBox();

		btnLoad = new JButton("Load");
		btnBack = new JButton("Back");
		btnAllLeaves = new JButton("All Leaves");
		btnCheckStatus = new JButton("Check Status");
		btnCancelLeaves = new JButton("Cancel Leaves");

		this.heightWidth();
	}

	private void heightWidth() {
		summaryFrame.setResizable(false);
		summaryFrame.setVisible(true);

		summaryFrame.setBounds(0, 0, 900, 700);
		summaryFrame.setLocationRelativeTo(null);

		pnlMain.setLayout(null);

		lblSummaryDisplay.setBounds(0, 30, 900, 75);
		lblSummaryDisplay.setFont(new Font("calibri light", Font.BOLD, 20));

		if(valueMap.get("level").equals(0)){
			lblLeaveType.setBounds(283, 151, 110, 40);
			cmbStatus.setBounds(397, 156, 110, 30);
			btnLoad.setBounds(550, 153, 110, 35);
		}else if(valueMap.get("level").equals(1)){
			lblTeamList.setBounds(122, 151, 120, 40);
			lblTeamList.setFont(new Font("calibri light", Font.PLAIN, 16));
			cmbTeamList.setBounds(251, 156, 130, 30);

			lblLeaveType.setBounds(416, 151, 110, 40);
			cmbStatus.setBounds(525, 156, 110, 30);
			btnLoad.setBounds(680, 153, 110, 35);
		}else if(valueMap.get("level").equals(2)){
			lblTlList.setBounds(7, 151, 100, 40);
			lblTlList.setFont(new Font("calibri light", Font.PLAIN, 16));
			cmbTlList.setBounds(90, 156, 130, 30);

			lblTeamList.setBounds(243, 151, 120, 40);
			lblTeamList.setFont(new Font("calibri light", Font.PLAIN, 16));
			cmbTeamList.setBounds(368, 156, 130, 30);

			lblLeaveType.setBounds(501, 151, 110, 40);
			cmbStatus.setBounds(605, 156, 110, 30);
			btnLoad.setBounds(750, 153, 110, 35);
		}
		lblLeaveType.setFont(new Font("calibri light", Font.PLAIN, 16));


		btnBack.setBounds(625, 500, 125, 40);
		btnAllLeaves.setBounds(325, 500, 125, 40);
		btnCheckStatus.setBounds(475, 500, 125, 40);
		btnCancelLeaves.setBounds(175, 500, 125, 40);

		pnlMain.add(lblSummaryDisplay);

		pnlMain.add(lblTlList);
		pnlMain.add(cmbTlList);

		pnlMain.add(lblTeamList);
		pnlMain.add(cmbTeamList);

		pnlMain.add(lblLeaveType);
		pnlMain.add(cmbStatus);

		pnlMain.add(btnLoad);
		pnlMain.add(btnBack);
		pnlMain.add(btnAllLeaves);
		pnlMain.add(btnCheckStatus);
		pnlMain.add(btnCancelLeaves);

		summaryFrame.add(pnlMain);
	}

	@SuppressWarnings("serial")
	public void loadTable(final String leaveTypeParam){
		final String leaveType = leaveTypeParam;
		table = new JTable(){
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component comp = super.prepareRenderer(renderer, row, column);
				if(table.getColumnCount() == 8 || table.getColumnCount() == 7 || table.getColumnCount() == 9){
					String columnName = table.getColumnName(6);
					if(table.getColumnCount() != 7){
						boolean lop = (Boolean) getModel().getValueAt(row, 6);
						if(columnName.equals("Lop")){
							if (lop) {
								comp.setBackground(new Color(255, 179, 179));
							}else{
								comp.setBackground(row % 2 == 0 ? new Color(168, 198, 255) : new Color(209, 209, 209));
							}
						}else{
							comp.setBackground(row % 2 == 0 ? new Color(168, 198, 255) : new Color(209, 209, 209));
						}
					}else{
						comp.setBackground(row % 2 == 0 ? new Color(168, 198, 255) : new Color(209, 209, 209));
					}
				}else if(table.getColumnCount() == 6){
					comp.setBackground(row % 2 == 0 ? new Color(168, 198, 255) : new Color(209, 209, 209)); 
				}
				return comp;
			}
		};
		scrollpane = new JScrollPane(table);
		scrollpane.setBounds(0, 250, 895, 183);
		pnlMain.add(scrollpane);
		((JComponent) table.getDefaultRenderer(Boolean.class)).setOpaque(true);
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
							return String.class;
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
