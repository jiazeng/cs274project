import java.io.*;
import java.util.*;

public class Delaunay {

        Comparator<Vertex> compareXFirst = new Comparator<Vertex>() {
            @Override
            public int compare(Vertex v1, Vertex v2) {
                if(v1.x < v2.x || v1.x == v2.x && v1.y > v2.y) {
                    return 1;
                } else {
                    return -1;
                }
            }
        };

        Comparator<Vertex> compareYFirst = new Comparator<Vertex>() {
            @Override
            public int compare(Vertex v1, Vertex v2) {
                if(v1.y > v2.y || (v1.y == v2.y && v1.x < v2.x)) {
                    return 1;
                } else {
                    return -1;
                }
            }
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

        public double inCircle(Vertex pa, Vertex pb, Vertex pc, Vertex pd) {
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

        public double leftOf(Vertex x, Edge e) {
            return orient2dexact(x, e.org, e.dest);
        }

        public double rightOf(Vertex x, Edge e) {
            return orient2dexact(x, e.dest, e.org);
        }

        public Edge[] delaunay(Subdivision s, int init, int finish, boolean xway, boolean alt, ArrayList<Vertex> vertices) {
            Edge[] edgePair = new Edge[2];

            int size = finish - init;
            if (size < 2) {
                return null;
            }
            Vertex[] tempArr; //copy arraylist to array for sorting
            tempArr = vertices.toArray(new Vertex[vertices.size()]);
            if (size == 2) {
                if (xway) {
                    Arrays.sort(tempArr, init, finish, compareXFirst);
                } else {
                    Arrays.sort(tempArr, init, finish, compareYFirst);
                }
                vertices = new ArrayList<Vertex>(Arrays.asList(tempArr));

                Vertex v1;
                Vertex v2;
                v1 = vertices.get(init);
                v2 = vertices.get(init + 1);
                Edge e = s.addEdge(v1, v2);

                edgePair[0] = e;
                edgePair[1] = e.sym();
                return edgePair;
            }
            if (size == 3) {
                if (xway) {
                    Arrays.sort(tempArr, init, finish, compareXFirst);
                } else {
                    Arrays.sort(tempArr, init, finish, compareYFirst);
                }
                vertices = new ArrayList<Vertex>(Arrays.asList(tempArr));
                Vertex v1;
                Vertex v2;
                Vertex v3;
                v1 = vertices.get(init);
                v2 = vertices.get(init + 1);
                v3 = vertices.get(init + 2);

                Edge e1 = s.addEdge(v1, v2);
                Edge e2 = s.addEdge(v2, v3);
                s.splice(e1.sym(), e2);
                double pre = orient2dexact(v1, v2, v3);
                if (pre > 0) {
                    s.connect(e2, e1);
                    edgePair[0] = e1;
                    edgePair[1] = e2.sym();
                } else if (pre < 0) {
                    Edge temp = s.connect(e2, e1);
                    edgePair[0] = temp.sym();
                    edgePair[1] = temp;
                } else { //<0
                    edgePair[0] = e1;
                    edgePair[1] = e2.sym();
                }
                return edgePair;
            }
            if(size > 3) {
                if(xway) {
                    Arrays.sort(tempArr, init, finish, compareXFirst);
                } else {
                    Arrays.sort(tempArr, init, finish, compareXFirst);
                }
            }

            Edge[] left;
            Edge[] right;
            if (alt) {
                left = delaunay(s, init, (init + finish) / 2, !xway, alt, vertices);
                right = delaunay(s, (init + finish) / 2, finish, !xway, alt, vertices);
            } else {
                left = delaunay(s, init, (init + finish) / 2, xway, alt, vertices);
                right = delaunay(s, (init + finish) / 2, finish, xway, alt, vertices);
            }
            Edge ldo = left[0];
            Edge ldi = left[1];
            Edge rdi = right[0];
            Edge rdo = right[1];

            if (alt) {
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
                    while (compareYFirst.compare(rdi.rnext().org, rdi.org) > 0) {
                        rdi = rdi.rnext();
                    }
                    while (compareYFirst.compare(rdo.org, rdo.lnext().org) > 0) {
                        rdo = rdo.lnext();
                    }
                }
            }

            while (true) {
                if (leftOf(rdi.org, ldi) > 0) {
                    ldi = ldi.lnext();
                } else if (rightOf(ldi.org, rdi) > 0) {
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
                if (rightOf(lcand.dest, base) > 0) {
                    while (inCircle(base.dest, base.org, lcand.dest, lcand.onext().dest) > 0) {
                        t = lcand.onext();
                        s.deleteEdge(lcand);
                        lcand = t;
                    }
                }

                rcand = base.oprev();

                if (rightOf(rcand.dest, base) > 0) {
                    while (inCircle(base.dest, base.org, rcand.dest, rcand.oprev().dest) > 0) {
                        t = rcand.oprev();
                        s.deleteEdge(rcand);
                        rcand = t;
                    }
                }

                lvalid = rightOf(lcand.dest, base);
                rvalid = rightOf(rcand.dest, base);

                if (lvalid <= 0 && rvalid <= 0) {
                    //finish merge
                    break;
                }
                // choose which edge to connect
                if (lvalid <= 0 || (rvalid > 0 && inCircle(lcand.dest, lcand.org, rcand.org, rcand.dest) > 0)) {
                    base = s.connect(rcand, base.sym());
                } else {
                    base = s.connect(base.sym(), lcand.sym());
                }
            }
            edgePair[0] = ldo;
            edgePair[1] = rdo;
            return edgePair;
        }

        //    outputTriangulation(s2, d, nverts, p.le, outname);
        // returns the three sides of a triangle with a given edge
        public int[] triangle(Edge start) {
            int numEdges = 1;
            int[] ids = new int[3];
            Edge curr = start;
            ids[0] = start.org.id;
            while (curr.lnext() != start) {
                curr = curr.lnext();
                numEdges++;
                if (numEdges > 3) {
                    int[] triang = {-1, -1, -1};
                    return triang;
                }
                ids[numEdges - 1] = curr.org.id;
            }
            Arrays.sort(ids);
            return ids;
        }

        // Returns a pair of points with a given edge
        public int[] toPoints(Edge e) {
            int[] points = new int[2];
            points[0] = e.org.id;
            points[1] = e.dest.id;
            return points;
        }

        public void triangulate (int numVertices, Edge start, String outputFile) throws IOException {
            Set<int[]> triangles = new HashSet<>();
            Set<int[]> visited = new HashSet<>();
            Queue<Edge> toCheck = new LinkedList<>();
            toCheck.add(start);
            visited.add(toPoints(start) );
            Edge curr;
            while (!toCheck.isEmpty()) {
                curr = toCheck.peek();
                toCheck.poll();
                triangles.add(triangle(curr));
                int[] twoPt1 = toPoints(curr.onext());
                int[] twoPt2 = toPoints(curr.sym());
                if(visited.contains(twoPt1)) {
                    toCheck.add(curr.onext());
                    visited.add(twoPt1);
                }
                if(visited.contains((twoPt2))) {
                    toCheck.add(curr.sym());
                    visited.add(twoPt2);
                }
            }

            int numTriangles = triangles.size();
            int[] temp = {-1, -1, -1};
            if (!triangles.contains(temp)) {
                numTriangles--;
            }

            int count = 1;
            curr = start;
            while (count < 4 && curr.rprev() != start) {
                curr = curr.rprev();
                count++;
            }
            if (count == 3 && numVertices > 3) {
                int[] ids = new int[3];
                ids[0] = curr.org.id;
                ids[1] = curr.rprev().org.id;
                ids[2] = curr.rprev().rprev().org.id;
                Arrays.sort(ids);
                triangles.remove(ids);
                numTriangles--;
            }

            // Write the file
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("./ele/" + outputFile))) {
                bw.write(numTriangles + " 3 0\n");
                int i = 0;
                for(int[] t: triangles) {
                    if(t[0] == -1) {
                        continue;
                    }
                    bw.write(i + " " + t[0] + " " + t[1] + " " + t[2] + "\n");
                    i++;
                }
                System.out.println("Finished writing to file " + outputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

}



