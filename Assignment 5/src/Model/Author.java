package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import Database.AppException;
import Database.AuditEntryTableGateway;
import Database.AuthorTableGateway;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Author {
	
	private int id;
	//private String authorGender;
	private SimpleStringProperty authorFirstName, authorLastName, authorWebsite, authorGender;	//Not all of these are great as StringProperties, but for now it'll have to do.
	private ObjectProperty<LocalDate> authorBirthDate;
	private AuthorTableGateway gateway;
	private AuditEntryTableGateway auditGateway;

	

	public Author() {
		authorFirstName = new SimpleStringProperty();
		authorLastName = new SimpleStringProperty();
		authorWebsite = new SimpleStringProperty();
		authorGender = new SimpleStringProperty("U");
		authorBirthDate = new SimpleObjectProperty<LocalDate>();
		id = 0;
		
	}

	public Author(String authorFirstName, String authorLastName, LocalDate authorBirthDate, 
			String authorGender, String authorWebsite) 
	{
		this();
		setAuthorFirstName(authorFirstName);
		setAuthorLastName(authorLastName);
		setDOB(authorBirthDate);
		setAuthorGender(authorGender);
		setAuthorWebsite(authorWebsite);
	}
	
	public void save() throws AppException {
		if(this.getId() == 0) {
			gateway.insertAuthor(this);
		} else {
			gateway.updateAuthor(this);
		}
	}
	
	public boolean isValidID(int id) {		
		if(id < 0)
			return false;
		return true;
	}
	
	public boolean isValidFirstName(String authorFirstName) {
		if(authorFirstName == null || authorFirstName.length() < 1 || authorFirstName.length() > 100)
			return false;
		return true;
	}

	public boolean isValidLastName(String authorLastName) {
		if(authorLastName == null || authorLastName.length() < 1 || authorLastName.length() > 100)
			return false;
		return true;
	}
	
	public boolean isValidGender(String authorGender) {
		if(authorGender == null || (!authorGender.equals("M") && !authorGender.equals("F") && !authorGender.equals("Unknown")))
			return false;
		return true;
	}

	public boolean isValidWebSite(String authorWebsite) {
		if(authorWebsite == null)
			return true;
		if(authorWebsite.length() > 100)
			return false;
		return true;
	}

	public boolean isValidDateOfBirth(LocalDate authorBirthDate) {
		if(authorBirthDate == null || !authorBirthDate.isBefore(LocalDate.now()))
			return false;
		return true;
	}
	

	@Override
	public String toString() {
		return "Full Name: " + getAuthorFirstName() + " " + getAuthorLastName();
	}

	/**
	 * Everything Below Here should be mutators/accesors/SimpleStringProperties, please feel free to ignore all of it.
	 */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public AuthorTableGateway getGateway() {
		return gateway;
	}

	public void setGateway(AuthorTableGateway gateway) {
		this.gateway = gateway;
	}

	public String getAuthorFirstName() {
		return authorFirstName.get();
	}

	public SimpleStringProperty authorFirstNameProperty() {
		return authorFirstName;
	}

	public String getAuthorWebsite() {
		return authorWebsite.get();
	}

	public SimpleStringProperty authorWebsiteProperty() {
		return authorWebsite;
	}

	public String getAuthorGender() {
		return authorGender.get();
	}

	public SimpleStringProperty authorGenderProperty() {
		return authorGender;
	}
	

	public void setAuthorFirstName(String authorFirstName) {
		this.authorFirstName.set(authorFirstName);
	}

	public void setAuthorLastName(String authorLastName) {
		this.authorLastName.set(authorLastName);
	}

	public void setAuthorWebsite(String authorWebsite) {
		this.authorWebsite.set(authorWebsite);
	}

	public void setAuthorGender(String authorGender) {
		this.authorGender.set(authorGender);
	}

	public String getAuthorLastName() {

		return authorLastName.get();
	}

	public ObjectProperty<LocalDate> authorBirthDateProperty() { 
		
		return authorBirthDate; 
	}
	
	public LocalDate getDOB() {
		return authorBirthDate.get();
	}
	
	public void setDOB(LocalDate date) {
		this.authorBirthDate.set(date);
	}
	public SimpleStringProperty authorLastNameProperty() {
		
		return authorLastName;  
	}
	public String getAuthorFullName() {
		
		return authorFirstName.get() + " " + authorLastName.get();
	}

	public void setAuthorName(String authorFirstName, String authorLastName) {
		
		this.authorFirstName.set(authorFirstName);
		this.authorLastName.set(authorLastName);
	}
	
	
}
