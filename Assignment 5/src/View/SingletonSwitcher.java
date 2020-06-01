package View;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Controller.AuditTrailController;
import Controller.AuthorDetailController;
import Controller.AuthorListController;
import Controller.BookDetailController;
import Controller.BookListController;
import Controller.GeneralController;
import Database.AppException;
import Database.AuthorTableGateway;
import Database.BookTableGateway;
import Database.PublisherTableGateway;
import Model.AuditTrailEntrys;
import Model.Author;
import Model.Book;
import Model.Publisher;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class SingletonSwitcher {
	private ObservableList<Book> books;
	private List<Publisher> publishers;
	private List<AuditTrailEntrys> audits;
	
	public static final int BOOK_LIST = 0;
	public static final int BOOK_DETAIL = 1;
	public static final int BOOK_AUDIT_TRAIL = 2;
	public static final int AUTHOR_LIST = 3;
	public static final int AUTHOR_DETAIL = 4;
	private static Logger logger = LogManager.getLogger(SingletonSwitcher.class);
	static  SingletonSwitcher singletonController = null;
	private BorderPane rootPane;
	private Connection conn;
	
	private BookDetailController bd;
	private PublisherTableGateway pubGateway;
	private BookTableGateway bookGateway;
	
	private SingletonSwitcher() {
		bd = null;
	}

	public static SingletonSwitcher getInstance() {
		if(singletonController == null) {
			singletonController = new SingletonSwitcher();
		}
		return singletonController;
	}
	
	public void setRootNode(BorderPane rootNode) {
		this.rootPane = rootNode;
	}
	
	public void setBooks(ObservableList<Book> bookList){
		this.books = bookList;

	}
	
	
    public boolean changeView(int viewType, Object arg) throws AppException {
		try {
			
			GeneralController controller = null;
			
			if(bd != null) {
				logger.error("*** Check if changed");
				if(bd.hasChanged()) {
					logger.error("*** Prompt to save!");
					logger.info(bd.hasChanged());
					
					Alert alert = new Alert(AlertType.CONFIRMATION);
					
					alert.getButtonTypes().clear();
					ButtonType buttonTypeOne = new ButtonType("Yes");
					ButtonType buttonTypeTwo = new ButtonType("No");
					ButtonType buttonTypeThree = new ButtonType("Cancel");
					alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree);

					alert.setTitle("Save Changes?");
					alert.setHeaderText("The current view has unsaved changes.");
					alert.setContentText("Do you wish to save them before switching to a different view?");

					Optional<ButtonType> result = alert.showAndWait();
					if(result.get().getText().equalsIgnoreCase("Yes")) {
						this.bd.saveBookClicked();
						logger.info("Saving changed Book");
					} else if(result.get().getText().equalsIgnoreCase("Cancel")) {
						return false;
					}
				}
			}
			URL fxmlFile = null;
			
			
			switch(viewType) {
				case BOOK_LIST:
					fxmlFile = this.getClass().getResource("BookListView.fxml");
					controller = new BookListController(new BookTableGateway(conn),new PublisherTableGateway(conn));
					bd = null;
					break;
				case BOOK_DETAIL:
					pubGateway = new PublisherTableGateway(conn);
					publishers = pubGateway.getPublishers();
					fxmlFile = this.getClass().getResource("BookDetailView.fxml");
					//controller = new BookDetailController((Book) arg, publishers);
					bd = new BookDetailController((Book) arg, publishers);
					controller = bd;
					break;
				case BOOK_AUDIT_TRAIL:
					bookGateway = new BookTableGateway(conn);
					audits = bookGateway.getAuditTrail((Book) arg);
					fxmlFile = this.getClass().getResource("AuditTrailView.fxml");
					controller = new AuditTrailController((Book) arg, audits);
					bd = null;
					break;
				case AUTHOR_LIST:
					fxmlFile = this.getClass().getResource("AuthorListView.fxml");
					controller = new AuthorListController(new AuthorTableGateway(conn));
					bd = null;
					break;
				case AUTHOR_DETAIL:
					fxmlFile = this.getClass().getResource("AuthorDetailView.fxml");
					controller = new AuthorDetailController((Author) arg);
					bd = null;
					break;
			}
			FXMLLoader loader = new FXMLLoader(fxmlFile);
			loader.setController(controller);		
			Parent viewNode = loader.load();
			rootPane.setCenter(viewNode);
		} catch (IOException e) {
			throw new AppException(e);
		}
		return true;
	}
    
    public Connection getConnection() {
		return conn;
	}

	public void setConnection(Connection conn) {
		this.conn = conn;
	}

}
