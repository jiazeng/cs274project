import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by jiazeng on 4/26/17.
 */
public class DelaunayTriangulation {

    public static void main(String[] args) {
        boolean alt = false;
        boolean validInput = false;
        String option = "";
        Scanner commandSC = new Scanner(System.in);

        // choose alternating option or vertical option
        while(!validInput) {
            System.out.println("Do you want to choose the alternating option? Y/N");
            option = commandSC.next().toLowerCase();
            if(option.equals("y") || option.equals("yes") || option.equals("n") || option.equals("no")){
                validInput = true; //input is valid
            }
        }
        if(option.contains("y")) {
            alt = true;
        }
        boolean validFileName = false;
        String inputFile = "";
        String[] fileNames = {"4.node", "633.node", "box.node", "dots.node", "flag.node", "grid.node", "ladder.node",
                "spiral.node", "tri.node", "ttimeu10000.node", "ttimeu100000.node", "ttimeu100000.node"};
        Set<String> fileSet = new HashSet<String>(Arrays.asList(fileNames));

        while (!validFileName) {
            System.out.println("Enter a valid input file name: " + Arrays.toString(fileNames));
            inputFile = commandSC.next().toLowerCase().trim();
            if (fileSet.contains(inputFile)) {
                validFileName = true; //exit loop
            }
        }
        // prompt for an output file name
        boolean validOutput = false;
        String outputFile = "";
        while(!validOutput) {
            System.out.println("Enter a valid output file that ends with .ele");
            outputFile = commandSC.next().toLowerCase().trim();
            if(outputFile.endsWith(".ele")) {
                validOutput = true;
            }
        }

        // Scan files to insert the data into points
        // insert points in to a vector of points to be sent to delaunay
        Scanner fileScan = null;
        try {
            fileScan = new Scanner(new File("./node/" + inputFile));
        } catch (FileNotFoundException e) {
            System.out.println("File not found exception");
            System.exit(1);
        }

        // String firstLine = "";
        int numVertices = 0;
        int numDimension = 0;
        int numAttributes = 0;
        int boudaryMarker = 0;

        // First line: <# of vertices> <dimension (must be 2)> <# of attributes> <# of boundary markers (0 or 1)>
        if (fileScan.hasNextLine()) { //first line
            Scanner lineScan = new Scanner((fileScan.nextLine())); //there must be 4 ints on the first line
            numVertices = lineScan.nextInt();
            numDimension = lineScan.nextInt();
            numAttributes = lineScan.nextInt();
            boudaryMarker = lineScan.nextInt();
        }

        ArrayList<Vertex> vertices = new ArrayList<Vertex>();

        // Remaining lines: <vertex #> <x> <y> [attributes] [boundary marker]
        // Blank lines and comments prefixed by `#' may be placed anywhere.
        // vertices must be numbered consecutively, starting from one or zero.
        while (fileScan.hasNextLine()) {
            String line = fileScan.nextLine();
            if(line.isEmpty() || !line.contains("#")) { //ignore empty lines and comments
                Scanner lineScan = new Scanner(line);
                int id = lineScan.nextInt();
                double x = lineScan.nextDouble();
                double y = lineScan.nextDouble();
                Vertex v = new Vertex(x, y, id);
                vertices.add(v);
            }
        }

        Subdivision s2 = new Subdivision();

        long startTime = System.nanoTime();

        Delaunay d = new Delaunay();
        Edge[] edgePair = d.delaunay(s2, 0, numVertices, true, alt, vertices);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("The time of the delaunay function is: " + duration/1000000 + " milliseconds.");

        try{
            d.triangulate(numVertices, edgePair[0], outputFile);
        } catch (IOException e){
            System.out.println("IO Exception");
        }

    }
}



