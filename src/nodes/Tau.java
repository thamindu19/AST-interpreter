package nodes;

public class Tau extends Node {
    private int n;

    public Tau(int n) {
        super("tau");
        this.setN(n);
    }

    private void setN(int n) {
        this.n = n;
    }

    public int getN() {
        return this.n;
    }
}
