package assembler;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.math.BigInteger;
import java.util.HashMap;

class helperClass extends global {
    static int lineCounter = 0;


    StringProperty errorMsg =  new SimpleStringProperty(this,"errorMsg","");
    static HashMap<Integer,Integer> usedConstTable = new HashMap<>();
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
///
     String convertToHex(ObservableList<CharSequence> out){
        StringBuilder text = new StringBuilder("");
        out.forEach(e->{
            String s =e.toString();

            if (s.length() != 0) {
                BigInteger b = new BigInteger(s,2);
                text.append(b.toString(16)+"\n");
            }
        });
        return text.toString();
    }


     String convertToBin(ObservableList<CharSequence> out){
        StringBuilder text = new StringBuilder("");
        out.forEach(e->{
            String s =e.toString();

            if (s.length() != 0) {
                BigInteger b = new BigInteger( s.toLowerCase(),16);
                text.append(b.toString(2)+"\n");
            }
        });
        return text.toString();
    }


    String textToAsciiBin(String string){

        String  result= "";
        for (char x : string.toCharArray()){
            result += Integer.toBinaryString((int)x);
        }

        return result;
    }
}
