
package nodes;

import java.util.ArrayList;

public class Tuple extends Rand {
    public ArrayList<Node> nodes;

    public Tuple() {
        super("tup");
        this.nodes = new ArrayList<Node>();
    }

    public String getTuple() {
        String tuple = "(";
        for (Node node : this.nodes) {
            if (node instanceof Tuple) {
                tuple = tuple + ((Tuple) node).getTuple() + ", ";
            } else {
                tuple = tuple + node.getValue() + ", ";
            }
        }
        tuple = tuple.substring(0, tuple.length() - 2) + ")";
        return tuple;
    }

}
