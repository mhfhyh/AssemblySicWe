package assembler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;


public class parser extends semantic{
    @FXML
    TextArea inputScreen;

    @FXML
    protected TextArea errorScreen;



    @FXML TableView<entry> SymbolTableView;
    @FXML TableColumn<entry,String> LabelColumn;
    @FXML TableColumn<entry,Integer> TypeColumn ;
    @FXML TableColumn<entry,Integer> AddressColumn ;
    @FXML TextArea machineCodeScreen ;
    @FXML Button toHexButton;
    @FXML TableView<machineCode> intermediateTable;
    @FXML TableColumn<machineCode,Integer> lineCol;
    @FXML TableColumn<machineCode,Integer> pcCol;
    @FXML TableColumn<machineCode,Integer> baseCol;
    @FXML TableColumn<machineCode,Integer> formatCol;
    @FXML TableColumn<machineCode,String> insCol;
    @FXML TableColumn<machineCode,String> addressCol;
    @FXML TableColumn<machineCode,String> restCol;


    private int lookahead = -1;
    private int numOfWord;
    private ArrayList<machineCode> intermediate = new ArrayList<>();
    private int format = -1 ;
    private static String addressLabel = null;
    private String insCode = null;
    private String codeRest = null;
    private String constLabel = null;
    private int Base = 0;
    private int lineBase = 0;
    private int linePc = 0;
    private int progEndAddress;
    private int ExecuteLabel=-1;
    private int literalAddressCounter;
    private static HashMap<Integer,LiteralTable> LiteralTable = new HashMap<>();
    private boolean OrgFlag =false;
    private int OrgPC = -1;
    private String modification= null;
    private boolean LTORGFlag =false;


    private ObservableList<CharSequence> code ;

    private void initialize(){
        lookBack = -1;
        symbolFound = null;
        Base =0;
        lineBase =0;
        linePc =0;
        tokenVal = -1;
        PC = 0;
        label = null;
        errorNum = -1;
        lineCounter = 0;
        errorScreen.setText("");
        SymbolTable = new ArrayList<>();
        intermediate = new ArrayList<>();
        LiteralTable = new HashMap<>();
        progEndAddress = -1;
        words = new LinkedList<>();
        currWordIndex = 0;
        tokenVal = -1;
        PC = 0;
        label = null;
        errorNum = -1;
        byteFlag = false;
        SymbolTable = new ArrayList<>();
        constLabel = null;
        OrgFlag =false;
        OrgPC = -1;
        ExecuteLabel= -1;
        modification= null;
        LTORGFlag =false;
    }
    public void okOnAction(){
        errorMsg.bindBidirectional(errorScreen.textProperty());
        //binding the textProperty of 'errorScreen TextArea' with 'errorMsg StringProperty' the result is each time we made a change on 'errorMsg' it do the same change on 'textProperty of errorScreen'

        initialize();
        //initialize the variables with zero values to began new assembling . At each time user press ok program began from scratch

        code = inputScreen.getParagraphs();//get the code from inputScreen text area
        nextSentence();// fetch the first instruction
            sic();

        //for symbol screen table
        ObservableList<entry> list=FXCollections.observableArrayList(SymbolTable);
        LabelColumn.setCellValueFactory(new PropertyValueFactory<>("mnemonic_labelName"));
        TypeColumn.setCellValueFactory(new PropertyValueFactory<>("token"));
        AddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        SymbolTableView.setItems(list);

        //--- for intermediate table in GUI
        ObservableList<machineCode> list1 = FXCollections.observableArrayList(intermediate);
        lineCol.setCellValueFactory(new PropertyValueFactory<>("line"));
        pcCol.setCellValueFactory(new PropertyValueFactory<>("pc"));
        baseCol.setCellValueFactory(new PropertyValueFactory<>("base"));
        formatCol.setCellValueFactory(new PropertyValueFactory<>("format"));
        insCol.setCellValueFactory(new PropertyValueFactory<>("InsCode"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("addressLabel"));
        restCol.setCellValueFactory(new PropertyValueFactory<>("codeRest"));

        intermediateTable.setItems(list1);


        writeToObjectFile();//write to object file
    }


//starting of the grammars
    private void sic(){

        header();
        body();
        tail();

        pass2();



    }



    private void header(){
        String currLabel = label;// getting the 'ID' string value from the lexical analyzer
        match(ID);
        match(START); // match the word start
        PC = tokenVal;// getting the starting address integer value from the lexical analyzer
        match(NUM);   // match the starting address
        SymbolTable.add(new entry(currLabel,ID,PC));//adding the starting program label with its address to symbol table

    }

    private void body(){

        if (lookahead == ID){
            String currLabel = label;// getting the 'ID' string value from the lexical analyzer
            constLabel = label;
            match(ID);
                SymbolTable.add(new entry(currLabel,ID,PC));
            rest();
            body();
        }
        else if(lookahead == FORMAT1 || lookahead == FORMAT2 || lookahead == FORMAT3 ||lookahead == PLUS ) {
            stmt();
            body();
        }
        else if(lookahead == BASE || lookahead == ORG || lookahead == STAR|| lookahead == LTORG){
            directive();
            body();
        }
    }
    private void tail(){
        match(END);
        match(ID);

        progEndAddress = PC;

        if (tokenVal == -3)
            ExecuteLabel = symbolFound.getAddress();

        else error("Undefined Label after END "+label);

    }
    private void directive(){

        if (lookahead == BASE){
            match(BASE);

            if (lookahead == NUM) match(NUM);

            else if(lookahead == ID) match(ID);

            else
                error("un unexpected token. expected ID or NUM  found:"+tokensWithStrings.get(lookahead));
        }
       else if (lookahead == ORG)
           org();
       else if (lookahead == STAR){
            format = -2;
            match(STAR);
            match(EQUAL);
            byteValue();
            linePc = PC;
            PC += addressLabel.length();
            //insCode = "--";
            match(QUOTE);

        }
        else if(lookahead == LTORG){
           match(LTORG);
           LTORGFlag = true;
           writeLiteral();
        }


    }



    private void org(){
        match(ORG);


        if (lookBack != -1){//-1 is an indicator for newline
        if (lookahead == NUM){
                        optimizeORG(tokenVal);
            match(NUM);
        }
        else if (lookahead == CONST){
            optimizeORG(symbolFound.getAddress());
            match(CONST);
        }

        else if(lookahead == ID){
            if (tokenVal == -3)//tokenVal == -3 indicate Backward defined label
                optimizeORG(symbolFound.getAddress());

            else
                error("Not a Backward defined label -> \'"+label +"\' forward is not allowed for newConstant");

          match(ID);
        }
        else error("syntax error: un unexpected token After "+tokensWithStrings.get(lookBack)+". Expected:  ID or CONST or ID found: "+tokensWithStrings.get(lookahead));
        }
        else {//'ORG' not followed by any thing it mean return to original PC (we call it returned 'ORG') ,that mean it should be Second org otherwise mark it as error
            if (OrgPC == -1)error("missed token. After 'ORG' expected ID or NUM or CONST -> ORG cannot followed by empty, unless it is returned \'ORG\' ,In this error it is not  ");
            else PC = OrgPC; //return the original PC value that is mean it is " returned 'ORG' "
        }


    }
    private void optimizeORG(int address){//this is not part of CFG
        if (OrgPC != -1)//finding second 'org'  , OrgPC containing the PC value at the time org is found otherwise it  = -1
            error("nested org not allowed");

        else { //finding first 'org'
            OrgPC =PC;
            PC = address;
            //OrgFlag = false;
        }
    }

    private void rest(){
        if(lookahead == FORMAT1 || lookahead == FORMAT2 || lookahead == FORMAT3 ||lookahead == PLUS)
            stmt();
        else if (lookahead == WORD || lookahead == BYTE || lookahead == RESW ||lookahead == RESB)
            data();
        else if(lookahead == EQU)
            newConstant();
        else
            errorWithNext("Missed token. ID should followed by (Instruction OR data directive OR constant directive .But found "+tokensWithStrings.get(lookahead)+" "+constLabel);//error
    }
    private void newConstant(){
                        SymbolTable.remove(SymbolTable.size()-1);//since program add Id each time it find one (if it is not address label) we delete that label because we know no it is a label for newConstant
        match(EQU);
        if (lookahead == NUM){
                        SymbolTable.add(new entry(constLabel,CONST,tokenVal)); //label is EQU ID 'EQU' is the token tokenVal is the value of the newConstant
            match(NUM);
        }
        else if (lookahead == ID){
            if (tokenVal == -3){ //-3 indicate that it is already defined label , see the lexical()
                                 //usedConstTable.put(lineCounter,symbolFound.getAddress());// in case of 'ID' we will use its address as value , in case of newConstant 'EQU' we will use its value as value for this newConstant
                        SymbolTable.add(new entry(constLabel,CONST,symbolFound.getAddress())); //label is EQU ID 'EQU' is the token tokenVal is the value of the newConstant
                match(ID);
            }
            else error("Not a Backward defined label -> \'"+label +"\' forward is not allowed for Constant");
        }
        else if (lookahead == CONST){
            SymbolTable.add(new entry(constLabel,CONST,symbolFound.getAddress())); //label is EQU ID 'EQU' is the token tokenVal is the value of the newConstant
        }
    }

    private void stmt(){
        linePc =PC;
        lineBase = Base;
        switch (lookahead){
            case FORMAT1:
                             format = 1;//instruction line format
                             insCode = label;// getting the instruction binary code as string value from the lexical analyzer
                match(FORMAT1);
                PC += 1;
                break;

            case FORMAT2:
                             format = 2;//instruction line format
                             insCode = label;// getting the instruction binary code as string value from the lexical analyzer
                             PC += 2;
                match(FORMAT2);
                            addressLabel = label;// getting the first operand string value from the lexical analyzer
                match(REGISTER);
                y();
                break;

            case FORMAT3:
                            format = 3;//instruction line format
                            insCode = label;// getting the instruction binary code as string value from the lexical analyzer
                match(FORMAT3);
                PC += 3;
                z();
                break;

            case PLUS:
                match(PLUS);
                PC += 4;
                            format = 7;//instruction line format
                            insCode = label;// getting the instruction binary code as string value from the lexical analyzer
                match(FORMAT3);
                z();
                break;
            /*default: error("un unexpected token. found: "+tokensWithStrings.get(lookahead));//error*/
        }

    }
    private void y(){
        if (lookahead == COMMA){
            match(COMMA);
                    codeRest = label;// getting the second operand as string value from the lexical analyzer
            match(REGISTER);
        }
        //if lookahead != COMMA it's mean Format 2 with one operand
    }
    private void z(){//called from stmt->format 3 and format 4

        if (lookahead == ID){
                    addressLabel = label;// getting the first operand string value from the lexical analyzer
            match(ID);
            index();
        }
        else if (lookahead == HASH){//intermediate mode
            match(HASH);
                    format +=2;//instruction line format, if format old value is 3 then format new value is 7 which is indicate (format 3 with intermediate) , otherwise (5) -> 9 which is indicate (format 4 with intermediate)
            imm();// imm for immediate
           // index();
        }
        else if(lookahead == AAT){//indirect mode
            match(AAT);
                    format +=3;//instruction line format, if format old value is 3 then format new value is 1 which is indicate (format 3 with indirect) , otherwise (5) -> 13 which is indicate (format 4 with indirect)
            indirect();
           // index();
            ///Solving som Errors
        }
        else if (lookahead == EQUAL) {//literal mod
            literal();
            index();
        }

        else if (lookahead == CONST){//Constant address label
            constAddressLabel();
            index();
        }
        else errorWithNext("syntax error: un unexpected token After \'"+tokensWithStrings.get(lookBack)+"\' . Expected:  ID or HASH or ATT found: "+tokensWithStrings.get(lookahead));
    }

    private void indirect(){
      if (lookahead == ID)
            match(ID);
      else if (lookahead == CONST)
            constAddressLabel();

      else errorWithNext("un unexpected token. expected ID or CONST found: "+tokensWithStrings.get(lookahead));//error
    }

    private void constAddressLabel(){
            addressLabel = Integer.toBinaryString(symbolFound.getAddress());
            match(CONST);
    }

    private void index(){
        if (lookahead == COMMA){
            match(COMMA);
                        format++;
            match(REGISTER);
        }


    }
    private void literal(){
        match(EQUAL);

        if (lookahead == STRING || lookahead == HEX){
        byteValue();
        LiteralTable.put(lineCounter,new LiteralTable(addressLabel,-1));
        addressLabel = "--";//we use this notation '--' to indicate that it is literal ,So we can replace it in passe 2
        match(QUOTE);
        }

        else if (lookahead == STAR) {
            addressLabel = Integer.toBinaryString(PC);//PC here it is actually act as immediate number value
            format = -1;
            match(STAR);
        }

    }

    private void imm(){

        if (lookahead == NUM) {
            addressLabel = Integer.toBinaryString(tokenVal);
            match(NUM);
        }
        else if (lookahead == ID){
            addressLabel = label;
            match(ID);
        }
        else if (lookahead == CONST){
            constAddressLabel();
        }
        else errorWithNext("un unexpected token. expected NUM or ID found: "+tokensWithStrings.get(lookahead));//error

    }

    private void data(){
        linePc =PC;
        lineBase = Base;

        switch (lookahead){
            case WORD:
                format = 0;
                match(WORD);
                            addressLabel = fill(Integer.toBinaryString(tokenVal),23,false);// getting the word value and save it as hex ,
                            //insCode = "--";

                match(NUM);
                PC += 3;
                break;

            case BYTE:
                            format =0;
                 match(BYTE);
                 byteValue();
                            PC += addressLabel.length();
                            //insCode = "--";
                 match(QUOTE);
                break;

            case RESW:
                match(RESW);
                match(NUM);
                PC += 3*tokenVal;
                break;

            case RESB:
                match(RESB);
                match(NUM);
                PC += tokenVal;
                break;
           /* default: error("un unexpected token. found: "+tokensWithStrings.get(lookahead));//error*/
        }
    }

    private void byteValue(){
        /*boolean literalFlag = false;
        if (lookBack == EQUAL) literalFlag =true;*/
        if (lookahead == STRING){
            match(STRING);
            match(QUOTE);
            addressLabel = textToAsciiBin(label);// getting the char byte value
            match(BYTEVLA);

        }

        else if (lookahead == HEX) {
            match(HEX);
            match(QUOTE);

            if (label.length() == 0)
            error("empty byte Value");
            else  addressLabel = new BigInteger(label,16).toString(2);// getting the hex byte value then convert it to Binary
            match(BYTEVLA);

        }
        //else  error("un unexpected token. found: "+tokensWithStrings.get(lookahead)); //error

    }
//-------------------end of grammars-------------------

    private void match(int tok){

        //if (lookahead != tok) mark it as error and look for the next
        if (lookahead != tok){ error("syntax error: un unexpected token After \'"+tokensWithStrings.get(lookBack)+"\' . Expected: "+tokensWithStrings.get(tok)+" found: "+tokensWithStrings.get(lookahead));}

        if(currWordIndex == numOfWord)
            nextSentence();
        else{
             lookBack = lookahead;
             lookahead = lexical();
        }
    }

    private void pass2() {
        if (!LTORGFlag)writeLiteral();// if LTORGFlag = true , it is indicate that 'LTORG' is encountered

        String output = "";

        for (machineCode line : intermediate) {

            switch (line.getFormat()) {
                case -2:
                case -1:
                case  0:
                    output += line.getAddressLabel() + "\n";
                    break;
                case 1:
                    output += line.getInsCode() + "\n";
                    break;
                case 2:
                    output += line.getInsCode() + line.getAddressLabel(4, false) + (line.getCodeRest() == null ? "" : line.getCodeRest(4, false)) + "\n";
                    break;
                case 5:
                    output += line.getInsCode().substring(0, 5);
                    if(Character.isDigit(line.getAddressLabel().charAt(0)))
                        output+=line.getAddressLabel(12,false)+"\n";
                    else output += F3OptimizeAddress(line.getLine(), line.getAddressLabel(), line.getPc(), line.getBase(), line.getFormat()) + "\n";

                    break;
                case 3:
                case 4:
                case 6:
                    output += line.getInsCode().substring(0, 5) + F3OptimizeAddress(line.getLine(), line.getAddressLabel(), line.getPc(), line.getBase(), line.getFormat()) + "\n";
                    break;//3 -> format 3,4 -> format 3 with indexing,5 -> format 3 with intermediate,6 -> format 3 with indirect.


                case 7:
                    output += line.getInsCode().substring(0, 5) + "110001" + F4OptimizeAddress(line.getLine(),line.getPc(),line.getAddressLabel()) + "\n";
                    break;//7 -> format 4,
                case 8:
                    output += line.getInsCode().substring(0, 5) + "111001" + F4OptimizeAddress(line.getLine(),line.getPc(),line.getAddressLabel()) + "\n";
                    break;//8 -> format 4 with indexing,
                case 9:
                    output += line.getInsCode().substring(0, 5) + "010001" + F4OptimizeAddress(line.getLine(),line.getPc(),line.getAddressLabel()) + "\n";
                    break;//9 -> format 4 with intermediate,
                case 10:
                    output += line.getInsCode().substring(0, 5) + "100001" + F4OptimizeAddress(line.getLine(),line.getPc(), line.getAddressLabel()) + "\n";
                    break;//10 -> format 4 with indirect.

            }

        }
        machineCodeScreen.setText(output);
        toHexButton.setDisable(false);
        toHexButton.setText("Hex");
    }


    //this function is specific to 7 -> format 4,
    //8 -> format 4 with indexing,
    //9 -> format 4 with intermediate,
    //10 -> format 4 with indirect
    private String F4OptimizeAddress(int line, int pc, String addressLabel){
        int address =-1;

        if (addressLabel.equalsIgnoreCase("--")){//replace the address with the address of literal data
            address = LiteralTable.get(line).getAddress();
        }
        else address = SymbolTable.indexOf(new entry(addressLabel,0,0));//in case not literal, at the beginning we check if the label is defined before

        if (address != -1) {
            address = SymbolTable.get(address).getAddress();
            writeModificationRecord(pc,address);
            return fill(Integer.toBinaryString(address), 20, false);
        }
        error("Line: "+line+" undefined Label "+addressLabel);
        return null;
    }

    //this function is specific to
    // 3 -> format 3, -> could be relative
    //4 -> format 3 with indexing, -> could be relative
    //5 -> format 3 with intermediate, -> could be relative
    //6 -> format 3 with indirect, -> could be relative
    /*this function 'F3OptimizeAddress()' has three tasks first determine wither given label is already defined in the SymbolTable or not.
     If it is not, mark it as error and return null.If it already defined find the address of that label.
     Task 2 is checking wither given address fit in 12 bits if it is yes return that address as binary as string.
     If it is not, make the address relative to PC or Base and return that address address as binary as string*/
    private String F3OptimizeAddress(int line, String addressLabel, int pc , int base, int format){
        if (addressLabel == null) error("addressLabel == null");
        else {
        int address=-1;
        if (addressLabel.equalsIgnoreCase("--")){//replace the address with the address of literal data
            address = LiteralTable.get(line).getAddress();
        }
        else if (SymbolTable.contains(new entry(addressLabel, 0, 0))){ // in case of not literal ,at the beginning we check if the label is defined before
            int index=SymbolTable.indexOf(new entry(addressLabel, 0, 0));
            entry lab = SymbolTable.get(index);
             address= lab.getAddress();
        }

        if (address != -1){//in case of address == -1 that is mean it is undefined label
            final int upperBound = 4095 ;

            if (address > upperBound) address = address - pc; //relative address to pc
            else {writeModificationRecord(pc,address);}//modification record; if it is not relative

            if (address > upperBound) {//relative address to pc not work (not fill), mark it as error
                error(address+" not fit");
                return null;}

            if (format == 3 )
            return "110010"+fill(Integer.toBinaryString(address),12,false);
            if (format == 4 )
            return "111010"+fill(Integer.toBinaryString(address),12,false);
            if (format == 5 )
            return "010010"+fill(Integer.toBinaryString(address),12,false);
            if (format == 6 )
            return "100010"+fill(Integer.toBinaryString(address),12,false);
        }

        error("Line: "+line+" undefined Label "+addressLabel);
        return null;
    }
        return null;
    }

    private void nextSentence(){
        writeInte();// writing the intermediate list current line

        lineCounter++;//each time this method is called it is indicate ending of current line and jumping to next line

        while (lineCounter <= code.size()){

            words = splitIgnoreSpaces(code.get(lineCounter-1).toString());//code.get(lineCounter-1).toString() -> 'code.get()' is return the text line in type of 'CharSequence' that's why we use '.toString()'
            if (words != null && words.size() != 0){//in case of null (or words.size()==0)  it is mean the line ether line comment or blank line , will skip until find a statement line , or reach the end of program.

                // since this 'splitIgnoreSpaces' function return NOT null object , we will reinitialize the variables to  do parsing in the new line
                currWordIndex = 0;
                numOfWord = words.size();
                lookBack = -1;// to use it as indicator of new line -> we use it i org()
                lookahead = lexical();// we do lexical analyzing to the first word in the new line
                break;
            }

            lineCounter++;
        }
        if (lineCounter > code.size()){lineCounter = 0;}//that's mean it reach the end of the program
    }

    //Mark error with given message 'errorMessage' and find next token
    private void errorWithNext(String errorMessage){
        error(errorMessage); //syntax error
        if(currWordIndex == numOfWord)
            nextSentence();
        else{
            lookBack =lookahead;
            lookahead = lexical();
        }
    }


//----------------------------------------------
   public void toHexOnAction(){

        ObservableList<CharSequence> out = machineCodeScreen.getParagraphs();
        String mOut;

        if(toHexButton.getText().equalsIgnoreCase("hex")){
            mOut = convertToHex(out);
            toHexButton.setText("Binary");
        }
       else {
            mOut = convertToBin(out);
            toHexButton.setText("Hex");
        }

        machineCodeScreen.setText(mOut.toUpperCase());
    }

   private void writeInte(){// write into intermediate list
        if (lineCounter != 0 && (insCode != null || addressLabel != null)){ // case of insCode != null mean its an instruction line not a directive , case of line counter == 0 mean there is nothing to write it . It is mean the next instruction is the first line in the program
            intermediate.add(new machineCode(lineCounter,linePc, lineBase,format,insCode,addressLabel,codeRest));
            format = -1;
            insCode = null;
            addressLabel = null;
            codeRest = null;

        }
    }

   private  void writeModificationRecord(int pc,int address){
        String pcS =Integer.toBinaryString(pc+2);
        String addressLength =Integer.toBinaryString(Integer.toBinaryString(address).length());
        //Col.1 M
        modification += "M";
       //Col.2-7 starting location of the address field to be modified
        modification += fill(pcS,6,false);
       //Col.8-9 length of the address field to be modified in bits
        modification += fill(addressLength,2,false);
       //new line
        modification += "\n";
    }

   private void writeLiteral(){
        if (LTORGFlag) literalAddressCounter = PC;// in case of finding 'LTORG' then writing literal data after it
        else literalAddressCounter =  progEndAddress;// in case of writing literal data after 'END'
       LiteralTable.forEach((lineCounter,literal) ->{
          // System.out.println(lineCounter+": "+literal.getValue()+"  "+literal.getAddress());
           literal.setAddress(literalAddressCounter);//setting address for this literal value
           intermediate.add(new machineCode(-1,literalAddressCounter, -2,-1,null,literal.getValue(),null));
           literalAddressCounter+=literalAddressCounter+literal.getValue().length();
       });
       if(LTORGFlag)PC = literalAddressCounter; // if LTORGFlag = true , it is indicate that 'LTORG' is encountered
   }

   private void writeToObjectFile(){
        String programName =SymbolTable.get(0).getMnemonic_labelName();
        int ProgramStartAddress = SymbolTable.get(0).getAddress();
        String ProgramLength = Integer.toBinaryString(progEndAddress-ProgramStartAddress);
       FileWriter writer = null;

       try {
           writer = new FileWriter(new File("D:"+programName+".sicxe"));
       } catch (IOException e) {
           e.printStackTrace();
           error(e.getMessage());
       }

       try {
           if (writer != null) {
               //Header----------
               //Col. 1 H
               writer.write("H");
               //Col. 2~7 Program name
               writer.write(String.format("%-6s",programName));
               //Col. 8~13 Starting address of object program (hex)
               writer.write(fill(String.valueOf(ProgramStartAddress),5,false));
               //Col. 14-19 Length of object program in bytes (hex)
               writer.write(fill(ProgramLength,5,false)+"\n\n");
               writer.flush();


               int counter = -1;
               machineCode intemLine ;
               Iterator<CharSequence> it = machineCodeScreen.getParagraphs().iterator();
               while (it.hasNext()&& ++counter < intermediate.size()){
                  CharSequence ch = it.next();
                   intemLine =intermediate.get(counter);
                  //Text record----------
                  // Col.1 T
                  writer.write("T");
                  // Col.2~7 Starting address for object code in this record (hex)
                  writer.write(Integer.toBinaryString(intemLine.getPc()));
                  //Col. 8~9 Length of object code in this record in bytes (hex)
                  writer.write(fill(Integer.toBinaryString(ch.length()),2,false));
                  //Col. 10~69 Object code, represented in hex (2 col. per byte)
                  writer.write(ch.toString()+"\n");
                  writer.flush();
                }
                //End Record
               String End= fill(Integer.toBinaryString(ExecuteLabel),6,false);
               writer.write("\nE"+End+"\n\n");

                //Modification Record
               if (modification!= null)
               writer.write(modification);

               writer.close();

           }


       } catch (IOException e) {
           e.printStackTrace();
           error(e.getMessage());
       }
   }

}

