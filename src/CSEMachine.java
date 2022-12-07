
import java.util.ArrayList;

import nodes.*;
import nodes.Node;

public class CSEMachine {
    private ArrayList<Node> control;
    private ArrayList<Node> stack;
    private ArrayList<E> environment;
    private static E e0 = new E(0);
    private static int i = 1;
    private static int j = 0;

    public CSEMachine(AST ast) {
        ArrayList<Node> control = new ArrayList<Node>();
        control.add(CSEMachine.e0);
        control.add(this.getDelta(ast.getRoot()));
        this.control = control;
        ArrayList<Node> stack = new ArrayList<Node>();
        stack.add(CSEMachine.e0);
        this.stack = stack;
        ArrayList<E> environment = new ArrayList<E>();
        environment.add(CSEMachine.e0);
        this.environment = environment;
    }

    public void execute() {
        E currentEnvironment = this.environment.get(0);
        int j = 1;
        while (!control.isEmpty()) {
            // pop last element of the control
            Node currentNode = control.get(control.size() - 1);
            control.remove(control.size() - 1);
            // rule no. 1
            if (currentNode instanceof Id) {
                this.stack.add(0, currentEnvironment.lookup((Id) currentNode));
                // rule no. 2
            } else if (currentNode instanceof Lambda) {
                Lambda lambda = (Lambda) currentNode;
                lambda.setEnvironment(currentEnvironment.getIndex());
                this.stack.add(0, lambda);
                // rule no. 3, 4, 10, 11, 12 & 13
            } else if (currentNode instanceof Gamma) {
                Node nextNode = this.stack.get(0);
                this.stack.remove(0);
                // lambda (rule no. 4 & 11)
                if (nextNode instanceof Lambda) {
                    Lambda lambda = (Lambda) nextNode;
                    E e = new E(j++);
                    if (lambda.identifiers.size() == 1) {
                        e.values.put(lambda.identifiers.get(0), this.stack.get(0));
                        this.stack.remove(0);
                    } else {
                        Tup tup = (Tup) this.stack.get(0);
                        this.stack.remove(0);
                        int i = 0;
                        for (Id id : lambda.identifiers) {
                            e.values.put(id, tup.nodes.get(i++));
                        }
                    }
                    for (E environment : this.environment) {
                        if (environment.getIndex() == lambda.getEnvironment()) {
                            e.setParent(environment);
                        }
                    }
                    currentEnvironment = e;
                    this.control.add(e);
                    this.control.add(lambda.getDelta());
                    this.stack.add(0, e);
                    this.environment.add(e);
                    // tup (rule no. 10)
                } else if (nextNode instanceof Tup) {
                    Tup tup = (Tup) nextNode;
                    int i = Integer.parseInt(this.stack.get(0).getValue());
                    this.stack.remove(0);
                    this.stack.add(0, tup.nodes.get(i - 1));
                    // ystar (rule no. 12)
                } else if (nextNode instanceof Ystar) {
                    Lambda lambda = (Lambda) this.stack.get(0);
                    this.stack.remove(0);
                    Eta eta = new Eta();
                    eta.setIndex(lambda.getIndex());
                    eta.setEnvironment(lambda.getEnvironment());
                    eta.setIdentifier(lambda.identifiers.get(0));
                    eta.setLambda(lambda);
                    this.stack.add(0, eta);
                    // eta (rule no. 13)
                } else if (nextNode instanceof Eta) {
                    Eta eta = (Eta) nextNode;
                    Lambda lambda = eta.getLambda();
                    this.control.add(new Gamma());
                    this.control.add(new Gamma());
                    this.stack.add(0, eta);
                    this.stack.add(0, lambda);
                    // builtin functions
                } else {
                    if ("Print".equals(nextNode.getValue())) {
                        // do nothing
                    } else if ("Stem".equals(nextNode.getValue())) {
                        Node s = this.stack.get(0);
                        this.stack.remove(0);
                        s.setValue(s.getValue().substring(0, 1));
                        this.stack.add(0, s);
                    } else if ("Stern".equals(nextNode.getValue())) {
                        Node s = this.stack.get(0);
                        this.stack.remove(0);
                        s.setValue(s.getValue().substring(1));
                        this.stack.add(0, s);
                    } else if ("Conc".equals(nextNode.getValue())) {
                        Node s1 = this.stack.get(0);
                        Node s2 = this.stack.get(1);
                        this.stack.remove(0);
                        this.stack.remove(0);
                        s1.setValue(s1.getValue() + s2.getValue());
                        this.stack.add(0, s1);
                    } else if ("Order".equals(nextNode.getValue())) {
                        Tup tup = (Tup) this.stack.get(0);
                        this.stack.remove(0);
                        Int n = new Int(Integer.toString(tup.nodes.size()));
                        this.stack.add(0, n);
                    } else if ("Null".equals(nextNode.getValue())) {
                        // implement
                    } else if ("Itos".equals(nextNode.getValue())) {
                        // implement
                    } else if ("Isinteger".equals(nextNode.getValue())) {
                        if (this.stack.get(0) instanceof Int) {
                            this.stack.add(0, new Bool("true"));
                        } else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);
                    } else if ("Isstring".equals(nextNode.getValue())) {
                        if (this.stack.get(0) instanceof Str) {
                            this.stack.add(0, new Bool("true"));
                        } else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);
                    } else if ("Istuple".equals(nextNode.getValue())) {
                        if (this.stack.get(0) instanceof Tup) {
                            this.stack.add(0, new Bool("true"));
                        } else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);
                    } else if ("Isdummy".equals(nextNode.getValue())) {
                        if (this.stack.get(0) instanceof Dummy) {
                            this.stack.add(0, new Bool("true"));
                        } else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);
                    } else if ("Istruthvalue".equals(nextNode.getValue())) {
                        if (this.stack.get(0) instanceof Bool) {
                            this.stack.add(0, new Bool("true"));
                        } else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);
                    } else if ("Isfunction".equals(nextNode.getValue())) {
                        if (this.stack.get(0) instanceof Lambda) {
                            this.stack.add(0, new Bool("true"));
                        } else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);
                    }
                }
                // rule no. 5
            } else if (currentNode instanceof E) {
                this.stack.remove(1);
                this.environment.get(((E) currentNode).getIndex()).setIsRemoved(true);
                int y = this.environment.size();
                while (y > 0) {
                    if (!this.environment.get(y - 1).getIsRemoved()) {
                        currentEnvironment = this.environment.get(y - 1);
                        break;
                    } else {
                        y--;
                    }
                }
                // rule no. 6 & 7
            } else if (currentNode instanceof Rator) {
                if (currentNode instanceof Uop) {
                    Node rator = currentNode;
                    Node rand = this.stack.get(0);
                    this.stack.remove(0);
                    stack.add(0, this.applyUnaryOperation(rator, rand));
                }
                if (currentNode instanceof Bop) {
                    Node rator = currentNode;
                    Node rand1 = this.stack.get(0);
                    Node rand2 = this.stack.get(1);
                    this.stack.remove(0);
                    this.stack.remove(0);
                    this.stack.add(0, this.applyBinaryOperation(rator, rand1, rand2));
                }
                // rule no. 8
            } else if (currentNode instanceof Beta) {
                if (Boolean.parseBoolean(this.stack.get(0).getValue())) {
                    this.control.remove(control.size() - 1);
                } else {
                    this.control.remove(control.size() - 2);
                }
                this.stack.remove(0);
                // rule no. 9
            } else if (currentNode instanceof Tau) {
                Tau tau = (Tau) currentNode;
                Tup tup = new Tup();
                for (int i = 0; i < tau.getN(); i++) {
                    tup.nodes.add(this.stack.get(0));
                    this.stack.remove(0);
                }
                this.stack.add(0, tup);
            } else if (currentNode instanceof Delta) {
                this.control.addAll(((Delta) currentNode).nodes);
            } else if (currentNode instanceof B) {
                this.control.addAll(((B) currentNode).nodes);
            } else {
                this.stack.add(0, currentNode);
            }
        }
    }

    public void printStack() {
        System.out.print("Stack: ");
        for (Node Node : this.stack) {
            System.out.print(Node.getValue());
            if (Node instanceof Lambda) {
                System.out.print(((Lambda) Node).getIndex());
            } else if (Node instanceof Delta) {
                System.out.print(((Delta) Node).getIndex());
            } else if (Node instanceof E) {
                System.out.print(((E) Node).getIndex());
            } else if (Node instanceof Eta) {
                System.out.print(((Eta) Node).getIndex());
            }
            System.out.print(",");
        }
        System.out.println();
    }

    public Node applyUnaryOperation(Node rator, Node rand) {
        if ("neg".equals(rator.getValue())) {
            int val = Integer.parseInt(rand.getValue());
            return new Int(Integer.toString(-1 * val));
        } else if ("not".equals(rator.getValue())) {
            boolean val = Boolean.parseBoolean(rand.getValue());
            return new Bool(Boolean.toString(!val));
        } else {
            return new Err();
        }
    }

    public Node applyBinaryOperation(Node rator, Node rand1, Node rand2) {
        if ("+".equals(rator.getValue())) {
            int val1 = Integer.parseInt(rand1.getValue());
            int val2 = Integer.parseInt(rand2.getValue());
            return new Int(Integer.toString(val1 + val2));
        } else if ("-".equals(rator.getValue())) {
            int val1 = Integer.parseInt(rand1.getValue());
            int val2 = Integer.parseInt(rand2.getValue());
            return new Int(Integer.toString(val1 - val2));
        } else if ("*".equals(rator.getValue())) {
            int val1 = Integer.parseInt(rand1.getValue());
            int val2 = Integer.parseInt(rand2.getValue());
            return new Int(Integer.toString(val1 * val2));
        } else if ("/".equals(rator.getValue())) {
            int val1 = Integer.parseInt(rand1.getValue());
            int val2 = Integer.parseInt(rand2.getValue());
            return new Int(Integer.toString(val1 / val2));
        } else if ("**".equals(rator.getValue())) {
            int val1 = Integer.parseInt(rand1.getValue());
            int val2 = Integer.parseInt(rand2.getValue());
            return new Int(Integer.toString((int) Math.pow(val1, val2)));
        } else if ("&".equals(rator.getValue())) {
            boolean val1 = Boolean.parseBoolean(rand1.getValue());
            boolean val2 = Boolean.parseBoolean(rand2.getValue());
            return new Bool(Boolean.toString(val1 && val2));
        } else if ("or".equals(rator.getValue())) {
            boolean val1 = Boolean.parseBoolean(rand1.getValue());
            boolean val2 = Boolean.parseBoolean(rand2.getValue());
            return new Bool(Boolean.toString(val1 || val2));
        } else if ("eq".equals(rator.getValue())) {
            String val1 = rand1.getValue();
            String val2 = rand2.getValue();
            return new Bool(Boolean.toString(val1.equals(val2)));
        } else if ("ne".equals(rator.getValue())) {
            String val1 = rand1.getValue();
            String val2 = rand2.getValue();
            return new Bool(Boolean.toString(!val1.equals(val2)));
        } else if ("ls".equals(rator.getValue())) {
            int val1 = Integer.parseInt(rand1.getValue());
            int val2 = Integer.parseInt(rand2.getValue());
            return new Bool(Boolean.toString(val1 < val2));
        } else if ("le".equals(rator.getValue())) {
            int val1 = Integer.parseInt(rand1.getValue());
            int val2 = Integer.parseInt(rand2.getValue());
            return new Bool(Boolean.toString(val1 <= val2));
        } else if ("gr".equals(rator.getValue())) {
            int val1 = Integer.parseInt(rand1.getValue());
            int val2 = Integer.parseInt(rand2.getValue());
            return new Bool(Boolean.toString(val1 > val2));
        } else if ("ge".equals(rator.getValue())) {
            int val1 = Integer.parseInt(rand1.getValue());
            int val2 = Integer.parseInt(rand2.getValue());
            return new Bool(Boolean.toString(val1 >= val2));
        } else if ("aug".equals(rator.getValue())) {
            if (rand2 instanceof Tup) {
                ((Tup) rand1).nodes.addAll(((Tup) rand2).nodes);
            } else {
                ((Tup) rand1).nodes.add(rand2);
            }
            return rand1;
        } else {
            return new Err();
        }
    }

    public String getTupleValue(Tup tup) {
        String temp = "(";
        for (Node Node : tup.nodes) {
            if (Node instanceof Tup) {
                temp = temp + this.getTupleValue((Tup) Node) + ", ";
            } else {
                temp = temp + Node.getValue() + ", ";
            }
        }
        temp = temp.substring(0, temp.length() - 2) + ")";
        return temp;
    }

    public String output() {
        this.execute();
        if (stack.get(0) instanceof Tup) {
            return this.getTupleValue((Tup) stack.get(0));
        }
        return stack.get(0).getValue();
    }

    public Node getNode(Node node) {
        switch (node.getValue()) {
            // unary operators
            case "not":
            case "neg":
                return new Uop(node.getValue());
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
                return new Bop(node.getValue());
            // gamma
            case "gamma":
                return new Gamma();
            // tau
            case "tau":
                return new Tau(node.children.size());
            // ystar
            case "ystar":
                return new Ystar();
            // operands <ID:>, <INT:>, <STR:>, <nil>, <true>, <false>, <dummy>
            default:
                if (node.getValue().startsWith("<ID:")) {
                    return new Id(node.getValue().substring(4, node.getValue().length() - 1));
                } else if (node.getValue().startsWith("<INT:")) {
                    return new Int(node.getValue().substring(5, node.getValue().length() - 1));
                } else if (node.getValue().startsWith("<STR:")) {
                    return new Str(node.getValue().substring(6, node.getValue().length() - 2));
                } else if (node.getValue().startsWith("<nil")) {
                    return new Tup();
                } else if (node.getValue().startsWith("<true>")) {
                    return new Bool("true");
                } else if (node.getValue().startsWith("<false>")) {
                    return new Bool("false");
                } else if (node.getValue().startsWith("<dummy>")) {
                    return new Dummy();
                } else {
                    System.out.println("Err node: " + node.getValue());
                    return new Err();
                }
        }
    }

    public B getB(Node node) {
        B b = new B();
        b.nodes = this.getPreOrderTraverse(node);
        return b;
    }

    public Lambda getLambda(Node node) {
        Lambda lambda = new Lambda(CSEMachine.i++);
        lambda.setDelta(this.getDelta(node.children.get(1)));
        if (",".equals(node.children.get(0).getValue())) {
            for (Node identifier : node.children.get(0).children) {
                lambda.identifiers.add(new Id(identifier.getValue().substring(4, node.getValue().length() - 1)));
            }
        } else {
            lambda.identifiers.add(
                    new Id(node.children.get(0).getValue().substring(4, node.children.get(0).getValue().length() - 1)));
        }
        return lambda;
    }

    private ArrayList<Node> getPreOrderTraverse(Node node) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        if ("lambda".equals(node.getValue())) {
            nodes.add(this.getLambda(node));
        } else if ("->".equals(node.getValue())) {
            nodes.add(this.getDelta(node.children.get(1)));
            nodes.add(this.getDelta(node.children.get(2)));
            nodes.add(new Beta());
            nodes.add(this.getB(node.children.get(0)));
        } else {
            nodes.add(this.getNode(node));
            for (Node child : node.children) {
                nodes.addAll(this.getPreOrderTraverse(child));
            }
        }
        return nodes;
    }

    public Delta getDelta(Node node) {
        Delta delta = new Delta(CSEMachine.j++);
        delta.nodes = this.getPreOrderTraverse(node);
        return delta;
    }

}
