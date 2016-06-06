package window;

public class Lcs {
	public static double sameDegree(String sa, String sb) {//已确保sa，sb不会为null
		
		//首先去除所有空格，或许回车也要去除
		sa=sa.replace(" ", "");
		sb=sb.replace(" ", "");
		
		int n = sa.length(), m = sb.length();
		char a[] = new char[n + 1];
		char b[] = new char[m + 1];
		sa.getChars(0, n, a, 1);
		sb.getChars(0, m, b, 1);

		int sum[][] = new int[n + 1][m + 1];

		for (int i = 0; i <= n; i++)
			sum[i][0] = 0;
		for (int j = 0; j <= m; j++)
			sum[0][j] = 0;

		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= m; j++) {
				if (a[i] == b[j])
					sum[i][j] = sum[i - 1][j - 1] + 1;
				else
					sum[i][j] = Math.max(sum[i - 1][j], sum[i][j - 1]);
			}
		}
		return (double) sum[n][m] / Math.max(n, m);
	}

	public static void main(String[] args) throws Exception{
		String a = "123";
		String b = " 1   2   3";
		System.out.println(sameDegree(a, b));
	}
}
