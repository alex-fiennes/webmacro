/* Generated By:JavaCC: Do not edit this line. WMParser_implTokenManager.java */
package org.webmacro.parser;
import java.util.*;
import java.io.*;
import org.webmacro.*;
import org.webmacro.engine.*;
import org.webmacro.directive.*;
import org.webmacro.directive.Directive;
import org.webmacro.directive.DirectiveProvider;
import org.webmacro.directive.DirectiveBuilder;
import org.webmacro.directive.Directive.ArgDescriptor;
import org.webmacro.directive.Directive.OptionChoice;
import org.webmacro.directive.Directive.Subdirective;

public class WMParser_implTokenManager implements WMParser_implConstants
{
  // Required by SetState
  void backup(int n) { input_stream.backup(n); }
private final int jjStopStringLiteralDfa_1(int pos, long active0)
{
   switch (pos)
   {
      default :
         return -1;
   }
}
private final int jjStartNfa_1(int pos, long active0)
{
   return jjMoveNfa_1(jjStopStringLiteralDfa_1(pos, active0), pos + 1);
}
private final int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private final int jjStartNfaWithStates_1(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_1(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_1()
{
   switch(curChar)
   {
      case 34:
         return jjStopAtPos(0, 24);
      case 36:
         return jjStopAtPos(0, 20);
      case 92:
         return jjStartNfaWithStates_1(0, 22, 1);
      default :
         return jjMoveNfa_1(0, 0);
   }
}
private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private final int jjMoveNfa_1(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 5;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
               case 4:
                  if ((0xffffffebffffdbffL & l) == 0L)
                     break;
                  if (kind > 55)
                     kind = 55;
                  jjCheckNAdd(4);
                  break;
               case 1:
                  if ((0xfc00ffffffffffffL & l) != 0L)
                  {
                     if (kind > 21)
                        kind = 21;
                  }
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               case 2:
                  if (curChar == 10 && kind > 21)
                     kind = 21;
                  break;
               case 3:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0xffffffffefffffffL & l) != 0L)
                  {
                     if (kind > 55)
                        kind = 55;
                     jjCheckNAdd(4);
                  }
                  else if (curChar == 92)
                     jjAddStates(0, 1);
                  break;
               case 1:
                  if ((0xf8000001f8000001L & l) != 0L && kind > 21)
                     kind = 21;
                  break;
               case 4:
                  if ((0xffffffffefffffffL & l) == 0L)
                     break;
                  if (kind > 55)
                     kind = 55;
                  jjCheckNAdd(4);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
               case 4:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 55)
                     kind = 55;
                  jjCheckNAdd(4);
                  break;
               case 1:
                  if ((jjbitVec0[i2] & l2) != 0L && kind > 21)
                     kind = 21;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 5 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjStopStringLiteralDfa_3(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x800000L) != 0L)
            return 0;
         if ((active0 & 0x1c000000L) != 0L)
         {
            jjmatchedKind = 52;
            return 29;
         }
         if ((active0 & 0x3000000000L) != 0L)
            return 13;
         if ((active0 & 0x400000L) != 0L)
            return 4;
         return -1;
      case 1:
         if ((active0 & 0x1c000000L) != 0L)
         {
            jjmatchedKind = 52;
            jjmatchedPos = 1;
            return 29;
         }
         return -1;
      case 2:
         if ((active0 & 0x1c000000L) != 0L)
         {
            jjmatchedKind = 52;
            jjmatchedPos = 2;
            return 29;
         }
         return -1;
      case 3:
         if ((active0 & 0xc000000L) != 0L)
            return 29;
         if ((active0 & 0x10000000L) != 0L)
         {
            jjmatchedKind = 52;
            jjmatchedPos = 3;
            return 29;
         }
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_3(int pos, long active0)
{
   return jjMoveNfa_3(jjStopStringLiteralDfa_3(pos, active0), pos + 1);
}
private final int jjStartNfaWithStates_3(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_3(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_3()
{
   switch(curChar)
   {
      case 34:
         return jjStopAtPos(0, 24);
      case 35:
         return jjStartNfaWithStates_3(0, 23, 0);
      case 36:
         return jjStopAtPos(0, 20);
      case 39:
         return jjStopAtPos(0, 25);
      case 40:
         return jjStopAtPos(0, 31);
      case 41:
         return jjStopAtPos(0, 32);
      case 42:
         return jjStopAtPos(0, 45);
      case 43:
         return jjStopAtPos(0, 43);
      case 44:
         return jjStopAtPos(0, 50);
      case 45:
         return jjStopAtPos(0, 44);
      case 46:
         return jjStopAtPos(0, 35);
      case 47:
         return jjStopAtPos(0, 46);
      case 59:
         return jjStopAtPos(0, 51);
      case 60:
         jjmatchedKind = 36;
         return jjMoveStringLiteralDfa1_3(0x2000000000L);
      case 61:
         jjmatchedKind = 41;
         return jjMoveStringLiteralDfa1_3(0x10000000000L);
      case 62:
         jjmatchedKind = 38;
         return jjMoveStringLiteralDfa1_3(0x8000000000L);
      case 91:
         return jjStopAtPos(0, 33);
      case 92:
         return jjStartNfaWithStates_3(0, 22, 4);
      case 93:
         return jjStopAtPos(0, 34);
      case 102:
         return jjMoveStringLiteralDfa1_3(0x10000000L);
      case 110:
         return jjMoveStringLiteralDfa1_3(0x4000000L);
      case 116:
         return jjMoveStringLiteralDfa1_3(0x8000000L);
      default :
         return jjMoveNfa_3(2, 0);
   }
}
private final int jjMoveStringLiteralDfa1_3(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_3(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 61:
         if ((active0 & 0x2000000000L) != 0L)
            return jjStopAtPos(1, 37);
         else if ((active0 & 0x8000000000L) != 0L)
            return jjStopAtPos(1, 39);
         else if ((active0 & 0x10000000000L) != 0L)
            return jjStopAtPos(1, 40);
         break;
      case 97:
         return jjMoveStringLiteralDfa2_3(active0, 0x10000000L);
      case 114:
         return jjMoveStringLiteralDfa2_3(active0, 0x8000000L);
      case 117:
         return jjMoveStringLiteralDfa2_3(active0, 0x4000000L);
      default :
         break;
   }
   return jjStartNfa_3(0, active0);
}
private final int jjMoveStringLiteralDfa2_3(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_3(0, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_3(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 108:
         return jjMoveStringLiteralDfa3_3(active0, 0x14000000L);
      case 117:
         return jjMoveStringLiteralDfa3_3(active0, 0x8000000L);
      default :
         break;
   }
   return jjStartNfa_3(1, active0);
}
private final int jjMoveStringLiteralDfa3_3(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_3(1, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_3(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 101:
         if ((active0 & 0x8000000L) != 0L)
            return jjStartNfaWithStates_3(3, 27, 29);
         break;
      case 108:
         if ((active0 & 0x4000000L) != 0L)
            return jjStartNfaWithStates_3(3, 26, 29);
         break;
      case 115:
         return jjMoveStringLiteralDfa4_3(active0, 0x10000000L);
      default :
         break;
   }
   return jjStartNfa_3(2, active0);
}
private final int jjMoveStringLiteralDfa4_3(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_3(2, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_3(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 101:
         if ((active0 & 0x10000000L) != 0L)
            return jjStartNfaWithStates_3(4, 28, 29);
         break;
      default :
         break;
   }
   return jjStartNfa_3(3, active0);
}
private final int jjMoveNfa_3(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 31;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 4:
                  if ((0xfc00ffffffffffffL & l) != 0L)
                  {
                     if (kind > 21)
                        kind = 21;
                  }
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 5;
                  break;
               case 2:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 53)
                        kind = 53;
                     jjCheckNAdd(30);
                  }
                  else if ((0x2400L & l) != 0L)
                  {
                     if (kind > 30)
                        kind = 30;
                  }
                  else if ((0x100000200L & l) != 0L)
                  {
                     if (kind > 29)
                        kind = 29;
                     jjCheckNAdd(7);
                  }
                  else if (curChar == 33)
                  {
                     if (kind > 49)
                        kind = 49;
                  }
                  else if (curChar == 38)
                     jjstateSet[jjnewStateCnt++] = 15;
                  else if (curChar == 60)
                     jjstateSet[jjnewStateCnt++] = 13;
                  else if (curChar == 35)
                     jjstateSet[jjnewStateCnt++] = 0;
                  if (curChar == 33)
                     jjstateSet[jjnewStateCnt++] = 11;
                  else if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 8;
                  break;
               case 0:
                  if (curChar != 35)
                     break;
                  if (kind > 16)
                     kind = 16;
                  jjCheckNAdd(1);
                  break;
               case 1:
                  if ((0xffffffffffffdbffL & l) == 0L)
                     break;
                  if (kind > 16)
                     kind = 16;
                  jjCheckNAdd(1);
                  break;
               case 5:
                  if (curChar == 10 && kind > 21)
                     kind = 21;
                  break;
               case 6:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 5;
                  break;
               case 7:
                  if ((0x100000200L & l) == 0L)
                     break;
                  if (kind > 29)
                     kind = 29;
                  jjCheckNAdd(7);
                  break;
               case 8:
                  if (curChar == 10 && kind > 30)
                     kind = 30;
                  break;
               case 9:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 8;
                  break;
               case 10:
                  if ((0x2400L & l) != 0L && kind > 30)
                     kind = 30;
                  break;
               case 11:
                  if (curChar == 61 && kind > 42)
                     kind = 42;
                  break;
               case 12:
                  if (curChar == 33)
                     jjstateSet[jjnewStateCnt++] = 11;
                  break;
               case 13:
                  if (curChar == 62 && kind > 42)
                     kind = 42;
                  break;
               case 14:
                  if (curChar == 60)
                     jjstateSet[jjnewStateCnt++] = 13;
                  break;
               case 15:
                  if (curChar == 38 && kind > 47)
                     kind = 47;
                  break;
               case 16:
                  if (curChar == 38)
                     jjstateSet[jjnewStateCnt++] = 15;
                  break;
               case 24:
                  if (curChar == 33 && kind > 49)
                     kind = 49;
                  break;
               case 29:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 52)
                     kind = 52;
                  jjstateSet[jjnewStateCnt++] = 29;
                  break;
               case 30:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 53)
                     kind = 53;
                  jjCheckNAdd(30);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 4:
                  if ((0xf8000001f8000001L & l) != 0L && kind > 21)
                     kind = 21;
                  break;
               case 2:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 52)
                        kind = 52;
                     jjCheckNAdd(29);
                  }
                  else if (curChar == 124)
                     jjstateSet[jjnewStateCnt++] = 20;
                  else if (curChar == 92)
                     jjAddStates(2, 3);
                  if (curChar == 78)
                     jjstateSet[jjnewStateCnt++] = 26;
                  else if (curChar == 79)
                     jjstateSet[jjnewStateCnt++] = 22;
                  else if (curChar == 65)
                     jjstateSet[jjnewStateCnt++] = 18;
                  break;
               case 1:
                  if (kind > 16)
                     kind = 16;
                  jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 3:
                  if (curChar == 92)
                     jjAddStates(2, 3);
                  break;
               case 17:
                  if (curChar == 68 && kind > 47)
                     kind = 47;
                  break;
               case 18:
                  if (curChar == 78)
                     jjstateSet[jjnewStateCnt++] = 17;
                  break;
               case 19:
                  if (curChar == 65)
                     jjstateSet[jjnewStateCnt++] = 18;
                  break;
               case 20:
                  if (curChar == 124 && kind > 48)
                     kind = 48;
                  break;
               case 21:
                  if (curChar == 124)
                     jjstateSet[jjnewStateCnt++] = 20;
                  break;
               case 22:
                  if (curChar == 82 && kind > 48)
                     kind = 48;
                  break;
               case 23:
                  if (curChar == 79)
                     jjstateSet[jjnewStateCnt++] = 22;
                  break;
               case 25:
                  if (curChar == 84 && kind > 49)
                     kind = 49;
                  break;
               case 26:
                  if (curChar == 79)
                     jjstateSet[jjnewStateCnt++] = 25;
                  break;
               case 27:
                  if (curChar == 78)
                     jjstateSet[jjnewStateCnt++] = 26;
                  break;
               case 28:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 52)
                     kind = 52;
                  jjCheckNAdd(29);
                  break;
               case 29:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 52)
                     kind = 52;
                  jjCheckNAdd(29);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 4:
                  if ((jjbitVec0[i2] & l2) != 0L && kind > 21)
                     kind = 21;
                  break;
               case 1:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 16)
                     kind = 16;
                  jjstateSet[jjnewStateCnt++] = 1;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 31 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private final int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 36:
         return jjStopAtPos(0, 20);
      case 39:
         return jjStopAtPos(0, 25);
      case 92:
         return jjStartNfaWithStates_0(0, 22, 1);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 5;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
               case 4:
                  if ((0xffffff6fffffdbffL & l) == 0L)
                     break;
                  if (kind > 56)
                     kind = 56;
                  jjCheckNAdd(4);
                  break;
               case 1:
                  if ((0xfc00ffffffffffffL & l) != 0L)
                  {
                     if (kind > 21)
                        kind = 21;
                  }
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               case 2:
                  if (curChar == 10 && kind > 21)
                     kind = 21;
                  break;
               case 3:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0xffffffffefffffffL & l) != 0L)
                  {
                     if (kind > 56)
                        kind = 56;
                     jjCheckNAdd(4);
                  }
                  else if (curChar == 92)
                     jjAddStates(0, 1);
                  break;
               case 1:
                  if ((0xf8000001f8000001L & l) != 0L && kind > 21)
                     kind = 21;
                  break;
               case 4:
                  if ((0xffffffffefffffffL & l) == 0L)
                     break;
                  if (kind > 56)
                     kind = 56;
                  jjCheckNAdd(4);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
               case 4:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 56)
                     kind = 56;
                  jjCheckNAdd(4);
                  break;
               case 1:
                  if ((jjbitVec0[i2] & l2) != 0L && kind > 21)
                     kind = 21;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 5 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjStopStringLiteralDfa_4(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x400000L) != 0L)
            return 2;
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_4(int pos, long active0)
{
   return jjMoveNfa_4(jjStopStringLiteralDfa_4(pos, active0), pos + 1);
}
private final int jjStartNfaWithStates_4(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_4(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_4()
{
   switch(curChar)
   {
      case 35:
         jjmatchedKind = 23;
         return jjMoveStringLiteralDfa1_4(0x23000L);
      case 36:
         return jjStopAtPos(0, 20);
      case 92:
         return jjStartNfaWithStates_4(0, 22, 2);
      case 123:
         return jjStopAtPos(0, 14);
      case 125:
         return jjStopAtPos(0, 15);
      default :
         return jjMoveNfa_4(1, 0);
   }
}
private final int jjMoveStringLiteralDfa1_4(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_4(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 35:
         if ((active0 & 0x20000L) != 0L)
            return jjStopAtPos(1, 17);
         break;
      case 98:
         return jjMoveStringLiteralDfa2_4(active0, 0x1000L);
      case 101:
         return jjMoveStringLiteralDfa2_4(active0, 0x2000L);
      default :
         break;
   }
   return jjStartNfa_4(0, active0);
}
private final int jjMoveStringLiteralDfa2_4(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_4(0, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_4(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa3_4(active0, 0x1000L);
      case 110:
         return jjMoveStringLiteralDfa3_4(active0, 0x2000L);
      default :
         break;
   }
   return jjStartNfa_4(1, active0);
}
private final int jjMoveStringLiteralDfa3_4(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_4(1, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_4(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 100:
         if ((active0 & 0x2000L) != 0L)
            return jjStopAtPos(3, 13);
         break;
      case 103:
         return jjMoveStringLiteralDfa4_4(active0, 0x1000L);
      default :
         break;
   }
   return jjStartNfa_4(2, active0);
}
private final int jjMoveStringLiteralDfa4_4(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_4(2, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_4(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 105:
         return jjMoveStringLiteralDfa5_4(active0, 0x1000L);
      default :
         break;
   }
   return jjStartNfa_4(3, active0);
}
private final int jjMoveStringLiteralDfa5_4(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_4(3, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_4(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 110:
         if ((active0 & 0x1000L) != 0L)
            return jjStopAtPos(5, 12);
         break;
      default :
         break;
   }
   return jjStartNfa_4(4, active0);
}
private final int jjMoveNfa_4(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 5;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 2:
                  if ((0xfc00ffffffffffffL & l) != 0L)
                  {
                     if (kind > 21)
                        kind = 21;
                  }
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 1:
               case 0:
                  if ((0xffffffe7ffffffffL & l) == 0L)
                     break;
                  if (kind > 11)
                     kind = 11;
                  jjCheckNAdd(0);
                  break;
               case 3:
                  if (curChar == 10 && kind > 21)
                     kind = 21;
                  break;
               case 4:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 2:
                  if ((0xf8000001f8000001L & l) != 0L && kind > 21)
                     kind = 21;
                  break;
               case 1:
                  if ((0xd7ffffffefffffffL & l) != 0L)
                  {
                     if (kind > 11)
                        kind = 11;
                     jjCheckNAdd(0);
                  }
                  else if (curChar == 92)
                     jjAddStates(4, 5);
                  break;
               case 0:
                  if ((0xd7ffffffefffffffL & l) == 0L)
                     break;
                  if (kind > 11)
                     kind = 11;
                  jjCheckNAdd(0);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 2:
                  if ((jjbitVec0[i2] & l2) != 0L && kind > 21)
                     kind = 21;
                  break;
               case 1:
               case 0:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 11)
                     kind = 11;
                  jjCheckNAdd(0);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 5 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjMoveStringLiteralDfa0_2()
{
   return jjMoveNfa_2(0, 0);
}
private final int jjMoveNfa_2(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 1;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0xffffffffffffdbffL & l) == 0L)
                     break;
                  kind = 18;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  kind = 18;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 18)
                     kind = 18;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 1 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   1, 3, 4, 6, 2, 4, 
};
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, null, null, null, 
"\43\142\145\147\151\156", "\43\145\156\144", "\173", "\175", null, "\43\43", null, null, "\44", null, 
"\134", "\43", "\42", "\47", "\156\165\154\154", "\164\162\165\145", 
"\146\141\154\163\145", null, null, "\50", "\51", "\133", "\135", "\56", "\74", "\74\75", "\76", 
"\76\75", "\75\75", "\75", null, "\53", "\55", "\52", "\57", null, null, null, "\54", 
"\73", null, null, null, null, null, };
public static final String[] lexStateNames = {
   "SQS", 
   "QS", 
   "COMMENT", 
   "WM", 
   "DEFAULT", 
};
public static final int[] jjnewLexState = {
   -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
   -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
   -1, -1, -1, -1, -1, -1, -1, 
};
static final long[] jjtoToken = {
   0x1fffffffffef801L, 
};
static final long[] jjtoSkip = {
   0x10000L, 
};
private CharStream input_stream;
private final int[] jjrounds = new int[31];
private final int[] jjstateSet = new int[62];
StringBuffer image;
int jjimageLen;
int lengthOfMatch;
protected char curChar;
public WMParser_implTokenManager(CharStream stream)
{
   input_stream = stream;
}
public WMParser_implTokenManager(CharStream stream, int lexState)
{
   this(stream);
   SwitchTo(lexState);
}
public void ReInit(CharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 31; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
public void ReInit(CharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
public void SwitchTo(int lexState)
{
   if (lexState >= 5 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

private final Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   if (jjmatchedPos < 0)
   {
      t.image = "";
      t.beginLine = t.endLine = input_stream.getBeginLine();
      t.beginColumn = t.endColumn = input_stream.getBeginColumn();
   }
   else
   {
      String im = jjstrLiteralImages[jjmatchedKind];
      t.image = (im == null) ? input_stream.GetImage() : im;
      t.beginLine = input_stream.getBeginLine();
      t.beginColumn = input_stream.getBeginColumn();
      t.endLine = input_stream.getEndLine();
      t.endColumn = input_stream.getEndColumn();
   }
   return t;
}

int curLexState = 4;
int defaultLexState = 4;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

public final Token getNextToken() 
{
  int kind;
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }
   image = null;
   jjimageLen = 0;

   switch(curLexState)
   {
     case 0:
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_0();
       break;
     case 1:
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_1();
       break;
     case 2:
       jjmatchedKind = 18;
       jjmatchedPos = -1;
       curPos = 0;
       curPos = jjMoveStringLiteralDfa0_2();
       if (jjmatchedPos < 0 || (jjmatchedPos == 0 && jjmatchedKind > 19))
       {
          jjmatchedKind = 19;
          jjmatchedPos = 0;
       }
       break;
     case 3:
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_3();
       if (jjmatchedPos == 0 && jjmatchedKind > 54)
       {
          jjmatchedKind = 54;
       }
       break;
     case 4:
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_4();
       break;
   }
     if (jjmatchedKind != 0x7fffffff)
     {
        if (jjmatchedPos + 1 < curPos)
           input_stream.backup(curPos - jjmatchedPos - 1);
        if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
        {
           matchedToken = jjFillToken();
           TokenLexicalActions(matchedToken);
       if (jjnewLexState[jjmatchedKind] != -1)
         curLexState = jjnewLexState[jjmatchedKind];
           return matchedToken;
        }
        else
        {
         if (jjnewLexState[jjmatchedKind] != -1)
           curLexState = jjnewLexState[jjmatchedKind];
           continue EOFLoop;
        }
     }
     int error_line = input_stream.getEndLine();
     int error_column = input_stream.getEndColumn();
     String error_after = null;
     boolean EOFSeen = false;
     try { input_stream.readChar(); input_stream.backup(1); }
     catch (java.io.IOException e1) {
        EOFSeen = true;
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
        if (curChar == '\n' || curChar == '\r') {
           error_line++;
           error_column = 0;
        }
        else
           error_column++;
     }
     if (!EOFSeen) {
        input_stream.backup(1);
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
     }
     throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

final void TokenLexicalActions(Token matchedToken)
{
   switch(jjmatchedKind)
   {
      default : 
         break;
   }
}
}
