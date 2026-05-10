package org.example;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

public class RandomPointGenerator {

    private String outputFile;
    private int dimension;
    private Random random = new Random();

    static private List<String> countries = List.of("Czech Republic", "Slovakia", "Germany", "Poland", "Austria");
    static private List<String> cities = List.of("Prague", "Brno", "Ostrava", "Bratislava", "Berlin", "Warsaw", "Vienna");
    static private List<String> addresses = List.of("Hlavni 123", "Prazska 45", "Dlouha 7", "U Lesa 10", "Namesti 1");

    public RandomPointGenerator(int dimension, String outputFile) {
        this.dimension = dimension;
        this.outputFile = outputFile;
    }

    public List<QuadTreePoint> appendNextNPoints(int N) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            List<QuadTreePoint> points = new ArrayList<QuadTreePoint>(N);
            for (int i = 0; i < N; i++) {
                ArrayList<Double> coords = new ArrayList<>();
                for (int d = 0; d < dimension; d++) {
                    coords.add(random.nextDouble() * 800.0);
                }
                String country = countries.get(random.nextInt(countries.size()));
                String city = cities.get(random.nextInt(cities.size()));
                String address = addresses.get(random.nextInt(addresses.size()));

                String coordsString = coords.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(","));

                String line = String.format("%s,%s,%s,%s", coordsString, country, city, address);
                points.add(new QuadTreePoint(coords,new QuadTreeData(country,address,city)));
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Wrote " + N + " points to file " + outputFile);
            return points;
        } catch (IOException e) {
            System.err.println("Error while writing to file: " + e.getMessage());
        }
        return Collections.emptyList();
    }
}