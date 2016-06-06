package server;

import java.net.ServerSocket;
import java.net.Socket;

public class MultServer {

	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(4700); // 创建一个ServerSocket在端口4700监听客户请求

			while (true) {// 循环监听
				Socket socket = serverSocket.accept();
				new ServerThread(socket).start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
