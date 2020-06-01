package Controller;

import javafx.util.converter.NumberStringConverter;

public class YearStringConverter extends NumberStringConverter {
	
	//if the string is not null then obj.toString if false then ""
	@Override
	public String toString(Number obj) {
		return obj != null ? obj.toString() : "";
	}
	
	//Conversion of the string to a number WITHOUT commas
	@Override
	public Number fromString(String string) {
		try {
			return Integer.parseInt(string);
		}catch (Exception e) {
			return 0;
		}
	}

}
