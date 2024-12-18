package com.kynesis;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class InfiniteGridTest extends Application {

    private Timeline horizontalTimeline;
    private Timeline verticalTimeline;

    @Override
    public void start(Stage primaryStage) {
        // Create the GridCanvas with fixed size
        InfiniteGrid canvas = new InfiniteGrid(800, 600);
        canvas.setMinorGridSpacing(20);
        canvas.setMajorGridSpacing(100);

        // Create scrolling animations
        horizontalTimeline = new Timeline(
            new KeyFrame(Duration.ZERO),
            new KeyFrame(Duration.millis(16), e -> {
                canvas.setGridX(canvas.getGridX() + 1);
                canvas.render();
            })
        );
        horizontalTimeline.setCycleCount(Animation.INDEFINITE);

        verticalTimeline = new Timeline(
            new KeyFrame(Duration.ZERO),
            new KeyFrame(Duration.millis(16), e -> {
                canvas.setGridY(canvas.getGridY() + 1);
                canvas.render();
            })
        );
        verticalTimeline.setCycleCount(Animation.INDEFINITE);

        // Create sidebar controls
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setPrefWidth(250);

        // Visibility Controls
        TitledPane visibilityPane = new TitledPane();
        visibilityPane.setText("Visibility");
        VBox visibilityBox = new VBox(5);
        
        CheckBox showGridCheck = new CheckBox("Show Grid");
        showGridCheck.setSelected(true);
        showGridCheck.selectedProperty().addListener((obs, old, nv) -> {
            canvas.setShowGrid(nv);
            canvas.render();
        });

        CheckBox showMinorGridCheck = new CheckBox("Show Minor Grid");
        showMinorGridCheck.setSelected(true);
        showMinorGridCheck.selectedProperty().addListener((obs, old, nv) -> {
            canvas.setShowMinorGrid(nv);
            canvas.render();
        });

        CheckBox showMajorGridCheck = new CheckBox("Show Major Grid");
        showMajorGridCheck.setSelected(true);
        showMajorGridCheck.selectedProperty().addListener((obs, old, nv) -> {
            canvas.setShowMajorGrid(nv);
            canvas.render();
        });

        visibilityBox.getChildren().addAll(showGridCheck, showMinorGridCheck, showMajorGridCheck);
        visibilityPane.setContent(visibilityBox);

        // Spacing Controls
        TitledPane spacingPane = new TitledPane();
        spacingPane.setText("Grid Spacing");
        VBox spacingBox = new VBox(5);

        Label minorSpacingLabel = new Label("Minor Grid Spacing:");
        Slider minorSpacingSlider = new Slider(5, 50, 20);
        minorSpacingSlider.setShowTickLabels(true);
        minorSpacingSlider.setShowTickMarks(true);
        minorSpacingSlider.valueProperty().addListener((obs, old, nv) -> {
            canvas.setMinorGridSpacing(nv.doubleValue());
            canvas.render();
        });

        Label majorSpacingLabel = new Label("Major Grid Spacing:");
        Slider majorSpacingSlider = new Slider(50, 200, 100);
        majorSpacingSlider.setShowTickLabels(true);
        majorSpacingSlider.setShowTickMarks(true);
        majorSpacingSlider.valueProperty().addListener((obs, old, nv) -> {
            canvas.setMajorGridSpacing(nv.doubleValue());
            canvas.render();
        });

        spacingBox.getChildren().addAll(minorSpacingLabel, minorSpacingSlider, 
                                      majorSpacingLabel, majorSpacingSlider);
        spacingPane.setContent(spacingBox);

        // Line Width Controls
        TitledPane lineWidthPane = new TitledPane();
        lineWidthPane.setText("Line Width");
        VBox lineWidthBox = new VBox(5);

        Label minorWidthLabel = new Label("Minor Line Width:");
        Slider minorWidthSlider = new Slider(0.1, 2, 0.5);
        minorWidthSlider.setShowTickLabels(true);
        minorWidthSlider.setShowTickMarks(true);
        minorWidthSlider.valueProperty().addListener((obs, old, nv) -> {
            canvas.setMinorGridLineWidth(nv.doubleValue());
            canvas.render();
        });

        Label majorWidthLabel = new Label("Major Line Width:");
        Slider majorWidthSlider = new Slider(0.5, 3, 1);
        majorWidthSlider.setShowTickLabels(true);
        majorWidthSlider.setShowTickMarks(true);
        majorWidthSlider.valueProperty().addListener((obs, old, nv) -> {
            canvas.setMajorGridLineWidth(nv.doubleValue());
            canvas.render();
        });

        lineWidthBox.getChildren().addAll(minorWidthLabel, minorWidthSlider, 
                                        majorWidthLabel, majorWidthSlider);
        lineWidthPane.setContent(lineWidthBox);

        // Color Controls
        TitledPane colorPane = new TitledPane();
        colorPane.setText("Colors");
        VBox colorBox = new VBox(5);

        Label backgroundLabel = new Label("Background Color:");
        ColorPicker backgroundPicker = new ColorPicker(Color.WHITE);
        backgroundPicker.setOnAction(e -> {
            canvas.setGridBackground(backgroundPicker.getValue());
            canvas.render();
        });

        Label minorColorLabel = new Label("Minor Grid Color:");
        ColorPicker minorColorPicker = new ColorPicker(Color.LIGHTGRAY);
        minorColorPicker.setOnAction(e -> {
            canvas.setMinorGridLineStroke(minorColorPicker.getValue());
            canvas.render();
        });

        Label majorColorLabel = new Label("Major Grid Color:");
        ColorPicker majorColorPicker = new ColorPicker(Color.GRAY);
        majorColorPicker.setOnAction(e -> {
            canvas.setMajorGridLineStroke(majorColorPicker.getValue());
            canvas.render();
        });

        colorBox.getChildren().addAll(backgroundLabel, backgroundPicker, 
                                    minorColorLabel, minorColorPicker,
                                    majorColorLabel, majorColorPicker);
        colorPane.setContent(colorBox);

        // Scrolling Controls
        TitledPane scrollPane = new TitledPane();
        scrollPane.setText("Scrolling");
        VBox scrollBox = new VBox(5);

        CheckBox horizontalScrollCheck = new CheckBox("Scroll Horizontally");
        horizontalScrollCheck.selectedProperty().addListener((obs, old, nv) -> {
            if (nv) {
                horizontalTimeline.play();
            } else {
                horizontalTimeline.stop();
            }
        });

        CheckBox verticalScrollCheck = new CheckBox("Scroll Vertically");
        verticalScrollCheck.selectedProperty().addListener((obs, old, nv) -> {
            if (nv) {
                verticalTimeline.play();
            } else {
                verticalTimeline.stop();
            }
        });

        Label scrollSpeedLabel = new Label("Scroll Speed:");
        Slider scrollSpeedSlider = new Slider(0.1, 5, 1);
        scrollSpeedSlider.setShowTickLabels(true);
        scrollSpeedSlider.setShowTickMarks(true);

        scrollBox.getChildren().addAll(horizontalScrollCheck, verticalScrollCheck, 
                                     scrollSpeedLabel, scrollSpeedSlider);
        scrollPane.setContent(scrollBox);

        // Add all sections to sidebar
        sidebar.getChildren().addAll(
            visibilityPane,
            spacingPane,
            lineWidthPane,
            colorPane,
            scrollPane
        );

        // Create main layout
        BorderPane root = new BorderPane();
        root.setRight(sidebar);
        root.setCenter(canvas);

        // Create and show scene
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("GridCanvas Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}