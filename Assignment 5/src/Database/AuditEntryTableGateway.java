package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import Model.AuditTrailEntrys;
import View.Launcher;

public class AuditEntryTableGateway{
	
	private static Logger logger = LogManager.getLogger(AuditEntryTableGateway.class);
	private Connection conn;
	
	public AuditEntryTableGateway(Connection conn) {
		this.conn = conn;
	}
	
	public AuditEntryTableGateway() {
		
	}

	public void add(AuditTrailEntrys obj) throws AppException {
		PreparedStatement st = null;
		try {
			conn = ConnectionFactory.createConnection();
			st = conn.prepareStatement("insert into BookAuditTrailTable(id, book_id, entry_msg) values(null, ?, ?)");
			st.setInt(1, obj.getBook().getId());
			st.setString(2, obj.getMessage());
			st.executeUpdate();
			
			logger.info("Saving Audit: " + obj.getMessage());
		}  catch (SQLException e) {
			e.printStackTrace();
			throw new AppException("AuditEntry save failed");
		}finally {
			try {
				if(st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new AppException(e);
			}
		}
	}

}
