  <table border="1" cellspacing="1">
    <tr bgcolor="#D0D0D0">
      <th><a href="DbDemo?form=actors&action=orderBy&attr=FIRSTNAME">First Name</a></th>
      <th><a href="DbDemo?form=actors&action=orderBy&attr=SURNAME">Last Name</a></th>
      <th></th>
    </tr>

    #foreach $actor in $actors {
      <tr>
        <td>$actor.FIRSTNAME</td>
        <td>$actor.SURNAME</td>
        <td><a href="DbDemo?form=movies&action=select&sql=select+*+from+movies+where+FIRSTNAME%3D%27$actor.FIRSTNAME%27+and+SURNAME%3D%27$actor.SURNAME%27+order+by+TITLE">show movies</a></td>
      </tr>
    }

    <tr><!-- navigation -->
      <td colspan=3 align="center">
          #if ($actors.getSkipRows() > 0) {
             [ <A HREF="DbDemo?form=actors&action=first"><TT>&lt;&lt;</TT></A> ]
             [ <A HREF="DbDemo?form=actors&action=prev"><TT>&lt;</TT></A> ]
          }

          #if ($actors.hasMore()) {
             [ <A HREF="DbDemo?form=actors&action=next"><TT>&gt;</TT></A> ]
             [ <A HREF="DbDemo?form=actors&action=last"><TT>&gt;&gt;</TT></A> ]          
          }
      </td>
    </tr>
  </table>
