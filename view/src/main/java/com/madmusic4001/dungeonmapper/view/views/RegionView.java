/**
 * Copyright (C) 2014 MadMusic4001
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.madmusic4001.dungeonmapper.view.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.controller.events.cell.CellPersistenceEvent;
import com.madmusic4001.dungeonmapper.controller.events.region.RegionSelectedEvent;
import com.madmusic4001.dungeonmapper.controller.managers.CellExitManager;
import com.madmusic4001.dungeonmapper.controller.managers.TerrainManager;
import com.madmusic4001.dungeonmapper.data.entity.Cell;
import com.madmusic4001.dungeonmapper.data.entity.CellExitType;
import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;
import com.madmusic4001.dungeonmapper.data.entity.World;
import com.madmusic4001.dungeonmapper.data.util.BitmapPacker;
import com.madmusic4001.dungeonmapper.data.util.DataConstants;
import com.madmusic4001.dungeonmapper.view.activities.editWorld.EditWorldActivity;
import com.madmusic4001.dungeonmapper.view.di.components.ViewComponent;
import com.madmusic4001.dungeonmapper.view.di.modules.ViewModule;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import javax.inject.Inject;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.madmusic4001.dungeonmapper.data.util.DataConstants.DOWN;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.Direction;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.EAST;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.NORTH;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.NORTHEAST;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.NUM_EXIT_DIRECTIONS;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.OriginLocation;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.SOUTH;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.SOUTHEAST;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.SOUTHWEST;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.UP;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.WEST;

/**
 * Draws a map of a region.
 */
public class RegionView extends GLSurfaceView {
	private static final int   FLOAT_SIZE           = 4;
	private static final int   SHORT_SIZE           = 2;
	private static final float FLOAT_ZERO           = 0.0f;
	private static final int   CELL_SIZE_DP         = 48;
	private static final int   CELL_EXIT_SIZE_DP    = CELL_SIZE_DP / 4;
	private static final int   GRID_LINE_WIDTH_DP   = 2;
	private static final int   BORDER_LINE_WIDTH_DP = 4;
	private static final int   TERRAIN_GL_NUM       = 0;
	private static final int   CELL_EXITS_GL_NUM    = 1;
	private static final int   GRID_GL_NUM          = 2;
	private static final int   BYTES_PER_FLOAT      = 4;
	private static final int   BYTES_PER_SHORT      = 2;

	@Inject
	protected CellExitManager cellExitManger;
	@Inject
	protected TerrainManager  terrainManager;
	@Inject
	protected EventBus eventBus;

	// Domain objects
	private Region      region;
	// render options
	public  MapRenderer mapRenderer;
	private boolean showGrid             = true;
	private boolean useDottedLineForGrid = true;
	private Paint borderPaint;
	private int   backgroundColor;
	private Paint gridPaint;

	// behavior options
	private Terrain currentTerrain;
	private SparseArray<CellExitType> currentCellExitsMap = new SparseArray<>();
	private boolean                   stickyTerrain       = true;
	private SparseBooleanArray        stickyCellExitsMap  = new SparseBooleanArray();

	// Pixel sizes (calculated when render options or device config changes)
	private DisplayMetrics displayMetrics;
	private float          cellSizePixels;
	private float          gridLineSizePixels;
	private float          borderLineSizePixels;
	private int            totalContentWidth;
	private int            totalContentHeight;

	// Registered listeners
	private OnTerrainChangedListener           terrainChangedListener = null;
	private SparseArray<OnExitChangedListener> exitChangedListenerMap = new SparseArray<>();
	private OnCellChangedListener              cellChangedListener    = null;

	// Matrices
	private final float[] matrixProjection        = new float[16];
	private final float[] matrixView              = new float[16];
	private final float[] matrixProjectionAndView = new float[16];

	// GL variables
	boolean surfaceCreated = false;
	int   vertexAttribHandle;
	int   textureCoordsHandle;
	int   matrixHandle;
	int   textureSamplerHandle;
	int[] textureNames;
	int[] vboNames;
	int[] iboNames;

	// Geometric variables
	private SparseArray<Rect>   terrainTextureRegions  = new SparseArray<>(30);
	private SparseArray<Rect[]> cellExitTextureRegions = new SparseArray<>(60);
	short              numCells = 0;
	SparseArray<Short> cellMap  = new SparseArray<>();
	float[]     gridVertices;
	FloatBuffer bkgrndVerticesBuffer;
	short[]     bkgrndIndices;
	ShortBuffer bkgrndIndicesBuffer;

	float[]     cellVertices;
	FloatBuffer cellVerticesBuffer;
	short[]     cellIndices;
	ShortBuffer cellIndicesBuffer;

	float[]     cellExitVertices;
	FloatBuffer cellExitVerticesBuffer;
	short[]     cellExitIndices;
	ShortBuffer cellExitIndicesBuffer;

	private GestureDetector detector;

	/**
	 * Creates a new RegionView with a reference to the context and the given attributes.
	 *
	 * @param context  the android context
	 * @param attrs  the view attributes for the view
	 */
	public RegionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		ViewComponent viewComponent = ((EditWorldActivity) context).getActivityComponent()
				.newViewComponent(new ViewModule(this));
		viewComponent.injectInto(this);
		eventBus.register(this);

		initView();
	}

	// <editor-fold desc="View lifecycle event handlers">
	//*******************************************************************************************
	// View lifecycle event handlers
	//*******************************************************************************************

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int desiredViewWidth = totalContentWidth + getPaddingLeft() + getPaddingRight();
		int desiredViewHeight = totalContentHeight + getPaddingTop() + getPaddingBottom();
		int measuredWidth = View.resolveSizeAndState(desiredViewWidth, widthMeasureSpec, 1);
		int measuredHeight = View.resolveSizeAndState(desiredViewHeight, heightMeasureSpec, 1);

		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	@Override
	public boolean onTouchEvent(@NonNull MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	// </editor-fold>

	// <editor-fold desc="Action methods">
	//*******************************************************************************************
	// Public action methods
	//*******************************************************************************************

	/**
	 * Sets the new map, recalculates various information and recreates the openGL vertexes
	 * information to draw the new map with the correct textures for each cell.
	 *
	 * @param newRegion the newMap
	 */
	public void setRegion(Region newRegion) {
		if (newRegion != this.region) {
			Region oldRegion = this.region;
			region = newRegion;
			boolean createGrid = false;
			if (oldRegion == null || oldRegion.getWidth() != newRegion.getWidth() ||
					oldRegion.getHeight() != newRegion.getHeight()) {
				createGrid = true;
				calcTotalContentSize();
				ViewGroup.LayoutParams params = getLayoutParams();
				params.width = totalContentWidth;
				params.height = totalContentHeight;
				requestLayout();
			}
			final boolean finalCreateGrid = createGrid;
			if (surfaceCreated) {
				queueEvent(new Runnable() {
					@Override
					public void run() {
						if (finalCreateGrid) {
							mapRenderer.createBkgrndTexture();
						}
						mapRenderer.setupCellTextures();
						requestRender();
					}
				});
			}
		}
	}

	/**
	 * Adds or updates a cell to render
	 *
	 * @param cell the MapCell to insert or update.
	 */
	public void updateCell(final Cell cell) {
		queueEvent(new Runnable() {
			@Override
			public void run() {
				eventBus.post(new CellPersistenceEvent(CellPersistenceEvent.Operation.SAVE, cell, null));
				mapRenderer.createSpritesForCell(cell, true);
				requestRender();
			}
		});
	}
	// </editor-fold>

	// <editor-fold desc="Getters and setters">
	//*******************************************************************************************
	// Getters and setters
	//*******************************************************************************************

	public boolean isShowGrid() {
		return showGrid;
	}

	/**
	 * Sets whether or not to draw a grid for the cells.
	 *
	 * @param showGrid true to draw a grid, false to not draw a grid.
	 */
	public void setShowGrid(boolean showGrid) {
		if (this.showGrid != showGrid) {
			if (showGrid) {
				gridLineSizePixels = dipToPixels(GRID_LINE_WIDTH_DP);
			}
			else {
				gridLineSizePixels = 0;
			}
			this.showGrid = showGrid;
			calcTotalContentSize();
			requestLayout();
		}
	}

	public boolean isUseDottedLineForGrid() {
		return useDottedLineForGrid;
	}

	/**
	 * Sets whether or not to draw the grid with a dotted line.
	 *
	 * @param useDottedLineForGrid true to draw grid with dotted lines,
	 *                             false to draw grid with solid lines.
	 */
	public void setUseDottedLineForGrid(boolean useDottedLineForGrid) {
		if (this.useDottedLineForGrid != useDottedLineForGrid) {
			this.useDottedLineForGrid = useDottedLineForGrid;
			queueEvent(new Runnable() {
				@Override
				public void run() {
					mapRenderer.createBkgrndTexture();
				}
			});
			requestRender();
		}
	}

	public void fillRegion() {
		boolean cellChanged;
		boolean isNew;

		for(int x = 0; x < region.getWidth(); x++) {
			for(int y = 0; y < region.getHeight(); y++) {
				Cell cell = region.getCell(x, y);
				cellChanged = false;
				isNew = false;
				if (cell == null) {
					cell = new Cell();
					cell.setX(x);
					cell.setY(y);
					region.putCell(cell);
					cell.setParent(region);
					isNew = true;
				}

				if (!currentTerrain.equals(cell.getTerrain()) || isNew) {
					cell.setTerrain(currentTerrain);
					cellChanged = true;
				}

				for (@Direction int direction = UP; direction <= DOWN; direction++) {
					CellExitType mapCellExitType = cell.getExitForDirection(direction);
					CellExitType currentExit = currentCellExitsMap.get(direction);
					if (!currentExit.equals(mapCellExitType) || isNew) {
						cell.setExitForDirection(direction, currentExit);
						cellChanged = true;
					}
				}

				if(cellChanged) {
					final Cell finalCell = cell;
					queueEvent(new Runnable() {
						@Override
						public void run() {
							eventBus.post(new CellPersistenceEvent(CellPersistenceEvent.Operation.SAVE, finalCell, null));
							mapRenderer.createSpritesForCell(finalCell, true);
						}
					});
				}
			}
		}

		queueEvent(new Runnable() {
			@Override
			public void run() {
				requestRender();
			}
		});
	}

	public void setStickyTerrain(boolean stickyTerrain) {
		this.stickyTerrain = stickyTerrain;
	}

	public void setStickyCellExit(@DataConstants.Direction int direction, boolean sticky) {
		stickyCellExitsMap.put(direction, sticky);
	}

	public void setCurrentTerrain(Terrain currentTerrain) {
		this.currentTerrain = currentTerrain;
	}

	public void setTerrainChangedListener(OnTerrainChangedListener terrainChangedListener) {
		this.terrainChangedListener = terrainChangedListener;
	}

	public void setCurrentCellExit(@DataConstants.Direction int direction, CellExitType cellExitType) {
		currentCellExitsMap.put(direction, cellExitType);
	}

	public void setOnExitChangedListener(@DataConstants.Direction int direction,
										 OnExitChangedListener listener) {
		exitChangedListenerMap.put(direction, listener);
	}

	public void setCellChangedListener(OnCellChangedListener cellChangedListener) {
		this.cellChangedListener = cellChangedListener;
	}
	// </editor-fold>

	// <editor-fold desc="Interface declarations">
	//**********************************************************************************************
	// Interface declarations
	//**********************************************************************************************

	/**
	 * Interface defining a method that will be called when a {@code CellExit} is changed.
	 */
	public interface OnExitChangedListener {
		/**
		 * Called when an exit style changes.
		 *
		 * @param cellExitType the new selected cellExit.
		 */
		void onExitChanged(CellExitType cellExitType);
	}

	/**
	 * Interface defining a method that will be called when the {@code Terrain} is changed.
	 */
	public interface OnTerrainChangedListener {
		/**
		 * Called when the terrain style changes.
		 *
		 * @param terrain the new selected terrain.
		 */
		void onTerrainChanged(Terrain terrain);
	}

	/**
	 * Interface defining a method that will be called when a {@code MapCell} is changed.
	 */
	public interface OnCellChangedListener {
		/**
		 * Called when any {@code MapCell} is changed.
		 *
		 * @param region the {@code Map} instance containing the cell that changed.
		 * @param xCoordinate the x coordinate of the {@code MapCell} that changed.
		 * @param yCoordinate the y coordinate of the {@code MapCell} that changed.
		 */
		void onCellChanged(Region region, int xCoordinate, int yCoordinate);
	}
	// </editor-fold>

	//*******************************************************************************************
	// Local classes
	//*******************************************************************************************

	private class MapViewGestureListener extends GestureDetector.SimpleOnGestureListener {
		private static final int ESTIMATED_TOAST_HEIGHT_DIPS = 48;

		public boolean onSingleTapConfirmed(MotionEvent event) {
			boolean cellChanged = false;
			boolean isNew = false;
			Point cellCoordinates = getCellFromScreenCoordinates(event.getX(), event.getY());
			Cell cell = region.getCell(cellCoordinates.x, cellCoordinates.y);
			if (cell == null) {
				cell = new Cell();
				cell.setX(cellCoordinates.x);
				cell.setY(cellCoordinates.y);
				region.putCell(cell);
				cell.setParent(region);
				isNew = true;
			}

			if (stickyTerrain && !currentTerrain.equals(cell.getTerrain()) || isNew) {
				cell.setTerrain(currentTerrain);
				cellChanged = true;
			}
			else if (cell.getTerrain() != null && cell.getTerrain() != currentTerrain) {
				currentTerrain = cell.getTerrain();
				if (terrainChangedListener != null) {
					terrainChangedListener.onTerrainChanged(currentTerrain);
				}
			}

			for (@Direction int direction = UP; direction <= DOWN; direction++) {
				CellExitType mapCellExitType = cell.getExitForDirection(direction);
				CellExitType currentExit = currentCellExitsMap.get(direction);
				boolean sticky = stickyCellExitsMap.get(direction);
				if (sticky && !currentExit.equals(mapCellExitType) || isNew) {
					cell.setExitForDirection(direction, currentExit);
					cellChanged = true;
				}
				else if (mapCellExitType != null && !mapCellExitType.equals(currentExit)) {
					currentCellExitsMap.put(direction, mapCellExitType);
					OnExitChangedListener listener = exitChangedListenerMap.get(direction);
					if (listener != null) {
						listener.onExitChanged(mapCellExitType);
					}
				}
			}

			if(cellChanged) {
				updateCell(cell);
				if (cellChangedListener != null) {
					cellChangedListener.onCellChanged(region, cellCoordinates.x, cellCoordinates.y);
				}
			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent event) {
			Log.d(((Object) this).getClass().getName(), "In onLongPress...");
			Point cellCoordinates = getCellFromScreenCoordinates(event.getX(), event.getY());
			Cell cell = region.getCell(cellCoordinates.x, cellCoordinates.y);
			Terrain terrain = (cell != null ? cell.getTerrain() : null);
			String terrainName;
			if (terrain != null) {
				terrainName = terrain.getDisplayName();
			}
			else {
				terrainName = getResources().getString(R.string.message_noTerrainSet);
			}

			// Initial cell array offset assumes origin is top left.Flip the offset based on the
			// map sizes and the user orientation
			// preference for this world
			World world = region.getParent();
			@OriginLocation int regionOrientation = world.getOriginLocation();
			if (NORTHEAST == regionOrientation || SOUTHEAST == regionOrientation) {
				cellCoordinates.x = (world.getRegionWidth() - 1) - cellCoordinates.x;
			}
			if (SOUTHWEST == regionOrientation || SOUTHEAST == regionOrientation) {
				cellCoordinates.y = (world.getRegionHeight() - 1) - cellCoordinates.y;
			}
			cellCoordinates.x += world.getOriginOffset();
			cellCoordinates.y += world.getOriginOffset();
			String toolTipText = String.format(
					getResources().getString(R.string.message_regionViewToolTip),
					cellCoordinates.x, cellCoordinates.y, terrainName);
			showToolTip(toolTipText, event.getX(), event.getY());
		}

		private boolean showToolTip(final CharSequence text, final float viewX, final float
				viewY) {
			final int[] screenPos = new int[2]; // origin is device display
			final Rect displayFrame = new Rect(); // includes decorations (e.g. status bar)
			final int screenX, screenY;

			getLocationOnScreen(screenPos);
			screenX = (int) viewX + screenPos[0];
			screenY = (int) viewY + screenPos[1];
			getWindowVisibleDisplayFrame(displayFrame);

			final int viewWidth = getWidth();
			final int viewHeight = getHeight();
			final int viewCenterX = screenPos[0] + viewWidth / 2;
			final int screenWidth = getResources().getDisplayMetrics().widthPixels;
			final int estimatedToastHeight = (int) (ESTIMATED_TOAST_HEIGHT_DIPS
					* getResources().getDisplayMetrics().density);

			final Handler handler = new Handler(getContext().getMainLooper());
			handler.post(new Runnable() {
				@Override
				public void run() {
					final Toast toolTip = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
					boolean showBelow = screenPos[1] < estimatedToastHeight;
					if (showBelow) {
						// Show below
						// Offsets are after decorations (e.g. status bar) are factored in
						toolTip.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,
										   viewCenterX - screenWidth / 2,
										   screenPos[1] - displayFrame.top + viewHeight);
					}
					else {
						// Show above
						// Offsets are after decorations (e.g. status bar) are factored in
						// NOTE: We can't use Gravity.BOTTOM because when the keyboard is up
						// its height isn't factored in.
						toolTip.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,
										   viewCenterX - screenWidth / 2,
										   screenPos[1] - displayFrame.top - estimatedToastHeight);
					}
					toolTip.setGravity(Gravity.TOP | Gravity.START, screenX, screenY);

					toolTip.show();
				}
			});
			return true;
		}
	}

	private class MapRenderer implements Renderer {
		private static final int NUM_VERTICES_PER_CELL = 4;
		private static final int NUM_POSITION_COORDS_PER_VERTEX = 3;
		private static final int NUM_TEXTURE_COORDS_PER_VERTEX = 2;
		private static final int NUM_INDICES_PER_CELL = 6;

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			generateGLNames();
			createShaders();
			surfaceCreated = true;
			GLES20.glClearColor(0.0f, 0.5f, 0.0f, 0.5f);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			GLES20.glViewport(0, 0, width, height);
			setMatrices();
			createBkgrndTexture();
			initializeTextures();
			setupCellTextures();
		}

		FPSCounter counter = new FPSCounter();

		@Override
		public void onDrawFrame(GL10 gl) {
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

			// Draw pre-rendered background texture
			drawTextured(bkgrndVerticesBuffer, bkgrndIndicesBuffer, 6, GRID_GL_NUM, false);
			// Draw terrains
			drawVBO(numCells * 6, TERRAIN_GL_NUM, false);
			// Draw cell exits
			drawVBO(numCells * NUM_EXIT_DIRECTIONS * 6, CELL_EXITS_GL_NUM, true);
			counter.logFrame();
		}

		public class FPSCounter {
			long startTime = System.nanoTime();
			int  frames    = 0;

			public void logFrame() {
				frames++;
				if (System.nanoTime() - startTime >= 10000000000L) {
					Log.d("FPSCounter", "fps: " + frames / 10);
					frames = 0;
					startTime = System.nanoTime();
				}
			}
		}

		private void drawTextured(FloatBuffer vertices, ShortBuffer indices, int numIndices,
								  int textureUnitNumber, boolean useBlending) {
			if (useBlending) {
				GLES20.glEnable(GLES20.GL_BLEND);
				GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			}
			else {
				GLES20.glDisable(GLES20.GL_BLEND);
			}
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureUnitNumber);

			vertices.position(0);
			GLES20.glEnableVertexAttribArray(vertexAttribHandle);
			GLES20.glVertexAttribPointer(vertexAttribHandle, 3, GLES20.GL_FLOAT, false, 20,
										 vertices);

			vertices.position(3);
			GLES20.glEnableVertexAttribArray(textureCoordsHandle);
			GLES20.glVertexAttribPointer(textureCoordsHandle, 2, GLES20.GL_FLOAT, false, 20,
										 vertices);

			GLES20.glUniformMatrix4fv(matrixHandle, 1, false, matrixProjectionAndView, 0);

			GLES20.glUniform1i(textureSamplerHandle, textureUnitNumber);
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, numIndices, GLES20.GL_UNSIGNED_SHORT,
								  indices);

			int error = GLES20.glGetError();
			if (error != 0) {
				Log.d(((Object) this).getClass().getName(), "GLError: " + error);
			}
			GLES20.glDisableVertexAttribArray(vertexAttribHandle);
			GLES20.glDisableVertexAttribArray(textureCoordsHandle);
		}

		private void drawVBO(int numIndices, int glUnitName, boolean useBlending) {
			if (useBlending) {
				GLES20.glEnable(GLES20.GL_BLEND);
				GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			}
			else {
				GLES20.glDisable(GLES20.GL_BLEND);
			}
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + glUnitName);

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboNames[glUnitName]);
			GLES20.glEnableVertexAttribArray(vertexAttribHandle);
			GLES20.glVertexAttribPointer(vertexAttribHandle, 3, GLES20.GL_FLOAT, false, 20,
										 0);

			GLES20.glEnableVertexAttribArray(textureCoordsHandle);
			GLES20.glVertexAttribPointer(textureCoordsHandle, 2, GLES20.GL_FLOAT, false, 20,
										 3 * BYTES_PER_FLOAT);

			GLES20.glUniformMatrix4fv(matrixHandle, 1, false, matrixProjectionAndView, 0);

			GLES20.glUniform1i(textureSamplerHandle, glUnitName);

			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, iboNames[glUnitName]);
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, numIndices, GLES20.GL_UNSIGNED_SHORT,
								  0);

			int error = GLES20.glGetError();
			if (error != 0) {
				Log.d(((Object) this).getClass().getName(), "GLError: " + error);
			}
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		}

		private void setMatrices() {
			int gridLineWidth = showGrid ? GRID_LINE_WIDTH_DP : 0;
			int width = BORDER_LINE_WIDTH_DP * 2 +
					CELL_SIZE_DP * region.getWidth() +
					gridLineWidth * (region.getWidth() + 1);
			int height = BORDER_LINE_WIDTH_DP * 2 +
					CELL_SIZE_DP * region.getHeight() +
					gridLineWidth * (region.getHeight() + 1);

			// Clear our matrices
			Arrays.fill(matrixProjection, 0.0f);
			Arrays.fill(matrixView, 0.0f);
			Arrays.fill(matrixProjectionAndView, 0.0f);

			Matrix.orthoM(matrixProjection, 0, 0.0f, width, 0.0f, height, -1.0f, 1.0f);
			// Set the camera position (View matrix)
			Matrix.setLookAtM(matrixView, 0,
							  0.0f, 0.0f, 1.0f,
							  0.0f, 0.0f, 0.0f,
							  0.0f, 1.0f, 0.0f);
			// Calculate the projection and view transformation
			Matrix.multiplyMM(matrixProjectionAndView, 0, matrixProjection, 0, matrixView, 0);
		}

		private FloatBuffer createFloatBuffer(int numElements) {
			ByteBuffer byteBuffer;
			byteBuffer = ByteBuffer.allocateDirect(numElements * FLOAT_SIZE);
			byteBuffer.order(ByteOrder.nativeOrder());
			return byteBuffer.asFloatBuffer();
		}

		private ShortBuffer createShortBuffer(int numElements) {
			ByteBuffer byteBuffer;
			byteBuffer = ByteBuffer.allocateDirect(numElements * SHORT_SIZE);
			byteBuffer.order(ByteOrder.nativeOrder());
			return byteBuffer.asShortBuffer();
		}

		private void addTexturedRectangle(float[] verticesArray, short[] indicesArray, short
				offset, Rect position, RectF textureRect, boolean updateBuffers, int glUnitRef) {
			int numVerticesFloats = NUM_VERTICES_PER_CELL * (NUM_POSITION_COORDS_PER_VERTEX + NUM_TEXTURE_COORDS_PER_VERTEX);

			int verticesStart = offset * numVerticesFloats;
			int verticesIndex = verticesStart;
			int indicesStart = offset * NUM_INDICES_PER_CELL;
			int indicesIndex = indicesStart;
			short vertexBegin = (short) (offset * 4);
			// Set vertices
			verticesArray[verticesIndex++] = position.left;
			verticesArray[verticesIndex++] = position.top;
			verticesArray[verticesIndex++] = 0.0f;
			verticesArray[verticesIndex++] = textureRect.left;
			verticesArray[verticesIndex++] = textureRect.bottom;

			verticesArray[verticesIndex++] = position.right;
			verticesArray[verticesIndex++] = position.top;
			verticesArray[verticesIndex++] = 0.0f;
			verticesArray[verticesIndex++] = textureRect.right;
			verticesArray[verticesIndex++] = textureRect.bottom;

			verticesArray[verticesIndex++] = position.right;
			verticesArray[verticesIndex++] = position.bottom;
			verticesArray[verticesIndex++] = 0.0f;
			verticesArray[verticesIndex++] = textureRect.right;
			verticesArray[verticesIndex++] = textureRect.top;

			verticesArray[verticesIndex++] = position.left;
			verticesArray[verticesIndex++] = position.bottom;
			verticesArray[verticesIndex++] = 0.0f;
			verticesArray[verticesIndex++] = textureRect.left;
			verticesArray[verticesIndex] = textureRect.top;

			// Set draw list indices
			indicesArray[indicesIndex++] = vertexBegin;
			indicesArray[indicesIndex++] = (short) (vertexBegin + 1);
			indicesArray[indicesIndex++] = (short) (vertexBegin + 2);
			indicesArray[indicesIndex++] = (short) (vertexBegin + 2);
			indicesArray[indicesIndex++] = (short) (vertexBegin + 3);
			indicesArray[indicesIndex] = vertexBegin;

			if (updateBuffers) {
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboNames[glUnitRef]);
				FloatBuffer buffer = createFloatBuffer(numVerticesFloats);
				buffer.put(verticesArray, verticesStart, numVerticesFloats);
				buffer.flip();
				GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, verticesStart * FLOAT_SIZE,
									   numVerticesFloats * FLOAT_SIZE, buffer);
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, iboNames[glUnitRef]);
				ShortBuffer shortBuffer = createShortBuffer(NUM_INDICES_PER_CELL);
				shortBuffer.put(indicesArray, indicesStart, NUM_INDICES_PER_CELL);
				shortBuffer.flip();
				GLES20.glBufferSubData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesStart * SHORT_SIZE,
									   NUM_INDICES_PER_CELL * SHORT_SIZE, shortBuffer);
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
			}
		}

		//Create bitmap of the grid lines for a cell
		private void createBkgrndTexture() {
			int mapWidth = region.getWidth();
			int mapHeight = region.getHeight();
			float width = mapWidth * CELL_SIZE_DP + 2 * BORDER_LINE_WIDTH_DP;
			float height = mapHeight * CELL_SIZE_DP + 2 * BORDER_LINE_WIDTH_DP;
			if (showGrid) {
				width += (mapWidth + 1) * GRID_LINE_WIDTH_DP;
				height += (mapHeight + 1) * GRID_LINE_WIDTH_DP;
			}
			float lineCenter = (BORDER_LINE_WIDTH_DP) / 2;
			RectF borderRect = new RectF(lineCenter, lineCenter,
										 width - lineCenter, height - lineCenter);

			Bitmap bkBitmap = Bitmap.createBitmap((int) width, (int) height,
												  Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bkBitmap);
			canvas.drawColor(backgroundColor);
			canvas.drawRect(borderRect, borderPaint);
			if (showGrid) {
				Path gridPath = new Path();
				lineCenter = (GRID_LINE_WIDTH_DP) / 2;
				float stride = CELL_SIZE_DP + GRID_LINE_WIDTH_DP;
				float currentPosition = lineCenter;
				float right = mapWidth * (CELL_SIZE_DP + GRID_LINE_WIDTH_DP) + lineCenter;
				float bottom = mapHeight * (CELL_SIZE_DP + GRID_LINE_WIDTH_DP) + lineCenter;
				for (int i = 0; i <= mapWidth; i++) {
					gridPath.moveTo(currentPosition, lineCenter);
					gridPath.lineTo(currentPosition, bottom);
					currentPosition += stride;
				}
				currentPosition = lineCenter;
				for (int i = 0; i <= mapHeight; i++) {
					gridPath.moveTo(lineCenter, currentPosition);
					gridPath.lineTo(right, currentPosition);
					currentPosition += stride;
				}
				canvas.translate(BORDER_LINE_WIDTH_DP, BORDER_LINE_WIDTH_DP);
				canvas.drawPath(gridPath, gridPaint);
			}

			bindTexture(bkBitmap, GRID_GL_NUM);
			bkBitmap.recycle();
			bkgrndVerticesBuffer = createFloatBuffer(20);
			bkgrndIndicesBuffer = createShortBuffer(6);
			gridVertices = new float[20];
			bkgrndIndices = new short[6];
			addTexturedRectangle(gridVertices, bkgrndIndices, (short) 0,
								 new Rect(0, 0, (int) width, (int) height),
								 new RectF(0.0f, 0.0f, 1.0f, 1.0f), false, 0);
			bkgrndVerticesBuffer.put(gridVertices);
			bkgrndVerticesBuffer.flip();
			bkgrndIndicesBuffer.put(bkgrndIndices);
			bkgrndIndicesBuffer.flip();
		}

		private void createShaders() {
			// Create the shaders
			int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
														 riGraphicTools.vs_Image);
			int fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER,
														   riGraphicTools.fs_Image);

			riGraphicTools.sp_Image = GLES20.glCreateProgram();
			GLES20.glAttachShader(riGraphicTools.sp_Image, vertexShader);
			GLES20.glAttachShader(riGraphicTools.sp_Image, fragmentShader);
			GLES20.glLinkProgram(riGraphicTools.sp_Image);
			// Set our shader program
			GLES20.glUseProgram(riGraphicTools.sp_Image);

			vertexAttribHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "vPosition");
			textureCoordsHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_Image,
															 "a_texCoord");
			matrixHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "uMVPMatrix");
			textureSamplerHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image,
															   "s_texture");
		}

		private void initializeTextures() {
			terrainManager.getTerrains();
			BitmapPacker packer = terrainManager.getBitmapPacker();
			terrainTextureRegions.clear();
			for (Terrain terrain : terrainManager.getTerrains()) {
				terrainTextureRegions.put(terrain.getId(), packer.getRect(terrain.getName()));
			}
			bindTexture(packer.getBitmap(), TERRAIN_GL_NUM);

			packer = cellExitManger.getBitmapPacker();
			cellExitTextureRegions.clear();
			for (CellExitType cellExitType : cellExitManger.getCellExits()) {
				Rect[] regions = new Rect[6];
				regions[UP] = packer.getRect(cellExitType.getName() + "_up");
				regions[NORTH] = packer.getRect(cellExitType.getName() + "_north");
				regions[WEST] = packer.getRect(cellExitType.getName() + "_west");
				regions[EAST] = packer.getRect(cellExitType.getName() + "_east");
				regions[SOUTH] = packer.getRect(cellExitType.getName() + "_south");
				regions[DOWN] = packer.getRect(cellExitType.getName() + "_down");
				cellExitTextureRegions.put(cellExitType.getId(), regions);
			}
			bindTexture(packer.getBitmap(), CELL_EXITS_GL_NUM);
		}

		private void generateGLNames() {
			textureNames = new int[3];
			GLES20.glGenTextures(3, textureNames, 0);
			vboNames = new int[2];
			GLES20.glGenBuffers(2, vboNames, 0);
			iboNames = new int[2];
			GLES20.glGenBuffers(2, iboNames, 0);
		}

		private void bindTexture(Bitmap bitmap, int textureNum) {
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureNum);

			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureNames[textureNum]);
			// Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
								   GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
								   GLES20.GL_NEAREST);

			// Set wrapping mode
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
								   GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
								   GLES20.GL_CLAMP_TO_EDGE);
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		}

		private void setupCellTextures() {
			int numVerticesFloats = NUM_VERTICES_PER_CELL * (NUM_POSITION_COORDS_PER_VERTEX + NUM_TEXTURE_COORDS_PER_VERTEX);

			if (region != null) {
				int numCells = region.getWidth() * region.getHeight();
				cellVertices = new float[numCells * numVerticesFloats];
				cellVerticesBuffer = createFloatBuffer(numCells * numVerticesFloats);
				cellIndices = new short[numCells * NUM_INDICES_PER_CELL];
				cellIndicesBuffer = createShortBuffer(numCells * NUM_INDICES_PER_CELL);
				cellExitVertices = new float[numCells * NUM_EXIT_DIRECTIONS * numVerticesFloats];
				cellExitVerticesBuffer = createFloatBuffer(numCells * NUM_EXIT_DIRECTIONS * numVerticesFloats);
				cellExitIndices = new short[numCells * NUM_EXIT_DIRECTIONS * NUM_INDICES_PER_CELL];
				cellExitIndicesBuffer = createShortBuffer(numCells * NUM_EXIT_DIRECTIONS * NUM_INDICES_PER_CELL);
				RegionView.this.numCells = 0;
				cellMap.clear();
				for (Cell cell : region.getCells()) {
					createSpritesForCell(cell, false);
				}

				GLES20.glDeleteBuffers(2, vboNames, TERRAIN_GL_NUM);
				GLES20.glDeleteBuffers(2, iboNames, TERRAIN_GL_NUM);

				cellVerticesBuffer.put(cellVertices);
				cellVerticesBuffer.flip();
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboNames[TERRAIN_GL_NUM]);
				GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, numCells * numVerticesFloats * BYTES_PER_FLOAT,
									cellVerticesBuffer, GLES20.GL_DYNAMIC_DRAW);
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

				cellIndicesBuffer.put(cellIndices);
				cellIndicesBuffer.flip();
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, iboNames[TERRAIN_GL_NUM]);
				GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, numCells * NUM_INDICES_PER_CELL * BYTES_PER_SHORT,
									cellIndicesBuffer, GLES20.GL_DYNAMIC_DRAW);
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

				cellExitVerticesBuffer.put(cellExitVertices);
				cellExitVerticesBuffer.flip();
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboNames[CELL_EXITS_GL_NUM]);
				GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, numCells * NUM_EXIT_DIRECTIONS * numVerticesFloats * BYTES_PER_FLOAT,
									cellExitVerticesBuffer, GLES20.GL_DYNAMIC_DRAW);
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

				cellExitIndicesBuffer.put(cellExitIndices);
				cellExitIndicesBuffer.flip();
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, iboNames[CELL_EXITS_GL_NUM]);
				GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
									numCells * NUM_EXIT_DIRECTIONS * NUM_INDICES_PER_CELL * BYTES_PER_SHORT,
									cellExitIndicesBuffer, GLES20.GL_DYNAMIC_DRAW);
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
			}
		}

		/**
		 * Creates the sprites to draw a cell.
		 *
		 * @param cell  the {@link Cell} to draw.
		 * @param updateBuffers  true if the bufffers should be updated, otherwise false
		 */
		public void createSpritesForCell(@NonNull Cell cell, boolean updateBuffers) {
			short cellNum = cellMap.get(cell.getId(), Short.MIN_VALUE);
			if (cellNum < 0) {
				cellNum = RegionView.this.numCells++;
				cellMap.put(cell.getId(), cellNum);
			}
			int gridLineWidth = isShowGrid() ? GRID_LINE_WIDTH_DP : 0;
			int cellOffset = CELL_SIZE_DP + gridLineWidth;
			int cellX = cell.getX();
			int cellY = cell.getY();
			cellY = (region.getHeight() - 1) - cellY;
			int left = cellX * cellOffset + BORDER_LINE_WIDTH_DP + gridLineWidth;
			int top = cellY * cellOffset + BORDER_LINE_WIDTH_DP + gridLineWidth;

			Rect rect = new Rect(left, top, left + CELL_SIZE_DP, top + CELL_SIZE_DP);
			RectF texRect = new RectF(terrainTextureRegions.get(cell.getTerrain().getId()));
			int atlasWidth = terrainManager.getBitmapPacker().getWidth();
			int atlasHeight = terrainManager.getBitmapPacker().getHeight();
			texRect.left /= atlasWidth;
			texRect.top /= atlasHeight;
			texRect.right /= atlasWidth;
			texRect.bottom /= atlasHeight;

			addTexturedRectangle(cellVertices, cellIndices, cellNum,
								 rect, texRect, updateBuffers, TERRAIN_GL_NUM);

			int offset = CELL_SIZE_DP - CELL_EXIT_SIZE_DP;
			atlasWidth = cellExitManger.getBitmapPacker().getWidth();
			atlasHeight = cellExitManger.getBitmapPacker().getHeight();
			for (@Direction int i = UP; i <= DOWN; i++) {
				CellExitType cellExitType = cell.getExitForDirection(i);
				if (cellExitType != null) {
					texRect = new RectF(cellExitTextureRegions.get(cellExitType.getId())[i]);
					switch (i) {
						case UP:
							rect = new Rect(left + CELL_EXIT_SIZE_DP,
											top + CELL_SIZE_DP/2,
											left + offset,
											top + offset);
							break;
						case NORTH:
							rect = new Rect(left,
											top + offset,
											left + CELL_SIZE_DP,
											top + CELL_SIZE_DP);
							break;
						case WEST:
							rect = new Rect(left,
											top,
											left + CELL_EXIT_SIZE_DP,
											top + CELL_SIZE_DP);
							break;
						case EAST:
							rect = new Rect(left + offset,
											top,
											left + CELL_SIZE_DP,
											top + CELL_SIZE_DP);
							break;
						case SOUTH:
							rect = new Rect(left,
											top,
											left + CELL_SIZE_DP,
											top + CELL_EXIT_SIZE_DP);
							break;
						case DOWN:
							Log.d("createSpritesForCell", "Adding DOWN cell exit");
							rect = new Rect(left + CELL_EXIT_SIZE_DP,
											top + CELL_EXIT_SIZE_DP,
											left + offset,
											top + CELL_SIZE_DP/2);
							break;
					}
					texRect.left /= atlasWidth;
					texRect.top /= atlasHeight;
					texRect.right /= atlasWidth;
					texRect.bottom /= atlasHeight;
					addTexturedRectangle(cellExitVertices, cellExitIndices,
										 (short) (cellNum * NUM_INDICES_PER_CELL + i), rect, texRect, updateBuffers,
										 CELL_EXITS_GL_NUM);
				}
			}
		}
	}

	// <editor-fold> dec="Plumbing methods">

	//*******************************************************************************************
	// Private plumbing methods
	//*******************************************************************************************

	private void initView() {
		setEGLContextClientVersion(2);
		setClickable(true);
		displayMetrics = getResources().getDisplayMetrics();

		TypedArray styleAttribs = null;
		int gridLineColor;
		int borderColor;
		try {
			styleAttribs = getContext().getTheme().obtainStyledAttributes(
					R.styleable.RegionView);
			showGrid = styleAttribs.getBoolean(R.styleable.RegionView_show_grid, true);
			useDottedLineForGrid = styleAttribs.getBoolean(
					R.styleable.RegionView_use_dotted_line_for_grid, true);
			borderColor = getColor(styleAttribs.getColorStateList(
					R.styleable.RegionView_border_color), Color.RED);
			backgroundColor = getColor(styleAttribs.getColorStateList(
					R.styleable.RegionView_background_color), Color.LTGRAY);
			gridLineColor = getColor(styleAttribs.getColorStateList(
					R.styleable.RegionView_grid_line_color), Color.MAGENTA);
		}
		finally {
			if (styleAttribs != null) {
				styleAttribs.recycle();
			}
		}
		gridPaint = new Paint();
		gridPaint.setColor(gridLineColor);
		gridPaint.setStyle(Style.STROKE);
		if (useDottedLineForGrid) {
			gridPaint.setPathEffect(new DashPathEffect(new float[]{2, 2}, FLOAT_ZERO));
		}
		if (showGrid) {
			gridLineSizePixels = (float) Math.ceil(dipToPixels(GRID_LINE_WIDTH_DP));
		}
		else {
			gridLineSizePixels = 0;
		}
		borderPaint = new Paint();
		borderPaint.setColor(borderColor);
		borderPaint.setStyle(Style.STROKE);
		borderLineSizePixels = (float) Math.ceil(dipToPixels(BORDER_LINE_WIDTH_DP));
		cellSizePixels = (float) Math.ceil(dipToPixels(CELL_SIZE_DP));
		calcTotalContentSize();
		mapRenderer = new MapRenderer();
		setRenderer(mapRenderer);
		detector = new GestureDetector(getContext(), new MapViewGestureListener());
	}

	private int getColor(ColorStateList colors, int defaultColor) {
		return colors != null ? colors.getDefaultColor() : defaultColor;
	}

	private float dipToPixels(int dipValue) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, displayMetrics);
	}

	private void calcTotalContentSize() {
		if (region != null) {
			Log.i("RegionView", "region.getWidth() = " + region.getWidth());
			Log.i("RegionView", "region.getHeight() = " + region.getHeight());
			Log.i("RegionView", "cellSizePixels = " + cellSizePixels);
			Log.i("RegionView", "borderLineSizePixels = " + borderLineSizePixels);
			Log.i("RegionView", "gridLineSizePixels = " + gridLineSizePixels);
			totalContentWidth = (int) Math.ceil(
					region.getWidth() * cellSizePixels
							+ 2 * borderLineSizePixels
							+ (region.getWidth() + 1) * gridLineSizePixels);
			totalContentHeight = (int) Math.ceil(
					region.getHeight() * cellSizePixels
							+ 2 * borderLineSizePixels
							+ (region.getHeight() + 1) * gridLineSizePixels);
			Log.d(((Object) this).getClass().getName(), "totalContentWidth: " + totalContentWidth
					+ ", totalContentHeight: " + totalContentHeight);
		}
		else {
			Log.d(((Object) this).getClass().getName(), "Region is null");
		}
	}

	private Point getCellFromScreenCoordinates(float viewX, float viewY) {
		Log.d(((Object) this).getClass().getName(), "viewX: " + viewX + " viewY: " + viewY);
		Point cellCoordinates = new Point();
		World world = region.getParent();
		int contentX, contentY;

		// Get the x and y position of the event relative to the content (map with grid) rectangle.
		// Calculate event position relative to top left of view
		contentX = (int) (viewX - borderLineSizePixels);
		contentY = (int) (viewY - borderLineSizePixels);

		// Calculate cell array offsets using view relative event position and size of cells,
		// borders, grid lines,
		// and whether or npt the user expects 0 based coordinates or 1 based coordinates
		cellCoordinates.x = (int) (contentX / (cellSizePixels + gridLineSizePixels));
		cellCoordinates.y = (int) (contentY / (cellSizePixels + gridLineSizePixels));

		// If the event was off the edge of the map grid just change the cell coordinate to the
		// nearest visible cell
		if (cellCoordinates.x < 0) {
			cellCoordinates.x = 0;
		}
		else if (cellCoordinates.x > world.getRegionWidth() - 1) {
			cellCoordinates.x = world.getRegionWidth() - 1;
		}
		if (cellCoordinates.y < 0) {
			cellCoordinates.y = 0;
		}
		else if (cellCoordinates.y > world.getRegionHeight() - 1) {
			cellCoordinates.y = world.getRegionHeight() - 1;
		}

		Log.d(((Object) this).getClass().getName(), "cellX: " + cellCoordinates.x + " cellY: " +
				cellCoordinates.y);
		return cellCoordinates;
	}
	// </editor-fold>

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onRegionSelected(RegionSelectedEvent event) {
		setRegion(event.getRegion());
		Log.e("RegionView", "Selected region: " + region);
	}
}
