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
            this.root = new QuadTreeNode(2, 4, rootBox,10);
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
