/*
* Copyright Acctiva Corporation, 2000.
*
* Software is governed and protected under the patent,
* copyright and trademark laws of governing countries
* and this code and embodiment herein has been registered in the
* the United States with the Patent and Trademark Office.
* 
* Right to use is strictly licensed by Acctiva corporation.
*
* Direct all questions and comments to support@acctiva.com.
*/
package com.acctiva.views.directory;
import javax.servlet.http.HttpServletRequest;
import org.opendoors.util.Console;
import org.opendoors.adt.OrderedSet;
import org.opendoors.store.*;
import com.acctiva.views.*;
import com.acctiva.schema.directory.CommonDirectory;
import com.acctiva.schema.directory.Refer;

/**
 * A two panel view of a user.
 * <p>
 * Panel 1: Information on referred party.
 * Panel 2: Voluntary information from party making referral.
 */
public class ReferralView extends GeneralCommitView implements Refer {

  

	static final FieldData[] panel1 = {
									new EmailData(REFERRED_EMAIL),
									new StringData(REFERRED_NAME_FIRST),
									new StringData(REFERRED_NAME_LAST),
									new StringData(REFERRED_ORG_NAME)
													};

	static final FieldData[] panel2 = {
										new StringData(REFERRAL_NOTE),
										new StringData(REFERRERS_NAME),
										new BooleanData(REFERRAL_NOTE_PUBLIC)
										        };

  static final FieldData[][] panels = {panel1, panel2};
  static final MetaField[][] layout = { {REFERRED_EMAIL,
                                      REFERRED_NAME_FIRST,
                                      REFERRED_NAME_LAST,
                                      REFERRED_ORG_NAME},
                                       null};
  static final boolean[] multi = {true, false};
  static final String[] titles = {"Directory.ReferredName",
                                  "Directory.ReferringNameAndNote"};
  
													
	public ReferralView() {}

  public FieldData[][] getDataPanels() {
      return panels;
  }

  public boolean lastIsCommit() { return true; }
      
  public boolean[] isMultiView() {
    return multi;
  }

  public Attributes[][] getMultiValues() {
    // need to do a look up here if the
    // request has a seqnbr value
    Attributes[][] v = new Attributes[2][];
    return v;
  }

  public MetaField[][] getMultiLayout() {
    return layout;
  }

	public String[] getPanelTitles() { return titles; }

  public String toString() { return "ReferralView"; }

  public CommitActionHandler getCommitHandler(CommitView v) {
    InsertInterimBOHandler i = new InsertInterimBOHandler(ActionVerb.COMMIT, v);
    i.setProperties("ReferralView", "Directory.ReferredName" + " " +
                                  "Directory.ReferringNameAndNote");
    return i;
  }

	public Attributes getCommitAttributes() {return null;}

	public String getCommitTitle() {return null;}

}


