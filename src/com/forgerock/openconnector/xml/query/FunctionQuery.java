
package com.forgerock.openconnector.xml.query;


public class FunctionQuery implements IPart {

    private String [] args;
    private String function;
   
    
    public FunctionQuery(String [] args, String function) {
        this.args = args;
        this.function = function;
    }

    // creates function-expression.
    // all args have to be prefixed with $x/, '', etc
    public String getExpression() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.function); 
        sb.append("(");

        // add args to function
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]);
            if (i < args.length-1)
                sb.append(", ");
        }

        sb.append(")");
        return sb.toString();
    }
}
