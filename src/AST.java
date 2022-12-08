import nodes.Node;

public class AST {
    private Node root;

    public AST(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return this.root;
    }

    /*
     * Call the standardize method in Node class on the root
     */
    public void standardize() {
        this.root.standardize();
    }
}
