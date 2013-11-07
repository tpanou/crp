package org.teiath.webservices.controllers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteSearchCriteria;
import org.teiath.service.crp.SearchRoutesService;
import org.teiath.service.crp.ViewRouteService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.webservices.model.ServiceRoute;
import org.teiath.webservices.model.ServiceRouteList;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@Controller
public class RoutesController {

	@Autowired
	private SearchRoutesService searchRoutesService;
	@Autowired
	private ViewRouteService viewRouteService;

	private static final Logger logger_c = Logger.getLogger(RoutesController.class);

	@RequestMapping(value = "/services/routes", method = RequestMethod.GET)
	public ServiceRouteList searchRoutes(String dFrom, String dTo, String tFrom, String tTo, Boolean avail,
	                                     Integer seats, Boolean obj, Double contr, Boolean smoke, String tags) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat time = new SimpleDateFormat("HH:mm");

		ServiceRouteList serviceRouteList = new ServiceRouteList();
		serviceRouteList.setServiceRoutes(new ArrayList<ServiceRoute>());
		ServiceRoute serviceRoute = null;

		RouteSearchCriteria criteria = new RouteSearchCriteria();
		try {
			criteria.setDateFrom(dFrom != null ? sdf.parse(dFrom) : null);
			criteria.setDateTo(dTo != null ? sdf.parse(dTo) : null);
			criteria.setTimeFrom(tFrom != null ? time.parse(tFrom) : null);
			criteria.setTimeTo(tTo != null ? time.parse(tTo) : null);
			criteria.setAvailability(avail != null ? avail ? 1 : 0 : null);
			criteria.setPeopleNumber(seats);
			criteria.setObjectTransportAllowed(obj != null ? obj ? 1 : 0 : null);
			criteria.setMaxAmount(contr != null ? new BigDecimal(contr) : null);
			criteria.setSmokingAllowed(smoke != null ? smoke ? 1 : 0 : null);
			criteria.setTags(tags);

			criteria.setPageNumber(0);
			criteria.setPageSize(Integer.MAX_VALUE);

			SearchResults<Route> results = searchRoutesService.searchRoutes(criteria, false);

			for (Route route : results.getData()) {
				serviceRoute = new ServiceRoute();
				serviceRoute.setStartingPoint(route.getStartingPoint());
				serviceRoute.setDestinationPoint(route.getDestinationPoint());
				serviceRoute.setRouteDate(route.getRouteDate());
				serviceRoute.setRouteTime(route.getRouteTime());
				serviceRoute.setFlexible(route.isFlexible());
				serviceRoute.setTotalSeats(route.getTotalSeats());
				serviceRoute.setObjectTransportAllowed(route.isObjectTransportAllowed());
				serviceRoute.setSmokingAllowed(route.isSmokingAllowed());
				serviceRoute.setContributionAmount(route.getContributionAmount());
				serviceRoute.setTags(route.getTags());
				serviceRoute.setComment(route.getComment());
				serviceRoute.setRecurring(route.isRecurring());
				serviceRoute.setDays(route.getDays());
				serviceRoute.setRouteCreationDate(route.getRouteCreationDate());
				serviceRoute.setRouteEndDate(route.getRouteEndDate());
				serviceRoute.setDriverName(route.getUser().getFullName());
				serviceRoute.setVehicleName(route.getVehicle().getFullName());
				serviceRoute.setCode(route.getCode());

				serviceRouteList.getServiceRoutes().add(serviceRoute);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		logger_c.debug("Returing Routes: " + serviceRoute);
		return serviceRouteList;
	}

	@RequestMapping(value = "/services/route", method = RequestMethod.GET)
	public ServiceRoute searchRouteByCode(String code) {

		ServiceRoute serviceRoute = new ServiceRoute();
		try {

			Route route = viewRouteService.getRouteByCode(code);
			serviceRoute.setStartingPoint(route.getStartingPoint());
			serviceRoute.setDestinationPoint(route.getDestinationPoint());
			serviceRoute.setRouteDate(route.getRouteDate());
			serviceRoute.setRouteTime(route.getRouteTime());
			serviceRoute.setFlexible(route.isFlexible());
			serviceRoute.setTotalSeats(route.getTotalSeats());
			serviceRoute.setObjectTransportAllowed(route.isObjectTransportAllowed());
			serviceRoute.setSmokingAllowed(route.isSmokingAllowed());
			serviceRoute.setContributionAmount(route.getContributionAmount());
			serviceRoute.setTags(route.getTags());
			serviceRoute.setComment(route.getComment());
			serviceRoute.setRecurring(route.isRecurring());
			serviceRoute.setDays(route.getDays());
			serviceRoute.setRouteCreationDate(route.getRouteCreationDate());
			serviceRoute.setRouteEndDate(route.getRouteEndDate());
			serviceRoute.setDriverName(route.getUser().getFullName());
			serviceRoute.setVehicleName(route.getVehicle().getFullName());
			serviceRoute.setCode(route.getCode());
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		logger_c.debug("Returing Route: " + serviceRoute);

		return serviceRoute;
	}
}
