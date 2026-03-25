import java.util.Arrays;

public class GradientDescent {
    private Double[][] ratings = {
        { null, -14.0, null, 1.0 },
        { -6.0, null, null, -2.0 },
        { null, -22.0, null, null },
        { null, -8.0, 0.0, null }
    };
    private double[][] degrees = {
        { 2, 3 },
        { -5, -2 },
        { -4, -3 },
        { 1, 2 }
    };
    private double[][] preferences = {
        { 3, -1 },
        { -3, 0 },
        { 4, 3 },
        { 3, -4 }
    };

    public double[][] getDegrees() {
        return degrees;
    }

    public double[][] getPreferences() {
        return preferences;
    }

    private double getRatingError(int p, int q) {
        final var FEATURES = preferences[0].length;

        var sum = 0.0;
        for (var f = 0; f < FEATURES; f++) {
            sum += preferences[p][f] * degrees[q][f];
        }
        return sum - ratings[p][q];
    }

    private double[] getGradient() {
        final var FEATURES = preferences[0].length;
        final var PERSONS = ratings.length;

        var gradient = new double[2 * FEATURES * PERSONS];
        var i = 0;

        // X
        for (int p = 0; p < PERSONS; p++) {
            for (int f = 0; f < FEATURES; f++) {
                var sum = 0.0;
                for (int q = 0; q < PERSONS; q++) {
                    var rqp = ratings[q][p];
                    if (rqp != null) {
                        sum += 2 * getRatingError(q, p) * preferences[q][f];
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
                        sum += 2 * getRatingError(p, q) * degrees[q][f];
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

    public int gradientDescent() {
        final var FEATURES = preferences[0].length;
        final var PERSONS = ratings.length;
        final var RHO = 0.5;
        final var SIGMA = 0.0001;

        double target = 42;
        var its = 0;
        while (target > 0.01) {
            its++;
            var gradient = getGradient();

            var ALPHA = 1 / RHO;

            var length = 0.0;
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

            target = targetFn(degrees, preferences);

            degrees = nextDegrees;
            preferences = nextPreferences;
        }
        return its;
    }
}
