/*
 * Created on Apr 23, 2004
 *
 */
package org.tcdi.opensource.wiki;

/**
 * 
 * @author Lane Sharman
 */
/**
 * The system may approve users to use the wiki system.
 * This class is the delegate which maintains state
 * and the decision for user approval.
 * <p>
 * Users are approved to use the Wiki system iff:
 * <pre>
 * 1) The system is running in Approval_Mode=false
 * or
 * 2) The system  is running in Approval_Mode=true
 * and the user is approved.
 * </pre>
 * <p>
 * Note: When a wiki system transitions from approval_mode=false
 * to true, existing users are grand-fathered in as approved users
 * so as not to cause a lot of disruption to the user base.   
 * @author Lane Sharman
 */
public class Approve
{
  //Allowable user states.
  public  static final int PRE_APPROVED = 0; // if the user existed, he/she is approved.
  public  static final int APPROVAL_PENDING=1;
  public  static final int APPROVED=2;
  public static final String[] AS_TEXT = {"Pre-Approved", "Approval Pending", "Approved"};
  
  private boolean approvalRequired = false;

  /**
   * Creates the approval subsystem instance using the wiki property object
   * governing whether we are running in approval mode or not.
   */
  public Approve(String approvalProperty)
  {
    approvalRequired = (approvalProperty != null && 
      (approvalProperty.trim().equalsIgnoreCase("Private") ||
       approvalProperty.trim().equalsIgnoreCase("Public_ApprovalRequired")));
  }
  
  public String getAsText(int stateValue)
  {
    return AS_TEXT[stateValue];
  }
  
  /**
   * For a new user, set the approval code properly.
   * @param user
   */
  public void newUser(WikiUser user, boolean isAdministrator)
  {
    if (!approvalRequired || isAdministrator)
    {
      user.setApprovalState(APPROVED);
    }
    else
    {
      user.setApprovalState(APPROVAL_PENDING); 
    }
  }
  
  /**
   * Approve a user.
   * @param user
   */
  public void approveUser(WikiUser user)
  {
    user.setApprovalState(APPROVED); 
  }
  
  /**
   * Evaluates the user to see if approved to use the wiki.
   * @param user the user to check.
   * @return true if user approved.
   */
  public boolean isUserApproved(WikiUser user)
  {
    int value = user.getApprovalState();
    return (value == APPROVED || value == PRE_APPROVED || !approvalRequired);
  }
  
  /**
   *  Returns true if the system is running in approve mode.
   * 
   * @author Lane Sharman
   */
  public boolean isRunningInApproveMode()
  {
    return approvalRequired;
  }
  
}
