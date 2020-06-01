package Model;

import Database.PublisherTableGateway;
import View.SingletonSwitcher;
import javafx.beans.property.SimpleStringProperty;

public class Publisher {
	
	private int id;
	private SimpleStringProperty publisherName;
	private PublisherTableGateway publisherGateway;

	

	public Publisher() {
		id = 0;
		publisherName = new SimpleStringProperty();

	}

	public Publisher(String publisherName) 
	{
		this();
		setPublisherName(publisherName);
	}
	public boolean isValidID(int id) {		
		if(id < 0)
			return false;
		return true;
	}
	
	public boolean isValidPublisherName(String publisherName) {
		if(publisherName == null || publisherName.length() < 1 || publisherName.length() > 255)
			return false;
		return true;
	}
	
	/*@Override
	public String toString() {
		return "Publisher's Name: " + getPublisherName();
	}*/

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	public PublisherTableGateway getPublisherGateway() {
		return publisherGateway;
	}
	public void setPublisherGateway(PublisherTableGateway publisherGateway) {
		this.publisherGateway = publisherGateway;
	}
	
	
	public SimpleStringProperty publisherNameProperty() {
		return publisherName;
	}
	public String getPublisherName() {
		return publisherName.get();
	}
	public void setPublisherName(String publisherName) {
		this.publisherName.set(publisherName);
	}
	
}

