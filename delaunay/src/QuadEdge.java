import java.util.*;
import java.io.*;
import java.util.Comparator;


class Vertex {
    double x;
    double y;
    int id;
}

//quadedge will contain the edge memories themselves, not just pointer
public class QuadEdge {
    public Edge[] edges;

    public QuadEdge(Vertex org, Vertex dest, int id) {
        Edge[] edges= new Edge[4];
        for(int i = 0; i <4; i++) {
            edges[i] = new Edge(this, i);
        }
        // use edge constructor !!!!!!!!
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


class Edge {
    public QuadEdge owner;
    public int rotnum; //or private?

    public Edge(QuadEdge own, int r) {
        owner = own;
        rotnum = r;
    }

    public Edge rot(){
        return owner.edges[(rotnum +1) % 4];
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

class Subdivision {
    int qeId = 0; //update each time when add edge

    public Edge addEdge(Vertex org, Vertex dest) {
        //QuadEdge qe = new QuadEdge(org, dest, next_id++);
        QuadEdge qe = new QuadEdge(org, dest, qeId);
        qeId++;

        //records.insert( {er->edge_id, er} );
        //return &er->edges[0];
       // return edges[0];
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
		/*
		//if(e.next == e && e.sym().next == e.sym() && e.rot().next == e.rotinv()
			&& e.rotinv().next == e.rot()) {
			//removable++;
			QuadEdge tmp = e.owner;
			// maybe remove?
			//delete tmp;

		}*/
    }
}


//----------------------------------------------------
class Delaunay{
    Comparator<Vertex> compareXFirst = new Comparator<Vertex>() {
        @Override
        public int compare(Vertex v1, Vertex v2) {
            if(v1.x > v2.x) {
                return 1; //v1>v2
            } else if (v1.x == v2.x) {
                if(v1.y > v2.y) {
                    return 1;
                } else if(v1.y == v2.y) { //v1=v2
                    return 0;
                } else { //v1<v2
                    return -1;
                }
            } else { //v1.x < v2.x
                return -1;
            }
        }
    };

    Comparator<Vertex> compareYFirst = new Comparator<Vertex>() {
        @Override
        public int compare(Vertex v1, Vertex v2) {
            if(v1.y > v2.y) {
                return 1; //v1>v2
            } else if (v1.y == v2.y) {
                if(v1.x > v2.x) {
                    return 1;
                } else if(v1.x == v2.x) { //v1=v2
                    return 0;
                } else { //v1<v2
                    return -1;
                }
            } else { //v1.y < v2.y
                return -1;
            }        }
    };


    public double orient2dexact(Vertex pa, Vertex pb, Vertex pc) {
        double acx, bcx, acy, bcy;
        //acx = pa[0], pc[0]
        acx = pa.x - pc.x;
        bcx = pb.x - pc.x;
        //acy = pa[1] - pc[1];
        acy = pa.y - pc.y;
        bcy = pb.y - pc.y;
        return acx * bcy - acy * bcx;

    }

    public double incircleexact(Vertex pa, Vertex pb, Vertex pc, Vertex pd) {
        double adx, ady, bdx, bdy, cdx, cdy;
        double abdet, bcdet, cadet;
        double alift, blift, clift;

        adx = pa.x - pd.x;
        ady = pa.y - pd.y;
        bdx = pb.x - pd.y;
        bdy = pb.y - pd.y;
        cdx = pc.x - pd.x;
        cdy = pc.y - pd.y;

        abdet = adx * bdy - bdx * ady;
        bcdet = bdx * cdy - cdx * bdy;
        cadet = cdx * ady - adx * cdy;
        alift = adx * adx + ady * ady;
        blift = bdx * bdx + bdy * bdy;
        clift = cdx * cdx + cdy * cdy;

        return alift * bcdet + blift * cadet + clift * abdet;
    }

    public double leftof(Vertex x, Edge e) {
        return orient2dexact(x, e.org, e.dest);
    }

    public double rightof(Vertex x, Edge e) {
        return orient2dexact(x, e.dest, e.org);
    }


    public Edge[] delaunay(Subdivision s, int init, int finish, boolean xway, boolean alt, Vertex[] vertices) {
        Edge[] edgePair = new Edge[2];
        Edge[] left = new Edge[2];
        Edge[] right = new Edge[2];

        int size = finish - init;
        if (size < 2) {
            return null;
        }
        if (size == 2) {
            if (xway) {
                Arrays.sort(vertices, init, finish, compareXFirst);
            } else {
                Arrays.sort(vertices, init, finish, compareYFirst);
            }
            Vertex v1;
            Vertex v2;
//            v1 = vertices.elementAt(init);
//            v2 = vertices.elementAt(init + 1);
            v1 = vertices[init];
            v2 = vertices[init + 1];

            Edge e = s.addEdge(v1, v2);
            edgePair[0] = e;
            edgePair[1] = e.sym();
            return edgePair;
        }
        if (size == 3) {
            if (xway) {
                Arrays.sort(vertices, init, finish, compareXFirst);
            } else {
                Arrays.sort(vertices, init, finish, compareYFirst);
            }
            Vertex v1;
            Vertex v2;
            Vertex v3;
//            v1 = vertices.elementAt(init);
//            v2 = vertices.elementAt(init + 1);
//            v3 = vertices.elementAt(init + 2);
            v1 = vertices[init];
            v2 = vertices[init + 1];
            v3 = vertices[init + 2];


            Edge e1 = s.addEdge(v1, v2);
            Edge e2 = s.addEdge(v2, v3);
            s.splice(e1.sym(), e2);
            double pred = orient2dexact(v1, v2, v3);
            if (pred > 0) {
                s.connect(e2, e1);
                edgePair[0] = e1;
                edgePair[1] = e2.sym();
            } else if (pred < 0) {
                Edge c = s.connect(e2, e1);
                edgePair[0] = c.sym();
                edgePair[1] = c;
            } else { //<0
                edgePair[0] = e1;
                edgePair[1] = e2.sym();
            }
            return edgePair;
        }


//        // size >= 4. divide and conquer
//        // triangulate the two halves
//        if (vertical) {
//            std::nth_element(
//                    vertices + start,
//                    vertices + ((start + end) / 2),
//                    vertices + end,
//                    x_first
//        );
//        } else {
//            std::nth_element(
//                    vertices + start,
//                    vertices + ((start + end) / 2),
//                    vertices + end,
//                    y_first
//        );


        // EdgePair left;
        // EdgePair right;

        if (alt) {
            left = delaunay(s, init, (init + finish) / 2, !xway, alt, vertices);
            right = delaunay(s, (init + finish) / 2, init, !xway, alt, vertices);
        } else {
            left = delaunay(s, init, (init + finish) / 2, xway, alt, vertices);
            right = delaunay(s, (init + finish) / 2, init, xway, alt, vertices);
        }

        Edge ldo = left[0];
        Edge ldi = left[1];
        Edge rdi = right[0];
        Edge rdo = right[1];


        if (alt) {
            // If vertical is True, then we need leftmost + rightmost of each side as before
            // If vertical is False, then we need topmost + bottommost
            // In either case, the recursive step outputs the opposite.

//            bool (*comparator)(Vertex, Vertex) = vertical ? x_first : y_first;


            if (xway) {
                while (compareXFirst.compare(ldo.rprev().org, ldo.org) > 0) {
                    ldo = ldo.rprev();
                }
                while (compareXFirst.compare(ldi.org, ldi.lprev().org) > 0) {
                    ldi = ldi.lprev();
                }

                while (compareXFirst.compare(rdi.rprev().org, rdi.org) > 0) {
                    rdi = rdi.rprev();
                }

                while (compareXFirst.compare(rdo.org, rdo.lprev().org) > 0) {
                    rdo = rdo.lprev();
                }
            } else {
                while (compareYFirst.compare(ldo.rnext().org, ldo.org) > 0) {
                    ldo = ldo.rnext();
                }
                while (compareYFirst.compare(ldi.org, ldi.lnext().org) > 0) {
                    ldi = ldi.lnext();
                }
                while (compareYFirst.compare(rdo.org, rdo.lnext().org) > 0) {
                    rdo = rdo.lnext();
                }
                while (compareYFirst.compare(rdi.rnext().org, rdi.org) >0) {
                    rdi = rdi.rnext();
                }

            }

        }

        while (true) {
            if (leftof(rdi.org, ldi) > 0) {
                ldi = ldi.lnext();
            } else if (rightof(ldi.org, rdi) > 0) {
                rdi = rdi.rprev();

            } else {
                break;
            }
        }

        Edge base = s.connect(rdi.sym(), ldi);
        if (ldi.org == ldo.org) {
            ldo = base.sym();
        }
        if (rdi.org == rdo.org) {
            rdo = base;
        }

        //merge loop
        Edge lcand;
        Edge rcand;
        Edge t;
        double lvalid;
        double rvalid;

        while (true) {
            lcand = base.sym().onext();
            if (rightof(lcand.dest, base) > 0) {
                while (incircleexact(base.dest, base.org, lcand.dest, lcand.onext().dest) > 0) {
                    t = lcand.onext();
                    s.deleteEdge(lcand);
                    lcand = t;
                }
            }

            rcand = base.oprev();

            if (rightof(rcand.dest, base) > 0) {
                //cout << "Entering rcand loop" << endl;
                while (incircleexact(base.dest, base.org, rcand.dest, rcand.oprev().dest) > 0) {
                    t = rcand.oprev();
                    s.deleteEdge(rcand);
                    rcand = t;
                }
            }

            lvalid = rightof(lcand.dest, base);
            rvalid = rightof(rcand.dest, base);

            if (lvalid <= 0 && rvalid <= 0) {
                //finish merge
                break;
            }
            // choose which edge to connect
            if (lvalid <= 0 || (rvalid > 0 && incircleexact(lcand.dest, lcand.org, rcand.org, rcand.dest) > 0)) {
                // Adding rcand
                base = s.connect(rcand, base.sym());
            } else {
                // Adding lcand
                base = s.connect(base.sym(), lcand.sym());
            }
            edgePair[0] = ldo;
            edgePair[1] = rdo;
//            return edgePair;
        }
        return edgePair;
    }

    public static void main(String[] args) {
        //input: node file
        Scanner sc = null;
        try {
//            sc = new Scanner(new File("/Users/jiazeng/Desktop/delaunay/4.node"));
            sc = new Scanner(new File("4.node"));
        } catch (FileNotFoundException e) {
            System.out.println("File not found exception");
            System.exit(1);
        }
        String next = "";

        while(sc.hasNextLine()) {
            next = sc.nextLine();
        }
        System.out.println(next);
        //output:
        //number of point,
    }
}
// 4 2 0 0
// 1 -10 4
// 2 10 -4
// 3 0 10
// 4 0 7.5


    // tuple<int, int, int> triang(Edge* start) {
    //     int nedges = 1;
    //     int ids[3];
    //     Edge* curr = start;
    //     ids[0] = start->org.id;
    //     while (curr->lnext() != start) {
    //         // we need to make sure points are visited in ccw order
    //         // Consider when the convex hull has 3 edges. If we
    //         // did not do this, then the triangle for the convex
    //         // hull will be outputted when it shouldn't be
    //         curr = curr->lnext();
    //         nedges++;
    //         if (nedges > 3) {
    //             return make_tuple (-1, -1, -1);
    //         }
    //         ids[nedges-1] = curr->org.id;
    //     }
    //     sort(begin(ids), end(ids));
    //     return make_tuple (ids[0], ids[1], ids[2]);
    // }

    // tuple<int, int> toTuple(Edge* e) {
    //     return make_tuple(e->org.id, e->dest.id);
    // }


    //custom comparator class
    //Collections.sort(Vector verticies, new Comparator<Vertex>(){

//TODO:
/*
subdivision.addEdge
delete e.owner (find out java way)
implement comparator
add Edge[2] as an output for Edge pair
*/






