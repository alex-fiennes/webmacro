<TABLE BORDER=0 CELLPADDING=5><TR><TD>
<BR><CENTER>
<FONT SIZE="+2">Toolbar</FONT><BR>
#if ($loggedInUser != null) {($loggedInUser)}
#else {(not logged in)}
</CENTER>

<UL>
#if ($loggedInUser == null) {
    <LI><A HREF="$SERVLET?action=show&target=ShopLogin.inc">Login</A><P>
} #else {
    <LI><A HREF="$SERVLET?action=show&target=SetPassword.inc">Set password</A><P>
    <LI><A HREF="$SERVLET?form=accounts&action=showAccount">Edit account</A><P>
    <LI><A HREF="$SERVLET?action=Logoff">Logoff</A><P>
}
<LI><A HREF="$SERVLET?action=show&target=goods.inc">Goods</A><P>
<LI><A HREF="$SERVLET?action=show&target=ShoppingCart.inc">Shopping cart</A><P>
#if ($loggedInUser != null) {
    <LI><A HREF="$SERVLET?action=show&target=orders.inc">Your orders</A><P>
}
#if ($isAdmin) {
    <LI><A HREF="$SERVLET?action=show&target=accounts.inc">Accounts</A><P>
    <LI><A HREF="$SERVLET?action=show&target=AdminOrders.inc">All orders</A><P>
}
</UL>

</TD></TR></TABLE>