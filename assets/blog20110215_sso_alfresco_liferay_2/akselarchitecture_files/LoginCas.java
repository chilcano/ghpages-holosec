/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package no.ren.alfresco;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import no.ren.util.RENProperties;

import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.springframework.extensions.webscripts.*;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;


/**
 * Login via a CAS proxy ticket.
 * 
 */
public class LoginCas extends DeclarativeWebScript {
    // dependencies
    private AuthenticationService authenticationService;
	private AuthenticationComponent authenticationComponent;
    
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
	public void setAuthenticationComponent(AuthenticationComponent authenticationComponent) {
		this.authenticationComponent = authenticationComponent;
	}
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
        // extract username
        String username = req.getParameter("u");
        if (username == null || username.length() == 0) {
            throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, "Username not specified");
        }
        // extract CAS ticket
        String ticket = req.getParameter("t");
        if (ticket == null) {
            throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, "Ticket not specified");
        }

        try {
            // add ticket to model for javascript and template access
            Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
        	
    		// validate our proxy CAS ticket
    		Cas20ProxyTicketValidator tv = new Cas20ProxyTicketValidator(RENProperties.getProperty("cas_url"));
    		tv.setAcceptAnyProxy(true);
    		String legacyServerServiceUrl = RENProperties.getProperty("server_url") + "/alfresco";
				Assertion assertion = tv.validate(ticket, legacyServerServiceUrl);
				String cas_username = assertion.getPrincipal().getName();
			
			// compare usernames
			if(!username.equals(assertion.getPrincipal().getName())) {
				throw new TicketValidationException("usernames does not match: "+username+"/"+cas_username);
			}
			
			// authenticate our user
    		authenticationComponent.setCurrentUser(username);
    		
    		// create a new alfresco ticket
    		String alfticket = authenticationService.getCurrentTicket();
    		model.put("ticket",  alfticket);
            return model;
            
        } catch(AuthenticationException e) {
            throw new WebScriptException(HttpServletResponse.SC_FORBIDDEN, "Login failed");
        } catch (TicketValidationException ex) {
    		ex.printStackTrace();
        } finally {
            authenticationService.clearCurrentSecurityContext();
        }
        return null;
    }
}