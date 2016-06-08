package window;

import java.awt.BorderLayout;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class Admin extends JFrame implements MouseListener {

	private JPopupMenu popupMenu = new JPopupMenu();

	JMenuItem[] menuItems = { // 所有的menuitem
			new JMenuItem("添加教师"), // 0
			new JMenuItem("添加课程"), // 1
			new JMenuItem("添加班级"), // 2
			new JMenuItem("添加学生"), // 3
			new JMenuItem("删除教师"), // 4
			new JMenuItem("删除课程"), // 5
			new JMenuItem("删除班级"), // 6
			new JMenuItem("删除学生"), // 7
			new JMenuItem("刷新"), // 8
			new JMenuItem("保存"), // 9
	};

	private JPanel contentPane = new JPanel();

	private JTable tables[] = new JTable[8];

	// 0 table_teacher
	// 1 table_course
	// 2 table_class
	// 3 table_student
	// 4 table_course1
	// 5 table_class1

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());// 设置为系统风格
					new Admin();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Admin() {
		setIconImage(new ImageIcon("icon_admin.png").getImage());
		for (int i = 0; i < menuItems.length; i++) {
			popupMenu.add(menuItems[i]);
			menuItems[i].addMouseListener(this);
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 755, 375);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		JSplitPane splitPanes[] = new JSplitPane[4];
		for (int i = 0; i < 4; i++) {
			splitPanes[i] = new JSplitPane() {
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					ImageIcon ii = new ImageIcon("image_background.jpg");
					g.drawImage(ii.getImage(), 0, 0, getWidth(), getHeight(), ii.getImageObserver());
				}
			};
			splitPanes[i].setDividerLocation(getWidth() / 2);
		}

		tabbedPane.addTab("教师-课程(增删改分配)", null, splitPanes[0], null);
		tabbedPane.addTab("班级-学生(增删改分配)", null, splitPanes[1], null);
		tabbedPane.addTab("课程-班级(分配)", null, splitPanes[2], null);
		tabbedPane.addTab("教师-班级(分配)", null, splitPanes[3], null);

		JScrollPane scrollPanes[] = new JScrollPane[8];

		for (int i = 0; i < 8; i++) {
			scrollPanes[i] = new JScrollPane();
			scrollPanes[i].setOpaque(false);
			scrollPanes[i].getViewport().setOpaque(false);
		}

		for (int i = 0; i < 4; i++) {
			splitPanes[i].setLeftComponent(scrollPanes[i * 2]);
			splitPanes[i].setRightComponent(scrollPanes[i * 2 + 1]);
		}

		for (int i = 0; i < 8; i++) {
			tables[i] = new JTable();
			scrollPanes[i].setViewportView(tables[i]);
		}

		refreshTeacherTable();
		refreshCourseTable(null);
		refreshClassTable();
		refreshStudentTable(null);
		refreshCourse1Table();
		refreshClass1Table(null);
		refreshTeacher1Table();
		refreshClass2Table(null);

		for (JTable table : tables) {
			table.addMouseListener(this);
		}

		setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		setTitle("");

		for (int i = 0; i < 8; i++) {
			if (e.getSource() == tables[i] && e.getButton() == e.BUTTON3) {
				int row = tables[i].rowAtPoint(e.getPoint());
				tables[i].setRowSelectionInterval(row, row);
				popupMenu.show(tables[i], e.getX(), e.getY());

				break;
			}
		}

		// 一点左边表格就刷新右边表格
		if (e.getSource() == tables[0] && tables[0].getSelectedRow() != -1) {
			refreshCourseTable(tables[0].getValueAt(tables[0].getSelectedRow(), 0));
		}
		if (e.getSource() == tables[2] && tables[2].getSelectedRow() != -1) {
			refreshStudentTable(tables[2].getValueAt(tables[2].getSelectedRow(), 0));
		}
		if (e.getSource() == tables[4] && tables[4].getSelectedRow() != -1) {
			refreshClass1Table(tables[4].getValueAt(tables[4].getSelectedRow(), 0));
		}
		if (e.getSource() == tables[6] && tables[6].getSelectedRow() != -1) {
			refreshClass2Table(tables[6].getValueAt(tables[6].getSelectedRow(), 0));
		}

		// 一点右边的表格就保存右边表格的选择
		for (int i = 1; i < 8; i += 2) {
			int row = tables[i].getSelectedRow();
			if (row == -1)
				continue;
			if (i == 1)
				Sql.selectTCo(tables[0], tables[1]);
			if (i == 3)
				Sql.selectClS(tables[2], tables[3]);
			if (i == 5)
				Sql.selectCoCl(tables[4], tables[5]);
			if (i == 7)
				Sql.selectTCl(tables[6], tables[7]);
		}

		for (int i = 0; i < menuItems.length; i++) {// 如果菜单被点就刷新全部
			if (e.getSource() == menuItems[i]) {
				if (i == 0)
					Sql.uploadTeacher();
				if (i == 1)
					Sql.uploadCourse();
				if (i == 2)
					Sql.uploadClass();
				if (i == 3)
					Sql.uploadStudent();
				if (i == 4)
					Sql.deleteTeacher(tables[0]);
				if (i == 5)
					Sql.deleteCourse(tables[1]);
				if (i == 6)
					Sql.deleteClass(tables[2]);
				if (i == 7)
					Sql.deleteStudent(tables[3]);
				if (i == 9) {
					Sql.updateTeacher(tables[0]);
					Sql.updateCourse(tables[1]);
					Sql.updateClass(tables[2]);
					Sql.updateStudent(tables[3]);
					setTitle("保存成功");
				}
				refreshAll();
				break;
			}
		}
	}

	void refreshAll() {
		refreshTeacherTable();
		refreshCourseTable(null);
		refreshCourse1Table();
		refreshStudentTable(null);
		refreshClassTable();
		refreshClass1Table(null);
		refreshTeacher1Table();
		refreshClass2Table(null);
	}

	void refreshTeacherTable() {
		tables[0].setModel(new DefaultTableModel(Sql.allTeacher(), new String[] { "工号", "密码", "姓名" }) {
			Class[] columnTypes = new Class[] { Integer.class, String.class, String.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			public boolean isCellEditable(int row, int column) {// 返回true表示能编辑，false表示不能编辑
				if (column == 0)
					return false;
				return true;
			}
		});
	}

	void refreshTeacher1Table() {
		tables[6].setModel(new DefaultTableModel(Sql.allTeacher1(), new String[] { "工号", "姓名" }) {
			Class[] columnTypes = new Class[] { Integer.class, String.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			public boolean isCellEditable(int row, int column) {// 返回true表示能编辑，false表示不能编辑
				return false;
			}
		});
	}

	void refreshCourseTable(Object tid) {
		// System.out.println("被选教师="+tid);
		tables[1].setModel(new DefaultTableModel(Sql.allCourse(tid), new String[] { "课程号", "课程", "选择" }) {
			Class[] columnTypes = new Class[] { Integer.class, String.class, Boolean.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			public boolean isCellEditable(int row, int column) {// 返回true表示能编辑，false表示不能编辑
				if (column == 0 || tables[0].getSelectedRow() == -1 && column == 2) // 左边表格没有选择，右边的就不能打勾
					return false;

				return true;
			}
		});
	}

	void refreshCourse1Table() {
		tables[4].setModel(new DefaultTableModel(Sql.allCourse(), new String[] { "课程号", "课程" }) {
			Class[] columnTypes = new Class[] { Integer.class, String.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			public boolean isCellEditable(int row, int column) {// 返回true表示能编辑，false表示不能编辑
				return false;
			}
		});
	}

	void refreshStudentTable(Object clid) {
		tables[3].setModel(
				new DefaultTableModel(Sql.allStudentFromClass(clid), new String[] { "学号", "密码", "姓名", "选择" }) {
					Class[] columnTypes = new Class[] { Integer.class, String.class, String.class, Boolean.class };

					public Class getColumnClass(int columnIndex) {
						return columnTypes[columnIndex];
					}

					public boolean isCellEditable(int row, int column) {// 返回true表示能编辑，false表示不能编辑
						if (column == 0 || tables[2].getSelectedRow() == -1 && column == 3) // 左边没点，右边就不能打勾
							return false;
						return true;
					}
				});
	}

	void refreshClassTable() {
		tables[2].setModel(new DefaultTableModel(Sql.allClass(), new String[] { "班级号", "班级" }) {
			Class[] columnTypes = new Class[] { Integer.class, String.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			public boolean isCellEditable(int row, int column) {// 返回true表示能编辑，false表示不能编辑
				if (column == 0)
					return false;
				return true;
			}
		});
	}

	void refreshClass1Table(Object coid) {
		tables[5].setModel(new DefaultTableModel(Sql.allClass(coid), new String[] { "班级号", "班级", "选择" }) {
			Class[] columnTypes = new Class[] { Integer.class, String.class, Boolean.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			public boolean isCellEditable(int row, int column) {// 返回true表示能编辑，false表示不能编辑
				if (tables[4].getSelectedRow() == -1)
					return false;
				if (column == 2)
					return true;
				return false;
			}
		});
	}

	void refreshClass2Table(Object tid) {
		tables[7].setModel(new DefaultTableModel(Sql.allClass2(tid), new String[] { "班级号", "班级", "选择" }) {
			Class[] columnTypes = new Class[] { Integer.class, String.class, Boolean.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			public boolean isCellEditable(int row, int column) {// 返回true表示能编辑，false表示不能编辑
				if (tables[6].getSelectedRow() == -1)
					return false;
				if (column == 2)
					return true;
				return false;
			}
		});
	}

}
