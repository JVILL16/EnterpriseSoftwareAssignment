package View;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.fxml.FXML;

import java.net.URL;
import java.sql.Connection;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Controller.MenuController;
import Database.AppException;
import Database.BookTableGateway;
import Database.ConnectionFactory;
import Model.Book;
import Model.Publisher;

/**
 * @author CS4743 Assignment 4 by Jheremi Villarreal
 *
 */
public class Launcher extends Application{

	private ObservableList<Book> books;
	private static Logger logger = LogManager.getLogger(Launcher.class);
	private Connection conn;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		URL fxmlFile = this.getClass().getResource("MainMenu.fxml");
		FXMLLoader loader = new FXMLLoader(fxmlFile);

		BorderPane rootNode = loader.load();
		Scene scene = new Scene(rootNode, 600, 500);
		primaryStage.setTitle("Book Inventory System");
		primaryStage.setScene(scene);
		primaryStage.show();		

		
		SingletonSwitcher viewSwitch = SingletonSwitcher.getInstance();
		viewSwitch.setConnection(conn);
		viewSwitch.setRootNode((BorderPane) rootNode);
		viewSwitch.setBooks(books);



	}
	@Override
	public void init() throws Exception {
		super.init();
		books = FXCollections.observableArrayList();
		logger.info("Creating connection...");
		
		try {
			conn = ConnectionFactory.createConnection();
		} catch(AppException e) {
			logger.fatal("Cannot connect to db");
			Platform.exit();
		}
		
		//to make the 100,000 book records
	/*	conn = ConnectionFactory.createConnection();
		BookTableGateway gateway = new BookTableGateway(conn);
        Random rm = new Random();
        for(int i = 0; i < 100000; i++) {
            String title = "Sequential Book #" + i;
            String summary = "super cool generated book #" + i;
            int year = 1970 + rm.nextInt(38);
            String isbn = "" + rm.nextInt();

            Publisher publisher = new Publisher();
            publisher.setId(1);

            Book book = new Book(title, summary, year, publisher, isbn);
            gateway.insertBook(book);
        }*/
	}


	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		super.stop();
		logger.info("Closing connection...");
		
		conn.close();
	}


	public static void main(String[] args) {
		launch(args);

	}
}
