package promparse;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sean
 * @date 2022/6/7 16:00
 */
class PromParserTest {
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
            many_label_pairs {country = "PRC", province = "GD", city="GZ", zipcode="510000" } 100
            some:aggregate:rate5m{a_b="c
            "}\t1
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
    private static final String refText= """
            go_gc_duration_seconds{quantile="0"} 4.9351e-05
            go_gc_duration_seconds{quantile="0.25"} 7.424100000000001e-05
            go_gc_duration_seconds{quantile="0.5",a="b"} 8.3835e-05
            go_gc_duration_seconds{quantile="0.8",a="b"} 8.3835e-05
            go_gc_duration_seconds{quantile="0.9",a="b"} 8.3835e-05
            wind_speed{A="2",c="3"} 12345
            go_gc_duration_seconds{quantile="1.0",a="b"} 8.3835e-05
            go_gc_duration_seconds{quantile="1.0",a="b"} 8.3835e-05
            go_gc_duration_seconds{quantile="1.0",a="b"} 8.3835e-05
            go_gc_duration_seconds{quantile="1.0",a="b"} 8.3835e-05
            go_gc_duration_seconds_count 99
            many_label_pairs{country="PRC",province="GD",city="GZ",zipcode="510000"} 100
            some:aggregate:rate5m{a_b="c
            "} 1
            go_goroutines 33123123
            _metric_starting_with_underscore 1
            testmetric{_label_starting_with_underscore="foo"} 1
            testmetric{label="\\"bar\\""} 1
            null_byte_metric{a="abc "} 1
            null_byte_metric{a="abc\\x00"} 1\n""";
    @Test
    public void checkParser() {
        StringBuilder sb = new StringBuilder();
        PromParser.parse(text, sb);
        assertEquals(refText,sb.toString());
    }
}