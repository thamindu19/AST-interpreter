import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import nodes.Node;

public class myrpal {

    public static void main(String[] args) throws FileNotFoundException {

        // Get input data
        String fileName = args[0];
        Scanner scanner = new Scanner(new File(fileName));
        ArrayList<String> data = new ArrayList<String>();
        while (scanner.hasNextLine()) {
            data.add(scanner.nextLine());
        }
        scanner.close();

        // Create root node
        int depth = 0;
        Node root = Node.create(data.get(0), depth, null, new ArrayList<Node>(), false);

        // Build the Abstract Syntax Tree
        AST ast = new AST(root);
        Node previousNode = root;
        for (String s : data.subList(1, data.size())) {
            int i = 0;
            int d = 0;
            while (s.charAt(i) == '.') {
                i++;
                d++;
            }
            Node node = Node.create(s.substring(i), d, null, new ArrayList<Node>(), false);
            if (depth < d) {
                node.setParent(previousNode);
                previousNode.children.add(node);
            } else {
                while (previousNode.getDepth() != d) {
                    previousNode = previousNode.getParent();
                }
                node.setParent(previousNode.getParent());
                previousNode.getParent().children.add(node);
            }
            previousNode = node;
            depth = d;
        }

        // Standardize the AST
        ast.standardize();

        // Build the CSE Machine
        CSEMachine machine = new CSEMachine(ast);

        // Output the answer
        System.out.println(machine.evaluate());
    }
}