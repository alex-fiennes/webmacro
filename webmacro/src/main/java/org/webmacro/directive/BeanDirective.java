/*
 * Copyright (C) 1998-2001 Semiotek Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of either of the following
 * Open Source licenses:
 *
 * The GNU General Public License, version 2, or any later version, as
 * published by the Free Software Foundation
 * (http://www.fsf.org/copyleft/gpl.html);
 *
 *  or
 *
 * The Semiotek Public License (http://webmacro.org/LICENSE.)
 *
 * This software is provided "as is", with NO WARRANTY, not even the
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See www.webmacro.org for more information on the WebMacro project.
 */

package org.webmacro.directive;

/**
 * This directive allows the instantiation of objects in WMScript.
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webmacro.Broker;
import org.webmacro.Context;
import org.webmacro.FastWriter;
import org.webmacro.Macro;
import org.webmacro.PropertyException;
import org.webmacro.TemplateVisitor;
import org.webmacro.WebMacroException;
import org.webmacro.engine.Block;
import org.webmacro.engine.BuildContext;
import org.webmacro.engine.BuildException;
import org.webmacro.engine.Builder;
import org.webmacro.engine.UndefinedMacro;
import org.webmacro.engine.Variable;
import org.webmacro.util.Instantiator;

/**
 * Implements a directive allowing a script writer 
 * to instantiate an arbitrary
 * java object in a WebMacro template subject to 
 * security restrictions.
 */

public class BeanDirective extends Directive
{

	   static Logger _log =  LoggerFactory.getLogger(BeanDirective.class);
   
   private static final String APP_BEANS_KEY = "org.webmacro.directive.BeanDirective.appBeans";

   private static final int BEAN_TARGET = 1;

   private static final int BEAN_CLASS_NAME = 2;

   private static final int BEAN_SCOPE = 3;

   private static final int BEAN_SCOPE_GLOBAL = 4;

   private static final int BEAN_SCOPE_APPLICATION = 5;

   private static final int BEAN_SCOPE_SESSION = 6;

   private static final int BEAN_SCOPE_PAGE = 7;

   private static final int BEAN_INITARGS = 8;

   private static final int BEAN_INITARGS_VAL = 9;

   private static final int BEAN_TYPE_STATIC = 10;

   private static final int BEAN_ON_NEW = 11;

   private static final int BEAN_ON_NEW_BLOCK = 12;

   private static final UndefinedMacro UNDEF = UndefinedMacro.getInstance();

   private static int[] BEAN_SCOPES =
   { BEAN_SCOPE_GLOBAL, BEAN_SCOPE_APPLICATION, BEAN_SCOPE_SESSION,
            BEAN_SCOPE_PAGE };

   static Map<String, Object> globalBeans = new HashMap<String, Object>(20);

   private Variable target;

   private String targetName;

   private String _className;

   private int scope;

   private boolean isStaticClass;

   private Object initArgObj;

   private Object[] initArgs;

   private Block onNewBlock;

   private Broker _broker;

   private static final ArgDescriptor[] myArgs = new ArgDescriptor[]
   { new LValueArg(BEAN_TARGET), new AssignmentArg(),
            new QuotedStringArg(BEAN_CLASS_NAME), new OptionalGroup(3),
            new KeywordArg(BEAN_SCOPE, "scope"), new AssignmentArg(),
            new SingleOptionChoice(5), new OptionalGroup(1),
            new KeywordArg(BEAN_SCOPE_GLOBAL, "global"), new OptionalGroup(1),
            new KeywordArg(BEAN_SCOPE_APPLICATION, "application"),
            new OptionalGroup(1),
            new KeywordArg(BEAN_SCOPE_SESSION, "session"),
            new OptionalGroup(1), new KeywordArg(BEAN_SCOPE_PAGE, "page"),
            new OptionalGroup(1), new KeywordArg(BEAN_TYPE_STATIC, "static"),
            new OptionalGroup(2), new KeywordArg(BEAN_INITARGS, "initArgs"),
            new RValueArg(BEAN_INITARGS_VAL), new OptionalGroup(2),
            new KeywordArg(BEAN_ON_NEW, "onNew"),
            new BlockArg(BEAN_ON_NEW_BLOCK) };

   private static final DirectiveDescriptor myDescr = new DirectiveDescriptor(
            "bean", null, myArgs, null);

   public static DirectiveDescriptor getDescriptor()
   {
      return myDescr;
   }

   public BeanDirective()
   {
   }

   @Override
  public Object build(DirectiveBuilder builder, BuildContext bc)
            throws BuildException
   {
      _broker = bc.getBroker();
      // appBeans map is created by the init method when this directive
      // is registered by the DirectiveProvider
      try
      {
         target = (Variable) builder.getArg(BEAN_TARGET, bc);
      }
      catch (ClassCastException e)
      {
         throw new NotVariableBuildException(myDescr.name, e);
      }
      targetName = target.getName();
      _className = (String) builder.getArg(BEAN_CLASS_NAME, bc);
      classForName(_className);

      // check if bean is declared as static
      // this implies global scope and no constructor invocation
      isStaticClass = (builder.getArg(BEAN_TYPE_STATIC) != null);
      if (isStaticClass)
         scope = BEAN_SCOPE_GLOBAL;
      else
      {
         scope = getScope(builder, bc);
         // initArgs is only valid for non-static beans
         initArgObj = builder.getArg(BEAN_INITARGS_VAL);
         if (initArgObj instanceof Builder)
            initArgObj = ((Builder) initArgObj).build(bc);
      }
      onNewBlock = (Block) builder.getArg(BEAN_ON_NEW_BLOCK, bc);

      _log.debug("BeanDirective, target=" + target + ", className="
               + _className + ", scope=" + scope + ", isStaticClass="
               + isStaticClass + ", initArgs=" + initArgObj);
      return this;
   }

   @SuppressWarnings("unchecked")
  public void write(FastWriter out, Context context) throws PropertyException,
            IOException
   {
      Map<Object,Object> appBeans = (Map<Object,Object>) _broker.getBrokerLocal(APP_BEANS_KEY);

      // = beanConf.appBeans;
      boolean isNew = false;

      try
      {
         while (initArgObj instanceof Macro && initArgObj != UNDEF)
            initArgObj = ((Macro) initArgObj).evaluate(context);

         // store init args in array
         if (initArgObj == null || initArgObj.getClass().isArray())
         {
            initArgs = (Object[]) initArgObj;
         }
         else
         {
            initArgs = new Object[]
            { initArgObj };
         }

         Object o = null;
         Class<?> c = null;
         switch (scope)
         {
            case BEAN_SCOPE_GLOBAL:
               synchronized (globalBeans)
               {
                  o = globalBeans.get(targetName);
                  if (o == null)
                  {
                     if (isStaticClass)
                     {
                        c = context.getBroker().classForName(_className);
                        o = new org.webmacro.engine.StaticClassWrapper(c);
                     }
                     else
                     {
                        // c = Class.forName(_className);
                        // o = c.newInstance();
                        o = instantiate(_className, initArgs);
                     }
                     isNew = true;
                     globalBeans.put(targetName, o);
                  }
               }
               break;

            case BEAN_SCOPE_APPLICATION:
               synchronized (appBeans)
               {
                  o = appBeans.get(targetName);
                  if (o == null)
                  {
                     o = instantiate(_className, initArgs);
                     isNew = true;
                     appBeans.put(targetName, o);
                  }
               }
               break;

            case BEAN_SCOPE_SESSION:
               javax.servlet.http.HttpSession session = (javax.servlet.http.HttpSession) context
                        .getProperty("Session");
               // if (context instanceof WebContext){
               if (session != null)
               {
                  synchronized (session)
                  {
                     o = session.getAttribute(targetName);
                     if (o == null)
                     {
                        o = instantiate(_className, initArgs);
                        isNew = true;
                        session.setAttribute(targetName, o);
                     }
                  }
               }
               else
               {
                  PropertyException e = new PropertyException(
                           "#bean usage error: session scope is only valid with servlets!");
                  _broker.getEvaluationExceptionHandler().evaluate(target,
                           context, e);
               }
               break;
            default:
               // make "page" the default scope
               // case BEAN_SCOPE_PAGE:
               // NOTE: page beans always overwrite anything in the context
               // with the same name
               o = instantiate(_className, initArgs);
               isNew = true;
               if (o != null)
               {
                  Class<?>[] paramTypes =
                  { Context.class };
                  try
                  {
                     java.lang.reflect.Method m = o.getClass().getMethod(
                              "init", paramTypes);
                     if (m != null)
                     {
                        Object[] args =
                        { context };
                        m.invoke(o, args);
                     }
                  }
                  catch (Exception e)
                  { // ignore
                  }
               }
               break;
         }

         _log.debug("BeanDirective: Class " + _className + " loaded.");
         target.setValue(context, o);
      }
      catch (PropertyException e)
      {
         this._broker.getEvaluationExceptionHandler().evaluate(target, context,
                  e);
      }
      catch (Exception e)
      {
         String errorText = "BeanDirective: Unable to load bean " + target
                  + " of type " + _className;
         writeWarning(errorText, context, out);
      }
      if (isNew && onNewBlock != null)
         onNewBlock.write(out, context);

   }

   @Override
  public void accept(TemplateVisitor v)
   {
      v.beginDirective(myDescr.name);
      v.visitDirectiveArg("BeanTarget", target);
      v.visitDirectiveArg("BeanClass", _className);
      v.visitDirectiveArg("BeanScope", new Integer(scope));
      v.visitDirectiveArg("BeanIsStatic", new Boolean(isStaticClass));
      v.visitDirectiveArg("BeanInitArgs", initArgs);
      v.endDirective();
   }

   private Object instantiate(String className, Object[] args) throws Exception
   {
      Class<?> c = classForName(className);
      return instantiate(c, args);
   }

   private Object instantiate(Class<?> c, Object[] args) throws Exception
   {
      return Instantiator.getInstance(_broker).instantiate(c, args);
   }

   private static int getScope(DirectiveBuilder builder, BuildContext bc)
            throws org.webmacro.engine.BuildException
   {
      int scope = -1;

      for (int i = 0; i < BEAN_SCOPES.length; i++)
      {
         scope = BEAN_SCOPES[i];
         if (builder.getArg(scope) != null)
            break;
      }
      return scope;
   }

   public static void init(Broker b)
   {
      // get configuration parameters
      synchronized (b)
      {
         b.setBrokerLocal(APP_BEANS_KEY, new HashMap<Object,Object>(20));
      }
   }

   private Class<?> classForName(String className) throws BuildException
   {

      Class<?> c;
      try
      {
         c = Instantiator.getInstance(_broker).classForName(className);
      }
      catch (WebMacroException e)
      {
         throw new BuildException("BeanDirective failed to load class \""
                  + className + "\"", e);
      }
      return c;
   }
}
