<CENTER>
<small>
#if ($lang == "en") {
   [ english ]
} #else {
   #if ($target == "EditAccount.inc" || $target == "EditItem.inc" || $target == "SetPassword.inc") {
      [ <U>english</U> ]
   } #else {
      [ <A HREF="$SERVLET?action=show3&lang=en&target=$target">english</A> ]
   }
}

#if ($lang == "cs") {
   [ �esky ]
} #else {
   #if ($target == "EditAccount.inc" || $target == "EditItem.inc" || $target == "SetPassword.inc") {
      [ <U>�esky</U> ]
   } #else {
      [ <A HREF="$SERVLET?action=show3&lang=cs&target=$target">�esky</A> ]
   }
}
</small>
</CENTER>