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
import java.util.HashMap;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class MakeTypeToImplementationMapParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "WS", "'.'", "':'", "'=>'"
	};
	public static final int EOF=-1;
	public static final int T__6=6;
	public static final int T__7=7;
	public static final int T__8=8;
	public static final int ID=4;
	public static final int WS=5;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public MakeTypeToImplementationMapParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public MakeTypeToImplementationMapParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override public String[] getTokenNames() { return MakeTypeToImplementationMapParser.tokenNames; }
	@Override public String getGrammarFileName() { return "/Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g"; }


	public static HashMap<String,String> typeToImplementation = new HashMap<String,String>();
	public static String rootSystemImplementation = "";



	// $ANTLR start "makemap"
	// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:82:1: makemap : root ( pair )+ ;
	public final void makemap() throws RecognitionException {
		try {
			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:83:3: ( root ( pair )+ )
			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:84:3: root ( pair )+
			{
			pushFollow(FOLLOW_root_in_makemap162);
			root();
			state._fsp--;

			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:85:3: ( pair )+
			int cnt1=0;
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==ID) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:85:5: pair
					{
					pushFollow(FOLLOW_pair_in_makemap168);
					pair();
					state._fsp--;

					}
					break;

				default :
					if ( cnt1 >= 1 ) break loop1;
					EarlyExitException eee = new EarlyExitException(1, input);
					throw eee;
				}
				cnt1++;
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "makemap"



	// $ANTLR start "root"
	// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:88:1: root : sys= ID '.' imp= ID ;
	public final void root() throws RecognitionException {
		Token sys=null;
		Token imp=null;

		try {
			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:89:3: (sys= ID '.' imp= ID )
			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:90:3: sys= ID '.' imp= ID
			{
			sys=(Token)match(input,ID,FOLLOW_ID_in_root190); 
			match(input,6,FOLLOW_6_in_root192); 
			imp=(Token)match(input,ID,FOLLOW_ID_in_root196); 
			rootSystemImplementation=(sys!=null?sys.getText():null)+"."+(imp!=null?imp.getText():null);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "root"



	// $ANTLR start "pair"
	// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:94:1: pair : sub= ID ':' typ= ID '=>' t2= ID '.' impl= ID ;
	public final void pair() throws RecognitionException {
		Token sub=null;
		Token typ=null;
		Token t2=null;
		Token impl=null;

		try {
			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:95:3: (sub= ID ':' typ= ID '=>' t2= ID '.' impl= ID )
			// /Users/brl/Work/Galileo workspace/com.adventium.design.exploration/grammars/MakeTypeToImplementationMap.g:96:3: sub= ID ':' typ= ID '=>' t2= ID '.' impl= ID
			{
			sub=(Token)match(input,ID,FOLLOW_ID_in_pair221); 
			match(input,7,FOLLOW_7_in_pair223); 
			typ=(Token)match(input,ID,FOLLOW_ID_in_pair227); 
			match(input,8,FOLLOW_8_in_pair229); 
			t2=(Token)match(input,ID,FOLLOW_ID_in_pair233); 
			match(input,6,FOLLOW_6_in_pair235); 
			impl=(Token)match(input,ID,FOLLOW_ID_in_pair239); 
			typeToImplementation.put((sub!=null?sub.getText():null)+":"+(typ!=null?typ.getText():null),(t2!=null?t2.getText():null)+"."+(impl!=null?impl.getText():null));
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "pair"

	// Delegated rules



	public static final BitSet FOLLOW_root_in_makemap162 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_pair_in_makemap168 = new BitSet(new long[]{0x0000000000000012L});
	public static final BitSet FOLLOW_ID_in_root190 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_6_in_root192 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_ID_in_root196 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_pair221 = new BitSet(new long[]{0x0000000000000080L});
	public static final BitSet FOLLOW_7_in_pair223 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_ID_in_pair227 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_8_in_pair229 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_ID_in_pair233 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_6_in_pair235 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_ID_in_pair239 = new BitSet(new long[]{0x0000000000000002L});
}
