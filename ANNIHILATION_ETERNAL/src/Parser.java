import java.util.ArrayList;
import java.util.List;

public final class Parser {
    private static final Token EOF = new Token (terminals.END_OF_FILE,"");
private final List<Token> tokens;//список токенов
private final int size;
private int pos;

public Parser(List<Token> tokens){
    this.tokens = tokens;
    size = tokens.size();
}
    public Statement parse(){
        final BlockStatement result = new BlockStatement();
        while (!match(terminals.END_OF_FILE)){
            result.add(statement());//добав выражение
        }
        return result;
    }
private Statement block(){
    final BlockStatement block = new BlockStatement();
    consume(terminals.LEFT_BRACE);
    while (!match(terminals.RIGHT_BRACE)){
        block.add(statement());//добав выражение
    }
    return block;
}
private Statement statementOrBlock(){
    if(get(0).getType() == terminals.LEFT_BRACE) return block();
    return statement();
}
private Statement statement(){
    if (match(terminals.PRINT)){
        return new PrintStatement(expression());
    }
    if(match(terminals.IF)){
        return ifElse();
    }
    if (match(terminals.WHILE)){
        return whileStatement();
    }
    if (match(terminals.DO)){
        return doWhileStatement();
    }
    if (match(terminals.BREAK)){
        return new BreakStatement();
    }
    if (match(terminals.CONTINUE)){
        return new ContinueStatement();
    }
    if (match(terminals.RETURN)){
        return new ReturnStatement(expression());
    }
    if (match(terminals.FOR)){
        return forStatement();
    }
    if (match(terminals.USERF)){
        return functionDefine();
    }

    if (get(0).getType() == terminals.WORD && get(1).getType() == terminals.LEFT_BRACKET){
        return new FunctionStatement(function());
    }
    return assignmentStatement();
}

    private Statement assignmentStatement() {//присваивание
        if(get(0).getType() == terminals.WORD && get(1).getType() == terminals.EQUALS){
            final String variable = consume(terminals.WORD).getText();
            consume(terminals.EQUALS);
            return new AssignmentStatement(variable, expression());
        }
        if(get(0).getType() == terminals.WORD && get(1).getType() == terminals.LB){
            final String variable = consume(terminals.WORD).getText();
            consume(terminals.LB);
            final Expression index = expression();
            consume(terminals.RB);
            consume(terminals.EQUALS);
            return new ArrayAssignmentStatement(variable,index,expression());
        }
        throw new RuntimeException("Unknown statement ");
    }
private Statement ifElse() {
    final Expression condition = expression();//лог выражения
    final Statement ifStatement= statementOrBlock();
    final Statement elseStatement;
    if(match(terminals.ELSE)){
        elseStatement = statementOrBlock();
    } else {
        elseStatement = null;
    }
     return new IfStatement(condition,ifStatement, elseStatement);
    }
private Statement whileStatement(){


    final Expression condition = expression();
    final Statement statement= statementOrBlock();
    return new WhileStatement(condition,statement);
}
private Statement doWhileStatement(){

    final Statement statement= statementOrBlock();
    consume(terminals.WHILE);
    final Expression condition = expression();
        return new DoWhileStatement(condition,statement);
}
private  Statement forStatement(){//для множества условий в цикле for
    final Statement initialization = assignmentStatement();
    consume(terminals.COMMA);
    final Expression termination = expression();
    consume(terminals.COMMA);
    final Statement increment = assignmentStatement();
    final Statement statement = statementOrBlock();
    return new ForStatement(initialization,termination,increment,statement);

}

    private FunctionDefine functionDefine(){
        final String name = consume(terminals.WORD).getText();
        consume(terminals.LEFT_BRACKET);
        final List<String> argNames = new ArrayList<>();
        while(!match(terminals.RIGHT_BRACKET)){
            argNames.add(consume(terminals.WORD).getText());
            match(terminals.COMMA);
        }
        final Statement body = statementOrBlock();
        return new FunctionDefine(name, argNames, body);
    }
    private FunctionalExpression function(){
        final String name = consume(terminals.WORD).getText();
        consume(terminals.LEFT_BRACKET);
        final FunctionalExpression function = new FunctionalExpression(name);
        while(!match(terminals.RIGHT_BRACKET)){
            function.addArgument(expression());
            match(terminals.COMMA);
        }
        return function;
    }
    private Expression array(){
        consume(terminals.LB);
        final List<Expression> elements = new ArrayList<>();
        while (!match(terminals.RB)){
            elements.add(expression());
            match(terminals.COMMA);
        }
        return new ArrayExpression(elements);
    }
    private Expression element(){
        final String variable = consume(terminals.WORD).getText();
        consume(terminals.LB);
        final Expression index = expression();
        consume(terminals.RB);
        return new ArrayAccessExpression(variable,index);
    }
private Expression expression(){
        return logicalOr();
    }

private Expression logicalOr(){
    Expression result = logicalAnd();
    while (true) {

        if (match(terminals.BARBAR)) {
            result = new ConditionalExpression(ConditionalExpression.Operator.OR,result,logicalAnd());
            continue;
        }
        break;
    }
    return result;
}
private Expression logicalAnd(){
    Expression result = equality();
    while (true) {

        if (match(terminals.AMPAMP)) {
            result = new ConditionalExpression(ConditionalExpression.Operator.AND,result,equality());
            continue;
        }
        break;
    }
    return result;
}
private  Expression equality(){
    Expression result = conditional();
    if (match(terminals.EQEQ)){
        return new ConditionalExpression(ConditionalExpression.Operator.EQUALS,result,conditional());

    }
    if (match(terminals.EXCLEQ)){
        return new ConditionalExpression(ConditionalExpression.Operator.NOT_EQUALS,result,conditional());
    }
    return result;
}
private  Expression conditional(){
    Expression result = additive();
    while (true){

        if (match(terminals.LESSER)){
            result = new ConditionalExpression(ConditionalExpression.Operator.LT,result,additive());
            continue;
        }
        if (match(terminals.LEQ)){
            result = new ConditionalExpression(ConditionalExpression.Operator.LTEQ,result,additive());
            continue;
        }
        if (match(terminals.GREATER)){
            result = new ConditionalExpression(ConditionalExpression.Operator.GT,result,additive());
            continue;
        }
        if (match(terminals.GEQ)){
            result = new ConditionalExpression(ConditionalExpression.Operator.GTEQ,result,additive());
            continue;
        }
        break;
    }
    return result;
}
private Expression additive() {
    Expression result = multiplicative();
    while (true){
        if (match(terminals.OP_PLUS)){
            result = new BinaryExpression('+',result,multiplicative());
            continue;
        }
        if (match(terminals.OP_SUBTRACTION)){
            result = new BinaryExpression('-',result,multiplicative());
            continue;
        }
        break;
    }
return result;
    }
private Expression multiplicative(){
    Expression result = unary();
    while (true){
        if (match(terminals.OP_MULTIPLY)){
            result = new BinaryExpression('*',result,unary());
       continue;
        }
        if (match(terminals.OP_DIVIDE)){
           result = new BinaryExpression('/',result,unary());
        continue;
        }
        break;
    }
return result;
}
private Expression unary(){//для того, чтобы были отрицательные числа
    if (match(terminals.OP_SUBTRACTION)){
        return new UnaryExpression('-', primary());
    }
    if (match(terminals.OP_PLUS)){
        return primary();
    }

return primary();
}
private Expression primary(){
final Token current = get(0);
if (match(terminals.DIGIT)){
    return new ValueExpression(Double.parseDouble (current.getText()));
}
    if (match(terminals.HEX_DIGIT)){
        return new ValueExpression(Long.parseLong (current.getText(), 16));
    }
    if (get(0).getType() == terminals.WORD && get(1).getType() == terminals.LEFT_BRACKET){
        return function();
    }
    if (get(0).getType() == terminals.WORD && get(1).getType() == terminals.LB){
        return element();
    }
    if ( get(0).getType() == terminals.LB){
        return array();
    }
    if (match(terminals.WORD)){
        return new VariableExpression(current.getText());
    }
    if (match(terminals.TEXT)){
        return new ValueExpression(current.getText());
    }
if (match(terminals.LEFT_BRACKET)){
    Expression result = expression();
    match(terminals.RIGHT_BRACKET);
    return result;
}
throw new RuntimeException("Unknown expression");
}
    private Token consume(terminals type){//проверка типа токена
        final Token current = get(0);
        if (type != current.getType()) throw new RuntimeException("Token " + current + "doesn't match" + type);
        pos++;
        return current;
    }
private boolean match(terminals type){//проверка типа токена
    final Token current = get(0);
    if (type != current.getType()) return false;
    pos++;
    return true;
}
    private Token get(int relativePosition){//получение текущего символа
        final int position = pos + relativePosition;
        if (position >= size)
            return EOF;
        return tokens.get(position);
    }

}
