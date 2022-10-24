// $ANTLR 3.5.2 /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g 2016-02-22 07:07:28


/*
--------------------------------------------------------------------------
Copyright 2021 Adventium Enterprises, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--------------------------------------------------------------------------
*/

package com.adventium.design.exploration.antlr3generated;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class MakeTypeToImplementationMapLexer extends Lexer {
	public static final int EOF=-1;
	public static final int T__6=6;
	public static final int T__7=7;
	public static final int T__8=8;
	public static final int ID=4;
	public static final int WS=5;

	// delegates
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public MakeTypeToImplementationMapLexer() {} 
	public MakeTypeToImplementationMapLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public MakeTypeToImplementationMapLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "/Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g"; }

	// $ANTLR start "T__6"
	public final void mT__6() throws RecognitionException {
		try {
			int _type = T__6;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:34:6: ( '.' )
			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:34:8: '.'
			{
			match('.'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__6"

	// $ANTLR start "T__7"
	public final void mT__7() throws RecognitionException {
		try {
			int _type = T__7;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:35:6: ( ':' )
			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:35:8: ':'
			{
			match(':'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__7"

	// $ANTLR start "T__8"
	public final void mT__8() throws RecognitionException {
		try {
			int _type = T__8;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:36:6: ( '=>' )
			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:36:8: '=>'
			{
			match("=>"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__8"

	// $ANTLR start "ID"
	public final void mID() throws RecognitionException {
		try {
			int _type = ID;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:69:4: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* )
			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:69:7: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
			{
			if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:70:3: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( ((LA1_0 >= '0' && LA1_0 <= '9')||(LA1_0 >= 'A' && LA1_0 <= 'Z')||LA1_0=='_'||(LA1_0 >= 'a' && LA1_0 <= 'z')) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop1;
				}
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ID"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:76:3: ( ( ' ' | '\\t' | '\\f' | '\\r' | '\\n' )+ )
			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:77:3: ( ' ' | '\\t' | '\\f' | '\\r' | '\\n' )+
			{
			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:77:3: ( ' ' | '\\t' | '\\f' | '\\r' | '\\n' )+
			int cnt2=0;
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( ((LA2_0 >= '\t' && LA2_0 <= '\n')||(LA2_0 >= '\f' && LA2_0 <= '\r')||LA2_0==' ') ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt2 >= 1 ) break loop2;
					EarlyExitException eee = new EarlyExitException(2, input);
					throw eee;
				}
				cnt2++;
			}

			 _channel=HIDDEN; 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WS"

	@Override
	public void mTokens() throws RecognitionException {
		// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:1:8: ( T__6 | T__7 | T__8 | ID | WS )
		int alt3=5;
		switch ( input.LA(1) ) {
		case '.':
			{
			alt3=1;
			}
			break;
		case ':':
			{
			alt3=2;
			}
			break;
		case '=':
			{
			alt3=3;
			}
			break;
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'G':
		case 'H':
		case 'I':
		case 'J':
		case 'K':
		case 'L':
		case 'M':
		case 'N':
		case 'O':
		case 'P':
		case 'Q':
		case 'R':
		case 'S':
		case 'T':
		case 'U':
		case 'V':
		case 'W':
		case 'X':
		case 'Y':
		case 'Z':
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'g':
		case 'h':
		case 'i':
		case 'j':
		case 'k':
		case 'l':
		case 'm':
		case 'n':
		case 'o':
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 't':
		case 'u':
		case 'v':
		case 'w':
		case 'x':
		case 'y':
		case 'z':
			{
			alt3=4;
			}
			break;
		case '\t':
		case '\n':
		case '\f':
		case '\r':
		case ' ':
			{
			alt3=5;
			}
			break;
		default:
			NoViableAltException nvae =
				new NoViableAltException("", 3, 0, input);
			throw nvae;
		}
		switch (alt3) {
			case 1 :
				// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:1:10: T__6
				{
				mT__6(); 

				}
				break;
			case 2 :
				// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:1:15: T__7
				{
				mT__7(); 

				}
				break;
			case 3 :
				// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:1:20: T__8
				{
				mT__8(); 

				}
				break;
			case 4 :
				// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:1:25: ID
				{
				mID(); 

				}
				break;
			case 5 :
				// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:1:28: WS
				{
				mWS(); 

				}
				break;

		}
	}



}
