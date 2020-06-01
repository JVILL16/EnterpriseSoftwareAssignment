package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Database.AppException;
import Database.AuthorTableGateway;
import Model.Author;
import View.SingletonSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class AuthorListController implements Initializable, GeneralController {
	
	 private static Logger logger = LogManager.getLogger();
	
	 @FXML private ListView<Author> authorList;
	 private ObservableList<Author> authors;
	 @FXML private Button delete;

	private AuthorTableGateway gateway;

	 
	 public AuthorListController(AuthorTableGateway gateway) {
	    	this.gateway = gateway;
	    	//authors = this.gateway.getAuthors();
	    	fetch();
	 }
	 
	 @FXML void onDeleteClick(ActionEvent event) throws IOException {
		 Author author = authorList.getSelectionModel().getSelectedItem();
			 try {
				 this.gateway.deleteAuthor(author);
			 }catch (AppException e) {
				 
			 }
			 fetch();
	 }
	    
	 @FXML void switchToAuthorDetailView(MouseEvent event) throws IOException {
			try {
				if(event.getClickCount()==2) {
					logger.info("Author double clicked.");
					SingletonSwitcher.getInstance().changeView(4,authorList.getSelectionModel().getSelectedItem());
				}
			}catch(Exception e) {
				
			}
	    }
	 public void fetch() {
		 List<Author> authors = gateway.getAuthors();
		 if(this.authors == null)
			 this.authors = FXCollections.observableArrayList(authors);
		 else
			 this.authors.setAll(authors);
	 }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
			this.authorList.setItems(authors);
	}

}
