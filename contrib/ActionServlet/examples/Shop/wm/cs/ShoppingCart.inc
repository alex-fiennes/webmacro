   #if ($message) {
      <P><FONT COLOR="green">$message</FONT><P>
   }

  <table border="1"  cellspacing="1" cellpadding=2>
    <tr><td bgcolor="gray" align=center colspan=4>
       <font color="white" size="+2">Váš nákupní koš</font>
    </td></tr>

    <tr bgcolor="#D0D0D0">
      <th><a href="$SERVLET?form=cart&action=orderBy&attr=ITEM">Položka</a></th>
      <th><a href="$SERVLET?form=cart&action=orderBy&attr=AMOUNT">Množství</a></th>
      <th><a href="$SERVLET?form=cart&action=orderBy&attr=PRICE">Cena za kus</a></th>
      <th>&nbsp;</th>
    </tr>

    #foreach $item in $cart {
      <tr>
        <td>$item.ITEM</td>
        <td>$item.AMOUNT</td>
        <td>$item.PRICE</td>
        <form method="post" action="$SERVLET">
        <td>
            <input type="hidden" name="form" value="cart">
            <input type="hidden" name="action" value="removeItem">
            <input type="hidden" name="item" value="$item.ITEM">
            <input type="text" name="count" size="3" value="1">
            <input type="submit" value="Odebrat ks">
        </td>
        </form>
      </tr>
    }

    <tr><td colspan=4 align="center">Celková cena: $totalPrice\$</td></tr>

    <tr><!-- navigation -->
      <td colspan=4 align="center">
          #if ($cart.getSkipRows() > 0) {
             [ <A HREF="$SERVLET?form=cart&action=first"><TT>&lt;&lt;</TT></A> ]
             [ <A HREF="$SERVLET?form=cart&action=prev"><TT>&lt;</TT></A> ]
          }

          #if ($cart.hasMore()) {
             [ <A HREF="$SERVLET?form=cart&action=next"><TT>&gt;</TT></A> ]
             [ <A HREF="$SERVLET?form=cart&action=last"><TT>&gt;&gt;</TT></A> ]          
          }
      </td>
    </tr>
  </table>

  #if ($totalPrice > 0) {
    <FORM METHOD="POST" action="$SERVLET">
      <INPUT TYPE="Hidden" NAME="form" VALUE="cart">
      <INPUT TYPE="Hidden" NAME="action" VALUE="order">
      #if($loggedInUser == null) {
        <INPUT TYPE="Submit" DISABLED VALUE="Objednat">
      } #else {
        <INPUT TYPE="Submit" VALUE="Objednat">
      }
    </FORM>

    #if($loggedInUser == null) {
      (pøed objednáním zboží se musíte <A HREF="$SERVLET?action=show&target=ShopLogin.inc">pøihlásit</A>)
    }
  }
