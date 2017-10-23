package sample;

import java.util.LinkedList;

public class lexer extends global {

    protected static LinkedList<String> words;
    protected static boolean newID = false;
    protected static int currWordIndex;
    protected static int tokenVal;
    protected static int PC;
    protected static String label = null;
    protected int errorNum = -1;

    private Boolean reswFlag = false;
    private Boolean resbFlag = false;
    private Boolean byteFlag = false;
    private Boolean idFlag   = false;


    protected int lexical(){

        String word = words.get(currWordIndex++);

        if (Character.isDigit(word.charAt(0))){
            if (isNumeric(word)){tokenVal = Integer.valueOf(word); return NUM;}
            else return errorNum = numFollByChar;
        }
        else if (Character.isLetter(word.charAt(0))){
            if (word.length()== 1)return REGISTER;
            if (word.equalsIgnoreCase("start")) return START;
            if (word.equalsIgnoreCase("end")) return END;
            if (word.equalsIgnoreCase("byte")) {byteFlag = true; return BYTE;}
            if (opTable.contains(word) ) return opTable.get(opTable.indexOf(word)).Token();
            else {label = word; return ID;}

        }
        if (word.charAt(0) == '@') return AAT;
        if (word.charAt(0) == '+') return PLUS;
        if (word.charAt(0) == ',') return COMMA;
        if (word.charAt(0) == '#') return HASH;


        return unExpectedToken;
    }
    private boolean isNumeric(String str){

        for (char c : str.toCharArray())
        {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}
