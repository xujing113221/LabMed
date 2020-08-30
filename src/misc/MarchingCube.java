package misc;

import java.util.HashMap;
import javax.media.j3d.IndexedTriangleArray;
import javax.vecmath.Point3f;

public class MarchingCube {

    public static final int CASE_0 = 0b00000000;
    public static final int CASE_1 = 0b00000001;
    public static final int CASE_2 = 0b00000011;
    public static final int CASE_3 = 0b00000101;
    public static final int CASE_4 = 0b01000001;
    public static final int CASE_5 = 0b00110010;
    public static final int CASE_6 = 0b01000011;
    public static final int CASE_7 = 0b01001010;
    public static final int CASE_8 = 0b00110011;
    public static final int CASE_9 = 0b10110001;
    public static final int CASE_10 = 0b01101001;
    public static final int CASE_11 = 0b01110001;
    public static final int CASE_12 = 0b00111010;
    public static final int CASE_13 = 0b10100101;
    public static final int CASE_14 = 0b10110010;
    public static final int[] BaseCase = { 0b00000001, 0b00000011, 0b00000101, 0b01000001, 0b00110010, 0b01000011,
            0b01001010, 0b00110011, 0b10110001, 0b01101001, 0b01110001, 0b00111010, 0b10100101, 0b10110010 };

    public static final float len = 1.0f;
    public static final Point3f[] _v = { new Point3f(len / 2, 0, 0), new Point3f(len, 0, len / 2),
            new Point3f(len / 2, 0, len), new Point3f(0, 0, len / 2), new Point3f(0, len / 2, 0),
            new Point3f(len, len / 2, 0), new Point3f(len, len / 2, len), new Point3f(0, len / 2, len),
            new Point3f(len / 2, len, 0), new Point3f(len, len, len / 2), new Point3f(len / 2, len, len),
            new Point3f(0, len, len / 2) };

    public static final HashMap<Integer, IndexedTriangleArray> _lookupTab = new HashMap<Integer, IndexedTriangleArray>();

    public MarchingCube() {
        super();

        _lookupTab.put(CASE_1, getCase1TriangleArray(_v[0], _v[4], _v[3]));
        _lookupTab.put(CASE_2, getCase2TriangleArray(_v[3], _v[1], _v[4], _v[5]));
        _lookupTab.put(CASE_3, getCase34TriangleArray(_v[0], _v[4], _v[3], _v[1], _v[2], _v[6]));
        _lookupTab.put(CASE_4, getCase34TriangleArray(_v[0], _v[4], _v[3], _v[6], _v[10], _v[9]));
        _lookupTab.put(CASE_5, getCase5TriangleArray(_v[0], _v[11], _v[4], _v[1], _v[9]));
        _lookupTab.put(CASE_6, getCase6TriangleArray(_v[3], _v[1], _v[4], _v[5], _v[6], _v[10], _v[9]));
        _lookupTab.put(CASE_7, getCase7TriangleArray(_v[2], _v[3], _v[7], _v[0], _v[1], _v[5], _v[6], _v[10], _v[9]));
        _lookupTab.put(CASE_8, getCase2TriangleArray(_v[1], _v[9], _v[3], _v[11]));
        _lookupTab.put(CASE_9, getCase9TriangleArray(_v[7], _v[3], _v[10], _v[0], _v[9], _v[5]));
        _lookupTab.put(CASE_10, getCase10TriangleArray(_v[7], _v[2], _v[4], _v[0], _v[10], _v[8], _v[6], _v[5]));
        _lookupTab.put(CASE_11, getCase11TriangleArray(_v[3], _v[0], _v[11], _v[6], _v[10], _v[5]));
        _lookupTab.put(CASE_12, getCase12TriangleArray(_v[0], _v[11], _v[4], _v[1], _v[9], _v[2], _v[3], _v[7]));
        _lookupTab.put(CASE_13, getCase13TriangleArray(_v[0], _v[4], _v[3], _v[7], _v[11], _v[10], _v[1], _v[2], _v[6],
                _v[5], _v[9], _v[8]));
        _lookupTab.put(CASE_14, getCase14TriangleArray(_v[4], _v[0], _v[7], _v[9], _v[1], _v[10]));

        // roll_dice();
    }

    public IndexedTriangleArray getTriArray(int index) {
        IndexedTriangleArray _trias = _lookupTab.get(index);
        int vertexcnt = _trias.getVertexCount();
        int indexcnt = _trias.getIndexCount();

        IndexedTriangleArray itrias = new IndexedTriangleArray(vertexcnt,
                IndexedTriangleArray.COORDINATES | IndexedTriangleArray.NORMALS, indexcnt);

        for (int i = 0; i < vertexcnt; i++) {
            Point3f point = new Point3f();
            _trias.getCoordinate(i, point);
            itrias.setCoordinate(i, point);
        }

        for (int i = 0; i < indexcnt; i++) {
            int newindex = _trias.getCoordinateIndex(i);
            itrias.setCoordinateIndex(i, newindex);

        }
        return itrias;
    }

    public static IndexedTriangleArray getCase1TriangleArray(Point3f a, Point3f b, Point3f c) {
        IndexedTriangleArray itrias = new IndexedTriangleArray(3,
                IndexedTriangleArray.COORDINATES | IndexedTriangleArray.NORMALS, 3);
        itrias.setCoordinate(0, a);
        itrias.setCoordinate(1, b);
        itrias.setCoordinate(2, c);
        itrias.setCoordinateIndex(0, 0);
        itrias.setCoordinateIndex(1, 1);
        itrias.setCoordinateIndex(2, 2);
        return itrias;
    }

    public static IndexedTriangleArray getCase2TriangleArray(Point3f a, Point3f b, Point3f c, Point3f d) {
        IndexedTriangleArray itrias = new IndexedTriangleArray(4,
                IndexedTriangleArray.COORDINATES | IndexedTriangleArray.NORMALS, 6);
        itrias.setCoordinate(0, a);
        itrias.setCoordinate(1, b);
        itrias.setCoordinate(2, c);
        itrias.setCoordinate(3, d);
        itrias.setCoordinateIndex(0, 0);
        itrias.setCoordinateIndex(1, 1);
        itrias.setCoordinateIndex(2, 2);
        itrias.setCoordinateIndex(3, 2);
        itrias.setCoordinateIndex(4, 1);
        itrias.setCoordinateIndex(5, 3);
        return itrias;
    }

    public static IndexedTriangleArray getCase34TriangleArray(Point3f a, Point3f b, Point3f c, Point3f d, Point3f e,
            Point3f f) {
        IndexedTriangleArray itrias = new IndexedTriangleArray(6,
                IndexedTriangleArray.COORDINATES | IndexedTriangleArray.NORMALS, 6);
        itrias.setCoordinate(0, a);
        itrias.setCoordinate(1, b);
        itrias.setCoordinate(2, c);
        itrias.setCoordinate(3, d);
        itrias.setCoordinate(4, e);
        itrias.setCoordinate(5, f);
        itrias.setCoordinateIndex(0, 0);
        itrias.setCoordinateIndex(1, 1);
        itrias.setCoordinateIndex(2, 2);
        itrias.setCoordinateIndex(3, 3);
        itrias.setCoordinateIndex(4, 4);
        itrias.setCoordinateIndex(5, 5);
        return itrias;
    }

    public static IndexedTriangleArray getCase5TriangleArray(Point3f a, Point3f b, Point3f c, Point3f d, Point3f e) {
        IndexedTriangleArray itrias = new IndexedTriangleArray(5,
                IndexedTriangleArray.COORDINATES | IndexedTriangleArray.NORMALS, 9);
        itrias.setCoordinate(0, a);
        itrias.setCoordinate(1, b);
        itrias.setCoordinate(2, c);
        itrias.setCoordinate(3, d);
        itrias.setCoordinate(4, e);
        itrias.setCoordinateIndex(0, 0);
        itrias.setCoordinateIndex(1, 1);
        itrias.setCoordinateIndex(2, 2);
        itrias.setCoordinateIndex(3, 0);
        itrias.setCoordinateIndex(4, 3);
        itrias.setCoordinateIndex(5, 1);
        itrias.setCoordinateIndex(6, 3);
        itrias.setCoordinateIndex(7, 4);
        itrias.setCoordinateIndex(8, 1);
        return itrias;
    }

    public static IndexedTriangleArray getCase6TriangleArray(Point3f a, Point3f b, Point3f c, Point3f d, Point3f e,
            Point3f f, Point3f g) {
        IndexedTriangleArray itrias = new IndexedTriangleArray(7,
                IndexedTriangleArray.COORDINATES | IndexedTriangleArray.NORMALS, 9);
        itrias.setCoordinate(0, a);
        itrias.setCoordinate(1, b);
        itrias.setCoordinate(2, c);
        itrias.setCoordinate(3, d);
        itrias.setCoordinate(4, e);
        itrias.setCoordinate(5, f);
        itrias.setCoordinate(6, g);
        itrias.setCoordinateIndex(0, 0);
        itrias.setCoordinateIndex(1, 1);
        itrias.setCoordinateIndex(2, 2);
        itrias.setCoordinateIndex(3, 2);
        itrias.setCoordinateIndex(4, 1);
        itrias.setCoordinateIndex(5, 3);
        itrias.setCoordinateIndex(6, 4);
        itrias.setCoordinateIndex(7, 5);
        itrias.setCoordinateIndex(8, 6);
        return itrias;
    }

    public static IndexedTriangleArray getCase7TriangleArray(Point3f a, Point3f b, Point3f c, Point3f d, Point3f e,
            Point3f f, Point3f g, Point3f h, Point3f m) {
        IndexedTriangleArray itrias = new IndexedTriangleArray(9,
                IndexedTriangleArray.COORDINATES | IndexedTriangleArray.NORMALS, 9);
        itrias.setCoordinate(0, a);
        itrias.setCoordinate(1, b);
        itrias.setCoordinate(2, c);
        itrias.setCoordinate(3, d);
        itrias.setCoordinate(4, e);
        itrias.setCoordinate(5, f);
        itrias.setCoordinate(6, g);
        itrias.setCoordinate(7, h);
        itrias.setCoordinate(8, m);
        itrias.setCoordinateIndex(0, 0);
        itrias.setCoordinateIndex(1, 1);
        itrias.setCoordinateIndex(2, 2);
        itrias.setCoordinateIndex(3, 3);
        itrias.setCoordinateIndex(4, 4);
        itrias.setCoordinateIndex(5, 5);
        itrias.setCoordinateIndex(6, 6);
        itrias.setCoordinateIndex(7, 7);
        itrias.setCoordinateIndex(8, 8);
        return itrias;
    }

    public static IndexedTriangleArray getCase9TriangleArray(Point3f a, Point3f b, Point3f c, Point3f d, Point3f e,
            Point3f f) {
        IndexedTriangleArray itrias = new IndexedTriangleArray(6,
                IndexedTriangleArray.COORDINATES | IndexedTriangleArray.NORMALS, 12);
        itrias.setCoordinate(0, a);
        itrias.setCoordinate(1, b);
        itrias.setCoordinate(2, c);
        itrias.setCoordinate(3, d);
        itrias.setCoordinate(4, e);
        itrias.setCoordinate(5, f);
        itrias.setCoordinateIndex(0, 0);
        itrias.setCoordinateIndex(1, 1);
        itrias.setCoordinateIndex(2, 2);
        itrias.setCoordinateIndex(3, 1);
        itrias.setCoordinateIndex(4, 3);
        itrias.setCoordinateIndex(5, 2);
        itrias.setCoordinateIndex(6, 2);
        itrias.setCoordinateIndex(7, 3);
        itrias.setCoordinateIndex(8, 4);
        itrias.setCoordinateIndex(9, 4);
        itrias.setCoordinateIndex(10, 3);
        itrias.setCoordinateIndex(11, 5);
        return itrias;
    }

    public static IndexedTriangleArray getCase10TriangleArray(Point3f a, Point3f b, Point3f c, Point3f d, Point3f e,
            Point3f f, Point3f g, Point3f h) {
        IndexedTriangleArray itrias = new IndexedTriangleArray(8,
                IndexedTriangleArray.COORDINATES | IndexedTriangleArray.NORMALS, 12);
        itrias.setCoordinate(0, a);
        itrias.setCoordinate(1, b);
        itrias.setCoordinate(2, c);
        itrias.setCoordinate(3, d);
        itrias.setCoordinate(4, e);
        itrias.setCoordinate(5, f);
        itrias.setCoordinate(6, g);
        itrias.setCoordinate(7, h);
        itrias.setCoordinateIndex(0, 0);
        itrias.setCoordinateIndex(1, 1);
        itrias.setCoordinateIndex(2, 2);
        itrias.setCoordinateIndex(3, 2);
        itrias.setCoordinateIndex(4, 1);
        itrias.setCoordinateIndex(5, 3);
        itrias.setCoordinateIndex(6, 4);
        itrias.setCoordinateIndex(7, 5);
        itrias.setCoordinateIndex(8, 6);
        itrias.setCoordinateIndex(9, 6);
        itrias.setCoordinateIndex(10, 5);
        itrias.setCoordinateIndex(11, 7);
        return itrias;
    }

    public static IndexedTriangleArray getCase11TriangleArray(Point3f a, Point3f b, Point3f c, Point3f d, Point3f e,
            Point3f f) {
        IndexedTriangleArray itrias = new IndexedTriangleArray(6,
                IndexedTriangleArray.COORDINATES | IndexedTriangleArray.NORMALS, 12);
        itrias.setCoordinate(0, a);
        itrias.setCoordinate(1, b);
        itrias.setCoordinate(2, c);
        itrias.setCoordinate(3, d);
        itrias.setCoordinate(4, e);
        itrias.setCoordinate(5, f);
        itrias.setCoordinateIndex(0, 0);
        itrias.setCoordinateIndex(1, 1);
        itrias.setCoordinateIndex(2, 2);
        itrias.setCoordinateIndex(3, 2);
        itrias.setCoordinateIndex(4, 1);
        itrias.setCoordinateIndex(5, 3);
        itrias.setCoordinateIndex(6, 2);
        itrias.setCoordinateIndex(7, 3);
        itrias.setCoordinateIndex(8, 4);
        itrias.setCoordinateIndex(9, 3);
        itrias.setCoordinateIndex(10, 1);
        itrias.setCoordinateIndex(11, 5);
        return itrias;
    }

    public static IndexedTriangleArray getCase12TriangleArray(Point3f a, Point3f b, Point3f c, Point3f d, Point3f e,
            Point3f f, Point3f g, Point3f h) {
        IndexedTriangleArray itrias = new IndexedTriangleArray(8,
                IndexedTriangleArray.COORDINATES | IndexedTriangleArray.NORMALS, 12);
        itrias.setCoordinate(0, a);
        itrias.setCoordinate(1, b);
        itrias.setCoordinate(2, c);
        itrias.setCoordinate(3, d);
        itrias.setCoordinate(4, e);
        itrias.setCoordinate(5, f);
        itrias.setCoordinate(6, g);
        itrias.setCoordinate(7, h);
        itrias.setCoordinateIndex(0, 0);
        itrias.setCoordinateIndex(1, 1);
        itrias.setCoordinateIndex(2, 2);
        itrias.setCoordinateIndex(3, 0);
        itrias.setCoordinateIndex(4, 3);
        itrias.setCoordinateIndex(5, 1);
        itrias.setCoordinateIndex(6, 3);
        itrias.setCoordinateIndex(7, 4);
        itrias.setCoordinateIndex(8, 1);
        itrias.setCoordinateIndex(9, 5);
        itrias.setCoordinateIndex(10, 6);
        itrias.setCoordinateIndex(11, 7);
        return itrias;
    }

    public static IndexedTriangleArray getCase13TriangleArray(Point3f a, Point3f b, Point3f c, Point3f d, Point3f e,
            Point3f f, Point3f g, Point3f h, Point3f i, Point3f j, Point3f k, Point3f l) {
        IndexedTriangleArray itrias = new IndexedTriangleArray(12,
                IndexedTriangleArray.COORDINATES | IndexedTriangleArray.NORMALS, 12);
        itrias.setCoordinate(0, a);
        itrias.setCoordinate(1, b);
        itrias.setCoordinate(2, c);
        itrias.setCoordinate(3, d);
        itrias.setCoordinate(4, e);
        itrias.setCoordinate(5, f);
        itrias.setCoordinate(6, g);
        itrias.setCoordinate(7, h);
        itrias.setCoordinate(8, i);
        itrias.setCoordinate(9, j);
        itrias.setCoordinate(10, k);
        itrias.setCoordinate(11, l);
        itrias.setCoordinateIndex(0, 0);
        itrias.setCoordinateIndex(1, 1);
        itrias.setCoordinateIndex(2, 2);
        itrias.setCoordinateIndex(3, 3);
        itrias.setCoordinateIndex(4, 4);
        itrias.setCoordinateIndex(5, 5);
        itrias.setCoordinateIndex(6, 6);
        itrias.setCoordinateIndex(7, 7);
        itrias.setCoordinateIndex(8, 8);
        itrias.setCoordinateIndex(9, 9);
        itrias.setCoordinateIndex(10, 10);
        itrias.setCoordinateIndex(11, 11);
        return itrias;
    }

    public static IndexedTriangleArray getCase14TriangleArray(Point3f a, Point3f b, Point3f c, Point3f d, Point3f e,
            Point3f f) {
        IndexedTriangleArray itrias = new IndexedTriangleArray(6,
                IndexedTriangleArray.COORDINATES | IndexedTriangleArray.NORMALS, 12);
        itrias.setCoordinate(0, a);
        itrias.setCoordinate(1, b);
        itrias.setCoordinate(2, c);
        itrias.setCoordinate(3, d);
        itrias.setCoordinate(4, e);
        itrias.setCoordinate(5, f);
        itrias.setCoordinateIndex(0, 0);
        itrias.setCoordinateIndex(1, 1);
        itrias.setCoordinateIndex(2, 2);
        itrias.setCoordinateIndex(3, 2);
        itrias.setCoordinateIndex(4, 1);
        itrias.setCoordinateIndex(5, 3);
        itrias.setCoordinateIndex(6, 3);
        itrias.setCoordinateIndex(7, 1);
        itrias.setCoordinateIndex(8, 4);
        itrias.setCoordinateIndex(9, 2);
        itrias.setCoordinateIndex(10, 3);
        itrias.setCoordinateIndex(11, 5);
        return itrias;
    }

    public void roll_dice() {
        for (int i : BaseCase) {
            CubeCase cube = new CubeCase(i, _lookupTab.get(i));
            for (int x = 0; x < 4; x++) {
                cube.rotate_left();
                for (int y = 0; y < 4; y++) {
                    cube.rotate_up();
                    for (int z = 0; z < 4; z++) {
                        cube.inverter();
                        if (!_lookupTab.containsKey(cube.get_cornerpias())) {
                            IndexedTriangleArray ita = cube.get_trias();
                            int index = cube.get_cornerpias();
                            // cube.print_triangles();
                            _lookupTab.put(index, ita);
                            // _lookupTab.put(cube.get_cornerpias(), cube.get_trias());
                        }
                        // _lookupTab.put(cube.get_cornerpias(), cube.get_trias());
                        cube.inverter();
                        cube.rotate_self();
                        if (!_lookupTab.containsKey(cube.get_cornerpias())) {
                            IndexedTriangleArray ita = cube.get_trias();
                            int index = cube.get_cornerpias();
                            // cube.print_triangles();
                            _lookupTab.put(index, ita);
                            // print_triangles(64);
                        }

                    }
                }
            }
        }
        // print_triangles(64);
    }

    public void print_triangles(int index) {
        Point3f p = new Point3f();
        IndexedTriangleArray ita = _lookupTab.get(index);
        for (int i = 0; i < ita.getVertexCount(); i++) {
            ita.getCoordinate(i, p);
            System.out.println(p.toString());
        }
    }

    public static void main(String[] args) {

        MarchingCube mc = new MarchingCube();

        System.out.println("before: " + MarchingCube._lookupTab.size());
        mc.roll_dice();

        System.out.println("after: " + MarchingCube._lookupTab.size());

        // for (int i = 0; i < 8; i++) {
        // int in = 0b11 << i;
        // if (in > 254)
        // continue;
        // System.out.println("index:" + Integer.toBinaryString(in));
        // mc.print_triangles(in);
        // }
        mc.print_triangles(1);
        mc.print_triangles(254);
        mc.print_triangles(64);
    }

}