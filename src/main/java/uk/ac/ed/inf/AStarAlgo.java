package uk.ac.ed.inf;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.HashMap;
import java.util.List;

public class AStarAlgo {
    public static HashMap<LongLat,Integer> vertices = new HashMap<>();
    public static HashMap<LongLat,LongLat> edges = new HashMap<>();
    public static Graph<LongLat, DefaultEdge> createNewGraph(){
        Graph<LongLat, DefaultEdge> g= new SimpleGraph<>(DefaultEdge.class);
        vertices.put(Drone.appleton,0);
        g.addVertex(Drone.appleton);
        addEdgeAndVertex(g, Drone.appleton);

        return g;
    }
    private static void addEdgeAndVertex(Graph<LongLat, DefaultEdge> g, LongLat currPos){
        for(int i=0;i<35;i++){
            int currAngle = i*10;
            LongLat nextPos =currPos.nextPosition(currAngle);
            if(!vertices.containsKey(nextPos)){
                if(nextPos.isConfined() && !currPos.inNoFlyZone(nextPos)){
                    g.addVertex(nextPos);
                    vertices.put(nextPos,0);
                    if(edges.get(currPos)!= nextPos){
                        g.addEdge(currPos,nextPos);
                        g.addEdge(nextPos,currPos);
                        edges.put(currPos,nextPos);
                        edges.put(nextPos,currPos);
                    }
                    addEdgeAndVertex(g,nextPos);
                }
            }

        }
    }
    public static List<LongLat> getPath(Graph<LongLat,DefaultEdge> g,LongLat start,LongLat end) {
        DijkstraShortestPath d = new DijkstraShortestPath(g);
        List<LongLat> path = d.getPath(start,end).getVertexList();
        return path;
    }
}
