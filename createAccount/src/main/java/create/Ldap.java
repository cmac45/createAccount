package create;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.apache.log4j.Logger;
public class Ldap {

	private static applicationProperties prop = null;
	private static String ldapUrl =""; 
	private static String conntype ="";
	private static String AdminDn ="";
	private static String password ="";
	
	private static void intilizePropFile(){
		prop = new applicationProperties();
		ldapUrl = prop.getPropertyValue("LdapURL");
		conntype = prop.getPropertyValue("conntype");
		AdminDn = prop.getPropertyValue("AdminDn");
		password = prop.getPropertyValue("LDAPpassword");
	}

	public static void addRole(String userRole, String userName) throws NamingException {
		intilizePropFile();
		
		final Hashtable<Object, Object> env = new Hashtable<Object, Object>();
		DirContext dctx = null;
		try {
			
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, ldapUrl);
			env.put(Context.SECURITY_AUTHENTICATION, conntype);
			env.put(Context.SECURITY_PRINCIPAL, AdminDn);
			env.put(Context.SECURITY_CREDENTIALS, password);
			dctx = new InitialDirContext(env);
			// Create a container set of attributes
			final Attributes container = new BasicAttributes();

			// Create the objectclass to add
			final Attribute objClasses = new BasicAttribute("objectClass");
			objClasses.add("groupOfUniqueNames");

			// Assign the username, first name, and last name
			final Attribute commonName = new BasicAttribute("cn", userRole);
			final Attribute uniqueMember = new BasicAttribute("uniqueMember", "cn="+userName+",ou=people,dc=ephesoft,dc=com");

			
			// Add these to the container
			container.put(objClasses);
			container.put(commonName);
			
			container.put(uniqueMember);
			
			// Create the entry
			dctx.createSubcontext(getRoleDN(userRole), container);
		} finally {
			if (null != dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					System.out.println("Error in closing ldap " + e);
				}
			}
		}
	}

	private static String getRoleDN(final String userName) {
		String userDN = new StringBuffer().append("cn=").append(userName).append(",OU=groups,dc=ephesoft,dc=com").toString();
		System.out.println(userDN);
		return userDN;
	}
	
	
	public static void addUser(String NewUser, String userTextPassword) throws NamingException {
		intilizePropFile();
		final Hashtable<Object, Object> env = new Hashtable<Object, Object>();
		DirContext dctx = null;
		try {
			

			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, ldapUrl);
			env.put(Context.SECURITY_AUTHENTICATION, conntype);
			env.put(Context.SECURITY_PRINCIPAL, AdminDn);
			env.put(Context.SECURITY_CREDENTIALS, password);
			dctx = new InitialDirContext(env);
			// Create a container set of attributes
			final Attributes container = new BasicAttributes();

			// Create the objectclass to add
			final Attribute objClasses = new BasicAttribute("objectClass");
			objClasses.add("inetOrgPerson");

			// Assign the username, first name, and last name
			final Attribute commonName = new BasicAttribute("cn", NewUser);
			final Attribute email = new BasicAttribute("mail", NewUser);
			final Attribute givenName = new BasicAttribute("givenName", NewUser);
			final Attribute uid = new BasicAttribute("uid", NewUser);
			final Attribute surName = new BasicAttribute("sn", NewUser);

			// Add password
			final Attribute userPassword = new BasicAttribute("userpassword", userTextPassword);

			// Add these to the container
			container.put(objClasses);
			container.put(commonName);
			container.put(givenName);
			container.put(email);
			container.put(uid);
			container.put(surName);
			container.put(userPassword);

			// Create the entry
			dctx.createSubcontext(getUserDN(NewUser), container);
		} finally {
			if (null != dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					System.out.println("Error in closing ldap " + e);
				}
			}
		}
	}

	private static String getUserDN(final String userName) {
		String userDN = new StringBuffer().append("cn=").append(userName).append(",OU=people,dc=ephesoft,dc=com").toString();
		System.out.println(userDN);
		return userDN;
	}

}
