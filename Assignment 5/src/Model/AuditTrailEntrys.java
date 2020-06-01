package Model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Database.AppException;
import Database.AuditEntryTableGateway;
import Database.BookTableGateway;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class AuditTrailEntrys{
	
	private static Logger logger = LogManager.getLogger(AuditTrailEntrys.class);
	
	private int id;
	private SimpleObjectProperty<LocalDateTime> batDateAdded;
	private SimpleStringProperty message;
	private AuditEntryTableGateway auditGateway;
	private Book book;
	
	public AuditTrailEntrys() {
		batDateAdded = new SimpleObjectProperty<LocalDateTime>();
		message = new SimpleStringProperty();
	}
	public AuditTrailEntrys(String message) 
	{
		this();
		setMessage(message);
	}

	public AuditTrailEntrys(LocalDateTime batDateAdded, String message) 
	{
		this();
		setBatDate(batDateAdded);
		setMessage(message);
	}
	
	public boolean isValidID(int id) {		
		if(id < 0)
			return false;
		return true;
	}
	
	public boolean isValidMessage(String message) {
		if(message == null || message.length() < 1 || message.length() > 255)
			return false;
		return true;
	}
	
	public boolean isValidDate(LocalDate batDateAdded) {
		if(batDateAdded == null || !batDateAdded.isBefore(LocalDate.now()))
			return false;
		return true;
	}
	
	public void save() {
		try {
			logger.info("save called");
			if(true) {
				logger.info("Validated all input fields!");	
				logger.info("null pointer exception " + getBook().getId());
				logger.info(this.getId());
				logger.info(this);
				logger.info(auditGateway);
				if(this.getId() == 0)
					auditGateway.add(this);//this is where the error starts
			}
		} catch (AppException e) {				
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return "The Message is: " + getMessage();
	}

	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	public SimpleStringProperty messageProperty() {
		return message;
	}
	public String getMessage() {
		return message.get();
	}
	public void setMessage(String message) {
		this.message.set(message);
	}
	
	public ObjectProperty<LocalDateTime> batDateAddedProperty() { 
		return batDateAdded; 
	}
	public LocalDateTime getBatDate() {
		return batDateAdded.get();
	}	
	public void setBatDate(LocalDateTime batDateAdded) {
		this.batDateAdded.set(batDateAdded);
	}
	
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	
	public AuditEntryTableGateway getAuditGateway() {
		return auditGateway;
	}
	public void setAuditGateway(AuditEntryTableGateway auditGateway) {
		this.auditGateway = auditGateway;
	}
	
}
