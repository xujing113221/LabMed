package main;

import java.util.LinkedList;
import java.util.Queue;
import javax.vecmath.Point3i;

import misc.BitMask;

/**
 * This class represents a segment. Simply spoken, a segment has a unique name,
 * a color for displaying in the 2d/3d viewport and contains n bitmasks where n
 * is the number of images in the image stack.
 * 
 * @author Karl-Ingo Friese
 */
public class Segment {
	private String _name; // the segment name
	private int _color; // the segment color
	private int _w; // Bitmask width
	private int _h; // Bitmask height
	private int _max; // min-max segment:max
	private int _min; // min-max segment:min
	// private float _rg_varianz; // region grow segment: varianz
	// private Point3i _rg_seed; // region grow segment: seed
	// public final String RG_SEG_NAME; // region grow segment: segment name

	private BitMask[] _layers; // each segment contains an array of n bitmasks

	/**
	 * Constructor for new segment objects.
	 * 
	 * @param name      the name of the new segment
	 * @param w         the width of the bitmasks
	 * @param h         the height of the bitmasks
	 * @param layer_num the total number of bitmasks
	 */
	public Segment(String name, int w, int h, int layer_num) {
		this._name = name;
		this._w = w;
		this._h = h;

		_max = 4000; // min-max segment
		_min = 100;

		_color = 0xff00ff;
		_layers = new BitMask[layer_num];

		for (int i = 0; i < layer_num; i++) {
			_layers[i] = new BitMask(_w, _h);
		}
	}

	/**
	 * create min-max segement
	 * 
	 * @param min
	 * @param max
	 * @param slices: Image Stack
	 */
	public void create_range_seg(int min, int max, ImageStack slices) {
		if (min > max)
			return;

		_max = max;
		_min = min;

		// clean segment mask
		for (int i = 0; i < _layers.length; i++)
			_layers[i].clear();

		int images_cnt = slices.getNumberOfImages();
		for (int i = 0; i < images_cnt; i++) {
			byte[] pixels = slices.getDiFile(i).getElement(0x7FE00010).getValues();
			for (int y = 0; y < _h; y++)
				for (int x = 0; x < _w; x++) {
					int index = (y * _w + x) * 2;
					int value = (pixels[index + 1] & 0xff) << 8 | (pixels[index] & 0xff);
					if (value >= min && value <= max)
						_layers[i].set(x, y, true);
				}
		}
	}

	/**
	 * create region grow segment: i(p) ∈ [i(s) − v ∗ i(s), i(s) + v ∗ i(s)]
	 * 
	 * @param seed: start point.
	 * @param var:  Varianz
	 */
	public void create_region_grow_seg(Point3i seed, float var, ImageStack slices) {

		Queue<Point3i> voxelQ = new LinkedList<Point3i>();
		final int DateLength = slices.getImageWidth() * slices.getImageHeight() * slices.getNumberOfImages();
		final Point3i[] n6 = { new Point3i(1, 0, 0), new Point3i(-1, 0, 0), new Point3i(0, 1, 0), new Point3i(0, -1, 0),
				new Point3i(0, 0, 1), new Point3i(0, 0, -1) };

		// clean segment mask
		for (int i = 0; i < _layers.length; i++)
			_layers[i].clear();

		voxelQ.add(seed); // start from seed.
		while (!voxelQ.isEmpty()) {
			Point3i p = voxelQ.poll();

			if (isMarkedVoxel(p)) // very important!! Avoid duplication in Quene.
				continue;

			if (voxelQ.size() > DateLength) // crash!!
				break;

			_layers[p.z].set(p.x, p.y, true); // mark the voxel, create segment.
			int p_value = getPixelValueFormSlices(p, slices);
			for (int i = 0; i < n6.length; i++) {
				Point3i np = new Point3i(p.x + n6[i].x, p.y + n6[i].y, p.z + n6[i].z);
				if (isVoxelInImageStack(np) && !isMarkedVoxel(np)) {
					int n6_value = getPixelValueFormSlices(np, slices);
					if (isRegionszugehoerigkeit(n6_value, p_value, var))
						voxelQ.add(np);
				}
			}
		}
	}

	private boolean isMarkedVoxel(Point3i p) {
		return _layers[p.z].get(p.x, p.y);
	}

	private boolean isRegionszugehoerigkeit(int p, int i, float v) {
		int min = (int) ((float) i - (float) i * v);
		int max = (int) ((float) i + (float) i * v);

		return (p <= max && p >= min) ? true : false;
	}

	private int getPixelValueFormSlices(Point3i p, ImageStack is) {
		byte[] pixels = is.getDiFile(p.z).getElement(0x7FE00010).getValues();
		int i = (p.x + p.y * _w) * 2;
		int value = (pixels[i + 1] & 0xff) << 8 | (pixels[i] & 0xff);

		return value;
	}

	private boolean isVoxelInImageStack(Point3i voxel) {
		int w = _w;
		int h = _h;
		int n = _layers.length;

		if (voxel.x >= w || voxel.x < 0)
			return false;
		if (voxel.y >= h || voxel.y < 0)
			return false;
		if (voxel.z >= n || voxel.z < 0)
			return false;

		return true;
	}

	/**
	 * Returns the number of bitmasks contained in this segment.
	 * 
	 * @return the number of layers.
	 */
	public int getMaskNum() {
		return _layers.length;
	}

	/**
	 * Returns the Bitmask of a single layer.
	 * 
	 * @param i the layer number
	 * @return the coresponding bitmask
	 */
	public BitMask getMask(int i) {
		return _layers[i];
	}

	/**
	 * Returns the name of the segment.
	 * 
	 * @return the segment name.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Sets the name of the segment.
	 * 
	 * @param name the new segment name
	 */
	public void setName(String name) {
		_name = name;
	}

	/**
	 * Returns the segment color as the usual rgb int value.
	 * 
	 * @return the color
	 */
	public int getColor() {
		return _color;
	}

	/**
	 * Sets the segment color.
	 * 
	 * @param color the segment color (used when displaying in 2d/3d viewport)
	 */
	public void setColor(int color) {
		_color = color;
	}

	public int get_max() {
		return _max;
	}

	public int get_min() {
		return _min;
	}

}
