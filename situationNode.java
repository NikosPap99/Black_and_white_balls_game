import java.util.ArrayList;

/* This is a class that will implement the nodes of the game's search tree. It has information about each node's children/parent,
it's depth and some useful functions for our game.*/
public class situationNode {
    private int depth;
    private ArrayList<situationNode> children = new ArrayList<situationNode>();
    private situationNode parent = null;
    private String[] situation;
    private int H = 0;
    
    public situationNode(int depth, String[] situation){
        this.depth = depth;
        this.situation = situation;
    }

    public void show() { // prints the information about the situation, which is used when the situation is a step in the eventual solution
        for (int i = 0; i < situation.length; i++) {
            System.out.print("" + situation[i] + " ");
        }
        System.out.print("g(n) : " + depth + " h(n) : " + H); // real cost and heuristic cost
    }

    public int getH() {
        return H;
    }

    public void setH(int H) {
        this.H = H;
    }

    public void setChildren(ArrayList<situationNode> children) {
        this.children = children;
    }

    public void setParent(situationNode parent) {
        this.parent = parent;
    }

    public situationNode getParent() {
        return parent;
    }

    public int getDepth() {
        return depth;
    }

    public String[] getSituation() {
        return situation;
    }
}