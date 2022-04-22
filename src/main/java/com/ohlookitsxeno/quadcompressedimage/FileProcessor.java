package com.ohlookitsxeno.quadcompressedimage;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import java.nio.file.*;

public class FileProcessor {

    public FileProcessor(){
        
    }

    public BufferedImage importImage(String s){
        BufferedImage out = null;
        try{
            out = ImageIO.read(new File(s));
        }catch (IOException e){
            System.out.println("File " + s + " not found.");
        }
        return out;
    }

    public void exportImage(BufferedImage img, String path, String ext){
        try {
            File out = new File(path+"."+ext);
            ImageIO.write(img, ext, out);
            System.out.println("Success!");
        } catch (IOException e){
            System.out.println("Error: Writing to image failed.");
        }
    }

    public void exportQCI(QuadImage qi, String s){
        String out = "QCI";
        out += "W"+qi.getRoot().getW();
        out += "H"+qi.getRoot().getH();
        out += encodeQCI(qi.getRoot());
        byte[] data = out.getBytes();
        File file = new File(s+".qci");
        try{
            Files.write(file.toPath(),data);
        }catch (IOException e){

        }
    }

    public QuadImage importQCI(Path p){
        boolean success;
        try{
            byte[] data = Files.readAllBytes(p);
            success = true;
        }catch (IOException e){success = false;}
        if(!success){
            System.out.println("File not found.");
            return null;
        }
        System.out.println("success");
        return null;
    }

    public String encodeQCI(Quad q){
        if(q.isSplit()){
            String out = "Q";
            for(Quad p : q.getQuads())
                out += encodeQCI(p);
            return out;
        }
        return q.getQCI();
    }
}
