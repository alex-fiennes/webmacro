   #if ($error) {
      <P><FONT COLOR="red">$error</FONT><P>
   }

   #if ($loginOK == false) {
      <P><FONT COLOR="red">Bad user name or password. 
      Try again.</FONT><P>
   }

   #if ($accountAdded) {
      <P><FONT COLOR="green">Account created. You may now log in.</FONT><P>
   }

   <FORM METHOD="POST" ACTION="$SERVLET">
   <TABLE BORDER=1>
   <tr><td bgcolor="gray" align=center colspan=2>
      <font color="white" size="+2">Login</font>
   </td></tr>

   <TR><TD>
   <TABLE>
   <TR><TD>
      User name:</TD><TD>
      #if ($userName != null) {
         <INPUT TYPE="Text" NAME="userName" VALUE="$userName">
      } #else {
         <INPUT TYPE="Text" NAME="userName">
      }
      </TD></TR><TR><TD>
      Password:</TD><TD>
      <INPUT TYPE="Password" NAME="password"></TD></TR>
      <TR><TD COLSPAN=2 ALIGN=CENTER>
         <INPUT TYPE="Submit" NAME="action" VALUE="Login">
      </TD></TR></TABLE>
   </TD></TR></TABLE>
   </FORM>

   #if (!$accountAdded) {
      <P>If you don't have an account, fill in the form 
      <A HREF="$SERVLET?action=show&target=NewAccount.inc">here</A>.
   }

   <P><I>(hint: default admininstrator account: user name = 'admin', password='admin')</I>