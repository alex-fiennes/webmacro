package test.com.shinyhosting.formhandler;

import javax.servlet.*;
import javax.servlet.http.*;

import java.util.*;


class MyHttpRequest implements HttpServletRequest
		{
		public Hashtable _parameters = new Hashtable();


		public java.lang.String getParameter(java.lang.String A)
			{
			return (String)_parameters.get(A);
			}

		public java.lang.String getAuthType()
			{
			return "";
			}
		public java.lang.String getRequestURI()
			{
			return "";
			}
		public java.lang.String getContextPath()
			{
			return "";
			}
		public java.util.Enumeration getHeaderNames()
			{
			return null;
			}
		public javax.servlet.http.Cookie[] getCookies()
			{
			return null;
			}
		public long getDateHeader(java.lang.String A)
			{
			return 0l;
			}
		public java.lang.String getHeader(java.lang.String A)
			{
			return null;
			}
		public java.lang.String getPathInfo()
			{
			return "";
			}
		public java.util.Enumeration getHeaders(java.lang.String A)
			{
			return null;
			}
		public int getIntHeader(java.lang.String A)
			{
			return 0;
			}
		public java.lang.String getMethod()
			{
			return "";
			}
		public java.lang.String getPathTranslated()
			{
			return "";
			}
		public java.lang.String getQueryString()
			{
			return null;
			}
		public java.lang.String getRemoteUser()
			{
			return null;
			}
		public java.lang.String getRequestedSessionId()
			{
			return null;
			}
		public java.lang.String getServletPath()
			{
			return null;
			}
		public javax.servlet.http.HttpSession getSession()
			{
			return null;
			}
		public boolean isRequestedSessionIdFromUrl()
			{
			return false;
			}
		public javax.servlet.http.HttpSession getSession(boolean A)
			{
			return null;
			}
		public java.security.Principal getUserPrincipal()
			{
			return null;
			}
		public boolean isRequestedSessionIdFromCookie()
			{
			return false;
			}
		public boolean isRequestedSessionIdFromURL()
			{
			return false;
			}
		public boolean isRequestedSessionIdValid()
			{
			return false;
			}
		public boolean isUserInRole(java.lang.String A)
			{
			return false;
			}
		public java.lang.Object getAttribute(java.lang.String A)
			{
			return null;
			}
		public java.util.Enumeration getAttributeNames()
			{
			return null;
			}
		public javax.servlet.ServletInputStream getInputStream()
			{
			return null;
			}
		public java.lang.String getCharacterEncoding()
			{
			return null;
			}
		public int getContentLength()
			{
			return 0;
			}
		public java.lang.String getContentType()
			{
			return "";
			}
		public java.util.Locale getLocale()
			{
			return null;
			}
		public java.util.Enumeration getLocales()
			{
			return new java.util.Enumeration() {
					public boolean hasMoreElements() {
						return false;
					}
					public Object nextElement() {
						return null;
					}
				};
			}
		public java.util.Enumeration getParameterNames()
			{
			return _parameters.keys();
			}
		public java.lang.String[] getParameterValues(java.lang.String A)
			{
			return new String[] { getParameter(A) };
			}
		public java.lang.String getProtocol()
			{
			return "http";
			}
		public java.io.BufferedReader getReader()
			{
			return null;
			}
		public java.lang.String getScheme()
			{
			return "";
			}
		public java.lang.String getRealPath(java.lang.String A)
			{
			return null;
			}
		public java.lang.String getRemoteAddr()
			{
			return "";
			}
		public java.lang.String getRemoteHost()
			{
			return "";
			}
		public javax.servlet.RequestDispatcher getRequestDispatcher(java.lang.String A)
			{
			return null;
			}
		public void removeAttribute(java.lang.String A)
			{
			return;
			}
		public java.lang.String getServerName()
			{
			return "servername";
			}
		public int getServerPort()
			{
			return 80;
			}
		public boolean isSecure()
			{
			return false;
			}
		public void setAttribute(java.lang.String A, java.lang.Object B)
			{
			}
		}


