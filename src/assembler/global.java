package assembler;

import java.util.ArrayList;
import java.util.HashMap;

public class global {
    final int ID = 0;
    final int FORMAT1 = 1;
    final int FORMAT2 = 2;
    final int FORMAT3 = 3;
    final int FORMAT4 = 4;
    final int START = 5;
    final int NUM = 6;
    final int REGISTER = 7;
    final int WORD = 8;
    final int BYTE = 9;
    final int RESW = 10;
    final int RESB = 11;
    final int PLUS = 12;
    final int AAT = 13;
    final int HASH = 14;
    final int COMMA = 15;
    final int STRING = 16;
    final int HEX = 17;
    final int END = 18;
    final int QUOTE = 19;
    final int BYTEVLA = 20;


    //----------------errors numbers
    final int numFollByChar = 1111;
    final int unExpectedToken = 1112;


    HashMap<Integer,String>  tokensWithStrings = new HashMap<>();
    public static ArrayList<entry> opTable = new ArrayList<>();
    public static ArrayList<entry> SymbolTable = new ArrayList<>();


    global(){
        opTable.add(new entry("lda",FORMAT3,0x00));
        opTable.add(new entry("ldx",FORMAT3,0x04));
        opTable.add(new entry("sta",FORMAT3,0x0c));
        opTable.add(new entry("stx",FORMAT3,0x10));

        opTable.add(new entry("add",FORMAT3,0x18));
        opTable.add(new entry("sub",FORMAT3,0x1c));
        opTable.add(new entry("mul",FORMAT3,0x20));
        opTable.add(new entry("div",FORMAT3,0x24));

        opTable.add(new entry("comp",FORMAT3,0x28));
        opTable.add(new entry("jlt" ,FORMAT3,0x38));
        opTable.add(new entry("jeq" ,FORMAT3,0x30));
        opTable.add(new entry("jgt" ,FORMAT3,0x34));

        opTable.add(new entry("jsub",FORMAT3,0x48));
        opTable.add(new entry("rsub",FORMAT3,0x4c));

        opTable.add(new entry("word",WORD,0x4c));
        opTable.add(new entry("byte",BYTE,0x4c));
        opTable.add(new entry("resw",RESW,0x4c));
        opTable.add(new entry("resb",RESB,0x4c));

        tokensWithStrings.put(ID,"ID");
        tokensWithStrings.put(FORMAT1,"FORMAT1");
        tokensWithStrings.put(FORMAT2,"FORMAT2");
        tokensWithStrings.put(FORMAT3,"FORMAT3");
        tokensWithStrings.put(START,"START");
        tokensWithStrings.put(END,"END");
        tokensWithStrings.put(NUM,"NUM");
        tokensWithStrings.put(WORD,"WORD");
        tokensWithStrings.put(BYTE,"BYTE");
        tokensWithStrings.put(RESW,"RESW");
        tokensWithStrings.put(PLUS,"PLUS '+' ");
        tokensWithStrings.put(AAT,"AAt '@' ");
        tokensWithStrings.put(COMMA,"COMMA ',' ");
        tokensWithStrings.put(HASH,"HASH '#' ");
        tokensWithStrings.put(QUOTE,"QUOTE '\'' ");
        tokensWithStrings.put(BYTEVLA,"BYTEVLA");
        tokensWithStrings.put(numFollByChar,"number followed by character without space in between");

    }


}
