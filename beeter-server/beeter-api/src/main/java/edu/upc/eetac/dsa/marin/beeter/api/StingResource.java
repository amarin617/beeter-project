package edu.upc.eetac.dsa.marin.beeter.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import edu.upc.eetac.dsa.marin.beeter.api.links.BeeterAPILinkBuilder;
import edu.upc.eetac.dsa.marin.beeter.api.model.Sting;
import edu.upc.eetac.dsa.marin.beeter.api.model.StingCollection;

@Path("/stings")
public class StingResource {
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	@Context
	private UriInfo uriInfo;

	@GET
	@Produces(MediaType.BEETER_API_STING_COLLECTION)
	public StingCollection getStings(@QueryParam("pattern") String pattern,
			@QueryParam("username") String username,
			@QueryParam("offset") String offset,
			@QueryParam("length") String length) {
		if ((offset == null) || (length == null))
			throw new BadRequestException(
					"offset and length are mandatory parameters");
		int ioffset, ilength;
		int icount = 0;
		try {
			ioffset = Integer.parseInt(offset);
			if (ioffset < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new BadRequestException(
					"offset must be an integer greater or equal than 0.");
		}
		try {
			ilength = Integer.parseInt(length);
			if (ilength < 1)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new BadRequestException(
					"length must be an integer greater or equal than 1.");
		}
		StingCollection stings = new StingCollection();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		try {
			Statement stmt = conn.createStatement();
			String sql = null;
			if ((username != null) && (pattern != null)) {
				sql = "select users.name, stings.* from users, stings where users.username=stings.username AND users.username='"
						+ username
						+ "' AND subject like '%"
						+ pattern
						+ "%' OR content like '%"
						+ pattern
						+ "%' ORDER BY last_modified desc LIMIT "
						+ offset
						+ "," + length;
			} else if ((username == null) && (pattern != null)) {
				sql = "select users.name, stings.* from users, stings where users.username=stings.username AND subject like '%"
						+ pattern
						+ "%' OR content like '%"
						+ pattern
						+ "%' ORDER BY last_modified desc limit "
						+ offset
						+ "," + length;
			} else if ((username != null) && (pattern == null)) {
				sql = "select users.name, stings.* from users, stings where users.username=stings.username AND users.username='"
						+ username
						+ "' ORDER BY last_modified desc LIMIT "
						+ offset + "," + length;
			} else {
				sql = "select users.name, stings.* from users, stings where users.username=stings.username ORDER BY last_modified desc LIMIT "
						+ offset + "," + length;
			}
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Sting sting = new Sting();
				sting.setStingid(rs.getString("stingid"));
				sting.setUsername(rs.getString("username"));
				sting.setAuthor(rs.getString("name"));
				sting.setContent(rs.getString("content"));
				sting.setSubject(rs.getString("subject"));
				sting.setLastModified(rs.getTimestamp("last_modified"));
				sting.addLink(BeeterAPILinkBuilder.buildURIStingId(uriInfo, sting.getStingid(), "self"));
				stings.addSting(sting);
				icount++;
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		}
		if (ioffset != 0) {
			String prevoffset = "" + (ioffset - ilength);
			stings.addLink(BeeterAPILinkBuilder.buildURIStings(uriInfo,
					prevoffset, length, username, "prev"));
		}
		stings.addLink(BeeterAPILinkBuilder.buildURIStings(uriInfo, offset,
				length, username, "self"));
		String nextoffset = "" + (ioffset + ilength);
		if (ilength <= icount) {
			stings.addLink(BeeterAPILinkBuilder.buildURIStings(uriInfo,
					nextoffset, length, username, "next"));
		}
		return stings;
	}

	@POST
	@Consumes(MediaType.BEETER_API_STING)
	@Produces(MediaType.BEETER_API_STING)
	public Sting createSting(Sting sting) {
		if (sting.getContent().length() > 100) {
			throw new BadRequestException(
					"Subject length must be less or equal than 100 characters");
		}
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			Statement stmt = conn.createStatement();
			String update = "insert into stings (username, content, subject) values ('"
					+ sting.getUsername()
					+ "', '"
					+ sting.getContent()
					+ "', '" + sting.getSubject() + "')";

			stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int stingid = rs.getInt(1);
				sting.setStingid(Integer.toString(stingid));

				String sql = "select stings.last_modified from stings where stings.stingid = "
						+ stingid;
				rs = stmt.executeQuery(sql);
				if (rs.next()) {
					sting.setLastModified(rs.getTimestamp(1));
				}
				rs.close();
				stmt.close();
				conn.close();

			} else {
				throw new StingNotFoundException();
			}
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		}
		return sting;
	}

	@GET
	@Path("/{stingid}")
	@Produces(MediaType.BEETER_API_STING)
	public Response getSting(@PathParam("stingid") String stingid,
			@Context Request req) {
		CacheControl cc = new CacheControl();
		Sting sting = new Sting();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String sql = "select users.name, stings.* from users, stings where stings.stingid="
					+ stingid + " and stings.username=users.username";
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				sting.setStingid(rs.getString("stingid"));
				sting.setUsername(rs.getString("username"));
				sting.setAuthor(rs.getString("name"));
				sting.setContent(rs.getString("content"));
				sting.setSubject(rs.getString("subject"));
				sting.setLastModified(rs.getTimestamp("last_modified"));
			} else {
				throw new StingNotFoundException();
			}
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new InternalServerException(e.getMessage());
			}
		}
		EntityTag eTag = new EntityTag(Integer.toString(sting.getLastModified()
				.hashCode()));
		// Verify if it matched with etag available in http request
		Response.ResponseBuilder rb = req.evaluatePreconditions(eTag);
		// If ETag matches the rb will be non-null;
		// Use the rb to return the response without any further processing
		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}
		// If rb is null then either it is first time request; or resource is
		// modified
		// Get the updated representation and return with Etag attached to it
		rb = Response.ok(sting).cacheControl(cc).tag(eTag);

		return rb.build();
	}

	@DELETE
	@Path("/{stingid}")
	public void deleteSting(@PathParam("stingid") String stingid) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		Statement stmt = null;
		String sql;
		try {
			stmt = conn.createStatement();
			sql = "delete from stings where stingid=" + stingid;
			// Aqui para dar el ok que se a completado no se si hacerlo con
			// resulset porque da error, pero con int parece que funciona
			// int rs2 = stmt.executeUpdate(sql);
			// rs2.close(); CAL????
			int rs2 = stmt.executeUpdate(sql);
			if (rs2 == 0)
				throw new StingNotFoundException();

		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		}
		// return?? No confirmamos que ok o que error??????
		finally {
			try {
				conn.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Context
	private SecurityContext security;

	@PUT
	@Path("/{stingid}")
	@Consumes(MediaType.BEETER_API_STING)
	@Produces(MediaType.BEETER_API_STING)
	public Sting updateSting(@PathParam("stingid") String stingid, Sting sting) {

		if (sting.getContent().length() > 500) {
			throw new BadRequestException(
					"Content length must be less or equal than 500 characters");
		}
		if (sting.getSubject().length() > 100) {
			throw new BadRequestException(
					"Subject length must be less or equal than 100 characters");
		}
		if (security.isUserInRole("registered")) {
			if (!security.getUserPrincipal().getName()
					.equals(sting.getUsername()))
				throw new ForbiddenException("you are not allowed...");
		} else {
			// Si fuera admin le dejo pasar
		}
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		try {
			Statement stmt = conn.createStatement();

			String sql = "update stings set stings.content='"
					+ sting.getContent() + "',stings.subject='"
					+ sting.getSubject() + "' where stings.stingid=" + stingid;
			stmt.executeUpdate(sql);
			// Aqui tendriamos q controlar el error de si
			// no se pudiera ejecutar la actualizacion
			sql = "select stings.last_modified from stings where stings.stingid = "
					+ stingid;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				sting.setLastModified(rs.getTimestamp("last_modified"));
				sting.setStingid(stingid);
			} else {
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		}
		return sting;
	}
}