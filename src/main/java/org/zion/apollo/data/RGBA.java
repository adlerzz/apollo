package org.zion.apollo.data;

//import org.zion.apollo.utils.Constants;

import org.zion.apollo.utils.Constants;

public class RGBA {

    private short R;
    private short G;
    private short B;
    private short A;

    public RGBA() {

    }

    public RGBA(RGBA orig) {
        this.R = orig.R;
        this.G = orig.G;
        this.B = orig.B;
        this.A = orig.A;
    }

    public RGBA(int raw) {
        this.R = (short) (raw & 0xFF);
        this.G = (short) ((raw >> 8) & 0xFF);
        this.B = (short) ((raw >> 16) & 0xFF);
        this.A = (short) ((raw >> 24) & 0xFF);
    }

    public RGBA(HSV pix) {
        short Hi;
        float f, p, q, t;

        float H = pix.getH();
        float S = (float) (pix.getS() / 255.0);
        float V = pix.getV();

        Hi = (short) (H / Constants.HEXOYA);

        f = (H / Constants.HEXOYA) - Hi;

        p = V * (1 - S);
        q = V * (1 - S * f);
        t = V * (1 - S * (1 - f));

        switch (Hi) {
            case 0:
                this.R = (short) V;
                this.G = (short) t;
                this.B = (short) p;
                this.A = (short) 0;
                break;
            case 1:
                this.R = (short) q;
                this.G = (short) V;
                this.B = (short) p;
                this.A = (short) 0;
                break;
            case 2:
                this.R = (short) p;
                this.G = (short) V;
                this.B = (short) t;
                this.A = (short) 0;
                break;

            case 3:
                this.R = (short) p;
                this.G = (short) q;
                this.B = (short) V;
                this.A = (short) 0;
                break;
            case 4:
                this.R = (short) t;
                this.G = (short) p;
                this.B = (short) V;
                this.A = (short) 0;
                break;
            default:
                this.R = (short) V;
                this.G = (short) p;
                this.B = (short) q;
                this.A = (short) 0;
                break;
        }
    }

    /**
     * @return the R
     */
    public short getR() {
        return R;
    }

    /**
     * @param R the R to set
     */
    public void setR(short R) {
        this.R = R;
    }

    /**
     * @return the G
     */
    public short getG() {
        return G;
    }

    /**
     * @param G the G to set
     */
    public void setG(short G) {
        this.G = G;
    }

    /**
     * @return the B
     */
    public short getB() {
        return B;
    }

    /**
     * @param B the B to set
     */
    public void setB(short B) {
        this.B = B;
    }

    /**
     * @return the A
     */
    public short getA() {
        return A;
    }

    /**
     * @param A the A to set
     */
    public void setA(short A) {
        this.A = A;
    }

    public int toRaw() {
        return ((this.R & 0xFF) |
                ((this.G & 0xFF) << 8) |
                ((this.B & 0xFF) << 16) |
                ((this.A & 0xFF) << 24)
        );
    }

    public String toString() {
        return String.format("{R: %02X, G: %02X; B: %02X}",
                this.R, this.G, this.B);
    }

    @Override
    public int hashCode() {
        return (this.A << 24) | (this.B << 16) | (this.G << 8) | (this.R & 0xFF);
    }
}
