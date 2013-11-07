package org.teiath.data.domain.crp;

import com.vividsolutions.jts.geom.Point;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "crp_route_points")
public class RoutePoint {

	public final static int DEPARTURE = 1;
	public final static int ARRIVAL = 2;
	public final static int WAYPOINT = 3;

	@Id
	@Column(name = "route_point_id")
	@SequenceGenerator(name = "route_points_seq", sequenceName = "crp_route_points_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "route_points_seq")
	private Integer id;

	@Column(name = "point_index", nullable = false)
	private int index;
	@Column(name = "point_type", nullable = false)
	private int type;
	@Column(name = "location", columnDefinition = "Geometry", nullable = false)
	@Type(type = "org.hibernate.spatial.GeometryType")
	private Point location;
	@Column(name = "address", length = 1000, nullable = true)
	private String address;
	@Column(name = "lat", length = 1000, nullable = true)
	private String lat;
	@Column(name = "lng", length = 1000, nullable = true)
	private String lng;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "route_id", nullable = false)
	private Route route;

	public RoutePoint() {
	}

	public RoutePoint(int index, int type, Point location, String address, Route route) {
		this.index = index;
		this.type = type;
		this.location = location;
		this.address = address;
		this.route = route;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}
}
