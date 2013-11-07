package org.teiath.data.domain.crp;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Formula;
import org.teiath.data.domain.Notification;
import org.teiath.data.domain.User;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

@Entity
@Table(name = "crp_routes")
public class Route
		implements Serializable, Comparator {

	@Id
	@Column(name = "rout_id")
	@SequenceGenerator(name = "routes_seq", sequenceName = "crp_routes_rout_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "routes_seq")
	private Integer id;

	@Column(name = "rout_code", length = 2000, nullable = false)
	private String code;
	@Column(name = "rout_starting_point", length = 2000, nullable = false)
	private String startingPoint;
	@Column(name = "rout_destination_point", length = 2000, nullable = false)
	private String destinationPoint;
	@Column(name = "rout_date", nullable = false)
	private Date routeDate;
	@Column(name = "rout_time", nullable = false)
	@Temporal(TemporalType.TIME)
	private Date routeTime;
	@Column(name = "rout_flexible", nullable = false)
	private boolean flexible;
	@Column(name = "rout_total_seats", nullable = false)
	private Integer totalSeats;
	@Column(name = "rout_obj_transport_allowed", nullable = false)
	private boolean objectTransportAllowed;
	@Column(name = "rout_smoking_allowed", nullable = false)
	private boolean smokingAllowed;
	@Column(name = "rout_contribution_amount", precision = 6, scale = 2, nullable = true)
	private BigDecimal contributionAmount;
	@Column(name = "rout_tags", length = 2000, nullable = true)
	private String tags;
	@Column(name = "rout_comment", length = 4000, nullable = true)
	private String comment;
	@Column(name = "rout_recurring", nullable = false)
	private boolean recurring;
	@Column(name = "rout_days", length = 2000, nullable = true)
	private String days;
	@Column(name = "rout_end_date", nullable = true)
	private Date routeEndDate;
	@Column(name = "rout_creation_date", nullable = false)
	private Date routeCreationDate;
	@Column(name = "rout_amea_accessible", nullable = false)
	private Boolean ameaAccessible;
	@Column(name = "rout_enabled", nullable = true)
	private Boolean enabled;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "vehi_id", nullable = false)
	private Vehicle vehicle;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "route")
	private Set<RouteInterest> routeInterests;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "route")
	private Set<Notification> notifications;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "assessedRoute")
	private Set<RouteAssessment> routeAssessments;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "route")
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@OrderBy("index ASC")
	private Set<RoutePoint> routePoints;

	@Formula(
			"(SELECT SUM(I.rint_number_passengers) from crp_route_interests I WHERE I.rint_status =1 AND I.rout_id=rout_id)")
	private Integer totalSeatsTaken;

	private String priceWithCurrency;

	@Transient
	private Double routeRating;


	public Route() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStartingPoint() {
		return startingPoint;
	}

	public void setStartingPoint(String startingPoint) {
		this.startingPoint = startingPoint;
	}

	public String getDestinationPoint() {
		return destinationPoint;
	}

	public void setDestinationPoint(String destinationPoint) {
		this.destinationPoint = destinationPoint;
	}

	public Date getRouteDate() {
		return routeDate;
	}

	public void setRouteDate(Date routeDate) {
		this.routeDate = routeDate;
	}

	public boolean isFlexible() {
		return flexible;
	}

	public void setFlexible(boolean flexible) {
		this.flexible = flexible;
	}

	public Integer getTotalSeats() {
		return totalSeats;
	}

	public void setTotalSeats(Integer totalSeats) {
		this.totalSeats = totalSeats;
	}

	public boolean isObjectTransportAllowed() {
		return objectTransportAllowed;
	}

	public void setObjectTransportAllowed(boolean objectTransportAllowed) {
		this.objectTransportAllowed = objectTransportAllowed;
	}

	public boolean isSmokingAllowed() {
		return smokingAllowed;
	}

	public void setSmokingAllowed(boolean smokingAllowed) {
		this.smokingAllowed = smokingAllowed;
	}

	public BigDecimal getContributionAmount() {
		return contributionAmount;
	}

	public void setContributionAmount(BigDecimal contributionAmount) {
		this.contributionAmount = contributionAmount;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isRecurring() {
		return recurring;
	}

	public void setRecurring(boolean recurring) {
		this.recurring = recurring;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public Date getRouteEndDate() {
		return routeEndDate;
	}

	public void setRouteEndDate(Date routeEndDate) {
		this.routeEndDate = routeEndDate;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<RouteInterest> getRouteInterests() {
		return routeInterests;
	}

	public void setRouteInterests(Set<RouteInterest> routeInterests) {
		this.routeInterests = routeInterests;
	}

	public String getPriceWithCurrency() {
		return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(this.contributionAmount);
	}

	public void setPriceWithCurrency(String priceWithCurrency) {
		this.priceWithCurrency = priceWithCurrency;
	}

	public int getReservations() {
		int reservations = 0;

		for (RouteInterest routeInterest : this.routeInterests) {
			if (routeInterest.getStatus() == RouteInterest.STATUS_APPROVED) {
				reservations += routeInterest.getNumberOfPassengers();
			}
		}

		return reservations;
	}

	public int getInterests() {
		int interests = 0;

		for (RouteInterest routeInterest : this.routeInterests) {
			if (routeInterest.getStatus() == RouteInterest.STATUS_PENDING) {
				interests++;
			}
		}

		return interests;
	}

	public Date getRouteTime() {
		return routeTime;
	}

	public void setRouteTime(Date routeTime) {
		this.routeTime = routeTime;
	}

	public Integer getTotalSeatsTaken() {
		return totalSeatsTaken;
	}

	public void setTotalSeatsTaken(Integer totalSeatsTaken) {
		this.totalSeatsTaken = totalSeatsTaken;
	}

	public Set<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(Set<Notification> notifications) {
		this.notifications = notifications;
	}

	public Date getRouteCreationDate() {
		return routeCreationDate;
	}

	public void setRouteCreationDate(Date routeCreationDate) {
		this.routeCreationDate = routeCreationDate;
	}

	public Set<RoutePoint> getRoutePoints() {
		return routePoints;
	}

	public void setRoutePoints(Set<RoutePoint> routePoints) {
		this.routePoints = routePoints;
	}

	public Boolean getAmeaAccessible() {
		return ameaAccessible;
	}

	public void setAmeaAccessible(Boolean ameaAccessible) {
		this.ameaAccessible = ameaAccessible;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == Route.class && this.id.equals(((Route) obj).getId());
	}

	public void materializeGeometry(String googleCoords) {
		StringTokenizer stringTokenizer = new StringTokenizer(googleCoords, "*");

		String routeToken;
		String coords;
		Geometry geom;
		WKTReader fromText = new WKTReader();
		int index = 1;
		StringTokenizer st;
		RoutePoint routePoint;
		while (stringTokenizer.hasMoreTokens()) {
			routeToken = stringTokenizer.nextToken();
			System.out.println("ROUTE TOKEN: " + routeToken);
			st = new StringTokenizer(routeToken, "|");
			while (st.hasMoreTokens()) {
				routePoint = new RoutePoint();
				routePoint.setRoute(this);
				routePoint.setIndex(index++);

				coords = st.nextToken().trim();
				System.out.println("COORDS: " + coords);

				if (coords.endsWith("D")) {
					routePoint.setType(RoutePoint.DEPARTURE);
					routePoint.setAddress(coords.substring(coords.indexOf("[") + 1, coords.indexOf("]")));
					coords = coords.replace(routePoint.getAddress(), "");
				} else if (coords.endsWith("A")) {
					routePoint.setType(RoutePoint.ARRIVAL);
					routePoint.setAddress(coords.substring(coords.indexOf("[") + 1, coords.indexOf("]")));
					coords = coords.replace(routePoint.getAddress(), "");
				} else if (coords.endsWith("W")) {
					routePoint.setType(RoutePoint.WAYPOINT);
					routePoint.setAddress("-");
				}

				coords = coords.replaceAll("A", "");
				coords = coords.replaceAll("D", "");
				coords = coords.replaceAll("W", "");
				coords = coords.replaceAll("\\)", "");
				coords = coords.replaceAll("\\(", "");
				coords = coords.replaceAll("\\[", "");
				coords = coords.replaceAll("\\]", "");

				try {
					geom = fromText.read("POINT(" + coords.replace(",", " ") + ")");
					geom.setSRID(4326);
					routePoint.setLocation((Point) geom);
					routePoint.setLat(routePoint.getLocation().getX() + "");
					routePoint.setLng(routePoint.getLocation().getY() + "");

					if (this.getRoutePoints() == null) {
						this.setRoutePoints(new HashSet<RoutePoint>());
					}

					this.getRoutePoints().add(routePoint);
				} catch (ParseException e) {
					throw new RuntimeException("Not a WKT string");
				}
			}
		}
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getRouteRating() {
		if (routeRating != 0) {
			return String.valueOf(routeRating).substring(0, 8);
		} else {
			return "0.0";
		}
	}

	public void setRouteRating(Double routeRating) {
		this.routeRating = routeRating;
	}

	public int compare(Object o1, Object o2) {
		Route r1 = (Route) o1;
		Route r2 = (Route) o2;
		return r1.routeRating.compareTo(r2.routeRating);
	}

}
