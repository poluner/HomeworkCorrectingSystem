package window;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import client.Client;

import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					new Sql();
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Login() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);

		JRadioButton radioButton_teacher = new JRadioButton("教师");
		panel.add(radioButton_teacher);

		JRadioButton radioButton_student = new JRadioButton("学生");
		panel.add(radioButton_student);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(radioButton_teacher);
		buttonGroup.add(radioButton_student);

		JButton button = new JButton("确定");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean isTeacher;
				if (radioButton_teacher.isSelected())
					isTeacher = true;
				else if (radioButton_student.isSelected())
					isTeacher = false;
				else
					return;
				String id = textField.getText();
				String pw = new String(passwordField.getPassword());
				if (Sql.pass(isTeacher, id, pw) == false)
					return;
				new Person(isTeacher, Integer.parseInt(id));
				dispose();
			}
		});
		panel.add(button);

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(null);

		JLabel lblNewLabel = new JLabel("工号/学号：");
		lblNewLabel.setBounds(90, 70, 72, 15);
		panel_1.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("密码：");
		lblNewLabel_1.setBounds(90, 114, 54, 15);
		panel_1.add(lblNewLabel_1);

		textField = new JTextField();
		textField.setBounds(172, 67, 127, 21);
		panel_1.add(textField);
		textField.setColumns(10);

		passwordField = new JPasswordField();
		passwordField.setBounds(172, 107, 127, 22);
		panel_1.add(passwordField);

	}
}
