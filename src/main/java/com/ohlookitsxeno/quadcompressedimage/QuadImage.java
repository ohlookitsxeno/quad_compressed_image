package com.ohlookitsxeno.quadcompressedimage;

import java.awt.image.BufferedImage;

public class QuadImage {
    
    private Quad root;
    private BufferedImage image;

    public QuadImage(){
        root = new Quad();
    }
    public QuadImage(BufferedImage img){
        root = new Quad(0,0,img.getWidth(),img.getHeight());
        image = img; 
        int a = average(0, 0, img.getWidth(), img.getHeight());
        root.setValue(a);
    }

    public void split(){
        split(root);
    }

    public void split(int n){
        for(int i = 0; i < n; i++)
            split();        
    }
    private void split(Quad q){
        if(!q.isSplit()){
            q.split();
            for(Quad qu : q.getQuads()){
                int avg = average(qu.getX(),qu.getY(),qu.getW(),qu.getH());
                qu.setValue(avg);
            }
        }else{
            Quad[] quads = q.getQuads();
            Double[] errors = {0.0,0.0,0.0,0.0};
            int min = 0;
            for(int i = 0; i < 4; i++){
                errors[i] = error(quads[i].getX(), quads[i].getY(), quads[i].getW(), quads[i].getH(), quads[i].getValue());
                if(errors[i] < errors[min] && errors[i] != 0) min = i;
                if(errors[min] == 0 && errors[i] != 0) min = i;
            }
            if(errors[min] != 0)
                split(quads[min]);
            System.out.println("Errors: "+errors[0] + "," + errors[1] + "," + errors[2] + "," + errors[3] + ", min:" + min);
        }
    }
    //average over a range of image
    public int average(int px, int py, int iw, int ih){
        int pixels = 0;
        int[] trgb = {0,0,0,0}; //total argb
        for(int x = px; x < px+iw; x++){
            for(int y = py; y < py+ih; y++){
                int color = image.getRGB(x,y);
                trgb[0] += (color >> 24) & 255;
                trgb[1] += (color >> 16) & 255;
                trgb[2] += (color >> 8) & 255;
                trgb[3] += color & 255;
                pixels++;
            }
        }
        if(pixels == 0) return -1;
        for(int i = 0; i < 4; i++)
            trgb[i] /= pixels;
        System.out.println("average: " + trgb[0] + ","+ trgb[1] + ","+ trgb[2] + ","+ trgb[3]);
        return (trgb[0]<<24) | (trgb[1] << 16) | (trgb[2] << 8) | trgb[3];
    }

    public double error(int px, int py, int iw, int ih, int col){
        double sum = 0;
        int[] crgb = {
            (col >> 24) & 255,
            (col >> 16) & 255,
            (col >> 8) & 255,
            col & 255
        };

        for(int x = px; x < px+iw; x++){
            for(int y = py; y < py+ih; y++){
                double difference = 0;
                int ocol = image.getRGB(x, y);
                difference += Math.abs(crgb[0] - (ocol >> 24) & 255)/255.;
                difference += Math.abs(crgb[1] - (ocol >> 16) & 255)/255.;
                difference += Math.abs(crgb[2] - (ocol >> 8) & 255)/255.;
                difference += Math.abs(crgb[3] - ocol & 255)/255.;
                sum += difference * difference/3;
            }
        }
        System.out.println(sum);
        return sum/(iw*ih);
    }
}
