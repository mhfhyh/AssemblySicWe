package sample;

import java.util.ArrayList;

public class machineCode extends ArrayList{

    private String addressLabel;
    private int line;
    private String mCode;
    private String codeRest;
    private boolean absoluteFlag;

    public machineCode(String addressLabel, int line, String mCode, String codeRest, boolean absoluteFlag) {
        this.addressLabel = addressLabel;
        this.line = line;
        this.mCode = mCode;
        this.codeRest = codeRest;
        this.absoluteFlag = absoluteFlag;
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

    public String getmCode() {
        return mCode;
    }

    public void setmCode(String mCode) {
        this.mCode = mCode;
    }

    public String getCodeRest() {
        return codeRest;
    }

    public void setCodeRest(String codeRest) {
        this.codeRest = codeRest;
    }

    public boolean isAbsoluteFlag() {
        return absoluteFlag;
    }

    public void setAbsoluteFlag(boolean absoluteFlag) {
        this.absoluteFlag = absoluteFlag;
    }

    @Override
    public boolean equals(Object v) {
        return this.addressLabel.equalsIgnoreCase((String)v);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.addressLabel != null ? this.addressLabel.hashCode() : 0);
        return hash;
    }

}

