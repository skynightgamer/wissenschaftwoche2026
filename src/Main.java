import java.util.Arrays;
import java.util.Timer;

public class Main {
    public static void main(String[] args) {
        var t = System.nanoTime();
        var gd = new GradientDescent();
        var its = gd.gradientDescent();
        System.out.println((System.nanoTime() - t) / 10_000 + "ms" + ", iterations: " + its);
        for (var d : gd.getDegrees()) {
            System.out.println(Arrays.toString(d));
        }
        for (var p : gd.getPreferences()) {
            System.out.println(Arrays.toString(p));
        }
    }
}
