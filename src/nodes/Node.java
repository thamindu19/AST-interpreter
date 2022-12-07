package nodes;

import java.util.ArrayList;

public class Node {
    private String value;
    private Node parent;
    public ArrayList<Node> children;
    private int depth;
    public boolean standardized = false;

    public Node(String value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getParent() {
        return this.parent;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return this.depth;
    }

    public int getSize() {
        return children.size();
    }

    public static Node create(String value, int depth) {
        Node node = new Node(value);
        node.setDepth(depth);
        node.children = new ArrayList<Node>();
        return node;
    }

    public static Node create(String value, int depth, Node parent, ArrayList<Node> children,
            boolean standardized) {
        Node node = new Node(value);
        node.setDepth(depth);
        node.setParent(parent);
        node.children = children;
        node.standardized = standardized;
        return node;
    }

    public void standardize() {
        if (!this.standardized) {
            for (Node child : this.children) {
                child.standardize();
            }
            switch (this.getValue()) {
                // standardizing let
                case "let":
                    Node temp1 = this.children.get(0).children.get(1);
                    temp1.setParent(this);
                    temp1.setDepth(this.depth + 1);
                    Node temp2 = this.children.get(1);
                    temp2.setParent(this.children.get(0));
                    temp2.setDepth(this.depth + 2);
                    this.children.set(1, temp1);
                    this.children.get(0).setValue("lambda");
                    this.children.get(0).children.set(1, temp2);
                    this.setValue("gamma");
                    break;
                // standardizing where
                case "where":
                    Node temp = this.children.get(0);
                    this.children.set(0, this.children.get(1));
                    this.children.set(1, temp);
                    this.setValue("let");
                    this.standardize();
                    break;
                // standardizing function_form
                case "function_form":
                    Node Ex = this.children.get(this.children.size() - 1);
                    Node currentLambda = create("lambda", this.depth + 1, this, new ArrayList<Node>(), true);
                    this.children.add(1, currentLambda);
                    while (!this.children.get(2).equals(Ex)) {
                        Node V = this.children.get(2);
                        this.children.remove(2);
                        V.setDepth(currentLambda.depth + 1);
                        V.setParent(currentLambda);
                        currentLambda.children.add(V);
                        if (this.children.size() > 3) {
                            currentLambda = create("lambda", currentLambda.depth + 1, currentLambda,
                                    new ArrayList<Node>(), true);
                            currentLambda.getParent().children.add(currentLambda);
                        }
                    }
                    currentLambda.children.add(Ex);
                    this.children.remove(2);
                    this.setValue("=");
                    break;
                // standardizing lambda (multi parameter functions)
                case "lambda":
                    if (this.children.size() > 2) {
                        Node Ey = this.children.get(this.children.size() - 1);
                        Node currentLambdax = create("lambda", this.depth + 1, this, new ArrayList<Node>(), true);
                        this.children.add(1, currentLambdax);
                        while (!this.children.get(2).equals(Ey)) {
                            Node V = this.children.get(2);
                            this.children.remove(2);
                            V.setDepth(currentLambdax.depth + 1);
                            V.setParent(currentLambdax);
                            currentLambdax.children.add(V);
                            if (this.children.size() > 3) {
                                currentLambdax = create("lambda", currentLambdax.depth + 1, currentLambdax,
                                        new ArrayList<Node>(), true);
                                currentLambdax.getParent().children.add(currentLambdax);
                            }
                        }
                        currentLambdax.children.add(Ey);
                        this.children.remove(2);
                    }
                    break;
                // standardizing within
                case "within":
                    Node X1 = this.children.get(0).children.get(0);
                    Node X2 = this.children.get(1).children.get(0);
                    Node E1 = this.children.get(0).children.get(1);
                    Node E2 = this.children.get(1).children.get(1);
                    Node gamma = create("gamma", this.depth + 1, this, new ArrayList<Node>(), true);
                    Node lambda = create("lambda", this.depth + 2, gamma, new ArrayList<Node>(), true);
                    X1.setDepth(X1.depth + 1);
                    X1.setParent(lambda);
                    X2.setDepth(X1.depth - 1);
                    X2.setParent(this);
                    E1.setDepth(E1.depth);
                    E1.setParent(gamma);
                    E2.setDepth(E2.depth + 1);
                    E2.setParent(lambda);
                    lambda.children.add(X1);
                    lambda.children.add(E2);
                    gamma.children.add(lambda);
                    gamma.children.add(E1);
                    this.children.clear();
                    this.children.add(X2);
                    this.children.add(gamma);
                    this.setValue("=");
                    break;
                // standardizing @
                case "@":
                    Node gamma1 = create("gamma", this.depth + 1, this, new ArrayList<Node>(), true);
                    Node e1 = this.children.get(0);
                    e1.setDepth(e1.getDepth() + 1);
                    e1.setParent(gamma1);
                    Node n = this.children.get(1);
                    n.setDepth(n.getDepth() + 1);
                    n.setParent(gamma1);
                    gamma1.children.add(n);
                    gamma1.children.add(e1);
                    this.children.remove(0);
                    this.children.remove(0);
                    this.children.add(0, gamma1);
                    this.setValue("gamma");
                    break;
                // standardizing and (simultaneous definitions)
                case "and":
                    Node comma = create(",", this.depth + 1, this, new ArrayList<Node>(), true);
                    Node tau = create("tau", this.depth + 1, this, new ArrayList<Node>(), true);
                    for (Node equal : this.children) {
                        equal.children.get(0).setParent(comma);
                        equal.children.get(1).setParent(tau);
                        comma.children.add(equal.children.get(0));
                        tau.children.add(equal.children.get(1));
                    }
                    this.children.clear();
                    this.children.add(comma);
                    this.children.add(tau);
                    this.setValue("=");
                    break;
                // standardizing rec
                case "rec":
                    Node X = this.children.get(0).children.get(0);
                    Node E = this.children.get(0).children.get(1);
                    Node F = create(X.getValue(), this.depth + 1, this, X.children, true);
                    Node G = create("gamma", this.depth + 1, this, new ArrayList<Node>(), true);
                    Node Y = create("ystar", this.depth + 2, G, new ArrayList<Node>(), true);
                    Node L = create("lambda", this.depth + 2, G, new ArrayList<Node>(), true);
                    X.setDepth(L.depth + 1);
                    X.setParent(L);
                    E.setDepth(L.depth + 1);
                    E.setParent(L);
                    L.children.add(X);
                    L.children.add(E);
                    G.children.add(Y);
                    G.children.add(L);
                    this.children.clear();
                    this.children.add(F);
                    this.children.add(G);
                    this.setValue("=");
                    break;
                // unary & binary operators, tuples, conditionals and commas are not
                // standardized due cse rules 6-13
                default:
                    break;
            }
        }
        this.standardized = true;
    }
}
