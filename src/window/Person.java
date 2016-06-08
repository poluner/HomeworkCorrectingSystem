package window;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.border.EmptyBorder;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.ListSelectionModel;

public class Person extends JFrame implements MouseListener {
	boolean isTeacher;
	int id;
	private int startx = 0;

	private JPopupMenu popupMenu_QuestionSet = new JPopupMenu();

	JMenuItem[] menuItems = { // 所有的menuitem
			new JMenuItem("上传题目"), // 0
			new JMenuItem("删除题目"), // 1
			new JMenuItem("刷新"), // 2
	};

	private JPanel contentPane_view = new JPanel();
	private JPanel panel_advanceChart = new JPanel() {
		public void paint(Graphics g0) {
			super.paint(g0);
			setBackground(Color.white);

			Point.basey = panel_advanceChart.getHeight() - 50;
			Vector<Point>[] vpoints = Sql.allPoint(table_course, table_scl);
			if (vpoints == null)
				return;
			scrollBar_1.setMaximum(vpoints[0].size());

			int rows[] = table_scl.getSelectedRows();

			Graphics2D g = (Graphics2D) g0;
			g.setStroke(new BasicStroke(3.0f));
			g.setFont(new Font("GBK", Font.PLAIN, 20));

			Color colors[] = new Color[7];
			colors[0] = Color.red;
			colors[1] = Color.orange;
			colors[2] = Color.yellow;
			colors[3] = Color.green;
			colors[4] = Color.blue;
			colors[5] = Color.black;
			colors[6] = Color.cyan;

			for (int r = 0; r < vpoints.length; r++) {
				String name = table_scl.getValueAt(rows[r], 1).toString();
				g.setColor(colors[r % 7]);
				g.drawString(name, 0, (r + 1) * 20);
				Vector<Point> vpoint = vpoints[r];
				for (int i = 0; i < vpoint.size(); i++) {
					int qid = vpoint.elementAt(i).qid;
					int score = vpoint.elementAt(i).score;
					int x = vpoint.elementAt(i).x;
					int y = vpoint.elementAt(i).y;
					if (i == 0) {// 起点前面没有点，也就没有线
						g.drawLine(x, y, x, y);
						g.drawString("作业" + qid + "得分" + score, x, y);
					} else {
						int p[] = vpoint.elementAt(i - 1).getXY();
						g.drawLine(p[0], p[1], x, y);
						g.drawString("作业" + qid + "得分" + score, x, y);
					}
				}
			}
		}
	};
	private JTable table_question = new JTable();
	private JTable table_answer = new JTable();

	// 进步曲线图
	private JPanel panel = new JPanel() {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			ImageIcon ii = new ImageIcon("image_background.jpg");
			g.drawImage(ii.getImage(), 0, 0, getWidth(), getHeight(), ii.getImageObserver());
		}
	};
	private JTable table_course = new JTable();
	private JTable table_scl = new JTable();
	private JScrollBar scrollBar_1 = new JScrollBar();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Person(true, 5);
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Person(boolean isTeacher, int id) {
		this.isTeacher = isTeacher;
		this.id = id;
		setTitle(Sql.yourName(isTeacher, id));

		if (isTeacher)
			setIconImage(new ImageIcon("icon_teacher.png").getImage());
		else
			setIconImage(new ImageIcon("icon_student.png").getImage());

		for (int i = 0; i < menuItems.length; i++) {
			popupMenu_QuestionSet.add(menuItems[i]);
			menuItems[i].addMouseListener(this);
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setBounds(100, 100, 850, 505);
		setExtendedState(JFrame.MAXIMIZED_BOTH);// 设置全屏，方便显示曲线图

		contentPane_view.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane_view.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane_view);

		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(new BorderLayout(0, 0));

		panel_advanceChart.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel_advanceChart.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane_view.add(tabbedPane, BorderLayout.CENTER);

		JSplitPane splitPane = new JSplitPane() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				ImageIcon ii = new ImageIcon("image_background.jpg");
				g.drawImage(ii.getImage(), 0, 0, getWidth(), getHeight(), ii.getImageObserver());
			}
		};
		splitPane.setDividerLocation(getWidth() / 2);
		tabbedPane.addTab("查看", null, splitPane, null);

		JScrollPane scrollPane_question = new JScrollPane();
		JScrollPane scrollPane_answer = new JScrollPane();

		splitPane.setLeftComponent(scrollPane_question);
		splitPane.setRightComponent(scrollPane_answer);
		table_question.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_question.setViewportView(table_question);
		table_answer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_answer.setViewportView(table_answer);

		refreshQuestionTable();
		refreshAnswerTable();

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setDividerLocation(getHeight() / 4);
		JSplitPane splitPane_2 = new JSplitPane();
		splitPane_2.setDividerLocation(getWidth() / 2);
		JScrollPane scrollPane = new JScrollPane();
		JScrollPane scrollPane_1 = new JScrollPane();
		tabbedPane.addTab("曲线图", null, panel, null);
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);

		panel.add(splitPane_1, BorderLayout.CENTER);

		splitPane_1.setLeftComponent(splitPane_2);

		splitPane_2.setLeftComponent(scrollPane);
		table_course.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table_course.setModel(new DefaultTableModel(Sql.allCourse(isTeacher, id), new String[] { "课程号", "课程" }) {
			Class[] columnTypes = new Class[] { Object.class, Object.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			public boolean isCellEditable(int row, int column) {// 返回true表示能编辑，false表示不能编辑
				return false;
			}
		});

		scrollPane.setViewportView(table_course);

		splitPane_2.setRightComponent(scrollPane_1);
		table_scl.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		scrollPane_1.setViewportView(table_scl);

		splitPane_1.setRightComponent(panel_advanceChart);
		scrollBar_1.setOrientation(JScrollBar.HORIZONTAL);

		panel_advanceChart.add(scrollBar_1, BorderLayout.SOUTH);

		table_question.addMouseListener(this);
		table_answer.addMouseListener(this);
		scrollBar_1.addMouseListener(this);
		table_course.addMouseListener(this);
		table_scl.addMouseListener(this);

		setVisible(true);

		// 设置透明
		scrollPane_question.setOpaque(false);
		scrollPane_question.getViewport().setOpaque(false);
		scrollPane_answer.setOpaque(false);
		scrollPane_answer.getViewport().setOpaque(false);
		table_question.setOpaque(false);
		splitPane_1.setOpaque(false);
		splitPane_2.setOpaque(false);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane_1.setOpaque(false);
		scrollPane_1.getViewport().setOpaque(false);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO 自动生成的方法存根
		startx = scrollBar_1.getValue();
		repaint();// 一点滑块就重绘曲线图
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
	public void mouseReleased(MouseEvent e) {// 还是释放时候好。。。。
		if (e.getSource() == table_question) {
			if (e.getClickCount() == 2) {
				String sqid = table_question.getValueAt(table_question.getSelectedRow(), 0).toString();
				if (Sql.pattern.matcher(sqid).matches()) {
					int qid = Integer.parseInt(sqid);
					new Question(qid, isTeacher, id);
				}
			}

			refreshAnswerTable();// 一点表格就立刻显示答案列表

			if (e.getButton() == e.BUTTON3 && isTeacher) {// 这个要放在前面，只有老师有右键菜单
				int row = table_question.rowAtPoint(e.getPoint());
				table_question.setRowSelectionInterval(row, row);
				popupMenu_QuestionSet.show(table_question, e.getX(), e.getY());
			}
		}
		if (e.getSource() == table_answer) {
			if (e.getClickCount() == 2) {// 双击弹出答案
				String curDate = Sql.format.format(new Date());
				String endTime = table_question.getValueAt(table_question.getSelectedRow(), 3).toString();
				if (isTeacher || curDate.compareTo(endTime) >= 0) {// 时间还未结束，学生不可查看他人答案
					int qid = Integer
							.parseInt(table_question.getValueAt(table_question.getSelectedRow(), 0).toString());
					int sid = Integer.parseInt(table_answer.getValueAt(table_answer.getSelectedRow(), 0).toString());
					Object score = table_answer.getValueAt(table_answer.getSelectedRow(), 3);
					new Answer(qid, sid, isTeacher, score);
				}
			}
			// 单击显示相似度
			refreshAnswerTable();
		}
		if (e.getSource() == menuItems[0]) {// 上传题目
			Sql.uploadQuestion(id);
			refreshQuestionTable();
		} else if (e.getSource() == menuItems[1]) {// 删除题目
			Sql.deleteQuestion(table_question);
			refreshQuestionTable();
		} else if (e.getSource() == menuItems[2]) {// 刷新
			refreshQuestionTable();
			refreshAnswerTable();
		}

		if (e.getSource() == table_course) {
			Object coid = table_course.getValueAt(table_course.getSelectedRow(), 0);
			refreshSClTable(coid);
		}
		if (e.getSource() == table_scl) {
			repaint();
		}
	}

	void refreshQuestionTable() {
		table_question.setModel(new DefaultTableModel(Sql.allQuestion(isTeacher, id),
				new String[] { "题目号", "标题", "开始时间", "结束时间", "课程号", "课程" }) {
			Class[] columnTypes = new Class[] { Object.class, Object.class, Object.class, Object.class, Object.class,
					Object.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			public boolean isCellEditable(int row, int column) {// 返回true表示能编辑，false表示不能编辑
				return false;
			}
		});
	}

	void refreshAnswerTable() {
		table_answer.setModel(new DefaultTableModel(Sql.allAnswer(table_question, table_answer),
				new String[] { "学号", "姓名", "班级", "分数", "相似度" }) {
			Class[] columnTypes = new Class[] { String.class, String.class, String.class, String.class, String.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			public boolean isCellEditable(int row, int column) {// 返回true表示能编辑，false表示不能编辑
				return false;
			}
		});
	}

	void refreshSClTable(Object coid) {
		table_scl.setModel(
				new DefaultTableModel(Sql.allSCl(coid, isTeacher, id), new String[] { "学号", "姓名", "班级号", "班级" }) {
					Class[] columnTypes = new Class[] { Object.class, Object.class, Object.class, Object.class };

					public Class getColumnClass(int columnIndex) {
						return columnTypes[columnIndex];
					}

					public boolean isCellEditable(int row, int column) {// 返回true表示能编辑，false表示不能编辑
						return false;
					}
				});

	}
}
