package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.sun.rowset.CachedRowSetImpl;

public class Client {
	Socket socket;

	public Client(String sql) {// 一连接就会发送sql语句
		try {
			socket = new Socket("127.0.0.1", 4700);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CachedRowSetImpl readRowSet() {
		try {
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			CachedRowSetImpl crsi = (CachedRowSetImpl) ois.readObject();
			return crsi;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
