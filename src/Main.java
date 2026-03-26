import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        var features = 3;

        var data = new JSONObject(Files.readString(Paths.get("batches.json")));
        var ratingSum = data.getDouble("ratingSum");
        var ratingNum = data.getInt("ratingNum");
        var persons = data.getInt("persons");
        var batches = data.getJSONArray("batches");

        var trainRatings = new Double[persons][persons];
        var trainSize = 0;
        var testRatings = new Double[persons][persons];
        var testSize = 0;
        for (int i = 0; i < batches.length(); i++) {
            var entries = batches.getJSONArray(i);
            var size = entries.length();
            if (i == 9) { testSize += size; }
            else { trainSize += size; }

            var ratingsSet = i == 9 ? testRatings : trainRatings;
            for (int j = 0; j < size; j++) {
                var entry = entries.getJSONObject(j);
                ratingsSet[entry.getInt("p")][entry.getInt("q")] = entry.getDouble("r");
            }
        }

        var initialVal = Math.sqrt(Math.abs(ratingSum / ratingNum / features));
        var degrees = new double[persons][features];
        var preferences = new double[persons][features];

        for (int i = 0; i < persons; i++) {
            for (int j = 0; j < features; j++) {
                degrees[i][j] = initialVal * (Math.random() / 5 + 0.9) * (Math.floor(Math.random() * 2) - 1);
                preferences[i][j] = initialVal * (Math.random() / 5 + 0.9) * (Math.floor(Math.random() * 2) - 1);
            }
        }

        var gd = new GradientDescent(trainRatings, degrees, preferences);
        var rater = gd.gradientDescent();
        System.out.println(rater);
        System.out.println("Final Train: " + rater.getFinalVal());
        gd.setRatings(testRatings);
        System.out.println("Final Test: " + gd.targetFn());
        System.out.println("Test to Train: " + (gd.targetFn() / testSize) / (rater.getFinalVal() / trainSize));
    }
}
