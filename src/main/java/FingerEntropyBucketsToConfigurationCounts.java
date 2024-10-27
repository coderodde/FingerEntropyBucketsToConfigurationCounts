public class FingerEntropyBucketsToConfigurationCounts {
    
    private static final double DEFAULT_BUCKET_WIDTH = 0.01;

    public static void main(String[] args) {
        
        System.out.println("# --- Entropy buckets N = 25 ---");
        run(25, DEFAULT_BUCKET_WIDTH);
        
        System.out.println();
        
        System.out.println("# --- Entropy buckets N = 36 ---");
        run(36, DEFAULT_BUCKET_WIDTH);
        
        System.out.println();
        
        System.out.println("# --- Entropy buckets N = 49 ---");
        run(49, DEFAULT_BUCKET_WIDTH);
    }
    
    private static int[] getEntropyBuckets(final int listSize,
                                           final double bucketWidth) {
        final Fingers fingers = new Fingers(listSize);
        final int[] entropyBuckets = getEntropyBucketArray(0.01);
        
        do {
            entropyBuckets[getEntropyBucketIndex(fingers.getEntropy(),
                                                 bucketWidth)]++;
        } while (fingers.advance());
        
        return entropyBuckets;
    }
    
    private static void printEntropyBuckets(final int[] entropyBuckets,
                                            final double bucketWidth) {
        double entropy = -1.0;
        
        for (final int bucketCount : entropyBuckets) {
            System.out.println(
                    String.format(
                            "%.2f %d", 
                            entropy, 
                            bucketCount)
                            .replaceAll(",", "."));
            
            entropy += bucketWidth;
        }
    }
    
    private static void run(final int listSize,
                            final double bucketWidth) {
        final int[] entropyBuckets = getEntropyBuckets(listSize,
                                                       bucketWidth);
        printEntropyBuckets(
                entropyBuckets,
                bucketWidth);
        
        System.out.printf(
                "# Optimal split entropy: %.2f\n", 
                getOptimalSumEntropy(
                        entropyBuckets, 
                        bucketWidth));
    }
    
    private static int[] getEntropyBucketArray(final double bucketWidth) {
        return new int[(int)(2.0 / bucketWidth) + 1];
    }
    
    private static int getEntropyBucketIndex(final double entropy,
                                             final double bucketWidth) {
        return (int) ((entropy + 1.0) / bucketWidth);
    }
    
    private static final class Fingers {
        private final int[] fingerIndices;
        private final int listLength;
        
        Fingers(int numberOfElementsInList) {
            this.listLength = numberOfElementsInList;
            
            int numberOfFingers = 
                    (int) Math.ceil(Math.sqrt(numberOfElementsInList));
            
            this.fingerIndices = new int[numberOfFingers];
            
            for (int i = 1; i < this.fingerIndices.length; i++) {
                fingerIndices[i] = i;
            }
        }
        
        double getEntropy() {
            double sum = 0.0;
            
            for (int i = 0; i < fingerIndices.length - 1; i++) {
                sum += Math.abs(fingerIndices[i + 1] - 
                                fingerIndices[i] -
                                fingerIndices.length);
            }
            
            return 1.0 - sum / listLength;
        }
        
        boolean advance() {
            for (int i = fingerIndices.length - 1; i >= 0; i--) {
                if (fingerIndices[i] < 
                        listLength - fingerIndices.length + i) {
                    
                    fingerIndices[i]++;
                    
                    for (int j = i + 1; j < fingerIndices.length; j++) {
                        fingerIndices[j] = fingerIndices[j - 1] + 1;
                    }
                    
                    return true;
                }
            }
            
            return false;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            
            for (int index : fingerIndices) {
                if (first) {
                    first = false; // Turn off the 'first' flag.
                } else {
                    sb.append(", ");
                }
                
                sb.append(index);
            }
            
            return sb.append("]").toString();
        }
    }
    
    private static double[] getEntropies(int listLength) {
        int numberOfFingers = getNumberOfFingers(listLength);
        double[] entropies = new double[binomial(listLength, numberOfFingers)];
        Fingers fingers = new Fingers(listLength);
        
        for (int i = 0; i != entropies.length; i++) {
            entropies[i] = fingers.getEntropy();
            fingers.advance();
        }
        
        return entropies;
    }
    
    private static int getNumberOfFingers(int listLength) {
        return (int) Math.ceil(Math.sqrt(listLength));
    }
    
    private static int factorial(int n) {
        if (n < 2) {
            return 1;
        }
        
        return n * factorial(n - 1);
    }
    
    private static int binomial(int n, int k) {
        return factorial(n) / factorial(n - k) / factorial(k);
    }
    
    private static double getOptimalSumEntropy(final int[] entropyBuckets,
                                               final double bucketWidth) {
        int bestSplitIndex = -1;
        int bestSplitValue = Integer.MAX_VALUE;
        
        for (int splitIndex = 0;
                 splitIndex < entropyBuckets.length; 
                 splitIndex++) {
            
            final int difference = getSplitDifference(entropyBuckets, 
                                                      splitIndex);
            
            if (bestSplitValue > difference) {
                bestSplitValue = difference;
                bestSplitIndex = splitIndex;
            }
        }
        
        return -1.0 + (double)(bestSplitIndex) * bucketWidth;
    }
    
    private static int getSplitDifference(final int[] entropyBuckets,
                                          final int splitIndex) {
        int sum1 = 0;
        int sum2 = 0;
        
        for (int i = 0; i < splitIndex; i++) {
            sum1 += entropyBuckets[i];
        }
        
        for (int i = splitIndex; i < entropyBuckets.length; i++) {
            sum2 += entropyBuckets[i];
        }
        
        return Math.abs(sum1 - sum2);
    }
    
    private static void lexicographicOrderStatistics() {
        System.out.println(
                "--- Finger configuration entropies as configurations grow " + 
                "in lexicographic order. ---");
        
        double[] entropies = getEntropies(10);
        int lineNumber = 0;
            
        for (double entropy : entropies) {
            System.out.printf(
                    "%4d %s\n", 
                    lineNumber++, 
                    Double.toString(entropy).replace(',', '.'));
        }
    }
}
