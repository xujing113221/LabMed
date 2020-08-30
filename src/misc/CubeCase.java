package misc;

import javax.media.j3d.IndexedGeometryArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.NodeComponent;
import javax.vecmath.Point3f;

public class CubeCase {

    private int _cornerpias;
    private IndexedTriangleArray _trias;

    public CubeCase(int i, IndexedTriangleArray ita) {
        super();
        this._cornerpias = i;
        this._trias = ita;
    }

    private boolean get(int val, int p) {
        return (val & ((int) (1)) << p) != 0;
    }

    private void set(int p, boolean t) {
        if (t)
            _cornerpias |= ((int) (1)) << p;
        else
            _cornerpias &= ~(((int) (1)) << p);
    }

    public void rotate_left() {
        int tmp = _cornerpias;
        set(0, get(tmp, 3));
        set(1, get(tmp, 0));
        set(2, get(tmp, 1));
        set(3, get(tmp, 2));
        set(4, get(tmp, 7));
        set(5, get(tmp, 4));
        set(6, get(tmp, 5));
        set(7, get(tmp, 6));

        /*
         * Point3d rotate left: [px,py,pz] ---> [1-pz,py,px]
         */
        float len = MarchingCube.len;
        Point3f p = new Point3f();
        for (int i = 0; i < _trias.getVertexCount(); i++) {
            _trias.getCoordinate(i, p);
            Point3f np = new Point3f(len - p.z, p.y, p.x);
            _trias.setCoordinate(i, np);
        }

    }

    public void rotate_self() {
        int tmp = _cornerpias;
        set(0, get(tmp, 4));
        set(1, get(tmp, 0));
        set(2, get(tmp, 3));
        set(3, get(tmp, 7));
        set(4, get(tmp, 5));
        set(5, get(tmp, 1));
        set(6, get(tmp, 2));
        set(7, get(tmp, 6));

        /*
         * Point3d rotate self: [px,py,pz] ---> [1-py,px,pz]
         */
        float len = MarchingCube.len;
        Point3f p = new Point3f();
        for (int i = 0; i < _trias.getVertexCount(); i++) {
            _trias.getCoordinate(i, p);
            Point3f np = new Point3f(len - p.y, p.x, p.z);
            _trias.setCoordinate(i, np);
        }

    }

    public void rotate_up() {
        int tmp = _cornerpias;
        set(0, get(tmp, 4));
        set(1, get(tmp, 5));
        set(2, get(tmp, 1));
        set(3, get(tmp, 0));
        set(4, get(tmp, 7));
        set(5, get(tmp, 6));
        set(6, get(tmp, 2));
        set(7, get(tmp, 3));

        /*
         * Point3d rotate up: [px,py,pz] ---> [px,pz,1-py]
         */
        float len = MarchingCube.len;
        Point3f p = new Point3f();
        for (int i = 0; i < _trias.getVertexCount(); i++) {
            _trias.getCoordinate(i, p);
            Point3f np = new Point3f(p.x, p.z, len - p.y);
            _trias.setCoordinate(i, np);
        }
    }

    public void inverter() {
        _cornerpias = ~_cornerpias & 0xff;

        /*
         * inverter: 123 ---> 213
         */
        for (int i = 0; i < _trias.getIndexCount(); i = i + 3) {
            int tmp = _trias.getCoordinateIndex(i);
            _trias.setCoordinateIndex(i, _trias.getCoordinateIndex(i + 1));
            _trias.setCoordinateIndex(i + 1, tmp);
        }

    }

    public int get_cornerpias() {
        return _cornerpias;
    }

    public IndexedTriangleArray get_trias() {
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

    public void print_triangles() {
        Point3f p = new Point3f();
        for (int i = 0; i < _trias.getVertexCount(); i++) {
            _trias.getCoordinate(i, p);
            System.out.println(p.toString());
        }
    }

    public static void main(String[] args) {
        Point3f a = MarchingCube._v[0];
        Point3f b = MarchingCube._v[1];
        Point3f c = MarchingCube._v[2];
        Point3f d = MarchingCube._v[3];
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

        int index = 0b00000001;
        CubeCase cc = new CubeCase(index, itrias);

        // System.out.println(itrias.getIndexCount());

        // IndexedTriangleArray tt = cc.get_trias();
        // System.out.println("before 1. convert");
        // for (int i = 0; i < tt.getIndexCount(); i++) {
        // System.out.println(itrias.getCoordinateIndex(i));
        // }
        // cc.inverter();
        // tt = cc.get_trias();
        // System.out.println("after 1. convert");
        // for (int i = 0; i < tt.getIndexCount(); i++) {
        // System.out.println(itrias.getCoordinateIndex(i));
        // }
        // cc.inverter();
        // tt = cc.get_trias();
        // System.out.println("after 2. convert");
        // for (int i = 0; i < tt.getIndexCount(); i++) {
        // System.out.println(itrias.getCoordinateIndex(i));
        // }
        // System.out.println("----------------");
        // cc.print_triangles();
        // cc.rotate_self();
        // cc.rotate_self();
        // cc.rotate_self();
        // cc.rotate_self();
        // System.out.println("----------------");
        // cc.print_triangles();
        // cc.rotate_up();
        // cc.rotate_up();
        // cc.rotate_up();
        // cc.rotate_up();
        System.out.println("----------------");
        cc.print_triangles();
        cc.rotate_left();
        // cc.rotate_left();
        // cc.rotate_left();
        // cc.rotate_left();
        System.out.println("----------------");
        cc.print_triangles();
        // cc.inverter();
        // System.out.println(Integer.toBinaryString(cc.get_cornerpias()));

    }

}