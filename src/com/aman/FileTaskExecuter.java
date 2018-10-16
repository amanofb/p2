package com.aman;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class FileTaskExecuter {
    private final static Logger logger = Logger.getLogger(FileTaskExecuter.class.getName());
    private static FileHandler fileHandler;
    private static SimpleFormatter simpleFormatter;

    public FileTaskExecuter() {
        initLogger();
    }

    private void initLogger() {
        logger.setLevel(Level.ALL);
        try {
            fileHandler = new FileHandler("tasks.log");
            simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            logger.addHandler(fileHandler);
            logger.info("Yayy! Logger initiated!");
        } catch (IOException e) {
            logger.severe("Unable to set config for logger" + e);
        }
    }

    public void copy(String[] args) {
        if (args.length != 2) {
            logger.severe("Wrong number of input parameters!");
            return;
        }
        String sourceFile = args[0];
        String destFile = args[1] + sourceFile;
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        File tempFile = null;

        try {
            fileInputStream = new FileInputStream(sourceFile);
            tempFile = new File("temp/" + sourceFile);
            tempFile.getParentFile().mkdirs();
            tempFile.createNewFile();

            fileOutputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024 * 1000];
            int noOfBytes = 0;

            logger.info("Copying file using streams");
             int count = 0;
            while ((noOfBytes = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, noOfBytes);
                count++;
//                if (count ==2) {
//                    throw new IOException();
//                }
            }

            URI tempFileUri = tempFile.toURI();
            File finalFile = new File(destFile);
            if (finalFile.exists()) {
                finalFile.delete();
            }
            finalFile.getParentFile().mkdirs();
            URI newfileUri = finalFile.toURI();

            Path newPath = Files.move(Paths.get(tempFileUri), Paths.get(newfileUri));
            Files.deleteIfExists(Paths.get(tempFile.getParent()));

            if (newPath != null) {
                logger.info("File renamed and moved successfully");
            } else {
                logger.severe("Failed to move the file");
            }
        }
        catch (FileNotFoundException e) {
            logger.severe("File not found" + e);
        }
        catch (IOException ioe) {
            try {
                tempFile.delete();
                Files.deleteIfExists(Paths.get(tempFile.getParent()));
            } catch (IOException e) {
                logger.severe("Exception while deleting temporary file " + ioe);
            }
            logger.severe("Exception while copying file " + ioe);
        }
        finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            }
            catch (IOException e) {
                logger.severe("Error while closing stream: " + e);
            }
        }
    }
}
