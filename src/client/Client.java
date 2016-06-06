package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.sun.rowset.CachedRowSetImpl;

public class Client {
	Socket socket;

	public Client() {
		try {
			socket = new Socket("127.0.0.1", 4700);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeSql(String sql) {// 将sql序列化传输
		try {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CachedRowSetImpl readRowSet() {// 接收后反序列化
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
