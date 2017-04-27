/**
 * Created by jiazeng on 4/26/17.
 */
public class Subdivision {
    int qeId = 0; //update each time when add edge

    public Edge addEdge(Vertex org, Vertex dest) {
        QuadEdge qe = new QuadEdge(org, dest, qeId);
        qeId++;
        return qe.edges[0];
    }

    public void splice(Edge e1, Edge e2) {
        Edge temp = e1.next;
        e1.next = e2.next;
        e2.next = temp;
        Edge a = e1.onext().rot();
        Edge b = e2.onext().rot();
        temp = a.next;
        a.next = b.next;
        b.next = temp;
    }

    public Edge connect(Edge e1, Edge e2) {
        Edge e = addEdge(e1.dest, e2.org);
        splice(e, e1.lnext());
        splice(e.sym(), e2);
        return e;
    }

    public void deleteEdge(Edge e) {
        splice(e, e.oprev());
        splice(e.sym(), e.sym().oprev());
    }
}