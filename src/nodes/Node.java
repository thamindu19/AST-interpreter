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

    public Node generateNode() {
        switch (this.getValue()) {
            // unary operators
            case "not":
            case "neg":
                return new Unary(this.getValue());
            // binary operators
            case "+":
            case "-":
            case "*":
            case "/":
            case "**":
            case "&":
            case "or":
            case "eq":
            case "ne":
            case "ls":
            case "le":
            case "gr":
            case "ge":
            case "aug":
                return new Binary(this.getValue());
            // gamma
            case "gamma":
                return new Gamma();
            // tau
            case "tau":
                return new Tau(this.children.size());
            // ystar
            case "ystar":
                return new Ystar();
            // operands <ID:>, <INT:>, <STR:>, <nil>, <true>, <false>, <dummy>
            default:
                if (this.getValue().startsWith("<ID:")) {
                    return new Id(this.getValue().substring(4, this.getValue().length() - 1));
                } else if (this.getValue().startsWith("<INT:")) {
                    return new Int(this.getValue().substring(5, this.getValue().length() - 1));
                } else if (this.getValue().startsWith("<STR:")) {
                    return new Str(this.getValue().substring(6, this.getValue().length() - 2));
                } else if (this.getValue().startsWith("<nil")) {
                    return new Tuple();
                } else if (this.getValue().startsWith("<true>")) {
                    return new Bool("true");
                } else if (this.getValue().startsWith("<false>")) {
                    return new Bool("false");
                } else if (this.getValue().startsWith("<dummy>")) {
                    return new Dummy();
                } else {
                    System.out.println("Err node: " + this.getValue());
                    return new Err();
                }
        }
    }

    public void standardize() {
        if (!this.standardized) {
            for (Node child : this.children) {
                child.standardize();
            }
            Node X, X1, X2, E, E1, E2, P, N, F, lambda, gamma, comma, tau, ystar;
            switch (this.getValue()) {
                case "let":
                    E = this.children.get(0).children.get(1);
                    E.setParent(this);
                    E.setDepth(this.depth + 1);
                    P = this.children.get(1);
                    P.setParent(this.children.get(0));
                    P.setDepth(this.depth + 2);
                    this.children.set(1, E);
                    this.children.get(0).setValue("lambda");
                    this.children.get(0).children.set(1, P);
                    this.setValue("gamma");
                    break;
                case "where":
                    P = this.children.get(0);
                    this.children.set(0, this.children.get(1));
                    this.children.set(1, P);
                    this.setValue("let");
                    this.standardize();
                    break;
                case "function_form":
                    E = this.children.get(this.children.size() - 1);
                    lambda = create("lambda", this.depth + 1, this, new ArrayList<Node>(), true);
                    this.children.add(1, lambda);
                    while (!this.children.get(2).equals(E)) {
                        Node V = this.children.get(2);
                        this.children.remove(2);
                        V.setDepth(lambda.depth + 1);
                        V.setParent(lambda);
                        lambda.children.add(V);
                        if (this.children.size() > 3) {
                            lambda = create("lambda", lambda.depth + 1, lambda,
                                    new ArrayList<Node>(), true);
                            lambda.getParent().children.add(lambda);
                        }
                    }
                    lambda.children.add(E);
                    this.children.remove(2);
                    this.setValue("=");
                    break;
                case "lambda":
                    if (this.children.size() > 2) {
                        E = this.children.get(this.children.size() - 1);
                        lambda = create("lambda", this.depth + 1, this, new ArrayList<Node>(), true);
                        this.children.add(1, lambda);
                        while (!this.children.get(2).equals(E)) {
                            Node V = this.children.get(2);
                            this.children.remove(2);
                            V.setDepth(lambda.depth + 1);
                            V.setParent(lambda);
                            lambda.children.add(V);
                            if (this.children.size() > 3) {
                                lambda = create("lambda", lambda.depth + 1, lambda,
                                        new ArrayList<Node>(), true);
                                lambda.getParent().children.add(lambda);
                            }
                        }
                        lambda.children.add(E);
                        this.children.remove(2);
                    }
                    break;
                case "within":
                    X1 = this.children.get(0).children.get(0);
                    X2 = this.children.get(1).children.get(0);
                    E1 = this.children.get(0).children.get(1);
                    E2 = this.children.get(1).children.get(1);
                    gamma = create("gamma", this.depth + 1, this, new ArrayList<Node>(), true);
                    lambda = create("lambda", this.depth + 2, gamma, new ArrayList<Node>(), true);
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
                case "@":
                    gamma = create("gamma", this.depth + 1, this, new ArrayList<Node>(), true);
                    E1 = this.children.get(0);
                    E1.setDepth(E1.getDepth() + 1);
                    E1.setParent(gamma);
                    N = this.children.get(1);
                    N.setDepth(N.getDepth() + 1);
                    N.setParent(gamma);
                    gamma.children.add(N);
                    gamma.children.add(E1);
                    this.children.remove(0);
                    this.children.remove(0);
                    this.children.add(0, gamma);
                    this.setValue("gamma");
                    break;
                case "and":
                    comma = create(",", this.depth + 1, this, new ArrayList<Node>(), true);
                    tau = create("tau", this.depth + 1, this, new ArrayList<Node>(), true);
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
                case "rec":
                    X = this.children.get(0).children.get(0);
                    E = this.children.get(0).children.get(1);
                    F = create(X.getValue(), this.depth + 1, this, X.children, true);
                    gamma = create("gamma", this.depth + 1, this, new ArrayList<Node>(), true);
                    ystar = create("ystar", this.depth + 2, gamma, new ArrayList<Node>(), true);
                    lambda = create("lambda", this.depth + 2, gamma, new ArrayList<Node>(), true);
                    X.setDepth(lambda.depth + 1);
                    X.setParent(lambda);
                    E.setDepth(lambda.depth + 1);
                    E.setParent(lambda);
                    lambda.children.add(X);
                    lambda.children.add(E);
                    gamma.children.add(ystar);
                    gamma.children.add(lambda);
                    this.children.clear();
                    this.children.add(F);
                    this.children.add(gamma);
                    this.setValue("=");
                    break;
                default:
                    break;
            }
        }
        this.standardized = true;
    }
}
