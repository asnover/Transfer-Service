package me.snover;

/**
 * Exists purely for cosmetic purposes and ease of reference.
 */
public enum ColorPalette {
    DARK_RED("b91815", 160, 0, 0),
    AQUA("4ed5fc", 31, 84, 99);

     final String HEXCODE;
     final int R;
     final int G;
     final int B;

     ColorPalette(String hexcode, int r, int g, int b) {
         HEXCODE = hexcode;
         R = r;
         G = g;
         B = b;
     }

     public String getHexCode() {
         return HEXCODE;
     }

     public int getR() {
         return R;
     }

     public int getG() {
         return G;
     }

     public int getB() {
         return B;
     }
}
