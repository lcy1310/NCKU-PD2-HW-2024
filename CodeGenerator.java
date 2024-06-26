import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.*;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {

    private static String titleName;
    private static boolean isChallengeData= false;
    private static List<String> classNameSet = new ArrayList<>();
    private static List<Integer> classNameTimes = new ArrayList<>();
    private static String linesForClass, linesForVariable, linesForFunction;
    public static void main(String[] args) {
        int challengePointClassTime=0;
        if (args.length == 0) {
            System.err.println("enter file name");
            return;
        }
        String fileName = args[0];
        System.out.println("File name: " + fileName);
        String mermaidCode = "";
        try {
            mermaidCode = Files.readString(Paths.get(fileName));
        }
        catch (IOException e) {
            System.err.println("cant read " + fileName);
            e.printStackTrace();
            return;
        }
        //////////////////////////////////////////////////////////////////////////
        //make mermaidcode int to string array
        if(mermaidCode.contains("{")){
            isChallengeData = true;
        }
        String[] lines = mermaidCode.split("\n");
        List<String> nonEmptyLines = new ArrayList<>();
        for (String line : lines) {
            line = line.trim().replaceAll("\\s+", " ");
            if (!line.isEmpty()) {
                nonEmptyLines.add(line);
            }
        }

        List<String> finalContent = new ArrayList<>();
        if(mermaidCode.contains("{")){
            int j=1, k=0, m=0,place=1, tag=0;////////////////////////////////////classAppearTime=0
            String nameForClass;
            List<String> specialCondition = new ArrayList<>();

            for(;j< nonEmptyLines.size();j++){
                String line = nonEmptyLines.get(j);
                if(line.contains("class")){
                    for(;place<line.length();place++){
                        if(line.charAt(place)=='s' && line.charAt(place+1)==' '){
                            for(tag=place+1; tag<line.length(); tag++){
                                //System.out.println(place);////////////////////////////////////////////////
                                if(line.charAt(tag)!=' '){

                                    if(line.contains("{")){
                                        nameForClass=line.substring(tag, line.length()-2);
                                    }else{
                                        nameForClass=line.substring(tag);
                                    }
                                    if(!classNameSet.contains(nameForClass)){
                                        classNameSet.add(nameForClass);
                                    }
                                    break;
                                }

                            }
                        }
                    }
                    //place=1;
                }
                place=1;
            }//find the class names set

            //System.out.println(classNameSet.size());///////////////////////////

            int i=0;
            for(k=0; k<classNameSet.size(); k++){
                for(i=0; i<nonEmptyLines.size(); i++){
                    String line = nonEmptyLines.get(i);
                    if(line.contains("{") && line.contains(classNameSet.get(k))){
                        int innerindex=i;
                        for(; innerindex<nonEmptyLines.size(); innerindex++){
                            line = nonEmptyLines.get(innerindex);
                            specialCondition.add(nonEmptyLines.get(innerindex));
                            if(line.contains("}")){
                                break;
                            }
                        }
                        i=innerindex;
                    }
                    if(line.contains(":") && line.contains(classNameSet.get(k))){
                        specialCondition.add(nonEmptyLines.get(i));
                    }
                    if((line.contains("class ") && !line.contains("{")) && line.contains(classNameSet.get(k))){
                        specialCondition.add(nonEmptyLines.get(i));
                    }

                }//specialCondition is specific class's whole string arraylist





                /////////////////test for arraylist
                /*for (int num = 0; num < specialCondition.size(); num++) {
                    System.out.println(specialCondition.get(num));
                }
                System.out.println("////////////////////////////////////////////");
                */

                /////////////////////////////////////////////////////////////////////////////////////////////////////////////
                i=0;
                for(;i<specialCondition.size();i++){
                    String line = specialCondition.get(i);
                    String nextLine = (i + 1 < specialCondition.size()) ? specialCondition.get(i + 1) : null;
                    if(finalContent==null && (((!line.contains("class") && !line.equals("classDiagram")) && isChallengeData) && nextLine!=null)){
                        for(;!specialCondition.get(i).contains("class");i++){
                            if(specialCondition.get(i).contains("class")){
                                break;
                            }
                        }
                    }
                    if(line.equals("classDiagram")){
                        continue;
                    }
                    if((line.contains("class ") && !line.contains("Diagram")) && challengePointClassTime!=1){
                        linesForClass=findTheClassName(line);
                        finalContent.add(linesForClass);
                        challengePointClassTime=1;
                    }
                    if(!line.contains("(") && (line.contains("+") || line.contains("-"))){
                        linesForVariable=findTheVariable(line);
                        finalContent.add(linesForVariable);
                    }
                    if(line.contains("(")){
                        linesForFunction=findTheFunction(line);
                        finalContent.add(linesForFunction);
                    }
                    if(line.contains("}")){
                        //continue;
                    }
                    if(nextLine==null){
                        finalContent.add("}");
                        try {
                            String output = titleName.concat(".java");
                            String content = String.join("", finalContent);
                            File file = new File(output);
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                                bw.write(content);
                            }
                            //System.out.println("Java class has been generated: " + output);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finalContent.clear();
                        specialCondition.clear();///////
                        challengePointClassTime=0;
                    }



                }

            }

        }








        if(!mermaidCode.contains("{")){

        for (int i = 0; i < nonEmptyLines.size(); i++) {
            String line = nonEmptyLines.get(i);
            String nextLine = (i + 1 < nonEmptyLines.size()) ? nonEmptyLines.get(i + 1) : null;

            String regex ="(?<!\\w)class(?<!\\w)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcherline = pattern.matcher(line);
            Matcher matchernextline = null;
            if(nextLine!=null){
                matchernextline = pattern.matcher(nextLine);
            }

            if(finalContent==null && (((!line.contains("class") && !line.equals("classDiagram")) && isChallengeData) && nextLine!=null)){
                for(;!nonEmptyLines.get(i).contains("class");i++){
                    if(nonEmptyLines.get(i).contains("class")){
                        break;
                    }
                }
            }
            //////////////////////////////////////////////////////////////////////////
            if(line.equals("classDiagram")){
                continue;
            }
            //String linesForClass, linesForVariable, linesForFunction;
            if(line.contains("class ") && !line.contains("Diagram")){
                linesForClass=findTheClassName(line);
                finalContent.add(linesForClass);
            }//creat the class

            if(!line.contains("(") && line.contains(":")){
                linesForVariable=findTheVariable(line);
                finalContent.add(linesForVariable);
            }//find the line that need us to write its variable

            if(line.contains("(")){//////////////////////////
                linesForFunction=findTheFunction(line);
                finalContent.add(linesForFunction);
            }//write the function

            /////////////////////////////////////////////////////////////////////////
            //if(line){} write for the fraction point when meet class at the next line or end of data
            if((nextLine==null || nextLine.contains("class ") )|| nextLine.contains("}")){
                finalContent.add("}");
                try {
                    String output = titleName.concat(".java");
                    String content = String.join("", finalContent);
                    File file = new File(output);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                        bw.write(content);
                    }
                    //System.out.println("Java class has been generated: " + output);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finalContent.clear();

            }


        }

    }
    }


    private static String findTheClassName(String mermaidCode){
        int place, endplace=0;
        //System.out.println(mermaidCode);//////
            String title="";
            String classtitle;
            mermaidCode=mermaidCode.replace(" ", "$");
            int theLengthOfString=mermaidCode.length();
            for(place=1; place<theLengthOfString;place++){
                if(mermaidCode.charAt(place-1)=='s' && mermaidCode.charAt(place)=='$'){

                    for(;place<theLengthOfString;place++){
                        if(mermaidCode.charAt(place)!='$'){
                            break;
                        }
                    }
                    break;
                }
            } //find the place for the type "    class N." that N sit on
            //System.out.println("place is "+ place);//////////////
            if(mermaidCode.contains("{")){
                for(endplace=place; endplace<theLengthOfString; endplace++){
                    //System.out.println("place is "+ endplace);//////////////
                    if(endplace+1<theLengthOfString){
                    if(mermaidCode.charAt(endplace+1)=='$'){//////////////////////////////////////////////////////problem is here
                        break;
                    }}

                }

            }
            if(!mermaidCode.contains("{")){
                title = mermaidCode.substring(place);
            }else{
                title = mermaidCode.substring(place, endplace+1);
            }
            titleName=title;
            classtitle="public class ".concat(title).concat(" {\n");
            return classtitle;
    }

    private static String findTheVariable(String mermaidCode){
        int place;

            String contentThatContainVariable="";
            int theLengthOfString = mermaidCode.length();
            for(place=0; place<theLengthOfString; place++){
                if(mermaidCode.charAt(place)=='+' || mermaidCode.charAt(place)=='-'){
                    break;
                }
            }

            if(mermaidCode.charAt(place)=='+'){
                contentThatContainVariable="    public ".concat(mermaidCode.substring(place+1)).concat(";\n");
            }
            if(mermaidCode.charAt(place)=='-'){
                contentThatContainVariable="    private ".concat(mermaidCode.substring(place+1)).concat(";\n");
            }
            return contentThatContainVariable;
    }

    private static String findTheFunction(String mermaidCode){
        int place, targetend, targetstart, typestart; //target is() type is at the line's end
            String contentOfFunction="", variableInFunction, titleOfFunction, thingsWantToKnow, typeOfFunction; //title is N(variable) things isused to know the string between set/get and (
            int theLengthOfString = mermaidCode.length();
            for(place=0; place<theLengthOfString; place++){
                if(mermaidCode.charAt(place)=='+' || mermaidCode.charAt(place)=='-'){
                    break;
                }
            }
            for(targetend=0; targetend<theLengthOfString; targetend++){
                if(mermaidCode.charAt(targetend)==')'){
                    break;
                }
            }
            for(targetstart=0; targetstart<theLengthOfString; targetstart++){
                if(mermaidCode.charAt(targetstart)=='('){
                    break;
                }
            }
            for(typestart=targetend+1; typestart<theLengthOfString; typestart++){
                if(mermaidCode.charAt(typestart)!=' '){
                    break;
                }
            }
            variableInFunction=mermaidCode.substring(targetstart, targetend);


            //////////////////////////////
            ///if(place+1<targetend)
                titleOfFunction=mermaidCode.substring(place+1, targetend+1);

            //////////////////////////


            if(targetstart>place+4){
                //thingsWantToKnow=mermaidCode.substring(place+4, targetstart).toLowerCase();/////////////////problem is here
                thingsWantToKnow=mermaidCode.substring(place+4, place+5).toLowerCase()+mermaidCode.substring(place+5, targetstart);
            }else{
                thingsWantToKnow="";
            }
            typeOfFunction=mermaidCode.substring(typestart);

            //System.out.println(mermaidCode.substring(place+1, place+4));
            //String checkforword=mermaidCode;

            if(mermaidCode.charAt(place)=='+'){
                if(mermaidCode.substring(place+1, place+4).equals("set")){
                    if(variableInFunction.contains(thingsWantToKnow)){
                        contentOfFunction="    public ".concat(typeOfFunction).concat(" ").concat(titleOfFunction).concat(" {\n        this.").concat(thingsWantToKnow).concat(" = ").concat(thingsWantToKnow).concat(";\n    }\n");
                    }
                }
                if(mermaidCode.substring(place+1, place+4).equals("get")){
                    //System.out.println(thingsWantToKnow);

                        contentOfFunction="    public ".concat(typeOfFunction).concat(" ").concat(titleOfFunction).concat(" {\n        return ").concat(thingsWantToKnow).concat(";\n    }\n");
                        //System.out.println(contentOfFunction);///

                }
                if(!mermaidCode.substring(place+1, place+4).equals("set") && !mermaidCode.substring(place+1, place+4).equals("get")){
                    if(typeOfFunction.equals("int")){
                        contentOfFunction="    public ".concat(typeOfFunction).concat(" ").concat(titleOfFunction).concat(" {return 0;}\n");
                    }else if (typeOfFunction.equals("boolean")) {
                        contentOfFunction="    public ".concat(typeOfFunction).concat(" ").concat(titleOfFunction).concat(" {return false;}\n");
                    }else if (typeOfFunction.equals("String")) {
                        contentOfFunction="    public ".concat(typeOfFunction).concat(" ").concat(titleOfFunction).concat(" {return \"\";}\n");
                    }else{
                        contentOfFunction="    public ".concat(typeOfFunction).concat(" ").concat(titleOfFunction).concat(" {;}\n");
                    }
                    // System.out.println(contentOfFunction);///
                }
            }

            if(mermaidCode.charAt(place)=='-'){
                if(mermaidCode.substring(place+1, place+4).equals("set")){
                    if(variableInFunction.contains(thingsWantToKnow)){
                        contentOfFunction="    private ".concat(typeOfFunction).concat(" ").concat(titleOfFunction).concat(" {\n    this.").concat(thingsWantToKnow).concat(" = ").concat(thingsWantToKnow).concat(";\n    }\n");
                    }
                }
                if(mermaidCode.substring(place+1, place+4).equals("get")){
                    if(variableInFunction.contains(thingsWantToKnow)){
                        contentOfFunction="    private ".concat(typeOfFunction).concat(" ").concat(titleOfFunction).concat(" {\n    return ").concat(thingsWantToKnow).concat(";\n    }\n");
                    }
                }
                if(!mermaidCode.substring(place+1, place+4).equals("set") && !mermaidCode.substring(place+1, place+4).equals("get")){
                    if(typeOfFunction.equals("int")){
                        contentOfFunction="    private ".concat(typeOfFunction).concat(" ").concat(titleOfFunction).concat(" {return 0;}\n");
                    }else if (typeOfFunction.equals("boolean")) {
                        contentOfFunction="    private ".concat(typeOfFunction).concat(" ").concat(titleOfFunction).concat(" {return false;}\n");
                    }else if (typeOfFunction.equals("String")) {
                        contentOfFunction="    private ".concat(typeOfFunction).concat(" ").concat(titleOfFunction).concat(" {return \"\";}\n");
                    }else{
                        contentOfFunction="    private ".concat(typeOfFunction).concat(" ").concat(titleOfFunction).concat(" {;}\n");
                    }
                }
            }
            return contentOfFunction;
    }
    private static void sortTheClass(){

    }
}
class shell1{}
class shell2{}
class shell3{}