   #if ($error != null) {
      <P><FONT COLOR="red">$error</FONT><P>
   }

   <TABLE BORDER=1>
   <tr><td bgcolor="gray" align=center colspan=2>
      <font color="white" size="+2">New account</font>
   </td></tr>

   <FORM METHOD="POST" action="$SERVLET">
   <TR><TD>
   <TABLE>
   <TR><TD>
      #if ($user == null) {
         User name:</TD><TD><INPUT TYPE="Text" NAME="userName"></TD></TR><TR><TD>
         Password:</TD><TD><INPUT TYPE="Password" NAME="password"></TD></TR><TR><TD>
         Confirm password:</TD><TD><INPUT TYPE="Password" NAME="confirm"></TD></TR><TR><TD>
         E-mail:</TD><TD><INPUT TYPE="Text" NAME="email"></TD></TR><TR><TD>
         First name:</TD><TD><INPUT TYPE="Text" NAME="firstName"></TD></TR><TR><TD>
         Surname:</TD><TD><INPUT TYPE="Text" NAME="surname"></TD></TR><TR><TD>
         Street address:</TD><TD><INPUT TYPE="Text" NAME="street"></TD></TR><TR><TD>
         City:</TD><TD><INPUT TYPE="Text" NAME="city"></TD></TR><TR><TD>
         Postal code:</TD><TD><INPUT TYPE="Text" NAME="postalCode"></TD></TR><TR><TD>
         State:</TD><TD><INPUT TYPE="Text" NAME="state"></TD></TR><TR><TD>
      } #else {
         User name:</TD><TD><INPUT TYPE="Text" NAME="userName" VALUE="$user.userName"></TD></TR><TR><TD>
         Password:</TD><TD><INPUT TYPE="Password" NAME="password"></TD></TR><TR><TD>
         Confirm password:</TD><TD><INPUT TYPE="Password" NAME="confirm"></TD></TR><TR><TD>
         E-mail:</TD><TD><INPUT TYPE="Text" NAME="email" VALUE="$user.email"></TD></TR><TR><TD>
         First name:</TD><TD><INPUT TYPE="Text" NAME="firstName" VALUE="$user.firstName"></TD></TR><TR><TD>
         Surname:</TD><TD><INPUT TYPE="Text" NAME="surname" VALUE="$user.surname"></TD></TR><TR><TD>
         Street address:</TD><TD><INPUT TYPE="Text" NAME="street" VALUE="$user.street"></TD></TR><TR><TD>
         City:</TD><TD><INPUT TYPE="Text" NAME="city" VALUE="$user.city"></TD></TR><TR><TD>
         Postal code:</TD><TD><INPUT TYPE="Text" NAME="postalCode" VALUE="$user.postalCode"></TD></TR><TR><TD>
         State:</TD><TD><INPUT TYPE="Text" NAME="state" VALUE="$user.state"></TD></TR><TR><TD>
      }
      <TR><TD COLSPAN=2 ALIGN=CENTER>
         <INPUT TYPE="Hidden" NAME="user" VALUE="userName,email,firstName,surname,street,city,postalCode,state">
         <INPUT TYPE="Hidden" NAME="form" VALUE="accounts">
         #if ($isAdmin) {
            <INPUT TYPE="Hidden" NAME="action" VALUE="newAccount2">
         } #else {
            <INPUT TYPE="Hidden" NAME="action" VALUE="newAccount">
         }
         <INPUT TYPE="Submit" VALUE="Submit">
      </TD></TR></TABLE>
   </TD></TR></TABLE>
   </FORM>
