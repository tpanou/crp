package org.teiath.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.NotificationDAO;
import org.teiath.data.dao.RouteDAO;
import org.teiath.data.dao.UserActionDAO;
import org.teiath.data.domain.Notification;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.UserAction;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.ntf.ViewNotificationService;

@Service("viewUserActionService")
@Transactional
public class ViewUserActionServiceImpl
		implements ViewUserActionService {

	@Autowired
	UserActionDAO userActionDAO;
	@Autowired
	RouteDAO routeDAO;

	@Override
	public UserAction getUserActionById(Integer userActionId)
			throws ServiceException {
		UserAction userAction;
		try {
			userAction = userActionDAO.findById(userActionId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
		return userAction;
	}

	@Override
	public Route getRouteByCode(String code)
			throws ServiceException {
		Route route;

		try {
			route = routeDAO.findByCode(code);
			if (route != null) {
				route.getRouteInterests().iterator();
				route.getRoutePoints().iterator();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return route;
	}
}
