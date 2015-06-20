package il.ac.tau.cs.databases.atlas.connector.command.NewCommands;

import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.db.Result;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

/**
 * Created by user on 22/05/2015.
 */
public class SearchResultsQuery extends GetResultsGeneralQuery {
	int limitNumOfResults = DBConstants.LIMIT;
	PreparedStatement statement = null;
	ResultSet resultSet = null;

	private Date sDate;
	private Date eDate;
	private String name;

	public SearchResultsQuery(Date sDate, Date eDate, String name,
			boolean isFemale, String continent) {
		super();
		this.sDate = sDate;
		this.eDate = eDate;
		this.name = name;
	}

	public SearchResultsQuery(Date sDate, Date eDate) {
		super();
		this.sDate = sDate;
		this.eDate = eDate;
		this.name = null;
	}

	public SearchResultsQuery(String name) {
		super();
		this.sDate = null;
		this.eDate = null;
		this.name = name;
	}

	@Override
	protected String makeStmt() {
		String select = makeSelectStmt();
		String from = makeFromStmt();
		String where = makeWhereStmt();
		String rest = makeRestOfStmt();
		if (byName()) {
			from += fromByName();
			where += whereByName();
		}
		if (byDates()) {
			where += whereByDates();
		}
		return select + from + where + rest;
	}

	protected String fromByName() {
		return "	, person_labels\n";
	}

	protected String whereByName() {
		return "	AND person_labels.label LIKE '%" + name + "%'\n"
				+ "	AND person_labels.person_ID = person.person_ID\n";
	}

	protected String whereByDates() {
		return "	AND ((person.wasBornOnDate >= '" + new java.sql.Date(this.sDate.getTime())
				+ "' AND person.wasBornOnDate <= '" + new java.sql.Date(this.eDate.getTime())
				+ "') OR (person.diedOnDate >= '" + new java.sql.Date(this.sDate.getTime())
				+ "' AND person.diedOnDate <= '" + new java.sql.Date(this.eDate.getTime()) + "'))\n";
	}

	/**
	 * We always pull death and birth details (so we can edit a person), but
	 * sometimes only one of them needs to show on map (like search by years) or
	 * we need to get more details.
	 * 
	 * @return true if the result is valid to show on map.
	 */
	@Override
	protected boolean ExtraValidateResult(Result result) {
		setMaxMinYear(result);
		boolean isValid = true;
		if (byDates()) {
			// Make sure to mark only dates in this range
			if ((result.getDate().before(sDate))
					|| (result.getDate().after(eDate))) {
				isValid = false;
			}
		}
		return isValid;
	}

	public Date getsDate() {
		return sDate;
	}

	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}

	public Date geteDate() {
		return eDate;
	}

	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return true if the search is by dates.
	 */
	public boolean byDates() {
		return (sDate != null) && (eDate != null);
	}
	
	/**
	 * @return true if the search is by name.
	 */
	public boolean byName() {
		return (name != null);
	}

}
