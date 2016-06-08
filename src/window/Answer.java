package window;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Answer extends JFrame {

	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Answer(1, 1, false, 63);
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Answer(int qid, int sid, boolean isTeacher, Object score) {
		setIconImage(new ImageIcon("icon_answer.png").getImage());
		setTitle("学生 " + sid + " 的答案");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				ImageIcon ii = new ImageIcon("image_background.jpg");
				g.drawImage(ii.getImage(), 0, 0, getWidth(), getHeight(), ii.getImageObserver());
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		JTextArea textArea = new JTextArea(Sql.showAnswer(qid, sid));
		scrollPane.setViewportView(textArea);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);

		textField = new JTextField();
		if (score != null)
			textField.setText(score.toString());
		panel.add(textField);
		textField.setColumns(10);

		JButton button = new JButton("打分");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String score = textField.getText();
				if (score.length() != 0 && Sql.pattern.matcher(score).matches()) {
					String ans=Sql.updateScore(qid, sid, Integer.parseInt(score))?"打分成功":"打分失败";
					setTitle("学生 " + sid + " 的答案"+ans);
				}
			}
		});
		panel.add(button);

		setVisible(true);

		// 权限
		textArea.setEditable(false);
		if (!isTeacher) {
			textField.setEditable(false);
			button.setVisible(false);
		}

		// 透明
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		textArea.setOpaque(false);
		panel.setOpaque(false);
		textField.setOpaque(false);
	}

}
