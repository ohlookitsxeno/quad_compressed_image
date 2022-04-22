package com.ohlookitsxeno.quadcompressedimage;

import java.awt.image.BufferedImage;

public class QuadImage {
    
    private Quad root;
    private BufferedImage image;
    private int splits = 0;
    private boolean maxSplit = false;

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

    public void split(int aipo){
        for(int i = 0; i < aipo; i++){
            if(!maxSplit)
                split();
            else
                return;
        }          
    }
    private void split(Quad q){
        if(!q.isSplit()){
            q.split();
            splits++;
            for(Quad qu : q.getQuads()){
                int avg = average(qu.getX(),qu.getY(),qu.getW(),qu.getH());
                qu.setValue(avg);
            }
        }else{
            Quad max = maxError(q);
            if(error(max.getX(),max.getY(),max.getW(),max.getH(),max.getValue()) != 0){
                split(max);
                splits++;
            }else{
                System.out.println("max splits achieved at " + splits + ", stopping.");
                maxSplit = true;
            }
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
        //System.out.println("average: " + trgb[0] + ","+ trgb[1] + ","+ trgb[2] + ","+ trgb[3]); //debug line
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
        return sum/(iw*ih);
    }

    public Quad maxError(Quad q){
        if(!q.isSplit())
            return q;
        
        Quad[] quads = q.getQuads();
        Double[] errors = {0.0,0.0,0.0,0.0};
        int max = 0;

        for(int i = 0; i < 4; i++){
            quads[i] = maxError(quads[i]);
            errors[i] = error(quads[i].getX(), quads[i].getY(), quads[i].getW(), quads[i].getH(), quads[i].getValue());
            if(errors[i] > errors[max]) max = i;
        }
        return quads[max];
    }

    public Quad getRoot(){
        return root;
    }

    public BufferedImage render(){
        BufferedImage out = new BufferedImage(root.getW(),root.getH(),BufferedImage.TYPE_INT_ARGB);
        renderer(root,out);
        return out;
    }
    private void renderer(Quad q, BufferedImage i){
        if(q.isSplit()){
            for(Quad s : q.getQuads())
                renderer(s,i);

        }else{
            for(int x = q.getX(); x < q.getX()+q.getW(); x++){
                for(int y = q.getY(); y < q.getY()+q.getH(); y++){
                    i.setRGB(x,y,q.getValue());
                }
            }
        }
        
    }
}
