package com.ohlookitsxeno.quadcompressedimage;

public class Quad {
    
    private int value;
    private Quad TL = null;
    private Quad TR = null;
    private Quad BL = null;
    private Quad BR = null;
    private boolean isSplit;

    private int[] pos;
    private int[] dim;

    public Quad(){

    }
    public Quad(int val){
        value = val;
    }

    public Quad(int x, int y, int w, int h){
        pos = new int[2];
        dim = new int[2];
        pos[0] = x;
        pos[1] = y;
        dim[0] = w;
        dim[1] = h;
        isSplit = false;
    }

    public void split(){
        int x = pos[0];
        int y = pos[1];
        double w = dim[0]/2.;
        double h = dim[1]/2.;

        TL = new Quad(x,y,(int)Math.ceil(w),(int)Math.ceil(h));
        TR = new Quad(x+(int)Math.ceil(w),y,(int)Math.floor(w),(int)Math.ceil(h));
        BL = new Quad(x,y+(int)Math.ceil(h),(int)Math.ceil(w),(int)Math.floor(h));
        BR = new Quad(x+(int)Math.ceil(w),y+(int)Math.ceil(h),(int)Math.floor(w),(int)Math.floor(h));
        isSplit = true;
    }

    public void setValue(int val){value = val;}
    public int getValue(){return value;}

    public Quad getTL(){return TL;}
    public Quad getTR(){return TR;}
    public Quad getBL(){return BL;}
    public Quad getBR(){return BR;}

    public Quad[] getQuads(){
        Quad[] quads = new Quad[4];
        quads[0] = TL;
        quads[1] = TR;
        quads[2] = BL;
        quads[3] = BR;
        return quads;
    }

    public void setTL(Quad in){TL = in;}
    public void setTR(Quad in){TR = in;}
    public void setBL(Quad in){BL = in;}
    public void setBR(Quad in){BR = in;}

    public int getX(){return pos[0];}
    public int getY(){return pos[1];}
    public int getW(){return dim[0];}
    public int getH(){return dim[1];}

    public boolean isSplit(){return isSplit;}
}
