package net.guides.springboot2.springboot2webappjsp.controllers;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

public class FileUploader {

    //Return store path if success
    //Return "fail" if fail

    public static String fileUpload(MultipartFile file) {
        //File upload path
        String storePath = "C:\\Users\\Ning\\IdeaProjects\\elec5620-backend\\EventsAssistant\\src\\main\\resources\\static";
        //Using original filename
        String storeName = file.getOriginalFilename();
        //New file store path
        File storeFile = new File(storePath + "/" + storeName);

        try {
            //Create directory if not exist
            if (!storeFile.getParentFile().exists()) {
                storeFile.getParentFile().mkdirs();
            }
            try {
                storeFile.createNewFile();
                file.transferTo(storeFile);
                return storeFile.getPath() + storeFile.getName();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return "fail";
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return "fail";
        }
    }
}
