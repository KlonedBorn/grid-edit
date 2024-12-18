package com.kynesis;

import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class InfiniteGrid extends Canvas {
  private static final StyleablePropertyFactory<InfiniteGrid> FACTORY =
      new StyleablePropertyFactory<>(Canvas.getClassCssMetaData());

  // Grid coordinate properties (to track position in grid space)
  private final DoubleProperty gridX = new SimpleDoubleProperty();
  private final DoubleProperty gridY = new SimpleDoubleProperty();

  // Grid visibility properties
  private final StyleableBooleanProperty showGrid;
  private final StyleableBooleanProperty showMinorGrid;
  private final StyleableBooleanProperty showMajorGrid;

  // Grid background
  private final StyleableObjectProperty<Color> gridBackground;

  // Minor grid appearance
  private final StyleableDoubleProperty minorGridSpacing;
  private final StyleableDoubleProperty minorGridLineWidth;
  private final StyleableObjectProperty<Color> minorGridLineStroke;

  // Major grid appearance
  private final StyleableDoubleProperty majorGridSpacing;
  private final StyleableDoubleProperty majorGridLineWidth;
  private final StyleableObjectProperty<Color> majorGridLineStroke;

  private final StyleableDoubleProperty gridScale;

  // CSS property definitions
  private static final CssMetaData<InfiniteGrid, Boolean> SHOW_GRID =
      FACTORY.createBooleanCssMetaData("-fx-show-grid", s -> s.showGrid, true);

  private static final CssMetaData<InfiniteGrid, Boolean> SHOW_MINOR_GRID =
      FACTORY.createBooleanCssMetaData("-fx-show-minor-grid", s -> s.showMinorGrid, true);

  private static final CssMetaData<InfiniteGrid, Boolean> SHOW_MAJOR_GRID =
      FACTORY.createBooleanCssMetaData("-fx-show-major-grid", s -> s.showMajorGrid, true);

  private static final CssMetaData<InfiniteGrid, Color> GRID_BACKGROUND =
      FACTORY.createColorCssMetaData("-fx-grid-background", s -> s.gridBackground, Color.WHITE);

  private static final CssMetaData<InfiniteGrid, Number> MINOR_GRID_SPACING =
      FACTORY.createSizeCssMetaData("-fx-minor-grid-spacing", s -> s.minorGridSpacing, 10.0);

  private static final CssMetaData<InfiniteGrid, Number> MINOR_GRID_LINE_WIDTH =
      FACTORY.createSizeCssMetaData("-fx-minor-grid-line-width", s -> s.minorGridLineWidth, 0.5);

  private static final CssMetaData<InfiniteGrid, Color> MINOR_GRID_LINE_STROKE =
      FACTORY.createColorCssMetaData(
          "-fx-minor-grid-line-stroke", s -> s.minorGridLineStroke, Color.LIGHTGRAY);

  private static final CssMetaData<InfiniteGrid, Number> MAJOR_GRID_SPACING =
      FACTORY.createSizeCssMetaData("-fx-major-grid-spacing", s -> s.majorGridSpacing, 50.0);

  private static final CssMetaData<InfiniteGrid, Number> MAJOR_GRID_LINE_WIDTH =
      FACTORY.createSizeCssMetaData("-fx-major-grid-line-width", s -> s.majorGridLineWidth, 1.0);

  private static final CssMetaData<InfiniteGrid, Color> MAJOR_GRID_LINE_STROKE =
      FACTORY.createColorCssMetaData(
          "-fx-major-grid-line-stroke", s -> s.majorGridLineStroke, Color.GRAY);

  private static final CssMetaData<InfiniteGrid, Number> GRID_SCALE =
      FACTORY.createSizeCssMetaData("-fx-grid-scale", s -> s.gridScale, 1.0);

  public InfiniteGrid() {
    this(0, 0);
  }

  public InfiniteGrid(double width, double height) {
    super(width, height);

    // Initialize styleable properties
    showGrid = new SimpleStyleableBooleanProperty(SHOW_GRID, this, "showGrid", true);
    showMinorGrid =
        new SimpleStyleableBooleanProperty(SHOW_MINOR_GRID, this, "showMinorGrid", true);
    showMajorGrid =
        new SimpleStyleableBooleanProperty(SHOW_MAJOR_GRID, this, "showMajorGrid", true);
    gridBackground =
        new SimpleStyleableObjectProperty<>(GRID_BACKGROUND, this, "gridBackground", Color.WHITE);
    minorGridSpacing =
        new SimpleStyleableDoubleProperty(MINOR_GRID_SPACING, this, "minorGridSpacing");
    minorGridLineWidth =
        new SimpleStyleableDoubleProperty(MINOR_GRID_LINE_WIDTH, this, "minorGridLineWidth");
    minorGridLineStroke =
        new SimpleStyleableObjectProperty<>(
            MINOR_GRID_LINE_STROKE, this, "minorGridLineStroke", Color.LIGHTGRAY);
    majorGridSpacing =
        new SimpleStyleableDoubleProperty(MAJOR_GRID_SPACING, this, "majorGridSpacing");
    majorGridLineWidth =
        new SimpleStyleableDoubleProperty(MAJOR_GRID_LINE_WIDTH, this, "majorGridLineWidth");
    majorGridLineStroke =
        new SimpleStyleableObjectProperty<>(
            MAJOR_GRID_LINE_STROKE, this, "majorGridLineStroke", Color.BLACK);
    gridScale = new SimpleStyleableDoubleProperty(GRID_SCALE, this, "gridScale");

    // Add listeners to trigger render on property changes
    showGrid.addListener((obs, old, nv) -> render());
    showMinorGrid.addListener((obs, old, nv) -> render());
    showMajorGrid.addListener((obs, old, nv) -> render());
    gridBackground.addListener((obs, old, nv) -> render());
    minorGridSpacing.addListener((obs, old, nv) -> render());
    minorGridLineWidth.addListener((obs, old, nv) -> render());
    minorGridLineStroke.addListener((obs, old, nv) -> render());
    majorGridSpacing.addListener((obs, old, nv) -> render());
    majorGridLineWidth.addListener((obs, old, nv) -> render());
    majorGridLineStroke.addListener((obs, old, nv) -> render());
    gridScale.addListener((obs, old, nv) -> render());
  }

  public void render() {
    GraphicsContext gc = getGraphicsContext2D();
    double width = getWidth();
    double height = getHeight();

    // Clear canvas with background color
    gc.setFill(gridBackground.get());
    gc.fillRect(0, 0, width, height);

    if (!showGrid.get()) {
      return;
    }

    // Get current viewport position
    double vpX = gridX.get();
    double vpY = gridY.get();

    // Draw minor grid
    if (showMinorGrid.get()) {
      gc.setStroke(minorGridLineStroke.get());
      gc.setLineWidth(minorGridLineWidth.get());
      double spacing = minorGridSpacing.get();

      // Calculate first visible grid line positions
      double firstX = spacing * Math.floor(vpX / spacing);
      double firstY = spacing * Math.floor(vpY / spacing);

      // Draw vertical lines
      for (double x = firstX; x <= vpX + width; x += spacing) {
        // Convert grid coordinate to canvas coordinate
        double canvasX = x - vpX;
        gc.strokeLine(canvasX, 0, canvasX, height);
      }

      // Draw horizontal lines
      for (double y = firstY; y <= vpY + height; y += spacing) {
        // Convert grid coordinate to canvas coordinate
        double canvasY = y - vpY;
        gc.strokeLine(0, canvasY, width, canvasY);
      }
    }

    // Draw major grid
    if (showMajorGrid.get()) {
      gc.setStroke(majorGridLineStroke.get());
      gc.setLineWidth(majorGridLineWidth.get());
      double spacing = majorGridSpacing.get();

      // Calculate first visible grid line positions
      double firstX = spacing * Math.floor(vpX / spacing);
      double firstY = spacing * Math.floor(vpY / spacing);

      // Draw vertical lines
      for (double x = firstX; x <= vpX + width; x += spacing) {
        // Convert grid coordinate to canvas coordinate
        double canvasX = x - vpX;
        gc.strokeLine(canvasX, 0, canvasX, height);
      }

      // Draw horizontal lines
      for (double y = firstY; y <= vpY + height; y += spacing) {
        // Convert grid coordinate to canvas coordinate
        double canvasY = y - vpY;
        gc.strokeLine(0, canvasY, width, canvasY);
      }
    }
  }

  // Expose CSS metadata
  public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
    return FACTORY.getCssMetaData();
  }

  @Override
  public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
    return getClassCssMetaData();
  }

  // Add getters/setters for gridX and gridY
  public double getGridX() {
    return gridX.get();
  }

  public void setGridX(double x) {
    gridX.set(x);
  }

  public DoubleProperty gridXProperty() {
    return gridX;
  }

  public double getGridY() {
    return gridY.get();
  }

  public void setGridY(double y) {
    gridY.set(y);
  }

  public DoubleProperty gridYProperty() {
    return gridY;
  }

  // Grid Visibility Properties

  public boolean isShowGrid() {
    return showGrid.get();
  }

  public void setShowGrid(boolean value) {
    showGrid.set(value);
  }

  public BooleanProperty showGridProperty() {
    return showGrid;
  }

  public boolean isShowMinorGrid() {
    return showMinorGrid.get();
  }

  public void setShowMinorGrid(boolean value) {
    showMinorGrid.set(value);
  }

  public BooleanProperty showMinorGridProperty() {
    return showMinorGrid;
  }

  public boolean isShowMajorGrid() {
    return showMajorGrid.get();
  }

  public void setShowMajorGrid(boolean value) {
    showMajorGrid.set(value);
  }

  public BooleanProperty showMajorGridProperty() {
    return showMajorGrid;
  }

  // Grid Background Property

  public Color getGridBackground() {
    return gridBackground.get();
  }

  public void setGridBackground(Color color) {
    gridBackground.set(color);
  }

  public ObjectProperty<Color> gridBackgroundProperty() {
    return gridBackground;
  }

  // Minor Grid Properties

  public double getMinorGridSpacing() {
    return minorGridSpacing.get();
  }

  public void setMinorGridSpacing(double spacing) {
    minorGridSpacing.set(spacing);
  }

  public DoubleProperty minorGridSpacingProperty() {
    return minorGridSpacing;
  }

  public double getMinorGridLineWidth() {
    return minorGridLineWidth.get();
  }

  public void setMinorGridLineWidth(double width) {
    minorGridLineWidth.set(width);
  }

  public DoubleProperty minorGridLineWidthProperty() {
    return minorGridLineWidth;
  }

  public Color getMinorGridLineStroke() {
    return minorGridLineStroke.get();
  }

  public void setMinorGridLineStroke(Color color) {
    minorGridLineStroke.set(color);
  }

  public ObjectProperty<Color> minorGridLineStrokeProperty() {
    return minorGridLineStroke;
  }

  // Major Grid Properties

  public double getMajorGridSpacing() {
    return majorGridSpacing.get();
  }

  public void setMajorGridSpacing(double spacing) {
    majorGridSpacing.set(spacing);
  }

  public DoubleProperty majorGridSpacingProperty() {
    return majorGridSpacing;
  }

  public double getMajorGridLineWidth() {
    return majorGridLineWidth.get();
  }

  public void setMajorGridLineWidth(double width) {
    majorGridLineWidth.set(width);
  }

  public DoubleProperty majorGridLineWidthProperty() {
    return majorGridLineWidth;
  }

  public Color getMajorGridLineStroke() {
    return majorGridLineStroke.get();
  }

  public void setMajorGridLineStroke(Color color) {
    majorGridLineStroke.set(color);
  }

  public ObjectProperty<Color> majorGridLineStrokeProperty() {
    return majorGridLineStroke;
  }

  // Grid Scale Property

  public double getGridScale() {
    return gridScale.get();
  }

  public void setGridScale(double scale) {
    gridScale.set(scale);
  }

  public DoubleProperty gridScaleProperty() {
    return gridScale;
  }
}