package com.ohlookitsxeno.quadcompressedimage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import javax.imageio.ImageIO;

public class App 
{
    BufferedImage wawa;
    public static void main( String[] args )
    {
        FileProcessor fp = new FileProcessor();

        BufferedImage test = fp.importImage("D:\\Documents\\VSCode\\quad_compressed_image\\testfolder\\man.jpg");


        QuadImage q = new QuadImage(test);
        q.split(10);
        BufferedImage woah = q.render();

        fp.exportQCI(q, "testfolder/testexp");
        fp.exportImage(woah, "testfolder/testexp", "png");

        

        //Path qtest = Paths.get("D:/Documents/VSCode/quad_compressed_image/testfolder/test.qci");
        //QuadImage ip = fp.importQCI(qtest);

    }
}
