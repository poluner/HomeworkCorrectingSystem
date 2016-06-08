package window;

public class Point {
	public static final int space = 100;
	public static int basey = 900;
	public static final int mult = 5;
	public int qid;
	public int score;
	public int x;
	public int y;

	public Point(int qid, Object score, int x) {
		this.qid = qid;
		this.score = score == null ? 0 : (int) score;
		this.x = x;
		this.y = basey - this.score * mult;
	}

	public int[] getXY() {
		return new int[] { x, y };
	}
}
