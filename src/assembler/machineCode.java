package assembler;

import java.util.ArrayList;

public class machineCode extends ArrayList{

    private String addressLabel;
    private int line;
    private int format;//1 -> format one , 2 -> format 2 , 3 -> format 3 , 4 -> format 3 with indexing ,5 -> format 4 , 6 -> format 4 with indexing , 7 -> format 3 with intermediate , 8 -> format 3 with intermediate and indexing, 9 -> format 4 with intermediate ,10 -> format 4 with intermediate and indexing , 11 -> format 3 with indirect ,12 -> format 3 with indirect and indexing , 13 -> format 4 with indirect , 14 -> format 4 with indirect and indexing
    private String InsCode;
    private String codeRest;
    private int pc;
    private int base;


    public machineCode(int line ,int pc,int base,int format ,String InsCode,String addressLabel, String codeRest) {
        this.line = line;
        this.pc = pc;
        this.base = base;
        this.format = format;
        this.addressLabel = addressLabel;
        this.InsCode = InsCode;
        this.codeRest = codeRest;

    }

    public int getFormat() {
        return format;
    }


    public String getAddressLabel() {
        return addressLabel;
    }

    public String getAddressLabel(int num,Boolean isRight) {
        return fill(addressLabel,num,isRight);
    }



    public int getLine() {
        return line;
    }



    public String getInsCode() {

        return InsCode;
    }

  /*  public String getInsCode(int num,Boolean isRight) {

        return fill(InsCode,num,isRight);
    }*/



    public String getCodeRest() {
        return codeRest;
    }

    public String getCodeRest(int num,Boolean isRight) {

        return fill(codeRest, num, isRight);
    }



    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    private String fill(String string,int numOfBits,boolean isRight){

        if (isRight)
            for (int i=string.length();i<=numOfBits;i++)
                string = string+"0";
        else
            for (int i=string.length();i<=numOfBits;i++)
                string = "0"+string;


        return string;
    }

    /*@Override
    public boolean equals(Object v) {
        return this.addressLabel.equalsIgnoreCase((String)v);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.addressLabel != null ? this.addressLabel.hashCode() : 0);
        return hash;
    }*/

}

