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
   [ èesky ]
} #else {
   #if ($target == "EditAccount.inc" || $target == "EditItem.inc" || $target == "SetPassword.inc") {
      [ <U>èesky</U> ]
   } #else {
      [ <A HREF="$SERVLET?action=show3&lang=cs&target=$target">èesky</A> ]
   }
}
</small>
</CENTER>