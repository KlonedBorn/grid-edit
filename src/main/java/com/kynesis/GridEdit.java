package com.kynesis;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class GridEdit extends Region {

  private final InfiniteGrid grids;
  private final SnapPane content;
  private final StackPane container;

  private final DoubleProperty gridSpacing = new SimpleDoubleProperty();
  private final IntegerProperty majorInterval = new SimpleIntegerProperty();

  public GridEdit(
      @NamedArg("gridSpacing") double gridSpacing, @NamedArg("majorInterval") int majorInterval) {
    grids = new InfiniteGrid();
    // Initialize the timeline for the transition
    Timeline timeline =
        new Timeline(
            new KeyFrame(Duration.ZERO, event -> grids.gridXProperty().set(0)), // Start at 0
            new KeyFrame(
                Duration.seconds(1), // Adjust the duration as needed
                event -> {
                  // Increment gridX by a fixed amount
                  double currentX = grids.gridXProperty().get();
                  grids.gridXProperty().set(currentX + 16); // Increment by 1, adjust as needed
                }));

    timeline.setCycleCount(Animation.INDEFINITE); // Repeat indefinitely
    timeline.play(); // Start the animation

    grids.setGridScale(3.0);
    grids.minorGridSpacingProperty().bind(gridSpacingProperty());
    grids.majorGridSpacingProperty().bind(gridSpacingProperty().multiply(majorIntervalProperty()));

    content = new SnapPane();

    // Make sure the grids canvas is visible
    grids.setMouseTransparent(true);

    widthProperty().addListener((obs, old, nvw) -> update());
    heightProperty().addListener((obs, old, nvw) -> update());

    setGridspacing(gridSpacing);
    setMajorInterval(majorInterval);

    // Create container with explicit order: grids first, then content
    this.container = new StackPane();
    container.getChildren().addAll(grids, content);

    // Ensure the container is using the full space
    container.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

    getChildren().add(container);

    // Set up clipping for the container
    Rectangle clip = new Rectangle();
    clip.widthProperty().bind(widthProperty());
    clip.heightProperty().bind(heightProperty());
    setClip(clip);
  }

  public GridEdit() {
    this(DEFAULT_GRID_SPACING, DEFAULT_MAJOR_INTERVAL);
  }

  private void update() {
    double width = getWidth();
    double height = getHeight();

    // Set canvas size to match parent
    grids.setWidth(width);
    grids.setHeight(height);

    // Set content size to 3x the parent size
    content.setPrefSize(width * 3, height * 3);

    // Make sure container fills the space
    container.setPrefSize(width, height);

    // Redraw the grid using GridCanvas render method
    grids.render();
  }

  public final double getGridSpacing() {
    return gridSpacing.get();
  }

  public final void setGridspacing(double gridSpacing) {
    this.gridSpacing.set(gridSpacing);
  }

  public final DoubleProperty gridSpacingProperty() {
    return gridSpacing;
  }

  public final int getMajorInterval() {
    return majorInterval.get();
  }

  public final void setMajorInterval(int majorInterval) {
    this.majorInterval.set(majorInterval);
  }

  public final IntegerProperty majorIntervalProperty() {
    return majorInterval;
  }

  @Override
  protected void layoutChildren() {
    super.layoutChildren();
    grids.render();
  }

  @Override
  public void requestLayout() {
    super.requestLayout();
    grids.render();
  }

  private class SnapPane extends Pane {
    public SnapPane() {
      // Ensure complete transparency
      setStyle("-fx-background-color: transparent;");
      setBackground(null);

      // Make sure mouse events pass through to grid
      setPickOnBounds(false);
    }
  }

  private static final double DEFAULT_GRID_SPACING = 20;
  private static final int DEFAULT_MAJOR_INTERVAL = 5;
}