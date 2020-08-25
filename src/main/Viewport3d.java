package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.View;
import javax.media.j3d.*;

import javax.vecmath.*;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;

import misc.MyObservable;
import misc.MyObserver;

/**
 * Three dimensional viewport for viewing the dicom images + segmentations.
 * 
 * @author Karl-Ingo Friese
 */
public class Viewport3d extends Viewport implements MyObserver {
	private static final long serialVersionUID = 1L;

	/**
	 * Private class, implementing the GUI element for displaying the 3d data.
	 */
	public class Panel3d extends Canvas3D {
		private static final long serialVersionUID = 1L;
		public SimpleUniverse _simple_u;
		public BranchGroup _scene;
		public float _distance;

		public Panel3d(GraphicsConfiguration config) {
			super(config);
			setMinimumSize(new Dimension(DEF_WIDTH, DEF_HEIGHT));
			setMaximumSize(new Dimension(DEF_WIDTH, DEF_HEIGHT));
			setPreferredSize(new Dimension(DEF_WIDTH, DEF_HEIGHT));
			setBackground(Color.black);

			_simple_u = new SimpleUniverse(this);
			_simple_u.getViewingPlatform().setNominalViewingTransform();
			_scene = null;

			_distance = 0.0045f;
			createScene();
			super.getView().setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);
		}

		public void createScene() {
			if (_scene != null) {
				_scene.detach();
			}
			_scene = new BranchGroup();
			_scene.setCapability(BranchGroup.ALLOW_DETACH);

			Transform3D trans3d = new Transform3D();
			trans3d.rotX(Math.PI / 2.0d); // rotate
			// trans3d.setScale(new Vector3d(1.0d, 1.0d, (256.0d / 113.d)));
			TransformGroup objTrans = new TransformGroup(trans3d);
			objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

			if (_map_name_to_seg.isEmpty()) {
				objTrans.addChild(draw_cube(_distance));
			} else {
				objTrans.addChild(draw_cube(_distance));
				for (String seg_name : _map_name_to_seg.keySet()) {
					Segment seg = _slices.getSegment(seg_name);
					objTrans.addChild(create_pointcloud(seg, _distance));
				}
			}

			_scene.addChild(objTrans);

			BoundingSphere bound = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0);
			// 添加通过鼠标左键控制3D物体旋转的对象
			MouseRotate mrotate = new MouseRotate();
			mrotate.setTransformGroup(objTrans);
			objTrans.addChild(mrotate);
			mrotate.setSchedulingBounds(bound);
			// 添加鼠标右键的拖拉运动控制3D物体（X,Y）平移
			MouseTranslate mtrans = new MouseTranslate();
			mtrans.setTransformGroup(objTrans);
			objTrans.addChild(mtrans);
			mtrans.setSchedulingBounds(bound);
			// 添加鼠标滚轮控制3D物体沿Z轴
			MouseWheelZoom mzoom = new MouseWheelZoom();
			mzoom.setTransformGroup(objTrans);
			objTrans.addChild(mzoom);
			mzoom.setSchedulingBounds(bound);

			_scene.compile();
			_simple_u.addBranchGraph(_scene);
			// 设置背景
			// Color3f bgColor = new Color3f(0.0f, 0.0f, 0.0f);
			// Background bg = new Background(bgColor);
			// bg.setApplicationBounds(bound);
			// _scene.addChild(bg);

			// 设置光源
			// Color3f lightColor = new Color3f(1.0f, 1.0f, 0.9f);
			// Vector3f lightDirection = new Vector3f(4.0f, -7.0f, -12.0f);
			// // 设置定向光的颜色和影响范围
			// DirectionalLight light = new DirectionalLight(lightColor, lightDirection);
			// light.setInfluencingBounds(bound);
			// // 将光源添加到场景
			// _scene.addChild(light);

			// 添加模型
			// objTrans.addChild(new ColorCube(0.5f));
			// objTrans.addChild(new Sphere(0.5f));
		}

	}

	private Panel3d _panel3d;

	/**
	 * Constructor, with a reference to the global image stack as argument.
	 * 
	 * @param slices a reference to the global image stack
	 */
	public Viewport3d() {
		super();

		this.setPreferredSize(new Dimension(DEF_WIDTH, DEF_HEIGHT));
		this.setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		_panel3d = new Panel3d(config);
		this.add(_panel3d, BorderLayout.CENTER);
	}

	public void setPointCloudDis(float distance) {
		_panel3d._distance = distance;
	}

	public float getPointCloudDis() {
		return _panel3d._distance;
	}

	/**
	 * calculates the 3d data structurs.
	 */
	public void update_view() {
		_panel3d.createScene();
	}

	private Shape3D create_pointcloud(Segment seg, float distance) {
		int w = _slices.getImageWidth();
		int h = _slices.getImageHeight();
		int s = _slices.getNumberOfImages();
		PointArray points = new PointArray(seg.getSegPointsCnt(), PointArray.COORDINATES);

		int index = 0;
		for (int z = 0; z < s; z++)
			for (int y = 0; y < h; y++)
				for (int x = 0; x < w; x++) {
					if (seg.getMask(z).get(y, x)) {
						float px = (x - w / 2.0f) * distance;
						float py = (y - h / 2.0f) * distance;
						float pz = (z - s / 2.0f) * (256.0f / s) * distance;
						points.setCoordinate(index++, new Point3f(px, py, pz));
					}

				}

		int color = seg.getColor();
		float color_r = (color & 0xff0000) * 1.0f;
		float color_g = (color & 0x00ff00) * 1.0f;
		float color_b = (color & 0x0000ff) * 1.0f;

		ColoringAttributes color_ca = new ColoringAttributes();
		color_ca.setColor(new Color3f(color_r, color_g, color_b));
		PointAttributes pointa = new PointAttributes();
		pointa.setPointAntialiasingEnable(true);
		pointa.setPointSize(distance);

		Appearance ap = new Appearance();
		ap.setColoringAttributes(color_ca);
		ap.setPointAttributes(pointa);

		Shape3D point_shape = new Shape3D(points, ap);

		return point_shape;
	}

	private Shape3D draw_cube(float distance) {
		LineArray lines = new LineArray(24, LineArray.COORDINATES);

		float a = 128.0f * distance;
		int n = 0;
		lines.setCoordinate(n++, new Point3f(a, a, a));
		lines.setCoordinate(n++, new Point3f(a, a, -a));
		lines.setCoordinate(n++, new Point3f(a, a, -a));
		lines.setCoordinate(n++, new Point3f(a, -a, -a));
		lines.setCoordinate(n++, new Point3f(a, -a, -a));
		lines.setCoordinate(n++, new Point3f(a, -a, a));
		lines.setCoordinate(n++, new Point3f(a, -a, a));
		lines.setCoordinate(n++, new Point3f(a, a, a));

		lines.setCoordinate(n++, new Point3f(-a, a, a));
		lines.setCoordinate(n++, new Point3f(-a, a, -a));
		lines.setCoordinate(n++, new Point3f(-a, a, -a));
		lines.setCoordinate(n++, new Point3f(-a, -a, -a));
		lines.setCoordinate(n++, new Point3f(-a, -a, -a));
		lines.setCoordinate(n++, new Point3f(-a, -a, a));
		lines.setCoordinate(n++, new Point3f(-a, -a, a));
		lines.setCoordinate(n++, new Point3f(-a, a, a));

		lines.setCoordinate(n++, new Point3f(a, a, a));
		lines.setCoordinate(n++, new Point3f(-a, a, a));
		lines.setCoordinate(n++, new Point3f(a, a, -a));
		lines.setCoordinate(n++, new Point3f(-a, a, -a));
		lines.setCoordinate(n++, new Point3f(a, -a, -a));
		lines.setCoordinate(n++, new Point3f(-a, -a, -a));
		lines.setCoordinate(n++, new Point3f(a, -a, a));
		lines.setCoordinate(n++, new Point3f(-a, -a, a));

		ColoringAttributes color_ca = new ColoringAttributes();
		color_ca.setColor(new Color3f(Color.white));
		LineAttributes linea = new LineAttributes();
		// linea.setLineWidth(1.0f);
		linea.setLineAntialiasingEnable(true);

		Appearance ap = new Appearance();
		ap.setColoringAttributes(color_ca);
		ap.setLineAttributes(linea);

		Shape3D cube = new Shape3D(lines, ap);
		return cube;
	}

	/**
	 * Implements the observer function update. Updates can be triggered by the
	 * global image stack.
	 */
	public void update(final MyObservable mo, final Message msg) {
		if (!EventQueue.isDispatchThread()) {
			// all swing thingies must be done in the AWT-EventQueue
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					update(mo, msg);
				}
			});
			return;
		}

		if (msg._type == Message.M_SEG_CHANGED) {
			String seg_name = ((Segment) (msg._obj)).getName();
			boolean update_needed = _map_name_to_seg.containsKey(seg_name);
			if (update_needed) {
				update_view();
			}
		}
	}
}
