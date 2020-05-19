import lb.Value;
import lb.Variables;

public class AssignmentStatement implements Statement{
    private final String variable;
    private final Expression expression;
    public AssignmentStatement(String variable, Expression expression){
        this.variable= variable;
        this.expression=expression;
    }
    public void execute(){
        final Value result = expression.eval();
        Variables.set(variable,result);
    }
    public String toString(){
        return String.format("%s = %s", variable, expression);
    }
}

