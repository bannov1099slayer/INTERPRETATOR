import lb.ArrayValue;
import lb.Value;
import lb.Variables;

public final class ArrayAssignmentStatement implements Statement {
    private final String variable;
    private final Expression index;
    private final Expression expression;
    public ArrayAssignmentStatement (String variable, Expression index , Expression expression){
        this.variable = variable;
        this.index = index;
        this.expression = expression;
    }

    public void execute(){
        final Value var = Variables.get(variable);
        if(var instanceof ArrayValue){
            final ArrayValue array = (ArrayValue) var;
            array.set((int) index.eval().asNumber(),expression.eval());
        }else {
            throw new RuntimeException("Array expected");
        }
    }
    public String toString(){
        return String.format("%s[%s] = %s", variable,index,expression);
    }
}