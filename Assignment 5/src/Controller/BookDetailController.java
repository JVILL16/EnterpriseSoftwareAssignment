package Controller;

import java.net.URL;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Database.AlertHelper;
import Database.AppException;
import Database.AuthorBookTableGateway;
import Database.AuthorTableGateway;
import Database.BookTableGateway;
import Database.ConnectionFactory;
import Model.AuditTrailEntrys;
import Model.Author;
import Model.AuthorBook;
import Model.Book;
import Model.Publisher;
import View.SingletonSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class BookDetailController implements Initializable, GeneralController{
	
private static Logger logger = LogManager.getLogger();
	
    @FXML private TextField Title;
    @FXML private TextArea Summary;
    @FXML private TextField ISBN;
    @FXML private TextField YearPublished;
    @FXML private Button Save;
    @FXML private Button AuditTrail;
    @FXML private ComboBox<Publisher> pubCombo;
    @FXML private TableView<AuthorBook> tableView;
  	@FXML private ComboBox<Author> addAuthor;
  	@FXML private Button deleteAuthor;
  	@FXML private Button confirmAddAuthor;
  	@FXML private TextField setRoyalty;
    
    private Book book;
     
    private String string_book;
    private String string_summary;
    private String string_isbn;
    private int int_yearPublished;
    private Publisher publisher_id;
    
    private ObservableList<Author> authors;
    private ObservableList<AuthorBook> authorBooks;
    
    private LocalDateTime originalTimestamp;
    private Connection conn;
    private BookTableGateway bookGateway = new BookTableGateway(conn);
	private AuthorBookTableGateway abGateway = new AuthorBookTableGateway(conn);
	private AuthorTableGateway authorGateway = new AuthorTableGateway(conn);
    
    
    private ObservableList<Publisher> publishers;
    
    public BookDetailController() {
    	//this.abGateway = new AuthorBookTableGateway();
		//this.authorGateway = new AuthorTableGateway();
    }
    
    public BookDetailController(Book book, List<Publisher> publishers) {
    	this();
        logger.info("Now showing: " + book.toString());
        this.book = book;
        this.publishers = FXCollections.observableArrayList(publishers);
        updateAuthorBooks();
        
        string_book = book.getTitle();
        string_summary = book.getSummary();
        string_isbn = book.getISBN();
        int_yearPublished = book.getYearPub();
        publisher_id = book.getPublisher();
        
        
        originalTimestamp = book.getLastModified();
        logger.info(bookGateway); // kept saying null so tried to find connection
        logger.info(publisher_id);
        logger.info(book.getTitle());
    }
    
    @FXML
    public void saveBookClicked() {
    	logger.info("Book's info is saved");
    	
    	if(!book.isValidTitle(book.getTitle())) {
    		logger.error("Invalid Book: " + book.getTitle()); 		
    		AlertHelper.showWarningMessage("ERROR", "Book is Invalid", "The TITLE of the book "
    				+ "that you inputed is invalid, try again.");
    		return;
    	}
    	if(!book.isValidSummary(book.getSummary())) {
    		logger.error("Invalid Summary: " + book.getSummary()); 		
    		AlertHelper.showWarningMessage("ERROR", "Book is Invalid", "The SUMMARY of the book "
    				+ "that you inputed is invalid, try again.");
    		return;
    	}
    	if(!book.isValidISBN(book.getISBN())) {
    		logger.error("Invalid ISBN: " + book.getISBN()); 		
    		AlertHelper.showWarningMessage("ERROR", "Book is Invalid", "The ISBN of the book "
    				+ "that you inputed is invalid, try again.");
    		return;
    	}
    	if(!book.isValidYearPub(book.getYearPub())) {
    		logger.error("Invalid Year: " + book.getYearPub()); 		
    		AlertHelper.showWarningMessage("ERROR", "Book is Invalid", "The YEAR of the book "
    				+ "that you inputed is invalid, try again.");
    		return;
    	}
    	if(!book.isValidPublisher(book.getPublisher())) {
    		logger.error("Invalid Publisher: " + book.getPublisher()); 		
    		AlertHelper.showWarningMessage("ERROR", "Book is Invalid", "The PUBLISHER of the book "
    				+ "was not selected, try again.");
    		return;
    	}
    	
    	if(book.getId() == 0) {
    		book.save();
    		
    	} else {
	    	LocalDateTime currentTimestamp = bookGateway.getLastModifiedById(book.getId());
	    	logger.info("o ts:" + originalTimestamp);
			logger.info("c ts:" + currentTimestamp);
			if(!currentTimestamp.equals(originalTimestamp)) {
				AlertHelper.showWarningMessage("Cannot Save!", "Record has changed since this view "
						+ "loaded", "Please refresh your view and try again. :(");
				return;
			}
			originalTimestamp = book.getLastModified();
			
	    	book.save();
    	}
    }
    
    public boolean hasChanged() {
    	
    	if(book.getId() == 0) {
    		logger.info(YearPublished.getText());
    		if(Title.getText() != null) {
    			//logger.info(Title.getText());
    			//logger.info(book.getTitle());
    			return true;
    		}
    		if(Summary.getText() != null) {
    			//logger.info(Summary.getText());
    			//logger.info(summary.getSummary());
    			return true;
    		}
    		if(ISBN.getText() != null) {
    			return true;
    		}
    		if(!(YearPublished.getText().equals("0"))) {
    			return true;
    		}
    		if(pubCombo.getValue() != null) {
    			return true;
    		}
    		return false;
    	}
    	
		if(!(Title.getText().equals(string_book))) {
			//logger.info(Title.getText());
			//logger.info(book.getTitle());
			return true;
		}
		if(!(Summary.getText().equals(string_summary))) {
			//logger.info(Summary.getText());
			//logger.info(summary.getSummary());
			return true;
		}
		if(!(ISBN.getText().equals(string_isbn))) {
			return true;
		}
		if(book.getYearPub() != int_yearPublished) {
			return true;
		}
		if(book.getPublisher() != publisher_id) {
			return true;
		}
		//fall through: has not changed
		return false;
		
	}
    
	   public void updateAuthorBooks() {
			try {
				if(this.authorBooks != null) {
					this.authorBooks.clear();
					this.authorBooks.addAll(book.getAuthorBooks());
				} else
					this.authorBooks = FXCollections.observableArrayList(book.getAuthorBooks());
				logger.debug("authorBooks: " + this.authorBooks);
				if(tableView != null)
					tableView.setItems(authorBooks);
			} catch (AppException e) {
				e.printStackTrace();
			}
	    }
	    @FXML
		void onDeleteAuthorClicked(ActionEvent event) {
	    	AuthorBook authorBook = tableView.getSelectionModel().getSelectedItem();
			if(authorBook != null) {
				Author author = authorBook.getAuthor();
		    		Alert alert = new Alert(AlertType.WARNING, "Are you sure you wish to delete author, '" +  author.getAuthorFirstName() + " "+ author.getAuthorLastName() + "'?", ButtonType.OK, ButtonType.CANCEL);
		    		Optional<ButtonType> result = alert.showAndWait();
		    		if (result.get() == ButtonType.OK) {
		    			try {
							this.abGateway.deleteAB(authorBook);
							
							updateAuthorBooks();
							
							book.createAudit("Author " +  author.getAuthorFirstName() + " "+ author.getAuthorLastName() + " removed from book");
		    			}catch (AppException e) {
		    				e.printStackTrace();
						}
					}
			}
		}
	    @FXML
	    void onAddAuthorClicked(ActionEvent event) {
			logger.info("Adding author");
			
			Author author = addAuthor.valueProperty().get();
			logger.info(author);
			int royalty = (int)(Double.parseDouble(setRoyalty.getText()) * 100000);
			
			AuthorBook authorBook = new AuthorBook(author, this.book, royalty);
			authorBook.setNewRecord(true);
			authorBook.setABGateway(abGateway);
			authorBook.save();
				
			updateAuthorBooks();
			
			book.createAudit("Author " + author.getAuthorFirstName() + " "+ author.getAuthorLastName() + " added to book");
	    }
	
	   @FXML
	    public void auditTrailViewClicked(ActionEvent event) {
	    	logger.info("Audit Trail View for Book");
	    	try {
		    	List<AuditTrailEntrys> audits = book.getAudits();
		    	if(audits.size() > 0) {
		    		logger.info("Audits: " + audits);	    	
		        	SingletonSwitcher.getInstance().changeView(2, book);
		    	}
		    	else {
		    		logger.error("No Audit Trail Exist");
		    		AlertHelper.showWarningMessage("ERROR", "Book has no Audit Trail", "The audit trail does not exist");
		    	}
	    	}catch(AppException e) {
		    		e.printStackTrace();
		    }
	    	
	    }
	   
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		pubCombo.setItems(publishers);
		
		pubCombo.setConverter(new PublisherStringConverter());
		

		List<Author> authorList;
		try {
			authorList = authorGateway.getAuthors();
			authors = FXCollections.observableArrayList(authorList);
			addAuthor.setItems(authors);
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
        Title.textProperty().bindBidirectional(book.titleProperty());
        //book.titleProperty().bindBidirectional(Title.textProperty());
		Summary.textProperty().bindBidirectional(book.summaryProperty());
        ISBN.textProperty().bindBidirectional(book.isbnProperty());
        YearPublished.textProperty().bindBidirectional(book.yearPublishedProperty(), new YearStringConverter());
        pubCombo.valueProperty().bindBidirectional(book.publisherProperty());
        
        TableColumn<AuthorBook, String> authorCol = new TableColumn<>("Author");
        authorCol.setPrefWidth(306);
        authorCol.setCellValueFactory(new PropertyValueFactory<AuthorBook, String>("author"));

        TableColumn<AuthorBook, String> royaltyCol = new TableColumn<>("Royalty");
        royaltyCol.setPrefWidth(80);
        royaltyCol.setCellValueFactory(new PropertyValueFactory<AuthorBook, String>("royalty"));
  
		tableView.getColumns().setAll(authorCol, royaltyCol);
		tableView.setItems(authorBooks);
        
	}
}
