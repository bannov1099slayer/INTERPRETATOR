package lb;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Variables {
    private static final NumberValue ZERO = new NumberValue(0);
    private static Map<String , Value > variables;
    private static final Stack<Map<String , Value >> stack;
    static {
        stack = new Stack<>();

        variables = new HashMap<>();
        variables.put("PI" , new NumberValue(Math.PI));
        variables.put("ПИ" , new NumberValue(Math.PI));
        variables.put("E" , new NumberValue(Math.E));
    }

    public static void push(){
        stack.push(new HashMap<>(variables));
    }
    public static void pop(){
        variables = stack.pop();
    }

    public static boolean isExists(String key){
        return variables.containsKey(key);
    }
    public static Value get(String key){
        if (!isExists(key)) return ZERO;
        return variables.get(key);
    }

    public static void set(String key, Value value){
        variables.put(key,value);

    }
}
