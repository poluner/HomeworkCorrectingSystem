package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;

public class MultServer {
	public static void main(String[] args) {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String dbURL = "jdbc:sqlserver://127.0.0.1:1433;DatabaseName=HomeworkCorrectingSystem";
			Connection connection = DriverManager.getConnection(dbURL, "sa", "123");
			
			ServerSocket serverSocket = new ServerSocket(4700); // 创建一个ServerSocket在端口4700监听客户请求
			while (true) {// 循环监听
				Socket socket = serverSocket.accept();
				new ServerThread(socket,connection).start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
