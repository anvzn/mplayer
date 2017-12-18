/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *
 * @author user
 */
public class Track {

    private final String title;
    private final String path;
    private FileInputStream fileStream;

    public Track(String path) {
        this.path = path;
        File file = new File(path);
        fileStream = null;
        try {
            fileStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        title = file.getName();
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public FileInputStream getFileStream() {
        return fileStream;
    }

}
