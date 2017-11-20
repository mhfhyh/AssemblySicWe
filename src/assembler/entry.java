package assembler;

import java.util.ArrayList;

public class entry extends ArrayList{

    private String mnemonic;
    private int token;
    private int address;
    private String opcode;

    public String getOpcode() {
        return opcode;
    }

    public entry(String mnemonic, int token, int address) {
        this.mnemonic = mnemonic;
        this.token = token;
        this.address = address;
    }

    public entry(String mnemonic, int token, String opcode) {
        this.mnemonic = mnemonic;
        this.token = token;
        this.opcode = opcode;
    }



    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public boolean comp(String op){
      return   this.mnemonic.equalsIgnoreCase(op);
    }

    @Override
    public boolean equals(Object v) {
        entry f =  (entry)v;
        return this.mnemonic.equalsIgnoreCase(f.mnemonic);
    }

    @Override
    public int hashCode() {
        return mnemonic.hashCode();
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public int getToken() {
        return token;
    }

    public int getAddress() {
        return address;
    }
}
