package Model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Database.AlertHelper;
import Database.AppException;
import Database.AuditEntryTableGateway;
import Database.AuthorBookTableGateway;
import Database.AuthorTableGateway;
import Database.BookTableGateway;
import Database.PublisherTableGateway;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class Book {
	
	private static Logger logger = LogManager.getLogger(Book.class);
	public Map<String, String[]> allMessages = new HashMap<String, String[]>();
	
	private int id;

	private SimpleStringProperty title, summary, isbn;
	private SimpleIntegerProperty yearPublished;
	private SimpleObjectProperty<LocalDateTime> lastModified;
	private SimpleObjectProperty<Publisher> publisher;
	private BookTableGateway bookGateway;
	private AuditEntryTableGateway auditGateway;
	private PublisherTableGateway pubGateway;
	private AuthorTableGateway authorGateway;
	private AuthorBookTableGateway abGateway;
	

	public Book() {
		title = new SimpleStringProperty();
		summary = new SimpleStringProperty();
		isbn = new SimpleStringProperty();
		yearPublished = new SimpleIntegerProperty();
		lastModified = new SimpleObjectProperty<LocalDateTime>();
		publisher = new SimpleObjectProperty<Publisher>();
		id = 0;
		auditGateway = new AuditEntryTableGateway();
		setupListeners();
		
	}

	public Book(String title, String summary, Integer yearPublished,
			Publisher publisher, String isbn) {
		this();
		setTitle(title);
		setSummary(summary);
		setYearPub(yearPublished);
		setPublisher(publisher);
		setISBN(isbn);
	}
	
	public void save() throws AppException{
		try {
			logger.info("save called");
			if(this.getId() == 0) {
				bookGateway.insertBook(this);
				AlertHelper.showWarningMessage(this.toString() + " is Saved", "Your new book is saved", "Congratulations");
				this.markAllMessagesValues();				
			}else {
				if(!this.isAllMessages()) 
					logger.info("book hasn't been changed, nothing to update");
				else
					bookGateway.updateBook(this);
				AlertHelper.showWarningMessage("Changes Saved", "Your changes have been saved", "");
				this.saveChanges();
			}
		} catch (AppException e) {				
			e.printStackTrace();
		}
	}
	//protected
	public void setupListeners() {
		title.addListener(changeListener);
		summary.addListener(changeListener);
		yearPublished.addListener(changeListener);
		publisher.addListener(changeListener);
		isbn.addListener(changeListener);
	}
	//protected
	public void markAllMessagesValues() { 
		clean();
		createAudit("Book added");
		logger.info("book added");
	}
	
	public AuditEntryTableGateway getAuditGateway() {
		return this.auditGateway;
	}
	
	public boolean isValidID(int id) {		
		if(id < 0)
			return false;
		return true;
	}

	public boolean isValidTitle(String title) {
		if(title == null || title.length() < 1 || title.length() > 255)
			return false;
		return true;			
	}

	public boolean isValidSummary(String summary) {
		if(summary == null)
			return true;
		if(summary.length() > 65536)
			return false;
		return true;
	}

	public boolean isValidYearPub(Integer yearPublished) {
		if(yearPublished < 1455 || yearPublished > 2019)
			return false;
		return true;
	}

	public boolean isValidISBN(String isbn) {	
		if(isbn.length() > 13)
			return false;
		return true;
	}
	
	public boolean isValidPublisher(Publisher publisher) {
		if (publisher == null)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return  "\"" + getTitle() + "\"";
	}	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public BookTableGateway getBookGateway() {
		return bookGateway;
	}

	public void setBookGateway(BookTableGateway bookGateway) {
		this.bookGateway = bookGateway;
	}

	public SimpleStringProperty titleProperty() {
		return title;
	}

	public String getTitle() {
		return title.get();
	}

	public void setTitle(String title) {
		this.title.set(title);
	}

	public SimpleStringProperty summaryProperty() {
		return summary;
	}

	public String getSummary() {
		return summary.get();
	}

	public void setSummary(String summary) {
		this.summary.set(summary);
	}

	public SimpleIntegerProperty yearPublishedProperty() {
		return yearPublished;
	}	

	public Integer getYearPub() {
		return yearPublished.get();
	}

	public void setYearPub(Integer yearPublished) {
		this.yearPublished.set(yearPublished);
	}	

	public SimpleStringProperty isbnProperty() {
		return isbn;
	}

	public String getISBN() {
		return isbn.get();
	}

	public void setISBN(String isbn) {
		this.isbn.set(isbn);
	}
	
	//publisher getters and setters
	public SimpleObjectProperty<Publisher> publisherProperty() { 
		return publisher; 
	}
	
	public Publisher getPublisher() {
		return publisher.get();
	}
	
	public void setPublisher(Publisher publisher) {
		this.publisher.set(publisher);
	}
	
	//last modified getters and setters
	public ObjectProperty<LocalDateTime> lastModifiedProperty() { 
		return lastModified; 
	}
	
	public LocalDateTime getLastModified() {
		return lastModified.get();
	}	

	public void setLastModified(LocalDateTime lastModified) {		
		this.lastModified.set(lastModified);
	}
	
	public List<AuditTrailEntrys> getAudits() {
		 List<AuditTrailEntrys> audits = this.bookGateway.getAuditTrail(this);
		 logger.info(audits);
		 for(AuditTrailEntrys entry : audits) 	 
			 entry.setAuditGateway(auditGateway);	
		 return audits;
		
	}
	
	public boolean isAllMessages() {
		return allMessages.size() > 0;
	}
	//protected
	public void markAllMessages(ObservableValue<?> property, Object oldValue, Object newValue) {
		String propertyName = getPropertyName(property);
		
		String[] old = allMessages.get(propertyName);
		String from = old != null ? old[0].toString() : oldValue.toString();
		String to = newValue.toString();
		allMessages.put(propertyName, new String[] {from, to});
	}
	
	public void clean() {
		allMessages.clear();
	}
	//protected
	public ChangeListener<Object> changeListener = new ChangeListener<Object>() {
		@Override
		public void changed(ObservableValue<?> o, Object oldVal, Object newVal) {
			if(oldVal != null && (o.getClass() != SimpleIntegerProperty.class || (Integer)oldVal != 0))
				markAllMessages(o, oldVal, newVal);
		}
	};
	
	public void saveChanges() {
		for(Entry<String, String[]> entry : allMessages.entrySet()) {
			String[] change = entry.getValue();
			String message = entry.getKey() + " changed from " + change[0] + " to " + change[1];
			logger.info(message);
			createAudit(message);
		}	 
		clean();
	}
	
	public void createAudit(String message) {
		AuditTrailEntrys audit = new AuditTrailEntrys(message);

		audit.setBook(this);
		audit.setAuditGateway(getAuditGateway());
		audit.save();
	}
	
	protected String getPropertyName(ObservableValue<?> property) {
		try {
			for(Field field : getClass().getDeclaredFields()) {
				if(!Modifier.isStatic(field.getModifiers())) {
					field.setAccessible(true);
					
					Object current = field.get(this);
					if(current == property) //found our property
						return field.getName();
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	public List<Author> getAuthors() throws AppException {
		List<Author> authors = this.bookGateway.getAuthors(this);
		
		for(Author author : authors)
			author.setGateway(authorGateway);
		
		return authors;
	}
	
	public List<AuthorBook> getAuthorBooks() throws AppException {
		return this.bookGateway.getAuthorBooks(this);
	}
}
