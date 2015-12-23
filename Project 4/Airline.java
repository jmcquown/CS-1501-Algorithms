
import java.util.*;
import java.io.*;

public class Airline {
	private static final String NEWLINE = System.getProperty("line.separator");

	static EdgeWeightedDigraph priceGraph, distanceGraph;
	static ArrayList<String> vertexArray, printArray;	//printArray holds the strings from the text file
	static DirectedEdge edge;
	static File file, tempFile;
	static Scanner fileScan;
	public static void main(String[] args) {
		Scanner inScan = new Scanner(System.in);
		String fileName;

		System.out.println("Please enter the file name: ");
		fileName = inScan.nextLine();
		file = new File(fileName);
		try {
			fileScan = new Scanner(file);
			readFile(fileScan);
			//User entered 0
			//Write route list to file
			tempFile = new File("temp.txt");
			

			menu(inScan);

			PrintWriter printWriter = new PrintWriter(new FileWriter(file, false));

			//Write number of vertices
			printWriter.println(priceGraph.V()-1);
			//Write all the names of the vertices to the file
			for (int i = 1; i < vertexArray.size(); i++)
				printWriter.println(vertexArray.get(i));

			//Iterate through the ArrayList that holds the strings of each route from the original text file
			for (int i = 0; i < printArray.size(); i++)
				printWriter.println(printArray.get(i));

			printWriter.close();
		}
		catch (Exception e) {
			System.out.println("File Not Found.");
			System.exit(0);
		}
	}

	//Reads the file inputted by the user
	public static void readFile(Scanner fileScan) {
		//Get the number of vertices
		int numOfVertices = 0;
		numOfVertices = fileScan.nextInt();
		fileScan.nextLine();	//Get rid of new line character

		//Set the global array to the appropriate size
		vertexArray = new ArrayList<String> (numOfVertices + 1);
		//Need to add something at the first index and then I can start adding the vertices at their appropriate indecies
		vertexArray.add("Zero");	

		//Create two new graphs. 
		//priceGraph's weights are the price and distanceGraph's weights are the distance.
		priceGraph = new EdgeWeightedDigraph(numOfVertices + 1);
		distanceGraph = new EdgeWeightedDigraph(numOfVertices + 1);

		//printArray will hold the original route information. No back edges.
		//This will make it easier to print to the file later when the program exits
		printArray = new ArrayList<String>();

		//Store the name of each vertex into the ArrayList at vertex number
		for (int i = 0; i < numOfVertices; i++) 
			vertexArray.add(i + 1, fileScan.nextLine());


		int vertex1, vertex2, distance;
		double price;
		String [] info;
		String currentLine;
		//Get all the information and add new edges to the graph
		while (fileScan.hasNextLine()) {
			currentLine = fileScan.nextLine();
			info = currentLine.split(" ");
			vertex1 = Integer.parseInt(info[0]);
			vertex2 = Integer.parseInt(info[1]);
			distance = Integer.parseInt(info[2]);
			price = Double.parseDouble(info[3]);

			//Create new edges and add them to their respective graphs
			edge = new DirectedEdge(vertex1, vertex2, price);
			priceGraph.addEdge(edge);
			//Add the currentLine to the printArray in order to save the lines I need to print to the text file later
			printArray.add(currentLine);

			edge = new DirectedEdge(vertex2, vertex1, price);
			priceGraph.addEdge(edge);

			edge = new DirectedEdge(vertex1, vertex2, distance);
			distanceGraph.addEdge(edge);
			edge = new DirectedEdge(vertex2, vertex1, distance);
			distanceGraph.addEdge(edge);
		}

	}

	//Modified version of the toString() method from EdgeWeightDigraph.java
	public static String printRoutes(EdgeWeightedDigraph G) {
		StringBuilder s = new StringBuilder();
		s.append(NEWLINE);
		for (int v = 1; v < G.V(); v++) {
			Iterator<DirectedEdge> iterator = priceGraph.adj(v).iterator();

			s.append("(" + v + ") " + vertexArray.get(v) + ": ");
			for (DirectedEdge e : G.adj[v]) {
				s.append(vertexArray.get(e.from()) + "->" + vertexArray.get(e.to()) + " " + (int) e.weight() + " " + iterator.next().weight() + " ");
			}
			s.append(NEWLINE);
		}
		return s.toString();
	}

	//Method that will print the shortest path from one vertex to another depending on the graph that is passed in
	public static void shortestPath(EdgeWeightedDigraph G, Scanner s) {
		s.nextLine();	//Consume newline character

		//Prompt the user for the source and destination
		String source, destination;
		System.out.println("Enter the source: ");
		source = s.nextLine();
		System.out.println("Enter the destination: ");
		destination = s.nextLine();

		DijkstraSP dijPath = new DijkstraSP(G, vertexArray.indexOf(source));
		//If they are both in the ArrayList of the vertices, then calculate the shortest path and print it
		if (dijPath.hasPathTo(vertexArray.indexOf(destination))) {
			StringBuilder st = new StringBuilder();
			int totalWeight = 0;
			//For loop which I don't understand
			for (DirectedEdge e : dijPath.pathTo(vertexArray.indexOf(destination))) {
				st.append(vertexArray.get(e.from()) + " " + e.weight() + " ");
				totalWeight += e.weight();
			}
			System.out.println("SHORTEST PATH from " + source + " to " + destination);
			System.out.println("----------------------------------------------");
			System.out.println("Shortest path from " + source + " to " + destination + " is " + totalWeight);

			System.out.println(st.toString());
			
		}
		else {
			System.out.println("Invalid source or destination");
		}
	}
	//Creates a new route using EdgeWeightedDigraph's addEdge() method
	public static void addRoute(Scanner s) {
		s.nextLine();
		String source, destination;
		int distance;
		double price;
		//Get the info from the user
		//Description foesn't specify on whether the source and dest are to be entered as a string or int...
		System.out.println("Enter source: ");
		source = s.nextLine();
		System.out.println("Enter destination: ");
		destination = s.nextLine();
		System.out.println("Enter distance: ");
		distance = s.nextInt();
		System.out.println("Enter price: ");
		price = s.nextDouble();

		//Create new edges for the respective graphs
		edge = new DirectedEdge(vertexArray.indexOf(source), vertexArray.indexOf(destination), distance);
		distanceGraph.addEdge(edge);
		//Add the route to the printArray list as a string. printArray's purpose is defined elsewhere in the program
		printArray.add(vertexArray.indexOf(source) + " " + vertexArray.indexOf(destination) + " " + distance + " " + price);

		edge = new DirectedEdge(vertexArray.indexOf(destination), vertexArray.indexOf(source), distance);
		distanceGraph.addEdge(edge);
		
		edge = new DirectedEdge(vertexArray.indexOf(source), vertexArray.indexOf(destination), price);
		priceGraph.addEdge(edge);
		edge = new DirectedEdge(vertexArray.indexOf(destination), vertexArray.indexOf(source), price);
		priceGraph.addEdge(edge);

		//Print statements are for testing purposes
		// System.out.println("Distance: \n" + printRoutes(distanceGraph));
		// System.out.println("Price: \n" + printRoutes(priceGraph));
	}

	//Method that will remove an edge from the adjacency list
	//The way it works is that it goes through the text file and will rewrite the text file without the specified vertices that need to be removed
	//Uses an ArrayList and adds a string from the file we are reading each time it does not contain the vertices that the user wants to remove
	public static void removeRoute(int src, int dest) {
		try {
			//Create a new BufferedReader to read the file that contains the graph information
			BufferedReader buffRead = new BufferedReader(new FileReader(file));
			//Store the source and destination vertices that should be deleted into an int array
			int lineToRemove [] = new int [2];
			lineToRemove[0] = src;
			lineToRemove[1] = dest;
			String currentLine;
			//This ArrayList will hold all of the strings that I want to write to the file after I read it
			ArrayList<String> fileToWriteContents = new ArrayList<String>();

			//Add the number of vertices in the graph
			fileToWriteContents.add(buffRead.readLine());
			//This for loop will skip past the names of all the vertices
			for (int i = 0; i < vertexArray.size() - 1; i++)
				fileToWriteContents.add(buffRead.readLine());	//Add each vertices name to the ArrayList

			//Reads the file and splits the line in order to get the vertices in each line
			while ((currentLine = buffRead.readLine()) != null) {
				String splitLine [] = currentLine.split(" ");
				//For some lines there only exists one string and no spaces. Therefore index 1 will be null. That is the reason for this try/catch
					//Checks if the vertices in the line are the same as the ones we want to delete
				if ( (lineToRemove[0] == Integer.parseInt(splitLine[0])) && (lineToRemove[1] == Integer.parseInt(splitLine[1])) ) {
					printArray.remove(currentLine);	//Remove the current route from the print ArrayList
					continue;
				} 
					
					//Remove the back edge from the file
				if ( (lineToRemove[0] == Integer.parseInt(splitLine[1])) && (lineToRemove[1] == Integer.parseInt(splitLine[0])) ) {
					printArray.remove(currentLine);	//Remove the current route from the print ArrayList
					continue;
				}
					
					
				//Else write the current line to the file plus the new line character
				fileToWriteContents.add(currentLine);
			}

			//Create a new PrintWriter to write to the same file that I just read from
			PrintWriter printWrite = new PrintWriter(new FileWriter(file));
			//Add each string in the ArrayList to the the file
			for (int i = 0; i < fileToWriteContents.size(); i++)
				printWrite.println(fileToWriteContents.get(i));
			//Close the file
			printWrite.close();

			//Read the new file and put each route into the price and distance graphs
			readFile(fileScan);
		}
		catch (Exception e) {
			// System.out.println("Exception " + e);
		}

	}
	public static void shortestHops(EdgeWeightedDigraph G, Scanner s) {
		s.nextLine();
		//Get info from the user
		String source, destination;
		System.out.println("Enter the source: ");
		source = s.nextLine();
		System.out.println("Enter the destination: ");
		destination = s.nextLine();

		BreadthFirstDirectedPaths bfs = new BreadthFirstDirectedPaths(G, vertexArray.indexOf(source));
		StringBuilder st = new StringBuilder();
		int count = 0;

		if (bfs.hasPathTo(vertexArray.indexOf(destination))) {
			for (int e : bfs.pathTo(vertexArray.indexOf(destination)) ) {
				st.append(" " + vertexArray.get(e));
				count++;
			}
		}
		System.out.println("FEWEST HOPS from " + source + " to " + destination);
		System.out.println("---------------------------------------");
		System.out.println("Fewest hops from " + source + " to " + destination + " is " + (count - 1));
		System.out.println("Path: ");
		System.out.println(st.toString());
		
	}

	public static void displayTrips (Scanner s) {
		s.nextLine();	//Get rid of newline character
		double price, pathPrice;
		System.out.println("Enter price: ");
		price = s.nextDouble();

		System.out.println("ALL PATHS OF COST " + price + " OR LESS");
		System.out.println("Note that paths are duplicated, once from each end city's point of view");
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("List of paths at most " + price + " in length:");
		//Create two for loops
		//Create new Dijkstra Objects with all possible vertices as a source
		//Then calculate the dist to each other vertice from that source
		for (int i = 1; i < priceGraph.V(); i++) {
			DijkstraSP tempDijk = new DijkstraSP(priceGraph, i);
			for (int j = 1; j < priceGraph.V(); j++) {
				pathPrice = tempDijk.distTo(j);
				//If the path is from the source to itself
				if (pathPrice == 0)
					continue;
				//Else if there is a path between the two vertices an disTo returns a price less than the user amount
				if ( (tempDijk.hasPathTo(j)) && (pathPrice <= price) ) {
					System.out.print("Cost: " + tempDijk.distTo(j) + " Path: ");
					for (DirectedEdge e : tempDijk.pathTo(j))
						System.out.println(vertexArray.get(e.from()) + " " + e.weight() + " " + vertexArray.get(e.to()));
					System.out.println();
				}
			}
		}
	}

	public static void menu(Scanner s) {
		int input = 0;
		do {
			System.out.println("Here are your options: ");
			System.out.println("\t1 - Print all routes");
			System.out.println("\t2 - Display MST");
			System.out.println("\t3 - Display shortest path based on distance");
			System.out.println("\t4 - Display shortest path based on price");
			System.out.println("\t5 - Display shortest path based on hops");
			System.out.println("\t6 - Display trips less than entered amount");
			System.out.println("\t7 - Add a new route");
			System.out.println("\t8 - Remove a route");
			System.out.println("\t0 - Exit");

			input = s.nextInt();

				//Print routes
			if (input == 1) 
				System.out.println("Routes: " + printRoutes(distanceGraph));
			//Display MST
			else if (input == 2) {
				PrimMST distancePrims = new PrimMST(distanceGraph);
				System.out.println("MINIMUM SPANNING TREE");
				System.out.println("---------------------");
				for (DirectedEdge e : distancePrims.edges())
					System.out.println(vertexArray.get(e.to()) + ", " + vertexArray.get(e.from()) + " : " + (int) e.weight());
			}
			//Shortest dist path
			else if (input == 3) 
				shortestPath(distanceGraph, s);
			//Shortest price path
			else if (input == 4) 
				shortestPath(priceGraph, s);
			//Shortest hops
			else if (input == 5) 
				shortestHops(distanceGraph, s);	//Doesn't matter what graph I use
			//Trips < entered amount
			else if (input == 6) {
				displayTrips(s);
			}
			//Add route
			else if (input == 7) 
				addRoute(s);
			//Remove
			else if (input == 8) {
				s.nextLine();
		//Get info from the user
				String source, destination;
				System.out.println("Enter source: ");
				source = s.nextLine();
				System.out.println("Enter destination: ");
				destination = s.nextLine();
				removeRoute(vertexArray.indexOf(source), vertexArray.indexOf(destination));
			}
		}while (input != 0);
	}
}