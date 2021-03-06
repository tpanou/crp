package org.teiath.service.crp;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.RouteNotificationCriteria;
import org.teiath.data.domain.crp.UserPlace;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface EditRouteNotificationCriteriaService {

	public void saveRouteNotificationCriteria(RouteNotificationCriteria routeNotificationCriteria)
			throws ServiceException;

	public RouteNotificationCriteria getRouteNotificationCriteriaById(Integer notificationCriteriaId)
			throws ServiceException;

	public Collection<UserPlace> getPlacesByUser(User user)
			throws ServiceException;
}
