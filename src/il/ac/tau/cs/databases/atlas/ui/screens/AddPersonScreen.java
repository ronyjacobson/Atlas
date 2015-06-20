package il.ac.tau.cs.databases.atlas.ui.screens;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.utils.DateUtils;
import il.ac.tau.cs.databases.atlas.ui.utils.GraphicUtils;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class AddPersonScreen extends BaseModifyPersonScreen {

	private static final String DEFAULT_CATEGORY = "Choose a category...";

	@Override
	protected String getTitleText() {
		return "Add a new person:";
	}

	@Override
	protected void addClearTextBoxListenersIfNeeded() {
		name.addMouseListener(new ClearTextBox(name));
		wikiLink.addMouseListener(new ClearTextBox(wikiLink));
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
			logger.error("", e);
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
					GraphicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (!isFemale.isSelected() && !isMale.isSelected()) {
			JOptionPane.showMessageDialog(null,
					"Please choose male or female.", GraphicUtils.PROJECT_NAME,
					JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (name.getText().trim().equalsIgnoreCase("")) {
			JOptionPane.showMessageDialog(null, "Name can not be blank.",
					GraphicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (name.getText().length() > DBConstants.PREF_LABEL_SIZE) {
			JOptionPane.showMessageDialog(null, "Name can not exceed "
							+ DBConstants.PREF_LABEL_SIZE + " characters.",
					GraphicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (category.getSelectedItem().toString()
				.equals(DEFAULT_CATEGORY)) {
			JOptionPane.showMessageDialog(null,
					"Please choose a category from the list.",
					GraphicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (wasBornOn.getCalendar() == null) {
			JOptionPane.showMessageDialog(null, "Please choose a birth date",
					GraphicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if ((hasDiedOn.getCalendar() != null)
				&& DateUtils.isSameDay(wasBornOn.getCalendar(),
				hasDiedOn.getCalendar())
				&& (!hasDiedIn.getSelectedItem().toString()
				.equals(NOT_DEAD_LOCATION))) {
			JOptionPane.showMessageDialog(null,
					"No way that the birth and death dates are the same.",
					GraphicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (hasDiedOn.getCalendar() != null
				&& DateUtils.isAfterDay(wasBornOn.getCalendar(),
				hasDiedOn.getCalendar())
				&& (!hasDiedIn.getSelectedItem().toString()
				.equals(NOT_DEAD_LOCATION))) {
			JOptionPane.showMessageDialog(null,
					"No way that the birth date is after the death date.",
					GraphicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (wasBornIn.getSelectedItem().toString()
				.equals(DEFAULT_BIRTH_LOCATION)) {
			JOptionPane.showMessageDialog(null,
					"Please choose a birth place from the list.",
					GraphicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (!locations.contains(wasBornIn.getSelectedItem().toString())) {
			JOptionPane.showMessageDialog(null,
					"Please choose a birth place that exists in the list.",
					GraphicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (!locations.contains(hasDiedIn.getSelectedItem().toString())) {
			JOptionPane.showMessageDialog(null,
					"Please choose a death place that exists in the list.",
					GraphicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (hasDiedIn.getSelectedItem().toString()
				.equals(DEFAULT_DEATH_LOCATION)
				&& hasDiedOn.getCalendar() != null) {
			JOptionPane.showMessageDialog(null,
					"Please choose a death place from the list.",
					GraphicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (wikiLink.getText().trim().equalsIgnoreCase("")) {
			JOptionPane.showMessageDialog(null,
					"Wikipedia link can not be blank.",
					GraphicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (wikiLink.getText().length() > DBConstants.WIKI_URL_SIZE) {
			JOptionPane.showMessageDialog(null,
					"Wikipedia link can not exceed "
							+ DBConstants.WIKI_URL_SIZE + " characters.",
					GraphicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else if (hasDiedOn.getCalendar() == null) {
			//make sure the date is valid if a location was entered
			if (!hasDiedIn.getSelectedItem().toString().equals(NOT_DEAD_LOCATION) &&
					!hasDiedIn.getSelectedItem().toString().equals(DEFAULT_DEATH_LOCATION)) {
				JOptionPane.showMessageDialog(null,
						"Please choose a death date or remove the death location",
						GraphicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		} else if (hasDiedIn.getSelectedItem().toString().equals(NOT_DEAD_LOCATION)) {
			if (hasDiedOn.getCalendar() != null) {
				int reply = JOptionPane
						.showConfirmDialog(
								null,
								"<html>You mentioned this person is not dead but entered a death date.<br>"
										+ "This person will be added without the death date.<br>Continue anyway?</html>",
								GraphicUtils.PROJECT_NAME,
								JOptionPane.YES_NO_OPTION);
					return reply == JOptionPane.YES_OPTION;
			}
		}
		return true;
	}
}
