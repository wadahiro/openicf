Identity Manager Installation Notes

1.) Copy the content of dist directory to WSHOME.
2.) Add the messages from message.xml to you own customMessageCatalog configuration
3.) Add the following value in the Custom Resources section of the Configure Managed Resources page
		com.waveset.adapter.CAServicedeskResourceAdapter

Connection Configuration

Required attributes:
	WSDL Location <- URL of the WSDL file
	Admin User
	Password

Optional:
	Connection URL <- If you want to alternate the endpoint in WSDL you can define other URL for webservice
	Policy

Schema Configuration

User schema accepts three different Resource User Attribute

Example 1:
	These are cnt user attributes, it can be write-able a read-able.
	userid  
	phone_number
	.
	.
	
Example 2:
	These attributes xx.xx can be only read-able. The adapter will ignore any modification on them.

	supervisor_contact_uuid.userid
	supervisor_contact_uuid.first_name
	supervisor_contact_uuid.last_name
	
	
Example 3:
	These attributes in this format objectType:cntProperty.referencedObjectProperty are read-able and write-able 

	cnt:supervisor_contact_uuid.userid
	
	This attribute will be translated to query -> select id from cnt where userid = $(attributeValue).
	If this query returns not 1 result the adapter will throw an error. It it returns with only one result then 
	the supervisor_contact_uuid will be equal with the handle of the returned object.
	
	IMPORTANT!!!
	
	If You specify attributes like this, MAKE the last two "Read Only"!!!
	
	cnt:supervisor_contact_uuid.userid
	cnt:supervisor_contact_uuid.first_name
	cnt:supervisor_contact_uuid.last_name
	
	
Errors

If there is any error or strange behaviour then set the Trace on to this adapter and you will find the problem.
Level1: It will trace every method invocation and catch almost every unexpected error.
Level2: It will trace every SOAP Exception thrown by CA Webservice
Level3: It will trace every value before and after the webservice call
Level4: There is no TRACE specified for this level. 	