package work.crystalnet.quartz.record;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

import lombok.extern.java.Log;
import work.crystalnet.quartz.info.RecordMemory;

@Log
public class DataRecord {

	private Connection connection;
	private final String url;

	public DataRecord(String url) {
		this.url = url;
	}

	public boolean load(UUID uid, RecordMemory memory) {
		try {
			openConnection();
			String sql = "select * from users where uid = ?";
			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.setString(1, uid.toString());
				try (ResultSet result = statement.executeQuery()) {
					while (result.next()) {
						memory.setRank(result.getInt("rank"));
						memory.setPoint(result.getInt("point"));
						memory.setCoin(result.getInt("coin"));
						memory.setToken(result.getInt("token"));
						memory.setLastIp(result.getString("ip"));
					}
				}
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "データベースに接続できませんでした", e);
			return false;
		} finally {
			try {
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				log.log(Level.SEVERE, "データベースとの切断に失敗しました", e);
			}
		}
		return true;
	}

	public boolean write(UUID uid, RecordMemory memory, InetSocketAddress address) {
		try {
			openConnection();
			String sql = "replace into users values (?, ?, ?, ?, ?, ?)";
			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.setString(1, uid.toString());
				statement.setInt(2, memory.getRank());
				statement.setInt(3, memory.getPoint());
				statement.setInt(4, memory.getCoin());
				statement.setInt(5, memory.getToken());
				statement.setString(6, address.getAddress().getHostAddress());
				int result = statement.executeUpdate();
				log.config("データベースと同期しました, Result: " + result);
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "データベースに接続できませんでした", e);
			return false;
		} finally {
			try {
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				log.log(Level.SEVERE, "データベースとの切断に失敗しました", e);
			}
		}
		return true;
	}

	private void openConnection() throws SQLException {
		if (connection != null && !connection.isClosed())
			return;

		synchronized (this) {
			if (connection != null && !connection.isClosed())
				return;
			connection = DriverManager.getConnection(url);
		}
	}
}
