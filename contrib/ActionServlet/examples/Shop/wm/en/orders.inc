  <table border="1" cellspacing="1" cellpadding=2>
    <tr><td bgcolor="gray" align=center colspan=5>
       <font color="white" size="+2">Your orders</font>
    </td></tr>

    <tr bgcolor="#D0D0D0">
      <th><a href="$SERVLET?form=orders&action=orderBy&attr=DATE">Date</a></th>
      <th>Items</th>
      <th><a href="$SERVLET?form=orders&action=orderBy&attr=PRICE">Total price</a></th>
      <th><a href="$SERVLET?form=orders&action=orderBy&attr=STATUS">Status</a></th>
    </tr>

    #foreach $order in $orders {
      <tr>
        <td>$order.DATE</td>
        <td>$order.ITEMS</td>
        <td>$order.PRICE\$</td>
        <td>$order.STATUS</td>
      </tr>
    }

    <tr><!-- navigation -->
      <td colspan=5 align="center">
          #if ($orders.getSkipRows() > 0) {
             [ <A HREF="$SERVLET?form=orders&action=first"><TT>&lt;&lt;</TT></A> ]
             [ <A HREF="$SERVLET?form=orders&action=prev"><TT>&lt;</TT></A> ]
          }

          #if ($orders.hasMore()) {
             [ <A HREF="$SERVLET?form=orders&action=next"><TT>&gt;</TT></A> ]
             [ <A HREF="$SERVLET?form=orders&action=last"><TT>&gt;&gt;</TT></A> ]          
          }
      </td>
    </tr>
  </table>
