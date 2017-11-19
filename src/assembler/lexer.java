package assembler;

import java.util.LinkedList;

public class lexer extends global {

    protected static LinkedList<String> words;
        protected static int currWordIndex = 0;
    protected static int tokenVal = -1;
    protected static int PC = 0;
    protected static String label = null;
    protected int errorNum = -1;
    protected static int lineCounter = 0;
    protected static Boolean byteFlag = false;




    protected int lexical(){

        String word = words.get(currWordIndex++);

        //first case word began with digit
        if (Character.isDigit(word.charAt(0))){
            if (byteFlag){label = word; return BYTEVLA; }
            if (isNumeric(word)){tokenVal = Integer.valueOf(word); return NUM;}
            else return errorNum = numFollByChar;
        }

        //second case word began with s letter
        else if (Character.isLetter(word.charAt(0))){
            if (byteFlag){ label = word; return BYTEVLA; }

            if (word.length()== 1){
                if (word.equalsIgnoreCase("A")){label = "0";return REGISTER;}
                if (word.equalsIgnoreCase("X")){label = "1";return REGISTER;}
                if (word.equalsIgnoreCase("L")){label = "2";return REGISTER;}
                if (word.equalsIgnoreCase("B")){label = "3";return REGISTER;}
                if (word.equalsIgnoreCase("S")){label = "4";return REGISTER;}
                if (word.equalsIgnoreCase("T")){label = "5";return REGISTER;}
                if (word.equalsIgnoreCase("F")){label = "6";return REGISTER;}
                if (word.equalsIgnoreCase("H"))return HEX;
                if (word.equalsIgnoreCase("C"))return STRING;
            }

            if (word.equalsIgnoreCase("start")) return START;
            if (word.equalsIgnoreCase("end")) return END;
            if (word.equalsIgnoreCase("word")) return WORD;
            if (word.equalsIgnoreCase("byte")) return BYTE;
            if (word.equalsIgnoreCase("resw")) return RESW;
            if (word.equalsIgnoreCase("resb")) return RESB;
                     entry test = new entry(word,0,0);//create an object of entry to compare its mnemonic -> it should be done like that to (overdid method  'equal' work)
            if (opTable.contains(test)) {
                tokenVal = opTable.get(opTable.indexOf(test)).getOpcode();
                return opTable.get(opTable.indexOf(test)).getToken();}
            if (SymbolTable.contains(test)) return SymbolTable.get(SymbolTable.indexOf(test)).getToken();
            else {label = word; return ID;}

        }
        //final case 'word' not a digit and neither a    letter
        if (word.charAt(0) == '@') return AAT;
        if (word.charAt(0) == '+') return PLUS;
        if (word.charAt(0) == ',') return COMMA;
        if (word.charAt(0) == '#') return HASH;
        if (word.charAt(0) == '\''){
            byteFlag = !byteFlag;
            return QUOTE;}


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
