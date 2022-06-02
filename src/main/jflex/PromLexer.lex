package promparse;

/**
 * A lexer for prometheus metrics
 * @author Sean
 */

/* parsed token */
enum TOKEN {
    Invalid,
    EOF,
    Linebreak,
    MName,
    BraceOpen,
    BraceClose,
    LName,
    LValue,
    Equal,
    Timestamp,
    Value
}

%%
%{
    public int line() { return yyline; }
%}
%final
%public
%unicode
%class PromLexer
%function Lex
%type TOKEN
%state sLabels,sLValue,sValue,sTimestamp
%line
%eofclose
%eofval{
return TOKEN.EOF;
%eofval}

D=[0-9]
L=[a-zA-Z_]
M=[a-zA-Z_:]
C=[^\n\r]
LB=\r\n|\n|\r

%%
\0                  { return TOKEN.EOF; }
{LB}                { yybegin(YYINITIAL); return TOKEN.Linebreak; }
[ \t]+              { /* ignore white space */ }
<YYINITIAL> {
    #{C}*           { /* ignore lines begin with # */}
    {M}({M}|{D})*   { yybegin(sValue); return TOKEN.MName; }
}
<sValue> {
    "{"             { yybegin(sLabels); return TOKEN.BraceOpen; }
    [^{ \t\n\r]+    { yybegin(sTimestamp); return TOKEN.Value; }
}
<sLabels> {
    {L}({L}|{D})*   { return TOKEN.LName; }
    "}"             { yybegin(sValue); return TOKEN.BraceClose; }
    "="             { yybegin(sLValue); return TOKEN.Equal; }
    ","             { /* ignore commas between labels */ }
}
<sLValue> \"(\\.|[^\\\"])*\" { yybegin(sLabels); return TOKEN.LValue; }
<sTimestamp> {
    {D}+            { return TOKEN.Timestamp; }
    {LB}            { yybegin(YYINITIAL); return TOKEN.Linebreak; }
}
/* default rule */
[^]                 { return TOKEN.Invalid; }