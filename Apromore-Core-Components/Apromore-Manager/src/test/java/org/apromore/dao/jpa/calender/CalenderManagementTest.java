/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.dao.jpa.calender;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZoneOffset;

import org.apromore.BaseTestClass;
import org.apromore.dao.CustomCalenderRepository;
import org.apromore.dao.model.CustomCalender;
import org.apromore.dao.model.Holiday;
import org.apromore.dao.model.WorkDay;
import org.assertj.core.api.Condition;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CalenderManagementTest extends BaseTestClass {

	@Autowired
	CustomCalenderRepository customCal;

	@Test
	public void createCustomCalender() {
//		Given
		CustomCalender calender = new CustomCalender("Test Calender");

//		When
		calender = customCal.saveAndFlush(calender);

//		Then
		assertThat(calender.getId()).isNotNull();
	}

	@Test
	public void testGetCusomCalenderByDesc() {

//		Given
		CustomCalender calenderToSave = new CustomCalender("Test Calender Desc");
		customCal.saveAndFlush(calenderToSave);

//		When
		CustomCalender calenderExpected = customCal.findByDescription("Test Calender Desc");

//		Then
		assertThat(calenderExpected.getId()).isNotNull();
		assertThat(calenderExpected.getCreated()).startsWith(calenderToSave.getCreated().subSequence(0, 15));
		assertThat(calenderExpected.getUpdated()).startsWith(calenderToSave.getUpdated().subSequence(0, 15));
		assertThat(calenderExpected.getCreateOffsetDateTime()).isNotNull();
		assertThat(calenderExpected.getUpdateOffsetDateTime()).isNotNull();

	}

	@Test
	public void testAddCustomCalenderWithWorkDayMonday() {
//		Given
		CustomCalender calenderToSave = new CustomCalender("Test Calender Work Day");
//		customCal.saveAndFlush(calenderToSave);

		OffsetTime startTime = OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC);
		OffsetTime endTime = OffsetTime.of(5, 0, 0, 0, ZoneOffset.UTC);
		WorkDay workDayMonDay = new WorkDay(DayOfWeek.MONDAY, startTime, endTime, true);
		calenderToSave.addWorkDay(workDayMonDay);

//		When
		CustomCalender calenderExpected = customCal.saveAndFlush(calenderToSave);

//		Then
		assertThat(calenderExpected.getId()).isNotNull();
		assertThat(calenderExpected.getWorkDays().get(0).getId()).isNotNull();
		assertThat(calenderExpected.getWorkDays().get(0).getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);

	}

	@Test
	public void testAddCustomCalenderWithNonWorkDaySaturday() {
//		Given
		CustomCalender calenderToSave = new CustomCalender("Test Calender Non Work Day");
//		customCal.saveAndFlush(calenderToSave);

		OffsetTime startTime = OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC);
		OffsetTime endTime = OffsetTime.of(5, 0, 0, 0, ZoneOffset.UTC);
		WorkDay workDayMonDay = new WorkDay(DayOfWeek.SATURDAY, startTime, endTime, false);
		calenderToSave.addWorkDay(workDayMonDay);

//		When
		CustomCalender calenderExpected = customCal.saveAndFlush(calenderToSave);

//		Then
		assertThat(calenderExpected.getId()).isNotNull();
		assertThat(calenderExpected.getWorkDays().get(0).getId()).isNotNull();
		assertThat(calenderExpected.getWorkDays().get(0).getDayOfWeek()).isEqualTo(DayOfWeek.SATURDAY);
		assertThat(calenderExpected.getWorkDays().get(0).isWorkingDay()).isFalse();

	}

	@Test
	public void testAddCustomCalenderWithBusinessDays() {
//		Given
		CustomCalender calenderToSave = new CustomCalender("Test Calender Business Calender");
//		customCal.saveAndFlush(calenderToSave);

		OffsetTime startTime = OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC);
		OffsetTime endTime = OffsetTime.of(5, 0, 0, 0, ZoneOffset.UTC);
		for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
			boolean isWorkDay = true;
			if (dayOfWeek.equals(DayOfWeek.SATURDAY) || dayOfWeek.equals(DayOfWeek.SUNDAY)) {
				isWorkDay = false;
			}
			WorkDay workDayMonDay = new WorkDay(dayOfWeek, startTime, endTime, isWorkDay);

			calenderToSave.addWorkDay(workDayMonDay);
		}

//		When
		CustomCalender calenderExpected = customCal.saveAndFlush(calenderToSave);

//		Then
		assertThat(calenderExpected.getId()).isNotNull();
		assertThat(calenderExpected.getWorkDays()).extracting("dayOfWeek", DayOfWeek.class)
				.contains(DayOfWeek.values());

		Condition<WorkDay> workDaysFilter = new Condition<WorkDay>() {
			@Override
			public boolean matches(WorkDay workDay) {
				return workDay.getDayOfWeek().equals(DayOfWeek.SATURDAY)
						|| workDay.getDayOfWeek().equals(DayOfWeek.SUNDAY);
			}
		};

		assertThat(calenderExpected.getWorkDays()).filteredOn(workDaysFilter).extracting("isWorkingDay", Boolean.class)
				.containsOnly(false);
	}
	
	@Test
	public void testAddCustomCalenderWithHoliday() {
//		Given
		CustomCalender calenderToSave = new CustomCalender("Test Calender Holiday Day");
//		customCal.saveAndFlush(calenderToSave);

		LocalDate holidayDate = LocalDate.now();
		Holiday holiday=new Holiday("TestName", "TestDesc", holidayDate);
		calenderToSave.addHoliday(holiday);

//		When
		CustomCalender calenderExpected = customCal.saveAndFlush(calenderToSave);

//		Then
		assertThat(calenderExpected.getId()).isNotNull();
		assertThat(calenderExpected.getHolidays().get(0).getId()).isNotNull();
		assertThat(calenderExpected.getHolidays().get(0).getLocalDateHolidayDate()).isEqualTo(holidayDate);

	}

}
