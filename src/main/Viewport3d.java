package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;

import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.Sphere;

import javax.vecmath.*;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;

import misc.MyObservable;
import misc.MyObserver;
import misc.MarchingCube;

/**
 * Three dimensional viewport for viewing the dicom images + segmentations.
 * 
 * @author Karl-Ingo Friese
 */
public class Viewport3d extends Viewport implements MyObserver {
	private static final long serialVersionUID = 1L;

	// TODO: 把下面两个变量放在父类中
	private int _window_width;
	private int _window_center;

	private boolean _ortho_slice;
	private boolean _marching_cube;
	private boolean _point_cloud;
	private boolean _show_original_data;

	private int[] _slices_pos = { 50, 128, 128 };
	private int _v2d_view_mode = 0;
	private int _mc_size;

	// private int _test_point = 1;
	private MarchingCube _mc = new MarchingCube();

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
			// trans3d.rotY(Math.PI / 4.0d);
			// trans3d.setScale(new Vector3d(1.0d, 1.0d, (256.0d / 113.d)));

			TransformGroup transfG = new TransformGroup(trans3d);
			transfG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			transfG.addChild(draw_cube(_distance));

			Transform3D transf = new Transform3D();
			transf.setScale(new Vector3d(1, -1, 1));
			TransformGroup objTrans = new TransformGroup(transf);
			objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			transfG.addChild(objTrans);

			if (_slices.getNumberOfImages() != 0) {
				if (_show_original_data) {
					float transparency = 0.1f;
					for (int i = 0; i < _slices.getNumberOfImages(); i++)
						objTrans.addChild(create_texture(0, i, _distance, transparency));
					for (int i = 0; i < _slices.getImageHeight(); i++)
						objTrans.addChild(create_texture(1, i, _distance, transparency));
					for (int i = 0; i < _slices.getImageWidth(); i++)
						objTrans.addChild(create_texture(2, i, _distance, transparency));
				}
				if (_ortho_slice) {
					float transparency = -1.0f;
					objTrans.addChild(create_texture(0, _slices_pos[0], _distance, transparency));
					objTrans.addChild(create_texture(1, _slices_pos[1], _distance, transparency));
					objTrans.addChild(create_texture(2, _slices_pos[2], _distance, transparency));
				}
			}
			if (!_map_name_to_seg.isEmpty()) {
				for (String seg_name : _map_name_to_seg.keySet()) {
					Segment seg = _slices.getSegment(seg_name);
					if (_point_cloud)
						objTrans.addChild(create_pointcloud(seg, _distance));
					if (_marching_cube) {
						Transform3D t3d = new Transform3D();
						t3d.rotZ(Math.PI / -2.0d); // rotate
						TransformGroup tf = new TransformGroup(t3d);
						tf.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
						create_MarchingCube(tf, seg, _mc_size, _distance);
						transfG.addChild(tf);
					}

				}
			}

			BoundingSphere bound = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.MAX_VALUE);
			// 添加通过鼠标左键控制3D物体旋转的对象
			MouseRotate mrotate = new MouseRotate();
			mrotate.setTransformGroup(transfG);
			transfG.addChild(mrotate);
			mrotate.setSchedulingBounds(bound);
			// 添加鼠标右键的拖拉运动控制3D物体（X,Y）平移
			MouseTranslate mtrans = new MouseTranslate();
			mtrans.setTransformGroup(transfG);
			transfG.addChild(mtrans);
			mtrans.setSchedulingBounds(bound);
			// 添加鼠标滚轮控制3D物体沿Z轴
			MouseWheelZoom mzoom = new MouseWheelZoom();
			mzoom.setTransformGroup(transfG);
			transfG.addChild(mzoom);
			mzoom.setSchedulingBounds(bound);

			DirectionalLight light = new DirectionalLight(new Color3f(0.0f, 1.0f, 0.0f),
					new Vector3f(4.0f, -7.0f, -12.0f));
			light.setInfluencingBounds(bound);
			_scene.addChild(light);

			// AmbientLight a_light = new AmbientLight();
			// a_light.setInfluencingBounds(bound);
			// a_light.setColor(new Color3f(0.0f, 0.0f, 0.8f));
			// _scene.addChild(d_light);

			// objTrans.addChild(new Sphere(128 * _distance));
			_scene.addChild(transfG);
			_scene.compile();
			_simple_u.addBranchGraph(_scene);
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
		_window_center = (int) (50 * 40.95);
		_window_width = (int) (50 * 40.95);
		_ortho_slice = false;
		_marching_cube = false;
		_point_cloud = false;
		_show_original_data = false;

		_mc_size = 3;

		_mc.roll_dice();

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

	public void showMc(TransformGroup tg, int index) {
		tg.addChild(create_MarchingCube(index));
	}

	private Shape3D create_texture(int view_mode, int active, double distance, float transparency) {
		int w = _slices.getImageWidth();
		int h = _slices.getImageHeight();
		int s = _slices.getNumberOfImages();

		Point3d a = null, b = null, c = null, d = null;
		if (view_mode == 0) {
			double pos = (double) (active - s / 2 + 1) * distance * w / s;
			double x = (double) w / 2 * distance;
			a = new Point3d(-x, x, pos);
			b = new Point3d(x, x, pos);
			c = new Point3d(x, -x, pos);
			d = new Point3d(-x, -x, pos);
		} else if (view_mode == 1) {
			double pos = (double) (active - w / 2 + 1) * distance;
			double x = (double) w / 2 * distance;
			a = new Point3d(-x, pos, x);
			b = new Point3d(x, pos, x);
			c = new Point3d(x, pos, -x);
			d = new Point3d(-x, pos, -x);
		} else {
			double pos = (double) (active - h / 2 + 1) * distance;
			double x = (double) h / 2 * distance;
			a = new Point3d(pos, -x, x);
			b = new Point3d(pos, x, x);
			c = new Point3d(pos, x, -x);
			d = new Point3d(pos, -x, -x);
		}

		QuadArray sq = new QuadArray(4, QuadArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);
		sq.setCoordinate(0, a);
		sq.setCoordinate(1, b);
		sq.setCoordinate(2, c);
		sq.setCoordinate(3, d);
		sq.setTextureCoordinate(0, 0, new TexCoord2f(0.0f, 0.0f));
		sq.setTextureCoordinate(0, 1, new TexCoord2f(1.0f, 0.0f));
		sq.setTextureCoordinate(0, 2, new TexCoord2f(1.0f, 1.0f));
		sq.setTextureCoordinate(0, 3, new TexCoord2f(0.0f, 1.0f));

		BufferedImage img = getBGImage(view_mode, active);
		ImageComponent2D i2d = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, img);
		Texture2D tex = new Texture2D(Texture2D.BASE_LEVEL, Texture2D.RGBA, img.getWidth(), img.getHeight());
		tex.setImage(0, i2d);

		Appearance ap_plane = new Appearance();
		PolygonAttributes pa = new PolygonAttributes();
		pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		TransparencyAttributes transat = new TransparencyAttributes();
		if (transparency >= 0) {
			transat.setTransparencyMode(TransparencyAttributes.NICEST);
			transat.setTransparency(transparency);
		} else {
			transat.setTransparencyMode(TransparencyAttributes.NONE);
		}

		ap_plane.setPolygonAttributes(pa);
		ap_plane.setTransparencyAttributes(transat);
		ap_plane.setTexture(tex);

		Shape3D square_sh = new Shape3D(sq, ap_plane);
		return square_sh;
	}

	private BufferedImage getBGImage(int mode, int pos) {
		int w = 0, h = 0;
		if (mode == 0) {
			w = _slices.getImageWidth();
			h = _slices.getImageHeight();
		} else if (mode == 1) {
			w = _slices.getImageHeight();
			h = _slices.getNumberOfImages();
		} else if (mode == 2) {
			w = _slices.getImageWidth();
			h = _slices.getNumberOfImages();
		}

		BufferedImage bg_img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		int z = pos;
		for (int y = 0; y < h; y++)
			for (int x = 0; x < w; x++) {
				int value = 0;
				if (mode == 0)
					value = getPixelValueFromSlices(y, x, z);
				else if (mode == 1)
					value = getPixelValueFromSlices(z, x, y);
				else
					value = getPixelValueFromSlices(x, z, y);
				int rgba = (int) ((float) value * 1.0f);

				bg_img.setRGB(x, y, (rgba << 24) | (rgba << 16) | (rgba << 8) | rgba);
			}

		return bg_img;
	}

	private int getPixelValueFromSlices(int x, int y, int z) {
		byte[] pixels = _slices.getDiFile(z).getElement(0x7FE00010).getValues();
		int index = (y * _slices.getImageWidth() + x) * 2;
		int pixel = (pixels[index + 1] & 0xff) << 8 | (pixels[index] & 0xff);

		return pixelToValue256(pixel);
	}

	private int pixelToValue256(int pixel) {
		int value = 0;
		int max = _window_center + _window_width + 1;
		int min = _window_center - _window_width;
		if (min < 0)
			min = 0;
		if (max > 4095)
			max = 4095;

		if (pixel > max)
			value = 255;
		else if (pixel < min)
			value = 0;
		else
			value = (int) ((pixel - min) * (255.0 / (max - min)));
		return value;
	}

	public void setViewMode(int mode) {
		this._v2d_view_mode = mode;
	}

	public void toggleShowOriginalData() {
		_show_original_data = !_show_original_data;
	}

	public void setWindowWidth(int w) {
		_window_width = w;
	}

	public void setWindowCenter(int c) {
		_window_center = c;
	}

	public int getWindowWidth() {
		return _window_width;
	}

	public int getWindowCenter() {
		return _window_center;
	}

	private void create_MarchingCube(TransformGroup objTrans, Segment seg, int size, float distance) {
		int w = _slices.getImageWidth();
		int h = _slices.getImageHeight();
		int s = _slices.getNumberOfImages();

		for (int z = 0; z < s; z = z + size)
			for (int y = 0; y < h; y = y + size)
				for (int x = 0; x < w; x = x + size) {

					int index = 0;
					if (x + size < w && y + size < h && z + size < s)
						index = getMCLookuotabIndex(seg, x, y, z, size);

					if (index > 0 && index < 0xff) {
						float px = (x - w / 2.0f) * distance;
						float py = (y - h / 2.0f) * distance;
						float pz = (z - s / 2.0f) * (256.0f / s) * distance;
						Point3f pos = new Point3f(px, py, pz);

						IndexedTriangleArray ita = _mc.getTriArray(index);

						Point3f p = new Point3f();
						for (int i = 0; i < ita.getVertexCount(); i++) {
							ita.getCoordinate(i, p);
							p.x = p.x * size * distance;
							p.y = p.y * size * distance;
							p.z = p.z * size * distance * w / s;
							// p.scale(size * distance);
							p.add(pos);
							ita.setCoordinate(i, p);
						}

						final GeometryInfo gi = new GeometryInfo(ita);
						final NormalGenerator normalGenerator = new NormalGenerator();
						normalGenerator.generateNormals(gi);
						final GeometryArray geometryArray = gi.getGeometryArray();

						Appearance ap = new Appearance();
						Material ma = new Material();
						ap.setMaterial(ma);
						objTrans.addChild(new Shape3D(geometryArray, ap));
					}

				}
	}

	private int getMCLookuotabIndex(Segment seg, int x, int y, int z, int size) {
		int index = 0;
		if (seg.getMask(z).get(x, y))
			index |= (int) (1) << 0;
		if (seg.getMask(z).get(x + size, y))
			index |= (int) (1) << 1;
		if (seg.getMask(z + size).get(x + size, y))
			index |= (int) (1) << 2;
		if (seg.getMask(z + size).get(x, y))
			index |= (int) (1) << 3;
		if (seg.getMask(z).get(x, y + size))
			index |= (int) (1) << 4;
		if (seg.getMask(z).get(x + size, y + size))
			index |= (int) (1) << 5;
		if (seg.getMask(z + size).get(x + size, y + size))
			index |= (int) (1) << 6;
		if (seg.getMask(z + size).get(x, y + size))
			index |= (int) (1) << 7;
		return index;
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

	private Shape3D create_MarchingCube(int index) {
		MarchingCube mc = new MarchingCube();
		IndexedTriangleArray ita = mc.getTriArray(index);

		ColoringAttributes color_ca = new ColoringAttributes();
		color_ca.setColor(new Color3f(0, 0, 1.0f));

		Appearance ap = new Appearance();
		ap.setColoringAttributes(color_ca);

		Shape3D point_shape = new Shape3D(ita, ap);
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

		// if (msg._type == Message.M_NEW_IMAGE_LOADED) {
		// update_view();
		// }

		if (msg._type == Message.M_NEW_ACTIVE_IMAGE) {
			_slices_pos[_v2d_view_mode] = _slices.getActiveImageID();
			if (_ortho_slice)
				update_view();
		}

		// if (msg._type == Message.M_SEG_CHANGED) {
		// String seg_name = ((Segment) msg._obj).getName();
		// boolean update_needed = _map_name_to_seg.containsKey(seg_name);
		// if (update_needed) {
		// update_view();
		// }
		// }
	}

	public void setMCsize(int size) {
		this._mc_size = size;
	}

	public void toggleOrthoSlice() {
		_ortho_slice = !_ortho_slice;
	}

	public void toggleMarchingCube() {
		_marching_cube = !_marching_cube;
	}

	public void togglePointCloud() {
		_point_cloud = !_point_cloud;
	}

	private Shape3D MCCube() {
		LineArray lines = new LineArray(24, LineArray.COORDINATES);

		float a = 0.5f;
		int n = 0;
		lines.setCoordinate(n++, new Point3f(a + a, a + a, a + a));
		lines.setCoordinate(n++, new Point3f(a + a, a + a, -a + a));
		lines.setCoordinate(n++, new Point3f(a + a, a + a, -a + a));
		lines.setCoordinate(n++, new Point3f(a + a, -a + a, -a + a));
		lines.setCoordinate(n++, new Point3f(a + a, -a + a, -a + a));
		lines.setCoordinate(n++, new Point3f(a + a, -a + a, a + a));
		lines.setCoordinate(n++, new Point3f(a + a, -a + a, a + a));
		lines.setCoordinate(n++, new Point3f(a + a, a + a, a + a));

		lines.setCoordinate(n++, new Point3f(-a + a, a + a, a + a));
		lines.setCoordinate(n++, new Point3f(-a + a, a + a, -a + a));
		lines.setCoordinate(n++, new Point3f(-a + a, a + a, -a + a));
		lines.setCoordinate(n++, new Point3f(-a + a, -a + a, -a + a));
		lines.setCoordinate(n++, new Point3f(-a + a, -a + a, -a + a));
		lines.setCoordinate(n++, new Point3f(-a + a, -a + a, a + a));
		lines.setCoordinate(n++, new Point3f(-a + a, -a + a, a + a));
		lines.setCoordinate(n++, new Point3f(-a + a, a + a, a + a));

		lines.setCoordinate(n++, new Point3f(a + a, a + a, a + a));
		lines.setCoordinate(n++, new Point3f(-a + a, a + a, a + a));
		lines.setCoordinate(n++, new Point3f(a + a, a + a, -a + a));
		lines.setCoordinate(n++, new Point3f(-a + a, a + a, -a + a));
		lines.setCoordinate(n++, new Point3f(a + a, -a + a, -a + a));
		lines.setCoordinate(n++, new Point3f(-a + a, -a + a, -a + a));
		lines.setCoordinate(n++, new Point3f(a + a, -a + a, a + a));
		lines.setCoordinate(n++, new Point3f(-a + a, -a + a, a + a));

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
}
