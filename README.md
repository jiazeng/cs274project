# cs274project

This the Delaunay Triangulation Project for CS274 at UC Berkeley.

##Instructions is provided by Prof.Jonathan R. Shewchuk:
Implement either two divide-and-conquer algorithms or two incremental insertion algorithms for constructing two-dimensional Delaunay triangulations, described by Leonidas J. Guibas and Jorge Stolfi, Primitives for the Manipulation of General Subdivisions and the Computation of Voronoi Diagrams, ACM Transactions on Graphics 4(2):74–123, April 1985. Feel free to skip Section 3, but read the rest of the paper. See also this list of errors in the Guibas and Stolfi paper, and Paul Heckbert, Very Brief Note on Point Location in Triangulations, December 1994. (The problem Paul points out can't happen in a Delaunay triangulation, but it's a warning in case you're ever tempted to use the Guibas and Stolfi walking-search subroutine in a non-Delaunay triangulation.)

Your implementations must use Guibas and Stolfi's quad-edge data structure (with appropriate simplifications, if you wish). You are responsible for reading and understanding the Guibas and Stolfi paper, except Section 3, which may be safely skipped. It's a long paper, so start early.

The purpose of this project is to burn an understanding of Delaunay triangulations and planar subdivision data structures into your brain.

Which of the two options should you choose? Well, the divide-and-conquer algorithm is faster, and will satisfy you better if you're a speed junkie. The incremental algorithm is more flexible because it's incremental, and therefore can be used in an on-line manner by mesh generators and other algorithms that choose vertices based on the state of the triangulation. I think the difficulty of the two implementations is equal. (Guibas and Stolfi's divide-and-conquer pseudocode is substantially more complicated than their incremental pseudocode, but the extra stuff you must devise yourself is more complicated for the incremental algorithm than for the divide-and-conquer algorithm.)

##Option 1: Divide-and-conquer algorithm
Implement the divide-and-conquer algorithm for which Guibas and Stolfi give pseudocode.
Next, implement a second version of the divide-and-conquer algorithm whose recursion alternates between using horizontal cuts (at even depths of the recursion tree) and vertical cuts (at odd depths) to divide the points into two subsets. In other words, you bisect the entire set of points by x-coordinate, then bisect each half-set by y-coordinate, then bisect each quarter-set by x-coordinate, and so on, alternating between directions. (This alternation is motivated by a paper by Rex Dwyer.) The original algorithm of Lee and Schachter (discussed by Guibas and Stolfi) uses vertical cuts only.

To bisect the sets quickly, I suggest using the standard O(n)-time quickselect median-finding/partitioning algorithm. It's not a good idea to fully sort the points at every level of the recursion, because if you do, you'll have a Θ(n log2 n) Delaunay triangulation algorithm, which defeats the speed advantage of alternating cuts.

You should find that you can use the same code to merge hulls regardless of whether the cuts are vertical or horizontal, but before/after you merge, you'll need to readjust the positions of the convex hull “handles” called ldi, rdi, ldo, and rdo in the Guibas–Stolfi paper.

##Language
If you write in any language other than C, C++, or Java, you are required to give me very complete instructions on how to compile and run your code. You may also be required to help me get access to a compiler, or to obtain for me an account with which I can run your code. If your submission does not run under Unix, I may need even more help. (On my desk next to my Linux machine, I do have a Windows machine, but I don't know a thing about how to compile code with it. I don't even think it has a C compiler installed.)

##Interface
Your program should use the same file formats as the program Triangle. Specifically, it should read a file with the suffix .node, and write a file with the suffix .ele.
An advantage of using these file formats is that I provide test data.

Here are the timing files (GNUzip-compressed): ttimeu10000.node.gz, ttimeu100000.node.gz, and ttimeu1000000.node.gz.
Here are some smaller test files for your pleasure (not compressed): tri.node, 4.node, box.node, spiral.node, flag.node, grid.node, dots.node, ladder.node, and 633.node.
Another advantage is that you (and I) can use my Show Me visualization program (included in the Triangle distribution) to view and print your input and output. You can also use Triangle to check if you're producing correct triangulations. (Note that Triangle uses a triangle-based data structure instead of quad-edges, and its code will probably not be as helpful to you as Guibas and Stolfi's pseudocode in producing your quad-edge-based implementation.)
You should provide command-line switches, an input prompt, or some other easy way to choose among the options. For the divide-and-conquer algorithm, these options are alternating cuts versus vertical cuts only. For the incrmental algorithm, the options should include slow “walking point location” versus fast point location; and randomized insertion versus non-randomized insertion. Randomized insertion should select a permutation uniformly from all possible permutations. Non-randomized insertion should insert vertices in the order in which they appear in the input.

##Geometric predicates
Insofar as possible, your algorithms should make all their arithmetic decisions based on the results of InCircle and CCW (aka Orient2D) predicates. Rather than writing your own, I suggest you download and blindly use my robust predicates for floating-point inputs.


##Borrowed code
You are welcome to use publicly available libraries or implementations of the following, so long as none of them was produced by any of your classmates: sorting, selection (aka median finding or partitioning), trees, other fundamental non-geometric data structures, command-line switch processing, file reading/writing, and geometric primitives like the InCircle and CCW predicates. You must write the quad-edge implementation and geometric algorithms all by yourself.

