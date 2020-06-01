package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Model.Author;
import Model.AuthorBook;
import Model.Book;
import Model.Publisher;


public class AuthorBookTableGateway {

	private static Logger logger = LogManager.getLogger(AuthorBookTableGateway.class);

	private Connection conn;
	
	public AuthorBookTableGateway(Connection conn) {
		this.conn = conn;
	}
	public AuthorBookTableGateway() {
		
	}

	public void insertAB(AuthorBook obj) throws AppException {
		PreparedStatement st = null;
		try {

			conn = ConnectionFactory.createConnection();
			st = conn.prepareStatement("INSERT INTO Author_BookTable(author_id, book_id, royalty) VALUES(?, ?, ?)");
			st.setInt(1, obj.getAuthor().getId());
			st.setInt(2, obj.getBook().getId());
			st.setFloat(3, obj.getRoyalty() / 100000.0f);
			st.executeUpdate();

			logger.info("added author " + obj.getAuthor().getAuthorFullName() + " to book " + obj.getBook().getTitle());
		} catch (SQLException e) {
			if(e.getMessage().startsWith("Duplicate entry"))
				throw new DuplicateKeyException(e.getMessage());
			else {
				e.printStackTrace();
				throw new AppException("Book insert failed: " + e.getMessage());
			}
		} finally {
			try {
				if(st != null)
					st.close();

			} catch (SQLException e) {
				e.printStackTrace();
				throw new AppException("Book insert failed");
			}
		}
	}

	public void updateAB(AuthorBook obj) throws AppException {
		PreparedStatement st = null;
		try {
			conn = ConnectionFactory.createConnection();
			st = conn.prepareStatement("UPDATE Author_BookTable SET royalty = ? WHERE author_id = ? and book_id = ?");
			st.setFloat(1, obj.getRoyalty() / 100000.0f);
			st.setInt(2, obj.getAuthor().getId());
			st.setInt(3, obj.getBook().getId());
			st.executeUpdate();
			
			logger.info("updated author royalty for " + obj.getAuthor().getAuthorFullName() + " to " + obj.getRoyalty() / 100000.0f);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException("Royalty update failed: " + e.getMessage());
		} finally {
			try {
				if(st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new AppException("Royalty update failed");
			}
		}
	}

	public List<AuthorBook> getAB() throws AppException {
		List<AuthorBook> authorBooks = new ArrayList<>();
		
		PreparedStatement st = null;
		try {

			conn = ConnectionFactory.createConnection();
			String sql = "select * from Author_BookTable ab " +
						 "inner join AuthorTable a on (ab.author_id = a.id) " +
						 "inner join BookTable b on (ab.book_id = b.id) " +
						 "inner join PublisherTable p on (b.publisher_id = p.id) ";
			
			st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery(); 
		    
			while (rs.next()) {
				
				//Getting author
				Author author = new Author();
				author.setId(rs.getInt("a.id"));
				author.setAuthorFirstName(rs.getString("a.first_name"));
				author.setAuthorLastName(rs.getString("a.last_name"));
				author.setDOB(rs.getDate("a.dob").toLocalDate());
				author.setAuthorGender(rs.getString("a.gender"));
				author.setAuthorWebsite(rs.getString("a.web_site"));
				
				//Getting book with publisher
				String publisherName = rs.getString("p.publisher_name");
				int id = rs.getInt("p.id");			
				Publisher publisher = new Publisher(publisherName);
				publisher.setId(id);
				String title = rs.getString("b.title");
				String summary = rs.getString("b.summary");
				int year_pub = rs.getInt("b.year_published");
				String isbn = rs.getString("b.isbn");
				Book book = new Book(title, summary, year_pub, publisher, isbn);
				book.setId(rs.getInt("b.id"));
				
				
				int royalty = (int)(rs.getFloat("royalty") * 100000);
				
				AuthorBook authorBook = new AuthorBook(author, book, royalty);
				authorBook.setABGateway(this);
				authorBooks.add(authorBook);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException("Author table fetch failed");
		} finally {
			try {
				if(st != null)
					st.close();

			} catch (SQLException e) {
				e.printStackTrace();
				throw new AppException("Author table fetch failed");
			}
		}
		
		return authorBooks;
	}

	public void deleteAB(AuthorBook obj) throws AppException {
		PreparedStatement st = null;
		try {

			conn = ConnectionFactory.createConnection();
			st = conn.prepareStatement("delete from Author_BookTable where author_id = ? and book_id = ?");
			logger.info(obj.getAuthor().getId());
			logger.info(obj.getBook().getId());
			st.setInt(1, obj.getAuthor().getId());
			st.setInt(2, obj.getBook().getId());
			st.executeUpdate();
			
		}  catch (SQLException e) {
			e.printStackTrace();
			throw new AppException("AuthorBook delete failed");
		} finally {
			try {
				if(st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new AppException("AuthorBook delete failed");
			}
		}
	}

}
