  <table border="1" cellspacing="1">
    <tr bgcolor="#D0D0D0">
      <th>Movie title</th>
    </tr>

    #foreach $movie in $movies {
      <tr>
        <td>$movie.TITLE</td>
      </tr>
    }

    <tr><!-- navigation -->
      <td colspan=2 align="center">
          #if ($movies.getSkipRows() > 0) {
             [ <A HREF="DbDemo?form=movies&action=first"><TT>&lt;&lt;</TT></A> ]
             [ <A HREF="DbDemo?form=movies&action=prev"><TT>&lt;</TT></A> ]
          }

          #if ($movies.hasMore()) {
             [ <A HREF="DbDemo?form=movies&action=next"><TT>&gt;</TT></A> ]
             [ <A HREF="DbDemo?form=movies&action=last"><TT>&gt;&gt;</TT></A> ]          
          }
      </td>
    </tr>
  </table>
