<TABLE BORDER=0 CELLPADDING=5><TR><TD>
<BR><CENTER>
<FONT SIZE="+2">Volby</FONT><BR>
#if ($loggedInUser != null) {($loggedInUser)}
#else {(nepøihlášen)}
</CENTER>

<UL>
#if ($loggedInUser == null) {
    <LI><A HREF="$SERVLET?action=show&target=ShopLogin.inc">Pøihlášení</A><P>
} #else {
    <LI><A HREF="$SERVLET?action=show&target=SetPassword.inc">Zmìnit heslo</A><P>
    <LI><A HREF="$SERVLET?form=accounts&action=showAccount">Editovat úèet</A><P>
    <LI><A HREF="$SERVLET?action=Logoff">Odhlásit</A><P>
}
<LI><A HREF="$SERVLET?action=show&target=goods.inc">Nabídka zboží</A><P>
<LI><A HREF="$SERVLET?action=show&target=ShoppingCart.inc">Nákupní koš</A><P>
#if ($loggedInUser != null) {
    <LI><A HREF="$SERVLET?action=show&target=orders.inc">Vaše objednávky</A><P>
}
#if ($isAdmin) {
    <LI><A HREF="$SERVLET?action=show&target=accounts.inc">Uživatelské úèty</A><P>
    <LI><A HREF="$SERVLET?action=show&target=AdminOrders.inc">Všechny objednávky</A><P>
}
</UL>

</TD></TR></TABLE>