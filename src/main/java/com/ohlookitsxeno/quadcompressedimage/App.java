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
        BufferedImage test = ImageIO.read(new File("D:\\Documents\\VSCode\\quad_compressed_image\\test\\jinx.png")); //placeholder test image
        

        QuadImage q = new  QuadImage(test);
        q.split(5);
        //fm.encode(q)
    }
}
