/*
 * This file is generated by jOOQ.
 */
package nl.jnext.workshop.testcontainers.vakantieplanner.jooq.tables.records;


import java.time.LocalDate;

import nl.jnext.workshop.testcontainers.vakantieplanner.jooq.tables.Holiday;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class HolidayRecord extends UpdatableRecordImpl<HolidayRecord> implements Record5<Integer, Integer, String, LocalDate, LocalDate> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.holiday.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.holiday.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.holiday.member_id</code>.
     */
    public void setMemberId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.holiday.member_id</code>.
     */
    public Integer getMemberId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>public.holiday.description</code>.
     */
    public void setDescription(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.holiday.description</code>.
     */
    public String getDescription() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.holiday.start_date</code>.
     */
    public void setStartDate(LocalDate value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.holiday.start_date</code>.
     */
    public LocalDate getStartDate() {
        return (LocalDate) get(3);
    }

    /**
     * Setter for <code>public.holiday.end_date</code>.
     */
    public void setEndDate(LocalDate value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.holiday.end_date</code>.
     */
    public LocalDate getEndDate() {
        return (LocalDate) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<Integer, Integer, String, LocalDate, LocalDate> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<Integer, Integer, String, LocalDate, LocalDate> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return Holiday.HOLIDAY.ID;
    }

    @Override
    public Field<Integer> field2() {
        return Holiday.HOLIDAY.MEMBER_ID;
    }

    @Override
    public Field<String> field3() {
        return Holiday.HOLIDAY.DESCRIPTION;
    }

    @Override
    public Field<LocalDate> field4() {
        return Holiday.HOLIDAY.START_DATE;
    }

    @Override
    public Field<LocalDate> field5() {
        return Holiday.HOLIDAY.END_DATE;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public Integer component2() {
        return getMemberId();
    }

    @Override
    public String component3() {
        return getDescription();
    }

    @Override
    public LocalDate component4() {
        return getStartDate();
    }

    @Override
    public LocalDate component5() {
        return getEndDate();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public Integer value2() {
        return getMemberId();
    }

    @Override
    public String value3() {
        return getDescription();
    }

    @Override
    public LocalDate value4() {
        return getStartDate();
    }

    @Override
    public LocalDate value5() {
        return getEndDate();
    }

    @Override
    public HolidayRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public HolidayRecord value2(Integer value) {
        setMemberId(value);
        return this;
    }

    @Override
    public HolidayRecord value3(String value) {
        setDescription(value);
        return this;
    }

    @Override
    public HolidayRecord value4(LocalDate value) {
        setStartDate(value);
        return this;
    }

    @Override
    public HolidayRecord value5(LocalDate value) {
        setEndDate(value);
        return this;
    }

    @Override
    public HolidayRecord values(Integer value1, Integer value2, String value3, LocalDate value4, LocalDate value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached HolidayRecord
     */
    public HolidayRecord() {
        super(Holiday.HOLIDAY);
    }

    /**
     * Create a detached, initialised HolidayRecord
     */
    public HolidayRecord(Integer id, Integer memberId, String description, LocalDate startDate, LocalDate endDate) {
        super(Holiday.HOLIDAY);

        setId(id);
        setMemberId(memberId);
        setDescription(description);
        setStartDate(startDate);
        setEndDate(endDate);
    }
}