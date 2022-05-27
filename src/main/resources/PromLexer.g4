lexer grammar PromLexer;

VALUE: [0-9]+ ('.' [0-9]+)?;
STRING
    : '\'' (~('\'' | '\\') | '\\' .)* '\''
    | '"' (~('"' | '\\') | '\\' .)* '"'
    ;

EQ:  '=';
LEFT_BRACE:  '{';
RIGHT_BRACE: '}';
COMMA: ',';

METRIC_NAME: [a-zA-Z_:] [a-zA-Z0-9_:]*;
LABEL_NAME:  [a-zA-Z_] [a-zA-Z0-9_]*;

WS: [\r\t\n ]+ -> skip;