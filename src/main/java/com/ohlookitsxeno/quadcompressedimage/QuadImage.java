package com.ohlookitsxeno.quadcompressedimage;

import java.awt.image.BufferedImage;

public class QuadImage {
    
    private Quad root;
    private BufferedImage image;
    private boolean maxSplit = false;

    public QuadImage(){
        root = new Quad();
    }
    public QuadImage(int w, int h){
        root = new Quad(0, 0, w, h);
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
            if(i % 100 == 0) System.out.println("Split " + i);
            if(!maxSplit)
                split();
            else
                return;
        }          
    }

    private void split(Quad q){
        if(!q.isSplit()){
            q.split();
            for(Quad qu : q.getQuads()){
                int avg = average(qu.getX(),qu.getY(),qu.getW(),qu.getH());
                qu.setValue(avg);
            }
        }else{
            Quad max = maxError(q);

            split(max);
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

    public double error(BufferedImage a, BufferedImage b){
        double[] sum = {0,0,0,0};
        int w = a.getWidth();
        int h = a.getHeight();
        for(int x = 0; x < w; x++){
            for(int y = 0; y < h; y++){
                int acol = a.getRGB(x, y);
                int bcol = b.getRGB(x, y);

                int[] acols = {
                    (acol >> 24) & 255,
                    (acol >> 16) & 255,
                    (acol >> 8) & 255,
                    acol & 255
                };
                int[] bcols = {
                    (bcol >> 24) & 255,
                    (bcol >> 16) & 255,
                    (bcol >> 8) & 255,
                    bcol & 255
                };
                for(int i = 0; i < 4; i++)
                    sum[i] += Math.pow((acols[i]-bcols[i])/255., 2);
            }
        }
        double[] weights = {1,1,1,1};
        double out = 0;        
        for(int i = 0; i < 4; i++){
            sum[i] /= w * h;
            out += sum[i] + weights[i];
        }
        return out;

    }

    public Quad maxError(Quad q){
        if(!q.isSplit())
            return q;

        Quad[] quads = q.getQuads();
        Double[] errors = {0.0,0.0,0.0,0.0};
        int max = 0;

        for(int i = 0; i < 4; i++){
            BufferedImage qi = render(quads[i]);
            BufferedImage source = image.getSubimage(quads[i].getX(), quads[i].getY(), qi.getWidth(), qi.getHeight());
            errors[i] = error(qi, source);
            if(errors[i] > errors[max]) max = i;
        }

        return maxError(quads[max]);
    }
    
    public Quad getRoot(){
        return root;
    }

    public BufferedImage render(){
        BufferedImage out = new BufferedImage(root.getW(),root.getH(),BufferedImage.TYPE_INT_ARGB);
        int[] pos = {0,0};
        renderer(root,out,pos);
        return out;
    }
    private BufferedImage render(Quad q){
        BufferedImage out = new BufferedImage(q.getW(),q.getH(),BufferedImage.TYPE_INT_ARGB);
        int[] pos = {q.getX(),q.getY()};
        renderer(q,out,pos);
        return out;
    }
    private void renderer(Quad q, BufferedImage i, int[] aipos){
        if(q.isSplit()){
            for(Quad s : q.getQuads())
                renderer(s,i,aipos);

        }else{
            for(int x = 0; x < q.getW(); x++){
                for(int y = 0; y < q.getH(); y++){
                    i.setRGB(x+q.getX()-aipos[0],y+q.getY()-aipos[1],q.getValue());
                }
            }
        }
        
    }

    public void addLines(BufferedImage b){
        lining(root, b);
    }
    private void lining(Quad q, BufferedImage b){
        if(q.isSplit()){
            for(int x = q.getX(); x < q.getX() + q.getW(); x++){
                int y = q.getY() + q.getH()/2;
                b.setRGB(x,y,0xff000000);
            }
            for(int y = q.getY(); y < q.getY() + q.getH(); y++){
                int x = q.getX() + q.getW()/2;
                b.setRGB(x,y,0xff000000);
            }
            for(Quad s : q.getQuads())
                lining(s,b);
        }
    }

}
