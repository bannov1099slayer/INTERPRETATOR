import lb.NumberValue;
import lb.StringValue;
import lb.Value;

public class ValueExpression implements Expression{
    private final Value value;

    public ValueExpression(double value){
        this.value = new NumberValue(value);
    }
    public ValueExpression(String value){
        this.value = new StringValue(value);
    }
    @Override
    public Value eval() {
        return value;
    }
    public String toString (){
        return value.asString();
    }

}
