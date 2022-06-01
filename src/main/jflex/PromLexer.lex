package promparse;

enum TOKEN {
    tInvalid,
    tEOF,
    tLinebreak,
    tMName,
    tBraceOpen,
    tBraceClose,
    tLName,
    tLValue,
    tComma,
    tEqual,
    tTimestamp,
    tValue
}

%%

%final
%public
%unicode
%class PromLexer
%function Lex
%type TOKEN
%state sInit,sLabels,sLValue,sValue,sTimestamp
%char
%line
%column
%debug

D=[0-9]
L=[a-zA-Z_]
M=[a-zA-Z_:]
C=[^\n\r]
LB=\r\n|\n|\r

%%
\0                  { return TOKEN.tEOF; }
{LB}                { yybegin(YYINITIAL); return TOKEN.tLinebreak; }
[ \t]+              { /* ignore white space */ }
<YYINITIAL> {
    #{C}*           { /* ignore lines begin with # */}
    {M}({M}|{D})*   { yybegin(sValue); return TOKEN.tMName; }
}
<sValue> {
    "{"             { yybegin(sLabels); return TOKEN.tBraceOpen; }
    [^{ \t\n\r]+      { yybegin(sTimestamp); return TOKEN.tValue; }
}
<sLabels> {
    {L}({L}|{D})*   { return TOKEN.tLName; }
    "}"             { yybegin(sValue); return TOKEN.tBraceClose; }
    "="             { yybegin(sLValue); return TOKEN.tEqual; }
    ","             { return TOKEN.tComma; }
}
<sLValue> \"(\\.|[^\\\"])*\" { yybegin(sLabels); return TOKEN.tLValue; }
<sTimestamp> {
    {D}+            { return TOKEN.tTimestamp; }
    {LB}            { yybegin(sInit); return TOKEN.tLinebreak; }
}
/* default rule */
.                 { return TOKEN.tInvalid; }