   #if ($error != null) {
      <P><FONT COLOR="red">$error</FONT><P>
   }

   #if ($itemEdited) {
      <P><FONT COLOR="green">Data položky uložena.</FONT><P>
   }

   <TABLE BORDER=1 CELLPADDING=5>
   <tr><td bgcolor="gray" align=center colspan=2>
      <font color="white" size="+2">Editovat položku</font>
   </td></tr>

   <TR><TD>
      <TABLE><TR><TD>
         Název:</TD><TD>$item</TD></TR><TR><TD>
         Množství:</TD><TD>$amount</TD></TR><TR><TD>
         Jenotková cena:</TD><TD>$price
      </TD></TR></TABLE>
   </TD></TR>
   <FORM METHOD="POST" action="$SERVLET">
   <TR><TD>
      <INPUT TYPE="Hidden" NAME="item" VALUE="$item">
      <INPUT TYPE="Text" NAME="count" VALUE="1" SIZE="4">
      <INPUT TYPE="Hidden" NAME="form" VALUE="item">
      <INPUT TYPE="Hidden" NAME="action" VALUE="addAmount">
      <INPUT TYPE="Submit" VALUE="Pøidat">
   </TD></TR>
   </FORM>
   <FORM METHOD="POST" action="$SERVLET">
   <TR><TD>
      <INPUT TYPE="Hidden" NAME="item" VALUE="$item">
      <INPUT TYPE="Text" NAME="count" VALUE="1" SIZE="4">
      <INPUT TYPE="Hidden" NAME="form" VALUE="item">
      <INPUT TYPE="Hidden" NAME="action" VALUE="removeAmount">
      <INPUT TYPE="Submit" VALUE="Odebrat">
   </TD></TR>
   </FORM>
   <FORM METHOD="POST" action="$SERVLET">
   <TR><TD>
      <INPUT TYPE="Hidden" NAME="item" VALUE="$item">
      <INPUT TYPE="Text" NAME="price" VALUE="0" SIZE="4">
      <INPUT TYPE="Hidden" NAME="form" VALUE="item">
      <INPUT TYPE="Hidden" NAME="action" VALUE="setPrice">
      <INPUT TYPE="Submit" VALUE="Nastavit cenu">
   </TD></TR>
   </FORM>
   </TABLE>
