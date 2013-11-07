package org.teiath.web.vm.social;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.Route;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.social.FacebookShareService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;

@SuppressWarnings("UnusedDeclaration")
public class FacebookShareRouteVM
		extends BaseVM {

	static Logger log = Logger.getLogger(FacebookShareRouteVM.class.getName());

	@WireVariable
	private FacebookShareService facebookShareService;

	private String post;
	private String token;

	private Route route;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {

		token = Executions.getCurrent().getParameter("token");

		try {
			StringBuilder string = new StringBuilder(Executions.getCurrent().getParameter("state"));
			string.delete(string.indexOf("?"), string.length());
			route = facebookShareService.getRouteById(Integer.parseInt(string.toString()));
		} catch (ServiceException e) {
			log.error(e.getMessage());
		}

		if (route != null) {
			//TODO post content
			post = "Υπηρεσία Εύρεσης Συνεπιβατών Τ.Ε.Ι Αθήνας - Νεα διαδρομή: "+route.getStartingPoint()+" - "+route.getDestinationPoint()+" Κωδικός διαδρομής: "+route.getCode();
		}
	}

	@Command
	public void onPost() {
		//Facebook Post

		FacebookClient facebookClient = new DefaultFacebookClient(token);
		if (route != null) {

			FacebookType publishMessageResponse = facebookClient
					.publish("me/feed", FacebookType.class, Parameter.with("message", post));
			Messagebox.show(MessageBuilder
					.buildErrorMessage("Το μήνυμα δημοσιεύτηκε επιτυχώς στο προφίλ σας", "Facebook"), "Social Networks",
					Messagebox.OK, Messagebox.INFORMATION);
			ZKSession.sendRedirect(PageURL.FACEBOOK_SUCCESS);
		}
	}

	@Command
	public void onCancel() {
		ZKSession.sendRedirect(PageURL.ROUTE_LIST);
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}
}
