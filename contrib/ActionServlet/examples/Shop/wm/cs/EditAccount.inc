   #if ($error != null) {
      <P><FONT COLOR="red">$error</FONT><P>
   }

   #if ($accountEdited) {
      <P><FONT COLOR="green">Data úètu uložena.</FONT><P>
   }

   <TABLE BORDER=1>
   <tr><td bgcolor="gray" align=center colspan=2>
      <font color="white" size="+2">Editovat úèet</font>
   </td></tr>

   <FORM METHOD="POST" action="$SERVLET">
   <TR><TD>
      <TABLE>
      <TR><TD>
         Uživ. jméno:</TD><TD><INPUT TYPE="Hidden" NAME="userName" VALUE="$user.userName">$user.userName</TD></TR><TR><TD>
         E-mail:</TD><TD><INPUT TYPE="Text" NAME="email" VALUE="$user.email"></TD></TR><TR><TD>
         Jméno:</TD><TD><INPUT TYPE="Text" NAME="firstName" VALUE="$user.firstName"></TD></TR><TR><TD>
         Pøíjmení:</TD><TD><INPUT TYPE="Text" NAME="surname" VALUE="$user.surname"></TD></TR><TR><TD>
         Ulice:</TD><TD><INPUT TYPE="Text" NAME="street" VALUE="$user.street"></TD></TR><TR><TD>
         Mìsto:</TD><TD><INPUT TYPE="Text" NAME="city" VALUE="$user.city"></TD></TR><TR><TD>
         PSÈ:</TD><TD><INPUT TYPE="Text" NAME="postalCode" VALUE="$user.postalCode"></TD></TR><TR><TD>
         Stát:</TD><TD><INPUT TYPE="Text" NAME="state" VALUE="$user.state">
      </TD></TR>
      <TR><TD COLSPAN=2 ALIGN=CENTER>
         <INPUT TYPE="Hidden" NAME="user" VALUE="userName,email,firstName,surname,street,city,postalCode,state">
         <INPUT TYPE="Hidden" NAME="form" VALUE="accounts">
         #if (!$adminEdit) {
            <INPUT TYPE="Hidden" NAME="action" VALUE="editAccount">
         } #else {
            <INPUT TYPE="Hidden" NAME="action" VALUE="editAccount2">
         }
         <INPUT TYPE="Submit" VALUE="Uložit">
      </TD></TR></TABLE>
   </TD></TR></TABLE>
   </FORM>
