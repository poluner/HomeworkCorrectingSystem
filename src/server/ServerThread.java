package server;

import com.sun.rowset.CachedRowSetImpl;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class ServerThread extends Thread {
	private Socket socket;
	private Connection connection;

	public ServerThread(Socket socket, Connection connection) {
		try {
			this.socket = socket;
			this.connection = connection;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			String sql = (String) ois.readObject();
			if (connection.prepareStatement(sql).execute()) {// 如果是select语句，才有结果集，否则这句话执行以下就行了
				ResultSet rs = connection.prepareStatement(sql).executeQuery();

				CachedRowSetImpl crsi = new CachedRowSetImpl();
				crsi.populate(rs);
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject(crsi);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
