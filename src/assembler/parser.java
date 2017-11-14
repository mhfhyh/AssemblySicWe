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
import java.util.LinkedList;


public class parser extends lexer{
    @FXML
    TextArea inputScreen;

    @FXML
    TextArea errorScreen;

    @FXML
    Button ok;

    @FXML TableView<entry> SymbolTableView;
    @FXML TableColumn<entry,String> LabelColumn;
    @FXML TableColumn<entry,Integer> TypeColumn ;
    @FXML TableColumn<entry,Integer> AddressColumn ;

    private int lookahead = -1;
    private int numOfWord;
    private ArrayList<machineCode> intermediate = new ArrayList<>();
    private String addressLabel = null;
    private String mCode = "";
    private String codeRest = null;
    private boolean absoluteFlag = false;

    ObservableList<CharSequence> code ;

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
        if (words != null)// in case of comment will skip until find a statement not a comment
            sic();


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
        //newID = true;
        match(ID);
        String currLabel = label;
       // newID = false;
        match(START);
        match(NUM);
        PC = tokenVal;
        SymbolTable.add(new entry(currLabel,START,tokenVal));


    }

    private void body(){

        if (lookahead == ID){
            String currLabel = label;
            match(ID);
                SymbolTable.add(new entry(currLabel,ID,PC));
            rest();
            body();
        }
        else if(lookahead == FORMAT1 || lookahead == FORMAT2 || lookahead == FORMAT3 ||lookahead == PLUS ) {
            stmt();
            body();
        }


    }

    private void tail(){
        match(END);
        match(START);

    }

    private void rest(){
        if (lookahead == FORMAT1 || lookahead == FORMAT2 || lookahead == FORMAT3 ||lookahead == PLUS)
            stmt();
        else if (lookahead == WORD || lookahead == BYTE || lookahead == RESW ||lookahead == RESB)
            data();
        else error("un unexpected token. found: "+tokensWithStrings.get(lookahead));//error


    }

    private void stmt(){
        switch (lookahead){
            case FORMAT1:
                match(FORMAT1);
                PC += 1;
                break;

            case FORMAT2:
                match(FORMAT2);
                PC += 2;
                match(REGISTER);
                y();
                break;

            case FORMAT3:
                match(FORMAT3);
                PC += 3;
                z();
                break;

            case PLUS:
                match(PLUS);
                PC += 4;
                match(FORMAT3);
                z();
                break;
            default: error("un unexpected token. found: "+tokensWithStrings.get(lookahead));//error
        }

    }
    private void y(){
        if (lookahead == COMMA){
            match(COMMA);
            match(REGISTER);
        }
    }
    private void z(){
        if (lookahead == ID){
            match(ID);
            index();
        }
        else if (lookahead == HASH){
            match(HASH);
            imm();
            index();
        }
        else if(lookahead == AAT){
            match(AAT);
            match(ID);
            index();
        }

    }
    private void index(){
        if (lookahead == COMMA){
            match(COMMA);
            match(REGISTER);
        }

    }
    private void imm(){

        if (lookahead == NUM)
            match(NUM);

        else if (lookahead == ID)
            match(ID);

        else error("un unexpected token. found: "+tokensWithStrings.get(lookahead));//error

    }

    private void data(){

        switch (lookahead){
            case WORD:
                match(WORD);
                match(NUM);
                PC += 3;
                break;

            case BYTE:
                match(BYTE);
                String byteValue="";
                if (lookahead == STRING){
                    match(STRING);
                    match(QUOTE);
                    match(BYTEVLA);
                    match(QUOTE);PC += label.length();}

                else if (lookahead == HEX) {
                    match(HEX);
                    match(QUOTE);
                    match(BYTEVLA);
                    match(QUOTE);
                    PC += (label.length())/2;}
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
            default: error("un unexpected token. found: "+tokensWithStrings.get(lookahead));//error
        }
    }
//-------------------end of grammars-------------------

    private void match(int tok){
        if (lookahead == tok){
            if(currWordIndex == numOfWord)
                nextSentence();
            else
                lookahead = lexical();

        }
        else error("syntax error: un unexpected token. expected: "+tokensWithStrings.get(tok)+" found: "+tokensWithStrings.get(lookahead)); //syntax error
    }


    private LinkedList<String> splitIgnoreSpaces(String text){

        if(text.length() == 0 || text.charAt(0) == '/') return null; //this is in case of the line is a comment line , (text.length() == 0) to skip the line spaces

        LinkedList<String> Words = new LinkedList<>();
        int index=0;
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


    private void error(String errorMsg){

        //  errorScreen.setText("Error line: "+lineCounter+' '+errorMsg);

        /*System.exit(0);*/
        try {
            throw new Exception();
        } catch (Exception e) {

            String old = errorScreen.getText();
            if (old == null )old = "";
            errorScreen.setText(old +"\n"+
                    "Error line: "+lineCounter+' '+errorMsg);

        }
        /*try {
                this.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }*/
        // error

    }

    private void nextSentence(){
        if (lineCounter != 0){
            intermediate.add(new machineCode(addressLabel,lineCounter,mCode,codeRest,absoluteFlag));
            addressLabel =null;
            mCode ="";
            codeRest = null;
            absoluteFlag = false;
        }//writing the translated machine code to the intermediate list

        lineCounter++;
        while (lineCounter <= code.size()){
            words = splitIgnoreSpaces(code.get(lineCounter-1).toString());
            if (words != null){
                currWordIndex = 0;
                numOfWord = words.size();
                lookahead = lexical();
                break;
            }
            lineCounter++;
        }
        if (lineCounter > code.size()){lineCounter = 0;}
        /*try {
            if (lineCounter > code.size())Thread.currentThread().stop();
        }catch (Exception e) {
   // errorScreen.setText("Done ");
        }*/


    }

    private void pass2() {

    }
    private boolean isEnglish(char ch){
        if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))return true;
        return false;
    }

    public void TableButtonOnAction(){

    }
}

