
import java.awt.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution {

    public static void main(String[] args) {

        try {
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(System.in));

            // Process first line, which consists of three, space-separated integers
            List<Integer> params = Arrays.asList(br.readLine().split("\\s")).stream().map(Integer::parseInt).collect(Collectors.toList());
            final int M = params.get(0), N = params.get(1), R = params.get(2);
            Integer[][] matrix = new Integer[M][N];
            for (int row = 0; row < M; row++) {
                //matrix[row] = new Integer[N - 1];
                matrix[row] = Arrays.asList(br.readLine().split("\\s")).stream().map(Integer::parseInt).collect(Collectors.toList()).toArray(new Integer[N - 1]);
            }

            RingMatrix ringMatrix = new RingMatrix(matrix);
            ringMatrix.rotate(R);
            int[][] newMat = ringMatrix.toMatrix();
            for (int r = 0; r < M; r++) {
                for (int c = 0; c < N; c++) {
                    System.out.print(newMat[r][c] + " ");
                }
                System.out.println();
            }

        } catch (IOException io){
            io.printStackTrace();
        }
    }

    static class RingMatrix {

        ArrayList<CircularIntArray> rings;
        int numRows, numCols;

        public RingMatrix(Integer[][] mat) {
            numRows = mat.length;
            numCols = mat[0].length;
            int numRings = Math.min(numRows, numCols) / 2;
            rings = new ArrayList<>(numRings);

            // Must now convert from 2D array representation to ring-based representation
            // Requires iterating over the matrix in counter-clockwise order for each ring
            // Each ring is basically a rectangle, so we can break it up into four sides
            // The first side is moving down (constant col, varying row)
            // The second side is moving right (constant row, varying col)
            // etc.
            // The important part is determining the four points that define the rectangle
            // Those four points should move diagonally after each iteration

            Point NW = new Point(0,0), SW = new Point(numRows - 1, 0),
                    SE = new Point(numRows - 1, numCols - 1), NE = new Point(0, numCols - 1);

            IntStream.range(0, numRings).forEach(
                    i -> {
                        ArrayList<Integer> curRing = new ArrayList<Integer>();
                        // Add the Western edge of the current ring
                        for (int row = NW.x; row < SW.x; row++) {
                            curRing.add(mat[row][NW.y]);
                        }
                        // Add the southern edge of the current ring
                        for (int col = SW.y; col < SE.y; col++) {
                            curRing.add(mat[SW.x][col]);
                        }
                        // Add the eastern edge of the current ring
                        for (int row = SE.x; row > NE.x; row--) {
                            curRing.add(mat[row][SE.y]);
                        }
                        // Add the northern edge of the current ring
                        for (int col = NE.y; col > NW.y; col--) {
                            curRing.add(mat[NE.x][col]);
                        }

                        // Move each point diagonally toward the center
                        NW.x++;
                        NW.y++;

                        SW.x--;
                        SW.y++;

                        SE.x--;
                        SE.y--;

                        NE.x++;
                        NE.y--;

                        rings.add(new CircularIntArray(curRing));
                    }
            );
        }

        public void rotate(int times) {
            rings.stream().forEach(i -> i.rotate(times));
        }

        public int[][] toMatrix() {
            int[][] mat = new int[numRows][numCols];
            Point NW = new Point(0,0), SW = new Point(numRows - 1, 0),
                    SE = new Point(numRows - 1, numCols - 1), NE = new Point(0, numCols - 1);

            IntStream.range(0, rings.size()).forEach(
                    i -> {
                        int[] curRing = rings.get(i).toArray();
                        //System.out.println(Arrays.toString(curRing));
                        int j = 0;
                        // Add the Western edge of the current ring
                        for (int row = NW.x; row < SW.x; row++) {
                            mat[row][NW.y] = curRing[j];
                            j++;
                        }
                        // Add the southern edge of the current ring
                        for (int col = SW.y; col < SE.y; col++) {
                            mat[SW.x][col] = curRing[j];
                            j++;
                        }
                        // Add the eastern edge of the current ring
                        for (int row = SE.x; row > NE.x; row--) {
                            mat[row][SE.y] = curRing[j];
                            j++;
                        }
                        // Add the northern edge of the current ring
                        for (int col = NE.y; col > NW.y; col--) {
                            mat[NE.x][col] = curRing[j];
                            j++;
                        }

                        // Move each point diagonally toward the center
                        NW.x++;
                        NW.y++;

                        SW.x--;
                        SW.y++;

                        SE.x--;
                        SE.y--;

                        NE.x++;
                        NE.y--;
                    }
            );
            return mat;
        }

    }

    static class CircularIntArray {
        ArrayList<Integer> arr;
        int curHead;

        public CircularIntArray(ArrayList<Integer> arr) {
            curHead = 0;
            this.arr = arr;
        }

         public void rotate() {
            curHead = Math.floorMod(curHead - 1,arr.size());
        }

        public void rotate(int times) {
            times = times % arr.size();
            curHead = Math.floorMod(curHead - times,arr.size());
        }


        public int[] toArray() {
            int[] a = new int[arr.size()];
            IntStream.range(0,arr.size()).forEach(
                    n ->  a[n] = arr.get((n + curHead) % arr.size()));
            return a;
        }
    }
}