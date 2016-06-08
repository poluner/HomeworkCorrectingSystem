package window;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JTable;
import javax.swing.text.JTextComponent;

import client.Client;

public class Sql {
	final static Pattern pattern = Pattern.compile("[0-9]*");
	final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	static boolean pass(boolean isTeacher, String id, String pw) {
		try {
			id.replace(" ", "");
			pw.replace(" ", "");
			if (id.length() == 0 || pw.length() == 0 || pattern.matcher(id).matches() == false)
				return false;

			String sql = isTeacher ? "select * from Teacher where tid='" + id + "' and password='" + pw + "'"
					: "select * from Student where sid='" + id + "' and password='" + pw + "'";

			ResultSet rs = new Client(sql).readRowSet();
			if (rs.next())
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	static String yourName(boolean isTeacher, int id) {
		String name = "Hello " + (isTeacher ? "Teacher " : "Student ");
		try {
			String sql = isTeacher ? "select name from Teacher where tid='" + id + "'"
					: "select name from Student where sid='" + id + "'";

			ResultSet rs = new Client(sql).readRowSet();
			if (rs.next())
				return name + rs.getString(1).replace(" ", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}

	static void uploadQuestion(int tid) {// 插入没问题
		try {
			String sql = "select max(qid) from Question";

			ResultSet rs = new Client(sql).readRowSet();
			rs.next();
			int qid = rs.getInt(1) + 1;
			new Question(qid, true, tid);
			sql = "insert into Question(qid,tid)values('" + qid + "','" + tid + "')";
			new Client(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void saveQuestion(int qid, String title, String beginTime, String endTime, String question) {
		try {
			String sql = "update Question set title='" + title.trim() + "',beginTime='" + beginTime + "',endTime='"
					+ endTime + "',question='" + question.trim() + "'where qid='" + qid + "'";
			new Client(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void saveAnswer(int qid, String answer) {
		try {
			String sql = "update Question set answer='" + answer.trim() + "'where qid='" + qid + "'";
			new Client(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void saveYourAnswer(int qid, int sid, String yourAnswer) {
		try {
			String sql_exist = "select * from Answer where qid='" + qid + "' and sid='" + sid + "'";
			boolean exist = new Client(sql_exist).readRowSet().next();

			String sql = exist
					? "update Answer set answer='" + yourAnswer + "'where qid='" + qid + "'and sid='" + sid + "'"
					: "insert into Answer(qid,sid,answer) values('" + qid + "','" + sid + "','" + yourAnswer + "')";
			new Client(sql);
		} catch (Exception e) {
			try {
				String sql = "update Answer set answer='" + yourAnswer + "'where qid='" + qid + "'and sid='" + sid
						+ "'";// 插入失败就更新
				new Client(sql);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	static void showAll(int qid, boolean isTeacher, int id, JTextComponent tcs[]) {
		try {
			String sql = "select title,beginTime,endTime,question,answer from Question where qid='" + qid + "'";

			ResultSet rs = new Client(sql).readRowSet();
			if (rs.next()) {

				for (int i = 0; i < tcs.length - 1; i++) {
					String s = rs.getString(i + 1);
					if (s == null)
						s = "";
					tcs[i].setText(s.trim());
					tcs[i].setEditable(isTeacher);// 只有老师可以编辑
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isTeacher == false) {
			try {
				String sql = "select answer from Answer where qid='" + qid + "'and sid='" + id + "'";

				ResultSet rs = new Client(sql).readRowSet();
				if (rs.next()) {
					String yourAnswer = rs.getString(1);
					if (yourAnswer == null)
						yourAnswer = "";
					tcs[tcs.length - 1].setText(yourAnswer.trim());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static String showAnswer(int qid, int sid) {
		try {
			String sql = "select answer from Answer where qid='" + qid + "'and sid='" + sid + "'";

			ResultSet rs = new Client(sql).readRowSet();
			if (rs.next()) {
				return rs.getString(1).trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	static void selectQClCo(int qid, JTable t_cl, JTable t_co) {
		try {
			int row = t_cl.getRowCount();
			for (int i = 0; i < row; i++) {
				Object clid = t_cl.getValueAt(i, 0);

				boolean select = (boolean) t_cl.getValueAt(i, 2);

				String sql_where = "where qid='" + qid + "'and clid='" + clid + "'";
				String sql_exist = "select * from QCl " + sql_where;
				boolean exist = new Client(sql_exist).readRowSet().next();

				String sql_op = null;
				if (select && !exist)
					sql_op = "insert into QCl values('" + qid + "','" + clid + "')";
				if (!select && exist)
					sql_op = "delete from QCl " + sql_where;
				if (sql_op != null)
					new Client(sql_op);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			int row = t_co.getRowCount();

			Object coid = null;
			for (int i = 0; i < row; i++) {
				boolean select = (boolean) t_co.getValueAt(i, 2);
				if (select) {
					coid = t_co.getValueAt(i, 0);
					break;
				}
			}
			if (coid != null) {
				String sql = "update Question set coid='" + coid + "'where qid='" + qid + "'";
				new Client(sql);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void uploadAnswer(JTable t) {
		try {
			Object qid = t.getValueAt(t.getSelectedRow(), 0);
			String sql = "update QuestionSet set aid='" + qid + "' where qid='" + qid + "'";
			new Client(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void uploadTitleTime(JTable t) {
		try {
			int row = t.getSelectedRow();
			Object qid = t.getValueAt(row, 0);
			Object title = t.getValueAt(row, 1);
			Object beginTime = t.getValueAt(row, 2);
			Object endTime = t.getValueAt(row, 3);

			format.setLenient(false);
			if (title != null) {
				String sql = "update QuestionSet set title='" + title + "'where qid='" + qid + "'";
				new Client(sql);
			}
			if (beginTime != null) {
				Object oet = t.getValueAt(row, 3);
				if (oet != null && beginTime.toString().compareTo(oet.toString()) > 0) {// 开始时间不可大于结束时间
					return;
				}
				format.parse(beginTime.toString());
				String sql = "update QuestionSet set beginTime='" + beginTime + "'where qid='" + qid + "'";
				new Client(sql);
			}
			if (endTime != null) {
				Object obt = t.getValueAt(row, 2);
				if (obt != null && endTime.toString().compareTo(obt.toString()) < 0) {
					return;
				}
				format.parse(endTime.toString());
				String sql = "update QuestionSet set endTime='" + endTime + "'where qid='" + qid + "'";
				new Client(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void uploadTeacher() {// 插入没问题
		try {
			String sql = "select max(tid) from Teacher";

			ResultSet rs = new Client(sql).readRowSet();
			rs.next();
			int tid = rs.getInt(1) + 1;
			sql = "insert into Teacher(tid)values('" + tid + "')";
			new Client(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void uploadCourse() {// 插入没问题
		try {
			String sql = "select max(coid) from Course";

			ResultSet rs = new Client(sql).readRowSet();
			rs.next();
			int coid = rs.getInt(1) + 1;
			sql = "insert into Course(coid)values('" + coid + "')";
			new Client(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void uploadClass() {// 插入没问题
		try {
			String sql = "select max(clid) from Class";

			ResultSet rs = new Client(sql).readRowSet();
			rs.next();
			int clid = rs.getInt(1) + 1;
			sql = "insert into Class(clid)values('" + clid + "')";
			new Client(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void uploadStudent() {// 插入没问题
		try {
			String sql = "select max(sid) from Student";

			ResultSet rs = new Client(sql).readRowSet();
			rs.next();
			int sid = rs.getInt(1) + 1;
			sql = "insert into Student(sid)values('" + sid + "')";
			new Client(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void deleteQuestion(JTable t) {
		try {
			Object qid = t.getValueAt(t.getSelectedRow(), 0);
			String sql_dQ = "delete from Question where qid='" + qid + "'";
			String sql_dQCl = "delete from QCl where qid='" + qid + "'";
			new Client(sql_dQ + sql_dQCl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void deleteTeacher(JTable t) {
		try {
			if (t.getSelectedRow() == -1)
				return;
			Object tid = t.getValueAt(t.getSelectedRow(), 0);

			String sql_dT = "delete from Teacher  where tid='" + tid + "'";
			String sql_dTCl = "delete from TCl where tid='" + tid + "'";
			String sql_dTCo = "delete from TCo where tid='" + tid + "'";
			new Client(sql_dT + sql_dTCl + sql_dTCo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void deleteCourse(JTable t) {
		try {
			if (t.getSelectedRow() == -1)
				return;
			Object coid = t.getValueAt(t.getSelectedRow(), 0);
			String sql_dCo = "delete from Course where coid='" + coid + "'";
			String sql_dCoCl = "delete from CoCl where coid='" + coid + "'";
			String sql_dTCo = "delete from TCo where coid='" + coid + "'";
			new Client(sql_dCo + sql_dCoCl + sql_dTCo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void deleteClass(JTable t) {
		try {
			if (t.getSelectedRow() == -1)
				return;
			Object clid = t.getValueAt(t.getSelectedRow(), 0);
			String sql_dCl = "delete from Class where clid='" + clid + "'";
			String sql_dCoCl = "delete from CoCl where clid='" + clid + "'";
			String sql_dQCl = "delete from QCl where clid='" + clid + "'";
			String sql_dSCl = "delete from SCl where clid='" + clid + "'";
			String sql_TCl = "delete from TCl where clid='" + clid + "'";
			new Client(sql_dCl + sql_dCoCl + sql_dQCl + sql_dSCl + sql_TCl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void deleteStudent(JTable t) {
		try {
			if (t.getSelectedRow() == -1)
				return;
			Object sid = t.getValueAt(t.getSelectedRow(), 0);
			String sql_dS = "delete from Student where sid='" + sid + "'";
			String sql_dSCl = "delete from SCl where sid='" + sid + "'";
			new Client(sql_dS + sql_dSCl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void updateTeacher(JTable t) {
		try {
			int row = t.getRowCount();
			for (int i = 0; i < row; i++) {
				Object tid = t.getValueAt(i, 0);
				if (pattern.matcher(tid.toString()).matches() == false)
					return;
				Object password = t.getValueAt(i, 1);
				Object name = t.getValueAt(i, 2);
				if (password == null)
					password = "";
				if (name == null)
					name = "";
				String sql = "update Teacher set password='" + password.toString().replace(" ", "") + "',name='"
						+ name.toString().replace(" ", "") + "' where tid='" + tid + "'";
				new Client(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void updateCourse(JTable t) {
		try {
			int row = t.getRowCount();
			for (int i = 0; i < row; i++) {
				Object coid = t.getValueAt(i, 0);
				if (pattern.matcher(coid.toString()).matches() == false)
					return;
				Object name = t.getValueAt(i, 1);
				if (name == null)
					name = "";
				String sql = "update Course set name='" + name.toString().replace(" ", "") + "' where coid='" + coid
						+ "'";
				new Client(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void updateClass(JTable t) {
		try {
			int row = t.getRowCount();
			for (int i = 0; i < row; i++) {
				Object clid = t.getValueAt(i, 0);
				if (pattern.matcher(clid.toString()).matches() == false)
					return;
				Object name = t.getValueAt(i, 1);
				if (name == null)
					name = "";
				String sql = "update Class set name='" + name.toString().replace(" ", "") + "' where clid='" + clid
						+ "'";
				new Client(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void updateStudent(JTable t) {
		try {
			int row = t.getRowCount();
			for (int i = 0; i < row; i++) {
				Object sid = t.getValueAt(i, 0);
				if (pattern.matcher(sid.toString()).matches() == false)
					return;
				Object password = t.getValueAt(i, 1);
				Object name = t.getValueAt(i, 2);
				if (password == null)
					password = "";
				if (name == null)
					name = "";
				String sql = "update Student set password='" + password.toString().replace(" ", "") + "',name='"
						+ name.toString().replace(" ", "") + "' where sid='" + sid + "'";
				new Client(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void selectTCo(JTable t1, JTable t2) {
		try {
			int row1 = t1.getSelectedRow();
			if (row1 == -1)
				return;
			Object tid = t1.getValueAt(row1, 0);
			if (pattern.matcher(tid.toString()).matches() == false)
				return;

			int row2 = t2.getRowCount();
			for (int i = 0; i < row2; i++) {
				Object coid = t2.getValueAt(i, 0);
				if (pattern.matcher(coid.toString()).matches() == false)
					return;
				boolean select = (boolean) t2.getValueAt(i, 2);

				String sql_where = "where tid='" + tid + "'and coid='" + coid + "'";
				String sql_exist = "select * from TCo " + sql_where;

				boolean exist = new Client(sql_exist).readRowSet().next();

				String sql_op = null;
				if (select && !exist)
					sql_op = "insert into TCo values('" + tid + "','" + coid + "')";
				if (!select && exist)
					sql_op = "delete from TCo " + sql_where;
				if (sql_op != null)
					new Client(sql_op);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void selectClS(JTable t1, JTable t2) {
		try {
			int row1 = t1.getSelectedRow();
			if (row1 == -1)
				return;
			Object clid = t1.getValueAt(row1, 0);
			if (pattern.matcher(clid.toString()).matches() == false)
				return;

			int row2 = t2.getRowCount();
			for (int i = 0; i < row2; i++) {
				Object sid = t2.getValueAt(i, 0);
				if (pattern.matcher(sid.toString()).matches() == false)
					return;
				boolean select = (boolean) t2.getValueAt(i, 3);

				String sql_where = "where clid='" + clid + "'and sid='" + sid + "'";
				String sql_exist = "select * from SCl " + sql_where;

				boolean exist = new Client(sql_exist).readRowSet().next();

				String sql_op = null;
				if (select && !exist)
					sql_op = "insert into SCl values('" + sid + "','" + clid + "')";
				if (!select && exist)
					sql_op = "delete from SCl " + sql_where;
				if (sql_op != null)
					new Client(sql_op);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void selectCoCl(JTable t1, JTable t2) {
		try {
			int row1 = t1.getSelectedRow();
			if (row1 == -1)
				return;
			Object coid = t1.getValueAt(row1, 0);
			if (pattern.matcher(coid.toString()).matches() == false)
				return;

			int row2 = t2.getRowCount();
			for (int i = 0; i < row2; i++) {
				Object clid = t2.getValueAt(i, 0);
				if (pattern.matcher(clid.toString()).matches() == false)
					return;
				boolean select = (boolean) t2.getValueAt(i, 2);

				String sql_where = "where coid='" + coid + "'and clid='" + clid + "'";
				String sql_exist = "select * from CoCl " + sql_where;

				boolean exist = new Client(sql_exist).readRowSet().next();

				String sql_op = null;
				if (select && !exist)
					sql_op = "insert into CoCl values('" + coid + "','" + clid + "')";
				if (!select && exist)
					sql_op = "delete from CoCl " + sql_where;
				if (sql_op != null)
					new Client(sql_op);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void selectTCl(JTable t1, JTable t2) {
		try {
			int row1 = t1.getSelectedRow();
			if (row1 == -1)
				return;
			Object tid = t1.getValueAt(row1, 0);
			if (pattern.matcher(tid.toString()).matches() == false)
				return;

			int row2 = t2.getRowCount();
			for (int i = 0; i < row2; i++) {
				Object clid = t2.getValueAt(i, 0);
				if (pattern.matcher(clid.toString()).matches() == false)
					return;
				boolean select = (boolean) t2.getValueAt(i, 2);

				String sql_where = "where tid='" + tid + "'and clid='" + clid + "'";
				String sql_exist = "select * from TCl " + sql_where;

				boolean exist = new Client(sql_exist).readRowSet().next();
				String sql_op = null;
				if (select && !exist)
					sql_op = "insert into TCl values('" + tid + "','" + clid + "')";
				if (!select && exist)
					sql_op = "delete from TCl " + sql_where;
				if (sql_op != null)
					new Client(sql_op);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static Object[][] allQuestion(boolean isTeacher, int id) {
		try {
			String sql = "select qid,title,beginTime,endTime,coid "
					+ (isTeacher ? "from Question where tid='" + id + "'"
							: "from Question where qid in(select qid from QCl where clid in( select clid from SCl where sid='"
									+ id + "'))");

			ResultSet rs = new Client(sql).readRowSet();
			rs.last();
			int row = rs.getRow();
			if (row == 0)
				return new Object[][] { new Object[] { "右键上传题目" } };
			int col = 6;
			Object[][] o = new Object[row][col];

			rs.beforeFirst();

			for (int i = 0; rs.next(); i++) {
				for (int j = 0; j < col - 1; j++) {
					o[i][j] = rs.getString(j + 1);
				}
				// System.out.println("coid=" + o[i][col - 2]);
				if (o[i][col - 2] == null)
					continue;
				String sql_coName = "select name from Course where coid='" + o[i][col - 2] + "'";

				ResultSet rs_coName = new Client(sql_coName).readRowSet();
				if (rs_coName.next())
					o[i][col - 1] = rs_coName.getString(1);
			}

			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	static Object[][] allAnswer(JTable t_que, JTable t_ans) {
		try {
			int getRow = t_que.getSelectedRow();
			if (getRow == -1)
				return null;
			Object qid = t_que.getValueAt(getRow, 0);
			if (pattern.matcher(qid.toString()).matches() == false)
				return null;

			String sql = "select a.sid,b.name,c.name,score from Answer a,Student b,Class c,SCl d where qid='" + qid
					+ "' and a.sid=b.sid and a.sid=d.sid and c.clId=d.clId order by c.name asc,score desc,a.sid asc";

			ResultSet rs = new Client(sql).readRowSet();
			rs.last();
			int row = rs.getRow();
			if (row == 0)
				return null;

			int col = 5;
			Object[][] o = new Object[row][col];

			rs.beforeFirst();
			for (int i = 0; rs.next(); i++) {
				for (int j = 0; j < col - 1; j++) {
					o[i][j] = rs.getString(j + 1);
				}
			}

			if (t_ans.getSelectedRow() != -1) {// 如果答案表被点了，就显示相似度
				int ans_row = t_ans.getSelectedRow();
				Object mySid = o[ans_row][0];// bug这里出现过一次下标越界
				String sql_answer = "select answer from Answer where qid='" + qid + "' and sid='";

				ResultSet rs_answer = new Client(sql_answer + mySid + "'").readRowSet();
				rs_answer.next();
				String myAnswer = rs_answer.getString(1);
				for (int i = 0; i < row; i++) {
					if (i != ans_row) {
						Object yourSid = o[i][0];

						rs_answer = new Client(sql_answer + yourSid + "'").readRowSet();
						rs_answer.next();
						String yourAnswer = rs_answer.getString(1);
						o[i][4] = Lcs.sameDegree(myAnswer, yourAnswer);
					}
				}
			}

			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static Object[][] allTeacher() {// 这里会有bug显示getstring索引越界,已解决
		try {
			String sql = "select * from Teacher";

			ResultSet rs = new Client(sql).readRowSet();
			rs.last();
			int row = rs.getRow();
			if (row == 0)
				return new Object[][] { new Object[] { "右键添加教师" } };
			int col = 3;
			Object[][] o = new Object[row][col];

			rs.beforeFirst();
			for (int i = 0; rs.next(); i++) {
				for (int j = 0; j < col; j++) {
					o[i][j] = rs.getString(j + 1);
				}
			}
			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static Object[][] allTeacher1() {
		try {
			String sql = "select * from Teacher";

			ResultSet rs = new Client(sql).readRowSet();
			rs.last();
			int row = rs.getRow();
			int col = 2;
			Object[][] o = new Object[row][col];

			rs.beforeFirst();
			for (int i = 0; rs.next(); i++) {
				o[i][0] = rs.getString(1);
				o[i][1] = rs.getString(3);
			}
			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static Object[][] allClass() {
		try {
			String sql = "select * from Class";

			ResultSet rs = new Client(sql).readRowSet();
			rs.last();
			int row = rs.getRow();
			if (row == 0)
				return new Object[][] { new Object[] { "右键添加班级" } };
			int col = 2;
			Object[][] o = new Object[row][col];

			rs.beforeFirst();
			for (int i = 0; rs.next(); i++) {
				o[i][0] = rs.getInt(1);
				o[i][1] = rs.getString(2);
			}

			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static Object[][] allClass(Object coid) {
		try {
			String sql = "select * from Class";

			ResultSet rs = new Client(sql).readRowSet();
			rs.last();
			int row = rs.getRow();
			if (row == 0)
				return null;
			int col = 3;
			Object[][] o = new Object[row][col];

			rs.beforeFirst();
			if (coid == null || pattern.matcher(coid.toString()).matches() == false)
				coid = -1;
			String sql_selected = "select clid from CoCl where coid='" + coid + "'";

			ResultSet rs_selectedClass = new Client(sql_selected).readRowSet();

			Set<Integer> classSet = new HashSet<Integer>();
			while (rs_selectedClass.next()) {
				classSet.add(rs_selectedClass.getInt(1));
			}

			for (int i = 0; rs.next(); i++) {
				o[i][0] = rs.getInt(1);
				o[i][1] = rs.getString(2);
				o[i][2] = classSet.contains(o[i][0]) ? true : false;
			}
			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static Object[][] allClass2(Object tid) {
		try {
			String sql = "select * from Class";
			ResultSet rs = new Client(sql).readRowSet();
			rs.last();
			int row = rs.getRow();
			if (row == 0)
				return null;
			int col = 3;
			Object[][] o = new Object[row][col];

			rs.beforeFirst();
			if (tid == null || pattern.matcher(tid.toString()).matches() == false)
				tid = -1;
			String sql_selected = "select clid from TCl where tid='" + tid + "'";

			ResultSet rs_selectedClass = new Client(sql_selected).readRowSet();

			Set<Integer> classSet = new HashSet<Integer>();
			while (rs_selectedClass.next()) {
				classSet.add(rs_selectedClass.getInt(1));
			}

			for (int i = 0; rs.next(); i++) {
				o[i][0] = rs.getInt(1);
				o[i][1] = rs.getString(2);
				o[i][2] = classSet.contains(o[i][0]) ? true : false;
			}
			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static Object[][] allCourse(Object tid) {// 显示所有课程，被老师tid选的会打上勾
		try {
			String sql = "select * from Course";

			ResultSet rs = new Client(sql).readRowSet();
			rs.last();
			int row = rs.getRow();
			if (row == 0)
				return new Object[][] { new Object[] { "右键添加课程" } };
			int col = 3;
			Object[][] o = new Object[row][col];

			rs.beforeFirst();
			if (tid == null || pattern.matcher(tid.toString()).matches() == false)
				tid = -1;
			String sql_selected = "select coid from TCo where tid='" + tid + "'";

			ResultSet rs_selectedCourse = new Client(sql_selected).readRowSet();

			Set<Integer> courseSet = new HashSet<Integer>();
			while (rs_selectedCourse.next()) {
				courseSet.add(rs_selectedCourse.getInt(1));
			}

			for (int i = 0; rs.next(); i++) {
				o[i][0] = rs.getInt(1);
				o[i][1] = rs.getString(2);
				o[i][2] = courseSet.contains(o[i][0]) ? true : false;
			}

			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static Object[][] allCourse() {// 显示所有课程
		try {
			String sql = "select * from Course";
			ResultSet rs = new Client(sql).readRowSet();
			rs.last();
			int row = rs.getRow();
			if (row == 0)
				return null;
			int col = 2;
			Object[][] o = new Object[row][col];

			rs.beforeFirst();
			for (int i = 0; rs.next(); i++) {
				o[i][0] = rs.getString(1);
				o[i][1] = rs.getString(2);
			}

			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static Object[][] allStudentFromClass(Object clid) {// 显示所有学生，本班的会打上勾
		try {
			String sql = "select * from Student";
			ResultSet rs = new Client(sql).readRowSet();
			rs.last();
			int row = rs.getRow();
			if (row == 0)
				return new Object[][] { new Object[] { "右键添加学生" } };
			int col = 4;
			Object[][] o = new Object[row][col];

			rs.beforeFirst();
			if (clid == null || pattern.matcher(clid.toString()).matches() == false)
				clid = -1;
			String sql_selected = "select sid from SCl where clid='" + clid + "'";

			ResultSet rs_selectedStudent = new Client(sql_selected).readRowSet();

			Set<Integer> studentSet = new HashSet<Integer>();
			while (rs_selectedStudent.next()) {
				studentSet.add(rs_selectedStudent.getInt(1));
			}

			for (int i = 0; rs.next(); i++) {
				o[i][0] = rs.getInt(1);
				o[i][1] = rs.getString(2);
				o[i][2] = rs.getString(3);
				o[i][3] = studentSet.contains(o[i][0]) ? true : false;
			}

			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static boolean updateScore(int qid, int sid, int score) {
		try {
			if (score < 0 || score > 100)
				return false;
			String sql = "update Answer set score='" + score + "' where qid='" + qid + "'and sid='" + sid + "'";
			new Client(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	static Object[][] allCourse(boolean isTeacher, int id) {
		try {
			String sql = isTeacher
					? "select coid,name from Course where coid in(select coid from TCo where tid='" + id + "')"
					: "select coid,name from Course where coid in(select coid from CoCl a,SCl b where sid='" + id
							+ "'and a.clid=b.clid)";

			ResultSet rs = new Client(sql).readRowSet();
			rs.last();
			int row = rs.getRow();

			int col = 2;
			Object o[][] = new Object[row][col];

			rs.beforeFirst();
			for (int i = 0; rs.next(); i++) {
				for (int j = 0; j < col; j++) {
					o[i][j] = rs.getObject(j + 1);
				}
			}
			return o;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static Object[][] allSCl(Object coid, boolean isTeacher, int id) {
		try {
			String sql = isTeacher
					? "select d.sid,d.name,b.clid,b.name from CoCl a,Class b,SCl c,Student d,TCo e where a.coid='"
							+ coid + "' and a.clid=c.clid and a.clid=b.clid and c.sid=d.sid and e.tid='" + id
							+ "' and e.coid=a.coid"
					: "select a.sid,a.name,b.clid,c.name from Student a,SCl b,Class c,CoCl d where a.sid=b.sid and b.clid=c.clid and d.clid=b.clid and coid='"
							+ coid + "'and b.clid in (select clid from SCl	where sid='" + id + "')";

			ResultSet rs = new Client(sql).readRowSet();
			rs.last();
			int row = rs.getRow();

			int col = 4;
			Object o[][] = new Object[row][col];

			rs.beforeFirst();
			for (int i = 0; rs.next(); i++) {
				for (int j = 0; j < col; j++) {
					o[i][j] = rs.getObject(j + 1);
				}
			}
			return o;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static Object[][] allCourseFromTeacher(int qid, int tid) {
		try {
			String sql = "select coid from TCo where tid='" + tid + "'";

			ResultSet rs = new Client(sql).readRowSet();
			rs.last();
			int row = rs.getRow();
			if (row == 0)
				return null;
			int col = 3;
			Object[][] o = new Object[row][col];

			rs.beforeFirst();

			String sql_set = "select coid from Question where qid='" + qid + "'";

			ResultSet rs_selectedCourse = new Client(sql_set).readRowSet();

			int selecedCoid = -1;
			if (rs_selectedCourse.next())
				selecedCoid = rs_selectedCourse.getInt(1);

			for (int i = 0; rs.next(); i++) {
				int coid = rs.getInt(1);
				o[i][0] = coid;
				// System.out.println("question coid="+coid);
				String sql_coName = "select name from Course where coid='" + coid + "'";

				ResultSet rs_coName = new Client(sql_coName).readRowSet();
				if (rs_coName.next()) {
					o[i][1] = rs_coName.getString(1);
				}
				o[i][2] = coid == selecedCoid ? true : false;
			}
			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static Vector<String> allClass(boolean isTeacher, int id, Object courseName) {
		try {
			String sql = isTeacher
					? "select a.className from TCl a,CoCl b where a.tid='" + id
							+ "'and a.className=b.className and b.courseName='" + courseName + "'"
					: "select className from SCl where sid='" + id + "'";

			ResultSet rs = new Client(sql).readRowSet();
			Vector<String> v = new Vector<String>();
			while (rs.next()) {
				v.add(rs.getString(1));
			}
			return v;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static Vector<String> allStudent(Object courseName) {
		try {
			String sql = "select name from Student a,SCl b where a.sid=b.sid and b.className='" + courseName + "'";

			ResultSet rs = new Client(sql).readRowSet();
			Vector<String> v = new Vector<String>();
			while (rs.next()) {
				v.add(rs.getString(1));
			}
			return v;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static Vector<Point>[] allPoint(JTable t, JTable t1) {// 人，行，4个值
		try {
			int row = t.getSelectedRow();
			if (row == -1)
				return null;
			Object coid = t.getValueAt(row, 0);

			int rows1[] = t1.getSelectedRows();
			if (rows1.length == 0)
				return null;

			Vector<Point> vs[] = new Vector[rows1.length];
			for (int i = 0; i < vs.length; i++) {
				vs[i] = new Vector<Point>();
			}

			int maxSize = 0;
			Vector<Point> maxv = null;
			for (int i = 0; i < rows1.length; i++) {

				Object sid = t1.getValueAt(rows1[i], 0);
				Object clid = t1.getValueAt(rows1[i], 2);

				String sql = "select a.qid,score from Question a,Answer b where coid='" + coid
						+ "'and a.qid=b.qid and sid in(select a.sid from SCl a,Student b where clid='" + clid
						+ "' and b.sid='" + sid + "' and a.sid=b.sid)";

				ResultSet rs = new Client(sql).readRowSet();

				int x = 0;
				while (rs.next()) {
					vs[i].add(new Point(rs.getInt(1), rs.getObject(2), x += Point.space));
				}
				if (vs[i].size() > maxSize) {
					maxSize = vs[i].size();
					maxv = vs[i];
				}
			}
			if (maxv == null)
				return null;
			for (int i = 0; i < rows1.length; i++) {
				for (int j = vs[i].size(); j < maxv.size(); j++) {
					vs[i].add(new Point(maxv.elementAt(j).qid, 0, maxv.elementAt(j).x));
				}
			}

			return vs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static Object[][] allClassFromQuestion(int qid, int tid) {// 该老师所教所有班级，被题目选的班级将打上勾
		try {
			String sql = "select clid from TCl where tid='" + tid + "'";

			ResultSet rs = new Client(sql).readRowSet();
			rs.last();
			int row = rs.getRow();
			if (row == 0)
				return null;
			int col = 3;
			Object[][] o = new Object[row][col];

			rs.beforeFirst();
			String sql_set = "select clid from QCl where qid='" + qid + "'";

			ResultSet rs_selectedClass = new Client(sql_set).readRowSet();

			Set<Integer> classSet = new HashSet<Integer>();
			while (rs_selectedClass.next()) {
				classSet.add(rs_selectedClass.getInt(1));
			}

			for (int i = 0; rs.next(); i++) {
				int clid = rs.getInt(1);
				o[i][0] = clid;
				String sql_clName = "select name from Class where clid='" + clid + "'";

				ResultSet rs_clName = new Client(sql_clName).readRowSet();
				if (rs_clName.next())
					o[i][1] = rs_clName.getString(1);

				o[i][2] = classSet.contains(clid) ? true : false;
			}
			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
