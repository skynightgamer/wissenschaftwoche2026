import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

public class Batcher {
    public static void main(String[] args) throws IOException {
        var NUM_BATCHES = 10;

        var lines = Files.readAllLines(Paths.get("data.txt"));
        var root = new JSONObject();
        root.put("persons", Integer.parseInt(lines.get(0)));
        lines.remove(0);

        //var entries = new JSONArray();
        var givenRatings = new LinkedList<JSONObject>();
        var pattern = Pattern.compile("\\((\\d*),(\\d*)\\) (-?[0-9.]*)");
        AtomicReference<Double> ratingSum = new AtomicReference<>(0.0);
        int ratingNum = lines.size();
        lines.stream()
            .map(pattern::matcher)
            .filter(Matcher::matches)
            .forEach(m -> {
                var entry = new JSONObject();
                entry.put("p", Integer.parseInt(m.group(1)));
                entry.put("q", Integer.parseInt(m.group(2)));
                entry.put("r", Double.parseDouble(m.group(3)));
                ratingSum.updateAndGet(v -> v + Double.parseDouble(m.group(3)));
                givenRatings.add(entry);
            });
        root.put("ratingNum", ratingNum);
        root.put("ratingSum", ratingSum);
        var batches = new LinkedList[NUM_BATCHES];
        for (int i = 0; i < 10; i++) {
            batches[i] = new LinkedList<JSONObject>();
        }
        var maxBatchSize = ratingNum / NUM_BATCHES;
        givenRatings.forEach(r -> {
            var availableBatches = new LinkedList[NUM_BATCHES];
            var i = 0;
            for (var batch : batches) {
                if (batch.size() < maxBatchSize) availableBatches[i++] = batch;
            }
            if (i == 0) {
                for (var batch : batches) {
                    if (batch.size() == maxBatchSize) availableBatches[i++] = batch;
                }
            }
            availableBatches[(int) (Math.random() * i)].add(r);
        });
        root.put("batches", batches);
        var jsonFile = Paths.get("batches.json");
        if (!Files.exists(jsonFile)) Files.createFile(jsonFile);
        Files.writeString(jsonFile, root.toString(4));
    }
}
