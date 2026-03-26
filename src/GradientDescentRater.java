public class GradientDescentRater {
    private double initialVal;
    private double finalVal;
    private int iterations;
    private long time;

    public GradientDescentRater(double initialVal, double finalVal, int iterations, long time) {
        this.initialVal = initialVal;
        this.finalVal = finalVal;
        this.iterations = iterations;
        this.time = time;
    }

    public double getInitialVal() {
        return initialVal;
    }

    public double getFinalVal() {
        return finalVal;
    }

    public int getIterations() {
        return iterations;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "initialVal: " + initialVal + " finalVal: " + finalVal + " time: " + time + "ms iterations: " + iterations;
    }
}
