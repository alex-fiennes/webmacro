   #if ($error) {
      <P><FONT COLOR="red">$error</FONT><P>
   }

   #if ($loginOK == false) {
      <P><FONT COLOR="red">Chybné jméno nebo heslo. 
      Zkuste to znovu.</FONT><P>
   }

   #if ($accountAdded) {
      <P><FONT COLOR="green">Úèet vytvoøen. Nyní se mùžete pøihlásit.</FONT><P>
   }

   <FORM METHOD="POST" action="$SERVLET">
   <TABLE BORDER=1>
   <tr><td bgcolor="gray" align=center colspan=2>
      <font color="white" size="+2">Pøihlášení</font>
   </td></tr>

   <TR><TD>
      <TABLE><TR><TD>
      Uživ. jméno:</TD><TD>
      #if ($userName != null) {
         <INPUT TYPE="Text" NAME="userName" VALUE="$userName">
      } #else {
         <INPUT TYPE="Text" NAME="userName">
      }
      </TD></TR><TR><TD>
         Heslo:</TD><TD>
      <INPUT TYPE="Password" NAME="password"></TD></TR>
      <TR><TD COLSPAN=2 ALIGN=CENTER>
         <INPUT TYPE="Hidden" NAME="action" VALUE="Login">
         <INPUT TYPE="Submit" VALUE="Pøihlásit">
      </TD></TR></TABLE>
   </TD></TR></TABLE>
   </FORM>

   #if (!$accountAdded) {
      <P>Pokud nemáte založen úèet, vyplòte 
      <A HREF="$SERVLET?action=show&target=NewAccount.inc">tento formuláø</A>.
   }

   <P><I>(nápovìda: implicitní admininstrátorský úèet: uživ. jméno = 'admin', heslo='admin')</I>