   #if ($error != null) {
      <P><FONT COLOR="red">$error</FONT><P>
   }

   #if ($passwordChanged) {
      <P><FONT COLOR="green">Heslo zmìnìno.</FONT><P>
   }

   <TABLE BORDER=1>
   <tr><td bgcolor="gray" align=center colspan=2>
      <font color="white" size="+2">Zmìnit heslo</font>
   </td></tr>

   <FORM METHOD="POST" action="$SERVLET">
   <TR><TD>
   <TABLE>
   <TR><TD>
         #if ($adminEdit) {
            Uživ. jméno:</TD><TD><INPUT TYPE="Hidden" NAME="userName" VALUE="$userName">$userName</TD></TR><TR><TD>
            <INPUT TYPE="Hidden" NAME="oldPassword" VALUE="*">
         } #else {
            Staré heslo:</TD><TD><INPUT TYPE="Password" NAME="oldPassword"></TD></TR><TR><TD>
         }
         Nové heslo:</TD><TD><INPUT TYPE="Password" NAME="newPassword"></TD></TR><TR><TD>
         Potvrzení hesla:</TD><TD><INPUT TYPE="Password" NAME="confirm"></TD></TR><TR><TD>
      <TR><TD COLSPAN=2 ALIGN=CENTER>
         <INPUT TYPE="Hidden" NAME="form" VALUE="accounts">
         <INPUT TYPE="Submit" VALUE="Odeslat">
         #if ($adminEdit) {
            <INPUT TYPE="Hidden" NAME="action" VALUE="setPassword2">
         } #else {
            <INPUT TYPE="Hidden" NAME="action" VALUE="setPassword">
         }
      </TD></TR></TABLE>
   </TD></TR></TABLE>
   </FORM>
