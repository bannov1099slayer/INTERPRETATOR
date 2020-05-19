import lb.Function;
import lb.NumberValue;
import lb.Value;

import java.util.List;

public final class UserDefinedFunction implements Function {
    private final List<String> argNames;
    private final Statement body;
    public UserDefinedFunction(List<String> argNames, Statement body){
        this.argNames = argNames;
        this.body = body;
    }
    public int getArgsCount(){
        return argNames.size();
    }
    public String getArgsName(int index){
        if (index < 0 || index >= getArgsCount()) return "";
        return argNames.get(index);
    }
    public Value execute(Value... args) {
        try {
            body.execute();
            return NumberValue.ZERO;
        } catch (ReturnStatement rt) {
            return rt.getResult();
        }
    }
}