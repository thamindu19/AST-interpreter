import nodes.Node;

public class AST {
    private Node root;

    public AST(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return this.root;
    }

    public void standardize() {
        this.root.standardize();
    }
}
