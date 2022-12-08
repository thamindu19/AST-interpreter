
package nodes;

public class Unary extends Rator {
    public Unary(String data) {
        super(data);

    }

    public Node operate(Rand rand) {
        if ("neg".equals(this.getValue())) {
            int val = Integer.parseInt(rand.getValue());
            return new Int(Integer.toString(-1 * val));
        } else if ("not".equals(this.getValue())) {
            boolean val = Boolean.parseBoolean(rand.getValue());
            return new Bool(Boolean.toString(!val));
        } else {
            return new Err();
        }
    }

}
