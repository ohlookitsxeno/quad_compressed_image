package com.ohlookitsxeno.quadcompressedimage;

public class Quad {
    
    private int value;
    /*Types
        Q - quarter
        H - RGB
        h - RGB (mini)
        T - ARGB
        t - ARGB (mini)
        G - Gray
        M - alpha + gray
        N - Transparent
    */
    private char type;
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

    public void splitVal(int tl, int tr, int bl, int br){
        split();

        TL.setValue(tl);
        TR.setValue(tr);
        BL.setValue(bl);
        BR.setValue(br);

    }

    public void setValue(int val){value = val; castType();}
    public int getValue(){return value;}

    private void castType(){
        int[] vrgb = {
            (value >> 24) & 255,
            (value >> 16) & 255,
            (value >> 8) & 255,
            value & 255
        };
        boolean alpha = vrgb[0] != 255;

        if(vrgb[0] == 0)
            type = 'N';
        else if(vrgb[1] == vrgb[2] && vrgb[2] == vrgb[3])
            type = alpha ? 'M' : 'G';
        else type = alpha ? 'T' : 'H';
    }

    public String getQCI(){
        if(isSplit) return "Q"; //just to be safe
        if(type == 'N') return "N";
        if(type == 'T')
            return "T" + hexString((value >> 24) & 255) + hexString((value >> 16) & 255) + hexString((value >> 8) & 255) + hexString(value & 255);
        if(type == 'G')
            return "G" + hexString(value & 255); //grey
        if(type == 'M')
            return "M" + hexString((value >> 24) & 255) + hexString(value & 255);  //alpha + grey
        if(type == 'H')
            return "H" + hexString((value >> 16) & 255) + hexString((value >> 8) & 255) + hexString(value & 255); //r + g + b
        return "";
    }

    public String hexString(int hex){
        String out = "";
        out += Integer.toHexString(hex);
        if(out.length() == 1)
            out = "0" + out;
        return out;
    }

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
