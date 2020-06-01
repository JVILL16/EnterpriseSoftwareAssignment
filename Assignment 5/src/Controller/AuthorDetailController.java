package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Database.AlertHelper;
import Database.AppException;
import Model.AuditTrailEntrys;
import Model.Author;
import View.SingletonSwitcher;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class AuthorDetailController implements Initializable, GeneralController {
	
private static Logger logger = LogManager.getLogger();
	
    @FXML private TextField FirstName;
    @FXML private TextField LastName;
    @FXML private TextField Website;
    @FXML private ComboBox<String> gender;
    @FXML private DatePicker datePicker;
    @FXML private Button Save;
   
    private Author author;

    
    public AuthorDetailController() {
    	
    }
    
    public AuthorDetailController(Author author) {
    	this();
        this.author = author;
        logger.info("Now showing: " + author.toString());
    }
    @FXML
    public void saveAuthorClicked() {
    	
    	logger.info("Author's info is saved");
    	
    	if(!author.isValidFirstName(author.getAuthorFullName())) {
    		logger.error("Invalid Author name " + author.getAuthorFullName());
    		
    		AlertHelper.showWarningMessage("ERROR", "Author's Name Invalid", "The name that you inputed is invalid, try again.");
    	return;
    	}
    	author.save();
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		gender.setItems(FXCollections.observableArrayList("M","F","U"));

        FirstName.textProperty().bindBidirectional(author.authorFirstNameProperty());
        LastName.textProperty().bindBidirectional(author.authorLastNameProperty());
        Website.textProperty().bindBidirectional(author.authorWebsiteProperty());
        gender.valueProperty().bindBidirectional(author.authorGenderProperty());
        datePicker.valueProperty().bindBidirectional(author.authorBirthDateProperty());
	}
}
