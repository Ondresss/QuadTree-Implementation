package org.example;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class QuadTreeApp extends  Application {

    private QuadTreeNode root;
    private final int CANVAS_WIDTH = 800;
    private final int CANVAS_HEIGHT = 800;
    private GraphicsContext ctx = null;
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
            this.root = new QuadTreeNode(2, 4, rootBox,10);
            Canvas canvas = new Canvas(this.CANVAS_WIDTH,this.CANVAS_HEIGHT);
            this.ctx = canvas.getGraphicsContext2D();


            for(QuadTreePoint point : points) {
                this.root.insert(point);
            }

            this.drawNode(this.root);
            TextField xInput = new TextField();
            xInput.setPromptText("X coord");

            TextField yInput = new TextField();
            yInput.setPromptText("Y coord");

            Button searchBtn = new Button("Point search");
            TextField xMinInput = new TextField();
            xMinInput.setPromptText("X Min");
            xMinInput.setPrefWidth(60);

            TextField yMinInput = new TextField();
            yMinInput.setPromptText("Y Min");
            yMinInput.setPrefWidth(60);

            TextField xMaxInput = new TextField();
            xMaxInput.setPromptText("X Max");
            xMaxInput.setPrefWidth(60);

            TextField yMaxInput = new TextField();
            yMaxInput.setPromptText("Y Max");
            yMaxInput.setPrefWidth(60);

            Button rangeBtn = new Button("Range Scan");

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

                    List<QuadTreePoint> foundPoints = this.root.rangeScan(searchBox);

                    System.out.println("--- Range Scan Výsledky (" + foundPoints.size() + ") ---");
                    for (QuadTreePoint p : foundPoints) {
                        System.out.println(p.getData().getCity() + ": " + p.getData().getAddress());
                        ctx.setStroke(Color.BLUE);
                        ctx.strokeRect(p.getCoords().get(0) - 4, p.getCoords().get(1) - 4, 8, 8);
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Chyba: Zadej platná čísla pro Range Scan!");
                }
            });


            searchBtn.setOnAction(e -> {
                try {
                    double x = Double.parseDouble(xInput.getText());
                    double y = Double.parseDouble(yInput.getText());

                    QuadTreePoint searchTarget = new QuadTreePoint(new ArrayList<>(List.of(x, y)), null);

                    QuadTreePoint found = this.root.pointSearch(searchTarget);

                    if (found != null) {
                        System.out.println("Nalezeno: " + found.getData().getCity() + ", " + found.getData().getAddress());
                        ctx.setStroke(Color.LIME);
                        ctx.setLineWidth(2);
                        ctx.strokeOval(x - 5, y - 5, 10, 10);
                    } else {
                        System.out.println("Bod na těchto souřadnicích neexistuje.");
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Zadej platná čísla!");
                }
            });


            HBox row1 = new HBox(10, xInput, yInput, searchBtn);
            row1.setAlignment(Pos.CENTER);

            HBox row2 = new HBox(10, xMinInput, yMinInput, xMaxInput, yMaxInput, rangeBtn);
            row2.setAlignment(Pos.CENTER);

            VBox controls = new VBox(10, row1, row2);
            controls.setPadding(new Insets(10));
            controls.setStyle("-fx-background-color: #f0f0f0;");

            VBox mainLayout = new VBox(canvas, controls);
            mainLayout.setAlignment(Pos.CENTER);

            primaryStage.setTitle("QuadTree JavaFX Visualizer");
            primaryStage.setScene(new Scene(mainLayout, CANVAS_WIDTH, CANVAS_HEIGHT + 110));
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
        InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        ArrayList<QuadTreePoint> points = new ArrayList<>();
        String line;
        while((line = bufferedReader.readLine()) != null) {
            String[] pointsStr = line.split(",");
            ArrayList<Double> coords = new ArrayList<>();
            for(int i = 0; i < 2; ++i) {
                Double quadPointDouble = Double.parseDouble(pointsStr[i]);
                coords.add(quadPointDouble);
            }
            QuadTreeData data = new QuadTreeData(pointsStr[2],pointsStr[3],pointsStr[4]);
            points.add(new QuadTreePoint(coords,data));
        }
        bufferedReader.close();
        return points;
    }

}
