<TABLE BORDER=0 CELLPADDING=5><TR><TD>
<BR><CENTER>
<FONT SIZE="+2">Volby</FONT><BR>
#if ($loggedInUser != null) {($loggedInUser)}
#else {(nep�ihl�en)}
</CENTER>

<UL>
#if ($loggedInUser == null) {
    <LI><A HREF="$SERVLET?action=show&target=ShopLogin.inc">P�ihl�en�</A><P>
} #else {
    <LI><A HREF="$SERVLET?action=show&target=SetPassword.inc">Zm�nit heslo</A><P>
    <LI><A HREF="$SERVLET?form=accounts&action=showAccount">Editovat ��et</A><P>
    <LI><A HREF="$SERVLET?action=Logoff">Odhl�sit</A><P>
}
<LI><A HREF="$SERVLET?action=show&target=goods.inc">Nab�dka zbo��</A><P>
<LI><A HREF="$SERVLET?action=show&target=ShoppingCart.inc">N�kupn� ko�</A><P>
#if ($loggedInUser != null) {
    <LI><A HREF="$SERVLET?action=show&target=orders.inc">Va�e objedn�vky</A><P>
}
#if ($isAdmin) {
    <LI><A HREF="$SERVLET?action=show&target=accounts.inc">U�ivatelsk� ��ty</A><P>
    <LI><A HREF="$SERVLET?action=show&target=AdminOrders.inc">V�echny objedn�vky</A><P>
}
</UL>

</TD></TR></TABLE>