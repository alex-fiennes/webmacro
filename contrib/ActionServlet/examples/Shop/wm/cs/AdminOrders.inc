  #if ($message) {
     <P><CENTER><FONT COLOR="green">$message</FONT><P>
  }

  <table border="1" cellspacing="1" cellpadding=2>
    <tr><td bgcolor="gray" align=center colspan=7>
       <font color="white" size="+2">Objedn�vky</font>
    </td></tr>

    <tr bgcolor="#D0D0D0">
      <th><a href="$SERVLET?form=AdminOrders&action=orderBy&attr=USER">U�ivatel</a></th>
      <th><a href="$SERVLET?form=AdminOrders&action=orderBy&attr=DATE">Datum</a></th>
      <th>Items</th>
      <th><a href="$SERVLET?form=AdminOrders&action=orderBy&attr=PRICE">Celkov� cena</a></th>
      <th><a href="$SERVLET?form=AdminOrders&action=orderBy&attr=STATUS">Stav</a></th>
      <th>&nbsp;</th>
      <th>&nbsp;</th>
    </tr>

    #foreach $order in $orders {
      <tr>
        <td>$order.USER</td>
        <td>$order.DATE</td>
        <td>$order.ITEMS</td>
        <td>$order.PRICE\$</td>
        <td>#if ($order.STATUS == "PENDING") {�EKAJ�C�}
            #else {#if ($order.STATUS == "REJECTED") {ZAM�TNUTO}
            #else {#if ($order.STATUS == "EXPEDED") {EXPEDOV�NO}
            #else {$order.STATUS}}}
        </td>
        <form method="post" action="$SERVLET">
        <td>
            <input type="hidden" name="id" value="$order.ID">
            <input type="hidden" name="form" value="AdminOrders">
            <input type="hidden" name="action" value="expede">
            #if ($order.STATUS == "PENDING") {
               <input type="submit" value="Expedovat">
            } #else {
               <input type="submit" DISABLED value="Expedovat">
            }
        </td>
        </form>
        <form method="post" action="$SERVLET">
        <td>
            <input type="hidden" name="id" value="$order.ID">
            <input type="hidden" name="form" value="AdminOrders">
            <input type="hidden" name="action" value="reject">
            #if ($order.STATUS == "PENDING") {
               <input type="submit" value="Zam�tnout">
            } #else {
               <input type="submit" DISABLED value="Zam�tnout">
            }
        </td>
        </form>
      </tr>
    }

    <tr><!-- navigation -->
      <td colspan=7 align="center">
          #if ($orders.getSkipRows() > 0) {
             [ <A HREF="$SERVLET?form=AdminOrders&action=first"><TT>&lt;&lt;</TT></A> ]
             [ <A HREF="$SERVLET?form=AdminOrders&action=prev"><TT>&lt;</TT></A> ]
          }

          #if ($orders.hasMore()) {
             [ <A HREF="$SERVLET?form=AdminOrders&action=next"><TT>&gt;</TT></A> ]
             [ <A HREF="$SERVLET?form=AdminOrders&action=last"><TT>&gt;&gt;</TT></A> ]          
          }
      </td>
    </tr>
  </table>

  <P>Pozn.: Zam�tnut� objedn�vky nevrac� zbo�� do nab�dky (zat�m)
