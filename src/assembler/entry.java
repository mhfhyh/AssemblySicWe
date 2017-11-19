package assembler;

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

    public int getOpcode() {
        return opcode;
    }
}
