package com.app.service;


import com.app.model.User_files;
import com.app.repository.UserFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class User_File_Service {

    @Autowired
    private UserFileRepository userFileRepository;

    @Value("${user.file.dir}")
    private String user_file_dir;


    //   Upload & then store it to the server.
    public void fileStore(MultipartFile file, String modifiedFileName) {
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, Paths.get(this.user_file_dir + "/" + modifiedFileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void saveFile(User_files userFiles, MultipartFile file) {
        String file_name = file.getOriginalFilename();
        String fileName = file_name.replace(" ", "_").trim();
        String file_extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String modifiedName = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + System.nanoTime() + "_" +
                fileName.substring(fileName.lastIndexOf("."));

        fileStore(file, modifiedName);
        userFiles.setFile_name(modifiedName);
        userFiles.setFile_dir(user_file_dir + "/" + modifiedName);
        userFiles.setFile_extension(file_extension);
        userFiles.setFull_path("./src/main/resources/static/media/files/blog/img/" + modifiedName); //   user_files
        userFileRepository.save(userFiles);

    }

    public void deleteFile(Long id) {
        User_files userFile = this.userFileRepository.findById(id).get();
        File file = new File(userFile.getFile_dir());
        file.delete();
        this.userFileRepository.deleteById(id);
    }

    public Page<User_files> allPages(Pageable pageable) {
        return userFileRepository.findAll(pageable);
    }

}
