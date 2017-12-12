package assembler;

public class LiteralTable {

    private String value;
    private int address;

    public LiteralTable( String value, int address) {

        this.value = value;
        this.address = address;
    }


    public String getValue() {
        return value;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }
}
