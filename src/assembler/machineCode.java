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

    public void setFormat(int format) {
        this.format = format;
    }

    public String getAddressLabel() {
        return addressLabel;
    }

    public void setAddressLabel(String addressLabel) {
        this.addressLabel = addressLabel;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getInsCode() {
        return InsCode;
    }

    public void setInsCode(String insCode) {
        this.InsCode = insCode;
    }

    public String getCodeRest() {
        return codeRest;
    }

    public void setCodeRest(String codeRest) {
        this.codeRest = codeRest;
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

