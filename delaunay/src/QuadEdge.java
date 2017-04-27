
//quadedge will contain the edge memories themselves, not just pointer
public class QuadEdge {
    public Edge[] edges;

    public QuadEdge(Vertex org, Vertex dest, int id) {
        edges= new Edge[4];
        for(int i = 0; i < 4; i++) {
            edges[i] = new Edge(this, i);
        }
        edges[0].org = org; // just linking all the edges correctly
        edges[0].dest = dest;
        edges[2].org = dest;
        edges[2].dest = org;
        // set next pointers
        edges[0].next = edges[0];
        edges[1].next = edges[3];
        edges[2].next = edges[2];
        edges[3].next = edges[1];
    }
}
