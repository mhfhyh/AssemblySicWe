package assembler;

import java.util.ArrayList;

public class entry extends ArrayList{

    private String mnemonic_labelName;
    private int token;
    private int address;
    private String opcode;

    public String getOpcode() {
        return opcode;
    }

    public entry(String mnemonic_labelName, int token, int address) {
        this.mnemonic_labelName = mnemonic_labelName;
        this.token = token;
        this.address = address;
    }

    public entry(String mnemonic_labelName, int token, String opcode) {
        this.mnemonic_labelName = mnemonic_labelName;
        this.token = token;
        this.opcode = opcode;
    }



    public void setMnemonic_labelName(String mnemonic_labelName) {
        this.mnemonic_labelName = mnemonic_labelName;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public boolean comp(String op){
      return   this.mnemonic_labelName.equalsIgnoreCase(op);
    }

    @Override
    public boolean equals(Object v) {
        entry f =  (entry)v;
        return this.mnemonic_labelName.equalsIgnoreCase(f.mnemonic_labelName);
    }

    @Override
    public int hashCode() {
        return mnemonic_labelName.hashCode();
    }

    public String getMnemonic_labelName() {
        return mnemonic_labelName;
    }

    public int getToken() {
        return token;
    }

    public int getAddress() {
        return address;
    }
}
