   #if ($error != null) {
      <P><FONT COLOR="red">$error</FONT><P>
   }

   #if ($itemAdded) {
      <P><FONT COLOR="green">Polo�ka byla p�id�na do nab�dky.</FONT><P>
   }

   <TABLE BORDER=1>
   <tr><td bgcolor="gray" align=center colspan=2>
      <font color="white" size="+2">Nov� polo�ka</font>
   </td></tr>

   <FORM METHOD="POST" action="$SERVLET">
   <TR><TD>
   <TABLE>
   <TR><TD>
      #if ($item == null) {
         N�zev:</TD><TD><INPUT TYPE="Text" NAME="itemName"></TD></TR><TR><TD>
         Po��te�n� mno�stv�:</TD><TD><INPUT TYPE="text" NAME="count"></TD></TR><TR><TD>
         Jednotkov� cena:</TD><TD><INPUT TYPE="text" NAME="price"></TD></TR><TR><TD>
      } #else {
         N�zev:</TD><TD><INPUT TYPE="Text" NAME="item" VALUE="$item"></TD></TR><TR><TD>
         Po��te�n� mno�stv�:</TD><TD><INPUT TYPE="text" NAME="count" VALUE="$count"></TD></TR><TR><TD>
         Jednotkov� cena:</TD><TD><INPUT TYPE="text" NAME="price" VALUE="$price"></TD></TR><TR><TD>
      }
      <TR><TD COLSPAN=2 ALIGN=CENTER>
         <INPUT TYPE="Hidden" NAME="item" VALUE="itemName,count,price">
         <INPUT TYPE="Hidden" NAME="form" VALUE="item">
         <INPUT TYPE="Hidden" NAME="action" VALUE="newItem">
         <INPUT TYPE="Submit" VALUE="Ulo�it">
      </TD></TR></TABLE>
   </TD></TR></TABLE>
   </FORM>
