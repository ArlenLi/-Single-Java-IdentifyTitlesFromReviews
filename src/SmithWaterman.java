/**
 * Class: SmithWaterman
 * Author : Harry Hull  (found from https://github.com/Samangan/Smith-Waterman-Local-Alignment/blob/master/SmithWaterman.java)
 * Cite date: 2016/04/16
 * Description (by Harry Hull):
 * Implements the following Smith-Waterman algorithm http://en.wikipedia.org/wiki/Smith_waterman
 * Affine Gap algorithm taken from:
 * http://en.wikipedia.org/wiki/Gap_penalty#Affine_gap_penalty
 * gap = o + (l-1)*e;
 * o:	gap opening penalty  (o < 0)
 * l:	length of the gap
 * e:	gap extension penalty (o < e < 0)
 * PS: the code has been modified by Lindong Li according to the needs
 */
import java.util.Stack;

public class SmithWaterman {
    private String one, two;
    private int matrix[][];
    private int gap;
    private int match;
    private int o;
    private int l;
    private int e;
    private int iL;
    private int jL;
    private String alignOne;
    private String alignTwo;

    public SmithWaterman(String one, String two) {
        this.one = "-" + one.toLowerCase();
        this.two = "-" + two.toLowerCase();
        this.match = 1;

        // Define affine gap starting values
        o = -2;
        l = 0;
        e = -1;

        // initialize matrix to 0
        matrix = new int[one.length() + 1][two.length() + 1];
        for (int i = 0; i < one.length(); i++)
            for (int j = 0; j < two.length(); j++)
                matrix[i][j] = 0;

    }

    // returns the alignment score
    public int computeSmithWaterman() {
        for (int i = 0; i < one.length(); i++) {
            for (int j = 0; j < two.length(); j++) {
                gap = -1;
                        //o + (l - 1) * e;
                if (i != 0 && j != 0) {
                    if (one.charAt(i) == two.charAt(j)) {
                        // match
                        // reset l
                        l = 0;
                        matrix[i][j] = Math.max(0, Math.max(
                                matrix[i - 1][j - 1] + match, Math.max(
                                        matrix[i - 1][j] + gap,
                                        matrix[i][j - 1] + gap)));
                    } else {
                        // gap
                        l++;
                        matrix[i][j] = Math.max(0, Math.max(
                                matrix[i - 1][j - 1] + gap, Math.max(
                                        matrix[i - 1][j] + gap,
                                        matrix[i][j - 1] + gap)));
                    }
                }
            }
        }

        // find the highest value
        int longest = 0;
        for (int i = 0; i < one.length(); i++) {
            for (int j = 0; j < two.length(); j++) {
                if (matrix[i][j] > longest) {
                    longest = matrix[i][j];
                    iL = i;
                    jL = j;
                }
            }
        }

//        // Backtrack to reconstruct the path
//        int i = iL;
//        int j = jL;
//        Stack<String> actions = new Stack<String>();
//
//        while (i != 0 && j != 0) {
//            // diag case
//            if (Math.max(matrix[i - 1][j - 1],
//                    Math.max(matrix[i - 1][j], matrix[i][j - 1])) == matrix[i - 1][j - 1]) {
//                actions.push("align");
//                i = i - 1;
//                j = j - 1;
//                // left case
//            } else if (Math.max(matrix[i - 1][j - 1],
//                    Math.max(matrix[i - 1][j], matrix[i][j - 1])) == matrix[i][j - 1]) {
//                actions.push("insert");
//                j = j - 1;
//                // up case
//            } else {
//                actions.push("delete");
//                i = i - 1;
//            }
//        }
//
//        String alignOne = new String();
//        String alignTwo = new String();
//
//        Stack<String> backActions = (Stack<String>) actions.clone();
//        for (int z = 0; z < one.length(); z++) {
//            alignOne = alignOne + one.charAt(z);
//            if (!actions.empty()) {
//                String curAction = actions.pop();
//                // System.out.println(curAction);
//                if (curAction.equals("insert")) {
//                    alignOne = alignOne + "-";
//                    while (actions.size()!= 0 && actions.peek().equals("insert")) {
//                        alignOne = alignOne + "-";
//                        actions.pop();
//                    }
//                }
//            }
//        }
//
//        for (int z = 0; z < two.length(); z++) {
//            alignTwo = alignTwo + two.charAt(z);
//            if (!backActions.empty()) {
//                String curAction = backActions.pop();
//                if (curAction.equals("delete")) {
//                    alignTwo = alignTwo + "-";
//                    while (backActions.size()!= 0&&backActions.peek().equals("delete")) {
//                        alignTwo = alignTwo + "-";
//                        backActions.pop();
//                    }
//                }
//            }
//        }
//
//        // print alignment
//        // System.out.println(alignOne + "\n" + alignTwo);
        return longest;
    }

    // Return the subStrings with the shortest edit distance
    public void getAlignStrings() {
        StringBuffer bufferOne = new StringBuffer();
        StringBuffer bufferTwo = new StringBuffer();
        int tempI = iL;
        int tempJ = jL;
        Stack<String> actions = new Stack<String>();

        while(matrix[tempI][tempJ] != 0) {
            int max = Math.max(matrix[tempI - 1][tempJ - 1], Math.max(matrix[tempI - 1][tempJ], matrix[tempI][tempJ - 1]));
            if (max == matrix[tempI - 1][tempJ - 1]) {
                actions.push("alignment");
                tempI--;
                tempJ--;
            } else if (max == matrix[tempI - 1][tempJ]) {
                actions.push("deletion");
                tempI--;
            } else {
                actions.push("insertion");
                tempJ--;
            }
        }

        tempI++;
        tempJ++;
        Stack<String> temp = (Stack<String>) actions.clone();

        while(!temp.isEmpty()) {
            String action = temp.pop();

            if (action.equals("alignment")) {
                bufferOne.append(one.charAt(tempI));
                bufferTwo.append(two.charAt(tempJ));
                tempI++;
                tempJ++;
            } else if (action.equals("deletion")) {
                bufferOne.append(one.charAt(tempI));
                bufferTwo.append("-");
                tempI ++;
            } else {
                bufferOne.append("-");
                bufferTwo.append(two.charAt(tempJ));
                tempJ ++;
            }
        }
        alignOne = bufferOne.toString();
        alignTwo = bufferTwo.toString();
    }
    public void printMatrix() {
        for (int i = 0; i < one.length(); i++) {
            if (i == 0) {
                for (int z = 0; z < two.length(); z++) {
                    if (z == 0)
                        System.out.print("   ");
                    System.out.print(two.charAt(z) + "  ");

                    if (z == two.length() - 1)
                        System.out.println();
                }
            }

            for (int j = 0; j < two.length(); j++) {
                if (j == 0) {
                    System.out.print(one.charAt(i) + "  ");
                }
                System.out.print(matrix[i][j] + "  ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        // DNA sequence Test:
//        SmithWaterman sw = new SmithWaterman("Ahis", "This was the most touching movie, I loved every thing about it. I find it very hard to believe this movie has been taken out of circulation. I know its out there somewhere. My husband and children deserve to see this movie,and I've vowed to find it somehow. The actors,the music,the script were all exquisite and I can remember even as a young person the profound effect it had on the way I viewed and still view the value of life.<br /><br />");
//        sw.computeSmithWaterman();
//        System.out.println("Alignment Score: " + sw.computeSmithWaterman());
//        sw.printMatrix();
//        sw.getAlignStrings();
//        System.out.println(sw.alignOne + " " + sw.alignTwo);

        SmithWaterman sw = new SmithWaterman("200 AMERICAN", "I loved every thing about 200 ameraican.");
        sw.computeSmithWaterman();
        sw.getAlignStrings();
        sw.printMatrix();
        System.out.println("Alignment Score: " + sw.computeSmithWaterman());

        System.out.println(sw.alignOne);
        System.out.println(sw.alignTwo);
    }
}