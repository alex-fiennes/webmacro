   #if ($error) {
      <P><FONT COLOR="red">$error</FONT><P>
   }

   #if ($loginOK == false) {
      <P><FONT COLOR="red">Chybn� jm�no nebo heslo. 
      Zkuste to znovu.</FONT><P>
   }

   #if ($accountAdded) {
      <P><FONT COLOR="green">��et vytvo�en. Nyn� se m��ete p�ihl�sit.</FONT><P>
   }

   <FORM METHOD="POST" action="$SERVLET">
   <TABLE BORDER=1>
   <tr><td bgcolor="gray" align=center colspan=2>
      <font color="white" size="+2">P�ihl�en�</font>
   </td></tr>

   <TR><TD>
      <TABLE><TR><TD>
      U�iv. jm�no:</TD><TD>
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
         <INPUT TYPE="Submit" VALUE="P�ihl�sit">
      </TD></TR></TABLE>
   </TD></TR></TABLE>
   </FORM>

   #if (!$accountAdded) {
      <P>Pokud nem�te zalo�en ��et, vypl�te 
      <A HREF="$SERVLET?action=show&target=NewAccount.inc">tento formul��</A>.
   }

   <P><I>(n�pov�da: implicitn� admininstr�torsk� ��et: u�iv. jm�no = 'admin', heslo='admin')</I>