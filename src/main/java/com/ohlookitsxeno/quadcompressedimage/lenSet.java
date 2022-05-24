package com.ohlookitsxeno.quadcompressedimage;

import java.util.*;

public class lenSet {
    
    private BitSet set;
    private int len;

    public lenSet(){
        set = new BitSet();
        len = 0;
    }

    public lenSet(BitSet b, int l){
        set = b;
        len = l;
    }

    public BitSet get(){return set;}
    public int length(){return len;}

    public void set(BitSet b, int l){set = b; len = l;}
    public void setLength(int l){len = l;}

    public static lenSet lenString(String s){
        return new lenSet(bitString(s),s.length());
    }

    public static lenSet lenVal(int i, int s){
        String bin = Integer.toBinaryString(i);
        String pad = String.format("%"+s+"s", bin).replace(' ','0');

        return new lenSet(bitString(pad),s);
    }

    public static lenSet lenCombine(lenSet... sets){
        lenSet out = new lenSet();
        int idx = 0;
        for(lenSet set : sets){
            for(int i = 0; i < set.length(); i++){
                if(set.get().get(i))
                    out.get().set(i+idx);
            }
            idx += set.length();
        }
        out.setLength(idx);
        return out;
    }

    private static BitSet bitString(String s){
        BitSet out = new BitSet();
        for(int i = 0; i < s.length(); i++){
            if(s.charAt(i) == '1')
                out.set(i);
        }
        return out;
    }

    private static BitSet bitCombine(BitSet... sets){
        BitSet out = new BitSet();
        int idx = 0;
        for(BitSet set : sets){
            for(int i = 0; i < set.length(); i++){
                if(set.get(i))
                    out.set(i+idx);
            }
            idx += set.length();
        }
        return out;
    }
}
