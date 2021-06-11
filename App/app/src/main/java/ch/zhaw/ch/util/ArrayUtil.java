package ch.zhaw.ch.util;

public class ArrayUtil {

    public static float[] getSubArray(float[] array, int min, int max) {
        float[] res = new float[max - min];
        for (int i = 0; i < max - min; i++) {
            res[i] = array[i+min];
        }
        return res;
    }
    public static double[] getSubArray(double[] array, int min, int max) {
        double[] res = new double[max - min];
        for (int i = 0; i < max - min; i++) {
            res[i] = array[i+min];
        }
        return res;
    }
    public static int[] range(int length) {
        int[] res = new int[length];
        for (int i = 0; i < length; i++) {
            res[i] = i;
        }
        return res;
    }

    public static int[] range(int length, int offset) {
        int[] res = new int[length];
        for (int i = 0; i < length; i++) {
            res[i] = i + offset;
        }
        return res;
    }

    public static int[] range(int length, int offset, int step) {
        int[] res = new int[length];
        for (int i = 0; i < length; i++) {
            res[i] = i * step + offset;
        }
        return res;
    }

    public static float[] range(int length, int offset, float step) {
        float[] res = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = i * step + offset;
        }
        return res;
    }

    public static int[] rangeOfValue(int length, int val) {
        int[] res = new int[length];
        for (int i = 0; i < length; i++) {
            res[i] = val;
        }
        return res;
    }


    public static float[] rangeOfValue(int length, float val) {
        float[] res = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = val;
        }
        return res;
    }

    public static double[] rangeOfValue(int length, double val) {
        double[] res = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = val;
        }
        return res;
    }

    public static int[] floor(float[] a) {
        int length = a.length;
        int res[] = new int[length];
        for (int i = 0; i < length; i++) {
            res[i] = (int) a[i];
        }
        return res;
    }

    public static float[] abs(float[] a) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = Math.abs(a[i]);
        }
        return res;
    }

    public static double[] abs(double[] a) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = Math.abs(a[i]);
        }
        return res;
    }

    public static int[] floatToInt(float[] a) {
        int length = a.length;
        int res[] = new int[length];
        for (int i = 0; i < length; i++) {
            res[i] = (int) a[i];
        }
        return res;
    }

    public static double[] floatToDouble(float[] a) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i];
        }
        return res;
    }

    public static float[] intToFloat(int[] a) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i];
        }
        return res;
    }

    public static float[] doubleToFloat(double[] a) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = (float) a[i];
        }
        return res;
    }

    public static int[] add(int[] a, int b) {
        int length = a.length;
        int res[] = new int[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] + b;
        }
        return res;
    }

    public static int[] sub(int[] a, int b) {
        int length = a.length;
        int res[] = new int[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] - b;
        }
        return res;
    }

    public static int[] mul(int[] a, int b) {
        int length = a.length;
        int res[] = new int[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] * b;
        }
        return res;
    }

    public static float[] div(int[] a, int b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] / (float) b;
        }
        return res;
    }

    public static double[] add(int[] a, double b) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] + b;
        }
        return res;
    }

    public static double[] sub(int[] a, double b) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] - b;
        }
        return res;
    }

    public static double[] mul(int[] a, double b) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] * b;
        }
        return res;
    }

    public static double[] div(int[] a, double b) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] / b;
        }
        return res;
    }

    public static float[] add(int[] a, float b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] + b;
        }
        return res;
    }

    public static float[] sub(int[] a, float b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] - b;
        }
        return res;
    }

    public static float[] mul(int[] a, float b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] * b;
        }
        return res;
    }

    public static float[] div(int[] a, float b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] / b;
        }
        return res;
    }

    public static int[] add(int[] a, int[] b) {
        int length = a.length;
        int res[] = new int[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] + b[i];
        }
        return res;
    }

    public static int[] sub(int[] a, int[] b) {
        int length = a.length;
        int res[] = new int[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] - b[i];
        }
        return res;
    }

    public static int[] mul(int[] a, int[] b) {
        int length = a.length;
        int res[] = new int[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] * b[i];
        }
        return res;
    }

    public static float[] div(int[] a, int[] b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] / (float) b[i];
        }
        return res;
    }

    public static float[] add(int[] a, float[] b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] + b[i];
        }
        return res;
    }

    public static float[] sub(int[] a, float[] b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] - b[i];
        }
        return res;
    }

    public static float[] mul(int[] a, float[] b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] * b[i];
        }
        return res;
    }

    public static float[] div(int[] a, float[] b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] / b[i];
        }
        return res;
    }

    public static float[] add(float[] a, float b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] + b;
        }
        return res;
    }

    public static float[] sub(float[] a, float b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] - b;
        }
        return res;
    }

    public static float[] mul(float[] a, float b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] * b;
        }
        return res;
    }

    public static float[] div(float[] a, float b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] / b;
        }
        return res;
    }


    public static double[] add(float[] a, double b) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] + b;
        }
        return res;
    }

    public static double[] sub(float[] a, double b) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] - b;
        }
        return res;
    }

    public static double[] mul(float[] a, double b) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] * b;
        }
        return res;
    }

    public static double[] div(float[] a, double b) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] / b;
        }
        return res;
    }

    public static float[] add(float[] a, int b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] + b;
        }
        return res;
    }

    public static float[] sub(float[] a, int b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] - b;
        }
        return res;
    }

    public static float[] mul(float[] a, int b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] * b;
        }
        return res;
    }

    public static float[] div(float[] a, int b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] / b;
        }
        return res;
    }

    public static float[] add(float[] a, float[] b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] + b[i];
        }
        return res;
    }

    public static float[] sub(float[] a, float[] b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] - b[i];
        }
        return res;
    }

    public static float[] mul(float[] a, float[] b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] * b[i];
        }
        return res;
    }

    public static float[] div(float[] a, float[] b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] / b[i];
        }
        return res;
    }

    public static double[] add(double[] a, double b) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] + b;
        }
        return res;
    }

    public static double[] sub(double[] a, double b) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] - b;
        }
        return res;
    }

    public static double[] mul(double[] a, double b) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] * b;
        }
        return res;
    }

    public static double[] div(double[] a, double b) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] / b;
        }
        return res;
    }

    public static double[] add(double[] a, double b[]) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] + b[i];
        }
        return res;
    }

    public static double[] sub(double[] a, double b[]) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] - b[i];
        }
        return res;
    }

    public static double[] mul(double[] a, double b[]) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] * b[i];
        }
        return res;
    }

    public static double[] div(double[] a, double b[]) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] / b[i];
        }
        return res;
    }

    public static double[] add(float[] a, double[] b) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] + b[i];
        }
        return res;
    }

    public static double[] sub(float[] a, double[] b) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] - b[i];
        }
        return res;
    }

    public static double[] mul(float[] a, double[] b) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] * b[i];
        }
        return res;
    }

    public static double[] div(float[] a, double[] b) {
        int length = a.length;
        double res[] = new double[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] / b[i];
        }
        return res;
    }


    public static float[] add(float[] a, int[] b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] + b[i];
        }
        return res;
    }

    public static float[] sub(float[] a, int[] b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] - b[i];
        }
        return res;
    }

    public static float[] mul(float[] a, int[] b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] * b[i];
        }
        return res;
    }

    public static float[] div(float[] a, int[] b) {
        int length = a.length;
        float res[] = new float[length];
        for (int i = 0; i < length; i++) {
            res[i] = a[i] / b[i];
        }
        return res;
    }


    public static void add2(float[] a, float[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] += b[i];
        }
    }

    public static void sub2(float[] a, float[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] -= b[i];
        }
    }

    public static void mul2(float[] a, float[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] *= b[i];
        }
    }

    public static void div2(float[] a, float[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] /= b[i];
        }
    }

    public static void add2(float[] a, float b) {
        for (int i = 0; i < a.length; i++) {
            a[i] += b;
        }
    }

    public static void sub2(float[] a, float b) {
        for (int i = 0; i < a.length; i++) {
            a[i] -= b;
        }
    }

    public static void mul2(float[] a, float b) {
        for (int i = 0; i < a.length; i++) {
            a[i] *= b;
        }
    }

    public static void div2(float[] a, float b) {
        for (int i = 0; i < a.length; i++) {
            a[i] /= b;
        }
    }

    public static void add2(double[] a, double[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] += b[i];
        }
    }

    public static void sub2(double[] a, double[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] -= b[i];
        }
    }

    public static void mul2(double[] a, double[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] *= b[i];
        }
    }

    public static void div2(double[] a, double[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] /= b[i];
        }
    }


    public static void add2(double[] a, double b) {
        for (int i = 0; i < a.length; i++) {
            a[i] += b;
        }
    }

    public static void sub2(double[] a, double b) {
        for (int i = 0; i < a.length; i++) {
            a[i] -= b;
        }
    }

    public static void mul2(double[] a, double b) {
        for (int i = 0; i < a.length; i++) {
            a[i] *= b;
        }
    }

    public static void div2(double[] a, double b) {
        for (int i = 0; i < a.length; i++) {
            a[i] /= b;
        }
    }

    public static void add2(double[] a, float[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] += b[i];
        }
    }

    public static void sub2(double[] a, float[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] -= b[i];
        }
    }

    public static void mul2(double[] a, float[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] *= b[i];
        }
    }

    public static void div2(double[] a, float[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] /= b[i];
        }
    }

    public static float max(float[] a) {
        float max = a[0];
        for (int i = 1; i < a.length; i++) {
            max = Float.max(max, a[i]);
        }
        return max;
    }

    public static float min(float[] a) {
        float min = a[0];
        for (int i = 1; i < a.length; i++) {
            min = Float.min(min, a[i]);
        }
        return min;
    }

    public static float sum(float[] a) {
        float sum = a[0];
        for (int i = 1; i < a.length; i++) {
            sum += a[i];
        }
        return sum;
    }

}
