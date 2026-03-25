import java.util.Arrays;

public class GradientDescent {
    private final Double[][] ratings;
    private double[][] degrees;
    private double[][] preferences;

    public GradientDescent(Double[][] ratings, double[][] degrees, double[][] preferences) {
        this.ratings = ratings;
        this.degrees = degrees;
        this.preferences = preferences;
    }

    public double[][] getDegrees() {
        return degrees;
    }

    public double[][] getPreferences() {
        return preferences;
    }

    private double[] getGradient() {
        final var FEATURES = preferences[0].length;
        final var PERSONS = ratings.length;

        var gradient = new double[2 * FEATURES * PERSONS];
        var i = 0;
        
        var ratingError = new double[PERSONS][PERSONS];
        for (int p = 0; p < PERSONS; p++) {
            for (int q = 0; q < PERSONS; q++) {
                var rpq = ratings[p][q];
                if (rpq != null) {
                    ratingError[p][q] = 0.0;
                    for (var f = 0; f < FEATURES; f++) {
                        ratingError[p][q] += preferences[p][f] * degrees[q][f];
                    }
                    ratingError[p][q] -= ratings[p][q];
                }
            }
        }

        // X
        for (int p = 0; p < PERSONS; p++) {
            for (int f = 0; f < FEATURES; f++) {
                var sum = 0.0;
                for (int q = 0; q < PERSONS; q++) {
                    var rqp = ratings[q][p];
                    if (rqp != null) {
                        sum += 2 * ratingError[q][p] * preferences[q][f];
                    }
                }
                gradient[i++] = sum;
            }
        }

        // W
        for (int p = 0; p < PERSONS; p++) {
            for (int f = 0; f < FEATURES; f++) {
                var sum = 0.0;
                for (int q = 0; q < PERSONS; q++) {
                    var rpq = ratings[p][q];
                    if (rpq != null) {
                        sum += 2 * ratingError[p][q] * degrees[q][f];
                    }
                }
                gradient[i++] = sum;
            }
        }

        return gradient;
    }

    private double targetFn(double[][] degrees, double[][] preferences) {
        final var FEATURES = preferences[0].length;
        final var PERSONS = ratings.length;

        var diff = 0.0;
        for (int p = 0; p < PERSONS; p++) {
            for (int q = 0; q < PERSONS; q++) {
                var rpq = ratings[p][q];
                if (rpq != null) {
                    var expected = 0.0;
                    for (int f = 0; f < FEATURES; f++) {
                        expected += preferences[p][f] * degrees[q][f];
                    }
                    diff += (rpq - expected) * (rpq - expected);
                }
            }
        }
        return diff;
    }

    public void gradientDescent() {
        final var FEATURES = preferences[0].length;
        final var PERSONS = ratings.length;
        final var RHO = 0.5;
        final var SIGMA = 0.0001;

        System.out.println("Initial result: " + targetFn(degrees, preferences));

        double length = 42;
        var its = 0;
        while (length > 0.01) {
            its++;
            var gradient = getGradient();

            var ALPHA = 1 / RHO;

            length = 0.0;
            for (var g : gradient) {
                length += g * g;
            }

            var nextDegrees = new double[PERSONS][FEATURES];
            var nextPreferences = new double[PERSONS][FEATURES];

            do {
                ALPHA *= RHO;
                for (int i = 0; i < FEATURES * PERSONS; i++) {
                    var p = i / FEATURES;
                    var f = i % FEATURES;
                    nextDegrees[p][f] = degrees[p][f] - gradient[i] * ALPHA;
                }
                for (int i = 0; i < FEATURES * PERSONS; i++) {
                    var p = i / FEATURES;
                    var f = i % FEATURES;
                    nextPreferences[p][f] = preferences[p][f] - gradient[i + FEATURES * PERSONS] * ALPHA;
                }
            } while (targetFn(nextDegrees, nextPreferences) > targetFn(degrees, preferences) - ALPHA * SIGMA * length);

            degrees = nextDegrees;
            preferences = nextPreferences;
        }
        System.out.println("Final result: " + targetFn(degrees, preferences));
        System.out.println("Iterations: " + its);
    }
}
