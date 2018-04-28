package com.services.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import create.NewUser;
 
@Path("/CreateNewUser")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class CreateNewUser {
 
@POST	
	public Response getMsg(@FormParam("username") String email, @FormParam("psw1") String password ) {
 
		String output =	"<!DOCTYPE html>"		
+ "<html>"
+ "<head>"
		+ "<meta charset=\"utf-8\" />"
		+ "<title>Create Account</title>"
		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"../styles.css\">"
		+ "</head>"
		+ "<body>"
			+ "<div class=\"form-style-5\">"
			+ "<p style=\"text-align: center;\">"
		+ "	<img src=\"../images/box.png\" height=\"82\">"
		+ "	</p>"
			+ "	<p style=\"text-align: center;\">"
		+ "	<img src=\"../images/ephesoft-logo.png \" height=\"82\">"
		+ "	</p>	"
				+ "<p>Account Successfully Created for: </p>"
				+ "<p>"+ email +"</p>"
				+ "<div align=\"center\"><a href=\"/dcma/home.html\">Login Here</a></div>"
			+ "</div>"
		+ "</body>"
		+ "</html>";
				
		System.out.println(email);
		//NewUser nu = new NewUser();
		//Create User Group LDAP
		NewUser.createNewUser(email, password);		
		return Response.status(200).entity(output).build();
	}
}