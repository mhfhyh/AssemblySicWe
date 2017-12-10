package assembler;

import java.util.ArrayList;
import java.util.LinkedList;

public class lexer extends helperClass {

    protected int numOfWord;
    protected int lookahead = -1;
    protected ArrayList<machineCode> intermediate = new ArrayList<>();
    entry symbolFound;



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
            if (word.equalsIgnoreCase("base")) return BASE;
            if (word.equalsIgnoreCase("org")) return ORG;
            if (word.equalsIgnoreCase("equ")) return EQU;
                                 entry test = new entry(word,0,0);//create an object of entry to compare its mnemonic -> it should be done like that to (overdid method  'equal' work)
            if (opTable.contains(test)){
                label = opTable.get(opTable.indexOf(test)).getOpcode();
                return opTable.get(opTable.indexOf(test)).getToken();
            }
            if (SymbolTable.contains(test)){//case of already defined ID
                tokenVal = -3;//it is mean the ID found is not new ID
                label = word;
                symbolFound = SymbolTable.get(SymbolTable.indexOf(test));
                return symbolFound.getToken();
            }
            else {
                tokenVal = -4;//it is mean the ID found is not new ID
                label = word;
                return ID;}
        }
        //final case 'word' not a digit and neither a letter
        if (word.charAt(0) == '@') return AAT;
        if (word.charAt(0) == '+') return PLUS;
        if (word.charAt(0) == ',') return COMMA;
        if (word.charAt(0) == '#') return HASH;
        if (word.charAt(0) == '=') return EQUAL;
        if (word.charAt(0) == '*') return STAR;
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


    //the aim of this function is to convert given text code to ArrayList of string it is ignoring the comment line and the (free lines)
    LinkedList<String> splitIgnoreSpaces(String text){

        if(text.length() == 0 || text.charAt(0) == '/') return null; //this is in case of the line is a comment line OR (text.length() == 0) to skip the line spaces

        LinkedList<String> Words = new LinkedList<>();
        // int index = 0;
        String word="";
        boolean commentFlag = false;//this indicate finding comment to ignore the rest -> 'false' indicate not finding comment tell now
        boolean unAcChar = false;//this indicate finding un accepted character

        for(int i=0;i<text.length();i++){// iterate throw all the line

            while (i<text.length() && text.charAt(i)!= ' ' ){// this for finding the complete word to add it

                char ch = text.charAt(i);
                if (ch == '/'){commentFlag = true; break;}//case of comment to ignore all the text after it
                if (ch != '*' && ch != '@' && ch != '#' && ch != '+'&& ch != ',' && ch != '\'' && ch != '=' && !Character.isDigit(ch) && !isEnglish(ch)){unAcChar = true; error("un accepted character ' "+ch+" '");break;}//error un accepted char

                if (ch == '\''){//case of  ' quote is used with byte value it should be ether 'c' or 'h' coming before it, else mark it as error
                    if (word.length()>0) {
                        if (word.equalsIgnoreCase("c") || word.equalsIgnoreCase("h")) {
                            Words.add(word);
                            word = "";
                        } else error("un accepted prefix ' " + word + " ' before quote , accepted is ether 'c' OR 'h'");
                    }
                    Words.add("\'");
                    i++;
                    String quotedText="";
                    while (text.charAt(i)!='\''){
                        quotedText += String.valueOf(text.charAt(i++));
                        if (i == text.length()){error("ending quote \' not found"); break;}
                    }
                    Words.add(quotedText);
                    if (text.charAt(i)=='\''){ i++; Words.add("\'");}
                    continue; //while we done we have to skip this iteration to next one
                }
                if(ch == '@' || ch == '#'|| ch == '+' || ch == ',' || ch == '='){//this is in case if finding ',' comma before or after word. Finding text followed by ('#' or '@') without space in between
                    if (word.length()>0){
                        if (ch == ',' ){Words.add(word);word="";}
                        else error("text is followed by "+ch+" without space in between");
                    }
                    Words.add(String.valueOf(ch));
                    break;
                }//end of case comma


                word+=text.charAt(i);
                i++;
            }

            if (word.length()>0){
                Words.add(word);
                word="";
            }

            if (commentFlag)break;//complement of case of comment
            if (unAcChar)return null;

        }
        return Words;
    }//end of splitIgnoreSpaces
    private boolean isEnglish(char ch){
        if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))return true;
        return false;
    }


}
