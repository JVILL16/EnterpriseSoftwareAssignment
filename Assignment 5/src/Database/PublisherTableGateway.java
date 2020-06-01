package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Model.Publisher;
import View.Launcher;

public class PublisherTableGateway {
	
	private static Logger logger = LogManager.getLogger(PublisherTableGateway.class);
	private Connection conn;
	
	
	public PublisherTableGateway(Connection conn) {
		this.conn = conn;
	}

	public List<Publisher> getPublishers() throws AppException {
		List<Publisher> publishers = new ArrayList<Publisher>();
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("select * from PublisherTable");
		
			ResultSet rs = st.executeQuery();
			while(rs.next()) {

				Publisher publisher = new Publisher();
				publisher.setPublisherName(rs.getString("publisher_name"));

				publisher.setPublisherGateway(this);
				publisher.setId(rs.getInt("id"));
				publishers.add(publisher);
			}
		} catch (SQLException e) {		
			e.printStackTrace();
			throw new AppException("RIGHT HERE");
		} finally {
			try {
				if(st != null)
					st.close();
				//if(conn != null)
					//conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new AppException(e);
			}
		}
		return publishers;
	}
	
	public Publisher getPublisherById(int id) throws AppException {
		Publisher publisher = null;
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("select * from PublisherTable where id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();

			rs.next();
			publisher = new Publisher(rs.getString("publisher_name"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return publisher;
	}
}
