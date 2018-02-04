/**
 * This program stores the amazon data into a graph using a HashMap to keep the original IDs in tact
 * @author Brianne Coffey
 * @version 12-10-2017
 */

import java.util.Collections;
import java.util.*;
import java.io.*;
import java.util.regex.Pattern;

@SuppressWarnings("unchecked")

public class Graph
{
    String fileName="amazon-elec-data.txt";
    HashMap<String, ArrayList<String>> graph;
    ArrayList<String> allAsins;
    ArrayList<byte[]> allText;
    HashMap<String, ArrayList<byte[]>> recordAsin;
    HashMap<String, ArrayList<byte[]>> recordReviewer;
    int connectedComponents;
    int reviewCount;


    public Graph() {

        graph = new HashMap<String, ArrayList<String>>();
        allAsins = new ArrayList<>();
        allText = new ArrayList<byte[]>();
        recordAsin = new HashMap<String, ArrayList<byte[]>>();
        recordReviewer = new HashMap<String, ArrayList<byte[]>>();
    }


    /**
     * This class reads in the graph, as well as returns the review and unique reviewers/asin counts.
     */
    public void readGraph() {

        Scanner fileScanner;

        try
        {

            fileScanner = new Scanner (new File (fileName));
            fileScanner.useDelimiter(Pattern.compile("[2][0][0-1][1-9]}"));
            while (fileScanner.hasNext()) {
                reviewCount++; //count for total number of reviews
                String word = fileScanner.next();
                String[] edge = word.split(":,; ");
                String reviewerID = edge[0].trim();
                String asin = edge[1].trim();

                byte[] byteArr = edge[4].getBytes();

                //Adds all asin ids for sorting
                allAsins.add(asin);

                //create adj list for IDs
                ArrayList<String> adj = graph.get(reviewerID);
                if(adj == null)
                    adj = new ArrayList<String>();
                graph.put(reviewerID, adj);
                adj.add(asin);

                //create adj list for asins
                ArrayList<String> adj1 = graph.get(asin);
                if(adj1 == null)
                    adj1 = new ArrayList<String>();
                graph.put(asin, adj1);
                adj1.add(reviewerID);
                //System.out.println(graph.values());

                //create hashmap of reviewerIDs and reviews (in byte arrays)
                ArrayList<byte[]> reviewsR = recordReviewer.get(reviewerID);
                if (reviewsR == null)
                    reviewsR = new ArrayList<byte[]>();
                recordReviewer.put(reviewerID, reviewsR);
                reviewsR.add(byteArr);


                //create hashmap of asins and reviews (in byte arrays)
                ArrayList<byte[]> reviewsA = recordAsin.get(asin);
                if (reviewsA == null)
                    reviewsA = new ArrayList<byte[]>();
                recordAsin.put(asin, reviewsA);
                reviewsA.add(byteArr);

            }

            System.out.println("Number of Reviews: " +  reviewCount);
            Set<String> keys = graph.keySet();
            int uniqueAsins = 0;
            int uniqueIDs = 0;
            for (String s: keys){
                if (s.startsWith("A")){
                    uniqueIDs++;
                }
                else
                    uniqueAsins++;
            }
            System.out.println("Number of Unique Reviewers: " + uniqueIDs);
            System.out.println("Number of Unique Products Reviewed: " + uniqueAsins);

        }
        catch (IOException e)
        {
            System.out.println(e);
        }


    }

    /**
     * This is the utility function for the DFS. I created by own implementation based off this link.
     * http://www.geeksforgeeks.org/connected-components-in-an-undirected-graph/
     * @param s, the node of either reviewer or asin
     * @param visited, the hashmap of booleans to represent lists of visited nodes
     */
    public void DFSUtil(String s, HashMap<String, Boolean> visited)
    {
        visited.put(s, true);
        Iterator<String> it = graph.get(s).listIterator();
        while(it.hasNext()){
            String n = it.next();
            if(!visited.containsKey(n)){
                DFSUtil(n, visited);
            }
        }
    }

    /**
     * the wrapper function for completing depth first search
     * it counts the method calls to generate connected component totals
     * @return total connected components of graph
     */

    public int DFS(){
        HashMap<String, Boolean> visited = new HashMap<>();
        for (String key: graph.keySet()){
            visited.put(key, false);
        }
        for (String node: visited.keySet()) {
            if(visited.get(node) == false)
            {
                DFSUtil(node, visited);
                connectedComponents++;
            }
        }
        return connectedComponents;
    }

    /**
     * This method returns the reviews for product
     * @param asin, the product ID
     */
    public void getTextFromAsin(String asin) {
        for (String id :  graph.get(asin)){
            for (byte[] review1 : recordReviewer.get(id)){
                for(byte[] review2  : recordAsin.get(asin))
                    if (review1==review2){
                        String rev = new String(review1);
                        System.out.println(id + " " + rev);
                    }
            }
        }
    }

    /**
     * This method returns the reviews by a given ID
     * @param reviewerID, the reviewer who wrote the reviews
     */
    public void getTextFromReviewerID(String reviewerID){
        for (String asin :  graph.get(reviewerID)){
            for (byte[] review1 : recordAsin.get(asin)){
                for(byte[] review2  : recordReviewer.get(reviewerID))
                    if (review1==review2){
                        String rev = new String(review1);
                        System.out.println(asin + " " + rev);
                    }
            }
        }
    }

    /**
     * For this method, it sorts the ASIN ids and returns the 1, 2, 3, 4, and 5 hundred-thousandth entries.
     * http://www.javapractices.com/topic/TopicAction.do?Id=207
     * @return
     */
    public String sortAsinsAndReturn(){
        Collections.sort(allAsins, String.CASE_INSENSITIVE_ORDER);
        String records = allAsins.get(100000) + '\n' + allAsins.get(200000) + '\n' + allAsins.get(300000) + '\n' + allAsins.get(400000) + '\n' + allAsins.get(500000) + '\n';
        return records;

    }

/**
 * I used this method to answer the question in number 2.
    public int getMaxDegree(){
        int maxDeg = 0;
        for (ArrayList<String> value: graph.values()){
            if(value.size() > maxDeg){
                maxDeg = value.size();
            }
        }
        return maxDeg;
    }
 */

    public static void main(String[] args) {

        Graph g = new Graph();
        g.readGraph();
        System.out.println("Total Connected Components: " + g.DFS());

        //System.out.println(g.getMaxDegree());
        g.getTextFromAsin("0528881469"); //testing method
        g.getTextFromReviewerID("AO94DHGC771SJ"); //testing method
        System.out.println(g.sortAsinsAndReturn());

    }
}

