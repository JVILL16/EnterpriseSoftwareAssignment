package Controller;

import Model.Publisher;
import javafx.util.StringConverter;

public class PublisherStringConverter extends StringConverter<Publisher>{
	
	@Override
	public String toString(Publisher object) {
	    return object.getPublisherName();
	}

	@Override
	public Publisher fromString(String string) {
	    return null;
	}
}

