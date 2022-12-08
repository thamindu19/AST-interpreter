
package nodes;

public class Binary extends Rator {
    public Binary(String data) {
        super(data);
    }

    public Node operate(Rand rand1, Rand rand2) {
        if ("+".equals(this.getValue())) {
            int val1 = Integer.parseInt(rand1.getValue());
            int val2 = Integer.parseInt(rand2.getValue());
            return new Int(Integer.toString(val1 + val2));
        } else if ("-".equals(this.getValue())) {
            int val1 = Integer.parseInt(rand1.getValue());
            int val2 = Integer.parseInt(rand2.getValue());
            return new Int(Integer.toString(val1 - val2));
        } else if ("*".equals(this.getValue())) {
            int val1 = Integer.parseInt(rand1.getValue());
            int val2 = Integer.parseInt(rand2.getValue());
            return new Int(Integer.toString(val1 * val2));
        } else if ("/".equals(this.getValue())) {
            int val1 = Integer.parseInt(rand1.getValue());
            int val2 = Integer.parseInt(rand2.getValue());
            return new Int(Integer.toString(val1 / val2));
        } else if ("**".equals(this.getValue())) {
            int val1 = Integer.parseInt(rand1.getValue());
            int val2 = Integer.parseInt(rand2.getValue());
            return new Int(Integer.toString((int) Math.pow(val1, val2)));
        } else if ("&".equals(this.getValue())) {
            boolean val1 = Boolean.parseBoolean(rand1.getValue());
            boolean val2 = Boolean.parseBoolean(rand2.getValue());
            return new Bool(Boolean.toString(val1 && val2));
        } else if ("or".equals(this.getValue())) {
            boolean val1 = Boolean.parseBoolean(rand1.getValue());
            boolean val2 = Boolean.parseBoolean(rand2.getValue());
            return new Bool(Boolean.toString(val1 || val2));
        } else if ("eq".equals(this.getValue())) {
            String val1 = rand1.getValue();
            String val2 = rand2.getValue();
            return new Bool(Boolean.toString(val1.equals(val2)));
        } else if ("ne".equals(this.getValue())) {
            String val1 = rand1.getValue();
            String val2 = rand2.getValue();
            return new Bool(Boolean.toString(!val1.equals(val2)));
        } else if ("ls".equals(this.getValue())) {
            int val1 = Integer.parseInt(rand1.getValue());
            int val2 = Integer.parseInt(rand2.getValue());
            return new Bool(Boolean.toString(val1 < val2));
        } else if ("le".equals(this.getValue())) {
            int val1 = Integer.parseInt(rand1.getValue());
            int val2 = Integer.parseInt(rand2.getValue());
            return new Bool(Boolean.toString(val1 <= val2));
        } else if ("gr".equals(this.getValue())) {
            int val1 = Integer.parseInt(rand1.getValue());
            int val2 = Integer.parseInt(rand2.getValue());
            return new Bool(Boolean.toString(val1 > val2));
        } else if ("ge".equals(this.getValue())) {
            int val1 = Integer.parseInt(rand1.getValue());
            int val2 = Integer.parseInt(rand2.getValue());
            return new Bool(Boolean.toString(val1 >= val2));
        } else if ("aug".equals(this.getValue())) {
            if (rand2 instanceof Tuple) {
                ((Tuple) rand1).nodes.addAll(((Tuple) rand2).nodes);
            } else {
                ((Tuple) rand1).nodes.add(rand2);
            }
            return rand1;
        } else {
            return new Err();
        }
    }

}
