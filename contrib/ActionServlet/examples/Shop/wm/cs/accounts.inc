   #if ($message) {
      <P><FONT COLOR="green">$message</FONT><P>
   }

  <table border="1" cellspacing="1" cellpadding=2>
    <tr><td bgcolor="gray" align=center colspan=6>
       <font color="white" size="+2">Uživatelské úèty</font>
    </td></tr>

    <tr bgcolor="#D0D0D0">
      <th><a href="$SERVLET?form=accounts&action=orderBy&attr=USERNAME">Uživ. jméno</a></th>
      <th><a href="$SERVLET?form=accounts&action=orderBy&attr=EMAIL">E-mail</a></th>
      <th><a href="$SERVLET?form=accounts&action=orderBy&attr=STATE">Stát</a></th>
      <th>&nbsp;</th>
      <th>&nbsp;</th>
      <th>&nbsp;</th>
    </tr>

    #foreach $account in $accounts {
      <tr>
        <td>$account.USERNAME</td>
        <td>$account.EMAIL</td>
        <td>$account.STATE</td>
        <form method="post" action="$SERVLET">
        <td>
            <input type="hidden" name="form" value="accounts">
            <input type="hidden" name="action" value="showAccount2">
            <input type="hidden" name="userName" value="$account.USERNAME">
            <input type="submit" value="Editovat">
        </td>
        </form>
        <form method="post" action="$SERVLET">
        <td>
            <input type="hidden" name="action" value="show2">
            <input type="hidden" name="target" value="SetPassword.inc">
            <input type="hidden" name="paramName" value="adminEdit">
            <input type="hidden" name="paramValue" value="true">
            <input type="hidden" name="paramName" value="userName">
            <input type="hidden" name="paramValue" value="$account.USERNAME">
            <input type="submit" value="Zmìnit heslo">
        </td>
        </form>
        <form method="post" action="$SERVLET">
        <td>
            <input type="hidden" name="form" value="accounts">
            <input type="hidden" name="action" value="removeAccount">
            <input type="hidden" name="user" value="$account.USERNAME">
            <input type="submit" value="Smazat">
        </td>
        </form>
      </tr>
    }

    <tr><!-- navigation -->
      <td colspan=6 align="center">
          #if ($accounts.getSkipRows() > 0) {
             [ <A HREF="$SERVLET?form=accounts&action=first"><TT>&lt;&lt;</TT></A> ]
             [ <A HREF="$SERVLET?form=accounts&action=prev"><TT>&lt;</TT></A> ]
          }

          #if ($accounts.hasMore()) {
             [ <A HREF="$SERVLET?form=accounts&action=next"><TT>&gt;</TT></A> ]
             [ <A HREF="$SERVLET?form=accounts&action=last"><TT>&gt;&gt;</TT></A> ]          
          }
      </td>
    </tr>
  </table>

  <form method="post" action="$SERVLET">
  <input type="hidden" name="action" value="show">
  <input type="hidden" name="target" value="NewAccount.inc">
  <input type="submit" value="Nový úèet">
  </form>
