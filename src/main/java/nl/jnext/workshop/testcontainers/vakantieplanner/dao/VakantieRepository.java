package nl.jnext.workshop.testcontainers.vakantieplanner.dao;

import static nl.jnext.workshop.testcontainers.vakantieplanner.jooq.tables.Holiday.HOLIDAY;
import static nl.jnext.workshop.testcontainers.vakantieplanner.jooq.tables.Member.MEMBER;

import nl.jnext.workshop.testcontainers.vakantieplanner.controller.HolidayController;
import nl.jnext.workshop.testcontainers.vakantieplanner.controller.exceptions.OverlappingHolidayException;
import nl.jnext.workshop.testcontainers.vakantieplanner.jooq.tables.records.HolidayRecord;
import nl.jnext.workshop.testcontainers.vakantieplanner.jooq.tables.records.MemberRecord;
import nl.jnext.workshop.testcontainers.vakantieplanner.model.Holiday;
import nl.jnext.workshop.testcontainers.vakantieplanner.model.Member;
import nl.jnext.workshop.testcontainers.vakantieplanner.model.MemberWithHolidays;
import org.jooq.Configuration;
import org.jooq.DSLContext;

import static org.jooq.impl.DSL.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class VakantieRepository {

    private final DSLContext create;

    private static Logger logger = LoggerFactory.getLogger(HolidayController.class);

    @Autowired
    VakantieRepository(DSLContext dslContext) {
        this.create = dslContext;
    }

    public int getIdForUsername(String user) {
        Optional<Integer> exisingUserId = Optional.ofNullable(create.selectFrom(MEMBER).where(MEMBER.NAME.eq(user)).fetchOne(MEMBER.ID));
        if (exisingUserId.isEmpty()) {
            logger.info("User record for user %s is created in DB".formatted(user));
            MemberRecord memberRecord = create.newRecord(MEMBER);
            memberRecord.setName(user);
            memberRecord.store();
            return memberRecord.getId();

        } else {
            return exisingUserId.get();
        }
    }

    public List<Holiday> retrieveHolidaysForMemberWithName(String user) {
        var fetchResult =
                create.select().from(HOLIDAY).join(MEMBER)
                        .on(MEMBER.ID.eq(HOLIDAY.MEMBER_ID))
                        .where(MEMBER.NAME.eq(user))
                        .fetchInto(HOLIDAY);

        return new ArrayList<>(fetchResult.map(
                fr -> new Holiday(fr.getId(), fr.getDescription(), fr.getStartDate(), fr.getEndDate())
        ));
    }

    public List<MemberWithHolidays> retrieveAllHolidays() {
//        Result<Record2<MemberRecord, Result<Record1<HolidayRecord>>>> result =

        var fetch = create.select(
                        MEMBER,
                        multiset(
                                selectFrom(HOLIDAY)
                                        .where(HOLIDAY.MEMBER_ID.eq(MEMBER.ID))
                        ))
                .from(MEMBER)
                .fetch();

        return fetch.stream().map(r -> new MemberWithHolidays(
                new Member(r.component1().getId(), r.component1().getName()),
                r.component2().map(h -> new Holiday(
                        h.getId(),
                        h.getDescription(),
                        h.getStartDate(),
                        h.getEndDate()
                )))).toList();
    }

    public Holiday retrieveHoliday(int id) {
        HolidayRecord holidayRecord = create.selectFrom(HOLIDAY).where(HOLIDAY.ID.eq(id)).fetchOne();
        if (holidayRecord == null) {
            throw new IllegalStateException("Holiday with id %s not found".formatted(id));
        }
        return new Holiday(
                holidayRecord.getId(),
                holidayRecord.getDescription(),
                holidayRecord.getStartDate(),
                holidayRecord.getEndDate());
    }

    public void deleteHoliday(int id) {
        HolidayRecord holidayRecord = create.selectFrom(HOLIDAY).where(HOLIDAY.ID.eq(id)).fetchOne();
        holidayRecord.delete();
    }

    public Holiday addHoliday(String user, Holiday holiday) {
        boolean possible = checkIfHolidayIsPossible(holiday.from(), holiday.to());
        if (!possible) {
            throw new OverlappingHolidayException();
        }
        int memberId = getIdForUsername(user);
        HolidayRecord holidayRecord = create.newRecord(HOLIDAY);
        holidayRecord.setMemberId(memberId);
        holidayRecord.setDescription(holiday.description());
        holidayRecord.setStartDate(holiday.from());
        holidayRecord.setEndDate(holiday.to());
        holidayRecord.store();
        return retrieveHoliday(holidayRecord.getId());
    }

    public void updateHoliday(String user, Holiday holiday) {
        create.transaction((Configuration trx) -> {
            DSLContext create = trx.dsl();
            HolidayRecord holidayRecord = create.selectFrom(HOLIDAY).where(HOLIDAY.ID.eq(holiday.id())).fetchOne();
            if (holidayRecord == null) {
                throw new IllegalStateException("Holiday to be updated could not be found");
            }
            // we first need to delete it in order to check date availability
            holidayRecord.delete();
            boolean possible = checkIfHolidayIsPossible(holiday.from(), holiday.to());
            if (possible) {
                addHoliday(user, holiday);
            } else {
                throw new OverlappingHolidayException();
            }
        });
    }

    boolean checkIfHolidayIsPossible(LocalDate from, LocalDate to) {
        String sql = """
                        SELECT
                        (holiday.start_date, holiday.end_date)
                        OVERLAPS (DATE '%s', DATE '%s')
                        FROM holiday;
                """.formatted(from, to);

        return create.fetch(sql)
                .getValues("overlaps")
                .stream().noneMatch(v -> (boolean) v);
    }
}