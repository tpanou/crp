package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.*;
import org.teiath.data.domain.Notification;
import org.teiath.data.domain.SmsMessage;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.data.email.IMailManager;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.properties.EmailProperties;
import org.teiath.data.properties.NotificationProperties;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.data.sms.SmsManager;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.sys.SysParameterService;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

@Service("listRouteInboxInterestsService")
@Transactional
public class ListRouteInboxInterestsServiceImpl
		implements ListRouteInboxInterestsService {

	@Autowired
	RouteInterestDAO routeInterestDAO;
	@Autowired
	RouteAssessmentDAO routeAssessmentDAO;
	@Autowired
	NotificationDAO notificationDAO;
	@Autowired
	RouteDAO routeDAO;
	@Autowired
	UserDAO userDAO;
	@Autowired
	VehicleDAO vehicleDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;
	@Autowired
	private NotificationProperties notificationProperties;
	@Autowired
	private SmsManager smsManager;
	@Autowired
	private SysParameterService sysParameterService;

	@Override
	public SearchResults<RouteInterest> searchRouteInterestsByCriteria(RouteInterestSearchCriteria criteria)
			throws ServiceException {
		SearchResults<RouteInterest> results;

		try {
			results = routeInterestDAO.search(criteria);
			for (RouteInterest routeInterest : results.getData()) {
				routeInterest.getUser().setAveragePassengerRating(
						routeAssessmentDAO.getPassengerAverageRating(routeInterest.getUser()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}

	@Override
	public void rejectRouteInterests(Route route)
			throws ServiceException {
		try {
			RouteInterestSearchCriteria routeInterestSearchCriteria = new RouteInterestSearchCriteria();
			routeInterestSearchCriteria.setRoute(route);
			SearchResults<RouteInterest> results;
			results = routeInterestDAO.search(routeInterestSearchCriteria);
			Collection<RouteInterest> routeInterests = results.getData();

			for (RouteInterest interest : routeInterests) {
				interest.setStatus(RouteInterest.STATUS_REJECTED);
				routeInterestDAO.save(interest);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public void approveRouteInterest(RouteInterest routeInterest)
			throws ServiceException {
		try {

			routeInterest.setStatus(RouteInterest.STATUS_APPROVED);
			routeInterestDAO.save(routeInterest);

			Notification notification = new Notification();
			notification.setUser(routeInterest.getUser());
			notification.setRoute(routeInterest.getRoute());
			notification.setType(Notification.TYPE_ROUTES);
			notification.setSentDate(new Date());
			notification.setTitle("Αποδοχή εκδήλωσης ενδιαφέροντος");
			String body = notificationProperties.getNotificationApproveBody()
					.replace("$1", routeInterest.getRoute().getUser().getFullName()).replace("$2", "διαδρομή");
			notification.setBody(body);
			notificationDAO.save(notification);

			if (routeInterest.getUser().isEmailNotifications()) {
				//Create and send Email
				String mailSubject = emailProperties.getRouteInterestApproveSubject()
						.replace("$1", "[Υπηρεσία εύρεσης Διαδρομών]:").replace("$2", "διαδρομής");
				StringBuilder htmlMessageBuiler = new StringBuilder();
				htmlMessageBuiler.append("<html> <body>");
				String mailBody = emailProperties.getRouteInterestApproveBody()
						.replace("$1", routeInterest.getRoute().getUser().getFullName()).replace("$2", "διαδρομή");
				htmlMessageBuiler.append(mailBody);
				htmlMessageBuiler.append("<br>");
				htmlMessageBuiler.append("<br>Ημερομηνία: " + new SimpleDateFormat("dd/MM/yyyy")
						.format(routeInterest.getRoute().getRouteDate()));
				htmlMessageBuiler.append("<br>Ώρα: " + new SimpleDateFormat("HH:mm")
						.format(routeInterest.getRoute().getRouteTime()));
				htmlMessageBuiler
						.append("<br>Ονοματεπώνυμο οδηγού: " + routeInterest.getRoute().getUser().getFullName());
				htmlMessageBuiler.append("<br>Αφετηρία: " + routeInterest.getRoute().getStartingPoint());
				htmlMessageBuiler.append("<br>Προορισμός: " + routeInterest.getRoute().getDestinationPoint());
				htmlMessageBuiler.append("<br>Όχημα: " + routeInterest.getRoute().getVehicle().getFullName());
				htmlMessageBuiler.append("</body> </html>");
				mailManager.sendMail(emailProperties.getFromAddress(), routeInterest.getUser().getEmail(), mailSubject,
						htmlMessageBuiler.toString());
			}

			if (routeInterest.getUser().isSmsNotifications() && sysParameterService.fetchSystemParameters().isSmsEnabledApproveInterest()) {
				smsManager.sendSMS(routeInterest.getUser().getMobileNumber().toString(),
						smsManager.fetchMessage(SmsMessage.ROUTE_INTEREST_APPROVE)
								.replace("$1", routeInterest.getRoute().getUser().getFullName()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public void rejectRouteInterest(RouteInterest routeInterest)
			throws ServiceException {
		try {
			routeInterest.setStatus(RouteInterest.STATUS_REJECTED);
			routeInterestDAO.save(routeInterest);

			Notification notification = new Notification();
			notification.setUser(routeInterest.getUser());
			notification.setRoute(routeInterest.getRoute());
			notification.setSentDate(new Date());
			notification.setTitle("Απορριψη εκδήλωσης ενδιαφέροντος");
			String body = notificationProperties.getNotificationRejectBody()
					.replace("$1", routeInterest.getRoute().getUser().getFullName()).replace("$2", "διαδρομή");
			notification.setBody(body);
			notificationDAO.save(notification);

			String mailSubject = emailProperties.getRouteInterestRejectSubject()
					.replace("$1", "[Υπηρεσία εύρεσης Διαδρομών]:").replace("$2", "διαδρομής");
			//TODO links: currently only content
			String link = "";
			String mailBody = emailProperties.getRouteInterestRejectBody()
					.replace("$1", routeInterest.getRoute().getUser().getFullName()).replace("$2", "διαδρομή")
					.replace("$3", link);
			mailManager.sendMail(emailProperties.getFromAddress(), routeInterest.getUser().getEmail(), mailSubject, mailBody);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public Collection<Vehicle> getVehiclesByPassenger(User user)
			throws ServiceException {
		Collection<Vehicle> vehicles;

		try {
			vehicles = vehicleDAO.findByPassenger(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return vehicles;
	}

	@Override
	public Route getRouteById(Integer routeId)
			throws ServiceException {
		Route route;
		try {
			route = routeDAO.findById(routeId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return route;
	}
}
