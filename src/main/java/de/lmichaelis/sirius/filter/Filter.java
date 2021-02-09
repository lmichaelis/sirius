package de.lmichaelis.sirius.filter;

import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public abstract class Filter {
    private Pattern pattern;
    private String expression;
    private boolean isCaseInsensitive;
    private boolean isLiteral;

    public void compile() throws PatternSyntaxException {
        int flags = 0;
        if(isCaseInsensitive){
            flags |= Pattern.CASE_INSENSITIVE;
            flags |= Pattern.UNICODE_CASE;
        }
        if (isLiteral) {
            flags |= Pattern.LITERAL;
        }
        pattern = Pattern.compile(expression, flags);
    }

//    public Matcher match(Text input){
//        return pattern.matcher(input);
//    }
//
    public boolean isMatch(Text input){
//        return match(input).matches();
        return false;
    }

    public abstract Text run(Text input);
}
