  #if ($message) {
     <P><CENTER><FONT COLOR="green">$message</FONT><P>
  }

  <table border="1" cellspacing="1" cellpadding=2>
    <tr><td bgcolor="gray" align=center colspan=6>
       <font color="white" size="+2">Nabídka zboží</font>
    </td></tr>

    <tr bgcolor="#D0D0D0">
      <th><a href="$SERVLET?form=goods&action=orderBy&attr=ITEM">Položka</a></th>
      <th><a href="$SERVLET?form=goods&action=orderBy&attr=AMOUNT">Množství</a></th>
      <th><a href="$SERVLET?form=goods&action=orderBy&attr=PRICE">Cena za kus</a></th>
      <th>&nbsp;</th>
      #if ($isAdmin) {<th>&nbsp;</th><th>&nbsp;</th>}
    </tr>

    #foreach $item in $goods {
      <tr>
        <td>$item.ITEM</td>
        <td>$item.AMOUNT</td>
        <td>$item.PRICE</td>
        <form method="post" action="$SERVLET">
        <td>
            <input type="hidden" name="form" value="cart">
            <input type="hidden" name="action" value="addItem">
            <input type="hidden" name="item" value="$item.ITEM">
            <input type="text" name="count" size="3" value="1">
            <input type="submit" value="Pøidat do koše">
        </td>
        </form>
        #if ($isAdmin) {
        <form method="post" action="$SERVLET">
        <td>
            <input type="hidden" name="action" value="show2">
            <input type="hidden" name="target" value="EditItem.inc">
            <input type="hidden" name="paramName" value="item">
            <input type="hidden" name="paramValue" value="$item.ITEM">
            <input type="hidden" name="paramName" value="amount">
            <input type="hidden" name="paramValue" value="$item.AMOUNT">
            <input type="hidden" name="paramName" value="price">
            <input type="hidden" name="paramValue" value="$item.PRICE">
            <input type="submit" value="Editovat">
        </td>
        </form>
        <form method="post" action="$SERVLET">
        <td>
            <input type="hidden" name="form" value="item">
            <input type="hidden" name="action" value="removeItem">
            <input type="hidden" name="item" value="$item.ITEM">
            <input type="submit" value="Odstranit">
        </td>
        </form>
        }
      </tr>
    }

    <tr><!-- navigation -->
      <td colspan=6 align="center">
          #if ($goods.getSkipRows() > 0) {
             [ <A HREF="$SERVLET?form=goods&action=first"><TT>&lt;&lt;</TT></A> ]
             [ <A HREF="$SERVLET?form=goods&action=prev"><TT>&lt;</TT></A> ]
          }

          #if ($goods.hasMore()) {
             [ <A HREF="$SERVLET?form=goods&action=next"><TT>&gt;</TT></A> ]
             [ <A HREF="$SERVLET?form=goods&action=last"><TT>&gt;&gt;</TT></A> ]          
          }
      </td>
    </tr>
  </table>

#if ($addedItem != null) {
  <P><CENTER><FONT COLOR="green">
  Pøidáno $addedItem.count x $addedItem.name do Vašeho
  <A HREF="$SERVLET?action=show&target=ShoppingCart.inc">nákupního koše</A>.
  </FONT></CENTER><P>
}

#if ($isAdmin) {
  <form method="post" action="$SERVLET">
  <input type="hidden" name="action" value="show">
  <input type="hidden" name="target" value="NewItem.inc">
  <input type="submit" value="Nová položka">
  </form>
}
