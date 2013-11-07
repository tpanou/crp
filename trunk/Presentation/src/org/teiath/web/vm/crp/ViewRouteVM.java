package org.teiath.web.vm.crp;

import org.apache.log4j.Logger;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.domain.crp.RoutePoint;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.crp.ViewRouteService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Vbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class ViewRouteVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewRouteVM.class.getName());

	@Wire("#days")
	private Label days;
	@Wire("#flexibleLabel")
	private Label flexibleLabel;
	@Wire("#ameaAccessibleLabel")
	private Label ameaAccessibleLabel;
	@Wire("#objectAllowedLabel")
	private Label objectAllowedLabel;
	@Wire("#smokingAllowedLabel")
	private Label smokingAllowedLabel;
	@Wire("#inquiryButton")
	private Button inquiryButton;
	@Wire("#reservationList")
	private Vbox reservationList;
	@Wire("#stopsList")
	private Vbox stopsList;
	@Wire("#vehicleTypeLabel")
	private Label vehicleTypeLabel;

	@WireVariable
	private ViewRouteService viewRouteService;

	private Route route;

	private RouteInterest routeInterest;
	private RouteInterestSearchCriteria routeInterestSearchCriteria;

	@AfterCompose
	@NotifyChange("route")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		try {
			if (Executions.getCurrent().getParameter("code") != null) {
				route = viewRouteService.getRouteByCode(Executions.getCurrent().getParameter("code"));
			} else if (ZKSession.getAttribute("routeCode") != null)
				route = viewRouteService.getRouteByCode((String) ZKSession.getAttribute("routeCode"));
			else {
				route = viewRouteService.getRouteById((Integer) ZKSession.getAttribute("routeId"));
			}

			if (route.getVehicle().getType() == Vehicle.CAR)
				vehicleTypeLabel.setValue(Labels.getLabel("vehicle.car"));
			else
				vehicleTypeLabel.setValue(Labels.getLabel("vehicle.motorcycle"));

			if (loggedUser.getId() == route.getUser().getId()) {
				inquiryButton.setVisible(false);
			}
			if (route.isRecurring()) {
				days.setValue("Κάθε " + route.getDays());
			}
			if (route.isFlexible()) {
				flexibleLabel.setValue(Labels.getLabel("common.yes"));
			} else {
				flexibleLabel.setValue(Labels.getLabel("common.no"));
			}
			if (route.getAmeaAccessible()) {
				ameaAccessibleLabel.setValue(Labels.getLabel("common.yes"));
			}
			if (route.isObjectTransportAllowed()) {
				objectAllowedLabel.setValue(Labels.getLabel("common.yes"));
			} else {
				objectAllowedLabel.setValue(Labels.getLabel("common.no"));
			}
			if (route.isSmokingAllowed()) {
				smokingAllowedLabel.setValue(Labels.getLabel("common.yes"));
			} else {
				smokingAllowedLabel.setValue(Labels.getLabel("common.no"));
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
		}

		routeInterestSearchCriteria = new RouteInterestSearchCriteria();
		routeInterestSearchCriteria.setRoute(route);
		//2: Pending Reservations
		routeInterestSearchCriteria.setStatus(RouteInterest.STATUS_PENDING);

		if (! route.getRouteInterests().isEmpty()) {
			for (RouteInterest r : route.getRouteInterests()) {
				reservationList.appendChild(new Label(r.getUser().getLastName() + " " + r.getUser().getFirstName()));
			}
		} else {
			reservationList.appendChild(new Label(Labels.getLabel("route.noPassengers")));
		}

		String path = "";
		List<String> stops = new ArrayList<>();

		Comparator<RoutePoint> comparator = new Comparator<RoutePoint>() {
			public int compare(RoutePoint c1, RoutePoint c2) {
				return Integer.valueOf(c1.getIndex()).compareTo(Integer.valueOf(c2.getIndex()));
			}
		};

		List<RoutePoint> routeList = new ArrayList<>(route.getRoutePoints());
		Collections.sort(routeList, comparator);

		for (RoutePoint p : routeList) {
			switch (p.getType()) {
				case RoutePoint.DEPARTURE:
					path += "(" + p.getAddress() + ")D|";
					break;
				case RoutePoint.ARRIVAL:
					path += "(" + p.getAddress() + ")A*";
					stops.add(p.getAddress());
					break;
				case RoutePoint.WAYPOINT:
					path += "(" + p.getLocation().getX() + ", " + p.getLocation().getY() + ")W|";
					break;
			}
		}

		if (! stops.isEmpty()) {
			for (int i = 0, j = stops.size() - 1; i < j; i++) {
				stopsList.appendChild(new Label((i + 1) + ". " + stops.get(i)));
			}
		} else {
			stopsList.appendChild(new Label(Labels.getLabel("route.noStops")));
		}

		if (! path.isEmpty()) {
			Clients.evalJavaScript("doPreview('" + path + "')");
		}
	}

	@Command
	public void onInquiry() {
		ZKSession.setAttribute("routeId", route.getId());
		ZKSession.sendRedirect(PageURL.ROUTE_INQUIRY);
	}

	@Command
	public void onBack() {
		if (ZKSession.getAttribute("fromNotification") != null) {
			ZKSession.removeAttribute("fromNotification");
			ZKSession.sendRedirect(PageURL.NOTIFICATIONS_LIST);
		} else if (ZKSession.getAttribute("fromUserAction") != null) {
			ZKSession.removeAttribute("fromUserAction");
			ZKSession.sendRedirect(PageURL.USER_ACTION_LIST);
		} else {
			ZKSession.sendRedirect(PageURL.ROUTE_LIST);
		}
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}
}
