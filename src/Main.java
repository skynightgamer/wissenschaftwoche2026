import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        var FEATURES = 4;

        var t = System.nanoTime();

        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("data.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var persons = Integer.parseInt(lines.get(0));

        Double[][] ratings = new Double[persons][persons];
        lines.remove(0);
        var pattern = Pattern.compile("\\((\\d*),(\\d*)\\) (-?[0-9.]*)");
        AtomicReference<Double> ratingSum = new AtomicReference<>(0.0);
        AtomicInteger ratingNum = new AtomicInteger();
        lines.stream()
            .map(pattern::matcher)
            .filter(Matcher::matches)
            .forEach(m -> {
                ratings[Integer.parseInt(m.group(1))][Integer.parseInt(m.group(2))] = Double.parseDouble(m.group(3));
                ratingNum.getAndIncrement();
                ratingSum.updateAndGet(v -> v + Double.parseDouble(m.group(3)));
            });

        System.out.println(ratingSum.get());
        var initialVal = Math.sqrt(Math.abs(ratingSum.get() / ratingNum.get() / FEATURES));
        var degrees = new double[persons][FEATURES];
        var preferences = new double[persons][FEATURES];

        for (int i = 0; i < persons; i++) {
            for (int j = 0; j < FEATURES; j++) {
                degrees[i][j] = initialVal * (Math.random() / 5 + 0.9) * (Math.floor(Math.random() * 2) - 1);
                preferences[i][j] = initialVal * (Math.random() / 5 + 0.9) * (Math.floor(Math.random() * 2) - 1);
            }
        }

        var gd = new GradientDescent(ratings, degrees, preferences);

        gd.gradientDescent();
        System.out.println((System.nanoTime() - t) / 1_000_000 + "ms");
        for (var d : gd.getDegrees()) {
            System.out.println(Arrays.toString(d));
        }
        for (var p : gd.getPreferences()) {
            System.out.println(Arrays.toString(p));
        }
    }
}
