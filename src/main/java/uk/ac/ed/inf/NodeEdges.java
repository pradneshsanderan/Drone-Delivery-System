package uk.ac.ed.inf;

public class NodeEdges {
    public LongLat node1;
    public LongLat node2;

    public NodeEdges(LongLat node1,LongLat node2){
        this.node1 = node1;
        this.node2 = node2;
    }
    public boolean identicalNodeEdge(NodeEdges nodeEdge){
        if(this == nodeEdge){
            return true;
        }
        if(nodeEdge == null){
            return false;
        }
        return nodeEdge.node1.equals(node1) && nodeEdge.node2.equals(node2) || nodeEdge.node1.equals(node2)||nodeEdge.node2.equals(node1);
    }
    public LongLat getTheOtherNode(LongLat node){
        if(node.equals(node1)){
            return node2;
        }
        return node1;
    }
}
