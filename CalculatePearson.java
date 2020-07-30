package 数据库wordnet;

public class CalculatePearson {
    public static void main(String[] args) {
        int n = 40;     //数量
        double[] x1 = new double[]{
            0.1375,
            0.0774,
            0.1225,
            0.2063,
            0.1968,
            0.0902,
            0.2500,
            0.1059,
            0.0688,
            0.0709,
            0.1803,
            0.1613,
            0.2352,
            0.2560,
            0.2358,
            0.4614,
            0.2128,
            0.1099,
            0.2164,
            0.6111,
            0.3163,
            0.1388,
            0.2140,
            0.2112,
            0.1947,
            0.1825,
            0.2778,
            0.1063,
            0.1799,
            0.3015,
            0.2149,
            0.1350,
            0.2813,
            0.2255,
            0.1829,
            0.7565,
            0.6996,
            0.5854,
            0.7063,
            0.5021
        };
        double[] x2 = new double[]{
                0.16,
                0.09,
                0.2,
                0.63,
                0.09,
                0.26,
                0.26,
                0.08,
                0.09,
                0.34,
                0.32,
                0.12,
                0.72,
                0.48,
                0.38,
                0.42,
                0.34,
                0.14,
                0.42,
                0.61,
                0.35,
                0.21,
                0.29,
                0.27,
                0.4,
                0.44,
                0.31,
                0.23,
                0.18,
                0.53,
                0.46,
                0.22,
                0.73,
                0.52,
                0.44,
                0.71,
                0.65,
                0.89,
                0.53,
                0.78
        };
        double sumXY = 0d;
        double sumX = 0d;
        double sumY = 0d;
        double sumPowX = 0d;
        double sumPowY = 0d;
        for (int i = 0; i < n; i++) {
            double x = x1[i];
            double y = x2[i];
            sumXY += x * y;
            sumX += x;
            sumY += y;
            sumPowX += Math.pow(x, 2);
            sumPowY += Math.pow(y, 2);
        }
        double pearson = (sumXY - sumX * sumY / n) / Math.sqrt((sumPowX - Math.pow(sumX, 2) / n) * (sumPowY - Math.pow(sumY, 2) / n));
        System.out.println(pearson);
    }
}
