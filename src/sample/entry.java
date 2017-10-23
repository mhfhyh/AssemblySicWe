package sample;

import java.util.ArrayList;

public class entry extends ArrayList{

    private String mnemonic;
    private int token;
    private int opcode;

    public entry(String mnemonic, int token, int opcode) {
        this.mnemonic = mnemonic;
        this.token = token;
        this.opcode = opcode;
    }

    public String Mnemonic() {
        return mnemonic;
    }

    public int Token() {
        return token;
    }

    public int Opcode() {
        return opcode;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    public boolean comp(String op){
      return   this.mnemonic.equalsIgnoreCase(op);
    }

    @Override
    public boolean equals(Object v) {
        return this.mnemonic.equalsIgnoreCase((String)v);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.mnemonic != null ? this.mnemonic.hashCode() : 0);
        return hash;
    }

}
