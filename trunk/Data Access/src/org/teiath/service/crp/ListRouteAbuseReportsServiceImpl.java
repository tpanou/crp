package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.RouteAbuseReportDAO;
import org.teiath.data.dao.UserDAO;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.RouteAbuseReport;
import org.teiath.data.email.IMailManager;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.properties.EmailProperties;
import org.teiath.data.search.RouteAbuseReportSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

@Service("listRouteAbuseReportsService")
@Transactional
public class ListRouteAbuseReportsServiceImpl
		implements ListRouteAbuseReportsService {

	@Autowired
	private RouteAbuseReportDAO routeAbuseReportDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;

	@Override
	public SearchResults<RouteAbuseReport> searchRouteAbuseReportsByCriteria(RouteAbuseReportSearchCriteria criteria)
			throws ServiceException {
		SearchResults<RouteAbuseReport> results;

		try {
			results = routeAbuseReportDAO.search(criteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
		return results;
	}

	@Override
	public void toggleBan(User user)
			throws ServiceException {
		try {
			if (user.isBanned()) {
				user.setBanned(false);

				userDAO.save(user);

				//Create and send Email
				String mailSubject = emailProperties.getUserRestoreSubject();
				String mailBody = emailProperties.getUserRestoreBody();
				mailManager.sendMail(emailProperties.getFromAddress(), user.getEmail(), mailSubject, mailBody);
			} else {
				user.setBanned(true);

				userDAO.save(user);

				//Create and send Email
				String mailSubject = emailProperties.getUserBanSubject();
				String mailBody = emailProperties.getUserBanBody();
				mailManager.sendMail(emailProperties.getFromAddress(), user.getEmail(), mailSubject, mailBody);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
