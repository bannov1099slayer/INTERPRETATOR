import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Lexer {
    private static final String OPERATOR_CHARS = "+-*/()[]{}=<>!&|,";//симолы строки

    private static final Map<String, terminals> OPERATORS;
    static {
        OPERATORS = new HashMap<>();
        OPERATORS.put("+",terminals.OP_PLUS);
        OPERATORS.put("-",terminals.OP_SUBTRACTION);
        OPERATORS.put("*",terminals.OP_MULTIPLY);
        OPERATORS.put("/",terminals.OP_DIVIDE);
        OPERATORS.put("(",terminals.LEFT_BRACKET);
        OPERATORS.put(")",terminals.RIGHT_BRACKET);
        OPERATORS.put("[",terminals.LB);
        OPERATORS.put("]",terminals.RB);
        OPERATORS.put("{",terminals.LEFT_BRACE);
        OPERATORS.put("}",terminals.RIGHT_BRACE);
        OPERATORS.put("=",terminals.EQUALS);
        OPERATORS.put("<",terminals.LESSER);
        OPERATORS.put(">",terminals.GREATER);
        OPERATORS.put(",",terminals.COMMA);
        OPERATORS.put("!",terminals.EXCL);
        OPERATORS.put("&",terminals.AMP);
        OPERATORS.put("|",terminals.BAR);
        OPERATORS.put("==",terminals.EQEQ);
        OPERATORS.put("!=",terminals.EXCLEQ);
        OPERATORS.put("<=",terminals.LEQ);
        OPERATORS.put(">=",terminals.GEQ);
        OPERATORS.put("&&",terminals.AMPAMP);
        OPERATORS.put("||",terminals.BARBAR);
    }
    private final String input;
    private final int length;//длина строки
    private List<Token> tokens;//список токенов
    private int pos;

    public Lexer(String input) {//конструктор сохраняет строку(input) в поле
        this.input = input;
        length = input.length();
        tokens = new ArrayList<>();
    }

    public List<Token> tokenize() {//возвращает список из токенов
        while (pos < length) {
            final char current = peek(0);//текущий символ
            if (Character.isDigit(current)) tokenizeNumber();//проверка число
            else if (Character.isLetter(current)) tokenizeWord();
            else if (current == '#') {
                next();
                tokenizeHexNumber();
            }
            else if (current == '"') {
                tokenizeText();
            }
            else if (OPERATOR_CHARS.indexOf(current) != -1) {//проверка символа
                tokenizeOperator();
            } else {
                next();
            }

        }
        return tokens;
    }

    private void tokenizeNumber() {
        final StringBuilder buffer = new StringBuilder();//буффер
        char current = peek(0);//текущий символ
        while (true) {//
            if (current == '.') {
                if (buffer.indexOf(".") != -1) throw new RuntimeException("Invalid float number");
            } else if (!Character.isDigit(current)) {
                break;
            }
            buffer.append(current);//добавляем в буффер текущий  символ
            current = next();//след символ

        }
        addToken(terminals.DIGIT, buffer.toString());//добавляем число и его текст
    }

    private void tokenizeHexNumber() {
        final StringBuilder buffer = new StringBuilder();
        char current = peek(0);
        while (Character.isDigit(current) || isHexNumber(current)) {
            buffer.append(current);
            current = next();

        }
        addToken(terminals.HEX_DIGIT, buffer.toString());
    }

    private static boolean isHexNumber(char current) {
        return "abcdef".indexOf(Character.toLowerCase(current)) != -1;
    }

    private void tokenizeOperator() {
        char current = peek(0);//текущий символ
        if (current == '/'){
            if (peek(1)=='/'){
                next();
                next();
                tokenizeComment();
                return;
            } else if (peek(1)=='*'){
                next();
                next();
                tokenizeMultilineComment();
                return;
            }
        }
        final StringBuilder buffer = new StringBuilder();
        while (true){
            final String text = buffer.toString();
            if(!OPERATORS.containsKey(text + current) && !text.isEmpty()){
               addToken(OPERATORS.get(text));
               return;
            }
            buffer.append(current);
            current = next();
        }
    }

    private void tokenizeWord() {
        final StringBuilder buffer = new StringBuilder();//буффер
        char current = peek(0);//текущий символ
        while (true) {//пока идут числовые символы они добавляются в буффер

            if (!Character.isLetterOrDigit(current) && (current != '_') && (current != '$')) {
                break;
            }
            buffer.append(current);//добавляем в буффер текущиий  символ
            current = next();//следующий символ
        }

        final String word = buffer.toString();
        switch (word){
            case "print": addToken(terminals.PRINT); break;
            case "if":    addToken(terminals.IF); break;
            case "else":    addToken(terminals.ELSE); break;
            case "while":   addToken(terminals.WHILE);break;
            case "for":   addToken(terminals.FOR);break;
            case "do":   addToken(terminals.DO);break;
            case "break":   addToken(terminals.BREAK);break;
            case "continue":addToken(terminals.CONTINUE);break;
            case "userf":addToken(terminals.USERF);break;
            case "return":addToken(terminals.RETURN);break;
            default:
                addToken(terminals.WORD, word);
                break;
        }

    }
    private void tokenizeText() {
        next();//пропуск одной "
        final StringBuilder buffer = new StringBuilder();//буффер
        char current = peek(0);//текущий символ
        while (true) {//пока идут числовые символы они добавляются в буффер
            if (current == '\\'){
                current = next();
                switch (current){
                    case '"': current = next(); buffer.append('"');continue;
                    case 'n': current = next(); buffer.append('\n');continue;
                    case 't': current = next(); buffer.append('\t');continue;
                }
                buffer.append('\\');
                continue;
            }
            if ( current == '"') {//условие выхода
                break;
            }
            buffer.append(current);//добавляем в буффер текущий  символ
            current = next();//следующий символ
        }
        next();//пропуск закрывающей кавычки
        addToken(terminals.TEXT, buffer.toString());

    }
private void tokenizeComment(){
     char current = peek(0);
     while("\r\n\0".indexOf(current) == -1){
         current = next();
     }
}
private void tokenizeMultilineComment(){
    char current = peek(0);
    while(true){
        if(current == '\0') throw new RuntimeException("Missing close tag");
        if(current == '*' && peek(1) == '/') break;
        current = next();
    }
    next();
    next();
}
private char next(){//увеличиваем позицию и возвращаем текущий символ
       pos++;
       return peek(0);
    }
private char peek(int relativePosition){//для просмотра символов
final int position = pos + relativePosition;//берем символ следующий после текущего
if (position >= length)
    return '\0';
return input.charAt(position);
  }
private void addToken(terminals type){
    addToken(type, "");
}//добавление токенов по типу(для операций)
private void addToken(terminals type, String text){
    tokens.add(new Token(type,text));
}//добавление токенов по типу(для чисел)
}
