package assembler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;


public class parser extends lexer{
    @FXML
    TextArea inputScreen;

    @FXML
    protected TextArea errorScreen;



    @FXML TableView<entry> SymbolTableView;
    @FXML TableColumn<entry,String> LabelColumn;
    @FXML TableColumn<entry,Integer> TypeColumn ;
    @FXML TableColumn<entry,Integer> AddressColumn ;
    @FXML TextArea machineCodeScreen ;
    @FXML Button toHex;

    private int lookahead = -1;
    private int numOfWord;
    private ArrayList<machineCode> intermediate = new ArrayList<>();
    private int format = -1;
    private String addressLabel = null;
    private String insCode = null;
    private String codeRest = null;

    private int Base = 0;
    private int lineBase = 0;
    private int linePc = 0;


    private ObservableList<CharSequence> code ;


    public void okOnAction(){

        tokenVal = -1;
        PC = 0;
        label = null;
        errorNum = -1;
        lineCounter = 0;
        errorScreen.setText("");
        SymbolTable = new ArrayList<>();
        //initialize the variables with zero values to began new assembling . At each time user press ok program began from scratch

        code = inputScreen.getParagraphs();
        nextSentence();
       // if (words != null)// in case of comment will skip until find a statement not a comment
            sic();

        //for symbol screen table
    ObservableList<entry> list=FXCollections.observableArrayList(SymbolTable);
        LabelColumn.setCellValueFactory(new PropertyValueFactory<>("mnemonic"));
        TypeColumn.setCellValueFactory(new PropertyValueFactory<>("token"));
        AddressColumn.setCellValueFactory(new PropertyValueFactory<>("opcode"));

        SymbolTableView.setItems(list);
    }

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

        SymbolTable.add(new entry(currLabel,ID,tokenVal));//adding the starting program label with its address to symbol table

    }

    private void body(){

        if (lookahead == ID){
            String currLabel = label;// getting the 'ID' string value from the lexical analyzer
            match(ID);
                SymbolTable.add(new entry(currLabel,ID,PC));
            rest();
            body();
        }
        else if(lookahead == FORMAT1 || lookahead == FORMAT2 || lookahead == FORMAT3 ||lookahead == PLUS ) {
            stmt();
            body();
        }
        else if(lookahead == BASE)
            directive();


    }
    private void directive(){
        if (lookahead == BASE){
            match(BASE);

            if (lookahead == NUM)

                match(NUM);

            else match(ID);
        }


    }

    private void tail(){
        match(END);
        match(ID);

    }

    private void rest(){
        if (lookahead == FORMAT1 || lookahead == FORMAT2 || lookahead == FORMAT3 ||lookahead == PLUS)
            stmt();
        else if (lookahead == WORD || lookahead == BYTE || lookahead == RESW ||lookahead == RESB)
            data();
       // else error("un unexpected token. found: "+tokensWithStrings.get(lookahead));//error


    }

    private void stmt(){

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
        linePc = PC;
        lineBase = Base;
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
            match(ID);
           // index();
        }
        else errorWithNext("syntax error: un unexpected token. expected: ID or HASH or ATT found: "+tokensWithStrings.get(lookahead));
    }
    private void index(){
        if (lookahead == COMMA){
            match(COMMA);
                        format++;
            match(REGISTER);
        }


    }
    private void imm(){

        if (lookahead == NUM)
            match(NUM);

        else if (lookahead == ID)
            match(ID);

        else errorWithNext("un unexpected token. expected NUM or ID found: "+tokensWithStrings.get(lookahead));//error

    }

    private void data(){
            format = 0;
        switch (lookahead){
            case WORD:

                match(WORD);
                            insCode = fill(Integer.toBinaryString(tokenVal),23,false);// getting the word value and save it as hex
                match(NUM);
                PC += 3;
                break;

            case BYTE:
                 match(BYTE);

                if (lookahead == STRING){
                    match(STRING);
                    match(QUOTE);
                                PC += label.length();
                                insCode = toAsciiBin(label);// getting the char byte value
                    match(BYTEVLA);
                    match(QUOTE);}

                else if (lookahead == HEX) {
                    match(HEX);
                    match(QUOTE);
                                PC += (label.length())/2;
                                insCode = label;// getting the hex byte value
                    match(BYTEVLA);
                    match(QUOTE);
                    }
                //else  error("un unexpected token. found: "+tokensWithStrings.get(lookahead)); //error
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
//-------------------end of grammars-------------------

    private void match(int tok){

        //if (lookahead != tok) mark it as error and look for the next
        if (lookahead != tok){ error("syntax error: un unexpected token. expected: "+tokensWithStrings.get(tok)+" found: "+tokensWithStrings.get(lookahead));}

        if(currWordIndex == numOfWord)
            nextSentence();
        else
             lookahead = lexical();
    }


    public void error(String errorMsg){
        String old = errorScreen.getText();
        if (old == null )old = "";
        errorScreen.setText(old +"\n"+
                (lineCounter != 0?("Error line: "+lineCounter+' '):"")+ errorMsg);
    }

    //Mark error with given message 'errorMsg' and find next token
    private void errorWithNext(String errorMsg){
        error(errorMsg); //syntax error
        if(currWordIndex == numOfWord)
            nextSentence();
        else
            lookahead = lexical();
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
                lookahead = lexical();// we do lexical analyzing to the first word in the new line
                break;
            }

            lineCounter++;
        }
        if (lineCounter > code.size()){lineCounter = 0;}//that's mean it reach the end of the program
    }

    private void pass2() {

        String output = "";
        Iterator<machineCode> it = intermediate.iterator();
        while (it.hasNext()){
            machineCode line = it.next();
            System.out.println("Line: "+line.getLine()+"Format: "+line.getFormat()+"InsCode: "+line.getInsCode()+"AddressLabel: "+line.getAddressLabel()+"CodeRest: "+line.getCodeRest());
            switch (line.getFormat()){
                case 0:case 1: output += line.getInsCode()+"\n";
                       break;
                case 2: output += line.getInsCode()+line.getAddressLabel(4,false)+ (line.getCodeRest()== null ? "" :line.getCodeRest(4,false))+"\n";
                        break;
                case 3: case 4:case 5: case 6: output += line.getInsCode().substring(0,5)+optimizeAddressLabel(line.getLine(),line.getAddressLabel(),line.getPc(),line.getBase(),line.getFormat())+"\n";
                                                break;//3 -> format 3,4 -> format 3 with indexing,5 -> format 3 with intermediate,6 -> format 3 with indirect.

                // ask Pro.Othman -> should we add the value of register x to the final address ?  case 4: output += line.getInsCode().substring(0,5)+optimizeAddressLabel(line.getAddressLabel(),line.getPc(),line.getBase(),line.getFormat())+"\n";
                case 7: output += line.getInsCode().substring(0,5)+"110001"+isExist(line.getLine(),line.getAddressLabel())+"\n";
                        break;//7 -> format 4,
                case 8: output += line.getInsCode().substring(0,5)+"111001"+isExist(line.getLine(),line.getAddressLabel())+"\n";
                        break;//8 -> format 4 with indexing,
                case 9: output += line.getInsCode().substring(0,5)+"010001"+isExist(line.getLine(),line.getAddressLabel())+"\n";
                        break;//9 -> format 4 with intermediate,
                case 10: output += line.getInsCode().substring(0,5)+"100001"+isExist(line.getLine(),line.getAddressLabel())+"\n";
                        break;//10 -> format 4 with indirect.

            }

        }
        machineCodeScreen.setText(output);
        toHex.setDisable(false);
    }
    private String isExist(int line,String addrLabel){
        int index = SymbolTable.indexOf(new entry(addrLabel,0,0));//at the beginning we check if the label is defined before
        if (index != -1)
            return fill(Integer.toBinaryString(SymbolTable.get(index).getAddress()),20,false);

        error("Line: "+line+" undefined Label "+addrLabel);
        return null;
    }

    /*this function has tow tasks first determine wither given label is already defined in the SymbolTable or not.
     If it is not mark it as error and return null.If it already defined find the address of that label and go to task 2.
     Task 2 is checking wither given address fit in 12 bits if it is yes return that address as string.
     If it is not make the address relative to PC or Base and return that address */
    private String optimizeAddressLabel(int line,String addressLabel, int pc , int base, int format){

        int index = SymbolTable.indexOf(new entry(addressLabel,0,0));//at the beginning we check if the label is defined before

        if (index != -1){
            int upperBound = 4095 ;
            /*if (format == 5 || format == 6 || format == 9 || format == 10 || format == 13 || format == 14)
                upperBound = 1048575;*/
            entry lab = SymbolTable.get(index);

            int address = lab.getAddress();

            if (lab.getAddress() > upperBound) address = address - pc; //relative address to pc
            else; //modification record;

            if (address > upperBound) {error(address+" not fit"); return null;} //relative address to pc not work

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


    private void writeInte(){
        if (lineCounter != 0 && insCode != null){ // case of insCode != null mean its an instruction line not a directive
            intermediate.add(new machineCode(lineCounter,linePc, lineBase,format,insCode,addressLabel,codeRest));
            format = -1;
            insCode = null;
            addressLabel = null;
            codeRest = null;

        }
    }

    private String toAsciiBin(String string){

        String  result= "";
        for (char x : string.toCharArray()){
            result += Integer.toBinaryString((int)x);
        }

        return result;
    }

    //----------------------------------------------
    //the aim of this function is to convert given text code to ArrayList of string it is ignoring the comment line and the (free lines)
    private LinkedList<String> splitIgnoreSpaces(String text){

        if(text.length() == 0 || text.charAt(0) == '/') return null; //this is in case of the line is a comment line OR (text.length() == 0) to skip the line spaces

        LinkedList<String> Words = new LinkedList<>();
        int index = 0;
        String word="";
        boolean commentFlag = false;//this indicate finding comment to ignore the rest -> 'false' indicate not finding comment tell now
        boolean unAcChar = false;//this indicate finding un accepted character

        for(int i=0;i<text.length();i++){// iterate throw all the line

            while (i<text.length() && text.charAt(i)!= ' ' ){// this for finding the complete word to add it

                char ch = text.charAt(i);
                if (ch == '/'){commentFlag = true; break;}//case of comment to ignore all the text after it
                if (ch != '@' && ch != '#' && ch != '+'&& ch != ',' && ch != '\'' && ch != '=' && !Character.isDigit(ch) && !isEnglish(ch)){unAcChar = true; error("un accepted character ' "+ch+" '");break;}//error un accepted char}

                if (ch == '\''){
                    if (word.length()>0) {
                        if (word.equalsIgnoreCase("c") || word.equalsIgnoreCase("h")) {
                            Words.add(word);
                            word = "";
                        } else error("un accepted prefix ' " + word + " ' before quote , accepted is ether 'c' OR 'h'");
                    }
                    Words.add("\'");
                    i++;
                    String quotedText="";
                    while (text.charAt(i)!='\''){
                        quotedText += String.valueOf(text.charAt(i++));
                        if (i == text.length()) {error("ending quote \' not found"); break;}
                    }
                    Words.add(quotedText);
                    if (text.charAt(i)=='\''){ i++; Words.add("\'");}
                    continue; //while we done we have to skip this iteration to next one
                }
                if(ch == '@' || ch == '#'|| ch == '+' || ch == ',' || ch == '='){//this is in case if finding ',' comma before or after word. Finding text followed by ('#' or '@') without space in between
                    if (word.length()>0){
                        if (ch == ',' ){Words.add(word);word="";}
                        else error("text is followed by "+ch+" without space in between");
                    }
                    Words.add(String.valueOf(ch));
                    break;
                }//end of case comma


                word+=text.charAt(i);
                i++;
            }

            if (word.length()>0){
                Words.add(word);
                word="";
            }

            if (commentFlag)break;//complement of case of comment
            if (unAcChar)return null;

        }
        return Words;
    }//end of splitIgnoreSpaces

    private boolean isEnglish(char ch){
        if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))return true;
        return false;
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
boolean hexFlag =false;
    public void toHexOnAction(){
        int currBase = 2;
        int toBase = 16;
        toHex.setText("Binary");
        if (hexFlag){
            currBase = 16;
            toBase = 2;
            toHex.setText("Hex");
        }

        ObservableList<CharSequence> out = machineCodeScreen.getParagraphs();
         String mOut = "";
        Iterator<CharSequence> it =out.iterator();
        while (it.hasNext()){
            int lin = Integer.parseInt((it.next()).toString(),currBase);
            mOut += Integer.toString(lin,toBase);
        }

        machineCodeScreen.setText(mOut);



    }
}

