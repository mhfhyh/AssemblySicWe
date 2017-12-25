package assembler;

class LiteralTable {

    private String value;
    private int address;

    LiteralTable(String value, int address) {

        this.value = value;
        this.address = address;
    }


    String getValue() {
        return value;
    }

    int getAddress() {
        return address;
    }

    void setAddress(int address) {
        this.address = address;
    }
}
