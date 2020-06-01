package Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Model.AuditTrailEntrys;
import Model.Author;
import Model.AuthorBook;
import Model.Book;
import Model.Publisher;
import View.Launcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BookTableGateway {
	
	private static Logger logger = LogManager.getLogger(BookTableGateway.class);
	private Connection conn;
	private AuthorBookTableGateway abGateway;
	
	public BookTableGateway(Connection conn) {
		this.conn = conn;
	}
	public BookTableGateway() {
		abGateway = new AuthorBookTableGateway();
	}
	public void deleteBook(Book book) throws AppException {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("delete from BookTable where id = ?");

			st.setInt(1, book.getId());
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
	
	public void insertBook(Book book) throws AppException {
		PreparedStatement st = null;
		try {
			String sql = "insert into BookTable "
					+ " (title, summary,  year_published, publisher_id, isbn) "
					+ " values (?, ?, ?, ?, ?) ";
			st = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			st.setString(1, book.getTitle());
			st.setString(2, book.getSummary());
			st.setInt(3, book.getYearPub());
			st.setInt(4, book.getPublisher().getId());
			st.setString(5, book.getISBN());
			st.executeUpdate();
			ResultSet rs = st.getGeneratedKeys();
			rs.first();
			book.setLastModified(null);
			book.setId(rs.getInt(1));
			book.getAudits();
			
			
			
			logger.info("New id is " + book.getId());
			
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
	
	public void updateBook(Book book) throws AppException {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("update BookTable set title = ?, summary = ?, year_published = ?,"
					+ " publisher_id = ?, isbn = ? where id = ?");
			st.setString(1, book.getTitle());
			st.setString(2, book.getSummary());
			st.setInt(3, book.getYearPub());
			st.setInt(4, book.getPublisher().getId());
			st.setString(5, book.getISBN());
			st.setInt(6, book.getId());
			st.executeUpdate();
			
			book.setLastModified(this.getLastModifiedById(book.getId()));
			book.getAudits();
			
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
	
	public List<Book> getBooks(String searchVar, int page, int pageSize) throws AppException {
		List<Book> books = new ArrayList<>();
		
		PreparedStatement st = null;
		try {
			conn = ConnectionFactory.createConnection();
		
			String query = "select * from BookTable b inner join PublisherTable p on (b.publisher_id = p.id)";
			if(searchVar != null)
				query += " where title like ?";
			query += " order by b.title asc";
			query += " limit " + pageSize + " offset " + (pageSize * page);
			
			st = conn.prepareStatement(query);
			
			if(searchVar != null)
				st.setString(1,  "%" + searchVar + "%");
			
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {

				String publisherName = rs.getString("p.publisher_name");
				int id = rs.getInt("p.id");			
				Publisher publisher = new Publisher(publisherName);
				publisher.setId(id);
				
				Timestamp ts = rs.getTimestamp("last_modified");
				
				String title = rs.getString("title");
				String summary = rs.getString("summary");
				int year_pub = rs.getInt("year_published");
				String isbn = rs.getString("isbn");
				Book book = new Book(title, summary, year_pub, publisher, isbn);			
				book.setLastModified(ts.toLocalDateTime());

				book.setBookGateway(this);
				book.setId(rs.getInt("id"));	
				books.add(book);
				
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
		return books;
	}
	
	public LocalDateTime getLastModifiedById(int id) throws AppException {
		LocalDateTime date = null;
		PreparedStatement st = null;
		try {
			conn = ConnectionFactory.createConnection();
			st = conn.prepareStatement("select * from BookTable where id = ?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			rs.next();
			Timestamp ts = rs.getTimestamp("last_modified");
			date = ts.toLocalDateTime();
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
	
		return date;
		
	}
	
	public List<AuditTrailEntrys> getAuditTrail(Book book) throws AppException {
		List<AuditTrailEntrys> audits = new ArrayList<>();
	
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("select * from BookAuditTrailTable where "
					+ "book_id = ? order by date_added ASC");
			st.setInt(1,book.getId());
	
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				int id = rs.getInt("id");
				Timestamp batDateAdded = rs.getTimestamp("date_added"); 
				String message = rs.getString("entry_msg");
				AuditTrailEntrys audit = new AuditTrailEntrys(batDateAdded.toLocalDateTime(), message);
				audit.setId(id);
				audit.setBook(book);
				audits.add(audit);
				
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
		return audits;
	}
	
	public List<Author> getAuthors(Book book) throws AppException {
		List<AuthorBook> authorBooks = getAuthorBooks(book);

		List<Author> authors = new ArrayList<>();
		if(book.getId() == 0)
			return authors;
		
		for(AuthorBook ab : authorBooks)
			authors.add(ab.getAuthor());
		
		return authors;
	}
	
	public List<AuthorBook> getAuthorBooks(Book book) throws AppException {
		List<AuthorBook> authorBooks = new ArrayList<>();
		
		if(book.getId() == 0)
			return authorBooks;
		
		PreparedStatement st = null;
		try {
			conn = ConnectionFactory.createConnection();

			String sql = "select * from Author_BookTable ab " +
						 "inner join AuthorTable a on (ab.author_id = a.id) " +
						 "where book_id = ? " +
						 "order by last_name ASC ";
			
			st = conn.prepareStatement(sql);
			st.setInt(1, book.getId());
			
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				Author author = new Author();
				author.setId(rs.getInt("a.id"));
				author.setAuthorFirstName(rs.getString("a.first_name"));
				author.setAuthorLastName(rs.getString("a.last_name"));
				author.setDOB(rs.getDate("a.dob").toLocalDate());
				author.setAuthorGender(rs.getString("a.gender"));
				author.setAuthorWebsite(rs.getString("a.web_site"));
				
				int royalty = (int)(rs.getFloat("royalty") * 100000);

				AuthorBook authorBook = new AuthorBook(author, book, royalty);
				authorBook.setABGateway(abGateway);
				authorBooks.add(authorBook);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException("AuditTrail fetch failed");
		} finally {
			try {
				if(st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new AppException("AuditTrail fetch failed");
			}
		}
		
		return authorBooks;
	}
	
	public List<Book> search(String searchVar) throws AppException {
		List<Book> books = new ArrayList<>();
		PreparedStatement ps = null;
		try {
			conn = ConnectionFactory.createConnection();
		
			String query = "select * from book b inner join publisher p on (b.publisher_id = p.id) order by b.title asc";
			if(searchVar != null)
				query += " where title like ?";
			
			ps = conn.prepareStatement(query);
			if(searchVar != null)
				ps.setString(1,  "%" + searchVar + "%");
			
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				
				String publisherName = rs.getString("p.publisher_name");
				int id = rs.getInt("p.id");			
				Publisher publisher = new Publisher(publisherName);
				publisher.setId(id);
				
				String title = rs.getString("title");
				String summary = rs.getString("summary");
				int year_pub = rs.getInt("year_published");
				String isbn = rs.getString("isbn");
				Book book = new Book(title, summary, year_pub, publisher, isbn);
				book.setBookGateway(this);
				books.add(book);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException("Book table fetch failed");
		} finally {
			try {
				if(ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new AppException("Book table fetch failed");
			}
		}
		
		return books;
	}
	
	public int maxNumberRecords(String searchVar) {
		int rv = 0;
		conn = ConnectionFactory.createConnection();
		
		try {
			Statement statement = conn.createStatement();
			ResultSet rs;
			if(searchVar != null) {
				rs = statement.executeQuery("select COUNT(*) from BookTable where title like '%" + searchVar + "%'");
			}
			else {
				rs = statement.executeQuery("select COUNT(*) from BookTable");
			}
			rs.next();
			rv = rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rv;
	}
	
}
