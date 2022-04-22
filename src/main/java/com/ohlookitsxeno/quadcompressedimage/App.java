package com.ohlookitsxeno.quadcompressedimage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class App 
{
    BufferedImage wawa;
    public static void main( String[] args ) throws IOException
    {
        BufferedImage test = ImageIO.read(new File("D:\\Documents\\VSCode\\quad_compressed_image\\testfolder\\jinx.png")); //placeholder test image
        

        QuadImage q = new  QuadImage(test);
        FileProcessor fp = new FileProcessor();

        q.split(5);
        System.out.println(fp.encodeQCI(q));
    }
}
