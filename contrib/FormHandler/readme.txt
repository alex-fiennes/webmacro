
FormHandler - Version 0.7 - EARLY PREVIEW VERSION
===========================

The formhandler package is ment to ease the hassle in developing an interactive web interface. It can be thought of as a transparent web interface for the modification of an object, your subclass of FormHandler.

An object has information and actions. Likewise your  FormHandler object has public variables (persistent information) and it has doActions (actions, specifically methods that start with 'do'). 

I do not consider this release quality. I had most of this working about 8 months ago, but school got in the way. This summer ('01) I am, amazingly, not taking any classes. In my spare time over the past 2 weeks most parts of the code has been re-implemented, and many core features changed. I am sure many other changes will be made as we discover better ways of interaction. 

What this means: For the moment I will not hesitate to break any backwards compatibility. I personally think the code is VERY usable in a real word project. However, I don't believe it has matured enough to call the structure final. 

To learn more look at the included examples in the examples directory. Also, Javadocs exist for most objects, and if I do say so myself, are quite informative. (Please tell me where this is not true!)

Also, see the To Do List section of the document for changes I am thinking about implementing.

Note: This software uses the WebMacro (http://www.webmacro.org) templating engine originally developed by Justin Wells and Semiotek Inc.


FormHandler - History
===========================

The FormHandler package started with this purpose: "To have an object encapsulate all the information necessary for the successful completion of a multi-page form." The other use I had for it was: "To have one object encapsulate the entire web interface for editing and object, similar to how JavaBeans can provide a GUI editing interface."

At that time there were two backend object for every form: A FormInfo (Information) object which contained all of the needed information and verification of that information, and the FormHandler (Behavior) object which took care of setting variables on the FormInfo, and calling it's validate method. The validate method would simply return the next template in the multipage form, or the last one if some submitted value was not right. 

Validator objects were at that time mutable. The first incarnation of doAction's worked, but they could not accept parameters. That was all working around November 2000, but school got in the way until now, June 2001. 

A lot has changed since then. The FormHandler and FormInfo objects were merged into that same object. (Think of it like this: the FormInfo part is the code you write, and the FormHandler part is what you inherit from.) Validators are now immutable objects which are supported by a "ValidationFilter" object. (THIS IS YET TO BE COMPLETED) Introspection is now done by WebMacro's introspection engine. DoAction's can accept multiple values (thanks WM!) and can return a custom view (or an error view).




FormHandler - Example
===========================

Because an example speaks more understanding than words, take the following FormHandler:

class MyFormHandler extends FormHandler {

public String myFirstName;
public String myLastName;

public Object doDisplayCat(String midInitial) {
	c.put("concatenated", myFirstName+" "+midInitial+" "+myLastName);
	return "templateCat.wm";
	}

public Object postvalidate(WebContext c) {
	return "defaultTemplate.wm";
	}

}

If a user submits a request with the form parameter "myFirstName" set to "George", and the request is passed to this FormHandler, then the pubic myFirstName variable will be set to George. In the same manner is a form parameter named "_doDisplayCat(middleInitial)" exists, then the doDisplayCat method will be called with whatever the value of the form parameter "middleInitial" is. 

Because the doDisplayCat() method returns a non-null value, defaultTemplate.wm will be returned to the user. If it returned null, the last method that will be called is the validate method, which should always return a value.

SEE MORE EXAMPLES IN THE examples DIRECTORY!


FormHandler - Issues that need to be addressed:
=================================================
1. Currently Validator's throw exceptions if they want to return a reason why the string was not valid. For performance reasons should we change this?




FormHandler - To Do List:
===========================


Top (easy) priorities:
----------------------
#. Change all validator's to immutable objects that do not hold any value, simply have a validate(String input):boolean method. No constructors available, must use the getInstance() methods?

#. Create a ignore-empty-value validator wrapper, useful to wrap say an optional email address. (Email validator would complain of no value cuz its not a valid email address...)

#. Add the optional passing of the WebContext as the first parameter to doAction methods.

#. Create a FormHandler base servlet subclasses from WMServlet...?



Major additions:
----------------
#. Add a "parameter validator filtering" object. 
 - It will contain a hastable to handle the validation of all parameters as they come in. Key: paramName Value: Validator.validate(String):boolean
 - ? Hold whether to set the variable w/ an invalid value or not. 
 - It may also contain a post-parameter-setting method to do multi parameter validation.
 - Need to think about its interaction with error handling.


#. Error handling interface - This needs more thought!
 - Let the "Should-I-Pass-On-This-Exception" method be in this interface! Upon reportException(e):void is can rethrow, or throw a new exception. Can only throw an exception of type RethrownException.
 - This involves keeping a list of all error that occurred, which errors were related to specific parameters, which error came from doActions, and what to do in all of these cases.
 - ? public error reporting methods call internal methods like addError(msg) and setErrorPrefixAndPostfix(fieldName)



Need more thought (lower priority):
-----------------------------------
With the following FormHandler type object you need to determine whether to subclass or to make them wrapper objects (this would imply making FormHandler an interface).

#.SequenceHandler - Used for multipage forms. Supports functions like Back, Next, etc.
 - Will need some way to group together form requirements. 
#.StaticHandler - validate method always returns one template.
#.RequestedViewHandler - validate method returns the template requested by a form parameter, or a default one if no other view is specified. Can check that the requested template is in a list of valid can-be-requested templates.


Always in the back of my mind (lowest priority):
------------------------------------------------

#. The whole having to have the FormHandlers clonable when using the HandlerAccessorMap is not very flexible. Maybe add a lifecycle to handlers? Say an init method, maybe a destroy method?
 - At the moment I say no to my own idea. It is easy enough to write your own HandlerAccessor, and they can write their own finilize method... we don't specifically control when they are GCed;
