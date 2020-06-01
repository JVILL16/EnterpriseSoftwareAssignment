package Controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javax.swing.text.html.ListView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Database.AuthorTableGateway;
import Database.BookTableGateway;
import Model.Author;
import Model.Book;
import View.Launcher;
import View.SingletonSwitcher;

public class MenuController implements Initializable {

    @FXML private ObservableList<Book> books;
	@FXML private MenuItem menuExit;
	@FXML private MenuItem bookList;
	@FXML private MenuItem addBook;
	@FXML private MenuItem authorList;
	@FXML private MenuItem addAuthor;

    @FXML private MenuBar menuBar;
    @FXML private BorderPane rootPane;
	
    private static Logger logger = LogManager.getLogger(MenuController.class);

    @FXML private void handleMenuAction(ActionEvent event) throws IOException {

		if(event.getSource() == bookList) {
	        logger.info("BookList button selected.");
	        SingletonSwitcher.getInstance().changeView(0, new Book());
		}else if (event.getSource() == addBook){ 
			logger.info("AddBook button selected.");
			Book book = new Book();
			book.setBookGateway(new BookTableGateway(SingletonSwitcher.getInstance().getConnection()));	
			SingletonSwitcher.getInstance().changeView(1, book);
		}else if(event.getSource() == authorList) {
	        logger.info("AuthorList button selected.");
	        SingletonSwitcher.getInstance().changeView(3, new Author());
		}else if (event.getSource() == addAuthor){ 
			logger.info("AddAuthor button selected.");
			Author author = new Author();
			author.setGateway(new AuthorTableGateway(SingletonSwitcher.getInstance().getConnection()));	
			SingletonSwitcher.getInstance().changeView(4, author);
		}else if(event.getSource() == menuExit) {
			logger.info("Exiting...");
			System.exit(0);
			logger.info("Exited.");
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		logger.info("Main Menu Loaded.");
	}
}
	
