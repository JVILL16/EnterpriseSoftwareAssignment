package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Database.AppException;
import Database.BookTableGateway;
import Database.PublisherTableGateway;
import Model.Book;
import View.SingletonSwitcher;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class BookListController implements Initializable, GeneralController {
	
	 private static Logger logger = LogManager.getLogger(BookListController.class);
	
	 @FXML private ListView<Book> bookList;
	 @FXML private Button searchButton;
	 @FXML private TextField searchTitle;
	 @FXML private Button firstPage;
     @FXML private Button prevPage;
     @FXML private Button nextPage;
     @FXML private Button lastPage;
     @FXML private Label entryNumber;
	    
	 private ObservableList<Book> books;
	 private BookTableGateway bookGateway;
	 private PublisherTableGateway pubGateway;
	 private int pageNm = 0;
	 private int maxRecords;
	    
	 public BookListController(BookTableGateway bookGateway, PublisherTableGateway pubGateway) {
	    this.bookGateway = bookGateway;
	    this.pubGateway = pubGateway;
	    fetch(null,0);
	 }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		maxRecords = bookGateway.maxNumberRecords(null);
		if(maxRecords < 50) {
			entryNumber.setText("Fetched records 1" + " to " + maxRecords + " out of " + maxRecords);
		}else {
			entryNumber.setText("Fetched records 1" + " to " + (pageNm+1)*50 + " out of " + maxRecords);
		}
		this.bookList.setItems(books);
	}
	@FXML
	public void switchToBookDetailView(MouseEvent event) throws IOException{
		try {
			if(event.getClickCount()==2) {
				logger.info("Book double clicked");
				SingletonSwitcher.getInstance().changeView(1,bookList.getSelectionModel().getSelectedItem());
			}
		}catch(Exception e) {
				
		}
	}
	
	 public void fetch(String searchVar, int pageNumber) {
		 try {
			maxRecords = bookGateway.maxNumberRecords(searchVar);
			List<Book> books = bookGateway.getBooks(searchVar, pageNumber, 50);
    		logger.info("searching books: " + books);
    		if(this.books == null)
    			this.books = FXCollections.observableArrayList(books);
    		else
    			this.books.setAll(books);
		} catch (AppException e) {
			e.printStackTrace();
		}
	 }
	
	@FXML void onDeleteClick(ActionEvent event) throws IOException {
		 Book book = bookList.getSelectionModel().getSelectedItem();
		 if(book != null) {
    		Alert alert = new Alert(AlertType.WARNING, "Are you sure you wish to delete book, '" + book.getTitle() + "'?", ButtonType.OK, ButtonType.CANCEL);
    		Optional<ButtonType> result = alert.showAndWait();
    		if (result.get() == ButtonType.OK) {
	    		try {
					this.bookGateway.deleteBook(book);
					maxRecords = bookGateway.maxNumberRecords(null);
					if(maxRecords < 50) {
		    			entryNumber.setText("Fetched records 1" + " to " + maxRecords + " out of " + maxRecords);
		    		}else {
		    			entryNumber.setText("Fetched records 1" + " to " + (pageNm+1)*50 + " out of " + maxRecords);
		    		}
				} catch (AppException e) {
					e.printStackTrace();
				}
	   
			 fetch(null, pageNm);
	    	}
		 }
	 }
	
    @FXML
    void onSearchClick(ActionEvent event) {
    		logger.info("calling search for " + searchTitle.getText());
    		fetch(searchTitle.getText(), 0);
    		if(pageNm > maxRecords / 50) {
    			pageNm = maxRecords / 50;
    		}
    		if((pageNm+1)*50 > maxRecords) {
    			entryNumber.setText("Fetched records " + (pageNm*50+1) + " to " + maxRecords + " out of " + maxRecords);
    		}
    		else {
    			entryNumber.setText("Fetched records " + (pageNm*50+1) + " to " + (pageNm+1)*50 + " out of " + maxRecords);
    		}
    		//entryNumber.setText("Size is " + maxRecords);
    }
    
    @FXML
    void onAction(ActionEvent event) {
    		onSearchClick(event);
    }
    
    @FXML
    void onFirstPageClicked(ActionEvent event) {
		pageNm = 0;
		if(maxRecords < 50) {
			entryNumber.setText("Fetched records 1" + " to " + maxRecords + " out of " + maxRecords);
		}
		else {
			entryNumber.setText("Fetched records 1" + " to " + (pageNm+1)*50 + " out of " + maxRecords);
		}
		fetch(searchTitle.getText(), 0);

}

	@FXML
	void onLastPageClicked(ActionEvent event) {
			pageNm = maxRecords / 50;
			entryNumber.setText("Fetched records " + (pageNm*50+1) + " to " + maxRecords + " out of " + maxRecords);
			fetch(searchTitle.getText(), pageNm);
	}
	
	@FXML
	void onNextPageClicked(ActionEvent event) {
			pageNm++;
			if(pageNm > maxRecords / 50) {
				pageNm = maxRecords / 50;
			}
			if((pageNm+1)*50 > maxRecords) {
				entryNumber.setText("Fetched records " + (pageNm*50+1) + " to " + maxRecords + " out of " + maxRecords);
			}
			else {
				entryNumber.setText("Fetched records " + (pageNm*50+1) + " to " + (pageNm+1)*50 + " out of " + maxRecords);
			}
			fetch(searchTitle.getText(), pageNm);
	}
	
	@FXML
	void onPrevPageClicked(ActionEvent event) {
			pageNm--;
			if(pageNm < 0) {
				pageNm = 0;
			}
			entryNumber.setText("Fetched records " + (pageNm*50+1) + " to " + (pageNm+1)*50 + " out of " + maxRecords);
			fetch(searchTitle.getText(), pageNm);
	}

}
