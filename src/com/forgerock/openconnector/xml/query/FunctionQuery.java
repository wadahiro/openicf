
package com.forgerock.openconnector.xml.query;


public class FunctionQuery implements IPart {

    private String [] args;
    private String function;
    private boolean not;
   
    
    public FunctionQuery(String [] args, String function, boolean not) {
        this.args = args;
        this.function = function;
        this.not = not;
    }

    // creates function-expression.
    // all args have to be prefixed with $x/, '', etc
    @Override
    public String getExpression() {
        if (not) {
            return createFalseExpression();
        } else {
            return createTrueExpression();
        }
    }

    private String createTrueExpression() {
        StringBuilder sb = new StringBuilder();
        sb.append("fn:");
        sb.append(this.function);
        sb.append("(");
        addArgs(sb);
        sb.append(")");
        return sb.toString();
    }

    private String createFalseExpression() {
        StringBuilder sb = new StringBuilder();
        sb.append("fn:");
        sb.append("not(");
        sb.append(this.function);
        sb.append("(");
        addArgs(sb);
        sb.append("))");
        return sb.toString();
    }

    private void addArgs(StringBuilder sb) {
        // add args to function
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]);
            if (i < args.length-1)
                sb.append(", ");
        }
    }
}
