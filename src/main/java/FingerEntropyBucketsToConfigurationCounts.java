public class FingerEntropyBucketsToConfigurationCounts {

    public static void main(String[] args) {
        Fingers fingers = new Fingers(50);
        int[] entropyBuckets = new int[201];
        
        do {
            entropyBuckets[getEntropyBucket(fingers.getEntropy())]++;
        } while (fingers.advance()); 
        
        double bucket = -1.0;
        
        for (int i : entropyBuckets) {
            System.out.println(bucket + " " + i);
            bucket += 0.02;
        }
        
        System.out.println("yeah");
    }
    
    private static int getEntropyBucket(double entropy) {
        return (int) ((entropy + 1.0) * 100);
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
                        listLength - (fingerIndices.length - i)) {
                    
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
}
