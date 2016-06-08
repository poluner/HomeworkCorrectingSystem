package window;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import javax.swing.ListSelectionModel;

public class Question extends JFrame implements ActionListener {
	private int qid;
	private int id;
	private boolean isTeacher;
	private boolean overtime = false;

	private JPanel contentPane = new JPanel();
	private JButton button_question = new JButton("保存");
	private JButton button_answer = new JButton("保存");
	private JButton button_yourAnswer = new JButton("保存");

	private JTextField textField_title = new JTextField();
	private JTextField textField_beginTime = new JTextField();
	private JTextField textField_endTime = new JTextField();
	private JTextArea textArea_question = new JTextArea();
	private JTextArea textArea_answer = new JTextArea();
	private JTextArea textArea_yourAnswer = new JTextArea();
	private JTable table_class = new JTable();
	private JTable table_course = new JTable();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Question(1, false, 1);
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Question(int qid, Boolean isTeacher, int id) {// 如果不是teacher就不显示一些东西即可
		this.qid = qid;
		this.id = id;
		this.isTeacher = isTeacher;
		setIconImage(new ImageIcon("icon_quetion.png").getImage());

		Sql.showAll(qid, isTeacher, id, new JTextComponent[] { textField_title, textField_beginTime, textField_endTime,
				textArea_question, textArea_answer, textArea_yourAnswer });

		if (isTeacher == false) {
			String curDate = Sql.format.format(new Date());
			if (curDate.compareTo(textField_endTime.getText()) > 0) {
				overtime = true;
				setTitle("题目：" + qid + " 提交时间结束");
			}
		}

		if (overtime == false)
			setTitle("题目：" + qid);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 784, 471);

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		JPanel panel_question = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				ImageIcon ii = new ImageIcon("image_background.jpg");
				g.drawImage(ii.getImage(), 0, 0, getWidth(), getHeight(), ii.getImageObserver());
			}
		};
		tabbedPane.addTab("题目", null, panel_question, null);
		panel_question.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel_question.setLayout(new BorderLayout(0, 0));

		JPanel panel_3 = new JPanel();
		panel_question.add(panel_3, BorderLayout.NORTH);

		JLabel label = new JLabel("标题：");
		panel_3.add(label);

		panel_3.add(textField_title);
		textField_title.setColumns(10);

		JLabel label_1 = new JLabel("开始时间：");
		panel_3.add(label_1);

		panel_3.add(textField_beginTime);
		textField_beginTime.setColumns(10);

		JLabel label_2 = new JLabel("结束时间：");
		panel_3.add(label_2);

		panel_3.add(textField_endTime);
		textField_endTime.setColumns(10);

		panel_3.add(button_question);

		JSplitPane splitPane = new JSplitPane();
		panel_question.add(splitPane, BorderLayout.CENTER);
		splitPane.setDividerLocation(getWidth() / 2);

		JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);

		scrollPane.setViewportView(textArea_question);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setRightComponent(splitPane_1);
		splitPane_1.setDividerLocation(getHeight() / 3);

		JScrollPane scrollPane_3 = new JScrollPane();
		splitPane_1.setLeftComponent(scrollPane_3);
		table_course.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		table_course
				.setModel(new DefaultTableModel(Sql.allCourseFromTeacher(qid, id), new String[] { "课程号", "课程", "选择" }) {
					Class[] columnTypes = new Class[] { Integer.class, String.class, Boolean.class };

					public Class getColumnClass(int columnIndex) {
						return columnTypes[columnIndex];
					}

					public boolean isCellEditable(int row, int column) {// 返回true表示能编辑，false表示不能编辑
						if (column == 0 || column == 1)
							return false;
						return true;
					}
				});
		scrollPane_3.setViewportView(table_course);

		JScrollPane scrollPane_4 = new JScrollPane();
		splitPane_1.setRightComponent(scrollPane_4);

		JPanel panel_answer = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				ImageIcon ii = new ImageIcon("image_background.jpg");
				g.drawImage(ii.getImage(), 0, 0, getWidth(), getHeight(), ii.getImageObserver());
			}
		};
		tabbedPane.addTab("参考答案", null, panel_answer, null);
		panel_answer.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel_answer.setLayout(new BorderLayout(0, 0));

		panel_answer.add(button_answer, BorderLayout.NORTH);

		JScrollPane scrollPane_1 = new JScrollPane();
		panel_answer.add(scrollPane_1, BorderLayout.CENTER);

		scrollPane_1.setViewportView(textArea_answer);

		textArea_yourAnswer.setEditable(!overtime);

		JPanel panel_yourAnswer = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				ImageIcon ii = new ImageIcon("image_background.jpg");
				g.drawImage(ii.getImage(), 0, 0, getWidth(), getHeight(), ii.getImageObserver());
			}
		};
		tabbedPane.addTab("你的答案", null, panel_yourAnswer, null);
		panel_yourAnswer.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel_yourAnswer.setLayout(new BorderLayout(0, 0));

		panel_yourAnswer.add(button_yourAnswer, BorderLayout.NORTH);
		JScrollPane scrollPane_2 = new JScrollPane();
		panel_yourAnswer.add(scrollPane_2, BorderLayout.CENTER);

		scrollPane_2.setViewportView(textArea_yourAnswer);
		table_class.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		table_class
				.setModel(new DefaultTableModel(Sql.allClassFromQuestion(qid, id), new String[] { "班级号", "班级", "选择" }) {
					Class[] columnTypes = new Class[] { Integer.class, String.class, Boolean.class };

					public Class getColumnClass(int columnIndex) {
						return columnTypes[columnIndex];
					}

					public boolean isCellEditable(int row, int column) {// 返回true表示能编辑，false表示不能编辑
						if (column == 0 || column == 1)
							return false;
						return true;
					}
				});

		scrollPane_4.setViewportView(table_class);

		MouseListener mouseListener = new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getSource() == table_course) {
					if (table_course.getSelectedColumn() == 2) {
						int row = table_course.getSelectedRow();
						if ((boolean) table_course.getValueAt(row, 2)) {// 选中一行之后将其他行设为false
							int rc = table_course.getRowCount();
							for (int i = 0; i < rc; i++) {
								if (i != row) {
									table_course.setValueAt(false, i, 2);
								}
							}
						}
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO 自动生成的方法存根

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO 自动生成的方法存根

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO 自动生成的方法存根

			}

			@Override
			public void mouseClicked(MouseEvent e) {

			}
		};
		table_course.addMouseListener(mouseListener);
		table_class.addMouseListener(mouseListener);

		button_question.addActionListener(this);
		button_answer.addActionListener(this);
		button_yourAnswer.addActionListener(this);

		setPower();

		setVisible(true);
		// 透明
		panel_3.setOpaque(false);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		splitPane.setOpaque(false);
		splitPane_1.setOpaque(false);
		scrollPane_3.setOpaque(false);
		scrollPane_3.getViewport().setOpaque(false);
		table_course.setOpaque(false);
		scrollPane_4.setOpaque(false);
		scrollPane_4.getViewport().setOpaque(false);
		table_class.setOpaque(false);
		textArea_question.setOpaque(false);
		scrollPane_1.setOpaque(false);
		scrollPane_1.getViewport().setOpaque(false);
		scrollPane_2.setOpaque(false);
		scrollPane_2.getViewport().setOpaque(false);
		textArea_answer.setOpaque(false);
		textArea_yourAnswer.setOpaque(false);
	}

	void setPower() {
		boolean nototime = false;
		String curDate = Sql.format.format(new Date());
		if (!isTeacher && curDate.compareTo(textField_beginTime.getText()) < 0)
			nototime = true;

		button_question.setVisible(isTeacher);
		button_answer.setVisible(isTeacher);
		button_yourAnswer.setVisible(!isTeacher && !overtime && !nototime);// 学生，时间在开始和结束时间之内
		textField_title.setEditable(isTeacher);
		textField_beginTime.setEditable(isTeacher);
		textField_endTime.setEditable(isTeacher);
		textArea_question.setEditable(isTeacher);
		textArea_answer.setEditable(isTeacher);
		textArea_answer.setVisible(isTeacher || overtime);
		textArea_yourAnswer.setEditable(!isTeacher && !overtime && !nototime);// 学生，时间在开始和结束时间之内
		table_course.setVisible(isTeacher);
		table_class.setVisible(isTeacher);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == button_question) {
			Sql.selectQClCo(qid, table_class, table_course);

			String title = textField_title.getText(), beginTime = textField_beginTime.getText(),
					endTime = textField_endTime.getText(), question = textArea_question.getText();
			if (title.length() == 0 || beginTime.length() == 0 || endTime.length() == 0 || question.length() == 0) {
				setTitle("题目：" + qid + " 课程和班级保存成功，其他均未保存");
				return;
			}

			Sql.format.setLenient(false);
			Date date_beginTime = null;
			Date date_endTime = null;
			try {
				date_beginTime = Sql.format.parse(beginTime);
				date_endTime = Sql.format.parse(endTime);
				if (beginTime.compareTo(endTime) > 0)
					throw new Exception();// 开始时间大于结束时间
			} catch (Exception e1) {
				setTitle("题目：" + qid + " 保存失败，时间填写有误");
				return;
			}

			Sql.saveQuestion(qid, title.trim(), beginTime, endTime, question.trim());

			setTitle("题目：" + qid + " 保存成功");
		}
		if (e.getSource() == button_answer) {
			String answer = textArea_answer.getText();
			if (answer.length() == 0) {
				setTitle("题目：" + qid + " 内容未填写");
				return;
			}
			Sql.saveAnswer(qid, answer.trim());
			setTitle("题目：" + qid + " 参考答案保存成功");
		}
		if (e.getSource() == button_yourAnswer) {
			String yourAnswer = textArea_yourAnswer.getText();
			if (yourAnswer.length() == 0) {
				setTitle("题目：" + qid + " 内容未填写");
				return;
			}
			Sql.saveYourAnswer(qid, id, yourAnswer.trim());
			setTitle("题目：" + qid + " 你的答案保存成功");
		}
	}
}
