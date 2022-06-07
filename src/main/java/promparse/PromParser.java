package promparse;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * @author Sean
 * @date 2022/6/2 18:01
 */
class Sample {
    String metricName;
    ArrayList<String> labelNames;
    ArrayList<String> labelValues;
    String val;
    String ts;
    boolean hasTs;
    /**
     * count of label pairs
     */
    int i;

    Sample(int buffer_size) {
        labelNames = new ArrayList<>(buffer_size);
        labelValues = new ArrayList<>(buffer_size);
        hasTs = false;
        i = 0;
    }
    public void addln(String e) {
        if(labelNames.size()>i)
            labelNames.set(i,e);
        else
            labelNames.add(e);
    }
    public void addlv(String e) {
        if(labelValues.size()>i)
            labelValues.set(i,e);
        else
            labelValues.add(e);
        ++i;
    }

    public void reset() {
        i = 0;
        hasTs = false;
    }

    public void clear() {
        labelNames.clear();
        labelValues.clear();
        metricName = val = ts = null;
        reset();
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append(metricName);
        if (i > 0) {
            sb.append("{");
            for (int j = 0; j < i; j++) {
                sb.append(labelNames.get(j)).append("=\"").append(labelValues.get(j)).append("\",");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append("}");
        }
        sb.append(" ").append(val);
        if (hasTs) {
            sb.append(ts);
        }
        sb.append("\n");
        return sb.toString();
    }
}

public class PromParser {
    private static final PromLexer scanner = new PromLexer(null);
    private static final Sample sample = new Sample(20);

    public static void parse(String str, StringBuilder parsed) {
        java.io.Reader in = new StringReader(str);
        try (in) {
            scanner.yyreset(in);
            sample.clear();
            boolean err = false;
            TOKEN tk;
            String mname;
            do {
                switch (scanner.Lex()) {
                    case Invalid, EOF -> err = true;
                    case Linebreak -> {}
                    case MName -> {
                        mname = scanner.yytext();
                        tk = scanner.Lex();
                        if (tk == TOKEN.BraceOpen) {
                            err = parseLabelValues();
                            tk = scanner.Lex();
                        }
                        if (tk != TOKEN.Value) {
                            err = true;
                            break;
                        }
                        sample.val = scanner.yytext();
                        switch (scanner.Lex()) {
                            case Linebreak, EOF -> {
                            }
                            case Timestamp -> {
                                sample.hasTs = true;
                                sample.ts = scanner.yytext();
                            }
                            default -> err=true;
                        }
                        if(!err) {
                            sample.metricName = mname;
                            parsed.append(sample.print());
                            sample.reset();
                        }
                    }
                }
            } while (!err && !scanner.yyatEOF());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean parseLabelValues() {
        try {
            TOKEN tk;
            while (true) {
                tk = scanner.Lex();
                if (tk == TOKEN.BraceClose)
                    return false;
                else if (tk == TOKEN.LName) {
                    sample.addln(scanner.yytext());
                    if (scanner.Lex() != TOKEN.Equal) {
                        return true;
                    }
                    if (scanner.Lex() != TOKEN.LValue)
                        return true;
                    else {
                        sample.addlv(scanner.yylv());
                    }
                } else {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
