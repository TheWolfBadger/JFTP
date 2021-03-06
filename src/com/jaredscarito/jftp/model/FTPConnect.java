package com.jaredscarito.jftp.model;

import com.jaredscarito.jftp.controller.MainController;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;

public class FTPConnect {
    private String username;
    private String password;
    private String host;
    private int port;
    private FTPClient client;
    public FTPConnect(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
    public FTPConnect(String host, String port, String username, String password) {
        this.host = host;
        this.port = Integer.parseInt(port);
        this.username = username;
        this.password = password;
    }
    public boolean connect() {
        this.client = new FTPClient();
        try {
            this.client.connect(this.host, this.port) ;
            if(!this.client.login(this.username, this.password)) return false;
            this.client.enterLocalPassiveMode();
            this.client.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    public boolean disconnect() {
        try {
            this.client.logout();
            this.client.disconnect();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public FTPClient getClient() {
        return this.client;
    }

    public FTPFile[] getFiles() {
        try {
            return this.client.listFiles();
        } catch (IOException e) {
            return null;
        }
    }
    public FTPFile[] getDirectories() {
        try {
            return this.client.listDirectories();
        } catch (IOException e) {
            return null;
        }
    }

    public boolean isDirectory(String filePath) {
        try {
            FTPFile[] files = this.client.listFiles(filePath);
            if(files.length == 0) {
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean uploadFile(String localFilePath, String ftpDestDir) {
        try {
            InputStream inp = new FileInputStream(new File(localFilePath));
            if(this.client.storeFile(ftpDestDir, inp)) {
                inp.close();
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    public boolean downloadFile(String ftpFilePath, String localDestDir) {
        try {
            FileOutputStream fos = new FileOutputStream(localDestDir);
            if(this.client.retrieveFile(ftpFilePath, fos)) {
                fos.close();
                return true;
            }
        } catch (IOException ex) {
            return false;
        }
        return false;
    }
    public boolean renameFTPFile(String oldName, String newName) {
        return false; // TODO
    }
}
