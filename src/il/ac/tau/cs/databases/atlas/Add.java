package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.utils.DateUtils;
import il.ac.tau.cs.databases.atlas.utils.GrapicUtils;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Add extends BaseModifyPerson {

	private static final String DEFAULT_CATEGORY = "Choose a category...";

	@Override
	protected void addClearTextBoxListenersIfNeeded() {
		ClearTextBox clearTextBoxListner = new ClearTextBox();
		name.addMouseListener(clearTextBoxListner);
		wikiLink.addMouseListener(clearTextBoxListner);
	}

	@Override
	protected void setCategoriesComboBox(Font fieldFont) {
		try {
			java.util.List<String> categories = Main.queries.getAllCategoriesNames();
			categories.add(0, DEFAULT_CATEGORY);
			category = new JComboBox<>(
					categories.toArray(new String[categories.size()]));
			category.setFont(fieldFont);
		} catch (AtlasServerException e) {
			// TODO handle exception
		}
	}

	@Override
	protected void addCategoryBarToPanel(JPanel panel) {
		panel.add(category);
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

	@Override
	protected boolean isInputValidated() {
		if (!wereDetailsEntered) {
			JOptionPane.showMessageDialog(null,
					"Please enter the needed details.",
					GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (!isFemale.isSelected() && !isMale.isSelected()) {
			JOptionPane.showMessageDialog(null,
					"Please choose male or female.", GrapicUtils.PROJECT_NAME,
					JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (name.getText().equalsIgnoreCase("")) {
			JOptionPane.showMessageDialog(null, "Name can not be blank.",
					GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (name.getText().length() > DBConstants.PREF_LABEL_SIZE) {
			JOptionPane.showMessageDialog(null, "Name can not exceed "
							+ DBConstants.PREF_LABEL_SIZE + " characters.",
					GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (category.getSelectedItem().toString()
				.equals(DEFAULT_CATEGORY)) {
			JOptionPane.showMessageDialog(null,
					"Please choose a category from the list.",
					GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (wasBornOn.getCalendar() == null) {
			JOptionPane.showMessageDialog(null, "Please choose a birth date",
					GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if ((hasDiedOn.getCalendar() != null)
				&& DateUtils.isSameDay(wasBornOn.getCalendar(),
				hasDiedOn.getCalendar())
				&& (!hasDiedIn.getSelectedItem().toString()
				.equals(NOT_DEAD_LOCATION))) {
			JOptionPane.showMessageDialog(null,
					"No way that the birth and death dates are the same.",
					GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (hasDiedOn.getCalendar() != null
				&& DateUtils.isAfterDay(wasBornOn.getCalendar(),
				hasDiedOn.getCalendar())
				&& (!hasDiedIn.getSelectedItem().toString()
				.equals(NOT_DEAD_LOCATION))) {
			JOptionPane.showMessageDialog(null,
					"No way that the birth date is after the death date.",
					GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (wasBornIn.getSelectedItem().toString()
				.equals(DEFAULT_BIRTH_LOCATION)) {
			JOptionPane.showMessageDialog(null,
					"Please choose a birth place from the list.",
					GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (!locations.contains(wasBornIn.getSelectedItem().toString())) {
			JOptionPane.showMessageDialog(null,
					"Please choose a birth place that exists in the list.",
					GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (!locations.contains(hasDiedIn.getSelectedItem().toString())) {
			JOptionPane.showMessageDialog(null,
					"Please choose a death place that exists in the list.",
					GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (hasDiedIn.getSelectedItem().toString()
				.equals(DEFAULT_DEATH_LOCATION)
				&& hasDiedOn.getCalendar() != null) {
			JOptionPane.showMessageDialog(null,
					"Please choose a death place from the list.",
					GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (wikiLink.getText().equalsIgnoreCase("")) {
			JOptionPane.showMessageDialog(null,
					"Wikipedia link can not be blank.",
					GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (wikiLink.getText().length() > DBConstants.WIKI_URL_SIZE) {
			JOptionPane.showMessageDialog(null,
					"Wikipedia link can not exceed "
							+ DBConstants.WIKI_URL_SIZE + " characters.",
					GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (hasDiedOn.getCalendar() == null) {
			//make sure the date is valid if a location was entered
			if (!hasDiedIn.getSelectedItem().toString().equals(NOT_DEAD_LOCATION) &&
					!hasDiedIn.getSelectedItem().toString().equals(DEFAULT_DEATH_LOCATION)) {
				JOptionPane.showMessageDialog(null,
						"Please choose a death date or remove the death location",
						GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		} else if (hasDiedIn.getSelectedItem().toString().equals(NOT_DEAD_LOCATION)) {
			if (hasDiedOn.getCalendar() != null) {
				int reply = JOptionPane
						.showConfirmDialog(
								null,
								"<html>You mentioned this person is not dead but entered a death date.<br>"
										+ "This person will be added without the death date.<br>Continue anyway?</html>",
								GrapicUtils.PROJECT_NAME,
								JOptionPane.YES_NO_OPTION);
					return reply == JOptionPane.YES_OPTION;
			}
		}
		return true;
	}
}
