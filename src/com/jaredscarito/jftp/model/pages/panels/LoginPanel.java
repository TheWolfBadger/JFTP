package com.jaredscarito.jftp.model.pages.panels;

import com.jaredscarito.jftp.model.FTPConnect;
import com.jaredscarito.jftp.model.PaneFile;
import com.jaredscarito.jftp.model.pages.MainPage;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.apache.commons.net.ftp.FTPFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class LoginPanel extends Panel {
    private String name;
    private TextField hostField;
    private TextField portField;
    private TextField userField;
    private TextField passField;
    private FTPConnect connection;
    private Button connectButton;
    public LoginPanel() {
        init();
    }
    public LoginPanel(String name, List<String> styleClasses) {
        super(name, styleClasses);
    }
    public LoginPanel(String name, String styleClass) {
        super(name, styleClass);
    }

    public FTPConnect getConnection() {
        return this.connection;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void init() { 
        setHgap(30);
        Label hostLab = new Label("Host:");
        hostLab.getStyleClass().add("login-label");
        add(hostLab, 0, 0);
        TextField hostField = new TextField();
        hostField.getStyleClass().add("login-textfield");
        add(hostField, 0, 1);
        Label portLab = new Label("Port:");
        portLab.getStyleClass().add("login-label");
        add(portLab, 1, 0);
        TextField portField = new TextField();
        portField.getStyleClass().add("login-textfield");
        add(portField, 1, 1);
        Label userLab = new Label("Username:");
        userLab.getStyleClass().add("login-label");
        add(userLab, 2, 0);
        TextField userField = new TextField();
        userField.getStyleClass().add("login-textfield");
        add(userField, 2, 1);
        Label passLab = new Label("Password:");
        passLab.getStyleClass().add("login-label");
        add(passLab, 3, 0);
        PasswordField passField = new PasswordField();
        passField.getStyleClass().add("login-textfield");
        add(passField, 3, 1);
        Button connectBtn = new Button("Connect");
        connectBtn.getStyleClass().add("login-button");
        add(connectBtn, 4, 1);
        this.connectButton = connectBtn;
        this.hostField = hostField;
        this.portField = portField;
        this.userField = userField;
        this.passField = passField;
        this.connectButton.getStyleClass().add("connect-btn");
        this.connectButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY) {
                    if (connection == null) {
                        String host = hostField.getText();
                        int port = 0;
                        try {
                            port = Integer.parseInt(portField.getText());
                        } catch (NumberFormatException ex) {}
                        String username = userField.getText();
                        String pass = passField.getText();
                        MainPage.get().getCommandPanel().addMessage("Connection: Attempting to connect to " + host + ":" + port, "GRAY", true); // CommandMessage
                        connection = new FTPConnect(host, port, username, pass);
                        if (connection.connect()) {
                            for (FTPFile file : connection.getFiles()) {
                                String name = file.getName();
                                String size = humanReadableByteCount(file.getSize(), true);
                                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy | hh:mm");
                                String lastModified = dateFormat.format(file.getTimestamp().getTimeInMillis());
                                ImageView icon;
                                if(file.isDirectory()) {
                                    icon = new ImageView(new Image("com/jaredscarito/jftp/resources/ftp-folder-icon.png"));
                                    icon.setFitWidth(15);
                                    icon.setFitHeight(15);
                                } else {
                                    // It's a file
                                    icon = new ImageView(new Image("com/jaredscarito/jftp/resources/ftp-file-icon.png"));
                                    icon.setFitWidth(15);
                                    icon.setFitHeight(15);
                                }
                                MainPage.get().getFtpFilesPanel().getTableView().getItems().add(new PaneFile(icon, name, size, lastModified));
                            }
                            /** /
                             for(FTPFile file : connection.getDirectories()) {
                             String name = file.getName();
                             String size = humanReadableByteCount(file.getSize(), true);
                             DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy | hh:mm");
                             String lastModified = dateFormat.format(file.getTimestamp().getTimeInMillis());
                             ftpFilesView.getItems().add(new PaneFile(name, size, lastModified));
                             }
                             /**/
                            MainPage.get().getCommandPanel().addMessage("Connection: Attempting to connect to " + host + ":" + port +
                                    " = SUCCESS", "GREEN", true); // CommandMessage
                            connectButton.setText("Disconnect");
                        } else {
                            MainPage.get().getCommandPanel().addMessage("ERROR: Failed to connect to " + host + ":" + port, "RED", true); // CommandMessage
                            connection = null;
                        }
                    } else {
                        // It is the disconnect button
                        MainPage.get().getCommandPanel().addMessage("Connection: Disconnected from FTP Server", "GRAY", false); // CommandMessage
                        MainPage.get().getFtpFilesPanel().getTableView().getItems().clear();
                        connection.disconnect();
                        connection = null;
                        connectButton.setText("Connect");
                    }
                }
            }
        });
    }
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "KMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
