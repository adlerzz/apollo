package org.adlerzz.apollo.engine.singles;

import static org.adlerzz.apollo.engine.utils.Constants.HEXOYA;

public class HSV {
    private short H;
    private short S;
    private short V;

    public HSV() {
        //
    }

    public HSV(int raw){
        this.H = (short) (raw & 0xFF);
        this.S = (short) ((raw >> 8) & 0xFF);
        this.V = (short) ((raw >> 16) & 0xFF);
    }


    public HSV(HSV orig) {
        this.H = orig.H;
        this.S = orig.S;
        this.V = orig.V;
    }

    private static short finite(double value, double min, double max) {
        short res;

        if (value < min) {
            res = (short) min;
        } else {
            if (value > max) {
                res = (short) max;
            } else {
                res = (short) value;
            }
        }

        return res;
    }

    public HSV(RGBA pix) {
        double R = pix.getR() / 255.0;
        double G = pix.getG() / 255.0;
        double B = pix.getB() / 255.0;

        double Hf, Sf, Vf, MIN, MAX;

        MAX = (R > G) ? R : G ;
        MIN = (R < G) ? R : G;

        if (MAX < B) MAX = B;
        if (MIN > B) MIN = B;

        if (MAX - MIN == 0) {
            Hf = 0.0;
        } else if (MAX == R) {
            if (G >= B) {
                Hf = ((G - B) / (MAX - MIN));
            } else {
                Hf = (6 - ((B - G) / (MAX - MIN)));
            }
        } else if (MAX == G) {
            Hf = (((B - R) / (MAX - MIN)) + 2);
        } else {
            Hf =  (((R - G) / (MAX - MIN)) + 4);
        }
        Hf *= HEXOYA;

        if (MAX < (1.0 / 255)) {
            Sf = 0.0;
        } else {
            Sf = (MAX - MIN) / MAX;
        }
        Vf = MAX;

        this.H = HSV.finite(Hf, 0, HEXOYA * 6);
        this.S = HSV.finite(Sf * 255.0, 0, 255.0);
        this.V = HSV.finite(Vf * 255.0, 0, 255.0);

    }

    /**
     * @return the H
     */
    public short getH() {
        return H;
    }

    /**
     * @param H the H to set
     */
    public void setH(double H) {
        this.H = (short) H;
    }

    /**
     * @return the S
     */
    public short getS() {
        return S;
    }

    /**
     * @param S the S to set
     */
    public void setS(double S) {
        this.S = (short) S;
    }

    /**
     * @return the V
     */
    public short getV() {
        return V;
    }

    /**
     * @param V the V to set
     */
    public void setV(double V) {
        this.V = (short) V;
    }

    public boolean lessThan(HSV op) {
        boolean res;

        if (this.V == op.V) {
            if (this.H == op.H) {
                res = this.S < op.S;
            } else {
                res = this.H < op.H;
            }
        } else {
            res = this.V < op.V;
        }
        return res;
    }

    public static long QDistance(HSV a, HSV b) {
        return (a.H - b.H) * (a.H - b.H)
                + (a.S - b.S) * (a.S - b.S)
                + (a.V - b.V) * (a.V - b.V);
    }

    public void setAs(HSV x) {
        this.H = x.H;
        this.S = x.S;
        this.V = x.V;
    }

    @Override
    public String toString() {
        return String.format("%02X'%02X'%02X",
                this.H, this.S, this.V);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        HSV c = (HSV) obj;

        return (c.H == this.H && c.S == this.S && c.V == this.V);

    }


    @Override
    public int hashCode() {
        return (this.V << 16) | (this.S << 8) | (this.H & 0xFF);
    }


}
