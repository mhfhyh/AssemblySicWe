package assembler;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

class helperClass extends global {

    static LinkedList<String> words;
    static int currWordIndex = 0;
    static int tokenVal = -1;
    static int PC = 0;
    static String label = null;
    int errorNum = -1;
    static int lineCounter = 0;
    static Boolean byteFlag = false;
    static ArrayList<entry> SymbolTable = new ArrayList<>();
    static HashMap<String,Integer> LiteralTable = new HashMap<>();
    StringProperty errorMsg =  new SimpleStringProperty(this,"errorMsg","");
    static HashMap<Integer,Integer> constTable = new HashMap<>();
    void error(String error){

        errorMsg.setValue(errorMsg.getValue()+"\n"+
                (lineCounter != 0?("Error line: "+lineCounter+' '):"")+ error);
    }





    //fill is used to fill empty bits with Zero 'unFilled' is the string that is we want to fill ,'numOfBits' specifying the number of bits we want to fill ,'isRight' if we want to fill right we make it 'true' otherwise fill it left
    String fill(String unFilled, int numOfBits, boolean isRight){

        if (isRight)
            for (int i=unFilled.length();i<=numOfBits;i++)
                unFilled = unFilled+"0";
        else
            for (int i=unFilled.length();i<=numOfBits;i++)
                unFilled = "0"+unFilled;


        return unFilled;
    }

    String toHex(ObservableList<CharSequence> out){

        String mOut = "";
        Iterator<CharSequence> it =out.iterator();

            while (it.hasNext()){
                String s = "";
                String x =(it.next()).toString();
                int sizeX = x.length()/4;
                int remindX = x.length()%4;
                int j =0;

                for (int i =0;i<sizeX;i++,j+=4){
                    String a = x.substring(j,j+4);
                    int lin = Integer.parseInt(a,2);
                    mOut += Integer.toString(lin,16);
                }
                if (remindX > 1) {
                    int lin = Integer.parseInt(x.substring(x.length()-1-remindX,x.length()-1),2);
                    s += Integer.toString(lin, 16);
                }
                mOut +="\n"+s;
            }




        return mOut;
    }

    String toBin(ObservableList<CharSequence> out){
        String mOut = "";
        Iterator<CharSequence> it =out.iterator();


            while (it.hasNext()){
                String s = "";
                String x =(it.next()).toString();
                int sizeX = x.length()/6;
                int remindX = x.length()%6;
                int j =0;

                for (int i =0;i<sizeX;i++,j+=6){
                    String a = x.substring(j,j+6);
                    int lin = Integer.parseInt(a,16);
                    mOut += Integer.toString(lin,2);
                }
                if (remindX > 1) {
                    long lin = Long.parseLong(x.substring(x.length()-1-remindX,x.length()-1),16);
                    s += Long.toString(lin, 2);
                }
                mOut +="\n"+s;
            }

     return mOut;
    }

    String textToAsciiBin(String string){

        String  result= "";
        for (char x : string.toCharArray()){
            result += Integer.toBinaryString((int)x);
        }

        return result;
    }
}
