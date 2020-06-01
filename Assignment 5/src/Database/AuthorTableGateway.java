package Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Model.AuditTrailEntrys;
import Model.Author;
import View.Launcher;

public class AuthorTableGateway {
	
	private static Logger logger = LogManager.getLogger(Launcher.class);
	private Connection conn;
	
	public AuthorTableGateway(Connection conn) {
		this.conn = conn;
	}
	public AuthorTableGateway() {

	}
	public void deleteAuthor(Author author) throws AppException {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("delete from AuthorTable where id = ?");

			st.setInt(1, author.getId());
			st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException(e);
		} finally {
			try {
				if(st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new AppException(e);
			}
		}
	}
	public void insertAuthor(Author author) throws AppException {
		PreparedStatement st = null;
		try {
			String sql = "insert into AuthorTable "
					+ " (first_name, last_name, dob, gender, web_site) "
					+ " values (?, ?, ?, ?, ?) ";
			st = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			st.setString(1, author.getAuthorFirstName());
			st.setString(2, author.getAuthorLastName());
			st.setDate(3, Date.valueOf(author.getDOB()));
			st.setString(4, author.getAuthorGender());
			st.setString(5, author.getAuthorWebsite());
			st.executeUpdate();
			ResultSet rs = st.getGeneratedKeys();
			rs.first();
			author.setId(rs.getInt(1));
			
			logger.info("New id is " + author.getId());
			
			rs.close();

		} catch (SQLException e) {
			throw new AppException(e);
		} finally {
			try {
				st.close();
			} catch (SQLException e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
	}
	
	public void updateAuthor(Author author) throws AppException {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("update AuthorTable set first_name = ?, last_name = ?, dob = ?, gender = ?, web_site = ? where id = ?");
			st.setString(1, author.getAuthorFirstName());
			st.setString(2, author.getAuthorLastName());
			st.setDate(3, Date.valueOf(author.getDOB()));
			st.setString(4, author.getAuthorGender());
			st.setString(5, author.getAuthorWebsite());
			st.setInt(6, author.getId());
			
			st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException(e);
		} finally {
			try {
				if(st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new AppException(e);
			}
		}
	}
	public List<Author> getAuthors() throws AppException {
		List<Author> authors = new ArrayList<>();

		PreparedStatement st = null;

		try {

			conn = ConnectionFactory.createConnection();
			st = conn.prepareStatement("select * from AuthorTable order by first_name");

			ResultSet rs = st.executeQuery();
			while(rs.next()) {

				Author author = new Author();
				author.setAuthorFirstName(rs.getString("first_name"));
				author.setAuthorLastName(rs.getString("last_name"));
				author.setAuthorGender(rs.getString("gender"));
				author.setDOB(rs.getDate("dob").toLocalDate());
				author.setAuthorWebsite(rs.getString("web_site"));
				author.setGateway(this);
				author.setId(rs.getInt("id"));
				authors.add(author);
			}
		} catch (SQLException e) {
		
			///e.printStackTrace();
			throw new AppException(e);
		} finally {
			try {
				if(st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new AppException(e);
			}
		}
		return authors;
	}
	public LocalDateTime getAuthorLastModifiedById(int id) throws AppException {
		LocalDateTime date = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("select * from AuthorTable where id = ?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			rs.next();
			Timestamp ts = rs.getTimestamp("last_modified");
			date = ts.toLocalDateTime();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException("modify last author error");
		} finally {
			try {
				if(st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new AppException("modify last author error");
			}
		}
		return date;
	}
	
	
	
}
