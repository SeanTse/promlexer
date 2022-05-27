parser grammar PromParser;

options { tokenVocab = PromLexer; }

literal: NUMBER | STRING;
