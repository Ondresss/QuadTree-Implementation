package org.example;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
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
            this.root = new QuadTreeNode(2, 1 << 2, rootBox,3);
            Canvas canvas = new Canvas(this.CANVAS_WIDTH,this.CANVAS_HEIGHT);
            this.ctx = canvas.getGraphicsContext2D();


            for(QuadTreePoint point : points) {
                this.root.insert(point);
            }

            this.drawNode(this.root);
            StackPane rootPane = new StackPane();
            rootPane.getChildren().add(canvas);

            primaryStage.setTitle("QuadTree JavaFX Visualizer");
            primaryStage.setScene(new Scene(rootPane, CANVAS_WIDTH, CANVAS_HEIGHT));
            primaryStage.show();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void drawNode(QuadTreeNode node) {
        if (node == null) return;

        QuadTreeBoundingBox box = node.getBoundingBox();

        double minX = box.getMin().get(0);
        double minY = box.getMin().get(1);
        double maxX = box.getMax().get(0);
        double maxY = box.getMax().get(1);

        double width = maxX - minX;
        double height = maxY - minY;

        this.ctx.setStroke(Color.BLACK);
        this.ctx.setLineWidth(0.5);
        this.ctx.strokeRect(minX, minY, width, height);

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
            for(String point : pointsStr) {
                Double quadPointDouble = Double.parseDouble(point);
                coords.add(quadPointDouble);
            }
            points.add(new QuadTreePoint(coords));
        }
        bufferedReader.close();
        return points;
    }

}
