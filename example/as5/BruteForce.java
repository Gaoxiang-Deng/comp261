public class BruteForce {
    String pattern;
    String text;
    public BruteForce(String pattern, String text) {
        // TODO maybe fill this in.
        this.pattern = pattern;
        this.text = text;
    }

    public int search(String pattern, String text) {
        if(pattern.length() == 0 || text.length() == 0){
            return -1;
        }
        int plength = pattern.length();
        int tlength = text.length();
        for(int t = 0; t <= tlength - plength; t++){
            boolean found = true;
            for(int i = 0; i <= plength - 1; i++){
                if(pattern.charAt(i) != text.charAt(t+i)) {
                    found = false;
                    break;
                }
            }
            if(found){
                return t;
            }

        }

       return -1;
    }
}


