package org.teiath.web.vm.user.places;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.UserPlace;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.values.CreateUserPlaceService;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

@SuppressWarnings("UnusedDeclaration")
public class CreateUserPlacePopupVM
		extends BaseVM {

	static Logger log = Logger.getLogger(CreateUserPlacePopupVM.class.getName());

	@WireVariable
	private CreateUserPlaceService createUserPlaceService;

	@Wire("#placeCreatePopupWin")
	private Window win;

	private UserPlace place;
	private User user;

	@AfterCompose
	@NotifyChange("place")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		place = new UserPlace();
		user = (User) Executions.getCurrent().getArg().get("USER");
	}

	@Command
	public void onSave() {
		try {
			place.setUser(user);
			createUserPlaceService.saveUserPlace(place);
			Messagebox.show(Labels.getLabel("place.message.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					BindUtils.postGlobalCommand(null, null, "receivePlace", null);
					win.detach();
				}
			});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("place.add")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	public void onCancel() {
		win.detach();
	}

	public UserPlace getPlace() {
		return place;
	}

	public void setPlace(UserPlace place) {
		this.place = place;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
