/**
 * Created by jiazeng on 4/26/17.
 */
public class Edge {
    public QuadEdge owner;
    public int rotnum; //or private?

    public Edge(QuadEdge own, int r) {
        owner = own;
        rotnum = r;
    }

    public Edge rot(){
        return owner.edges[(rotnum + 1) % 4];
    }
    public Edge sym() {
        return owner.edges[(rotnum + 2) % 4];
    }
    public Edge rotinv() {
        return owner.edges[(rotnum + 3) % 4];
    }
    public Edge onext(){
        return next;
    }
    public Edge oprev(){
        return owner.edges[(rotnum + 1) % 4].next.rot();
    }
    public Edge lnext(){
        return this.rotinv().onext().rot();
    };
    public Edge rnext() {
        return this.rot().onext().rotinv();
    }
    public Edge lprev() {
        return this.onext().sym();
    }
    public Edge rprev() {
        return this.sym().onext();
    }
    Vertex org;
    Vertex dest;
    Edge next;

}
