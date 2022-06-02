package promparse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.StringReader;


/**
 * @author Sean
 * @date 2022/6/2 10:46
 */
final class PromLexerTest {
    static PromLexer scanner;

    private final static String text = """
            # HELP go_gc_duration_seconds A summary of the GC invocation durations.
            # \tTYPE go_gc_duration_seconds summary
            go_gc_duration_seconds{quantile="0"} 4.9351e-05
            go_gc_duration_seconds{quantile="0.25",} 7.424100000000001e-05
            go_gc_duration_seconds{quantile="0.5",a="b"} 8.3835e-05
            go_gc_duration_seconds{quantile="0.8", a="b"} 8.3835e-05
            go_gc_duration_seconds{ quantile="0.9", a="b"} 8.3835e-05
            # Hrandom comment starting with prefix of HELP
            #
            wind_speed{A="2",c="3"} 12345
            # comment with escaped \\n newline
            # comment with escaped \\ escape character
            # HELP nohelp1
            # HELP nohelp2
            go_gc_duration_seconds{ quantile="1.0", a="b" } 8.3835e-05
            go_gc_duration_seconds { quantile="1.0", a="b" } 8.3835e-05
            go_gc_duration_seconds { quantile= "1.0", a= "b", } 8.3835e-05
            go_gc_duration_seconds { quantile = "1.0", a = "b" } 8.3835e-05
            go_gc_duration_seconds_count 99
            some:aggregate:rate5m{a_b="c"}\t1
            # HELP go_goroutines Number of goroutines that currently exist.
            # TYPE go_goroutines gauge
            go_goroutines 33  \t123123
            _metric_starting_with_underscore 1
            testmetric{_label_starting_with_underscore="foo"} 1
            testmetric{label="\\"bar\\""} 1

            # HELP metric foo bar

            null_byte_metric{a="abc "} 1
            # HELP metric foo\\x00bar
            null_byte_metric{a="abc\\x00"} 1""";
    private static final TOKEN[] expectedTokens = {TOKEN.Linebreak, TOKEN.Linebreak, TOKEN.MName, TOKEN.BraceOpen, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.BraceClose, TOKEN.Value, TOKEN.Linebreak, TOKEN.MName, TOKEN.BraceOpen, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.BraceClose, TOKEN.Value, TOKEN.Linebreak, TOKEN.MName, TOKEN.BraceOpen, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.BraceClose, TOKEN.Value, TOKEN.Linebreak, TOKEN.MName, TOKEN.BraceOpen, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.BraceClose, TOKEN.Value, TOKEN.Linebreak, TOKEN.MName, TOKEN.BraceOpen, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.BraceClose, TOKEN.Value, TOKEN.Linebreak, TOKEN.Linebreak, TOKEN.Linebreak, TOKEN.MName, TOKEN.BraceOpen, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.BraceClose, TOKEN.Value, TOKEN.Linebreak, TOKEN.Linebreak, TOKEN.Linebreak, TOKEN.Linebreak, TOKEN.Linebreak, TOKEN.MName, TOKEN.BraceOpen, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.BraceClose, TOKEN.Value, TOKEN.Linebreak, TOKEN.MName, TOKEN.BraceOpen, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.BraceClose, TOKEN.Value, TOKEN.Linebreak, TOKEN.MName, TOKEN.BraceOpen, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.BraceClose, TOKEN.Value, TOKEN.Linebreak, TOKEN.MName, TOKEN.BraceOpen, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.BraceClose, TOKEN.Value, TOKEN.Linebreak, TOKEN.MName, TOKEN.Value, TOKEN.Linebreak, TOKEN.MName, TOKEN.BraceOpen, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.BraceClose, TOKEN.Value, TOKEN.Linebreak, TOKEN.Linebreak, TOKEN.Linebreak, TOKEN.MName, TOKEN.Value, TOKEN.Timestamp, TOKEN.Linebreak, TOKEN.MName, TOKEN.Value, TOKEN.Linebreak, TOKEN.MName, TOKEN.BraceOpen, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.BraceClose, TOKEN.Value, TOKEN.Linebreak, TOKEN.MName, TOKEN.BraceOpen, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.BraceClose, TOKEN.Value, TOKEN.Linebreak, TOKEN.Linebreak, TOKEN.Linebreak, TOKEN.Linebreak, TOKEN.MName, TOKEN.BraceOpen, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.BraceClose, TOKEN.Value, TOKEN.Linebreak, TOKEN.Linebreak, TOKEN.MName, TOKEN.BraceOpen, TOKEN.LName, TOKEN.Equal, TOKEN.LValue, TOKEN.BraceClose, TOKEN.Value, TOKEN.EOF};
    private static final String[] expectedText = {"go_gc_duration_seconds", "{", "quantile", "=", "\"0\"", "}", "4.9351e-05", "go_gc_duration_seconds", "{", "quantile", "=", "\"0.25\"", "}", "7.424100000000001e-05", "go_gc_duration_seconds", "{", "quantile", "=", "\"0.5\"", "a", "=", "\"b\"", "}", "8.3835e-05", "go_gc_duration_seconds", "{", "quantile", "=", "\"0.8\"", "a", "=", "\"b\"", "}", "8.3835e-05", "go_gc_duration_seconds", "{", "quantile", "=", "\"0.9\"", "a", "=", "\"b\"", "}", "8.3835e-05", "wind_speed", "{", "A", "=", "\"2\"", "c", "=", "\"3\"", "}", "12345", "go_gc_duration_seconds", "{", "quantile", "=", "\"1.0\"", "a", "=", "\"b\"", "}", "8.3835e-05", "go_gc_duration_seconds", "{", "quantile", "=", "\"1.0\"", "a", "=", "\"b\"", "}", "8.3835e-05", "go_gc_duration_seconds", "{", "quantile", "=", "\"1.0\"", "a", "=", "\"b\"", "}", "8.3835e-05", "go_gc_duration_seconds", "{", "quantile", "=", "\"1.0\"", "a", "=", "\"b\"", "}", "8.3835e-05", "go_gc_duration_seconds_count", "99", "some:aggregate:rate5m", "{", "a_b", "=", "\"c\"", "}", "1", "go_goroutines", "33", "123123", "_metric_starting_with_underscore", "1", "testmetric", "{", "_label_starting_with_underscore", "=", "\"foo\"", "}", "1", "testmetric", "{", "label", "=", "\"\\\"bar\\\"\"", "}", "1", "null_byte_metric", "{", "a", "=", "\"abc \"", "}", "1", "null_byte_metric", "{", "a", "=", "\"abc\\x00\"", "}", "1"};

    @BeforeAll
    public static void setScanner() {
        java.io.StringReader reader = new StringReader(text);
        scanner = new PromLexer(reader);
    }

    @Test
    public void checkTokens() {
        /* token count */
        int i = 0;
        /* line count */
//        int line = 0;
        /* text count */
        int t = 0;
        try {
            do {
                TOKEN tk = scanner.Lex();
//                line = scanner.line();
                Assertions.assertEquals(expectedTokens[i], tk);
                ++i;
                if (tk != TOKEN.Linebreak && tk != TOKEN.EOF && tk != TOKEN.Invalid) {
                    String txt = scanner.yytext();
                    Assertions.assertEquals(expectedText[t], txt);
                    t++;
                }
            } while (!scanner.yyatEOF());
        } catch (Exception e) {
//            System.err.println("ERROR lexing line #" + line);
            System.out.println("Unexpected exception:");
            e.printStackTrace();
        }
    }

}