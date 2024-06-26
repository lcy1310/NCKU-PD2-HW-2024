import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RegExp {
    public static void main(String[] args) {
        String str1 = args[1];
        String str2 = args[2];
        int s2Count = Integer.parseInt(args[3]);

        //For your testing of input correctness
        //System.out.println("The input file:"+args[0]);
        //System.out.println("str1="+str1);
        //System.out.println("str2="+str2);
        //System.out.println("num of repeated requests of str2 = "+s2Count);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String line;
            while ((line = reader.readLine()) != null) {
                //You main code should be invoked here

                if (isPalindrome(line)) {
                    System.out.print("Y,");
                } else {
                    System.out.print("N,");
                }

                if (stringContainStr1(line, str1)) {
                    System.out.print("Y,");
                } else {
                    System.out.print("N,");
                }

                int count = counterForStr2(line, str2);
                if (count >= s2Count) {
                    System.out.print("Y,");
                } else {
                    System.out.print("N,");
                }

                if(findTheFormatOfAmXB2m(line)){
                    System.out.println("Y");
                } else {
                    System.out.println("N");
                }

               // System.out.println(line);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static boolean isPalindrome(String str) {
        str = str.toLowerCase();
        int left = 0;
        int right = str.length() - 1;

        while (left < right) {
            if (str.charAt(left) != str.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }

        return true;
    }

    private static boolean stringContainStr1(String source, String target) {
        int sourceLength = source.length();
        int targetLength = target.length();

        for (int i = 0; i <= sourceLength - targetLength; i++) {
            boolean match = true;

            for (int j = 0; j < targetLength; j++) {
                if (Character.toLowerCase(source.charAt(i + j)) != Character.toLowerCase(target.charAt(j))) {
                    match = false;
                    break;
                }
            }

            if (match) {
                return true;
            }
        }

        return false;
    }

    private static int stringContainStr2AndCount(String source, String target, int startIndex) {
        int sourceLength = source.length();

        for (int i = startIndex; i <= sourceLength - target.length(); i++) {
            boolean match = true;

            for (int j = 0; j < target.length(); j++) {
                if (Character.toLowerCase(source.charAt(i + j)) != Character.toLowerCase(target.charAt(j))) {
                    match = false;
                    break;
                }
            }

            if (match) {
                return i;
            }
        }

        return -1;
    }

    private static int counterForStr2(String source, String target) {
        int count = 0;
        int index = 0;
        int targetLength = target.length();

        while ((index = stringContainStr2AndCount(source, target, index)) != -1) {
            count++;
            index += targetLength;
        }

        return count;
    }

    private static boolean findTheFormatOfAmXB2m(String source){
        int left=0;
        int right=source.length()-1;
        int aRepeat=0;
        int bRepeat=0;

        for(left=0; left<source.length(); left++){
            if(Character.toLowerCase(source.charAt(left))== 'a'){
                aRepeat++;
                break;
            }
        }

        if(aRepeat<1){
            return false;
        }

        for(right=source.length()-1; right>left; right--){
            if(Character.toLowerCase(source.charAt(right))== 'b' && Character.toLowerCase(source.charAt(right-1))== 'b'){
                bRepeat=2;
                return true;
            }
        }
        if(bRepeat!=2){
            return false;
        }else{
            return true;
        }

    }

}