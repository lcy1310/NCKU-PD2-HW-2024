import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Path;//////////////////////////////////////////////////////


//put data in data arraylist

public class HtmlParserfinish {
    private static List<String> variance = new ArrayList<>();
    private static List<String> top3value = new ArrayList<>();
    private static List<Double> top3valueDouble = new ArrayList<>();
    private static List<String> top3stock= new ArrayList<>();
    private static List<String> b0ANDb1 = new ArrayList<>();
    private static List<String> names = new ArrayList<>();
    private static List<String> dataRead = new ArrayList<>();
    private static List<String> reserve = new ArrayList<>();
    private static List<Double> varianceseries=new ArrayList<>();
    private static boolean isFirstRead = true;
    private static boolean fullOrNot=false;
    public static void main(String[] args) {
        createIfNotExists("output.csv");
        createIfNotExists("data.csv");

        String csvFilePath = "data.csv";
        boolean containsAlphabets = isContainChar(csvFilePath);

        double average, variancedata;
        int mode=Integer.parseInt(args[0]);
        int taskmode=-1;
        String stockTarget="";
        int startDay=-1;
        int endDay=-1;
        if(mode!=0){
            taskmode=Integer.parseInt(args[1]);
            if(taskmode!=0){
                stockTarget=args[2];
                startDay=Integer.parseInt(args[3]);
                endDay=Integer.parseInt(args[4]);
            }
        }
        ArrayList<Double> dataGiveFunc = new ArrayList<>();
        ArrayList<String> name = new ArrayList<>();
        ArrayList<Double> original = new ArrayList<>();
        ArrayList<String> movingAverage= new ArrayList<>();
        if(mode==0){
            try {
                Document doc = Jsoup.connect("https://pd2-hw3.netdb.csie.ncku.edu.tw").get();
                Element table = doc.select("table").first();
                Elements rows = table.select("tr");
                for (Element row : rows) {
                    Elements cells = row.select("th,td");
                    StringBuilder csvRow = new StringBuilder();
                    for (Element cell : cells) {
                        csvRow.append(",").append(cell.text());
                    }
                    String[] parts = csvRow.toString().split(",");
                    if (isFirstRead) {
                        isFirstRead = false;
                        names.add(parts[1]);
                        for (int i = 2; i < parts.length; i++) {
                            names.add(parts[i]);
                        }
                        if(!containsAlphabets){
                            writeDataToFile(names);
                        }

                    } else {
                        for (int i = 1; i < parts.length; i++) {
                            dataRead.add(parts[i]);
                            reserve.add(parts[i]);
                        }
                    }
                }
                fullOrNot=has31Lines(csvFilePath);
                if(!fullOrNot){
                    writeDataToFile(dataRead);
                }
                reserve.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if(mode!=0 && taskmode==0){
            List<String> dataNames = new ArrayList<>();
            List<Double> dataList = new ArrayList<>();
            readCSVToArrayListWithDouble(csvFilePath, dataNames, dataList);
            //System.out.println(dataNames);
            //writeStringsToCSV(dataNames, "output.csv");
            //writeDoublesToCSV(dataList, "output.csv");
            try{
                copyCSVFile("data.csv", "output.csv");
            }catch (IOException e) {
                    e.printStackTrace();
                }
        }
        if(mode!=0 && taskmode==1){
           readCSVToArrayListWithDouble("data.csv", name, original);
           //System.out.println(original);
            int place=name.indexOf(stockTarget);
            int firstday=startDay;
            for(;firstday+4<=endDay;firstday++){
                for(int i=place+((firstday-1)*name.size()); i<name.size()*(firstday+4);i=i+name.size()){
                    dataGiveFunc.add(original.get(i));
                }
                average=task1(dataGiveFunc);
                dataGiveFunc.clear();
                if(average==(int)average){
                    movingAverage.add(Integer.toString((int)average));    
                }else{
                    movingAverage.add(Double.toString(average));
                }
                //movingAverage.add(average);
            }
            List<String> dataNames = new ArrayList<>();
            dataNames.add(stockTarget);
            dataNames.add(Integer.toString(startDay));
            dataNames.add(Integer.toString(endDay));
            writeStringsToCSV(dataNames, "output.csv");
            //writeDoublesToCSV(movingAverage, "output.csv");
            writeStringsToCSV(movingAverage, "output.csv");
            movingAverage.clear();
            dataNames.clear();

        }
        if(mode!=0 && taskmode==2){
            readCSVToArrayListWithDouble("data.csv", name, original);
            int place=name.indexOf(stockTarget);
            for(int i=place+((startDay-1)*name.size()); i<name.size()*endDay; i=i+name.size()){
                dataGiveFunc.add(original.get(i));
            }
            //System.out.println(dataGiveFunc);//
            variancedata=task2(dataGiveFunc);
            //System.out.println(dataGiveFunc);
            if(variancedata==(int)variancedata){
                variance.add(Integer.toString((int)variancedata));
            }else{
                variance.add(Double.toString(variancedata));
            }
            //variance.add(variancedata);
            dataGiveFunc.clear();
            List<String> dataNames = new ArrayList<>();
            dataNames.add(stockTarget);
            dataNames.add(Integer.toString(startDay));
            dataNames.add(Integer.toString(endDay));
            writeStringsToCSV(dataNames, "output.csv");
            //writeDoublesToCSV(variance, "output.csv");
            writeStringsToCSV(variance, "output.csv");
            variance.clear();
            dataNames.clear();

        }
        if(mode!=0 && taskmode==3){
            readCSVToArrayListWithDouble("data.csv", name, original);
            task3(name, original, startDay, endDay);
            dataGiveFunc.clear();
            top3stock.add(Integer.toString(startDay));
            top3stock.add(Integer.toString(endDay));
            writeStringsToCSV(top3stock, "output.csv");
            //writeDoublesToCSV(top3value, "output.csv");
            writeStringsToCSV(top3value, "output.csv");
            top3stock.clear();
            top3value.clear();
            varianceseries.clear();
            top3valueDouble.clear();
        }
        if(mode!=0 && taskmode==4){
            readCSVToArrayListWithDouble("data.csv", name, original);
            int place=name.indexOf(stockTarget);
            for(int i=place+((startDay-1)*name.size()); i<name.size()*endDay; i=i+name.size()){
                dataGiveFunc.add(original.get(i));
            }
            task4(dataGiveFunc, startDay, endDay);
            dataGiveFunc.clear();
            List<String> dataNames = new ArrayList<>();
            dataNames.add(stockTarget);
            dataNames.add(Integer.toString(startDay));
            dataNames.add(Integer.toString(endDay));
            writeStringsToCSV(dataNames, "output.csv");
            //writeDoublesToCSV(b0ANDb1, "output.csv");
            writeStringsToCSV(b0ANDb1, "output.csv");
            b0ANDb1.clear();
            dataNames.clear();

        }
    }

    private static double task1 (ArrayList<Double> data){
        double average=0;
        for (int i=0; i<data.size();i++) {
            average=average+data.get(i);
        }
        average=average/5;
        average=processformat(average);
        return average;
    }

    private static double task2 (ArrayList<Double> data){
        double average=0, sum=0, placeholder;
        for (int i=0; i<data.size();i++) {
            average=average+data.get(i);
        }
        average=average/(double)data.size();
        for(int i=0; i<data.size();i++){
            placeholder=(data.get(i)-average)*(data.get(i)-average);
            sum=sum+placeholder;
        }
        sum=sum/(double)(data.size()-1);

        double precision = 0.0000000001;
        double start = 0;
        double end = sum;
        double mid = (start + end) / 2.0;
        if(sum>1){
                while (positive(mid * mid - sum) > precision) {
                        if (mid * mid < sum) {
                                start = mid;
                        } else {
                                end = mid;
                        }
                        mid = (start + end) / 2.0;
                }
                mid=processformat(mid);
        }
        if(sum==1){
                mid=1.0;
                mid=processformat(mid);
        }
        if(sum<1){
                start=sum;
                end=1.0;
                mid = (start + end) / 2.0;
                while (positive(mid * mid - sum) > precision) {
                        if (mid * mid < sum) {
                                start = mid;
                        } else {
                                end = mid;
                        }
                        mid = (start + end) / 2.0;
                }
                mid=processformat(mid);
        }
        return mid;
    }

    private static void task3(ArrayList<String> name,ArrayList<Double> original, int startDay, int endDay){
        sort(name, original, startDay, endDay);
        ArrayList<Double> copyList = new ArrayList<>(varianceseries);
        Collections.sort(copyList, Collections.reverseOrder());
        double placeholder=0;
        placeholder=copyList.get(0);
        if(copyList.get(0)==(int)placeholder){
            top3value.add(Integer.toString((int)placeholder));
        }else{
            top3value.add(Double.toString(copyList.get(0)));
        }

        placeholder=copyList.get(1);
        if(copyList.get(1)==(int)placeholder){
            top3value.add(Integer.toString((int)placeholder));
        }else{
            top3value.add(Double.toString(copyList.get(1)));
        }

        placeholder=copyList.get(2);
        if(copyList.get(2)==(int)placeholder){
            top3value.add(Integer.toString((int)placeholder));
        }else{
            top3value.add(Double.toString(copyList.get(2)));
        }

        top3valueDouble.add(copyList.get(0));
        top3valueDouble.add(copyList.get(1));
        top3valueDouble.add(copyList.get(2));
        int index1=varianceseries.indexOf(top3valueDouble.get(0));
        int index2=varianceseries.indexOf(top3valueDouble.get(1));
        int index3=varianceseries.indexOf(top3valueDouble.get(2));
        top3stock.add(name.get(index1));
        top3stock.add(name.get(index2));
        top3stock.add(name.get(index3));
    }

    private static void task4(ArrayList<Double> data, int startDay, int endDay){
        double average=0;
        int duration=endDay-startDay+1;
        double kid=0, mom=0;
        double averageTime=((double)startDay+(double)endDay)/2;
        for(int i=0; i<data.size(); i++){
            average=average+data.get(i);
        }
        average=average/(double)duration;
        for(int i=0; i<data.size(); i++){
            kid=kid+(((startDay+i)-averageTime)*(data.get(i)-average));
            mom=mom+(((startDay+i)-averageTime)*((startDay+i)-averageTime));
        }
        double b0, b1;
        b1=kid/mom;
        b0=average-(b1*averageTime);
        b1=processformat(b1);
        b0=processformat(b0);
        if(b1==(int)b1){
            b0ANDb1.add(Integer.toString((int)b1));
        }else{
            b0ANDb1.add(Double.toString(b1));
        }
        if(b0==(int)b0){
            b0ANDb1.add(Integer.toString((int)b0));
        }else{
            b0ANDb1.add(Double.toString(b0));
        }
        //b0ANDb1.add(b1);
        //b0ANDb1.add(b0);
    }

    private static double processformat(double number){
        if (number == (int) number) {
                //System.out.println(number);
                return (int) number;
        }else{
                DecimalFormat df = new DecimalFormat("#.##");
                String formatted = df.format(number);
                double answer=Double.parseDouble(formatted);
                return answer;
        }
    }

    private static double positive(double number){
        if(number>=0){
            return number;
        }else{
            return -number;
        }
    }

    private static void sort(ArrayList<String> name,ArrayList<Double> original, int startDay, int endDay){
        int stockNumber=name.size();
        int placeForStock=0, placeForPrice=0;
        double value;
        ArrayList<Double> data=new ArrayList<>();
        for(; placeForStock<stockNumber; placeForStock++){
            for(placeForPrice=placeForStock+(startDay-1)*stockNumber; placeForPrice<(stockNumber*endDay); placeForPrice=placeForPrice+stockNumber){
                data.add(original.get(placeForPrice));
            }
            value=task2(data);
            value=processformat(value);
            varianceseries.add(value);
            data.clear();
        }
    }

    //

    private static void writeDataToFile(List<String> target) throws IOException {
        FileWriter fw = new FileWriter("data.csv", true);
        BufferedWriter bw = new BufferedWriter(fw);
        for (int i = 0; i < target.size(); i++) {
            if (i != 0) {
                bw.write(",");
            }
            bw.write(target.get(i));
            if ((i + 1) % (names.size()) == 0) {
                bw.newLine();
            }
        }
        bw.close();
        fw.close();
    }

    public static boolean isContainChar(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            Pattern pattern = Pattern.compile("[a-zA-Z]");

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean has31Lines(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int lineCount = 0;
            while (reader.readLine() != null) {
                lineCount++;
            }
            return lineCount == 31;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void readCSVToArrayList(String filePath, List<String> dataNames, List<String> dataList) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                boolean isFirstLine = true;
                while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (isFirstLine) {
                                for (String part : parts) {
                                        dataNames.add(part.trim());
                                }
                                isFirstLine = false;
                        } else {
                                for (String part : parts) {
                                        dataList.add(part.trim());
                                }
                        }
                }
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    public static void readCSVToArrayListWithDouble(String filePath, List<String> dataNames, List<Double> dataList) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                boolean isFirstLine = true;
                while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (isFirstLine) {
                                for (String part : parts) {
                                        dataNames.add(part.trim());
                                }
                                isFirstLine = false;
                        } else {
                                for (String part : parts) {
                                        dataList.add(Double.parseDouble(part.trim()));
                                }
                        }
                }
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    //

    public static void writeStringsToCSV(List<String> list, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            int existingLines = (int) Files.lines(Paths.get(filename)).count();
            if (existingLines != 0) {
                writer.newLine();
            }
            for (int i = 0; i < list.size(); i++) {
                if (i != 0) {
                    writer.write(",");
                }
                writer.write(list.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeDoublesToCSV(List<Double> list, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            int existingLines = (int) Files.lines(Paths.get(filename)).count();
            if (existingLines != 0) {
                writer.newLine();
            }
            for (int i = 0; i < list.size(); i++) {
                if (i != 0) {
                    writer.write(",");
                }
                writer.write(String.valueOf(list.get(i)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////////////////////////////////

    public static void createIfNotExists(String filename) {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyCSVFile(String sourceFilename, String destinationFilename) throws IOException {
        try (
            BufferedReader reader = new BufferedReader(new FileReader(sourceFilename));
            BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFilename))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}