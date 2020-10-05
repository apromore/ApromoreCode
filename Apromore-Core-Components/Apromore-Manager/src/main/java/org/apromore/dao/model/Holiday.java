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
package org.apromore.dao.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

import lombok.NoArgsConstructor;

@Entity
@Table(name = "holiday")
@Configurable("holiday")
@Cache(expiry = 180000, size = 1000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
@NoArgsConstructor
public class Holiday implements Serializable {

	private Long id;

	private Long referenceId;

	private String name;
	private String description;
	private String holidayDate;
	private LocalDate localDateHolidayDate;

	private String createdBy;
	private String updatedBy;

	private CustomCalender customCalender;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	@Column(name = "reference_id")
	public Long getReferenceId() {
		return referenceId;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	@Column(name = "holiday_date")
	public String getHolidayDate() {
		return holidayDate;
	}

	@ManyToOne
	@JoinColumn(name = "calender_id")
	public CustomCalender getCustomCalender() {
		return customCalender;
	}

	@Column(name = "created_by")
	public String getCreatedBy() {
		return createdBy;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setReferenceId(Long referenceId) {
		this.referenceId = referenceId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setHolidayDate(String holidayDate) {
		this.holidayDate = holidayDate;
	}

	@Transient
	public LocalDate getLocalDateHolidayDate() {
		localDateHolidayDate = LocalDate.parse(holidayDate);
		return localDateHolidayDate;
	}

	public void setLocalDateHolidayDate(LocalDate localDateHolidayDate) {
		localDateHolidayDate = localDateHolidayDate;
	}

	public void setCustomCalender(CustomCalender customCalender) {
		this.customCalender = customCalender;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Column(name = "updated_by")
	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Holiday(String name, String description, LocalDate localDateHolidayDate) {
		this.name = name;
		this.description = description;
		this.localDateHolidayDate = localDateHolidayDate;
		this.holidayDate=localDateHolidayDate.toString();

	}

}
