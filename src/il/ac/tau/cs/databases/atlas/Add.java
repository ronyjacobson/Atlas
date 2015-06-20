package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.io.IOException;
import java.util.Date;

public class Add extends BaseModifyPerson {

	public Add() throws IOException {
	}

	@Override
	protected String getButtonText() {
		return "Add to ATLAS!";
	}

	@Override
	protected void showMessage() {
		triggerJsCode("personAdded('"
				+ name.getText() + "');");
	}

	@Override
	protected void execQuery(Long birthLocationId, Long deathLocationId, Date birthDate, Date deathDate, String link) throws AtlasServerException {
		Main.queries.addNew(
				name.getText(), category.getSelectedItem().toString(),
				birthDate, birthLocationId, deathDate, deathLocationId,
				link, isFemale.isSelected());
	}
}
