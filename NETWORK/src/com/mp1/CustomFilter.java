package com.mp1;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class CustomFilter extends FileFilter{

    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
 
        String extension = FileType.getExtension(f.getName());
        if (extension != null) {
            if (extension.equals("tiff") ||
                extension.equals("tif") ||
                extension.equals("gif") ||
                extension.equals("jpeg") ||
                extension.equals("jpg") ||
                extension.equals("png") ||
                extension.equals("webm")||
                extension.equals("mkv")||
                extension.equals("flv")||
                extension.equals("ogg")||
                extension.equals("ogv")||
                extension.equals("gifv")||
                extension.equals("avi")||
                extension.equals("wmv")||
                extension.equals("mov")||
                extension.equals("mp4")||
                extension.equals("m4v")||
                extension.equals("mpg")||
                extension.equals("mpeg")||
                extension.equals("3gp")||
                extension.equals("mp3")||
                extension.equals("wav")||
                extension.equals("wma")||
                extension.equals("flac")||
                extension.equals("aiff")) {
                    return true;
            } else {
                return false;
            }
        }
 
        return false;
    }
 
    //The description of this filter
    public String getDescription() {
        return "Media Files";
    }
}
