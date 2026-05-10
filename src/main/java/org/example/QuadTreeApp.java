package org.example;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class QuadTreeApp extends  Application {

    private QuadTreeNode root;
    private final int CANVAS_WIDTH = 800;
    private final int CANVAS_HEIGHT = 800;
    private GraphicsContext ctx = null;
    private RandomPointGenerator generator;
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            ArrayList<QuadTreePoint> points = this.loadPoints("points.csv");
            ArrayList<Double> mins = new ArrayList<>();
            mins.add(0.0);
            mins.add(0.0);
            ArrayList<Double> maxs = new ArrayList<>();
            maxs.add((double)this.CANVAS_WIDTH);
            maxs.add((double)this.CANVAS_HEIGHT);
            QuadTreeBoundingBox rootBox = new QuadTreeBoundingBox(mins, maxs);
            this.root = new QuadTreeNode(2, 30, rootBox,15);
            Canvas canvas = new Canvas(this.CANVAS_WIDTH,this.CANVAS_HEIGHT);
            this.ctx = canvas.getGraphicsContext2D();
            this.generator = new RandomPointGenerator(2,"./points.csv");

            for(QuadTreePoint point : points) {
                this.root.insert(point);
            }

            this.drawNode(this.root);
            VBox menu = new VBox(15);
            menu.setPadding(new Insets(20));
            menu.setPrefWidth(250);
            menu.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc; -fx-border-width: 0 0 0 1;");

            javafx.scene.control.Label pointSearchLabel = new javafx.scene.control.Label("Point Search");
            pointSearchLabel.setStyle("-fx-font-weight: bold;");

            TextField xInput = new TextField();
            xInput.setPromptText("X coord");
            TextField yInput = new TextField();
            yInput.setPromptText("Y coord");
            Button searchBtn = new Button("Search Point");
            searchBtn.setMaxWidth(Double.MAX_VALUE);

            VBox pointSearchBox = new VBox(5, pointSearchLabel, xInput, yInput, searchBtn);

            javafx.scene.control.Label rangeScanLabel = new javafx.scene.control.Label("Range Scan (Box)");
            rangeScanLabel.setStyle("-fx-font-weight: bold;");

            Label timeLabel = new Label("Last operation: - ms");

            timeLabel.setStyle("-fx-font-size: 16px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-style: italic; " +
                    "-fx-text-fill: #444;");

            TextField xMinInput = new TextField();
            xMinInput.setPromptText("X Min");
            TextField yMinInput = new TextField();
            yMinInput.setPromptText("Y Min");
            TextField xMaxInput = new TextField();
            xMaxInput.setPromptText("X Max");
            TextField yMaxInput = new TextField();
            yMaxInput.setPromptText("Y Max");
            Button rangeBtn = new Button("Scan Area");
            rangeBtn.setMaxWidth(Double.MAX_VALUE);

            VBox rangeScanBox = new VBox(5, rangeScanLabel, xMinInput, yMinInput, xMaxInput, yMaxInput, rangeBtn);

            menu.getChildren().addAll(pointSearchBox, new javafx.scene.control.Separator(), rangeScanBox);

            rangeBtn.setOnAction(e -> {
                try {
                    double xMin = Double.parseDouble(xMinInput.getText());
                    double yMin = Double.parseDouble(yMinInput.getText());
                    double xMax = Double.parseDouble(xMaxInput.getText());
                    double yMax = Double.parseDouble(yMaxInput.getText());

                    QuadTreeBoundingBox searchBox = new QuadTreeBoundingBox(
                            new ArrayList<>(List.of(xMin, yMin)),
                            new ArrayList<>(List.of(xMax, yMax))
                    );

                    long startTime = System.nanoTime();

                    List<QuadTreePoint> foundPoints = this.root.rangeScan(searchBox);

                    long endTime = System.nanoTime();

                    double durationMillis = (endTime - startTime) / 1_000_000.0;

                    timeLabel.setText(String.format("Range Scan: %.4f ms (%d bodů)", durationMillis, foundPoints.size()));

                    System.out.println("--- Range Scan Results (" + foundPoints.size() + ") ---");
                    System.out.println("--- Range Scan Results (" + foundPoints.size() + ") ---");
                    for (QuadTreePoint p : foundPoints) {
                        ctx.setStroke(Color.BLUE);
                        ctx.strokeRect(p.getCoords().get(0) - 4, p.getCoords().get(1) - 4, 8, 8);
                    }
                } catch (Exception ex) { System.out.println("Error in range scan"); }
            });

            searchBtn.setOnAction(e -> {
                try {
                    double x = Double.parseDouble(xInput.getText());
                    double y = Double.parseDouble(yInput.getText());
                    QuadTreePoint searchTarget = new QuadTreePoint(new ArrayList<>(List.of(x, y)), null);
                    QuadTreePoint found = this.root.pointSearch(searchTarget);

                    if (found != null) {
                        ctx.setStroke(Color.LIME);
                        ctx.setLineWidth(2);
                        ctx.strokeOval(x - 5, y - 5, 10, 10);

                        this.showPoint(found);
                    }
                } catch (Exception ex) { System.out.println("Chyba v Point Search vstupu!"); }
            });

            javafx.scene.control.Label deleteLabel = new javafx.scene.control.Label("Delete Area (Box)");
            deleteLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: darkred;");

            TextField xMinDel = new TextField(); xMinDel.setPromptText("X Min");
            TextField yMinDel = new TextField(); yMinDel.setPromptText("Y Min");
            TextField xMaxDel = new TextField(); xMaxDel.setPromptText("X Max");
            TextField yMaxDel = new TextField(); yMaxDel.setPromptText("Y Max");


            Button deleteAreaBtn = new Button("Delete Area");
            deleteAreaBtn.setMaxWidth(Double.MAX_VALUE);
            deleteAreaBtn.setStyle("-fx-base: #ff9999;");

            javafx.scene.control.Label generateLabel = new javafx.scene.control.Label("Generate Next N points");
            generateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");

            TextField countInput = new TextField("100");
            countInput.setPromptText("Počet bodů...");

            Button generateButton = new Button("Generate");
            generateButton.setMaxWidth(Double.MAX_VALUE);
            generateButton.setStyle(
                    "-fx-background-color: #3498db; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 5; " +
                            "-fx-cursor: hand;"
            );
            javafx.scene.control.Label noPointsLabel = new javafx.scene.control.Label(String.format("Number of points: %d",this.loadPoints("./points.csv").size()));
            noPointsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: darkred;");
            generateButton.setOnAction(e -> {
                try {
                    int n = Integer.parseInt(countInput.getText());
                    List<QuadTreePoint> appendedPoints =  this.generator.appendNextNPoints(n);
                    ArrayList<QuadTreePoint> allPoints = this.loadPoints("./points.csv");

                    for(QuadTreePoint p : appendedPoints) {
                        this.root.insert(p);
                    }
                    noPointsLabel.setText(String.format("Number of points: %d",allPoints.size()));
                    this.drawNode(this.root);


                } catch (NumberFormatException ex) {

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            pointSearchLabel.setStyle("-fx-font-weight: bold;");

            TextField generateInput = new TextField(); yMaxDel.setPromptText("N Points");


            VBox generateBox = new VBox(3,generateLabel,generateButton,countInput,noPointsLabel);

            VBox deleteBox = new VBox(5, deleteLabel, xMinDel, yMinDel, xMaxDel, yMaxDel, deleteAreaBtn);
            deleteBox.getChildren().add(timeLabel);
            menu.getChildren().addAll(new javafx.scene.control.Separator(), deleteBox,generateBox);

            deleteAreaBtn.setOnAction(e -> {
                try {
                    double xMin = Double.parseDouble(xMinDel.getText());
                    double yMin = Double.parseDouble(yMinDel.getText());
                    double xMax = Double.parseDouble(xMaxDel.getText());
                    double yMax = Double.parseDouble(yMaxDel.getText());

                    QuadTreeBoundingBox areaToDelete = new QuadTreeBoundingBox(
                            new ArrayList<>(List.of(xMin, yMin)),
                            new ArrayList<>(List.of(xMax, yMax))
                    );
                    long startTime = System.nanoTime();
                    boolean changed = this.root.deleteArea(areaToDelete);

                    long endTime = System.nanoTime();

                    long durationNano = (endTime - startTime);
                    double durationMillis = durationNano / 1_000_000.0;

                    System.out.println("Delete took " + durationMillis + " ms");
                    timeLabel.setText(String.format("Delete took: %.4f ms", durationMillis));
                    if (changed) {
                        ctx.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
                        this.drawNode(this.root);
                    } else {
                        System.out.println("No points found");
                        timeLabel.setText(timeLabel.getText() + " (not found)");
                    }
                } catch (Exception ex) {
                    System.out.println("Error while deleting");
                }
            });
            javafx.scene.control.Label reorgLabel = new javafx.scene.control.Label("Tree Optimization");
            reorgLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

            Button reorgBtn = new Button("Optimize & Rebalance");
            reorgBtn.setMaxWidth(Double.MAX_VALUE);
            reorgBtn.setStyle(
                    "-fx-background-color: #27ae60; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 5; " +
                            "-fx-cursor: hand;"
            );

            VBox reorgBox = new VBox(5, reorgLabel, reorgBtn);
            menu.getChildren().addAll(new javafx.scene.control.Separator(), reorgBox);

            reorgBtn.setOnAction(e -> {
                try {
                    long startTime = System.nanoTime();
                    this.root = this.root.rebalance();
                    long endTime = System.nanoTime();
                    double durationMillis = (endTime - startTime) / 1_000_000.0;
                    ctx.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
                    this.drawNode(this.root);
                    timeLabel.setText(String.format("Optimize: %.4f ms", durationMillis));
                    System.out.println("Tree optimization complete in " + durationMillis + " ms");

                } catch (Exception ex) {
                    System.out.println("Error during rebalance: " + ex.getMessage());
                }
            });

            HBox mainLayout = new HBox(canvas, menu);

            primaryStage.setTitle("QuadTree JavaFX Visualizer");
            primaryStage.setScene(new Scene(mainLayout, CANVAS_WIDTH + 250, CANVAS_HEIGHT));
            primaryStage.show();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void drawNode(QuadTreeNode node) {
        if (node == null) return;

        QuadTreeBoundingBox box = node.getBoundingBox();
        double x = box.getMin().get(0);
        double y = box.getMin().get(1);
        double w = box.getMax().get(0) - x;
        double h = box.getMax().get(1) - y;

        this.ctx.setStroke(Color.BLACK);
        this.ctx.setLineWidth(1);
        this.ctx.strokeRect(x, y, w, h);
        if (!node.getChildren().isEmpty()) {
            for (QuadTreeNode child : node.getChildren()) {
                drawNode(child);
            }
        } else {
            this.ctx.setFill(Color.RED);
            for (QuadTreePoint p : node.getPoints()) {
                this.ctx.fillOval(p.getCoords().get(0) - 2, p.getCoords().get(1) - 2, 4, 4);
            }
        }
    }

    ArrayList<QuadTreePoint> loadPoints(String filename) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        ArrayList<QuadTreePoint> points = new ArrayList<>();
        String line;

        try {
            while ((line = bufferedReader.readLine()) != null) {
                String[] pointsStr = line.split(",");

                ArrayList<Double> coords = new ArrayList<>();
                for (int i = 0; i < 2; ++i) {
                    Double quadPointDouble = Double.parseDouble(pointsStr[i]);
                    coords.add(quadPointDouble);
                }

                QuadTreeData data = new QuadTreeData(pointsStr[2], pointsStr[3], pointsStr[4]);
                points.add(new QuadTreePoint(coords, data));
            }
        } finally {
            bufferedReader.close();
        }

        return points;
    }

    void showPoint(QuadTreePoint point) {
        if (point == null) return;

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Info about point");

        VBox vbox = new VBox(15);
        vbox.setPadding(new javafx.geometry.Insets(20));
        vbox.setPrefWidth(300);
        vbox.setStyle("-fx-background-color: white;");

        Label headPos = new Label("Location in tree");
        headPos.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 14px;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        List<Double> coords = point.getCoords();
        for (int i = 0; i < coords.size(); i++) {
            String label = (i == 0) ? "X:" : (i == 1) ? "Y:" : "... " + (i + 1) + ":";
            grid.add(new Label(label), 0, i);
            grid.add(new Label(String.format("%.2f", coords.get(i))), 1, i);
        }

        Separator sep = new Separator();
        Label headData = new Label("Data");
        headData.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 14px;");

        VBox dataBox = new VBox(5);

        Object rawData = point.getData();
        if (rawData instanceof QuadTreeData d) {
            dataBox.getChildren().addAll(
                    new Label("Country: " + d.getCountry()),
                    new Label("City: " + d.getCity()),
                    new Label("Address: " + d.getAddress())
            );
        } else {
            dataBox.getChildren().add(new Label("Bod isnt in tree"));
        }

        Button closeBtn = new Button("Close");
        closeBtn.setMaxWidth(Double.MAX_VALUE);
        closeBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> popupStage.close());

        vbox.getChildren().addAll(headPos, grid, sep, headData, dataBox, closeBtn);

        Scene scene = new Scene(vbox);
        popupStage.setScene(scene);
        popupStage.show();
    }

}
