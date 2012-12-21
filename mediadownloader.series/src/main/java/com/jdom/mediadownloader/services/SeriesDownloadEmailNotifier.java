/** 
 *  Copyright (C) 2012  Just Do One More
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jdom.mediadownloader.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.jdom.mediadownloader.domain.User;
import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.download.SeriesDownloadListener;
import com.jdom.mediadownloader.services.series.SeriesService;
import com.jdom.util.email.Email;

public class SeriesDownloadEmailNotifier implements SeriesDownloadListener {

	private final EmailService emailService;

	private final Email templateEmail;

	public SeriesDownloadEmailNotifier(EmailService emailService,
			Email templateEmail) {
		this.emailService = emailService;
		this.templateEmail = templateEmail;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.series.download.SeriesDownloadListener#downloadComplete(com.jdom.mediadownloader.series.domain.Series)
	 */
	@Override
	public void downloadComplete(Series series) {
		sendEmails(Arrays.asList(series));
	}

	private void sendEmails(List<Series> seriesList) {

		Collection<Email> emailsToSend = new ArrayList<Email>();

		// Construct email notifications per series
		for (Series series : seriesList) {
			Collection<User> usersToNotify = SeriesService
					.getUsersToNotifyForSeries(series);
			if (!usersToNotify.isEmpty()) {
				emailsToSend.add(createEmail(series, usersToNotify,
						templateEmail));
			}
		}

		for (Email email : emailsToSend) {
			emailService.email(email);
		}
	}

	/**
	 * Get the list of notifiees for the series.
	 * 
	 * @param series
	 *            the series
	 * @param usersToNotify
	 *            the collection of users to notify
	 * @param email
	 *            the template email
	 * @return the complete email object
	 */
	private Email createEmail(Series series, Collection<User> usersToNotify,
			Email email) {
		Collection<String> emailAddresses = new ArrayList<String>();

		// Add all user notifiees address
		for (User user : usersToNotify) {
			emailAddresses.add(user.getEmailAddress());
		}

		email.setEmailAddresses(emailAddresses);

		// Create string representation of the show, note the previous episode
		// because the persisted object has already been updated
		String show = "[" + series.getName() + "] - Season ["
				+ series.getSeason() + "] Episode ["
				+ (series.getEpisode() - 1) + "]";

		// Add subject
		email.setSubject("Notification for " + show);

		// Add body
		StringBuilder sb = new StringBuilder(
				"This email is to notify you that the following show is available:\n\n")
				.append(show);

		email.setBody(sb.toString());

		return email;
	}
}
