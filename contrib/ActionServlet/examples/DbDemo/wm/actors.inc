  <table border="1" cellspacing="1">
    <tr bgcolor="#D0D0D0">
      <th><a href="DbDemo?form=actors&action=orderBy&attr=FIRSTNAME">First Name</a></th>
      <th><a href="DbDemo?form=actors&action=orderBy&attr=SURNAME">Last Name</a></th>
      <th>&nbsp;</th>
    </tr>

    #foreach $actor in $actors {
      <tr>
        <td>$actor.FIRSTNAME</td>
        <td>$actor.SURNAME</td>
        <form method="post" action="$SERVLET">
        <td>
            <input type="Hidden" name="form" value="movies">
            <input type="Hidden" name="action" value="select">
            <input type="Hidden" name="sql" value="select * from movies where FIRSTNAME='$actor.FIRSTNAME' and SURNAME='$actor.SURNAME' order by TITLE">
            <input type="Submit" value="Show movies">
        </td>
        </form>
      </tr>
    }

    <tr><!-- navigation -->
      <td colspan=3 align="center">
          #if ($actors.getSkipRows() > 0) {
             [ <A HREF="$SERVLET?form=actors&action=first"><TT>&lt;&lt;</TT></A> ]
             [ <A HREF="$SERVLET?form=actors&action=prev"><TT>&lt;</TT></A> ]
          }

          #if ($actors.hasMore()) {
             [ <A HREF="$SERVLET?form=actors&action=next"><TT>&gt;</TT></A> ]
             [ <A HREF="$SERVLET?form=actors&action=last"><TT>&gt;&gt;</TT></A> ]          
          }
      </td>
    </tr>
  </table>
