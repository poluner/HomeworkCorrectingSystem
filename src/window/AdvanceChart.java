package window;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollBar;
import javax.swing.JButton;

public class AdvanceChart extends JFrame {
	String person;
	String id;
	int startx;

	private JPanel contentPane=new JPanel();
	private JComboBox comboBox_courseName = new JComboBox();
	private JComboBox comboBox_className = new JComboBox();
	private JComboBox comboBox_sName = new JComboBox();
	private Object lastSelectCourse = null;
	private Object lastSelectClass = null;
	private final JScrollBar scrollBar = new JScrollBar();
	private final JButton btnNewButton = new JButton("New button");

	public void paint(Graphics g) {
		super.paint(g);
		try {
			Object courseName = comboBox_courseName.getSelectedItem();
			Object className = comboBox_className.getSelectedItem();
			Object sName = comboBox_sName.getSelectedItem();
			if (courseName == null || className == null || sName == null)
				return;

			int space = 50;// 行间距设为50
			int x = 50 - startx;// 起始x为50-startx
			int y = 500;// x轴位置
			int perx = 0, pery = 0;

			Vector<Object> point = Sql.allPoint(courseName, className, sName);

			for (int i = 0; i < point.size(); i += 2) {
				Date date = (Date) point.elementAt(i);
				int score = (int) point.elementAt(i + 1);
				if (i == 0) {// 起点前面没有点，也就没有线
					perx = x;
					pery = y - 5 * score;
				}
				g.drawLine(perx, pery, x, y - 5 * score);
				g.drawString("(" + date + ")" + score + "分", x, y - 5 * score);
				perx = x;
				pery = y - 5 * score;
				x += space;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Sql();
				new AdvanceChart("Student", "s1");
			}
		});
	}

	/**
	 * Create the frame.
	 */
	
	
	public AdvanceChart(String person, String id )  {
		this.person = person;
		this.id = id;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);

		comboBox_courseName.setModel(new DefaultComboBoxModel(Sql.allCourse(person, id)));
		panel.add(comboBox_courseName);
		panel.add(comboBox_className);
		panel.add(comboBox_sName);
		scrollBar.setOrientation(JScrollBar.HORIZONTAL);
		scrollBar.setMaximum(0);

		contentPane.add(scrollBar, BorderLayout.SOUTH);
		
		contentPane.add(btnNewButton, BorderLayout.CENTER);

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (comboBox_courseName.getSelectedItem() != lastSelectCourse) {
					lastSelectCourse = comboBox_courseName.getSelectedItem();
					comboBox_className.setModel(
							new DefaultComboBoxModel(Sql.allClass(person, id, comboBox_courseName.getSelectedItem())));
				}
				if (comboBox_className.getSelectedItem() != lastSelectClass) {
					lastSelectClass = comboBox_className.getSelectedItem();
					comboBox_sName
							.setModel(new DefaultComboBoxModel(Sql.allStudent(comboBox_className.getSelectedItem())));
					startx = 0;
					// 根据点数初始化滑块的最大范围
					scrollBar.setMaximum(Sql.pointNumber(comboBox_courseName.getSelectedItem(),
							comboBox_className.getSelectedItem(), comboBox_sName.getSelectedItem()));
				}
				repaint();// 一点击就会重绘曲线图
			}
		};

		comboBox_courseName.addActionListener(actionListener);
		comboBox_className.addActionListener(actionListener);
		comboBox_sName.addActionListener(actionListener);

		MouseListener mouseListener = new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// System.out.println("滑块位置：" + scrollBar.getValue());
				startx = scrollBar.getValue();
				repaint();// 一点滑块就重绘曲线图
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		};
		scrollBar.addMouseListener(mouseListener);

		setVisible(true);
	}

}
