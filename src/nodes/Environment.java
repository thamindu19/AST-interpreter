package nodes;

import java.util.HashMap;

public class Environment extends Node {
    private int index;
    private Environment parent;
    private boolean isRemoved = false;
    public HashMap<Id, Node> values;

    public Environment(int i) {
        super("e");
        this.setIndex(i);
        this.values = new HashMap<Id, Node>();
    }

    public void setIndex(int i) {
        this.index = i;
    }

    public int getIndex() {
        return this.index;
    }

    public void setParent(Environment e) {
        this.parent = e;
    }

    public Environment getParent() {
        return this.parent;
    }

    public void setIsRemoved(boolean isRemoved) {
        this.isRemoved = isRemoved;
    }

    public boolean getIsRemoved() {
        return this.isRemoved;
    }

    public Node lookup(Id id) {
        for (Id key : this.values.keySet()) {
            if (key.getValue().equals(id.getValue())) {
                return this.values.get(key);
            }
        }
        if (this.parent != null) {
            return this.parent.lookup(id);
        } else {
            return new Node(id.getValue());
        }
    }
}
