package com.ohlookitsxeno.quadcompressedimage;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

import javax.imageio.ImageIO;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class FileProcessor {

    public static BufferedImage importImage(String s){
        BufferedImage out = null;
        try{
            out = ImageIO.read(new File(s));
        }catch (IOException e){
            System.out.println("File " + s + " not found.");
        }
        return out;
    }

    public static void exportImage(BufferedImage img, String path, String ext){
        try {
            File out = new File(path+"."+ext);
            ImageIO.write(img, ext, out);
            System.out.println("Success!");
        } catch (IOException e){
            System.out.println("Error: Writing to image failed.");
        }
    }

    public static void exportQCI(QuadImage qi, String s){
        String out = "QCI";
        out += qi.getRoot().getW()+"W";
        out += qi.getRoot().getH()+"H";
        out += encodeQCI(qi.getRoot());
        byte[] data = out.getBytes(StandardCharsets.US_ASCII);
        File file = new File(s+".qci");
        try{
            Files.write(file.toPath(),data);
        }catch (IOException e){

        }
    }

    public static void exportXEI(QuadImage qi, String s){
        String type = "XEI";
        byte[] headType = type.getBytes(StandardCharsets.US_ASCII);
        byte[] ow = ByteBuffer.allocate(4).putInt(qi.getRoot().getW()).array();
        byte[] oh = ByteBuffer.allocate(4).putInt(qi.getRoot().getH()).array();
        byte[] head = {headType[0],headType[1],headType[2],ow[1],ow[2],ow[3],oh[1],oh[2],oh[3]};
        byte[] data = encodeXEI(qi.getRoot());

        byte[] comb = ByteBuffer.allocate(data.length + head.length).put(head).put(data).array();
        File file = new File(s+".xei");
        try{
            Files.write(file.toPath(),comb);
        }catch (IOException e){
        }
    }

    public static QuadImage importTree(String s){
        Path o = Paths.get(s);
        boolean success;
        byte[] data = {};
        try{
            data = Files.readAllBytes(o);
            success = true;
        }catch (IOException e){success = false;}
        if(!success){
            System.out.println("File not found.");
            return null;
        }
        if(data[0] == 'Q' && data[1] == 'C' && data[2] == 'I')
            return decodeQCI(data);
        System.out.println("warning, invalid.");
        return null;
    }

    private static QuadImage decodeQCI(byte[] data){
        int pos = 3; //skip 3
        StringBuilder wid = new StringBuilder();
        StringBuilder hei = new StringBuilder();
        while(data[pos] != 'W')
            wid.append((char)data[pos++]);
        pos++;
        while(data[pos] != 'H')
            hei.append((char)data[pos++]);
        pos++;
        int w = Integer.parseInt(wid.toString());
        int h = Integer.parseInt(hei.toString());
        QuadImage q = new QuadImage(w,h);
        ArrayList<Byte> crop = new ArrayList<>();
        for(byte b : Arrays.copyOfRange(data, pos, data.length)){
          crop.add(b);
        }
        qciDecoder(crop, q.getRoot());
        return q;
    }

    private static String crunch(ArrayList<Byte> data, int n){
        String out = "";
        for(int i = 0; i < n; i++)
            out += (char)(byte)data.remove(0);
        return out;
    }

    private static void qciDecoder(ArrayList<Byte> data, Quad q){
        String head = crunch(data, 1);
        if(head.equals("Q")){
            q.split();
            for(Quad qu : q.getQuads())
                qciDecoder(data, qu);
        }else if(head.equals("T")){
            int[] argb = {
                Integer.valueOf(crunch(data,2),16),
                Integer.valueOf(crunch(data,2),16),
                Integer.valueOf(crunch(data,2),16),
                Integer.valueOf(crunch(data,2),16)
            };
            q.setValue((argb[0]<<24) | (argb[1] << 16) | (argb[2] << 8) | argb[3]);
        }else if(head.equals("H")){
            int[] rgb = {
                Integer.valueOf(crunch(data,2),16),
                Integer.valueOf(crunch(data,2),16),
                Integer.valueOf(crunch(data,2),16)
            };
            q.setValue((255<<24) | (rgb[0] << 16) | (rgb[1] << 8) | rgb[2]);
        }else if(head.equals("G")){
            int grey = Integer.valueOf(crunch(data,2),16);
            q.setValue((255<<24) | (grey << 16) | (grey << 8) | grey);
        }else if(head.equals("M")){
            int alpha = Integer.valueOf(crunch(data,2),16);
            int grey = Integer.valueOf(crunch(data,2),16);
            q.setValue((alpha<<24) | (grey << 16) | (grey << 8) | grey);
        }else if(head.equals("N")){
            q.setValue(0);
        }
    }

    private static String encodeQCI(Quad q){
        if(q.isSplit()){
            String out = "Q";
            for(Quad p : q.getQuads())
                out += encodeQCI(p);
            return out;
        }
        return q.getQCI();
    }

    private static byte[] encodeXEI(Quad q){
        lenSet ls = q.getXEI();
        byte[] out = new byte[(ls.length()+7)/8];
        for(int i = 0; i < ls.length(); i++){
            if(ls.get().get(i)){
                out[out.length-(i/8)-1] |= 1<<(i%8);
            }
        }
        return out;
    }
}
