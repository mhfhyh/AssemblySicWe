package sample;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.util.Iterator;
import java.util.LinkedList;


public class parser extends lexer{
@FXML
TextArea inputScreen;

@FXML
TextArea errorScreen;

@FXML
    Button ok;

    private int lookahead;
    private int numOfWord;
    private int lineCounter=0;
    public void okOnAction(){

        ObservableList<CharSequence> code = inputScreen.getParagraphs();
        Iterator<CharSequence> it = code.iterator();

        while (it.hasNext()){//iterate throw all the code
            lineCounter++;
            words = splitIgnoreSpaces(it.next().toString());
            currWordIndex=0;
            numOfWord = words.size();
            if (words != null){//in cass of null that is mean this lin is a line comment
                lookahead =lexical();
                sic(); }
        }


    }

    private void sic(){

        header();
        body();
        tail();

    }

    private void header(){
    newID = true;
    match(ID);
    newID = false;
    match(START);
    match(NUM);
    SymbolTable.add(new entry(label,ID,tokenVal)); label = null; tokenVal = -1;


    }

    private void body(){

        if (lookahead == ID){
            newID = true;
            match(ID);
            newID = false;
            SymbolTable.add(new entry(label,ID,PC)); label = null; tokenVal = -1;
            reset();
            body();
        }
        else if(lookahead == FORMAT1 || lookahead == FORMAT2 || lookahead == FORMAT3 ||lookahead == PLUS ) {
            stmt();
            body();
        }


    }

    private void tail(){
        match(END);
        match(ID);

    }

    private void reset(){
        if (lookahead == FORMAT1 || lookahead == FORMAT2 || lookahead == FORMAT3 ||lookahead == PLUS)
            stmt();
        else if (lookahead == WORD || lookahead == BYTE || lookahead == RESW ||lookahead == RESB)
            data();
        else ; error("un unexpected token. found: "+tokensWithStrings.get(lookahead));//error


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
                if (lookahead == STRING) {match(STRING); PC += label.length();}
                else if (lookahead == HEX) {match(HEX); PC += (label.length())/2;}
                else  error("un unexpected token. found: "+tokensWithStrings.get(lookahead)); //error
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
            if(currWordIndex < numOfWord)lookahead = lexical();}
        else error("syntax error: un unexpected token. expected: "+tokensWithStrings.get(tok)+" found: "+tokensWithStrings.get(lookahead)); //syntax error
    }


    private LinkedList<String> splitIgnoreSpaces(String text){

        if(text.charAt(0) == '/') return null; //this is in case of the line is a comment line

        LinkedList<String> Words = new LinkedList<>();
        int index=0;
        String word="";
        boolean commentFlag = false;//this indicate finding comment to ignore the reset -> 'false' indicate not finding comment tell now

        for(int i=0;i<text.length();i++){// iterate throw all the line

            while (i<text.length() && text.charAt(i)!= ' ' ){// this for finding the complete word to add it

                char ch = text.charAt(i);
                if (ch == '/'){commentFlag = true; break;}//case of comment to ignore all the text after it
                if (ch != '@' && ch != '#' && ch != ',' && !Character.isDigit(ch) && !Character.isLetter(ch))error("un accepted character");//error un accepted char

                if(ch == '@' || ch == '#' || ch ==','){//this is in case if finding ',' comma before or after word. Finding text followed by ('#' or '@') without space in between
                        if (word.length()>0){
                           if (ch == ','){Words.add(word);word="";}
                           else error("text is followed by "+ch+" without space in between");
                        }
                    Words.add(",");
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

        }
        return Words;
    }//end of splitIgnoreSpaces


    private void error(String errorMsg){

         errorScreen.setText("Error line: "+lineCounter+' '+errorMsg);
        System.out.println("Error line: "+lineCounter+' '+errorMsg);
        /*System.exit(0);*/
        try {
            throw new Exception();
        } catch (Exception e) {
            errorScreen.setText("Error line: "+lineCounter+' '+errorMsg);
            Thread.currentThread().stop();

        }
        /*try {
                this.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }*/
        // error

    }
}

