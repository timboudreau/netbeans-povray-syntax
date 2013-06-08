/* 
 * The MIT License
 *
 * Copyright 2013 Tim Boudreau.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.netbeans.modules.povray.file.parsing;

import java.util.Arrays;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 *
 * @author Tim Boudreau
 */
public class PovLexer implements Lexer<PovTokenId> {

    private final LexerRestartInfo<PovTokenId> info;

    PovLexer(LexerRestartInfo<PovTokenId> info) {
        this.info = info;
    }

    static class CSet {

        private final char[] chars;

        CSet(char... chars) {
            this.chars = chars;
            Arrays.sort(chars);
        }

        boolean contains(char c) {
            return Arrays.binarySearch(chars, c) >= 0;
        }
    }

    private enum In {

        none,
        block_open,
        block_close,
        comment,
        line_comment,
        argument_list,
        string,
        vector,
        operator,
        number,
        pound,
        whitespace,
        word,
        other;
        private final CSet punc = new CSet('.', ',', '<', '>', '#', '(', ')', '{', '}', ';', ':', /* '/' , */ '*', '+', '=');
        private final CSet operators = new CSet( /* '/', */'*', '+', '=');

        boolean isEndInclusive() {
            return this == comment || this == vector || this == string || this == argument_list;
        }

        boolean isStartSequence(char curr, char prev, In alreadyIn, LexerInput in) {
            if (alreadyIn == comment || (alreadyIn == line_comment && curr != '\n') || (alreadyIn == string && curr != '"') || (alreadyIn == argument_list)) {
                return false;
            }
            switch (this) {
                case none:
                    return false;
                case comment:
                    boolean result = prev == '/' && curr == '*';
                    if (!result && curr == '/') {
                        char c = (char) in.read();
                        try {
                            result = c == '*';
                        } finally {
                            in.backup(1);
                        }
                    }
                    return result;
                case line_comment:
                    boolean res = curr == '/' && prev == '/';
                    if (!res && curr == '/') {
                        char c = (char) in.read();
                        try {
                            res = c == '/';
                        } finally {
                            in.backup(1);
                        }
                    }
                    return res;
                case argument_list :
                    return curr == '(';
                case whitespace:
                    return Character.isWhitespace(curr) || curr == ';' || curr == ','; //?XXX
                case vector:
                    return curr == '<';
                case pound:
                    return curr == '#';
                case block_open:
                    return curr == '{';
                case block_close:
                    return curr == '}';
                case string:
                    return alreadyIn != string && curr == '"';
                case operator:
                    return operators.contains(curr);
                case word:
                    return !Character.isWhitespace(curr) && !punc.contains(curr);
                case number:
                    return Character.isDigit(curr);
            }
            return false;
        }

        boolean isEndSequence(char curr, char prev) {
            switch (this) {
                case none:
                    return true;
                case whitespace:
                    return !Character.isWhitespace(curr);
                case argument_list :
                    return curr == ')';
                case comment:
                    return prev == '*' && curr == '/';
                case line_comment:
                    return curr == '\n';
                case vector:
                    return curr == '>';
                case pound:
                    return Character.isWhitespace(curr);
                case block_open:
                case block_close:
                case operator:
                    return true;
                case string:
                    return curr == '"';
                case word:
                    return Character.isWhitespace(curr) || punc.contains(curr) || operators.contains(curr);
                case number:
                    return curr != '.' && !Character.isDigit(curr);
                case other:
                    return true;
            }
            return false;
        }

        static In findStart(char curr, char prev, In what, LexerInput in) {
            for (In i : values()) {
                if (i.isStartSequence(curr, prev, what, in)) {
                    return i;
                }
            }
            return null;
        }

        PovTokenId toId(StringBuilder sb) {
            switch (this) {
                case comment:
                case number:
                case operator:
                case pound:
                case vector:
                case whitespace:
                case block_close:
                case string:
                case argument_list :
                case block_open:
                    return PovTokenId.valueOf(name());
                case line_comment:
                    return PovTokenId.comment;
                case none:
                    return PovTokenId.valueOf(whitespace.name());
                case word:
                    String s = sb.toString();
                    Keywords k = Keywords.match(s);
                    if (k == null) {
                        char[] c = s.toCharArray();
                        boolean dig = true;
                        for (int i = 0; i < c.length; i++) {
                            dig = Character.isDigit(c[i]);
                            if (!dig) {
                                break;
                            }
                        }
                        return dig ? PovTokenId.number : PovTokenId.identifier;
                    } else {
                        return PovTokenId.keyword;
                    }
                case other:
                default:
                    return PovTokenId.other;
            }
        }
    }

    @Override
    public Token<PovTokenId> nextToken() {
        TokenFactory<PovTokenId> f = info.tokenFactory();
        LexerInput input = info.input();
        StringBuilder sb = new StringBuilder();

        char pc = 0;
        In in = In.none;

        for (;;) {
            char c = (char) input.read();
            if (c == -1 || c == 65535) { //65535 huh?
                break;
            }
            sb.append(c);
            if (in == In.none) {
                in = In.findStart(c, pc, in, input);
                if (in == null) {
//                    new Error("No match for '" + pc + c + "'").printStackTrace();
                    in = In.other;
                }
            } else {
                if (in.isEndSequence(c, pc)) {
                    if (!in.isEndInclusive()) {
                        input.backup(1);
                        sb.setLength(sb.length() - 1);
                    }
                    break;
                }
            }
            pc = c;
        }
        if (sb.length() == 0) {
            return null;
        }
        return f.getFlyweightToken(in.toId(sb), sb.toString());
    }

    /*
     @Override
     public Token<PovTokenId> nextToken() {
     TokenFactory<PovTokenId> f = info.tokenFactory();
     LexerInput in = info.input();
     StringBuilder sb = new StringBuilder();
     boolean inWhitespace = true;
     boolean allNum = false;
     boolean allWs = true;
     boolean allPunc = false;
     boolean inComment = false;
     char pc = 0;
     for(;;) {
     int i = in.read();
     if (i == -1) {
     break;
     }
     char c = (char) i;
     if (inComment && pc == '*' && c == '/') {
     inComment = false;
     //                return f.getFlyweightToken(PovTokenId.comment, sb.toString());
     }
     if (!inComment && pc == '/' && c == '*') {
     inComment = true;
     }
     boolean ws = Character.isWhitespace(c);
     allWs &= ws;
     if (ws != inWhitespace) {
     if (ws) {
     break;
     }
     }
     if (!ws) {
     sb.append(c);
     allNum &= Character.isDigit(c);
     allPunc &= !Character.isDigit(c) && !Character.isAlphabetic(c);
     }
     pc = c;
     }
     PovTokenId id;
     if (allWs) {
     id = PovTokenId.whitespace;
     } else if (allPunc) {
     id = PovTokenId.punctuation;
     } else if (allNum) {
     id = PovTokenId.numeric_literal;
     } else {
     id = PovTokenId.find(sb.toString());
     }
     return f.getFlyweightToken(id, sb.toString());
     }
     */
    @Override
    public Object state() {
        return null;
    }

    @Override
    public void release() {
        //do nothing
    }

    static enum States {

        NONE,
        IN_COMMENT,
    }
}
