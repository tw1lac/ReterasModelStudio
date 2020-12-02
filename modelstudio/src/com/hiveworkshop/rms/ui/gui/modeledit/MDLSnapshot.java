package com.hiveworkshop.rms.ui.gui.modeledit;

import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.editor.model.AnimFlag.Entry;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelViewManager;
import com.hiveworkshop.rms.filesystem.GameDataFileSystem;
import com.hiveworkshop.rms.parsers.blp.BLPHandler;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLayer.FilterMode;
import com.hiveworkshop.rms.parsers.mdlx.util.MdxUtils;
import com.hiveworkshop.rms.parsers.slk.GameObject;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.ui.util.ExceptionPopup;
import com.hiveworkshop.rms.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

public class MDLSnapshot {

	ModelView dispMDL;
	Vec3 cameraPos = new Vec3(0, 0, 0);
	double zoom = 1;
	boolean enabled = false;

	boolean texLoaded = false;

	JCheckBox wireframe;
	HashMap<Bitmap, Integer> textureMap = new HashMap<>();

	Class<? extends Throwable> lastThrownErrorClass;
	private final ProgramPreferences programPreferences;

	public MDLSnapshot(final ModelView dispMDL, final int width, final int height,
			final ProgramPreferences programPreferences) throws LWJGLException {
		this.dispMDL = dispMDL;
		this.width = width;
		this.height = height;
		this.programPreferences = programPreferences;
	}

	public void setCameraPosition(final Vec3 cameraPos) {
		this.cameraPos = cameraPos;
	}

	public void setYangle(final float yangle) {
		this.yangle = yangle;
	}

	public void setXangle(final float xangle) {
		this.xangle = xangle;
	}

	public void setZoom(final double zoom) {
		this.zoom = zoom;
	}

	public void setWireframeHandler(final JCheckBox nwireframe) {
		wireframe = nwireframe;
	}

	private final float[] whiteDiffuse = { 1f, 1f, 1f, 1f };
	private final float[] posSun = { 0.0f, 10.0f, 0.0f, 1.0f };

	boolean wantReload = false;

	public void reloadTextures() {
		wantReload = true;
    }

    boolean wantReloadAll = false;
    private final int width;
    private final int height;

    public void reloadAllTextures() {
        wantReloadAll = true;
    }

    public static ModelViewManager createDefaultDisplay(final GameObject unit) {
        final ModelViewManager mdlDisplay;
        final EditableModel model;
        try {
            String field = unit.getField("file");
            if (field.endsWith(".mdl")) {
                field = field.replace(".mdl", ".mdx");
            } else {
                field += ".mdx";
            }
            model = new EditableModel(
                    MdxUtils.loadMdlx(GameDataFileSystem.getDefault().getResourceAsStream(field)));
            mdlDisplay = new ModelViewManager(model);

            Animation bestStandAnim = null;
            for (final Animation anim : model.getAnims()) {
                if (anim.getName().toLowerCase().contains("stand")) {
                    final String animProps = unit.getField("Animprops");
                    // should not be case sensitive!
                    final String[] animationNames = animProps.split(",");
                    boolean isGoodAnimation = true;
                    for (final String name : animationNames) {
                        if (!anim.getName().toLowerCase().contains(name.toLowerCase())) {
                            isGoodAnimation = false;
                            break;
                        }
                    }
                    if (isGoodAnimation && ((bestStandAnim == null)
                            || (anim.getName().length() < bestStandAnim.getName().length()))) {
                        bestStandAnim = anim;
                    }
                }
            }
            if (bestStandAnim != null) {
                for (final Geoset geo : model.getGeosets()) {
                    final AnimFlag visibilityFlag = geo.getVisibilityFlag();
                    if (visibilityFlag != null) {
                        for (int i = 0; i < visibilityFlag.size(); i++) {
                            final Entry entry = visibilityFlag.getEntry(i);
                            if ((entry.time == bestStandAnim.getStart()) && (((Number) entry.value).intValue() == 0)) {
                                mdlDisplay.makeGeosetNotEditable(geo);
                                mdlDisplay.makeGeosetNotVisible(geo);
                            }
                        }
                    }
                }
            }
            return mdlDisplay;
        } catch (final IOException e1) {
            throw new RuntimeException(e1);
        }
    }

    public static int loadTexture(final BufferedImage image) {

        final int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        final ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL);
        // 4 for RGBA, 3 for RGB

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                final int pixel = pixels[(y * image.getWidth()) + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green component
                buffer.put((byte) (pixel & 0xFF)); // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component.
                // Only for RGBA
            }
        }

        buffer.flip(); // FOR THE LOVE OF GOD DO NOT FORGET THIS

        // You now have a ByteBuffer filled with the color data of each pixel.
        // Now just create a texture ID and bind it. Then you can load it using
        // whatever OpenGL method you want, for example:

        final int textureID = GL11.glGenTextures(); // Generate texture ID
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); // Bind texture ID

        // Setup wrap mode
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        // Setup texture scaling filtering
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        // Send texel data to OpenGL
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE, buffer);

        // Return the texture ID so we can bind it later again
        return textureID;
    }

    public void forceReloadTextures() {
        texLoaded = true;

        for (final Geoset geo : dispMDL.getModel().getGeosets()) {
            for (int i = 0; i < geo.getMaterial().getLayers().size(); i++) {
                final Layer layer = geo.getMaterial().getLayers().get(i);
                final Bitmap tex = layer.firstTexture();
                if (textureMap.get(tex) == null) {
                    String path = tex.getPath();
                    if (path.length() == 0) {
                        if (tex.getReplaceableId() == 1) {
                            path = "ReplaceableTextures\\TeamColor\\TeamColor00";
                        } else if (tex.getReplaceableId() == 2) {
                            path = "ReplaceableTextures\\TeamGlow\\TeamGlow00";
                        }
                    } else {
                        path = path.substring(0, path.length() - 4);
                    }
                    tryReadTexture(layer, tex, path);
                }
			}
		}
	}

	public void addGeosets(final List<Geoset> geosets) {
		for (final Geoset geo : geosets) {// .getModel().getGeosets()
			for (int i = 0; i < geo.getMaterial().getLayers().size(); i++) {
				final Layer layer = geo.getMaterial().getLayers().get(i);
				final Bitmap tex = layer.firstTexture();
                String path = tex.getPath();
                if (path.length() == 0) {
                    if (tex.getReplaceableId() == 1) {
                        path = "ReplaceableTextures\\TeamColor\\TeamColor00";
                    } else if (tex.getReplaceableId() == 2) {
                        path = "ReplaceableTextures\\TeamGlow\\TeamGlow00";
                    }
                } else {
                    path = path.substring(0, path.length() - 4);
                }
                tryReadTexture(layer, tex, path);

            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void tryReadTexture(Layer layer, Bitmap tex, String path) {
        Integer texture = null;
        try {
            final BufferedImage gameTex = BLPHandler.get().getGameTex(path + ".blp");
            texture = loadTexture(layer, gameTex);
        } catch (final Exception exc) {
            exc.printStackTrace();
            final BufferedImage customTex = BLPHandler.get()
                    .getCustomTex(dispMDL.getModel().getWorkingDirectory().getPath() + "\\" + path + ".blp");
            texture = loadTexture(layer, customTex);
        }
        textureMap.put(tex, texture);
    }

    public void initGL() {
        try {
            if ((programPreferences == null) || programPreferences.textureModels()) {
                texLoaded = true;
                for (final Geoset geo : dispMDL.getModel().getGeosets()) {
                    for (int i = 0; i < geo.getMaterial().getLayers().size(); i++) {
                        final Layer layer = geo.getMaterial().getLayers().get(i);
                        final Bitmap tex = layer.firstTexture();
                        String path = tex.getPath();
                        if (path.length() == 0) {
                            if (tex.getReplaceableId() == 1) {
                                path = "ReplaceableTextures\\TeamColor\\TeamColor00";
                            } else if (tex.getReplaceableId() == 2) {
                                path = "ReplaceableTextures\\TeamGlow\\TeamGlow00";
                            } else {
                                path = "textures\\white";
                            }
                        } else {
                            path = path.substring(0, path.length() - 4);
                        }
                        tryReadTexture(layer, tex, path);
                    }
                }
            }

		} catch (final Throwable e) {
			JOptionPane.showMessageDialog(null, "initGL failed because of this exact reason:\n"
					+ e.getClass().getSimpleName() + ": " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public void zoomToFit() {
		zoomToFit(VertexFilter.IDENTITY);
	}

	public void zoomToFit(final VertexFilter<? super GeosetVertex> filter) {

		setYangle(35);
		final EditableModel model = dispMDL.getModel();
		final List<Vec3> shapeData = new ArrayList<>();
		for (final Geoset geo : dispMDL.getVisibleGeosets()) {
			boolean isOnlyAdditive = true;
			for (final Layer layer : geo.getMaterial().getLayers()) {
                if (!layer.getFilterMode().toString().contains("Add")) {
                    isOnlyAdditive = false;
                    break;
                }
			}
			if (!isOnlyAdditive) {
				for (final GeosetVertex vertex : geo.getVertices()) {
					if (filter.isAccepted(vertex)) {
						shapeData.add(vertex);
					}
				}
			}
		}
		final Vec3 center = Vec3.centerOfGroup(shapeData);
		double maxDistance = 0;
		for (final Vec3 vertex : shapeData) {
			final double distance = vertex.distance(center);
			if (distance > maxDistance) {
				maxDistance = distance;
			}
		}
		final double zoom = 128 / maxDistance;
		setCameraPosition(new Vec3(0, -Math.sqrt(maxDistance) * 1.3, maxDistance));
		setZoom(zoom);
	}

	public BufferedImage getBufferedImage() throws Exception {
		return getBufferedImage(VertexFilter.IDENTITY);
	}

	public BufferedImage getBufferedImage(final VertexFilter<? super GeosetVertex> renderMask) throws Exception {
		System.out.println("Building " + width + "x" + height + " image at zoom=" + zoom + ", cameraPos=" + cameraPos
				+ ", xangle=" + xangle + ", yangle=" + yangle);
		final BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		// paintComponent(image.getGraphics(),5);
		final Pbuffer buffer = new Pbuffer(getWidth(), getHeight(), new PixelFormat(), null, null);
		buffer.makeCurrent();
		final ByteBuffer pixels = ByteBuffer.allocateDirect(getWidth() * getHeight() * 4);
		initGL();
		paintGL(renderMask);
		GL11.glReadPixels(0, 0, getWidth(), getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
		final int[] data = new int[getWidth() * getHeight()];
		pixels.asIntBuffer().get(data);
		for (int i = 0; i < data.length; i++) {
			final int rgba = data[i];
			final int a = rgba & 0xFF;
			data[i] = (rgba >>> 8) | (a << 24);
		}
		image.getRaster().setDataElements(0, 0, getWidth(), getHeight(), data);
		return createFlipped(image);
	}

	public Area getOutline() throws Exception {
		// construct the GeneralPath
		final BufferedImage bi = getBufferedImage();
		final GeneralPath gp = new GeneralPath();

		boolean cont = false;
		final int targetRGB = 0;// new Color(0,0,0,0).getRGB();
		for (int xx = 0; xx < bi.getWidth(); xx++) {
			for (int yy = 0; yy < bi.getHeight(); yy++) {
				if (bi.getRGB(xx, yy) != targetRGB) {
					if (cont) {
						gp.lineTo(xx, yy);
						gp.lineTo(xx, yy + 1);
						gp.lineTo(xx + 1, yy + 1);
						gp.lineTo(xx + 1, yy);
						gp.lineTo(xx, yy);
					} else {
						gp.moveTo(xx, yy);
					}
					cont = true;
				} else {
					cont = false;
				}
			}
			cont = false;
		}
		gp.closePath();

		// construct the Area from the GP & return it
		return new Area(gp);
	}

	private static BufferedImage createFlipped(final BufferedImage image) {
		final AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getScaleInstance(1, -1));
		at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
		return createTransformed(image, at);
	}

	private static BufferedImage createTransformed(final BufferedImage image, final AffineTransform at) {
		final BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = newImage.createGraphics();
		g.transform(at);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}

	boolean initialized = false;
	int current_height;
	int current_width;
	private float xangle;
	private float yangle;
    private boolean drawBackground;

    public boolean renderTextures() {
        return texLoaded && ((programPreferences == null) || programPreferences.textureModels());
    }

    public void paintGL() {
        paintGL(VertexFilter.IDENTITY);
    }

    public void zoomToFitOld() {
        setYangle(35);
        double width = 128;
        double depth = 64;
        final EditableModel model = dispMDL.getModel();
        final ExtLog exts = model.getExtents();
        boolean loadedWidth = false;
        final List<CollisionShape> sortedIdObjects = model.sortedIdObjects(CollisionShape.class);
        double avgWidth = 0;
        int widthItems = 0;
        for (final CollisionShape shape : sortedIdObjects) {
            for (final Vec3 vertex : shape.getVertices()) {
                loadedWidth = true;
                avgWidth += vertex.x;
                widthItems++;
            }
            avgWidth += shape.getPivotPoint().x;
            widthItems++;
        }
        if (loadedWidth) {
            avgWidth /= widthItems;
            double varianceGuy = 0;
            for (final CollisionShape shape : sortedIdObjects) {
                for (final Vec3 vertex : shape.getVertices()) {
                    loadedWidth = true;
                    final double dx = vertex.x - avgWidth;
                    varianceGuy += dx * dx;
                }
                final double dx = shape.getPivotPoint().x - avgWidth;
                varianceGuy += dx * dx;
            }
            varianceGuy /= widthItems;
            width = Math.sqrt(varianceGuy) * 6;
        }
        if (!loadedWidth && (exts != null) && (exts.getMaximumExtent() != null) && (exts.getMinimumExtent() != null)) {
            width = (exts.getMaximumExtent().x) / 2;
            depth = (exts.getMaximumExtent().y) / 3;
            loadedWidth = true;
        }
        setCameraPosition(new Vec3(0, -20, width));
        setZoom(Math.min(1,
                ((exts == null) && (exts.getBoundsRadius() > 0)) ? (128 / width) : (32 / exts.getBoundsRadius())));
    }

    public double convertX(final double x) {
        return ((x + cameraPos.x) * zoom) + (getWidth() / 2.0);
    }

    public double convertY(final double y) {
        return ((-y + cameraPos.y) * zoom) + (getHeight() / 2.0);
    }

    public double geomX(final double x) {
        return ((x - (getWidth() / 2.0)) / zoom) - cameraPos.x;
    }

    public double geomY(final double y) {
        return -(((y - (getHeight() / 2.0)) / zoom) - cameraPos.y);
    }

    public Rectangle2D.Double pointsToGeomRect(final Point a, final Point b) {
        final Point2D.Double topLeft = new Point2D.Double(Math.min(geomX(a.x), geomX(b.x)),
                Math.min(geomY(a.y), geomY(b.y)));
        final Point2D.Double lowRight = new Point2D.Double(Math.max(geomX(a.x), geomX(b.x)),
                Math.max(geomY(a.y), geomY(b.y)));
        return new Rectangle2D.Double(topLeft.x, topLeft.y, (lowRight.x - (topLeft.x)),
                ((lowRight.y) - (topLeft.y)));
    }

    public Rectangle2D.Double pointsToRect(final Point a, final Point b) {
        final Point2D.Double topLeft = new Point2D.Double(Math.min((a.x), (b.x)), Math.min((a.y), (b.y)));
        final Point2D.Double lowRight = new Point2D.Double(Math.max((a.x), (b.x)), Math.max((a.y), (b.y)));
        return new Rectangle2D.Double(topLeft.x, topLeft.y, (lowRight.x - (topLeft.x)),
                ((lowRight.y) - (topLeft.y)));
    }

    public static int loadTexture(final Layer layer, final BufferedImage image) {
        return loadTexture(image);
    }

    private static final int BYTES_PER_PIXEL = 4;

    public void paintGL(final VertexFilter<? super GeosetVertex> renderMask) {
        // setSize(getParent().getSize());
        if (wantReloadAll) {
            wantReloadAll = false;
            wantReload = false;// If we just reloaded all, no need to reload
            // some.
            try {
                initGL();// Re-overwrite textures
            } catch (final Exception e) {
				e.printStackTrace();
				ExceptionPopup.display("Error loading textures:", e);
			}
		} else if (wantReload) {
			wantReload = false;
			try {
				forceReloadTextures();
			} catch (final Exception e) {
				e.printStackTrace();
				ExceptionPopup.display("Error loading new texture:", e);
			}
		} else if (!texLoaded && ((programPreferences == null) || programPreferences.textureModels())) {
			forceReloadTextures();
			texLoaded = true;
		}
		try {
			if ((getWidth() != current_width) || (getHeight() != current_height)) {
				current_width = getWidth();
				current_height = getHeight();
				glViewport(0, 0, current_width, current_height);
			}
			if ((programPreferences != null) && (programPreferences.viewMode() == 0)) {
				glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			} else if ((programPreferences == null) || (programPreferences.viewMode() == 1)) {
				glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			}
			glViewport(0, 0, getWidth(), getHeight());
			glEnable(GL_DEPTH_TEST);

			GL11.glDepthFunc(GL11.GL_LEQUAL);
			GL11.glDepthMask(true);
			glEnable(GL_COLOR_MATERIAL);
			glEnable(GL_LIGHTING);
			glEnable(GL_LIGHT0);
			glEnable(GL_LIGHT1);
			glEnable(GL_NORMALIZE);
			GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
			// System.out.println("max:
			// "+GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE));
			if (renderTextures()) {
				glEnable(GL11.GL_TEXTURE_2D);
			}
			GL11.glEnable(GL11.GL_BLEND);
			if (drawBackground) {
				glClearColor(0.3137254901960784f, 0.3137254901960784f, 0.3137254901960784f, 1.0f);
			} else {
				glClearColor(0f, 0f, 0f, 0f);
			}
			// glClearColor(0f, 0f, 0f, 1.0f);
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			gluPerspective(45f, (float) current_width / (float) current_height, 1.0f, 1500.0f);
			// GLU.gluOrtho2D(45f, (float)current_width/(float)current_height,
			// 1.0f, 600.0f);
			// glRotatef(angle, 0, 0, 0);
			// glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
			// gluOrtho2D(0.0f, (float) getWidth(), 0.0f, (float) getHeight());
			// GLU.
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			// GL11.glShadeModel(GL11.GL_SMOOTH);

			glTranslatef(0f + (cameraPos.x * (float) zoom), -70f - (cameraPos.y * (float) zoom),
					-200f - (cameraPos.z * (float) zoom));
			glRotatef(yangle, 1f, 0f, 0f);
			glRotatef(xangle, 0f, 1f, 0f);
			glScalef((float) zoom, (float) zoom, (float) zoom);

			final FloatBuffer ambientColor = BufferUtils.createFloatBuffer(4);
			ambientColor.put(0.2f).put(0.2f).put(0.2f).put(1f).flip();
			// float [] ambientColor = {0.2f, 0.2f, 0.2f, 1f};
			// FloatBuffer buffer =
			// ByteBuffer.allocateDirect(ambientColor.length*8).asFloatBuffer();
			// buffer.put(ambientColor).flip();
			glLightModel(GL_LIGHT_MODEL_AMBIENT, ambientColor);

			final FloatBuffer lightColor0 = BufferUtils.createFloatBuffer(4);
			lightColor0.put(0.5f).put(0.5f).put(0.5f).put(1f).flip();
			final FloatBuffer lightPos0 = BufferUtils.createFloatBuffer(4);
			lightPos0.put(40.0f).put(100.0f).put(80.0f).put(1f).flip();
			glLight(GL_LIGHT0, GL_DIFFUSE, lightColor0);
			glLight(GL_LIGHT0, GL_POSITION, lightPos0);

			final FloatBuffer lightColor1 = BufferUtils.createFloatBuffer(4);
			lightColor1.put(0.2f).put(0.2f).put(0.2f).put(1f).flip();
			final FloatBuffer lightPos1 = BufferUtils.createFloatBuffer(4);
			lightPos1.put(-100.0f).put(100.5f).put(0.5f).put(1f).flip();

			glLight(GL_LIGHT1, GL_DIFFUSE, lightColor1);
			glLight(GL_LIGHT1, GL_POSITION, lightPos1);

			// glColor3f(1f,1f,0f);
			// glColorMaterial ( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE ) ;
			// glEnable(GL_COLOR_MATERIAL);
			glColor4f(0.5882352941176471f, 0.5882352941176471f, 1f, 0.3f);
			// glPushMatrix();
			// glTranslatef(getWidth() / 2.0f, getHeight() / 2.0f, 0.0f);
			// glRotatef(2*angle, 0f, 0f, -1.0f);
			// glRectf(-50.0f, -50.0f, 50.0f, 50.0f);
			for (final Geoset geo : dispMDL.getVisibleGeosets()) {// .getModel().getGeosets()
				if (!dispMDL.getEditableGeosets().contains(geo) && (dispMDL.getHighlightedGeoset() != geo)) {
					for (int i = 0; i < geo.getMaterial().getLayers().size(); i++) {
						final Layer layer = geo.getMaterial().getLayers().get(i);
						final Bitmap tex = layer.firstTexture();
						final Integer texture = textureMap.get(tex);
						if (texture != null) {
							// texture.bind();
							GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
							if (renderTextures()) {
								GL11.glEnable(GL11.GL_TEXTURE_2D);
							}
							GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
						} else {
							GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_COLOR);
							GL11.glDisable(GL11.GL_TEXTURE_2D);
						}
						if (layer.getFilterMode() == FilterMode.ADDITIVE) {
							// GL11.glDisable(GL11.GL_DEPTH_TEST);
							GL11.glDepthMask(false);
							GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
							// GL11.glBlendFunc(GL11.GL_SRC_ALPHA,
							// GL11.GL_ONE_MINUS_SRC_ALPHA);
						} else if (layer.getFilterMode() == FilterMode.ADDALPHA) {
							// GL11.glDisable(GL11.GL_DEPTH_TEST);
							GL11.glDepthMask(false);
							GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
						} else {
							// GL11.glEnable(GL11.GL_DEPTH_TEST);
							GL11.glDepthMask(true);
							GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						}
						glBegin(GL11.GL_TRIANGLES);
						for (final Triangle tri : geo.getTriangles()) {
							for (final GeosetVertex v : tri.getVerts()) {
								if (renderMask.isAccepted(v)) {
									if (v.getNormal() != null) {
										GL11.glNormal3f(v.getNormal().y, v.getNormal().z,
												v.getNormal().x);
									}
									GL11.glTexCoord2f(
											v.getTverts().get(v.getTverts().size() - 1 - layer.getCoordId()).x,
											v.getTverts().get(v.getTverts().size() - 1 - layer.getCoordId()).y);
									GL11.glVertex3f(v.y / 1.0f, v.z / 1.0f, v.x / 1.0f);
								}
							}
						}

						glEnd();
					}
				}
			}
			glColor3f(2f, 2f, 2f);
			for (final Geoset geo : dispMDL.getEditableGeosets()) {// .getModel().getGeosets()
				if (dispMDL.getHighlightedGeoset() != geo) {
					for (int i = 0; i < geo.getMaterial().getLayers().size(); i++) {
						final Layer layer = geo.getMaterial().getLayers().get(i);
						final Bitmap tex = layer.firstTexture();
						final Integer texture = textureMap.get(tex);
						if (texture != null) {
							// texture.bind();
							GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
						}
						if (layer.getFilterMode() == FilterMode.ADDITIVE) {
							// GL11.glDisable(GL11.GL_DEPTH_TEST);
							GL11.glDepthMask(false);
							// GL11.glBlendFunc(GL11.GL_SRC_ALPHA,
							// GL11.GL_ONE_MINUS_SRC_ALPHA);
							GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
						} else if (layer.getFilterMode() == FilterMode.ADDALPHA) {
							// GL11.glDisable(GL11.GL_DEPTH_TEST);
							GL11.glDepthMask(false);
							GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
						} else {
							// GL11.glEnable(GL11.GL_DEPTH_TEST);
							GL11.glDepthMask(true);
							GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						}
						glBegin(GL11.GL_TRIANGLES);
						for (final Triangle tri : geo.getTriangles()) {
							for (final GeosetVertex v : tri.getVerts()) {
								if (renderMask.isAccepted(v)) {
									GL11.glNormal3f(v.getNormal().y, v.getNormal().z,
											v.getNormal().x);
									GL11.glTexCoord2f(
											v.getTverts().get(v.getTverts().size() - 1 - layer.getCoordId()).x,
											v.getTverts().get(v.getTverts().size() - 1 - layer.getCoordId()).y);
									GL11.glVertex3f(v.y / 1.0f, v.z / 1.0f, v.x / 1.0f);
								}
							}
						}

                        glEnd();
					}
				}
			}
			GL11.glDepthMask(true);

            if (dispMDL.getHighlightedGeoset() != null) {

                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_COLOR);
				GL11.glDisable(GL11.GL_TEXTURE_2D);

                glColor3f(1f, 3f, 1f);
				glBegin(GL11.GL_TRIANGLES);
				for (final Triangle tri : dispMDL.getHighlightedGeoset().getTriangles()) {
					for (final GeosetVertex v : tri.getVerts()) {
						if (renderMask.isAccepted(v)) {
							GL11.glNormal3f(v.getNormal().y, v.getNormal().z, v.getNormal().x);
							GL11.glTexCoord2f(v.getTverts().get(0).x, v.getTverts().get(0).y);
							GL11.glVertex3f(v.y / 1.0f, v.z / 1.0f, v.x / 1.0f);
						}
					}
				}
				glEnd();
				// }
			}

			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			if ((programPreferences != null) && programPreferences.showNormals()) {
				glBegin(GL11.GL_LINES);
				glColor3f(1f, 1f, 3f);
				// if( wireframe.isSelected() )
				for (final Geoset geo : dispMDL.getVisibleGeosets()) {// .getModel().getGeosets()
					for (final Triangle tri : geo.getTriangles()) {
						for (final GeosetVertex v : tri.getVerts()) {
							if (renderMask.isAccepted(v)) {
                                GL11.glNormal3f(v.getNormal().y, v.getNormal().z, v.getNormal().x);
                                GL11.glVertex3f(v.y / 1.0f, v.z / 1.0f, v.x / 1.0f);

                                GL11.glNormal3f(v.getNormal().y, v.getNormal().z, v.getNormal().x);
                                GL11.glVertex3f((v.y / 1.0f) + (float) ((v.getNormal().y * 6) / zoom),
                                        (v.z / 1.0f) + (float) ((v.getNormal().z * 6) / zoom),
                                        (v.x / 1.0f) + (float) ((v.getNormal().x * 6) / zoom));
                            }
						}
					}
				}

				glEnd();
				// GL11.glDisable(GL11.GL_BLEND);
			}

			// glPopMatrix();
		} catch (final Throwable e) {
			if ((lastThrownErrorClass == null) || (lastThrownErrorClass != e.getClass())) {
				lastThrownErrorClass = e.getClass();
				JOptionPane.showMessageDialog(null, "Rendering failed because of this exact reason:\n"
						+ e.getClass().getSimpleName() + ": " + e.getMessage());
			}
			throw new RuntimeException(e);
		}
	}
}
