   #if ($error != null) {
      <P><FONT COLOR="red">$error</FONT><P>
   }

   <TABLE BORDER=1>
   <tr><td bgcolor="gray" align=center colspan=2>
      <font color="white" size="+2">Nový úèet</font>
   </td></tr>

   <FORM METHOD="POST" action="$SERVLET">
   <TR><TD>
   <TABLE>
   <TR><TD>
      #if ($user == null) {
         Uživ. jméno:</TD><TD><INPUT TYPE="Text" NAME="userName"></TD></TR><TR><TD>
         Heslo:</TD><TD><INPUT TYPE="Password" NAME="password"></TD></TR><TR><TD>
         Potvrzení hesla:</TD><TD><INPUT TYPE="Password" NAME="confirm"></TD></TR><TR><TD>
         E-mail:</TD><TD><INPUT TYPE="Text" NAME="email"></TD></TR><TR><TD>
         Jméno:</TD><TD><INPUT TYPE="Text" NAME="firstName"></TD></TR><TR><TD>
         Pøíjmení:</TD><TD><INPUT TYPE="Text" NAME="surname"></TD></TR><TR><TD>
         Ulice:</TD><TD><INPUT TYPE="Text" NAME="street"></TD></TR><TR><TD>
         Mìsto:</TD><TD><INPUT TYPE="Text" NAME="city"></TD></TR><TR><TD>
         PSÈ:</TD><TD><INPUT TYPE="Text" NAME="postalCode"></TD></TR><TR><TD>
         Stát:</TD><TD><INPUT TYPE="Text" NAME="state"></TD></TR><TR><TD>
      } #else {
         Uživ. jméno:</TD><TD><INPUT TYPE="Text" NAME="userName" VALUE="$user.userName"></TD></TR><TR><TD>
         Heslo:</TD><TD><INPUT TYPE="Password" NAME="password"></TD></TR><TR><TD>
         Potvrzení hesla:</TD><TD><INPUT TYPE="Password" NAME="confirm"></TD></TR><TR><TD>
         E-mail:</TD><TD><INPUT TYPE="Text" NAME="email" VALUE="$user.email"></TD></TR><TR><TD>
         Jméno:</TD><TD><INPUT TYPE="Text" NAME="firstName" VALUE="$user.firstName"></TD></TR><TR><TD>
         Pøíjmení:</TD><TD><INPUT TYPE="Text" NAME="surname" VALUE="$user.surname"></TD></TR><TR><TD>
         Ulice:</TD><TD><INPUT TYPE="Text" NAME="street" VALUE="$user.street"></TD></TR><TR><TD>
         Mìsto:</TD><TD><INPUT TYPE="Text" NAME="city" VALUE="$user.city"></TD></TR><TR><TD>
         PSÈ:</TD><TD><INPUT TYPE="Text" NAME="postalCode" VALUE="$user.postalCode"></TD></TR><TR><TD>
         Stát:</TD><TD><INPUT TYPE="Text" NAME="state" VALUE="$user.state"></TD></TR><TR><TD>
      }
      <TR><TD COLSPAN=2 ALIGN=CENTER>
         <INPUT TYPE="Hidden" NAME="user" VALUE="userName,email,firstName,surname,street,city,postalCode,state">
         <INPUT TYPE="Hidden" NAME="form" VALUE="accounts">
         #if ($isAdmin) {
            <INPUT TYPE="Hidden" NAME="action" VALUE="newAccount2">
         } #else {
            <INPUT TYPE="Hidden" NAME="action" VALUE="newAccount">
         }
         <INPUT TYPE="Submit" VALUE="Odeslat">
      </TD></TR></TABLE>
   </TD></TR></TABLE>
   </FORM>
