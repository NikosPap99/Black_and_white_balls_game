import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import java.lang.Math;
import java.util.Scanner;


public class A_Star {
    private static int N; 

    // function that checks if the user input is valid according to the game rules
    public static boolean checkIfValidInput(String[] situation, int N) { 
        if(situation.length != (2*N + 1)) {                             
            return false; 
        }

        int whiteCounter = 0;
        int blackCounter = 0;

        for(int i = 0; i < situation.length; i++) { // anything other than the symbols 'W', 'B', '_' is invalid
            if(situation[i].equals("W")) {
                whiteCounter++;
                continue;
            }
            else if(situation[i].equals("B")) {
                blackCounter++;
                continue;
            }
            else if(situation[i].equals("_")) {
                continue;
            }
            return false;
        }

        if((whiteCounter != N) || (blackCounter != N)) { // crucial game rule
            return false;
        }

        return true;
    }

    // heuristic function
    public static int calculateH(String[] situation) { 
        int whitePos = 0; // the pos
        int blackCounter = 0;
        int H = 0;
        ArrayList<Integer> blackPositions = new ArrayList<Integer>();

        for(int i = 0; i < situation.length; i++) { // we pass every space until we find the first white ball...
            if(situation[i].equals("W")) {
                whitePos = i;
                for(int j = i + 1; j < situation.length; j++) { // ... and then we count how many black balls are on its right
                    if(situation[j].equals("B")) {
                        blackCounter++;
                        blackPositions.add(j);
                    }
                }
                if(blackCounter > ((float)N) / 2) { // in this case, there are too many black balls to move, so it's cheaper to move the white over all of them
                    H += (blackPositions.get(blackCounter - 1) + 1) - whitePos; 
                }
                else { // else, we calculate how much it will cost to move all the black balls to the left of the white one
                    int blackMovedCounter = 0;
                    for(int k = 0; k < blackPositions.size(); k++) {
                        H = H + (blackPositions.get(k) + 1) - whitePos - blackMovedCounter ;
                        blackMovedCounter++;
                    }
                    i = situation.length;
                }
                blackCounter = 0;
                blackPositions.clear();
            } // the loop is repeated for each white ball, if we move the first white ball
        }
        return H; // the heuristic value is returned
    }

    public static ArrayList<situationNode> findChildren(situationNode node) { // function that extends a node (finds children according to rules)
        String[] parentSituation = node.getSituation();
        ArrayList<situationNode> children = new ArrayList<situationNode>();
        ArrayList<String[]> grandparentSituations = new ArrayList<String[]>();
        situationNode grandparent = node.getParent();
        int parentDepth = node.getDepth();
        int blankPos = 0;

        while(grandparent != null) { // get all the parents/grandparents of the node
            grandparentSituations.add(grandparent.getSituation());
            grandparent = grandparent.getParent();
        }

        for(int i = 0; i < parentSituation.length; i++) {
            if(parentSituation[i].equals("_")) {
                blankPos = i;
            }
        }
        
        for(int i = 0; i < parentSituation.length; i++) {
            int cost = Math.abs(i - blankPos); // compute the distance of the ball from the blank space
            if(cost == 0) {
                continue;
            }

            if(cost <= N) { // only then can the ball move to the space
                String[] childSituation = new String[parentSituation.length];
                childSituation = Arrays.copyOf(parentSituation, parentSituation.length);
                String temp = childSituation[i];
                childSituation[i] = childSituation[blankPos];
                childSituation[blankPos] = temp; // we move the symbol to the space
                boolean cycle = false;

                for(int j = 0; j < grandparentSituations.size(); j++) { // if the new situation is identical to a grandparent, we must avoid it (loops)...
                    if(childSituation == grandparentSituations.get(j)) {
                        cycle = true;
                        break;
                    }
                }

                if(!cycle) { // ... else, compute its depth and add it to the list of children
                    int childDepth = parentDepth + cost;
                    situationNode childNode = new situationNode(childDepth, childSituation);
                    childNode.setParent(node);
                    children.add(childNode);
                }
            }
        }

        return children;
    }

    // function that checks if a situation is final
    public static boolean checkIfFinal(String[] situation) { 
        int blackCounter = 0;
        int pos = 0;

        if(!situation[situation.length - 1].equals("W")) {
            return false;
        }

        while(blackCounter < N) {
            if(situation[pos].equals("W")) {
                return false;
            }

            if(situation[pos].equals("B")) {
                blackCounter++;
            }

            pos++;
        }

        return true;
    }

    // once we reach the solution, we "climb up" the tree to get every situation we passed from in order to print the steps
    public static void printSolution(situationNode solution) {
        ArrayList<situationNode> path = new ArrayList<situationNode>(); // here we will store the path
        path.add(solution); // first of all we store the final situation
        situationNode parent = solution.getParent(); // then we go to its predecessors ...

        while(parent != null) { // ... and store each one of them until we get to the root of the tree (whose parent is null)
            path.add(0, parent);
            parent = parent.getParent();
        }

        for(situationNode node : path) { // now, we just need to show the path
            node.show();
            System.out.println(" ->");
        }

        System.out.println("Done!");
    }
    public static void main(String[] args) {
        Scanner scannerForN = new Scanner(System.in);
        System.out.println("Please enter N : "); // the user determines how many balls of the same colour there will be
        N = scannerForN.nextInt();
        while(N <= 0) { // we always need to check for invalid inputs
            System.out.println("N must be positive, enter another one : ");
            N = scannerForN.nextInt();
        }
        System.out.println("Please enter the initial situation : "); // the user then enter the situations he wishes to see the AI solve
        Scanner scannerForSituation = new Scanner(System.in);
        String[] initialSituation = scannerForSituation.nextLine().split("");
        while(!checkIfValidInput(initialSituation, N)) { // while the situation is not valid, the user is asked to reenter one
            System.out.println("Invalid situation, please enter something different : ");
            initialSituation = scannerForSituation.nextLine().split("");
        }
        ArrayList<situationNode> searchFrontier = new ArrayList<situationNode>(); // initialise search frontier
        ArrayList<situationNode> solutions = new ArrayList<situationNode>();
        situationNode initialSituationNode = new situationNode(0, initialSituation);
        searchFrontier.add(initialSituationNode);
        System.out.print("Initial situation: ");
        initialSituationNode.show();
        System.out.println("");

        while(!searchFrontier.isEmpty()) {
            situationNode node = searchFrontier.get(0); // first item of the frontier

            if(!solutions.isEmpty()) {
                int lowestCost = 9999;
                System.out.println(solutions.size());

                for(situationNode solution : solutions) {
                    if(solution.getDepth()  < lowestCost) {
                        lowestCost = solution.getDepth();
                    }
                }

                if((node.getH() + node.getDepth() )>= lowestCost) { // this means we have found the best solution
                    break;
                }
            }

            if(checkIfFinal(node.getSituation())) { // if it's a valid final situation we need to store it
                solutions.add(node);
                continue;
            }

            ArrayList<situationNode> children = findChildren(node);
            for(situationNode child: children) {
                child.setH(calculateH(child.getSituation()));
            }

            for(situationNode child : children) {
                for(int i = 0; i < searchFrontier.size(); i++) {
                    if((child.getDepth() + child.getH()) <= (searchFrontier.get(i).getDepth() + searchFrontier.get(i).getH())) {
                        searchFrontier.add(i, child);
                        break;
                    }

                    if(i == searchFrontier.size() - 1) {
                        searchFrontier.add(child);
                    }
                }
            }
            searchFrontier.remove(node);
        }

        for(situationNode solution : solutions) {
            printSolution(solution);
        }
    }
}
